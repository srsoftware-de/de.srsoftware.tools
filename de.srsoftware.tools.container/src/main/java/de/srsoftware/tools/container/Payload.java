/* © SRSoftware 2025 */
package de.srsoftware.tools.container;

import static de.srsoftware.tools.container.Error.error;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * A wrapper for results that carry an actual payload
 * @param <Content> the type of the expected payload
 */
public class Payload<Content> implements Container<Content> {
	private final Content object;

	/**
	 * Wrap a payload as a successful instance of Result
	 * @param object the payload object
	 */
	public Payload(Content object) {
		this.object = object;
	}

	/**
	 * get the payload
	 * @return the payload object
	 */
	public Content get() {
		return object;
	}

	/**
	 * wrap a payload
	 * @param object the payload to pack
	 * @return the wrapped payload object
	 * @param <P> the type of the payload
	 */
	public static <P> Container<P> of(P object) {
		if (object == null) return error("Can not create payload of NULL value!");
		return new Payload<>(object);
	}

	@Override
	public Optional<Content> optional() {
		return Optional.ofNullable(object);
	}

	@Override
	public <Inner> Stream<Inner> stream() throws ClassCastException {
		if (object instanceof Collection<?> coll) {
				Collection<Inner> collection = (Collection<Inner>)coll;
				return collection.stream();
		}
		var inner = (Inner)object;
		return Stream.of(inner);
	}

	@Override
	public <Inner> Stream<Container<Inner>> streamContained() {
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

	/**
	 * map a payload to another object
	 * @param mapper the mapper function
	 * @return the object returned by the mapper function
	 * @param <T>
	 */
	public <T> T then(Function<Payload<Content>, T> mapper) {
		return mapper.apply(this);
	}

	@Override
	public String toString() {
		return object.toString();
	}
}
