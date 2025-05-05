/* © SRSoftware 2025 */
package de.srsoftware.tools;


import static de.srsoftware.tools.NameConversion.NO_CONVERSION;
import static de.srsoftware.tools.Optionals.nullable;
import static de.srsoftware.tools.Strings.camelCase;
import static de.srsoftware.tools.Strings.snakeCase;
import static java.util.Optional.empty;

import java.util.*;
import java.util.function.Predicate;

/**
 * This class represents an X(ht)ML tag
 * @author Stephan Richter, 2018-2024
 *
 */
public class Tag extends TreeMap<String, String> {
	/** anchor constant: a */    public static final String ANCHOR    = "a";
	/** body constant */         public static final String BODY      = "body";
	/** class constant */        public static final String CLASS     = "class";
	/** div constant */          public static final String DIV       = "div";
	/** href constant */         public static final String HREF      ="href";
	/** id constant */           public static final String ID        = "id";
	/** img constant */          public static final String IMG       = "img";
	/** paragraph constant: p */ public static final String PARAGRAPH = "p";
	/** script constant */       public static final String SCRIPT    = "script";
	/** span constant */         public static final String SPAN      = "span";
	/** style constant */        public static final String STYLE     = "style";
	/** title constant */        public static final String TITLE     = "title";

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
	 * Create a new tag. The tag`s class will match the name of the Tag class converted as specified.
	 * @param nameConversion specifies, how to convert the class name
	 */
	public Tag(NameConversion nameConversion){
		super();
		var name = getClass().getSimpleName();
		type = switch (nameConversion){
			case CAMEL_CASE -> camelCase(name);
			case LOWER_CASE -> name.toLowerCase();
			case NO_CONVERSION -> name;
			case SNAKE_CASE -> snakeCase(name);
			case UPPER_CASE -> name.toUpperCase();
		};
	}

	/**
	 * Add children to the tag. The parent of the children will be set to this tag, too.
	 * If a child had another parent before, this relationship will be terminated.
	 * @param newChildren an aray of tags to be added.
	 * @return this tag
	 */
	public Tag add(Tag... newChildren) {
		return addAll(Arrays.asList(newChildren));
	}

	/**
	 * Add children to the tag. The parent of the children will be set to this tag, too.
	 * If a child had another parent before, this relationship will be terminated.
	 * @param newChildren an aray of tags to be added.
	 * @return this tag
	 */
	public Tag addAll(Iterable<Tag> newChildren) {
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

	/**
	 * add an &lt;alt&gt; attribute with a given value to this tag
 	 * @param name the attribute name
	 * @param val the text to set for the alt attribute
	 * @return this tag
	 * @param <T> the type of this tag
	 */
	@SuppressWarnings("unchecked")
	public <T extends Tag> T attr(String name, String val) {
		put(name, val);
		return (T)this;
	}

	/**
	 * set an attribute of this tag
	 * @param name the attribute name
	 * @param val the value
	 * @return this tag
	 * @param <T> the type of this tag
	 */
	public <T extends Tag> T attr(String name, int val) {
		return attr(name, "" + val);
	}

	/**
	 * return the list of child tags
	 * @return the children of this tag
	 */
	public List<Tag> children() {
		return children;
	}

	/**
	 * adds several classes to a tag
	 * @param classes the collection of classes to add
	 * @return the tag, now with more classes
	 * @param <T> the type of the tag
	 */
	@SuppressWarnings("unchecked")
	public <T extends Tag> T clazz(Collection<String> classes) {
		attr(CLASS, String.join(" ", classes));
		return (T)this;
	}

	/**
	 * adds classes to this tag
	 * @param classes the classes to add
	 * @return this tag object
	 * @param <T> the type of this tag object
	 */
	@SuppressWarnings("unchecked")
	public <T extends Tag> T clazz(String... classes) {
		attr(CLASS, String.join(" ", classes));
		return (T)this;
	}

	/**
	 * add a text tag with the given content as child
	 * @param content the content of the tag
	 * @return the tag itself
	 * @param <T> the type of the tag
	 */
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
		if (filter == null) return List.of();
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

	/**
	 * set the id attribute of this tag
	 * @param id the value to use
	 * @return the altered tag
	 * @param <T> the type of the tag
	 */
	public <T extends Tag> T id(String id) {
		return attr(ID, id);
	}

	/**
	 * create a string representation of this tag (and its children) including the tags children, append to the string builder
	 * @param sb the string builder to append to
	 * @param indent the indentation for the sub-ordered levels
	 * @param currentIndentation the indentation of this level
	 */
	protected void indent(StringBuilder sb, int indent, int currentIndentation) {
		boolean textType = type == null || type.isBlank();
		if (!textType) {
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
			if (!textType) sb.append(">").append("\n");
			for (Tag child : children) child.indent(sb, indent, currentIndentation + indent);
			if (!textType) sb.append(" ".repeat(currentIndentation));
			if (!textType) sb.append("</").append(type).append(">\n");
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

	/**
	 * create a tag of a given type
	 * @param type the type to use as tag type
	 * @return the created tag
	 */
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

	/**
	 * add x and y attributes to the tag
	 * @param x the value for the x attribute
	 * @param y the value for the y attribute
	 * @return this tag
	 * @param <T> the type of this tag
	 */
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

	/**
	 * set width and size attribute of this tag
	 * @param width the width
	 * @param height the height
	 * @return the altered tag
	 * @param <T> the type of the returned tag
	 */
	public <T extends Tag> T size(int width, int height) {
		return attr("width", width).attr("height", height);
	}

	/**
	 * set style attribute
	 * @param style the content to set for the style attribute
	 * @return the altered tag
	 * @param <T> the type of the tag
	 */
	public <T extends Tag> T style(String style) {
		return attr(STYLE, style);
	}

	/**
	 * add a title attribute to this tag
	 * @param title the title to set
	 * @return this tag
	 * @param <T> the type of this tag
	 */
	public <T extends Tag> T title(String title) {
		return attr(TITLE, title);
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
		boolean       textTeype = type == null || type.isBlank();
		if (!textTeype) {
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
			if (!textTeype) sb.append(">");
			for (Tag child : children) sb.append(child.toString());
			if (!textTeype) sb.append("</").append(type).append(">");
		}

		return sb.toString();
	}


	/**
	 * export string with indented tags
	 * @param indent the number of spaces used for indentation
	 * @return the exported xml
	 */
	public String toString(int indent) {
		StringBuilder sb = new StringBuilder();
		indent(sb, indent, 0);
		return sb.toString();
	}

	/**
	 * return the type of this tag
	 * @return the tag type
	 */
	public String type() {
		return type;
	}
}
