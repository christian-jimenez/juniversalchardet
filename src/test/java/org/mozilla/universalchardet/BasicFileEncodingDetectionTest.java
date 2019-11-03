package org.mozilla.universalchardet;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.mozilla.universalchardet.common.ResourceLoaderTest;
import org.mozilla.universalchardet.common.TestConstants;

public class BasicFileEncodingDetectionTest extends ResourceLoaderTest {

	private static final String WITH_BOM = " with BOM";
	
	@Test
	public void testUTF8() throws IOException {
		assertEquals(Constants.CHARSET_UTF_8, getFileEncoding(TestConstants.UTF8_FILE_NAME));
	}
	
	@Test
	public void testUTF8withBOM() throws IOException {
		assertEquals(Constants.CHARSET_UTF_8 + WITH_BOM, getFileEncoding(TestConstants.UTF8_WIT_BOM_FILE_NAME));
	}

	@Test
	public void testUTF8N() throws IOException {
		assertEquals(Constants.CHARSET_UTF_8, getFileEncoding(TestConstants.UTF8N_FILE_NAME));
	}
	
	@Test
	public void testUTF8Emoji() throws IOException {
		assertEquals(Constants.CHARSET_UTF_8, getFileEncoding(TestConstants.UTF8NEMOJI_FILE_NAME));
	}

	@Test
	public void testUTF16LE() throws IOException {
		assertEquals(Constants.CHARSET_UTF_16LE, getFileEncoding(TestConstants.UTF16LE_FILE_NAME));
	}

	@Test
	public void testShifJis() throws IOException {
		assertEquals(Constants.CHARSET_SHIFT_JIS, getFileEncoding(TestConstants.SHIFT_JIS_FILE_NAME));
	}

	@Test
	public void testEUC() throws IOException {
		assertEquals(Constants.CHARSET_EUC_JP, getFileEncoding(TestConstants.EUC_JC_FILE_NAME));
	}
	
	@Test
	public void testEUCTW() throws IOException {
		assertEquals(Constants.CHARSET_EUC_TW, getFileEncoding(TestConstants.EUC_TW_FILE_NAME));
	}
	
	@Test
	public void testEUCKR() throws IOException {
		assertEquals(Constants.CHARSET_EUC_KR, getFileEncoding(TestConstants.EUC_KR_FILE_NAME));
	}

	@Test
	public void testISO2022JP() throws IOException {
		assertEquals(Constants.CHARSET_ISO_2022_JP, getFileEncoding(TestConstants.ISO_2022_JP_FILE_NAME));
	}

	@Test
	public void testBIG5() throws IOException {
		assertEquals(Constants.CHARSET_BIG5, getFileEncoding(TestConstants.BIG5_FILE_NAME));
	}

	@Test
	public void testWindows1255() throws IOException {
		assertEquals(Constants.CHARSET_WINDOWS_1255, getFileEncoding(TestConstants.WINDOWS_1255_FILE_NAME));
	}

	private String getFileEncoding(final String testFileName) throws IOException {
		return UniversalDetector.detectCharset(getFileResource(testFileName));
	}

}
