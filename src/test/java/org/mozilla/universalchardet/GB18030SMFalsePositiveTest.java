package org.mozilla.universalchardet;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.UnsupportedEncodingException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author ian
 * @since Jul 13, 2011
 */
public class GB18030SMFalsePositiveTest {
	
	private UniversalDetector detector;
	
	@BeforeEach
	public void setup() throws Exception {
		detector = new UniversalDetector();
	}
	
	@AfterEach
	public void teardown() throws Exception {
		detector.reset();
	}

	@Test
	public void testFalsePositiveBug11() throws UnsupportedEncodingException {
		String testString = "[°4°0°T°C°C°0°C°T";
		byte[] testBuf = new byte[] { 91, -80, 52, -80, 48, -80, 84, -80, 67, -80, 67, -80, 48, -80, 67, -80, 84 };
		byte[] buf = testString.getBytes(Constants.CHARSET_WINDOWS_1252);

		assertArrayEquals(testBuf, buf);

		detector.handleData(buf, 0, buf.length);
		detector.dataEnd();

		String encoding = detector.getDetectedCharset();

		assertEquals(Constants.CHARSET_WINDOWS_1252, encoding);
	}

	@Test
	public void testFalsePositiveBug9() throws UnsupportedEncodingException {
		String testString = "Wykamol,£588.95,0.18,0.12,testingSpecialised Products for DIY and Professionals£12";
		byte[] buf = testString.getBytes(Constants.CHARSET_WINDOWS_1252);

		detector.handleData(buf);
		detector.dataEnd();

		String encoding = detector.getDetectedCharset();

		assertEquals(Constants.CHARSET_WINDOWS_1252, encoding);
	}

}