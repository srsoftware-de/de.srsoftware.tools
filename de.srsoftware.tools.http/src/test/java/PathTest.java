/* © SRSoftware 2025 */
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sun.net.httpserver.*;
import de.srsoftware.tools.Path;
import de.srsoftware.tools.PathHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import org.junit.jupiter.api.Test;

public class PathTest {

	private class ExchangeMock extends HttpExchange {

		private final URI uri;

		public ExchangeMock(URI uri){
			this.uri = uri;
		}

		@Override
		public Headers getRequestHeaders() {
			return null;
		}

		@Override
		public Headers getResponseHeaders() {
			return null;
		}

		@Override
		public URI getRequestURI() {
			return uri;
		}

		@Override
		public String getRequestMethod() {
			return "";
		}

		@Override
		public HttpContext getHttpContext() {
			return null;
		}

		@Override
		public void close() {

		}

		@Override
		public InputStream getRequestBody() {
			return null;
		}

		@Override
		public OutputStream getResponseBody() {
			return null;
		}

		@Override
		public void sendResponseHeaders(int i, long l) throws IOException {

		}

		@Override
		public InetSocketAddress getRemoteAddress() {
			return null;
		}

		@Override
		public int getResponseCode() {
			return 0;
		}

		@Override
		public InetSocketAddress getLocalAddress() {
			return null;
		}

		@Override
		public String getProtocol() {
			return "";
		}

		@Override
		public Object getAttribute(String s) {
			return null;
		}

		@Override
		public void setAttribute(String s, Object o) {

		}

		@Override
		public void setStreams(InputStream inputStream, OutputStream outputStream) {

		}

		@Override
		public HttpPrincipal getPrincipal() {
			return null;
		}
	}

	private PathHandler mockHandler(){
		var handler = new PathHandler(){};
		handler.bindPath("/");
		return handler;
	}

	@Test
	void testNull(){
		var path = Path.of(null);
		assertEquals(0,path.size());
	}

	@Test
	void testEmpty(){
		var path = Path.of("");
		assertEquals(0,path.size());
	}

	@Test
	void testBlank(){
		var path = Path.of("   ");
		assertEquals(0,path.size());
	}

	@Test
	void testRoot(){
		var path = Path.of("/");
		assertEquals(0,path.size());
	}

	@Test
	void testWithoutLeadingSlash(){
		var path = Path.of("this/is/a/test");
		assertEquals("this/is/a/test",path.toString());
		assertEquals("this",path.pop());
		assertEquals("is",path.pop());
		assertEquals("a",path.pop());
		assertEquals("test",path.pop());
		assertTrue(path.isEmpty());
	}

	@Test
	void testWithLeadingSlash(){
		var path = Path.of("/this/is/a/test");
		assertEquals("this/is/a/test",path.toString());
		assertEquals("this",path.pop());
		assertEquals("is",path.pop());
		assertEquals("a",path.pop());
		assertEquals("test",path.pop());
		assertTrue(path.isEmpty());
	}

	@Test
	void testRelativePath1() throws URISyntaxException {
		var ex = new ExchangeMock(new URI("/this/is/a/test"));
		var path = mockHandler().relativePath(ex);
		assertEquals("this",path.pop());
		assertEquals("is",path.pop());
		assertEquals("a",path.pop());
		assertEquals("test",path.pop());
		assertTrue(path.isEmpty());
	}

	@Test
	void testRelativePath2() throws URISyntaxException {
		var ex = new ExchangeMock(new URI("this/is/a/test"));
		var path = mockHandler().relativePath(ex);
		assertEquals("this",path.pop());
		assertEquals("is",path.pop());
		assertEquals("a",path.pop());
		assertEquals("test",path.pop());
		assertTrue(path.isEmpty());
	}
}
