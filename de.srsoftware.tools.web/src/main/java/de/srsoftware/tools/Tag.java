/* © SRSoftware 2024 */
package de.srsoftware.tools;


import java.util.*;

/**
 * @author Stephan Richter, 2018-2024
 *
 */
public class Tag extends TreeMap<String, String> {
	private final List<Tag> children = new ArrayList<>();
	private final String    type;

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
		attr("class", String.join(" ", classes));
		return (T)this;
	}

	@SuppressWarnings("unchecked")
	public <T extends Tag> T clazz(String... classes) {
		attr("class", String.join(" ", classes));
		return (T)this;
	}

	@SuppressWarnings("unchecked")
	public <T extends Tag> T content(String content) {
		add(new Text(content));
		return (T)this;
	}

	public <T extends Tag> T id(String id) {
		return attr("id", id);
	}

	public boolean is(String type) {
		return this.type != null && this.type.equalsIgnoreCase(type);
	}

	public static Tag of(String type) {
		return new Tag(type);
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

	public String flat() {
		StringBuilder sb = new StringBuilder("<" + type);
		for (var entry : entrySet()) {
			sb.append(" ").append(entry.getKey());
			var value = entry.getValue();
			if (value != null) sb.append("=\"").append(entry.getValue()).append("\"");
			break;
		}
		if (children.isEmpty()) {
			sb.append(" />");
		} else {
			sb.append(">…</").append(type).append(">");
		}

		return sb.toString();
	}

	@Override
	public String toString() {
		StringBuilder sb    = new StringBuilder();
		boolean       empty = type == null || type.isBlank();
		if (!empty) {
			sb.append("<").append(type);
			for (var entry : entrySet()) {
				sb.append(" ").append(entry.getKey());
				var value = entry.getValue();
				if (value != null) sb.append("=\"").append(entry.getValue()).append("\"");
			}
		}
		if (children.isEmpty()) {
			sb.append(" />");
		} else {
			if (!empty) sb.append(">");
			for (Tag child : children) sb.append(child.toString());
			if (!empty) sb.append("</").append(type).append(">");
		}

		return sb.toString();
	}

	protected void indent(StringBuilder sb, int indent, int currentIndentation) {
		boolean empty = type == null || type.isBlank();
		if (!empty) {
			sb.append(" ".repeat(currentIndentation)).append("<").append(type);
			for (var entry : entrySet()) {
				sb.append(" ").append(entry.getKey());
				var value = entry.getValue();
				if (value != null) sb.append("=\"").append(entry.getValue()).append("\"");
			}
		}
		if (children.isEmpty()) {
			sb.append(" />\n");
		} else {
			if (!empty) {
				sb.append(">").append("\n");
			}
			for (Tag child : children) child.indent(sb, indent, currentIndentation + indent);
			if (!empty) sb.append(" ".repeat(currentIndentation));
			if (!empty) sb.append("</").append(type).append(">\n");
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
