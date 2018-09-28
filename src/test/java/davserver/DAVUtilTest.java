package davserver;

import org.apache.http.util.Asserts;
import org.junit.Test;

import davserver.repository.error.NotAllowedException;

/**
 * DAVUtils test class 
 *  
 * @author tkrieger
 *
 */
public class DAVUtilTest {

	/**
	 * Test Path Component detection with .. inside path
	 */
	@Test
	public void checkPathCompsDots() {
		Exception e = null;
		String    uri = "/litmus/../../col/";
		
		try {
			DAVUtil.getPathComps(uri);
		} catch (NotAllowedException nae) {
			e = nae;
		}
				
		Asserts.check(e != null, "Need the exception here");
	}
	
	
	
}
