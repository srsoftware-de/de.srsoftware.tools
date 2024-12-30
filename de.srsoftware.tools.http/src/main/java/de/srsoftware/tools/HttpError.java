/* © SRSoftware 2024 */
package de.srsoftware.tools;

/**
 * A special Error type that additionally carries an error code
 * @param <NONE> the type of the result that was expected when the error occured
 */
public class HttpError<NONE> extends Error<NONE> {
	private final int code;

	/**
	 * create a new Error object carrying the passed message
	 *
	 * @param code the error code
	 * @param message the message to add to the Error object
	 */
	public HttpError(String message, int code) {
		super(message);
		this.code = code;
	}

	/**
	 * return the error code assigned with this error
	 * @return a numeric error code
	 */
	public int code() {
		return code;
	}
}
