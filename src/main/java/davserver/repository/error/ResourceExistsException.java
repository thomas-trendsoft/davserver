package davserver.repository.error;

public class ResourceExistsException extends RepositoryException {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = -5578520657733185808L;

	public ResourceExistsException(String string) {
		super(string);
	}

}
