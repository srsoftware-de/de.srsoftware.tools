/* © SRSoftware 2025 */
package de.srsoftware.tools;

import static org.junit.jupiter.api.Assertions.*;

import de.srsoftware.tools.container.Payload;
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
	public void testParseSimple() throws IOException {
		var code   = load("simple.html");
		var result = XMLParser.parse(code);
		assertInstanceOf(Payload.class, result);
		var tag = ((Payload<Tag>)result).get();
		System.out.println(tag.toString(2));
		assertEquals("<html><head /><body /></html>", tag.toString());
	}

	@Test
	public void testParseWithAttributes() throws IOException {
		var code   = load("attributes.html");
		var result = XMLParser.parse(code);
		assertInstanceOf(Payload.class, result);
		var tag = ((Payload<Tag>)result).get();
		System.out.println(tag.toString(2));
		assertEquals("<html><head class=\"secret\" /><body class=\"empty\" id=\"blob\" /></html>", tag.toString());
	}

	@Test
	public void testParseWithEmptyAttributes() throws IOException {
		var code   = load("empty_attributes.html");
		var result = XMLParser.parse(code);
		assertInstanceOf(Payload.class, result);
		var tag = ((Payload<Tag>)result).get();
		System.out.println(tag.toString(2));
		assertEquals("<html><head class=\"secret\" /><body empty id=\"test\" wild /></html>", tag.toString());
	}


	@Test
	public void testParseUnmatched() throws IOException {
		var code   = load("unmatched.html");
		var result = XMLParser.parse(code);
		assertInstanceOf(Payload.class, result);
		var tag = ((Payload<Tag>)result).get();
		System.out.println(tag.toString(2));
		assertEquals("<html lang=\"en\"><head><meta role=\"broken\"><legal>content</legal></meta></head><body>Test</body></html>", tag.toString());
	}

	@Test
	public void testParseXHtml() throws IOException {
		var code   = load("xhtml.html");
		var result = XMLParser.parse(code);
		assertInstanceOf(Payload.class, result);
		var tag = ((Payload<Tag>)result).get();
		System.out.println(tag.toString(2));
		assertEquals("<html lang=\"en\"><head><meta charset=\"UTF-8\" /></head><body><h1>Test</h1></body></html>", tag.toString());
	}

	@Test
	public void testParseMixed() throws IOException {
		var code   = load("mixed_content.html");
		var result = XMLParser.parse(code);
		assertInstanceOf(Payload.class, result);
		var tag = ((Payload<Tag>)result).get();
		System.out.println(tag.toString(2));
		assertEquals("<html lang=\"en\"><head><meta charset=\"UTF-8\" /><title>Title</title></head><body><p>\nThis <em>paragraph</em> contains <b>mixed, <i>nested</i></b> Content\n</p></body></html>", tag.toString());
	}

	@Test
	public void testParseSingleQuotedAttributes() throws IOException {
		var code = load("single_quoted_attributes.html");
		var result = XMLParser.parse(code);
		assertInstanceOf(Payload.class, result);
		var tag = ((Payload<Tag>)result).get();
		System.out.println(tag.toString(2));
		assertEquals("<html><head class=\"secret\" /><body class=\"empty\" id=\"blob\" single_quoted=\"this is shit\" /></html>", tag.toString());
	}

	@Test
	public void testTolerateBroken() throws IOException{
		var code = load("broken.html");
		var result = XMLParser.parse(code);
		assertInstanceOf(Payload.class, result);
		var tag = ((Payload<Tag>)result).get();
		System.out.println(tag.toString(2));
		assertEquals("<html lang=\"en\"><body><table><tr><td class=\"kalDat1\"><a class=\"kalDetl\" href=\"somewhere\" title=\"Termindetails\">Märchentheater 'Hänsel & Gretel</a></td></tr></table></body></html>", tag.toString());
	}
}
