/*
 * (C) Copyright 2016-2017 Alberto Fernández <infjaf@gmail.com> (C) Copyright 2006-2007 Kohei TAKETA <k-tak@void.in> (Java port) (C) Copyright 2001 Netscape Communications Corporation.
 *
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version 1.1 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for the specific language governing rights and
 * limitations under the License.
 *
 * The Original Code is Mozilla Universal charset detector code.
 *
 * The Initial Developer of the Original Code is Netscape Communications Corporation. Portions created by the Initial Developer are Copyright (C) 2001 the Initial Developer. All Rights Reserved.
 *
 * Contributor(s): Shy Shalom <shooshX@gmail.com> Kohei TAKETA <k-tak@void.in> (Java port)
 *
 * Alternatively, the contents of this file may be used under the terms of either the GNU General Public License Version 2 or later (the "GPL"), or the GNU Lesser General Public License Version 2.1 or
 * later (the "LGPL"), in which case the provisions of the GPL or the LGPL are applicable instead of those above. If you wish to allow use of your version of this file only under the terms of either
 * the GPL or the LGPL, and not to allow others to use your version of this file under the terms of the MPL, indicate your decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete the provisions above, a recipient may use your version of this file under the terms of any one of the MPL, the GPL or the
 * LGPL.
 */
package org.mozilla.universalchardet;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.mozilla.universalchardet.exception.ProberInitializationException;
import org.mozilla.universalchardet.prober.CharsetProber;
import org.mozilla.universalchardet.prober.EscCharsetProber;
import org.mozilla.universalchardet.prober.Latin1Prober;
import org.mozilla.universalchardet.prober.MBCSGroupProber;
import org.mozilla.universalchardet.prober.SBCSGroupProber;

public class UniversalDetector {

	public static final float SHORTCUT_THRESHOLD = 0.95f;
	public static final float MINIMUM_THRESHOLD = 0.20f;
	private static final String WITH_BOM = " with BOM";

	public enum InputState {
		PURE_ASCII, ESC_ASCII, HIGHBYTE
	}

	private InputState inputState;
	private boolean done;
	private boolean start;
	private boolean gotData;
	private byte lastChar;
	private String detectedCharset;
	private boolean bom;

	private CharsetProber[] probers;
	private CharsetProber escCharsetProber;

	private CharsetListener listener;

	public UniversalDetector() {
		this(null);
	}

	/**
	 * @param listener a listener object that is notified of the detected encoding. Can be null.
	 */
	public UniversalDetector(final CharsetListener listener) {
		this.listener = listener;
		this.escCharsetProber = null;
		this.probers = new CharsetProber[3];

		this.reset();
	}

	public boolean isDone() {
		return this.done;
	}

	/**
	 * @return The detected encoding is returned. If the detector couldn't determine what encoding was used, null is returned.
	 */
	public String getDetectedCharset() {
		return this.detectedCharset;
	}

	public boolean isBom() {
		return this.bom;
	}

	public void setListener(final CharsetListener listener) {
		this.listener = listener;
	}

	public CharsetListener getListener() {
		return this.listener;
	}

	/**
	 * Feed the detector with more data
	 *
	 * @param buf The buffer containing the data
	 */
	public void handleData(final byte[] buf) {
		this.handleData(buf, 0, buf.length);
	}

	private void setGotDataIfHasLength(final int length) {
		if (length > 0) {
			this.gotData = true;
		}
	}

	private boolean detectFromStart(final byte[] buf, final int offset, final int length) {
		boolean result = false;

		if (this.start) {
			this.start = false;
			if (length > 3) {
				String detectedBOM = detectCharsetFromBOM(buf, offset);
				if (null != detectedBOM) {
					this.detectedCharset = detectedBOM;
					this.done = true;
					this.bom = true;
					result = true;
				}
			}
		}

		return result;
	}

	private boolean isHighByteInputState() {
		return (InputState.HIGHBYTE == this.inputState);
	}

	private boolean isPureAsciiInputState() {
		return (InputState.PURE_ASCII == this.inputState);
	}

	private boolean isEscAsciiInputState() {
		return (InputState.ESC_ASCII == this.inputState);
	}

	private void resetEscCharsetProber() {
		if (null != this.escCharsetProber) {
			this.escCharsetProber = null;
		}
	}

	private void initEscCharsetProber() {
		if (null == this.escCharsetProber) {
			this.escCharsetProber = new EscCharsetProber();
		}
	}

	private <P extends CharsetProber> void addProber(final int position, final Class<P> proberClass) throws InstantiationException, IllegalAccessException {
		if (null == this.probers[position]) {
			this.probers[position] = proberClass.newInstance();
		}
	}

	private void initialiseProbers() {
		try {
			this.addProber(0, MBCSGroupProber.class);
			this.addProber(1, SBCSGroupProber.class);
			this.addProber(2, Latin1Prober.class);
		} catch (InstantiationException | IllegalAccessException e) {
			throw new ProberInitializationException("Unable to instantiate probers", e);
		}
	}

	private void setInputStateAndProbers(final byte[] buf, final int offset, final int length) {
		int maxPos = offset + length;
		for (int i = offset; i < maxPos; ++i) {
			int c = buf[i] & 0xFF;
			if (((c & 0x80) != 0) && (c != 0xA0)) {
				if (!this.isHighByteInputState()) {
					this.inputState = InputState.HIGHBYTE;
					this.resetEscCharsetProber();
					this.initialiseProbers();
				}
			} else {
				if ((this.isPureAsciiInputState()) && ((c == 0x1B) || ((c == 0x7B) && (this.lastChar == 0x7E)))) {
					this.inputState = InputState.ESC_ASCII;
				}

				this.lastChar = buf[i];
			}
		}
	}

	private void handleDataWithProber(final byte[] buf, final int offset, final int length, final CharsetProber prober) {
		CharsetProber.ProbingState probingState = prober.handleData(buf, offset, length);
		if (CharsetProber.ProbingState.FOUND_IT == probingState) {
			this.done = true;
			this.detectedCharset = prober.getCharSetName();
		}
	}

	private void handleDataWithProbers(final byte[] buf, final int offset, final int length) {
		for (int i = 0; (i < this.probers.length) && (!this.done); i++) {
			this.handleDataWithProber(buf, offset, length, this.probers[i]);
		}
	}

	/**
	 * Feed the detector with more data
	 *
	 * @param buf    Buffer with the data
	 * @param offset initial position of data in buf
	 * @param length length of data
	 */
	public void handleData(final byte[] buf, final int offset, final int length) {
		if (!this.done) {
			this.setGotDataIfHasLength(length);

			if (!this.detectFromStart(buf, offset, length)) {
				this.setInputStateAndProbers(buf, offset, length);

				if (this.isEscAsciiInputState()) {
					this.initEscCharsetProber();
					this.handleDataWithProber(buf, offset, length, this.escCharsetProber);
				} else {
					if (this.isHighByteInputState()) {
						this.handleDataWithProbers(buf, offset, length);
					} // If is pure ASCII do nothing
				}
			}
		}
	}

	private static String checkIsUtf8Charset(final int b2, final int b3) {
		return ((b2 == 0xBB) && (b3 == 0xBF)) ? Constants.CHARSET_UTF_8 : null;
	}

	private static String checkIsIso10646Ucs43412Charset(final int b2, final int b3, final int b4) {
		return ((b2 == 0xFF) && (b3 == 0x00) && (b4 == 0x00)) ? Constants.CHARSET_X_ISO_10646_UCS_4_3412 : null;
	}

	private static String checkIsUtf16BECharset(final int b2) {
		return (b2 == 0xFF) ? Constants.CHARSET_UTF_16BE : null;
	}

	private static String checkIsUtf32BECharset(final int b2, final int b3, final int b4) {
		return ((b2 == 0x00) && (b3 == 0xFE) && (b4 == 0xFF)) ? Constants.CHARSET_UTF_32BE : null;
	}

	private static String checkIsIso10646Ucs42143Charset(final int b2, final int b3, final int b4) {
		return ((b2 == 0x00) && (b3 == 0xFF) && (b4 == 0xFE)) ? Constants.CHARSET_X_ISO_10646_UCS_4_2143 : null;
	}

	private static String checkIsUtf16LECharset(final int b2) {
		return (b2 == 0xFE) ? Constants.CHARSET_UTF_16LE : null;
	}

	private static String checkIsUtf32LECharset(final int b2, final int b3, final int b4) {
		return ((b2 == 0xFE) && (b3 == 0x00) && (b4 == 0x00)) ? Constants.CHARSET_UTF_32LE : null;
	}

	public static String detectCharsetFromBOM(final byte[] buf) {
		return detectCharsetFromBOM(buf, 0);
	}

	private static String detectCharsetFromBOM(final byte[] buf, final int offset) {
		String charset = null;

		if (buf.length > (offset + 3)) {
			int b1 = buf[offset] & 0xFF;
			int b2 = buf[offset + 1] & 0xFF;
			int b3 = buf[offset + 2] & 0xFF;
			int b4 = buf[offset + 3] & 0xFF;

			switch (b1) {
			case 0xEF:
				charset = checkIsUtf8Charset(b2, b3);
				break;
			case 0xFE:
				String isoCharset = checkIsIso10646Ucs43412Charset(b2, b3, b4);
				charset = (null != isoCharset) ? isoCharset : checkIsUtf16BECharset(b2);
				break;
			case 0x00:
				String utfBeCharset = checkIsUtf32BECharset(b2, b3, b4);
				charset = (null != utfBeCharset) ? utfBeCharset : checkIsIso10646Ucs42143Charset(b2, b3, b4);
				break;
			case 0xFF:
				String utfCharset = checkIsUtf32LECharset(b2, b3, b4);
				charset = (null != utfCharset) ? utfCharset : checkIsUtf16LECharset(b2);
				break;
			default:
				break;
			}
		}

		return charset;
	}

	private boolean hasDetectedCharset() {
		boolean detected = false;
		if (null != this.detectedCharset) {
			this.done = true;
			this.reportDetectedCharset();

			detected = true;
		}

		return detected;
	}

	private void reportDetectedCharset() {
		if (null != this.listener) {
			this.listener.report(this.detectedCharset);
		}
	}

	/**
	 * Marks end of data reading. Finish calculations.
	 */
	public void dataEnd() {
		if ((this.gotData) && (!this.hasDetectedCharset()) && (this.isHighByteInputState())) {
			float proberConfidence;
			float maxProberConfidence = 0.0f;
			int maxProber = 0;

			for (int i = 0; i < this.probers.length; ++i) {
				proberConfidence = this.probers[i].getConfidence();
				if (proberConfidence > maxProberConfidence) {
					maxProberConfidence = proberConfidence;
					maxProber = i;
				}
			}

			if (maxProberConfidence > MINIMUM_THRESHOLD) {
				this.detectedCharset = this.probers[maxProber].getCharSetName();
				this.reportDetectedCharset();
			}
		} // If InputState is ESC_ASCII or other do nothing
	}

	/**
	 * Resets detector to be used again.
	 */
	public void reset() {
		this.done = false;
		this.start = true;
		this.detectedCharset = null;
		this.gotData = false;
		this.inputState = InputState.PURE_ASCII;
		this.lastChar = 0;
		this.bom = false;

		if (this.escCharsetProber != null) {
			this.escCharsetProber.reset();
		}

		for (CharsetProber prober : this.probers) {
			if (prober != null) {
				prober.reset();
			}
		}
	}

	/**
	 * Gets the charset of a File.
	 *
	 * @param file The file to check charset for
	 * @return The charset of the file, null if cannot be determined
	 * @throws IOException if some IO error occurs
	 */
	public static String detectCharset(final File file) throws IOException {
		return detectCharset(file.toPath());
	}

	/**
	 * Gets the charset of a Path.
	 *
	 * @param path The path to file to check charset for
	 * @return The charset of the file, null if cannot be determined
	 * @throws IOException if some IO error occurs
	 */
	public static String detectCharset(final Path path) throws IOException {
		try (InputStream fis = new BufferedInputStream(Files.newInputStream(path))) {
			return detectCharset(fis);
		}
	}

	/**
	 * Gets the charset of content from InputStream.
	 *
	 * @param inputStream InputStream containing text file
	 * @return The charset of the file, null if cannot be determined
	 * @throws IOException if some IO error occurs
	 */
	public static String detectCharset(final InputStream inputStream) throws IOException {
		byte[] buf = new byte[4096];

		UniversalDetector detector = new UniversalDetector(null);

		int nread;
		while (((nread = inputStream.read(buf)) > 0) && (!detector.isDone())) {
			detector.handleData(buf, 0, nread);
		}

		detector.dataEnd();

		String encoding = detector.getDetectedCharset();

		if ((detector.isBom()) && (Constants.CHARSET_UTF_8 == detector.getDetectedCharset())) {
			encoding += WITH_BOM;
		}

		detector.reset();

		return encoding;
	}

}
