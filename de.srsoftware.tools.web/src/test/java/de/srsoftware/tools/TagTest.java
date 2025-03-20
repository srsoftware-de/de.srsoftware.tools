/* © SRSoftware 2024 */
package de.srsoftware.tools;

import static de.srsoftware.tools.TagFilter.*;
import static org.junit.jupiter.api.Assertions.*;

import de.srsoftware.tools.result.Payload;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.junit.jupiter.api.Test;

public class TagTest {
	private static InputStream load(String filename) throws IOException {
		var resource = XmlParserTest.class.getClassLoader().getResource(filename);
		return resource.openConnection().getInputStream();
	}

	@Test
	public void testIndentation() {
		var code     = new Tag("html").add(new Tag("head").add(new Tag("title").content("Title"))).add(new Tag("body").attr("id", "nice").add(new Tag("h1").id("test").content("this is the headline"))).toString(2);
		var expected = "<html>\n  <head>\n    <title>\n      Title\n    </title>\n  </head>\n  <body id=\"nice\">\n    <h1 id=\"test\">\n      this is the headline\n    </h1>\n  </body>\n</html>\n";
		assertEquals(expected, code);
	}
	@Test
	public void testMixedContent() {
		var tag = new Tag("p").content("This ").add(new Tag("b").content("is")).content(" a ").add(new Tag("i").content("test"));
		assertEquals("<p>This <b>is</b> a <i>test</i></p>", tag.toString());
	}

	@Test
	public void testFindAttribute() throws IOException {
		var result = XMLParser.parse(load("finding_nemo.html"));
		assertInstanceOf(Payload.class, result);
		Payload<Tag> payload = (Payload<Tag>)result;
		List<Tag>    matches = payload.get().find(withAttribute("type"));
		assertEquals(2, matches.size());
		assertEquals("input", matches.getFirst().type());
		assertEquals("input", matches.get(1).type());
	}

	@Test
	public void testFindAttributeWithValue() throws IOException {
		var result = XMLParser.parse(load("finding_nemo.html"));
		assertInstanceOf(Payload.class, result);
		Payload<Tag> payload = (Payload<Tag>)result;
		List<Tag>    matches = payload.get().find(attributeEquals("id", "last"));
		assertEquals(1, matches.size());
		assertEquals("li", matches.getFirst().type());

		matches = payload.get().find(attributeEquals("class", "red"));
		assertEquals(1, matches.size());
		var tag = matches.getFirst();
		assertEquals("span", tag.type());
		var child = tag.children().getFirst();
		assertInstanceOf(Text.class, child);
		var text = (Text)child;
		assertEquals("Text", text.toString());

		matches = payload.get().find(attributeContains("class", "title"));
		assertEquals(2, matches.size());
		tag = matches.getFirst();
		assertEquals("title", tag.type());
		tag = matches.get(1);
		assertEquals("h1", tag.type());
	}

	@Test
	public void testFindTagType() throws IOException {
		var result = XMLParser.parse(load("finding_nemo.html"));
		assertInstanceOf(Payload.class, result);
		Payload<Tag> payload = (Payload<Tag>)result;
		List<Tag>    matches = payload.get().find(ofType("select"));
		assertEquals(1, matches.size());
		var select = matches.getFirst();
		assertEquals("select", select.type());
	}

	@Test
	public void testFindAttributeHas() throws IOException {
		var result = XMLParser.parse(load("finding_nemo.html"));
		assertInstanceOf(Payload.class, result);
		Payload<Tag> payload = (Payload<Tag>)result;
		var          matches = payload.get().find(attributeHas("name", "several"));
		assertEquals(1, matches.size());
		var select = matches.getFirst();
		assertEquals("select", select.type());

		matches = payload.get().find(attributeHas("name", "values"));
		assertEquals(1, matches.size());
		select = matches.getFirst();
		assertEquals("select", select.type());

		matches = payload.get().find(attributeHas("name", "for"));
		assertEquals(1, matches.size());
		select = matches.getFirst();
		assertEquals("select", select.type());

		matches = payload.get().find(attributeHas("name", "tag"));
		assertEquals(1, matches.size());
		select = matches.getFirst();
		assertEquals("select", select.type());

		matches = payload.get().find(attributeHas("name", "several values"));
		assertTrue(matches.isEmpty());

		matches = payload.get().find(attributeHas("name", "for tag"));
		assertTrue(matches.isEmpty());
	}

	@Test
	public void testFindAttributeStartsWith() throws IOException {
		var result = XMLParser.parse(load("finding_nemo.html"));
		assertInstanceOf(Payload.class, result);
		Payload<Tag> payload = (Payload<Tag>)result;
		List<Tag>    matches = payload.get().find(attributeStartsWith("class", "page_"));
		assertEquals(1, matches.size());
		assertEquals("h1", matches.getFirst().type());
	}

	@Test
	public void testFindAttributeEndsWith() throws IOException {
		var result = XMLParser.parse(load("finding_nemo.html"));
		assertInstanceOf(Payload.class, result);
		Payload<Tag> payload = (Payload<Tag>)result;
		List<Tag>    matches = payload.get().find(attributeEndsWith("class", "_first"));
		assertEquals(1, matches.size());
		assertEquals("h1", matches.getFirst().type());
	}

	@Test
	public void testStrip() throws IOException {
		var result = XMLParser.parse(load("finding_nemo.html"));
		assertInstanceOf(Payload.class, result);
		Payload<Tag> payload = (Payload<Tag>)result;
		List<Tag>    matches = payload.get().find(ofType("p"));
		assertEquals(1, matches.size());
		var p = matches.getFirst();
		assertEquals("p", p.type());
		var text = p.strip().trim();
		assertEquals("Some Text with emphasis.", text);
	}
}
