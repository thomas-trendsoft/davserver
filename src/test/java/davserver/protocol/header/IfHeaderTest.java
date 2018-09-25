package davserver.protocol.header;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class IfHeaderTest {
	
	@Test
	public void testIfList() throws ParseException {
		String example1 = "(<opaquelocktoken:21b3e94d-2b7c-4453-b371-acb86792ed06> [d41d8cd98f00b204e9800998ecf8437e]) (Not <DAV:no-lock> [d41d8cd98f00b204e9800998ecf8437e])";
		String example2 = "(<opaquelocktoken:87225e2a-0c86-4a4c-808f-87d01d68a01b>)";
		
		Pattern PCondition   = Pattern.compile("(?<not>Not )?(?<state>\\<[^\\>\\<]*\\>|\\[[^\\[\\]]*\\])");
		Matcher m = PCondition.matcher(example2);
		
		while (m.find()) {
			System.out.println("found");
		}
	}

}
