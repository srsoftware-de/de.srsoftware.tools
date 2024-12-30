package de.srsoftware.tools;

public class HttpError<NONE> extends Error<NONE>{
	private final int code;

	/**
	 * create a new Error object carrying the passed message
	 *
	 * @param message the message to add to the Error object
	 */
	public HttpError(String message, int code) {
		super(message);
		this.code = code;
	}

	public int code() {
		return code;
	}
}
