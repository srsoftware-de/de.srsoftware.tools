package de.srsoftware.tools;

public class Closing extends Payload<Tag> {

	/**
	 * Wrap a payload as a successful instance of Result
	 *
	 * @param object the payload object
	 */
	public Closing(Tag object) {
		super(object);
	}

	public static Closing of(Tag tag){
		return new Closing(tag);
	}
}
