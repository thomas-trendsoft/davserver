package davserver.protocol.header;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import davserver.repository.Collection;

/**
 * Implementation of IfHeader Handling
 * 
 * @author tkrieger
 *
 */
public class IfHeader {
	
	protected static Pattern PCondition   = Pattern.compile("(?<not>Not )?(?<state>\\<[^\\>\\<]*\\>|\\[[^\\[\\]]*\\])");
		
	/**
	 * Resource tag
	 */
	private URI resource;
	
	/**
	 * condition list
	 */
	private List<IfCondition> conditions;
	
	/**
	 * Defaultkonstruktor 
	 */
	public IfHeader(URI resource) {
		this.resource   = resource;
		this.conditions = new LinkedList<>();
	}
	
	public List<IfCondition> getConditions() {
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
	public boolean evaluate(String lock,String etag) {
		for (IfCondition c : getConditions()) {
			if (c.entity && c.state.compareTo(etag) != 0) {
				return false;
			} else if (c.state.compareTo(lock) != 0) {
				return false;
			}
			
		}
		return true;
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
			Matcher m = PCondition.matcher(plist);
			while (m.find()) {
				String state = m.group("state");
				IfCondition c = new IfCondition();
				c.not   = (m.group("not") != null);
				c.state = state.substring(1,state.length()-1);
				c.entity = state.startsWith("[");
				ret.getConditions().add(c);
			}			
		} else {
			throw new ParseException("no list",off);
		}
		
		return ret;
	}
	
}
