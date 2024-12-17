/* © SRSoftware 2024 */
package de.srsoftware.tools;

public class Comment implements Result<String> {
	/**
	 * Wrap a payload as a successful instance of Result
	 *
	 * @param ignored the payload object
	 */
	public Comment(String ignored) {
	}

	public static Comment of(String comment) {
		return new Comment(comment);
	}
}
