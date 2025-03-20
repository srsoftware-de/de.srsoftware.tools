/* © SRSoftware 2025 */
package de.srsoftware.tools.result;

import static de.srsoftware.tools.result.Error.error;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * This interface can be used as a result type for functions that may return something or an error.
 * @param <Content> The expected type of the result payload
 */
public interface Result<Content> {
	/**
	 * Transform this Result object to another via a mapping function.
	 * @param mapper a function, that processes results and produces results
	 * @return the result of the mapping function
	 * @param <Mapped> the payload type of the result of the mapping function
	 */
	public <Mapped> Result<Mapped> map(Function<Result<Content>, Result<Mapped>> mapper);

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
	public <Inner> Stream<Result<Inner>> stream();

	/**
	 * Transforms the Result to an Error with appropriate payload type
	 * @param res if this result is an error, it will be transformed using the Error.transform method, otherwise a new Error will be created
	 * @return the transformed error
	 * @param <T> the payload type of the returned error
	 */
	public static <T> Result<T> transform(Result<?> res) {
		return res instanceof Error<?> err ? err.transform() : error("Invalid parameter: %s", res.getClass().getSimpleName());
	}
}
