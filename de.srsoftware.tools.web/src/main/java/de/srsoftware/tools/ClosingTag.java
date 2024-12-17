/* © SRSoftware 2024 */
package de.srsoftware.tools;

public class ClosingTag extends Tag {
	public ClosingTag(String type) {
		super(type);
	}

	public static ClosingTag of(String type) {
		return new ClosingTag(type);
	}
}
