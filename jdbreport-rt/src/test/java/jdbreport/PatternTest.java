package jdbreport;

import static org.junit.Assert.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class PatternTest {

	@Test
	public void test() {
		Pattern pattern = Pattern.compile("(0.0+)|(0+)|[\\00+]");
		Matcher m = pattern.matcher("0.000");
		assertTrue(m.matches());
		System.out.println(m.replaceAll("-"));
		m = pattern.matcher("\0");
		assertTrue(m.matches());
		System.out.println(m.replaceAll("-"));
		m = pattern.matcher("0");
		assertTrue(m.matches());
		System.out.println(m.replaceAll("-"));
		m = pattern.matcher("0.012");
		assertFalse(m.matches());
	}

}
