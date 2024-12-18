/* © SRSoftware 2024 */
package de.srsoftware.tools;

import static java.lang.Character.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class XMLParser {
	public static Result<Tag> parse(InputStream input) throws IOException, ParseException {
		var result = parse(new PushbackReader(new InputStreamReader(input)));
		return result == null || result.isEmpty() ? Error.of("nope!") : Payload.of(result.removeFirst());
	}

	private static List<Tag> parse(PushbackReader input) throws IOException {
		var tags = new ArrayList<Tag>();
		while (true) {
			var prefix = readUntil(input, "<", true);
			if (!prefix.isBlank()) {
				System.out.println("encountered some text: " + prefix);
				tags.add(new Text(prefix));
			}
			int c = read(input);
			if (c <= 0) break;
			if (c == '<') {
				var token  = readUntil(input, ">", false);
				var result = convert(token);
				if (result instanceof OpeningTag) {
					var children = parse(input);
					if (!children.isEmpty() && children.getLast() instanceof ClosingTag closingTag) {
						if (result.is(closingTag.type())) {
							children.removeLast();
						} else {
							children.removeLast();
							children.forEach(result::add);
							tags.add(result);
							break;
						}
					}
					children.forEach(result::add);
					tags.add(result);
				}
				if (result instanceof ClosedTag) {
					tags.add(result);
				}
				if (result instanceof ClosingTag) {
					tags.add(result);
					break;
				}
			}
		}
		return tags;
	}

	private static Tag convert(String token) throws IOException {
		if (token.startsWith("/")) return ClosingTag.of(token.substring(1));
		if (token.startsWith("!")) return Comment.of(token);
		return toTag(token);
	}

	private static Tag toTag(String token) throws IOException {
		token	= token.trim();
		boolean closing = token.endsWith("/");
		if (closing) token = token.substring(0, token.length() - 2).trim();
		if (token.isBlank()) return null;
		var parts = token.split(" ", 2);
		var type  = parts[0];
		Tag tag   = closing ? ClosedTag.of(type) : OpeningTag.of(type);
		if (parts.length > 1) fetchAttributes(tag, parts[1]);
		return tag;
	}

	private static void fetchAttributes(Tag tag, String data) throws IOException {
		PushbackReader input = new PushbackReader(new StringReader(data));
		while (input.ready()) {
			skipWhitespace(input);
			String token = readUntil(input, " ", true).trim();
			if (token.isEmpty()) return;
			var parts = token.split("=", 2);
			var key   = parts[0];
			var val   = parts.length > 1 ? parts[1] : null;
			if (val != null && val.startsWith("\"") && val.endsWith("\"")) val = val.substring(1, val.length() - 1);
			tag.attr(key, val);
		}
	}

	/**
	 * Load input stream into local buffer, create input stream from local buffer.
	 * May help with some websites.
	 * @param input the transient input stream to read from
	 * @return input stream created from in-memory buffer
	 */
	public static InputStream preload(InputStream input) throws IOException {
		var bos = new ByteArrayOutputStream();
		input.transferTo(bos);
		input.close();
		return new ByteArrayInputStream(bos.toByteArray());
	}

	private static int read(PushbackReader input) throws IOException {
		if (!input.ready()) {
			return 0;
		}
		var c = input.read();
		// System.out.print(cx);
		// System.out.flush();
		return c;
	}

	private static String readUntil(PushbackReader input, String delimiters, boolean pushBack) throws IOException {
		var token = new StringBuilder();
		int c     = read(input);
		while (unlimited(c, delimiters)) {
			switch (c) {
				case '"':
					token.append('"').append(readUntil(input, "\"", false)).append('"');
					break;
				case '\'':
					token.append("'").append(readUntil(input, "'", false)).append("'");
					break;
				default:
					token.append((char)c);
			}
			c = read(input);
		}
		if (pushBack && c > 0) input.unread(c);
		return token.toString();
	}

	private static void skipWhitespace(PushbackReader input) throws IOException {
		int c = read(input);
		while (c > 0 && isWhitespace(c)) c = read(input);
		if (c > 0) input.unread(c);
	}

	private static boolean unlimited(int c, String delimiter) {
		return c > 0 && delimiter.indexOf(c) < 0;
	}
}
