package davserver;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Resource URL Helper class 
 * 
 * @author tkrieger
 *
 */
public class DAVUrl {
	
	/**
	 * URL prefix
	 */
	private String prefix;
	
	/**
	 * Repository name (URL Path Element)
	 */
	private String repository;
	
	/**
	 * Resource reference
	 */
	private String resref;
	
	/**
	 * Defaultkonstruktor 
	 * 
	 * @param url
	 * @param prefix
	 */
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

	/**
	 * Get url prefix
	 * 
	 * @return
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * Get the repository name
	 * 
	 * @return
	 */
	public String getRepository() {
		return repository;
	}

	/**
	 * Get resource reference
	 * 
	 * @return
	 */
	public String getResref() {
		return resref;
	}

}
