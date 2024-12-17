/* © SRSoftware 2024 */
package de.srsoftware.tools;

import static de.srsoftware.tools.NotImplemented.notImplemented;
import static de.srsoftware.tools.Optionals.absentIfBlank;
import static java.lang.Character.*;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;

public class XMLParser {
	public static Result<Tag> parse(InputStream input) throws IOException, ParseException {
		return parseTag(new PushbackReader(new InputStreamReader(input)));
	}

	private static Result<Tag> parseTag(PushbackReader input) throws IOException, ParseException {
		skipWhitespace(input);
		int c = read(input);
		if (c == '<') {
			c = read(input);
			while (c == '!'){
				input.unread(c);
				ignoreComment(input);
				skipWhitespace(input);
				c = read(input);
				if (c != '<') throw new UnexpectedCharacter(c,'<');
				c = read(input);
			}
			input.unread(c);
			skipWhitespace(input);
			c	= read(input);
			boolean closing = (c == '/');
			if (closing) {
				skipWhitespace(input);
			} else
				input.unread(c);

			var type = readType(input);
			if (type.isPresent()) {
				var tag = new Tag(type.get());
				readAttributes(input).forEach(tag::attr);
				c = read(input);
				if (c == '/') {
					skipWhitespace(input);
					c = read(input);
					if (c != '>') throw new UnexpectedCharacter(c, '>');
					System.out.println("returning "+tag);
					return TagPayload.of(tag);
				}
				if (c == '>') {
					if (closing){
						System.out.println("closing "+tag);
						return Closing.of(tag);
					}
					var inner = readInner(input);
					if (inner instanceof Content content) {
						System.out.println("Content: "+content.content());
						tag.content(content.content());
						inner = readInner(input);
					}
					if (inner instanceof Closing closingTag) {
						System.out.println("returning "+tag);
						return TagPayload.of(tag);
					}
					while (inner instanceof TagPayload innerTag) {
						tag.add(innerTag.get());
						inner = readInner(input);
					}
					if (inner instanceof Closing closingTag) {
						System.out.println("returning "+tag);
						return TagPayload.of(tag);
					}
					if (inner instanceof Content content) {
						System.out.println("Content: "+content.content());
						return TagPayload.of(tag.content(content.content()));
					}
					throw notImplemented("handling of nested content");
				}
			} else {
				StringWriter sw = new StringWriter();
				input.transferTo(sw);
				throw new RuntimeException("encountered tag without type: "+sw);
			}
		}
		throw notImplemented(XMLParser.class, "parseTag(…)");
	}

	private static void ignoreComment(PushbackReader input) throws IOException {
		int c = read(input);
		while (c != '>'){
			c = read(input);
			if (c == '"') readDoubleQuotedString(input);
			if (c == '\'') readSingleQuotedString(input);
		}
	}

	private static int peek(PushbackReader input) throws IOException {
		int c = read(input);
		input.unread(c);
		return c;
	}

	private static int read(PushbackReader input) throws IOException {
		if (!input.ready()) {
			return 0;
		}
		int c = input.read();
		char cx = (char)c;
		//System.out.print(cx);
		//System.out.flush();
		return c;
	}

	private static void skipWhitespace(PushbackReader input) throws IOException {
		int c = read(input);
		while (c>0 && isWhitespace(c)) c = read(input);
		input.unread(c);
	}

	private static Map<String, String> readAttributes(PushbackReader input) throws IOException, UnexpectedCharacter {
		var attributes = new HashMap<String,String>();
		while (true) {
			skipWhitespace(input);
			int c = peek(input);
			if (c == '>' || c == '/') {
				return attributes;
			}
			var name = readAttributeName(input);
			var value = readAttributeValue(input);
			attributes.put(name,value);
		}
	}

	private static String readAttributeValue(PushbackReader input) throws IOException, UnexpectedCharacter {
		var c = peek(input);
		if (c == '"') return readDoubleQuotedString(input);
		if (c == '\'') return readSingleQuotedString(input);
		return readString(input);
	}

	private static String readDoubleQuotedString(PushbackReader input) throws IOException, UnexpectedCharacter {
		int c = read(input);
		if (c != '"') throw new UnexpectedCharacter(c,'"');
		StringBuilder sb = new StringBuilder();
		c = read(input);
		while (c != '"'){
			if (c == '\'') {
				input.unread(c);
				sb.append(readSingleQuotedString(input));
			}
			sb.append((char) c);
			c = read(input);
		}
		return sb.toString();
	}

	private static String readSingleQuotedString(PushbackReader input) {
		throw notImplemented(XMLParser.class,"readSingleQuotedString");
	}

	private static String readString(PushbackReader input) {
		throw notImplemented(XMLParser.class,"readString");
	}

	private static String readAttributeName(PushbackReader input) throws UnexpectedCharacter, IOException {
		StringBuilder name = new StringBuilder();
		int c = read(input);
		while (isAlphabetic(c)||isDigit(c)||c=='-'||c=='_'){
			name.append((char)c);
			c = read(input);
		}
		if (c != '=' && c!= '>') throw new UnexpectedCharacter(c,'=');
		return name.toString();
	}

	private static Result<Tag> readInner(PushbackReader input) throws IOException, ParseException {
		Stack<Integer> prefix = new Stack<>();
		skipWhitespace(input);
		int c = peek(input);
		if (c == '<') return parseTag(input);
		while (!prefix.empty()) input.unread(prefix.pop());
		return parseContent(input);
	}

	private static Result<Tag> parseContent(PushbackReader input) throws IOException {
		StringBuilder sb = new StringBuilder();
		int c = read(input);
		while (c > 0 && c != '<'){
			switch (c){
				case '"':
					input.unread(c);
					sb.append(readDoubleQuotedString(input));
					break;
				case '\'':
					input.unread(c);
					sb.append(readSingleQuotedString(input));
					break;
				default: sb.append((char)c);
			}
			c = read(input);
		}
		input.unread(c);
		return new Content(sb.toString());
	}

	private static Optional<String> readType(PushbackReader input) throws IOException {
		StringBuilder sb = new StringBuilder();
		int           c	 = read(input);
		while (isAlphabetic(c)||isDigit(c)) {
			sb.append((char)c);
			c = read(input);
		}
		input.unread(c);
		return absentIfBlank(sb.toString());
	}
}
