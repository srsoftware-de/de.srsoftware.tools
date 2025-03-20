/* © SRSoftware 2025 */
package de.srsoftware.tools;

import java.util.Collection;
import java.util.List;
import java.util.Map;

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
	 * @param data (optional) data to carry along
	 * @param exceptions (optional) exceptions to wrap
	 */
	public HttpError(int code, String message, Map<String, Object> data, Collection<Exception> exceptions) {
		super(message, data, exceptions);
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
	 * create a new HttpError object carrying the passed message
	 * @param code the error code
	 * @param message the message to add to the Error object, may contain placeholder marks
	 * @param fills the list of objects to fill the marks
	 * @param <T> the type of the result expected at the place this error occurred
	 * @return the created HttpError object
	 */
	public static <T> HttpError<T> of(int code, String message, Object... fills) {
		return new HttpError<>(code, message.formatted(fills), null, null);
	}

	/**
	 * create a new HttpError object carrying the passed message
	 * @param code the error code
	 * @param exceptions (optional) exceptions to wrap
	 * @param message the message to add to the Error object, may contain placeholder marks
	 * @param fills the list of objects to fill the marks
	 * @param <T> the type of the result expected at the place this error occurred
	 * @return the created HttpError object
	 */
	public static <T> HttpError<T> of(int code, Collection<Exception> exceptions, String message, Object... fills) {
		return new HttpError<>(code, message.formatted(fills), null, exceptions);
	}

	/**
	 * create a new HttpError object carrying the passed message
	 * @param code the error code
	 * @param exception exceptions to wrap
	 * @param message the message to add to the Error object, may contain placeholder marks
	 * @param fills the list of objects to fill the marks
	 * @param <T> the type of the result expected at the place this error occurred
	 * @return the created HttpError object
	 */
	public static <T> HttpError<T> of(int code, Exception exception, String message, Object... fills) {
		return new HttpError<>(code, message.formatted(fills), null, List.of(exception));
	}

	/**
	 * create a new HttpError object carrying the passed message
	 * @param code the error code
	 * @param data (optional) data to carry along
	 * @param message the message to add to the Error object, may contain placeholder marks
	 * @param fills the list of objects to fill the marks
	 * @param <T> the type of the result expected at the place this error occurred
	 * @return the created HttpError object
	 */
	public static <T> HttpError<T> of(int code, Map<String, Object> data, String message, Object... fills) {
		return new HttpError<>(code, message.formatted(fills), data, null);
	}

	/**
	 * create a new HttpError object carrying the passed message
	 * @param code the error code
	 * @param data (optional) data to carry along
	 * @param exceptions (optional) exceptions to wrap
	 * @param message the message to add to the Error object, may contain placeholder marks
	 * @param fills the list of objects to fill the marks
	 * @param <T> the type of the result expected at the place this error occurred
	 * @return the created HttpError object
	 */
	public static <T> HttpError<T> of(int code, Map<String, Object> data, Collection<Exception> exceptions, String message, Object... fills) {
		return new HttpError<>(code, message.formatted(fills), data, exceptions);
	}

	/**
	 * create a new HttpError object carrying the passed message
	 * @param code the error code
	 * @param data (optional) data to carry along
	 * @param exception exception to wrap
	 * @param message the message to add to the Error object, may contain placeholder marks
	 * @param fills the list of objects to fill the marks
	 * @param <T> the type of the result expected at the place this error occurred
	 * @return the created HttpError object
	 */
	public static <T> HttpError<T> of(int code, Map<String, Object> data, Exception exception, String message, Object... fills) {
		return new HttpError<>(code, message.formatted(fills), data, List.of(exception));
	}

	/**
	 * create an HttpError with the same metadata content as this Object, but with different payload type
	 * @return the transformed Error (new object)
	 * @param <NewType> the payload type of the returned error
	 */
	@Override
	public <NewType> HttpError<NewType> transform() {
		return new HttpError<>(code, message(), data(), exceptions());
	}
}
