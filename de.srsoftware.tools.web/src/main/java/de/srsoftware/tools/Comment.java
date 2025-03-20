/* © SRSoftware 2025 */
package de.srsoftware.tools;

/**
 * Represents a comment tag
 */
public class Comment extends Tag {
	/**
	 * create a comment tag
	 * @param content the comment
	 */
	public Comment(String content) {
		super("!--");
	}

	/**
	 * create a comment tag
	 * @param content the comment
	 * @return the created tag
	 */
	public static Comment of(String content) {
		return new Comment(content);
	}
}
