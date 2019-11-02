package org.mozilla.universalchardet;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.mozilla.universalchardet.common.ResourceLoaderTest;
import org.mozilla.universalchardet.common.TestConstants;

public class ReaderTest extends ResourceLoaderTest {

	private static final String TEST_STRING = "    コンソール アプリケーション : universalchardet プロジェクトの概要";

	@Test
	public void testUTF8() throws IOException {
		assertEquals(TEST_STRING, getSecondLine(TestConstants.UTF8_FILE_NAME));
	}

	@Test
	public void testUTF8N() throws IOException {
		assertEquals(TEST_STRING, getSecondLine(TestConstants.UTF8N_FILE_NAME));
	}

	@Test
	public void testUTF16LE() throws IOException {
		assertEquals(TEST_STRING, getSecondLine(TestConstants.UTF16LE_FILE_NAME));
	}

	@Test
	public void testShifJis() throws IOException {
		assertEquals(TEST_STRING, getSecondLine(TestConstants.SHIFT_JIS_FILE_NAME));
	}

	@Test
	public void testEUC() throws IOException {
		assertEquals(TEST_STRING, getSecondLine(TestConstants.EUC_JC_FILE_NAME));
	}

	@Test
	public void testISO2022JP() throws IOException {
		assertEquals(TEST_STRING, getSecondLine(TestConstants.ISO_2022_JP_FILE_NAME));
	}

	private String getSecondLine(String testFileName) throws IOException {
		try (BufferedReader reader = ReaderFactory.createBufferedReader(getFileResource(testFileName))) {
			reader.readLine(); // Skip first line

			return reader.readLine(); // return second line
		}
	}

}
