package davserver.protocol.header;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import davserver.DAVUrl;
import davserver.repository.IRepository;
import davserver.repository.LockEntry;
import davserver.repository.Resource;
import davserver.repository.error.NotAllowedException;
import davserver.repository.error.NotFoundException;
import davserver.utils.Pair;

/**
 * Implementation of IfHeader Handling
 * 
 * @author tkrieger
 *
 */
public class IfHeader {
	
	protected static Pattern PList        = Pattern.compile("(?<res>\\<[^\\>]\\>\\s)?\\((?<cond>[^\\)]*)\\)\\s*");
	protected static Pattern PCondition   = Pattern.compile("(?<state>(Not )?\\<[^\\>\\<]*\\>|\\[[^\\[\\]]*\\])");
		
	/**
	 * condition list
	 */
	private List<Pair<URI,List<IfCondition>>> conditions;
	
	/**
	 * Defaultkonstruktor 
	 */
	public IfHeader() {
		this.conditions = new LinkedList<>();
	}
	
	public List<Pair<URI,List<IfCondition>>> getConditions() {
		return conditions;
	}

	/**
	 * Evaluate the if expression 
	 * 
	 * @param lock
	 * @param etag
	 * @return
	 * @throws NotAllowedException 
	 * @throws NotFoundException 
	 */
	public HashSet<String> evaluate(HashMap<String,LockEntry> locks,Resource r,IRepository repos,DAVUrl url) {
		HashSet<String> ret = null;
		// check condition disjunktion
		for (Pair<URI,List<IfCondition>> sub : getConditions()) {
			
			boolean cval         = true;
			HashSet<String> sret = new HashSet<String>();
			
			// conjunction conditions
			for (IfCondition c : sub.getValue()) {
				// entity condition
				if (c.entity) {
					int ec = -1;
					if (sub.getKey() == null) {
						ec = c.state.compareTo(r.getETag());						
					} else {
						DAVUrl   tu = new DAVUrl(sub.getKey().getPath(), url.getPrefix()); 
						Resource tr;
						try {
							tr = repos.locate(tu.getResref());
							ec = c.state.compareTo(tr.getETag());
						} catch (NotFoundException | NotAllowedException e) {
							e.printStackTrace();
							ec = -1;
						}
					}
					if ((c.not && ec != 0) || (!c.not && ec == 0)) {
						sret.add(c.state);
						System.out.println("add state: " + c.state);						
					} else {
						cval = false;
					}
				// lock condition
				} else {
					if ((locks == null || !locks.containsKey(c.state)) && !c.not) {
						cval = false;
					} else if (!c.not && locks.containsKey(c.state)) {
						if (sub.getKey() != null) {
							LockEntry le = locks.get(c.state);
							DAVUrl    tu = new DAVUrl(sub.getKey().getPath(), url.getPrefix());
							if (le.getRef().compareTo(tu.getResref()) != 0) {
								System.out.println("fail on missed resource url");
								cval = false;
							}
						}
						sret.add(c.state);
					} else {
						cval = false;
					}
				}
			} // sub list
			if (cval && sret.size() > 0) {
				if (ret == null) ret = new HashSet<String>();
				ret.addAll(sret);				
			}
		}
		// is at least one condition true
		if (ret == null) 
			return ret;
		// if locks given one must be satisfied
		if (locks != null) {
			for (String t : ret) {
				if (locks.containsKey(t)) {
					return ret;
				}
			}
			return null;
		} else {
			return ret;
		}
	}

	/**
	 * Parse the object from a http header value
	 * 
	 * @param value
	 * @return
	 */
	public static IfHeader parseIfHeader(String value) throws ParseException {
		int      off = 0;
		IfHeader ret = null;
		
		if (value == null) {
			throw new ParseException("no value", 0);
		} 
		
		ret = new IfHeader();
		
		// parse conditions
		Matcher m = PList.matcher(value);
		List<Pair<String,String>> disj = new LinkedList<>();
		while (m.find()) {
			String sub = m.group("cond");
			String uri = m.group("res");
			System.out.println(sub);
			disj.add(new Pair<String,String>(uri,sub));
		}
			
		for (Pair<String,String> sub : disj) {
			List<IfCondition> clist = new LinkedList<>();
			Matcher sm = PCondition.matcher(sub.getValue());
			while (sm.find()) {
				String state = sm.group("state");
				IfCondition c = new IfCondition();
				c.not   = state.startsWith("Not ");
				c.state = state.substring((c.not ? 5 : 1),state.length()-1);
				c.entity = state.startsWith("[");
				clist.add(c);
			}
			if (clist != null & clist.size() > 0) {
				URI uref = null;
				if (sub.getKey() != null) {
					try {
						uref = new URI(sub.getKey());
					} catch (URISyntaxException e) {
						throw new ParseException("invalid resource uri", 0);
					}
				}
				ret.getConditions().add(new Pair<URI,List<IfCondition>>(uref,clist));											
			}
		}
		
		if (ret.getConditions().size() == 0) {
			throw new ParseException("no list",off);
		}
		
		return ret;
	}
	
}
