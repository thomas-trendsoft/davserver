package davserver;

public class DAVException extends Exception {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = -3077726713606823602L;
	
	private int status;
	
	public DAVException(int status,String msg) {
		super(msg);
		this.status = status;
	}
	
	public int getStatus() {
		return status;
	}

}
