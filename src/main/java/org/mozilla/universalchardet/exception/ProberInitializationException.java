/**
 *
 */
package org.mozilla.universalchardet.exception;

/**
 * @author Christian.Jimenez
 * @version 1.0
 */
public class ProberInitializationException extends RuntimeException {

	/* */
	private static final long serialVersionUID = -250554778599303890L;

	public ProberInitializationException(final String errorMessage, final Throwable err) {
		super(errorMessage, err);
	}

}
