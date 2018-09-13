package davserver.repository.error;

public class ConflictException extends RepositoryException{

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = -1977957188376721794L;

	public ConflictException(String string) {
		super(string);
	}

}
