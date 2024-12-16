/* © SRSoftware 2024 */
package de.srsoftware.tools;

/**
 * A wrapper for results that carry an actual payload
 * @param <T> the type of the expected payload
 */
public class Payload<T> implements Result<T> {
	private final T object;

	/**
	 * Wrap a payload as a successful instance of Result
	 * @param object the payload object
	 */
	public Payload(T object) {
		this.object = object;
	}

	/**
	 * wrap a payload
	 * @param object the payload to pack
	 * @return the wrapped payload object
	 * @param <T> the type of the payload
	 */
	public static <T> Payload<T> of(T object) {
		return new Payload<>(object);
	}

	@Override
	public boolean isError() {
		return false;
	}

	/**
	 * get the payload
	 * @return the payload object
	 */
	public T get() {
		return object;
	}
}
