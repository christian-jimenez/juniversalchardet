package org.mozilla.universalchardet;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.charset.Charset;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mozilla.universalchardet.common.ResourceLoaderTest;

public class Bug20LatinDetectedAsMaccyrillicTest extends ResourceLoaderTest {

	private static final String TEST_STRING = "ÄÜÖßäöü,Name1ÄÜÖßäöü,Name2ÄÜÖßäöü,Name3ÄÜÖßäöü,StreetÄÜÖßäöü,MÄÜÖßäöü,DE,80080,München,ContactÄÜÖßäöü,+49(0)ÄÜÖßäöü,ÄÜÖßäöü@gls-itservices.com,CommentÄÜÖßäöü,+49,(0)98,765,432,BlÄÜÖßäöü";

	@Test
	@Disabled("Bug not fixed yet")
	public void testFile() throws IOException {
		String originalEncoding = UniversalDetector.detectCharset(getFileResource("bug20-example-latin.txt"));

		assertEquals(Constants.CHARSET_WINDOWS_1252, originalEncoding);
	}

	@Test
	@Disabled("Bug not fixed yet")
	public void testLatin() throws IOException {
		UniversalDetector detector = new UniversalDetector();
		detector.handleData(TEST_STRING.getBytes(Charset.forName(Constants.CHARSET_WINDOWS_1252)));
		detector.dataEnd();

		assertEquals(Constants.CHARSET_WINDOWS_1252, detector.getDetectedCharset());
	}

	@Test
	public void testUTF8() {
		UniversalDetector detector = new UniversalDetector();
		detector.handleData(TEST_STRING.getBytes(Charset.forName(Constants.CHARSET_UTF_8)));
		detector.dataEnd();

		assertEquals(Constants.CHARSET_UTF_8, detector.getDetectedCharset());
	}

}
