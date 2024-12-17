/* © SRSoftware 2024 */
package de.srsoftware.tools;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Test;


public class XmlParserTest {
	private static final System.Logger LOG = System.getLogger(XmlParserTest.class.getSimpleName());

	private static InputStream load(String filename) throws IOException {
		var resource = XmlParserTest.class.getClassLoader().getResource(filename);
		return resource.openConnection().getInputStream();
	}

	@Test
	public void testParseSimple() throws IOException, ParseException {
		var code   = load("simple.html");
		var result = XMLParser.parse(code);
		assertInstanceOf(OpeningTag.class, result);
		var tag = ((OpeningTag)result).get();
		System.out.println(tag.toString(2));
		assertEquals("<html><head /><body /></html>", tag.toString());
	}

	@Test
	public void testParseWithAttributes() throws IOException, ParseException {
		var code   = load("attributes.html");
		var result = XMLParser.parse(code);
		assertInstanceOf(OpeningTag.class, result);
		var tag = ((OpeningTag)result).get();
		System.out.println(tag.toString(2));
		assertEquals("<html><head class=\"secret\" /><body class=\"empty\" id=\"blob\" /></html>", tag.toString());
	}

	@Test
	public void testParseWithEmptyAttributes() throws IOException, ParseException {
		var code   = load("empty_attributes.html");
		var result = XMLParser.parse(code);
		assertInstanceOf(OpeningTag.class, result);
		var tag = ((OpeningTag)result).get();
		System.out.println(tag.toString(2));
		assertEquals("<html><head class=\"secret\" /><body empty id=\"test\" wild /></html>", tag.toString());
	}


	@Test
	public void testParseUnmatched() throws IOException, ParseException {
		var code   = load("unmatched.html");
		var result = XMLParser.parse(code);
		assertInstanceOf(OpeningTag.class, result);
		var tag = ((OpeningTag)result).get();
		System.out.println(tag.toString(2));
		assertEquals("<html lang=\"en\"><head><meta role=\"broken\"><legal>content</legal><body>TEst</body></meta></head></html>", tag.toString());
	}

	@Test
	public void testParseXHtml() throws IOException, ParseException {
		var code   = load("xhtml.html");
		var result = XMLParser.parse(code);
		assertInstanceOf(OpeningTag.class, result);
		var tag = ((OpeningTag)result).get();
		System.out.println(tag.toString(2));
		assertEquals("<html lang=\"en\"><head><meta charset=\"UTF-8\" /></head><body><h1>Test</h1></body></html>", tag.toString());
	}
}
