/* © SRSoftware 2024 */
package de.srsoftware.tools;

import static java.lang.Character.*;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Parser to create Document Object Model from InputStream
 */
public class XMLParser {
	private static final System.Logger LOG = System.getLogger(XMLParser.class.getSimpleName());
	private static final Pattern CHARSET = Pattern.compile("<meta .*charset=\\W?([-\\w\\d]+)");

	private XMLParser() {
		// discourage intantiation
	}

	/**
	 * Parse the inputstream, try to load DOM as Tag
	 * @param input the input stream to read from
	 * @return Payload&lt;Tag&gt; if stream is read successfull and contains xml, Error otherwise
	 */
	public static Result<Tag> parse(InputStream input) {
		try {
			var result = parse(new PushbackReader(new InputStreamReader(input)));
			return result.isEmpty() ? Error.error("Failed to parse content") : Payload.of(result.getFirst());
		} catch (Exception e) {
			return Error.error(e,"Failed to parse content of stream");
		}
	}

	private static List<Tag> parse(PushbackReader input) throws IOException {
		var tags = new ArrayList<Tag>();
		while (true) {
			var prefix = readUntil(input, "<", true,false);
			if (!prefix.isBlank()) tags.add(new Text(prefix));
			int c = read(input);
			if (c <= 0) break;
			if (c == '<') {
				var token  = readUntil(input, ">", false,true);
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
		var parts = token.split("\\s", 2);
		var type  = parts[0];
		Tag tag   = closing ? ClosedTag.of(type) : OpeningTag.of(type);
		if (parts.length > 1) fetchAttributes(tag, parts[1]);
		return tag;
	}

	private static void fetchAttributes(Tag tag, String data) throws IOException {
		PushbackReader input = new PushbackReader(new StringReader(data));
		while (input.ready()) {
			skipWhitespace(input);
			String token = readUntil(input, " \t", true,true).trim();
			if (token.isEmpty()) return;
			var parts = token.split("=", 2);
			var key   = parts[0];
			var val   = parts.length > 1 ? parts[1] : null;
			if (val != null) {
				if (val.startsWith("\"") && val.endsWith("\"")) {
					val = val.substring(1, val.length() - 1);
				} else if (val.startsWith("'") && val.endsWith("'")) val = val.substring(1, val.length() - 1);
			}
			tag.attr(key, val);
		}
	}

	/**
	 * Load input stream into local buffer, create input stream from local buffer.
	 * May help with some websites.
	 * @param input the transient input stream to read from
	 * @return input stream created from in-memory buffer
	 * @throws IOException if input cannot be read
	 */
	public static InputStream preload(InputStream input) throws IOException {
		var bos = new ByteArrayOutputStream();
		input.transferTo(bos);
		input.close();
		var content = bos.toString(UTF_8);
		var matcher = CHARSET.matcher(content);
		if (matcher.find()){
			var encoding = matcher.group(1);
			if (!"utf-8".equalsIgnoreCase(encoding)) LOG.log(System.Logger.Level.DEBUG,"Reading with charset {0}…",encoding);
			content = bos.toString(encoding);
		}
		return new ByteArrayInputStream(content.getBytes(UTF_8));
	}

	private static int read(PushbackReader input) throws IOException {
		if (!input.ready()) {
			return 0;
		}
		return input.read();
	}

	private static String readUntil(PushbackReader input, String delimiters, boolean pushBack, boolean matchQuotes) throws IOException {
		var token = new StringBuilder();
		int c     = read(input);
		while (unlimited(c, delimiters)) {
			switch (c) {
				case '"':
					if (matchQuotes) {
						token.append('"').append(readUntil(input, "\"", false,true)).append('"');
						break;
					}
				case '\'':
					if (matchQuotes) {
						token.append("'").append(readUntil(input, "'", false,true)).append("'");
						break;
					}
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
