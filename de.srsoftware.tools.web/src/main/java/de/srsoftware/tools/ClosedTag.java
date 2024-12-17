/* © SRSoftware 2024 */
package de.srsoftware.tools;

public class ClosedTag implements Result<Tag> {
	private final Tag tag;

	/**
	 * Wrap a payload as a successful instance of Result
	 *
	 * @param tag the payload tag
	 */
	public ClosedTag(Tag tag) {
		this.tag = tag;
	}

	public static Result<?> of(Tag tag) {
		return new ClosedTag(tag);
	}

	public Tag get() {
		return tag;
	}
}
