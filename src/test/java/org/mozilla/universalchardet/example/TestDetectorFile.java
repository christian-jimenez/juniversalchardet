package org.mozilla.universalchardet.example;

import java.io.File;

import org.mozilla.universalchardet.UniversalDetector;

public class TestDetectorFile {

	public static void main (final String[] args) throws java.io.IOException {
		if (args.length != 1) {
			System.err.println("Usage: java TestDetectorFile FILENAME");
			System.exit(1);
		}

		String encoding = UniversalDetector.detectCharset(new File(args[0]));
		if (encoding != null) {
			System.out.println("Detected encoding = " + encoding);
		} else {
			System.out.println("No encoding detected.");
		}
	}
}
