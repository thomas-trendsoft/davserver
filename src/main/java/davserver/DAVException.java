package davserver;

/**
 * DAV protocol exception 
 * 
 * @author tkrieger
 *
 */
public class DAVException extends Exception {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = -3077726713606823602L;
	
	/**
	 * Http status code
	 */
	private int status;
	
	/**
	 * Defaultconstructor 
	 * 
	 * @param status
	 * @param msg
	 */
	public DAVException(int status,String msg) {
		super(msg);
		this.status = status;
	}
	
	/**
	 * get http status
	 * 
	 * @return
	 */
	public int getStatus() {
		return status;
	}

}
