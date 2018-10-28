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
	 * path components
	 */
	private List<String> comps;
	
	/**
	 * Defaultkonstruktor 
	 * 
	 * @param url
	 * @param prefix
	 * @throws NotAllowedException 
	 */
	public DAVUrl(String surl,String prefix) throws NotAllowedException {

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
			
			// remove query or fragment parts
			int idx;
			if ((idx = uri.indexOf("?")) > 0) {
				uri = uri.substring(0,idx);
			}
			if ((idx = uri.indexOf("#")) > 0) {
				uri = uri.substring(0,idx);
			}

			comps = DAVUtil.getPathComps(uri);

			if (comps.size() > 0) {
				repository = URLDecoder.decode(comps.get(0),"utf-8");
			} else {
				throw new NotAllowedException("no repository root");
			}
						
			// create resource url
			resref = "";
			for (idx=1;idx<comps.size();idx++) {
				resref += "/" + comps.get(idx);  
			}
			
			if (resref.isEmpty())
				resref = "/";
			
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
	
	public String getURI() throws UnsupportedEncodingException {
		String base = String.valueOf(this);
		
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
