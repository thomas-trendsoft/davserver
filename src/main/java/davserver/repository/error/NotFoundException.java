package davserver.repository.error;

public class NotFoundException extends RepositoryException {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = -6417294243038506764L;

	public NotFoundException(String string) {
		super(string);
	}

}
