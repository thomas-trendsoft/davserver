package davserver.protocol;

import java.io.UnsupportedEncodingException;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;

import davserver.repository.IRepository;

public class DAVGet {

	private boolean debug;
	
	public DAVGet() {
		this.debug = true;
	}
	
	/**
	 * 
	 * @param req
	 * @param response
	 * @param repos
	 */
	public void handleGet(HttpRequest req,HttpResponse response,IRepository repos) {
		System.out.println("handle get");
		try {
			response.setEntity(new StringEntity("test"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	

}
