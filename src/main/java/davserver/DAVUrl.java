package davserver;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class DAVUrl {
	
	private String prefix;
	
	private String repository;
	
	private String resref;
	
	public DAVUrl(String url,String prefix) {

		this.prefix     = prefix;
		this.repository = null;
		this.resref     = null;
		
		if (url.length() < prefix.length()) {
			return;
		}
		
		try {
			// get repository part
			String uri = url.substring(prefix.length());
			int    ci = uri.indexOf("/", 1); 
			if (ci <= 0) {
				repository = URLDecoder.decode(uri.substring(1),"utf-8");
			} else {
				repository = URLDecoder.decode(uri.substring(1, ci),"utf-8");
			}
			
			// create resource url
			if (uri.length() >= ci) {
				resref = URLDecoder.decode(uri.substring(ci),"utf-8");
				int idx;
				// remove query or fragment parts
				if ((idx = resref.indexOf("?")) > 0) {
					resref = resref.substring(0,idx);
				}
				if ((idx = resref.indexOf("#")) > 0) {
					resref = resref.substring(0,idx);
				}
			} 		
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public String getPrefix() {
		return prefix;
	}

	public String getRepository() {
		return repository;
	}

	public String getResref() {
		return resref;
	}

}
