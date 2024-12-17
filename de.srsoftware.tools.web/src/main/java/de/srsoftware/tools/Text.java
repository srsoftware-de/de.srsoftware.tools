package de.srsoftware.tools;

public class Text extends Tag{
	private final String content;

	public Text(String content) {
		super(null);
		this.content = content;
	}

	@Override
	public Tag add(Tag... tags) {
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

	@Override
	public String toString() {
		return content;
	}

	@Override
	public String toString(int indent) {
		return content;
	}
}
