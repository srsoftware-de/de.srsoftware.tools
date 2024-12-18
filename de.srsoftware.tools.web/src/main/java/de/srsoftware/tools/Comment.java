/* © SRSoftware 2024 */
package de.srsoftware.tools;

public class Comment extends Tag {
	public Comment(String content) {
		super("!--");
	}

	public static Comment of(String type) {
		return new Comment(type);
	}
}
