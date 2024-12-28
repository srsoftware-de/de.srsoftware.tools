/* © SRSoftware 2024 */
package de.srsoftware.tools;

/**
 * Tag that represents Text in HTML
 */
public class Text extends Tag {
	/**
	 * the content of this text element
	 */
	private final String content;

	/**
	 * create new Text tag with provided content
	 * @param content the content to store in this tag
	 */
	public Text(String content) {
		super(null);
		this.content = content;
	}

	@Override
	public Tag add(Tag... newChildren) {
		throw new RuntimeException("Must not add tag(s) to Text!");
	}

	@Override
	public <T extends Tag> T attr(String key, String val) {
		throw new RuntimeException("Must not add attribute to Text!");
	}

	@Override
	protected void indent(StringBuilder sb, int indent, int currentIndentation) {
		sb.append(" ".repeat(currentIndentation)).append(content).append("\n");
	}

	public static Text of(String content) {
		return new Text(content);
	}

	@Override
	public String strip() {
		return content;
	}

	@Override
	public String toString() {
		return content;
	}

	@Override
	public String toString(int indent) {
		return content;
	}
}
