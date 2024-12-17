package de.srsoftware.tools;

public class TagPayload extends Payload<Tag>{
	/**
	 * Wrap a payload as a successful instance of Result
	 *
	 * @param object the payload object
	 */
	public TagPayload(Tag object) {
		super(object);
	}

	public static TagPayload of(Tag tag){
		return new TagPayload(tag);
	}
}
