/* © SRSoftware 2024 */
package de.srsoftware.tools;

/**
 * Represents a Tag of the form &lt;type /&gt;
 */
public class ClosedTag extends Tag {
	/**
	 * create a new instance
	 * @param type the type of the tag
	 */
	public ClosedTag(String type) {
		super(type);
	}

	/**
	 * helper method to create a new instance
	 * @param type type of the tag to be created
	 * @return the created tag
	 */
	public static ClosedTag of(String type) {
		return new ClosedTag(type);
	}
}
