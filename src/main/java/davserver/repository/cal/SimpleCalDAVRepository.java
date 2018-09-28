package davserver.repository.cal;

import davserver.DAVServer;
import davserver.repository.simple.SimpleRepository;

public class SimpleCalDAVRepository extends SimpleRepository {

	public SimpleCalDAVRepository() {
		super();
	}
	
	@Override
	public int getProtocol() {
		return DAVServer.PROT_CARDDAV;
	}
	
}
