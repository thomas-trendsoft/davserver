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

import davserver.repository.LockEntry;

/**
 * Implementation of IfHeader Handling
 * 
 * @author tkrieger
 *
 */
public class IfHeader {
	
	protected static Pattern PList        = Pattern.compile("\\((?<cond>[^\\)])\\)\\s*");
	protected static Pattern PCondition   = Pattern.compile("(?<not>Not )?(?<state>\\<[^\\>\\<]*\\>|\\[[^\\[\\]]*\\])");
		
	/**
	 * Resource tag
	 */
	private URI resource;
	
	/**
	 * condition list
	 */
	private List<List<IfCondition>> conditions;
	
	/**
	 * Defaultkonstruktor 
	 */
	public IfHeader(URI resource) {
		this.resource   = resource;
		this.conditions = new LinkedList<>();
	}
	
	public List<List<IfCondition>> getConditions() {
		return conditions;
	}

	/**
	 * Get resource (resource tag list) null if only list
	 * 
	 * @return
	 */
	public URI getResource() {
		return resource;
	}
	
	/**
	 * Evaluate the if expression 
	 * 
	 * @param lock
	 * @param etag
	 * @return
	 */
	public HashSet<String> evaluate(HashMap<String,LockEntry> locks,String etag) {
		HashSet<String> ret = null;
		for (List<IfCondition> sub : getConditions()) {
			for (IfCondition c : sub) {
				if (c.entity && c.state.compareTo(etag) != 0) {
					System.out.println("fail on etag:  " + c.state + ":" + etag);
					return null;
				} else if (!c.entity) {
					if (c.state.compareTo("DAV:no-lock")==0 && locks == null) {
						if (ret == null) ret = new HashSet<String>();
						ret.add(c.state);						
					} else if (!locks.containsKey(c.state)) {
						System.out.println("fail on state:  " + c.state);
						return null;					
					} else {
						if (ret == null) ret = new HashSet<String>();
						ret.add(c.state);
					}
				}
			} // sub list
			if (ret != null) 
				return ret;
		}
		return ret;
	}

	/**
	 * Parse the object from a http header value
	 * 
	 * @param value
	 * @return
	 */
	public static IfHeader parseIfHeader(String value) throws ParseException {
		int      off = 0;
		URI      res = null;
		IfHeader ret = null;
		
		if (value == null) {
			throw new ParseException("no value", 0);
		} 
		
		if (value.startsWith("<")) {
			try {
				off = value.indexOf('>');
				System.out.println("ifres: " + value.substring(1, off));
				res = new URI(value.substring(1, off));
				off++;
			} catch (URISyntaxException e) {
				throw new ParseException("no uri resource ref",1);
			}
		} 
		
		ret = new IfHeader(res);
		String list = value.substring(off).trim();
		if (list.startsWith("(")) {
			off = list.indexOf(")");
			String plist = list.substring(0 ,off+1);
			System.out.println(plist);
			Matcher m = PList.matcher(plist);
			while (m.find()) {
				String sub = m.group("cond");
				if (sub != null) {
					List<IfCondition> clist = new LinkedList<>();
					Matcher sm = PCondition.matcher(sub);
					while (sm.find()) {
						String state = m.group("state");
						IfCondition c = new IfCondition();
						c.not   = (m.group("not") != null);
						c.state = state.substring(1,state.length()-1);
						c.entity = state.startsWith("[");	
						clist.add(c);
					}
					if (clist != null & clist.size() > 0) {
						ret.getConditions().add(clist);											
					}
				}
			}			
		} else {
			throw new ParseException("no list",off);
		}
		
		return ret;
	}
	
}
