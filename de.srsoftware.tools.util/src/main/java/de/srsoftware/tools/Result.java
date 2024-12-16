/* © SRSoftware 2024 */
package de.srsoftware.tools;

/**
 * This interface can be used as a result type for functions that may return something or an error.
 * @param <T> The expected type of the result payload
 */
public interface Result<T> {
	/**
	 * Indicate, whether a given result is an error.
	 * @return true, only it the returned value denotes an error.
	 */
	public boolean isError();
}
