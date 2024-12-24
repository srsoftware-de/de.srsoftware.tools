/* © SRSoftware 2024 */
package de.srsoftware.tools;


import static de.srsoftware.tools.Optionals.nullable;
import static java.util.Optional.empty;

import java.util.*;
import java.util.function.Predicate;

/**
 * This class represents an X(ht)ML tag
 * @author Stephan Richter, 2018-2024
 *
 */
public class Tag extends TreeMap<String, String> {
	private final List<Tag> children = new ArrayList<>();
	private Tag	        parent;
	private final String    type;

	/**
	 * Create a new tag of the specified type
	 * @param type the type of the tag
	 */
	public Tag(String type) {
		this.type = type;
	}

	/**
	 * Add children to the tag. The parent of the children will be set to this tag, too.
	 * If a child had another parent before, this relationship will be terminated.
	 * @param newChildren an aray of tags to be added.
	 * @return this tag
	 */
	public Tag add(Tag... newChildren) {
		return add(Arrays.asList(newChildren));
	}

	/**
	 * Add children to the tag. The parent of the children will be set to this tag, too.
	 * If a child had another parent before, this relationship will be terminated.
	 * @param newChildren an aray of tags to be added.
	 * @return this tag
	 */
	public Tag add(List<Tag> newChildren) {
		for (Tag child : newChildren) {
			if (child != null) {
				if (child.parent != null) child.parent.removeChild(child);
				child.parent = this;
				children.add(child);
			}
		}
		return this;
	}

	/**
	 * add this Tag to another Tag
	 * @param parent the tag to which this object is appended
	 * @return the parent tag
	 * @param <T> the type of the parent tag
	 */
	public <T extends Tag> T addTo(T parent) {
		parent.children().add(this);
		this.parent = this;
		return parent;
	}

	/**
	 * add an &lt;alt&gt; attribute with a given value to this tag
	 * @param txt the text to set for the alt attribute
	 * @return this tag
	 * @param <T> the type of this tag
	 */
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

	/**
	 * find all Tags, that satisfy a given filter
	 * @param filter a predicate that acts as filter
	 * @return the list of tags that satisfy the filter predicate
	 */
	public List<Tag> find(Predicate<Tag> filter) {
		List<Tag> hits = new ArrayList<>();
		if (filter.test(this)) hits.add(this);
		for (var child : children) hits.addAll(child.find(filter));
		return hits;
	}

	/**
	 * Extract this tag as text without explicitly listing its children.
	 * @return a flat representation of this tag
	 */
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

	/**
	 * get the aggregated string content of the children of this tag
	 * @param indent children will be indented by the given number of spaces
	 * @return the combined code of the children
	 */
	public Optional<String> inner(int indent) {
		if (children().isEmpty()) return empty();
		StringBuilder sb = new StringBuilder();
		children().forEach(child -> sb.append(child.toString(indent)));
		return Optional.of(sb.toString());
	}

	/**
	 * test, whether a tag is of a given type
	 * @param type the type to test against
	 * @return true, only if the type of this tag matches the given string
	 */
	public boolean is(String type) {
		return this.type != null && this.type.equalsIgnoreCase(type);
	}

	public static Tag of(String type) {
		return new Tag(type);
	}

	/**
	 * get the parent of this tag
	 * @return empty, if this tag has no parent or an optional containing the parent tag of this element.
	 */
	public Optional<Tag> parent() {
		return nullable(parent);
	}

	public <T extends Tag> T pos(int x, int y) {
		return attr("x", x).attr("y", y);
	}

	/**
	 * Removes a certain child tag
	 * @param child the tag to be removed from the list of children
	 * @return this tag
	 * @param <T> the type of this tag
	 */
	@SuppressWarnings("unchecked")
	public <T extends Tag> T removeChild(Tag child) {
		children.remove(child);
		child.parent = null;
		return (T)this;
	}

	/**
	 * Remove a tag from its parent tag.
	 * If the tag has no parent, noting changes.
	 * @return the parent tag, if it was present
	 */
	public Optional<Tag> remove() {
		return nullable(parent).map(p -> p.removeChild(this));
	}

	public <T extends Tag> T size(int width, int height) {
		return attr("width", width).attr("height", height);
	}

	public <T extends Tag> T style(String style) {
		return attr("style", style);
	}

	/**
	 * add a title attribute to this tag
	 * @param title the title to set
	 * @return this tag
	 * @param <T> the type of this tag
	 */
	public <T extends Tag> T title(String title) {
		return attr("title", title);
	}

	/**
	 * Return the text content of this tag and its descendants, i.e. the code without the tags.
	 * @return the combined text
	 */
	public String strip() {
		var sb = new StringBuilder();
		children.stream().map(Tag::strip).forEach(sb::append);
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
