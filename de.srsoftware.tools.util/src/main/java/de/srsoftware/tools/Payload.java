/* © SRSoftware 2024 */
package de.srsoftware.tools;

import static de.srsoftware.tools.Error.error;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

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
	public static <P> Result<P> of(P object) {
		if (object == null) return error("Can not create payload of NULL value!");
		return new Payload<>(object);
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
	public Optional<P> optional() {
		return Optional.ofNullable(object);
	}

	@Override
	public <Inner> Stream<Result<Inner>> stream() {
		if (object instanceof Collection<?> coll) {
			try {
				Collection<Inner> collection = (Collection<Inner>)coll;
				return collection.stream().map(Payload::of);
			} catch (ClassCastException cce) {
				return Stream.of(error(cce, "Failed to cast %s", coll.getClass().getSimpleName()));
			}
		}
		try {
			var inner = (Inner)object;
			return Stream.of(Payload.of(inner));
		} catch (ClassCastException cce) {
			return Stream.of(error(cce, "Failed to cast %s", object.getClass().getSimpleName()));
		}
	}

	@Override
	public String toString() {
		return object.toString();
	}
}
