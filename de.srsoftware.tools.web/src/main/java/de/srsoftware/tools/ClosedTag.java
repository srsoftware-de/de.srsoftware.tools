/* © SRSoftware 2024 */
package de.srsoftware.tools;

public class ClosedTag extends Tag {
	public ClosedTag(String type) {
		super(type);
	}

	public static ClosedTag of(String type) {
		return new ClosedTag(type);
	}
}
