package org.mozilla.universalchardet;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.mozilla.universalchardet.common.ResourceLoaderTest;
import org.mozilla.universalchardet.common.TestConstants;

public class BOMTest extends ResourceLoaderTest {

	private static final String TEST_STRING = "========================================================================";

	@Test
	public void testUTF8() throws IOException {
		assertEquals(TEST_STRING, getFirstLine(TestConstants.UTF8_FILE_NAME));
	}

	@Test
	public void testUTF8N() throws IOException {
		assertEquals(TEST_STRING, getFirstLine(TestConstants.UTF8N_FILE_NAME));
	}

	@Test
	public void testUTF16LE() throws IOException {
		assertEquals(TEST_STRING, getFirstLine(TestConstants.UTF16LE_FILE_NAME));
	}

	private String getFirstLine(final String testFileName) throws IOException {
		try (BufferedReader reader = ReaderFactory.createBufferedReader(getFileResource(testFileName));) {
			return reader.readLine(); // return first line
		}
	}

}
