/* © SRSoftware 2024 */
package de.srsoftware.tools;


import java.util.*;

import static de.srsoftware.tools.Optionals.nullable;
import static java.util.Optional.empty;

/**
 * @author Stephan Richter, 2018-2024
 *
 */
public class Tag extends TreeMap<String, String> {
	private final List<Tag> children = new ArrayList<>();
	private Tag parent;
	private final String    type;

	public Tag(String type) {
		this.type = type;
	}

	public Tag add(Tag... newChildren) {
		for (Tag child : newChildren) {
			if (child != null) {
				if (child.parent != null) child.parent.removeChild(child);
				child.parent = this;
				children.add(child);
			}
		}
		return this;
	}

	public <T extends Tag> T addTo(T parent) {
		parent.children().add(this);
		this.parent = this;
		return parent;
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

	public List<Tag> find(String attr){
		List<Tag> hits = new ArrayList<>();
		nullable(get(attr)).ifPresent(o -> hits.add(this));
		for (var child : children) hits.addAll(child.find(attr));
		return hits;
	}

	public List<Tag> find(String attr, String value){
		List<Tag> hits = new ArrayList<>();
		nullable(get(attr)).filter(value::equals).ifPresent(o -> hits.add(this));
		for (var child : children) hits.addAll(child.find(attr,value));
		return hits;
	}

	public <T extends Tag> T id(String id) {
		return attr("id", id);
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

	public boolean is(String type) {
		return this.type != null && this.type.equalsIgnoreCase(type);
	}

	public static Tag of(String type) {
		return new Tag(type);
	}

	public <T extends Tag> T pos(int x, int y) {
		return attr("x", x).attr("y", y);
	}

	private void removeChild(Tag child) {
		children.remove(child);
		child.parent = null;
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



	public String toString(int indent) {
		StringBuilder sb = new StringBuilder();
		indent(sb, indent, 0);
		return sb.toString();
	}

	public String type() {
		return type;
	}
}
