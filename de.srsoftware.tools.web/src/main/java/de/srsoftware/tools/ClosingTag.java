/* © SRSoftware 2025 */
package de.srsoftware.tools;

/**
 * Represents a Tag of the form &lt;/ type&gt;
 */
public class ClosingTag extends Tag {
	/**
	 * create a new instance
	 * @param type the type of the tag
	 */
	public ClosingTag(String type) {
		super(type);
	}

	/**
	 * helper method to create a new instance
	 * @param type type of the tag to be created
	 * @return the created tag
	 */
	public static ClosingTag of(String type) {
		return new ClosingTag(type);
	}
}
