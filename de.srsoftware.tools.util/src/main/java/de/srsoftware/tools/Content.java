/* © SRSoftware 2024 */
package de.srsoftware.tools;

/**
 * A wrapper for results that carry an actual payload
 * @param <Payload> the type of the expected payload
 */
public class Content<Payload> implements Result<Payload> {
	private final Payload object;

	/**
	 * Wrap a payload as a successful instance of Result
	 * @param object the payload object
	 */
	public Content(Payload object) {
		this.object = object;
	}

	/**
	 * wrap a payload
	 * @param object the payload to pack
	 * @return the wrapped payload object
	 * @param <T> the type of the payload
	 */
	public static <T> Content<T> of(T object) {
		return new Content<>(object);
	}

	/**
	 * get the payload
	 * @return the payload object
	 */
	public Payload get() {
		return object;
	}
}
