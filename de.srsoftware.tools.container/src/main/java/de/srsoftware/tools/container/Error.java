/* © SRSoftware 2025 */
package de.srsoftware.tools.container;

import static java.text.MessageFormat.format;
import static java.util.Optional.empty;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;
import org.json.JSONObject;

/**
 * This class may be returned by methods whose execution failed.
 * It may carry additional data about the cause of the failure
 * @param <None> This result is not expected to carry a payload in the sense of a positive execution result.
 */
public class Error<None> implements Container<None> {
	/** data **/
	public static final String DATA = "data";
	/** exceptions **/
	public static final String EXCEPTIONS = "exceptions";
	/** message **/
	public static final String MESSAGE = "message";
	private final List<Exception> exceptions = new ArrayList<>();
	private final Map<String, Object> data   = new HashMap<>();
	private final String	  message;

	/**
	 * create a new Error object carrying the passed message
	 * @param message the message to add to the Error object
	 * @param data (optional) data to carry along
	 * @param exceptions (optional) exceptions to wrap
	 */
	public Error(String message, Map<String, Object> data, Collection<Exception> exceptions) {
		this.message = message;
		if (data != null) this.data.putAll(data);
		if (exceptions != null) this.exceptions.addAll(exceptions);
	}

	/**
	 * add an exception to this Error object
	 * @param exception the exception to add
	 * @return this object
	 */
	public Error<None> add(Exception exception) {
		exceptions.add(exception);
		return this;
	}

	/**
	 * Add values to the data map
	 * @param tokens a list of objects. The even elements are used as key in the data map, the odd ones as values.
	 * @return this Error object
	 */
	public Error<None> addData(Object... tokens) {
		for (int i = 0; i < tokens.length - 1; i += 2) data.put(tokens[i].toString(), tokens[i + 1]);
		return this;
	}

	/**
	 * return the data map
	 * @return metadata added to this Error object
	 */
	public Map<String, Object> data() {
		return data;
	}

	/**
	 * create a new Error object carrying the passed message
	 * @param message the message to add to the Error object, may contain placeholder marks
	 * @param fills the list of objects to fill the marks
	 * @param <T> the type of the result expected at the place this error occurred
	 * @return the created Error object
	 */
	public static <T> Error<T> error(String message, Object... fills) {
		return new Error<>(format(message,fills), null, null);
	}

	/**
	 * create a new Error object carrying the passed message
	 * @param exception exception to wrap
	 * @param message the message to add to the Error object, may contain placeholder marks
	 * @param fills the list of objects to fill the marks
	 * @param <T> the type of the result expected at the place this error occurred
	 * @return the created Error object
	 */
	public static <T> Error<T> error(Exception exception, String message, Object... fills) {
		return new Error<>(format(message,fills), null, exception == null ? null : List.of(exception));
	}

	/**
	 * create a new Error object carrying the passed message
	 * @param exceptions (optional) exceptions to wrap
	 * @param message the message to add to the Error object, may contain placeholder marks
	 * @param fills the list of objects to fill the marks
	 * @param <T> the type of the result expected at the place this error occurred
	 * @return the created Error object
	 */
	public static <T> Error<T> error(Collection<Exception> exceptions, String message, Object... fills) {
		return new Error<>(format(message,fills), null, exceptions);
	}

	/**
	 * create a new Error object carrying the passed message
	 * @param data (optional) data to carry along
	 * @param message the message to add to the Error object, may contain placeholder marks
	 * @param fills the list of objects to fill the marks
	 * @param <T> the type of the result expected at the place this error occurred
	 * @return the created Error object
	 */
	public static <T> Error<T> error(Map<String, Object> data, String message, Object... fills) {
		return new Error<>(format(message,fills), data, null);
	}

	/**
	 * create a new Error object carrying the passed message
	 * @param data (optional) data to carry along
	 * @param exception exceptions to wrap
	 * @param message the message to add to the Error object, may contain placeholder marks
	 * @param fills the list of objects to fill the marks
	 * @param <T> the type of the result expected at the place this error occurred
	 * @return the created Error object
	 */
	public static <T> Error<T> error(Map<String, Object> data, Exception exception, String message, Object... fills) {
		return new Error<>(format(message,fills), data, exception == null ? null : List.of(exception));
	}

	/**
	 * create a new Error object carrying the passed message
	 * @param data (optional) data to carry along
	 * @param exceptions (optional) exceptions to wrap
	 * @param message the message to add to the Error object, may contain placeholder marks
	 * @param fills the list of objects to fill the marks
	 * @param <T> the type of the result expected at the place this error occurred
	 * @return the created Error object
	 */
	public static <T> Error<T> error(Map<String, Object> data, Collection<Exception> exceptions, String message, Object... fills) {
		return new Error<>(format(message,fills), data, exceptions);
	}

	/**
	 * get the collection of Exceptions added to this Error object
	 * @return the list of Exceptions
	 */
	public Collection<Exception> exceptions() {
		return exceptions;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	/**
	 * Create a json object of the error.
	 * @return the json object describing the error.
	 */
	public JSONObject json() {
		var json = new JSONObject(Map.of(MESSAGE, message));
		if (!exceptions.isEmpty()) json.put(EXCEPTIONS, exceptions);
		if (!data.isEmpty()) json.put(DATA, data);
		return json;
	}

	/**
	 * return the message encapsulated in this error object
	 * @return the message string
	 */
	public String message() {
		return message;
	}


	@Override
	public Optional<None> optional() {
		return empty();
	}

	@Override
	public <Inner> Stream<Inner> stream() {
		return Stream.empty();
	}

	@Override
	public <Inner> Stream<Container<Inner>> streamContained() {
		return Stream.of(this.transform());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(message);
		for (var ex : exceptions) sb.append("\n").append(ex.getMessage());
		return sb.toString();
	}


	/**
	 * map an error to another object
	 * @param mapper the mapper function
	 * @return the object returned by the mapper function
	 * @param <T> type of the expected result
	 */
	public <T> T then(Function<Error<None>, T> mapper) {
		return mapper.apply(this);
	}


	/**
	 * create an Error with the same metadata content as this Object, but with different payload type
	 * @return the transformed Error (new object)
	 * @param <NewType> the payload type of the returned error
	 */
	public <NewType> Error<NewType> transform() {
		return Error.error(data, exceptions, message);
	}

	/**
	 * Wrap an error object as Optional
	 * @return an Optional carrying this Error object
	 */
	public Optional<Error<None>> wrap() {
		return Optional.of(this);
	}
}
