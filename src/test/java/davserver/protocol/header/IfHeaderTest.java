package davserver.protocol.header;

import java.text.ParseException;
import java.util.List;

import org.apache.http.util.Asserts;
import org.junit.Test;

public class IfHeaderTest {
	
	@Test
	public void testIfList() throws ParseException {
		String example1 = "(<opaquelocktoken:21b3e94d-2b7c-4453-b371-acb86792ed06> [d41d8cd98f00b204e9800998ecf8437e]) (Not <DAV:no-lock> [d41d8cd98f00b204e9800998ecf8437e])";
		String example2 = "(<opaquelocktoken:87225e2a-0c86-4a4c-808f-87d01d68a01b>)";
		
		IfHeader h = IfHeader.parseIfHeader(example1);
		Asserts.check(h.getConditions().size() == 2, "wrong disjunction size: example1 " + h.getConditions().size());
		for (List<IfCondition> e1 : h.getConditions()) {
			Asserts.check(e1.size() == 2, "example1 missing sub state");
		}

		h = IfHeader.parseIfHeader(example2);
		Asserts.check(h.getConditions().size() == 1, "wrong disjunction size: example2 " + h.getConditions().size());
	}

}
