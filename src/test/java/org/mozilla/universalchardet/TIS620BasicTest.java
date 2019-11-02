package org.mozilla.universalchardet;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.mozilla.universalchardet.common.ResourceLoaderTest;

public class TIS620BasicTest extends ResourceLoaderTest {

	@Test
	public void testTIS620() throws IOException {
		assertEquals(Constants.CHARSET_TIS620, UniversalDetector.detectCharset(getFileResource("tis620.txt")));
	}

}
