package davserver.protocol.xml;

import org.junit.Test;

import davserver.DAVServer;
import davserver.repository.properties.ResourceType;

/**
 * some test on the resourcetypes class 
 * 
 * @author tkrieger
 *
 */
public class ResourceTypeTest {

	@Test
	public void baseTest() {
		ResourceType rt = new ResourceType();
		
		rt.addType(DAVServer.CalDAVNS, "test");
	}
	
}
