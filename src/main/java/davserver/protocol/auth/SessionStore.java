package davserver.protocol.auth;

import java.util.HashMap;
import java.util.UUID;

import org.apache.http.HttpRequest;

/**
 * Basic session store for auth methods 
 * 
 * @author tkrieger
 *
 */
public class SessionStore {

	/**
	 * Simple memory storage
	 */
	private HashMap<String,Session> store;
	
	/**
	 * Defaultconstructor 
	 */
	public SessionStore() {
		store = new HashMap<String,Session>();
	}
	
	/**
	 * query session from id
	 * 
	 * @param sid
	 * @return
	 */
	public Session get(String sid) {
		return store.get(sid);
	}
	
	/**
	 * create a session id for a new connection 
	 * 
	 * @return
	 */
	private String createSessionID() {
		// TODO better implementation
		return UUID.randomUUID().toString();
	}

	
	/**
	 * creates an session with an id
	 * 
	 * @param req
	 * @return
	 */
	public Session create(HttpRequest req) {
		Session s = new Session(createSessionID());
		store.put(s.getId(), s);
		return s;
	}
	
	
}
