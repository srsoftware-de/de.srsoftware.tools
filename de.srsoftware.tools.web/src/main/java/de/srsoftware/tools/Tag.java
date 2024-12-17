/* © SRSoftware 2024 */
package de.srsoftware.tools;

import java.util.*;

import static de.srsoftware.tools.Optionals.absentIfBlank;

/**
 * @author Stephan Richter, 2018-2024
 *
 */
public class Tag extends HashMap<String, String> {
	private final List<Tag> children = new ArrayList<>();
	private final String    type;
	private String	        content = null;

	public Tag(String type) {
		this.type = type;
	}

	public Tag add(Tag... tags) {
		for (Tag tag : tags) {
			if (tag != null) children().add(tag);
		}
		return this;
	}

	public <T extends Tag> T addTo(T tag) {
		tag.children().add(this);
		return tag;
	}

	public <T extends Tag> T alt(String txt) {
		return attr("alt", txt);
	}

	@SuppressWarnings("unchecked")
	public <T extends Tag> T attr(String key, String val) {
		put(key, val);
		return (T)this;
	}

	public <T extends Tag> T attr(String key, int i) {
		return attr(key, "" + i);
	}

	public List<Tag> children() {
		return children;
	}

	@SuppressWarnings("unchecked")
	public <T extends Tag> T clazz(Collection<String> classes) {
		put("class", String.join(" ", classes));
		return (T)this;
	}

	@SuppressWarnings("unchecked")
	public <T extends Tag> T clazz(String... classes) {
		put("class", String.join(" ", classes));
		return (T)this;
	}

	@SuppressWarnings("unchecked")
	public <T extends Tag> T content(String content) {
		this.content = content == null ? null : content.trim();
		return (T)this;
	}

	public <T extends Tag> T id(String id) {
		return attr("id", id);
	}

	public boolean is(String type) {
		return this.type != null && this.type.equalsIgnoreCase(type);
	}

	public <T extends Tag> T pos(int x, int y) {
		return attr("x", x).attr("y", y);
	}

	public <T extends Tag> T size(int width, int height) {
		return attr("width", width).attr("height", height);
	}

	public <T extends Tag> T style(String style) {
		return attr("style", style);
	}

	public <T extends Tag> T title(String t) {
		return attr("title", t);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("<" + type);
		for (Entry<String, String> entry : entrySet()) sb.append(" ").append(entry.getKey()).append("=\"").append(entry.getValue()).append("\"");
		if (children.isEmpty() && absentIfBlank(content).isEmpty()) {
			sb.append(" />");
		} else {
			sb.append(">");
			for (Tag child : children) sb.append(child.toString());
			absentIfBlank(content).ifPresent(sb::append);
			sb.append("</").append(type).append(">");
		}

		return sb.toString();
	}

	protected void indent(StringBuilder sb, int indent, int currentIndentation) {
		sb.append(" ".repeat(currentIndentation)).append("<").append(type);
		for (Entry<String, String> entry : entrySet()) sb.append(" ").append(entry.getKey()).append("=\"").append(entry.getValue()).append("\"");
		if (children.isEmpty()) {
			if (content == null) {
				sb.append(" />\n");
			} else {
				sb.append(">").append(content).append("</").append(type).append(">\n");
			}
		} else {
			sb.append(">\n");
			for (Tag child : children) {
				child.indent(sb, indent, currentIndentation + indent);
			}
			sb.append(" ".repeat(currentIndentation)).append("</").append(type).append(">\n");
		}
	}

	public String toString(int indent) {
		StringBuilder sb = new StringBuilder();
		indent(sb, indent, 0);
		return sb.toString();
	}

	public String type() {
		return type;
	}
}
