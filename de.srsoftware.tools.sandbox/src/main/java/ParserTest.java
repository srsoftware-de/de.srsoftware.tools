import de.srsoftware.tools.TagPayload;
import de.srsoftware.tools.XMLParser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class ParserTest {
	public static void main(String[] args) throws URISyntaxException, IOException {
		URL url = new URI("https://srsoftware.de").toURL();
		var input = url.openConnection().getInputStream();
		var result = XMLParser.parse(input);
		if (result instanceof TagPayload tag){
			System.out.println(tag.get().toString(2));
		}

	}
}
