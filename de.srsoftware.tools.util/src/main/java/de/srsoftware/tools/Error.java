/* © SRSoftware 2024 */
package de.srsoftware.tools;


import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

/**
 * Generic Error class for methods that return Result
 * @param <T> The return type for actual results
 */
public class Error<T> implements Result<T> {
	private final String        cause;
	private Map<String, Object> metadata;

	/**
	 * Create an Error carrying its cause
	 * @param cause a description why the error was returned
	 */
	public Error(String cause) {
		this.cause = cause;
	}

	/**
	 * Get the cause of the error.
	 * @return return the message describing what caused the error
	 */
	public String cause() {
		return cause;
	}

	@Override
	public boolean isError() {
		return true;
	}

	/**
	 * Create an Error object carrying the given cause and add more metadata
	 * @param cause a description why the error was returned
	 * @param tokens additional metadata
	 * @return the created Error
	 * @param <T> the type of the result that was expected
	 */
	public static <T> Error<T> message(String cause, Object... tokens) {
		var err      = new Error<T>(cause);
		err.metadata = new HashMap<>();
		for (int i = 0; i < tokens.length - 1; i += 2) {
			err.metadata.put(tokens[i].toString(), tokens[i + 1]);
		}
		return err;
	}

	/**
	 * Create a json object of the error.
	 * @return the json object describing the error.
	 */
	public JSONObject json() {
		var json = new JSONObject(Map.of("error", cause));
		if (metadata != null) json.put("metadata", metadata);
		return json;
	}
}
