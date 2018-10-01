package davserver.repository.cal.resources;

import java.io.IOException;
import java.io.InputStream;

import davserver.repository.Resource;

/**
 * Parser to get the different calendar resources
 * @author tkrieger
 *
 */
public class CalendarResourceParser {
	
	/**
	 * global parse calendar resource 
	 * 
	 * @param data input stream
	 * 
	 * @return a resource like vevent or vtodo
	 * @throws IOException 
	 */
	public static Resource parse(InputStream data) throws IOException {
		
		// check stream
		if (data == null) {
			return null;
		}
		
		// copy into string buffer
		StringBuilder buf = new StringBuilder();
		int    c   = 0;
		
		while ((c = data.read()) != -1) {
			buf.append((char)c);
		}
		
		String vdata = buf.toString(); 
		System.out.println(vdata);
		
		return null;
	}

}
