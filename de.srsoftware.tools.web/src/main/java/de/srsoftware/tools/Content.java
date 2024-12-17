package de.srsoftware.tools;

import static de.srsoftware.tools.NotImplemented.notImplemented;

public class Content extends Payload<Tag> {
	private final String content;

	/**
	 * Wrap a payload as a successful instance of Result
	 *
	 * @param object the payload object
	 */
	public Content(Tag object) {
		super(object);
		throw notImplemented(Content.class,"constructor");
	}

	public Content(String content){
		super(null);
		this.content = content;
	}

	public String content(){
		return content;
	}
}
