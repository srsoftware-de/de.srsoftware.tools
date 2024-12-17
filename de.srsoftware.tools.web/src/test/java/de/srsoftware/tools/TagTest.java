/* © SRSoftware 2024 */
package de.srsoftware.tools;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TagTest {

	@Test
	public void testIndentation(){
		var code = new Tag("html")
				.add(new Tag("head").add(new Tag("title").content("Title")))
				.add(new Tag("body").attr("id","nice").add(new Tag("h1").id("test").content("this is the headline")))
				.toString(2);
		var expected = "<html>\n  <head>\n    <title>Title</title>\n  </head>\n  <body id=\"nice\">\n    <h1 id=\"test\">this is the headline</h1>\n  </body>\n</html>\n";
		assertEquals(expected,code);

	}
	@Test
	public void testMixedContent() {
		var tag = new Tag("p").add(new Tag(null).attr("dont","show").content("This ")).add(new Tag("b").content("is")).add(new Tag("").content(" a ")).add(new Tag("i").content("test"));
		System.out.println(tag.toString(2));
		System.out.println(tag);
		assertEquals("<p>This <b>is</b> a <i>test</i></p>", tag.toString());
	}
}
