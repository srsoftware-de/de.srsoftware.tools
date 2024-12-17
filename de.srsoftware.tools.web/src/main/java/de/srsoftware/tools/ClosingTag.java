/* © SRSoftware 2024 */
package de.srsoftware.tools;

public class ClosingTag implements Result<Tag> {
	private final Tag tag;
	private ClosingTag(Tag token) {
		this.tag = token;
	}

	public static ClosingTag of(String type) {
		return new ClosingTag(new Tag(type));
	}

	public boolean matches(Tag tag) {
		if (tag == null) return false;
		return this.tag.type().equals(tag.type());
	}

	public Tag get() {
		return tag;
	}
}
