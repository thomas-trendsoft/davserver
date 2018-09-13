package davserver.repository.error;

public class NotAllowedException extends RepositoryException {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = 2945441111794209666L;

	public NotAllowedException(String string) {
		super(string);
	}

}
