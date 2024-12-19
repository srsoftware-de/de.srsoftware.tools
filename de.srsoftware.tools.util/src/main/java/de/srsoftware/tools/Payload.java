/* © SRSoftware 2024 */
package de.srsoftware.tools;

import java.util.function.Function;

/**
 * A wrapper for results that carry an actual payload
 * @param <P> the type of the expected payload
 */
public class Payload<P> implements Result<P> {
	private final P object;

	/**
	 * Wrap a payload as a successful instance of Result
	 * @param object the payload object
	 */
	public Payload(P object) {
		this.object = object;
	}

	/**
	 * wrap a payload
	 * @param object the payload to pack
	 * @return the wrapped payload object
	 * @param <P> the type of the payload
	 */
	public static <P> de.srsoftware.tools.Payload<P> of(P object) {
		return new de.srsoftware.tools.Payload<>(object);
	}

	/**
	 * get the payload
	 * @return the payload object
	 */
	public P get() {
		return object;
	}

	@Override
	public <Mapped> Result<Mapped> map(Function<Result<P>, Result<Mapped>> mapper) {
		return mapper.apply(this);
	}

	@Override
	public String toString() {
		return object.toString();
	}
}
