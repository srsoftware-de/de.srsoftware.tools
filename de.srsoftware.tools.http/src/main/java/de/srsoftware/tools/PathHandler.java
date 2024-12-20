/* © SRSoftware 2024 */
package de.srsoftware.tools; /* © SRSoftware 2024 */


import static de.srsoftware.tools.Optionals.nullable;
import static java.lang.System.Logger.Level.*;
import static java.net.HttpURLConnection.*;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsExchange;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * implementation of HttpHandler that attaches to a given path
 */
public abstract class PathHandler implements HttpHandler {
	public static final String  AUTHORIZATION    = "Authorization";
	public static final String  CONTENT_TYPE     = "Content-Type";
	public static final String  DEFAULT_LANGUAGE = "en";
	public static final String  DELETE           = "DELETE";
	private static final String FORWARDED_HOST   = "x-forwarded-host";
	public static final String  GET	             = "GET";
	public static final String  HOST             = "host";
	public static final String  JSON             = "application/json";
	public static System.Logger LOG	             = System.getLogger(PathHandler.class.getSimpleName());
	public static final String  POST             = "POST";

	private String[] paths;

	/**
	 * Object to hold auth data
	 * @param userId the user's id
	 * @param pass the assigned plaintext password
	 */
	public record BasicAuth(String userId, String pass) {
	}

	/**
	 * this class allows to bind a PathHandler to a HttpServer instance
	 */
	public class Bond {
		/**
		 * create a new bond
		 * @param paths the paths to bind to
		 */
		Bond(String[] paths) {
			PathHandler.this.paths = paths;
		}

		/**
		 * create a context on the server object for every path stored in this Bond
		 * @param server the server to bind to
		 * @return this PathHandler object
		 */
		public PathHandler on(HttpServer server) {
			for (var path : paths) server.createContext(path, PathHandler.this);
			return PathHandler.this;
		}
	}

	/**
	 * create a response with status code 400, send payload
	 * @param ex the HttpExchange to write to
	 * @param bytes the payload
	 * @return true – result is only created to allow return badRequest(…)
	 * @throws IOException if writing to the HttpEchange object fails
	 */
	public static boolean badRequest(HttpExchange ex, byte[] bytes) throws IOException {
		return sendContent(ex, HTTP_BAD_REQUEST, bytes);
	}

	/**
	 * create a response with status code 400, send payload
	 * @param ex the HttpExchange to write to
	 * @param o the payload
	 * @return true – result is only created to allow return badRequest(…)
	 * @throws IOException if writing to the HttpEchange object fails
	 */
	public static boolean badRequest(HttpExchange ex, Object o) throws IOException {
		return sendContent(ex, HTTP_BAD_REQUEST, o);
	}

	/**
	 * Bind the PathHandler object to one/several paths
	 * @param path the paths to bind to
	 * @return a bond, that can be used to create a context on a HttpServer
	 */
	public Bond bindPath(String... path) {
		return new Bond(path);
	}

	/**
	 * "not found" default implementation
	 * @param path ignored
	 * @param ex HttpExchange used to return the not-implmented notification
	 * @return false
	 * @throws IOException if sending the response fails
	 */
	public boolean doDelete(String path, HttpExchange ex) throws IOException {
		return notFound(ex);
	}

	/**
	 * "not found" default implementation
	 * @param path ignored
	 * @param ex HttpExchange used to return the not-implmented notification
	 * @return false
	 * @throws IOException if sending the response fails
	 */
	public boolean doGet(String path, HttpExchange ex) throws IOException {
		return notFound(ex);
	}

	/**
	 * "not found" default implementation
	 * @param path ignored
	 * @param ex HttpExchange used to return the not-implmented notification
	 * @return false
	 * @throws IOException if sending the response fails
	 */
	public boolean doPost(String path, HttpExchange ex) throws IOException {
		return notFound(ex);
	}

	@Override
	public void handle(HttpExchange ex) throws IOException {
		String path   = relativePath(ex);
		String method = ex.getRequestMethod();
		LOG.log(INFO, "{0} {1}", method, path);
		boolean ignored = switch (method) {
			case DELETE -> doDelete(path,ex);
			case GET -> doGet(path,ex);
			case POST -> doPost(path,ex);
			default -> false;
		};
 		ex.getResponseBody().close();
	}

	public String relativePath(HttpExchange ex) {
		var requestPath = ex.getRequestURI().toString();
		for (var path : paths){
					if (requestPath.startsWith(path)) {
						requestPath = requestPath.substring(path.length());
						break;
					}
				}
				if (!requestPath.startsWith("/")) requestPath = "/" + requestPath;
				var pos = requestPath.indexOf("?");
				if (pos >= 0) requestPath = requestPath.substring(0, pos);
				return requestPath;
		}

		/******* begin of static methods *************/

		/**
		 * extracts the body of an HttpExchange
		 * @param ex the exchange to process
		 * @return the content of the HttpExchange
		 * @throws IOException if reading the body failed
		 */
		public static String body(HttpExchange ex) throws IOException {
			return new String(ex.getRequestBody().readAllBytes(), UTF_8);
		}

		/**
		 * extract the value of an <em>Authorization</em> header, if present
		 * @param ex the HttpExchange to extract from
		 * @return an optional, carrying the first value of an <em>Authorization</em> header, of present. empty, otherwise.
		 */
		public static Optional<String> getAuthToken(HttpExchange ex) {
			return getHeader(ex, AUTHORIZATION);
		}

		public static Optional<BasicAuth> getBasicAuth(HttpExchange ex) {
			return getAuthToken(ex)
			    .filter(token -> token.startsWith("Basic "))  //
			    .map(token -> token.substring(6))
			    .map(Base64.getDecoder()::decode)
			    .map(bytes -> new String(bytes, UTF_8))
			    .map(token -> token.split(":", 2))
			    .map(arr -> new BasicAuth(arr[0], arr[1]));
		}

		public static Optional<String> getBearer(HttpExchange ex) {
			return getAuthToken(ex).filter(token -> token.startsWith("Bearer ")).map(token -> token.substring(7));
		}

		public static Optional<String> getHeader(HttpExchange ex, String key) {
			return nullable(ex.getRequestHeaders().get(key)).map(List::stream).flatMap(Stream::findFirst);
		}

		public static String hostname(HttpExchange ex) {
			var headers = ex.getRequestHeaders();
			var host    = headers.getFirst(FORWARDED_HOST);
			if (host == null) host = headers.getFirst(HOST);
			var proto = nullable(headers.getFirst("X-forwarded-proto")).orElseGet(() -> ex instanceof HttpsExchange ? "https" : "http");
			return host == null ? null : proto + "://" + host;
		}

		public static JSONObject json(HttpExchange ex) throws IOException {
			return new JSONObject(body(ex));
		}

		public static String language(HttpExchange ex) {
			return getHeader(ex, "Accept-Language")  //
			    .map(s -> Arrays.stream(s.split(",")))
			    .flatMap(Stream::findFirst)
			    .orElse(DEFAULT_LANGUAGE);
		}

		public static boolean notFound(HttpExchange ex) throws IOException {
			LOG.log(ERROR, "not implemented");
			return sendEmptyResponse(HTTP_NOT_FOUND, ex);
		}

		public Map<String, String> queryParam(HttpExchange ex) {
			return Arrays
			    .stream(ex.getRequestURI().getQuery().split("&"))  //
			    .map(s -> s.split("=", 2))
			    .collect(Collectors.toMap(arr -> arr[0], arr -> arr[1]));
		}

		public static boolean sendEmptyResponse(int statusCode, HttpExchange ex) throws IOException {
			ex.sendResponseHeaders(statusCode, 0);
			return false;
		}

		public static boolean sendRedirect(HttpExchange ex, String url) throws IOException {
			ex.getResponseHeaders().add("Location", url);
			return sendEmptyResponse(HTTP_MOVED_TEMP, ex);
		}

		/**
		 * create a response given status code, send payload
		 * @param ex the HttpExchange to write to
		 * @param status the status code
		 * @param bytes the payload
		 * @return true – result is only created to allow return badRequest(…)
		 * @throws IOException if writing to the HttpEchange object fails
		 */
		public static boolean sendContent(HttpExchange ex, int status, byte[] bytes) throws IOException {
			LOG.log(DEBUG, "sending {0} response…", status);
			ex.sendResponseHeaders(status, bytes.length);
			ex.getResponseBody().write(bytes);
			return true;
		}

		/**
		 * create a response given status code, send payload
		 * @param ex the HttpExchange to write to
		 * @param status the status code
		 * @param o the payload
		 * @return true – result is only created to allow return badRequest(…)
		 * @throws IOException if writing to the HttpEchange object fails
		 */
		public static boolean sendContent(HttpExchange ex, int status, Object o) throws IOException {
			if (o instanceof List<?> list) o = new JSONArray(list);
			if (o instanceof Map<?, ?> map) o = new JSONObject(map);
			if (o instanceof Error<?> error) o = error.json();
			if (o instanceof JSONObject) ex.getResponseHeaders().add(CONTENT_TYPE, JSON);
			return sendContent(ex, status, o.toString().getBytes(UTF_8));
		}


		public static boolean sendContent(HttpExchange ex, byte[] bytes) throws IOException {
			return sendContent(ex, HTTP_OK, bytes);
		}

		public static boolean sendContent(HttpExchange ex, Object o) throws IOException {
			return sendContent(ex, HTTP_OK, o);
		}

		public static boolean serverError(HttpExchange ex, Object o) throws IOException {
			sendContent(ex, HTTP_INTERNAL_ERROR, o);
			return false;
		}

		public static String url(HttpExchange ex) {
			return hostname(ex) + ex.getRequestURI();
		}
	}
