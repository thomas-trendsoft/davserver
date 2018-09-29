package davserver.utils;

import java.util.HashMap;

import davserver.protocol.acl.Principal;
import davserver.protocol.auth.ICredentialStore;

/**
 * Basic demo implementation for tests 
 * 
 * @author tkrieger
 *
 */
public class SimpleCredentialStore implements ICredentialStore {

	/**
	 * Memory password storage
	 */
	private HashMap<String,Pair<String, Principal>> store;
	
	/**
	 * Defaultconstructor
	 */
	public SimpleCredentialStore() {
		store = new HashMap<String,Pair<String,Principal>>();
	}
	
	/**
	 * add demo user
	 * 
	 * @param user
	 * @param pass
	 * @param entry
	 */
	public void addCredentials(String user,String pass,Principal entry) {
		Pair<String,Principal> e = new Pair<>(pass,entry);
		store.put(user, e);
	}
	
	@Override
	public Principal checkAuth(String username, String secret) {
		Pair<String,Principal> entry = store.get(username);
		if (entry != null && entry.getKey().compareTo(secret)==0) {
			return entry.getValue();
		}
		return null;
	}

}
