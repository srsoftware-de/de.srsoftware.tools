/* © SRSoftware 2024 */
package de.srsoftware.tools;

import java.util.function.Function;

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
}
