/* © SRSoftware 2024 */
package de.srsoftware.tools;

public class OpeningTag extends Tag {
	public OpeningTag(String type) {
		super(type);
	}
	public static OpeningTag of(String type) {
		return new OpeningTag(type);
	}
}
