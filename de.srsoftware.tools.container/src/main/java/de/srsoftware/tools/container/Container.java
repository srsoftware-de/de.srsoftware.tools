/* © SRSoftware 2025 */
package de.srsoftware.tools.container;

import static de.srsoftware.tools.container.Error.error;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * This interface can be used as a result type for functions that may return something or an error.
 * @param <Content> The expected type of the result payload
 */
public interface Container<Content> {

	/**
	 * checks whether the object returned by optional() is empty
	 * @return true, only if optional().isEmpty() returns true
	 */
	public default boolean isEmpty(){
		return optional().isEmpty();
	}

	/**
	 * Transform this Result object to another via a mapping function.
	 * @param mapper a function, that processes results and produces results
	 * @return the result of the mapping function
	 * @param <T> the result type of the mapping function
	 */
	public default <T> T map(Function<Container<Content>, T> mapper){
		return mapper.apply(this);
	}

	/**
	 * Create an Optional from a Result.
	 * @return An optional containing the Result`s content, if set.
	 */
	public Optional<Content> optional();

	/**
	 * Create a stream of the Payload. This may be suitable, if the payload is a collection.
	 * @return a stream of results
	 * @param <Inner> the type of the payloads of the elements of the stream
	 */
	public <Inner> Stream<Container<Inner>> streamContained();

	/**
	 * Create a stream of the Payload. This may be suitable, if the payload is a collection.
	 * @param <Inner> the type of the elements of the stream
	 * @return a stream of the contents items.
	 */
	public <Inner> Stream<Inner> stream();

	/**
	 * Transforms the Result to an Error with appropriate payload type
	 * @param res if this result is an error, it will be transformed using the Error.transform method, otherwise a new Error will be created
	 * @return the transformed error
	 * @param <T> the payload type of the returned error
	 */
	public static <T> Error<T> transform(Container<?> res) {
		return res instanceof Error<?> err ? err.transform() : error("Invalid parameter: %s", res.getClass().getSimpleName());
	}
}
