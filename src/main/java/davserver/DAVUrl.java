package davserver;

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
		
		String uri = url.substring(prefix.length());
		int    ci = uri.indexOf("/", 1); 
		if (ci <= 0) {
			repository = uri.substring(1);
		} else {
			repository = uri.substring(1, ci);
		}
		
		if (uri.length() >= ci) {
			resref = uri.substring(ci);			
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
