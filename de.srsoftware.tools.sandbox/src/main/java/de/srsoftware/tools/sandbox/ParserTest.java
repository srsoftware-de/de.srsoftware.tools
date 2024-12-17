/* © SRSoftware 2024 */
package de.srsoftware.tools.sandbox;

import de.srsoftware.tools.OpeningTag;
import de.srsoftware.tools.XMLParser;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class ParserTest {
	public static void main(String[] args) throws URISyntaxException, IOException {
		URL url    = new URI("https://srsoftware.de").toURL();
		var input  = url.openConnection().getInputStream();
		var result = XMLParser.parse(input);
		if (result instanceof OpeningTag tag) {
			System.out.println(tag.get().toString(2));
			Files.writeString(Path.of("/tmp/test.html"), tag.get().toString(2));
		}
	}
}
