package davserver;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import davserver.repository.error.NotAllowedException;

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
	public DAVUrl(String surl,String prefix) {

		this.prefix     = prefix;
		this.repository = null;
		this.resref     = null;
		
		if (surl == null) {
			return;
		}
		String url = surl.trim();
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
			if (ci == -1) {
				resref = "/";
			} else if (uri.length() >= ci) {
				resref = URLDecoder.decode(uri.substring(ci),"utf-8");
				System.out.println("resref base: " + resref);
				int idx;
				// remove query or fragment parts
				if ((idx = resref.indexOf("?")) > 0) {
					resref = resref.substring(0,idx);
				}
				if ((idx = resref.indexOf("#")) > 0) {
					resref = resref.substring(0,idx);
				}
				// cut last / to avaid checks
				if (resref.trim().endsWith("/")) {
					resref = resref.trim().substring(0,resref.length()-1);
				}
			} 	
			System.out.println("resref: " + resref);
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
	
	public String getURI() throws NotAllowedException, UnsupportedEncodingException {
		String base = String.valueOf(this);
		List<String> comps = DAVUtil.getPathComps(base);
		
		String uri = "";
		for (String s : comps) {
			uri += "/" + URLEncoder.encode(s, "utf-8");
		}
		return uri;
	}
	
	@Override
	public String toString() {
		return prefix + "/" + repository + resref;
	}

}
