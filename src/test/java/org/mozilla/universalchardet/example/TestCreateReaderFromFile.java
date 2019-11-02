package org.mozilla.universalchardet.example;


import java.io.File;
import java.io.Reader;

import org.mozilla.universalchardet.ReaderFactory;

public class TestCreateReaderFromFile {

	public static void main (final String[] args) throws java.io.IOException {
		if (args.length != 1) {
			System.err.println("Usage: java TestCreateReaderFromFile FILENAME");
			System.exit(1);
		}

		try (Reader reader = ReaderFactory.createBufferedReader(new File(args[0]));) {
			// Do whatever you want with the reader
		}
	}

}
