package org.mozilla.universalchardet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class ShortStringTests {

	// Tets case for https://github.com/albfernandez/juniversalchardet/issues/22
	@Test
	@Disabled("data too short")
	public void testDecodeBytes() {

		final String string = "aeaCàêäÇ";
		Charset s;
		byte[] bytes;

		bytes = string.getBytes(StandardCharsets.UTF_8);
		s = this.guessCharset(bytes);
		assertEquals(string, new String(string.getBytes(s), s)); // SUCCESS

		bytes = string.getBytes(StandardCharsets.ISO_8859_1);
		s = this.guessCharset(bytes); // detected charset = TIS-620, Thai charset ???!!!
		assertEquals(string, new String(string.getBytes(s), s)); // FAILS of course !
	}

	// Tets case for https://github.com/albfernandez/juniversalchardet/issues/22
	// With less accute characters, it's improved detection
	@Test
	public void testDecodeBytesBetterStats() {
		final String string = "Château";
		Charset s;
		byte[] bytes;

		bytes = string.getBytes(StandardCharsets.UTF_8);
		s = guessCharset(bytes);
		assertEquals(string, new String(string.getBytes(s), s)); // SUCCESS

		bytes = string.getBytes(StandardCharsets.ISO_8859_1);
		s = guessCharset(bytes);
		assertEquals(string, new String(string.getBytes(s), s)); // SUCCESS
	}

	@Test
	public void testShortString() throws UnsupportedEncodingException {
		assertNull(guessCharsetName("abcd".getBytes()));
	}

	private Charset guessCharset(final byte[] bytes) {
		return Charset.forName(guessCharsetName(bytes));
	}

	private String guessCharsetName(final byte[] bytes) {
		final UniversalDetector detector = new UniversalDetector();
		detector.handleData(bytes, 0, bytes.length);
		detector.dataEnd();
		
		return detector.getDetectedCharset();
	}
}
