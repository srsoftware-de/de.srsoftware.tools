/* © SRSoftware 2024 */
package de.srsoftware.tools;

import java.util.*;
import java.util.function.Function;
import org.json.JSONObject;

/**
 * This class may be returned by methods whose execution failed.
 * It may carry additional data about the cause of the failure
 * @param <None> This result is not expected to carry a payload in the sense of a positive execution result.
 */
public class Error<None> implements Result<None> {
	private final List<Exception> exceptions = new ArrayList<>();
	private final Map<String, Object> data   = new HashMap<>();
	private final String	  message;

	/**
	 * create a new Error object carrying the passed message
	 * @param message the message to add to the Error object
	 */
	public Error(String message) {
		this.message = message;
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
	 * get the list of Exceptions added to this Error object
	 * @return the list of Exceptions
	 */
	public List<Exception> exceptions() {
		return exceptions;
	}

	/**
	 * create a new Error object with the message formatted with the given fills
	 * @param message a message string, which may contain placeholders
	 * @param fills the objects to replace the placeholders
	 * @return a new Error object populated with the formatted message
	 * @param <None> any type
	 */
	public static <None> Error<None> format(String message, Object... fills) {
		return new Error<None>(message.formatted(fills));
	}

	/**
	 * Create a json object of the error.
	 * @return the json object describing the error.
	 */
	public JSONObject json() {
		var json = new JSONObject(Map.of("error", message));
		if (!exceptions.isEmpty()) json.put("exceptions", exceptions);
		if (!data.isEmpty()) json.put("data", data);
		return json;
	}

	@Override
	public <Mapped> Result<Mapped> map(Function<Result<None>, Result<Mapped>> mapper) {
		return mapper.apply(this);
	}

	/**
	 * create a new Error object carrying the passed message
	 * @param message the message to add to the Error object
	 * @return a new Error object populated with the passed message
	 * @param <None> any type
	 * @param exceptions exceptions to be added to the errors metadata
	 */
	public static <None> Error<None> of(String message, Exception... exceptions) {
		var err = new Error<None>(message);
		for (Exception e : exceptions) err.add(e);
		return err;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(message);
		for (var ex : exceptions) sb.append("\n").append(ex.getMessage());
		return sb.toString();
	}

	/**
	 * create an Error with the same metadata content as this Object, but with different payload type
	 * @return the transformed Error (new object)
	 * @param <NewType> the payload type of the returned error
	 */
	public <NewType> Error<NewType> transform() {
		Error<NewType> transformed = Error.of(message);
		transformed.data.putAll(data());
		transformed.exceptions.addAll(exceptions);
		return transformed;
	}

	/**
	 * Wrap an error object as Optional
	 * @return an Optional carrying this Error object
	 */
	public Optional<Error<None>> wrap() {
		return Optional.of(this);
	}
}
