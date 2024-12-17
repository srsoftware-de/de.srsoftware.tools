/* © SRSoftware 2024 */
package de.srsoftware.tools;

import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


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
		assertInstanceOf(TagPayload.class,result);
		var tag = ((TagPayload)result).get();
		assertEquals("<html><head /><body /></html>",tag.toString());
	}

	@Test
	public void testParseWithAttributes() throws IOException, ParseException {
		var code   = load("attributes.html");
		var result = XMLParser.parse(code);
		assertInstanceOf(TagPayload.class,result);
		var tag = ((TagPayload)result).get();
		assertEquals("<html><head class=\"secret\" /><body id=\"blob\" class=\"empty\" /></html>",tag.toString());
	}

	@Test
	public void testParseUnmatched() throws IOException, ParseException {
		var code   = load("unmatched.html");
		var result = XMLParser.parse(code);
		assertInstanceOf(TagPayload.class,result);
		var tag = ((TagPayload)result).get();
		assertEquals("<html lang=\"en\"><head><meta charset=\"UTF-8\" /></head></html>",tag.toString());
	}

	@Test
	public void testParseXHtml() throws IOException, ParseException {
		var code   = load("xhtml.html");
		var result = XMLParser.parse(code);
		assertInstanceOf(TagPayload.class,result);
		var tag = ((TagPayload)result).get();
		assertEquals("<html lang=\"en\"><head><meta charset=\"UTF-8\" /><body><h1>Test</h1></body></head></html>",tag.toString());
	}
}
