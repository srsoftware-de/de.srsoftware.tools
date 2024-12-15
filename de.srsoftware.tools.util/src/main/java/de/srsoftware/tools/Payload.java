/* © SRSoftware 2024 */
package de.srsoftware.tools;


public class Payload<T> implements Result<T> {
	private final T object;

	public Payload(T object) {
		this.object = object;
	}

	public static <T> Payload<T> of(T object) {
		return new Payload<>(object);
	}

	@Override
	public boolean isError() {
		return false;
	}

	public T get() {
		return object;
	}
}
