/* © SRSoftware 2024 */
package de.srsoftware.tools;

public class OpeningTag extends Payload<Tag> {
	/**
	 * Wrap a payload as a successful instance of Result
	 *
	 * @param object the payload object
	 */
	public OpeningTag(Tag object) {
		super(object);
	}

	public static OpeningTag of(Tag tag) {
		return new OpeningTag(tag);
	}
}
