package davserver.repository.error;

public class LockedException extends RepositoryException {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = 1859587371232585271L;
	
	public LockedException(String string) {
		super(string);
	}


}
