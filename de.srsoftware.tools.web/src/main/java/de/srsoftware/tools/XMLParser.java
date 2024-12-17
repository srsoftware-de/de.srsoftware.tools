/* © SRSoftware 2024 */
package de.srsoftware.tools;

import static java.lang.Character.*;

import java.io.*;

public class XMLParser {
	public static Result<Tag> parse(InputStream input) throws IOException, ParseException {
		var result = parse(new PushbackReader(new InputStreamReader(input)));
		if (result instanceof OpeningTag openingTag) return openingTag;
		return Error.of("Failed!");
	}

	private static Result<?> parse(PushbackReader input) throws IOException {
		skipWhitespace(input);
		int c = read(input);
		while (c == '<') {
			String token  = readUntil(input, ">", false);
			var    result = convert(token);
			if (result instanceof ClosedTag closedTag) {
				return closedTag;
			}
			if (result instanceof ClosingTag closing) {
				return closing;
			}
			if (result instanceof OpeningTag opening) {
				var       tag	= opening.get();
				Result<?> child = null;
				do {
					child = parse(input);
					if (child instanceof OpeningTag ot) {
						//System.out.printf("adding to %s: %s\n",tag.flat(), ot.get().flat());
						tag.add(ot.get());
						continue;
					}
					if (child instanceof ClosedTag ct) {
						//System.out.printf("adding to %s: %s\n",tag.flat(), ct.get().flat());
						tag.add(ct.get());
						continue;
					}
					if (child instanceof ClosingTag closing) {
						if (closing.matches(tag)) return opening;
						//System.out.println("Closing: "+closing.token());
						//System.out.println("Parent: "+tag.flat());
					}
					if (child instanceof Content content) {
						//System.out.printf("setting content of %s: %s\n", tag,content.get());
						tag.content(content.get());
						continue;
					}
				} while (child != null);
				return opening;
			}
			if (result instanceof Comment comment) {
				skipWhitespace(input);
				c = read(input);
			}
		}
		if (c > 0) input.unread(c);
		String token = readUntil(input, "<", true);
		return token.isBlank() ? null : Content.of(token);
	}

	private static Result<?> convert(String token) throws IOException {
		if (token.startsWith("/")) return ClosingTag.of(token.substring(1));
		if (token.startsWith("!")) return Comment.of(token);
		return toTag(token);
	}

	private static Result<?> toTag(String token) throws IOException {
		token	= token.trim();
		boolean closing = token.endsWith("/");
		if (closing) token = token.substring(0, token.length() - 2).trim();
		if (token.isBlank()) return Error.of("encountered empty tag!");
		var parts = token.split(" ", 2);
		var type  = parts[0];
		var tag   = new Tag(type);
		if (parts.length > 1) fetchAttributes(tag, parts[1]);
		return closing ? ClosedTag.of(tag) : OpeningTag.of(tag);
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

	private static boolean unlimited(int c, String delimiter) {
		return c > 0 && delimiter.indexOf(c) < 0;
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

	private static void skipWhitespace(PushbackReader input) throws IOException {
		int c = read(input);
		while (c > 0 && isWhitespace(c)) c = read(input);
		if (c > 0) input.unread(c);
	}
}
