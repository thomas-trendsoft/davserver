package davserver.protocol.header;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class IfHeaderTest {
	
	@Test
	public void testIfList() throws ParseException {
		String example = "(<opaquelocktoken:21b3e94d-2b7c-4453-b371-acb86792ed06> [d41d8cd98f00b204e9800998ecf8437e]) (Not <DAV:no-lock> [d41d8cd98f00b204e9800998ecf8437e])";
		Pattern PCondition   = Pattern.compile("(?<not>Not )?(?<state>\\<[^\\>\\<]*\\>|\\[[^\\[\\]]*\\])");
		Matcher m = PCondition.matcher(example);
		
		while (m.find()) {
			System.out.println("found");
		}
	}

}
