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
	 * @param message the error message. may contain marks for formatting
	 * @param fills the objects to fill in while formatting
	 */
	public HttpError(int code, String message, Object... fills) {
		super(message.formatted(fills));
		this.code = code;
	}

	/**
	 * return the error code assigned with this error
	 * @return a numeric error code
	 */
	public int code() {
		return code;
	}

	/**
	 * create a new HttpError with a formatted message
	 * @param code the error code
	 * @param message the error message. may contain marks for formatting
	 * @param fills the objects to fill in while formatting
	 * @return the created HttpError object
	 * @param <T> the expected result type
	 */
	public static <T> HttpError<T> of(int code, String message, Object... fills) {
		return new HttpError<T>(code, message, fills);
	}
}
