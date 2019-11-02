package org.mozilla.universalchardet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;

public class BasicStreamEncodingDetectionTest {

	@Test
	public void testUTF8() throws IOException {
		assertEquals("UTF-8", this.getFileEncoding("utf8.txt"));
	}

	@Test
	public void testUTF8N() throws IOException {
		assertEquals("UTF-8", this.getFileEncoding("utf8n.txt"));
	}

	@Test
	public void testUTF16LE() throws IOException {
		assertEquals("UTF-16LE", this.getFileEncoding("utf16le.txt"));
	}

	@Test
	public void testShifJis() throws IOException {
		assertEquals("SHIFT_JIS", this.getFileEncoding("shiftjis.txt"));
	}

	@Test
	public void testEUC() throws IOException {
		assertEquals("EUC-JP", this.getFileEncoding("euc.txt"));
	}

	@Test
	public void testISO2022JP() throws IOException {
		assertEquals("ISO-2022-JP", this.getFileEncoding("iso2022jp.txt"));
	}

	@Test
	public void testBIG5() throws IOException {
		assertEquals("BIG5", this.getFileEncoding("big5.txt"));
	}

	@Test
	public void testEUCTW() throws IOException {
		assertEquals("EUC-TW", this.getFileEncoding("euctw.txt"));
	}

	@Test
	public void testEUCKR() throws IOException {
		assertEquals("EUC-KR", this.getFileEncoding("euckr.txt"));
	}

	@Test
	public void testWindows1255() throws IOException {
		assertEquals("WINDOWS-1255", this.getFileEncoding("windows1255.txt"));
	}

	@Test
	public void testUTF8Emoji() throws IOException {
		assertEquals("UTF-8", this.getFileEncoding("utf8n-emoji.txt"));
	}

	private String getFileEncoding(final String testFileName) throws IOException {
		File file = new File("src/test/resources/" + testFileName);
		EncodingDetectorInputStream edis = null;
		EncodingDetectorOutputStream edos = null;
		try {
			edis = new EncodingDetectorInputStream(new BufferedInputStream(Files.newInputStream(file.toPath())));
			edos = new EncodingDetectorOutputStream(NullOutputStream.NULL_OUTPUT_STREAM);
			byte[] buffer = new byte[1024];
			int read = 0;
			while ((read = edis.read(buffer)) > 0) {
				edos.write(buffer, 0, read);
			}
		} finally {
			edos.close();
			edis.close();
		}
		String encodingRead = edis.getDetectedCharset();
		String encodingWrite = edos.getDetectedCharset();
		assertNotNull(encodingRead);
		assertNotNull(encodingWrite);
		assertTrue(encodingRead.equals(encodingWrite));

		return encodingRead;
	}

}
