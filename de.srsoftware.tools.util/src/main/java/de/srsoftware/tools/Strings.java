/* © SRSoftware 2025 */
package de.srsoftware.tools;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Base64;
import java.util.UUID;

/**
 * Utilities for String handling
 */
public class Strings {
	private Strings() {
	}

	/**
	 * create a base-64 representation of the byte array
	 * @param bytes the byte array to transform
	 * @return the base 64 encoded representation of the bytes
	 */
	public static String base64(byte[] bytes) {
		return Base64.getEncoder().encodeToString(bytes);
	}

	/**
	 * convert a string to camel case: every character following a space is converted to uppercase, spaces are removed
	 * @param text the text to be converted
	 * @return the camelcase version of the provided text
	 */
	public static String camelCase(String text) {
		if (text == null) return null;
		var sb    = new StringBuilder();
		var upper = false;
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c == ' '||c == '_') {
				upper = true;
			} else {
				sb.append(upper ? Character.toUpperCase(c) : c);
				upper = false;
			}
		}
		return sb.toString();
	}

	private static String entity(int idx) {
		if (idx < 10) return "&#00"+idx+";";
		if (idx < 100) return "&#0"+idx+";";
		return "&#00"+idx+";";
	}

	/**
	 * replace some special characters that clash with xml markup
	 * @param text original text to be processed
	 * @return text with replacements
	 */
	public static String escapeHtmlEntities(String text) {
		final StringBuilder result = new StringBuilder();
		final StringCharacterIterator iterator = new StringCharacterIterator(text);
		char character = iterator.current();
		while (character != CharacterIterator.DONE) {
			var appendix = switch (character){
				case '<' -> "&lt;";
				case '>' -> "&gt;";
				case '&' -> "&amp;";
				case '"' -> "&quot;";
				case '\t' -> entity(9);
				default -> character;
			};
			result.append(appendix);
			character = iterator.next();
		}
		return result.toString();
	}

	/**
	 * stuff the placeholders in the text with the string representations of the objects, don't indent
	 * @param indent add whitespace in front of the line
	 * @param text a text with {} placeholders
	 * @param fills the fills to apply
	 * @return the text with placeholders replaced
	 */
	public static String fill(final int indent, final String text, final Object... fills) {
		final StringBuilder sb = new StringBuilder(" ".repeat(indent));

		if (fills == null || fills.length < 1) return sb.append(text).toString();

		final var parts = text.split("\\{\\}", fills.length + 1);

		for (int i = 0; i < parts.length - 1; i++) {
			sb.append(parts[i]);
			sb.append(fills[i]);
		}

		sb.append(parts[parts.length - 1]);
		return sb.toString();
	}

	/**
	 * stuff the placeholders in the text with the string representations of the objects, don't indent
	 * @param text a text with {} placeholders
	 * @param fills the fills to apply
	 * @return the text with placeholders replaced
	 */
	public static String fill(final String text, final Object... fills) {
		return fill(0, text, fills);
	}


	/**
	 * get a hexadecimal representation of the byte array
	 * @param bytes a byte array to convert to a hex string
	 * @return a string containing a hexadecimal representation of the given byte array
	 */
	public static String hex(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for (byte b : bytes) sb.append(String.format("%02x", b));
		return sb.toString();
	}

	/**
	 * convert a string to nake case: every uppercase character is converted to lowercase and prefixed by an underscore. Spaces are also converted to underscores.
	 * @param text the text to be converted
	 * @return the snake case version of the provided text
	 */
	public static String snakeCase(String text){
		var sb = new StringBuilder();
		for (int i=0; i<text.length();i++){
			char c = text.charAt(i);
			if (c == ' ') c = '_';
			if (Character.isUpperCase(c) && i>0 && sb.charAt(sb.length()-1)!='_') sb.append("_");
			if (c != '_' || sb.charAt(sb.length()-1)!=c ) sb.append(Character.toLowerCase(c));
		}
		return sb.toString();
	}

	/**
	 * shorthand to create a new UUID
	 * @return a random uuid
	 */
	public static String uuid() {
		return UUID.randomUUID().toString();
	}
}
