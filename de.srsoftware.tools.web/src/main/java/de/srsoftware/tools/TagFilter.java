/* © SRSoftware 2024 */
package de.srsoftware.tools;

import static de.srsoftware.tools.Tag.*;

import java.util.function.Predicate;

/**
 * Filters for tags
 */
public class TagFilter {

	/** anchor filter */    public static final Predicate<Tag> IS_ANCHOR    = tag -> ANCHOR.equals(tag.type());
	/** body filter */      public static final Predicate<Tag> IS_BODY      = tag -> BODY.equals(tag.type());
	/** div filter */       public static final Predicate<Tag> IS_DIV       = tag -> DIV.equals(tag.type());
	/** image filter */     public static final Predicate<Tag> IS_IMAGE     = tag -> IMG.equals(tag.type());
	/** paragraph filter */ public static final Predicate<Tag> IS_PARAGRAPH = tag -> PARAGRAPH.equals(tag.type());
	/** script filter */    public static final Predicate<Tag> IS_SCRIPT    = tag -> SCRIPT.equals(tag.type());
	/** span filter */      public static final Predicate<Tag> IS_SPAN      = tag -> SPAN.equals(tag.type());

	private TagFilter() {
		// discourage instantiation
	}

	/**
	 * Tests whether a tag as an attribute with the given name whose content contains the given value
	 * @param key tha attribute name
	 * @param value the value to look for
	 * @return a predicate that returns true for a tag that fulfills the aforementioned condition
	 */
	public static Predicate<Tag> attributeContains(String key, String value) {
		if (key == null) return t -> false;
		if (value == null) return t -> false;
		return tag -> {
			var val = tag.get(key);
			return val != null && val.contains(value);
		};
	}

	/**
	 * Returns a predicate that tests, whether a tag has a certain attribute whose value ends with the passed value
	 * @param key the name of the attribute to look for
	 * @param value the value to test against
	 * @return the predicate that returns true if the described condition is fulfilled
	 */
	public static Predicate<Tag> attributeEndsWith(String key, String value) {
		if (key == null) return t -> false;
		if (value == null) return t -> false;
		return tag -> {
			var val = tag.get(key);
			return val != null && val.endsWith(value);
		};
	}



	/**
	 * Tests whether a tag as an attribute with the given name whose content matches the given value
	 * @param key tha attribute name
	 * @param value the value to look for
	 * @return a predicate that returns true for a tag that fulfills the aforementioned condition
	 */
	public static Predicate<Tag> attributeEquals(String key, String value) {
		if (key == null) return t -> false;
		if (value == null) return t -> false;
		return tag -> value.equals(tag.get(key));
	}



	/**
	 * Tests, whether value equals at least on entry in a multi-valued attribute.
	 * Examlple: &lt;div class="top left border" &gt; will be matched by key="class" and value="left".
	 * @param key the attribute name
	 * @param value the value to look for
	 * @return a predicate that returns true for a tag that fulfills the aforementioned condition
	 */
	public static Predicate<Tag> attributeHas(String key, String value) {
		if (key == null) return t -> false;
		if (value == null) return t -> false;
		return tag -> {
			var val = tag.get(key);
			if (val == null) return false;
			var tokens = val.split(" ");
			for (var token : tokens) {
				if (token.equals(value)) return true;
			}
			return false;
		};
	}

	/**
	 * Returns a predicate that tests, whether a tag has a certain attribute whose value starts with the passed value
	 * @param key the name of the attribute to look for
	 * @param value the value to test against
	 * @return the predicate that returns true if the described condition is fulfilled
	 */
	public static Predicate<Tag> attributeStartsWith(String key, String value) {
		if (key == null) return t -> false;
		if (value == null) return t -> false;
		return tag -> {
			var val = tag.get(key);
			return val != null && val.startsWith(value);
		};
	}

	/**
	 * Tests, whether a given tag has a type that matches the passed string
	 * @param type the type of the tag we are looking for
	 * @return a predicate that returns true, if the given tag is of the provided type
	 */
	public static Predicate<Tag> ofType(String type) {
		if (type == null) return t -> false;
		return tag -> type.equals(tag.type());
	}

	/**
	 * Tests, whether a given tag has an attribute of the passed name
	 * @param attributeName the attribute name to look for
	 * @return a predicate that returns true only if the tag has an attribute of the given name
	 */
	public static Predicate<Tag> withAttribute(String attributeName) {
		return tag -> tag.get(attributeName) != null;
	}

}
