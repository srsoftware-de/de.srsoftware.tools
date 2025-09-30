/* © SRSoftware 2025 */
package de.srsoftware.tools; /* © SRSoftware 2024 */


import static de.srsoftware.tools.MimeType.MIME_JSON;
import static de.srsoftware.tools.Optionals.nullable;
import static java.lang.System.Logger.Level.*;
import static java.net.HttpURLConnection.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Optional.empty;
import static java.util.function.Predicate.not;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsExchange;
import de.srsoftware.tools.container.Error;
import de.srsoftware.tools.container.Payload;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * implementation of HttpHandler that attaches to a given path
 */
public abstract class PathHandler implements HttpHandler {
	/** "Authorization" **/   public static final String AUTHORIZATION    = "Authorization";
	/** "Content-Type" **/    public static final String CONTENT_TYPE     = "Content-Type";
	/** DELETE **/            public static final String DELETE           = "DELETE";
	/** x-forwarded-host **/  public static final String FORWARDED_HOST   = "x-forwarded-host";
	/** X-forwarded-proto **/ public static final String FORWARDED_PROTO  = "X-forwarded-proto";
	/** GET **/               public static final String GET	          = "GET";
	/** host **/              public static final String HOST             = "host";
	/** OPTIONS **/           public static final String OPTIONS          = "OPTIONS";
	/** PATCH **/             public static final String PATCH            = "PATCH";
	/** POST **/              public static final String POST             = "POST";

	/** System Logger **/    public System.Logger LOG	                 = System.getLogger(getClass().getSimpleName());

	/**
	 * Creates a new instance
	 */
	public PathHandler(){

	}

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
			var ph = PathHandler.this;
			for (var path : paths) {
				server.createContext(path, PathHandler.this);
				ph.LOG.log(INFO,"Binding {0} to {1}.",ph.getClass().getSimpleName(),path);
			}
			return ph;
		}
	}

	/**
	 * this method sets the 'Access-Control-Allow-Origin' header value
	 * @param ex the HttpExchange, whose response headers are to be modified
	 * @param origin the value for the header
	 * @return the HttpExchange
	 */
	public static HttpExchange allowOrigin(HttpExchange ex,String origin){
		ex.getResponseHeaders().add("Access-Control-Allow-Origin",origin);
		return ex;
	}

	/**
	 * this method sets the 'Access-Control-Allow-Credentials' header value to true
	 * @param ex the HttpExchange, whose response headers are to be modified
	 * @return the HttpExchange
	 */
	public static HttpExchange allowCredentials(HttpExchange ex){
		ex.getResponseHeaders().add("Access-Control-Allow-Credentials","true");
		return ex;
	}

	/**
	 * create a response with status code 400, send payload
	 * @param ex the HttpExchange to write to
	 * @param bytes the payload
	 * @return true – result is only created to allow return badRequest(…)
	 * @throws IOException if writing to the HttpEchange object fails
	 */
	public boolean badRequest(HttpExchange ex, byte[] bytes) throws IOException {
		return sendContent(ex, HTTP_BAD_REQUEST, bytes);
	}

	/**
	 * create a response with status code 400, send payload
	 * @param ex the HttpExchange to write to
	 * @param o the payload
	 * @return true – result is only created to allow return badRequest(…)
	 * @throws IOException if writing to the HttpEchange object fails
	 */
	public boolean badRequest(HttpExchange ex, Object o) throws IOException {
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
	 * @param ex HttpExchange used to return the not-implemented notification
	 * @return false
	 * @throws IOException if sending the response fails
	 */
	public boolean doDelete(Path path, HttpExchange ex) throws IOException {
		return notFound(ex);
	}

	/**
	 * "not found" default implementation
	 * @param path ignored
	 * @param ex HttpExchange used to return the not-implemented notification
	 * @return false
	 * @throws IOException if sending the response fails
	 */
	public boolean doGet(Path path, HttpExchange ex) throws IOException {
		return notFound(ex);
	}

	/**
	 * "not found" default implementation
	 * @param path ignored
	 * @param ex HttpExchange used to return the not-implemented notification
	 * @return false
	 * @throws IOException if sending the response fails
	 */
	public boolean doOptions(Path path, HttpExchange ex) throws IOException {
		return notFound(ex);
	}

	/**
	 * "not found" default implementation
	 * @param path ignored
	 * @param ex HttpExchange used to return the not-implemented notification
	 * @return false
	 * @throws IOException if sending the response fails
	 */
	public boolean doPatch(Path path, HttpExchange ex) throws IOException {
		return notFound(ex);
	}

	/**
	 * "not found" default implementation
	 * @param path ignored
	 * @param ex HttpExchange used to return the not-implemented notification
	 * @return false
	 * @throws IOException if sending the response fails
	 */
	public boolean doPost(Path path, HttpExchange ex) throws IOException {
		return notFound(ex);
	}

	@Override
	public void handle(HttpExchange ex) throws IOException {
		Path path   = relativePath(ex);
		String method = ex.getRequestMethod();
		LOG.log(INFO, "{0} {1}", method, path);
		boolean ignored = switch (method) {
			case DELETE -> doDelete(path,ex);
			case GET -> doGet(path,ex);
			case OPTIONS -> doOptions(path,ex);
			case PATCH -> doPatch(path,ex);
			case POST -> doPost(path,ex);
			default -> handleMethod(method,path,ex);
		};
		ex.getRequestBody().close();
 		ex.getResponseBody().close();
	}

	/**
	 * Default implementation, defaulting to notFound(ex).
	 * You can override this method to implement own httpRequest method handlers
	 * @param method method from the request header
	 * @param path path from the request header
	 * @param ex http exchange to process
	 * @return a boolean that is ignored
	 * @throws IOException if the transmission fails
	 */
	public boolean handleMethod(String method,Path path, HttpExchange ex) throws IOException {
		return notFound(ex);
	}

	/**
	 * convert the request URI to a path object
	 * @param ex the http exchange to process
	 * @return the path object
	 */
	public Path relativePath(HttpExchange ex) {
		var requestPath = ex.getRequestURI().toString();
		for (var path : paths){
			if (requestPath.startsWith(path)) {
				requestPath = requestPath.substring(path.length());
				break;
			}
		}
		var pos = requestPath.indexOf("?");
		if (pos >= 0) requestPath = requestPath.substring(0, pos);
		return Path.of(requestPath);
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
	 * extracts the request content type of a given HttpExchange
	 * @param ex the exchange whose content type is to be investigated
	 * @return an Optional containing the content type or empty if no content type was submitted
	 */
	public static Optional<String> contentType(HttpExchange ex){
		var list = ex.getRequestHeaders().get(CONTENT_TYPE);
		return list == null ? empty() : list.stream().findAny();
	}

	/**
	 * fetches data from an form-urlencoded message body
	 * @param ex the exchange whose content type is to be evaluated
	 * @return a data map representing the form data
	 * @throws IOException if the data cannot be read
	 */
	public static Map<String, Object> formData(HttpExchange ex) throws IOException {
		return Query.decode(body(ex));
	}

	/**
	 * extract the value of an <em>Authorization</em> header, if present
	 * @param ex the HttpExchange to extract from
	 * @return an optional, carrying the first value of an <em>Authorization</em> header, of present. empty, otherwise.
	 */
	public static Optional<String> getAuthToken(HttpExchange ex) {
		return getHeader(ex, AUTHORIZATION);
	}

	/**
	 * retrieve and decode a basic auth header
	 * @param ex the HttpExchange to process
	 * @return an optional containing the basic auth data, of such a header is present. empty otherwise.
	 */
	public static Optional<BasicAuth> getBasicAuth(HttpExchange ex) {
		return getAuthToken(ex)
			.filter(token -> token.startsWith("Basic "))  //
			.map(token -> token.substring(6))
			.map(Base64.getDecoder()::decode)
			.map(bytes -> new String(bytes, UTF_8))
			.map(token -> token.split(":", 2))
			.filter(arr -> arr.length == 2)
			.map(arr -> new BasicAuth(arr[0], arr[1]));
	}

	/**
	 * try to extract a bearer token from the headers of the HttpExchange
	 * @param ex the HttpExchange to process
	 * @return an optional carrying the token without the 'Bearer' prefix or empty, if no such token is given
	 */
	public static Optional<String> getBearer(HttpExchange ex) {
		return getAuthToken(ex).filter(token -> token.startsWith("Bearer ")).map(token -> token.substring(7));
	}

	/**
	 * get the value of a specific header field
	 * @param ex the HttpExchange to extract from
	 * @param key the key to search for
	 * @return an Optional carrying the value belonging to the key, or empty, if no value is set
	 */
	public static Optional<String> getHeader(HttpExchange ex, String key) {
		return nullable(ex.getRequestHeaders().get(key)).map(List::stream).flatMap(Stream::findFirst);
	}

	/**
	 * get the hostname requested by the client from the host header or – if set – the hostname submitted in the x-forwarded-host header
	 * @param ex the HttpExchange to process
	 * @return the host url, e.g. https://example.com
	 */
	public static String hostname(HttpExchange ex) {
		var headers = ex.getRequestHeaders();
		var host    = headers.getFirst(FORWARDED_HOST);
		if (host == null) host = headers.getFirst(HOST);
		var proto = nullable(headers.getFirst(FORWARDED_PROTO)).orElseGet(() -> ex instanceof HttpsExchange ? "https" : "http");
		return host == null ? null : proto + "://" + host;
	}

	/**
	 * Try to parse the body of this HttpExchange as JSON object
	 * @param ex the HttpExchange
	 * @return a json object build from the contents of this exchange
	 * @throws IOException if anything bad happens
	 */
	public static JSONObject json(HttpExchange ex) throws IOException {
		return new JSONObject(body(ex));
	}

	/**
	 * request the Accept-Language header
	 * @param ex the HttpExchange to prompt
	 * @return the set of languages passed via header
	 */
	public static List<String> languages(HttpExchange ex) {
		return getHeader(ex, "Accept-Language")
			.map(s -> s.split(","))
			.stream()
			.flatMap(Arrays::stream)
			.filter(not(String::isBlank))
			.toList();
	}

	/**
	 * sends a "not found" response
	 * @param ex the HttpExchange to act upon
	 * @return false
	 * @throws IOException if sending the response fails
	 */
	public boolean notFound(HttpExchange ex) throws IOException {
		LOG.log(ERROR, "not implemented");
		return sendEmptyResponse(HTTP_NOT_FOUND, ex);
	}

	/**
	 * map the query from the Request URI to a map
	 * @param ex the HttpExchange to read the entries from
	 * @return the query parameters as key → value map
	 */
	public static Map<String, Object> queryParam(HttpExchange ex) {
		return Query.decode(ex.getRequestURI().getQuery());
	}

	/**
	 * send a response without body
	 * @param statusCode send this status code
	 * @param ex the HttpExchange to act on
	 * @return false
	 * @throws IOException if the response cannot be sent
	 */
	public static boolean sendEmptyResponse(int statusCode, HttpExchange ex) throws IOException {
		ex.sendResponseHeaders(statusCode, 0);
		return false;
	}

	/**
	 * send a redirect to the client
	 * @param ex the HttpExchange to use
	 * @param url the targeted location
	 * @return false
	 * @throws IOException if the anser cannot be sent
	 */
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
	public boolean sendContent(HttpExchange ex, int status, byte[] bytes) throws IOException {
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
	public boolean sendContent(HttpExchange ex, int status, Object o) throws IOException {
		if (o instanceof Payload<?> payload) o = payload.get();
		if (o instanceof Stream<?> stream) o = stream.toList();
		if (o instanceof Set<?> set) o = List.copyOf(set);
		if (o instanceof List<?> list) o = new JSONArray(list);
		if (o instanceof Mappable mappable) o = mappable.toMap();
		if (o instanceof Map<?, ?> map) o = new JSONObject(map);
		if (o instanceof HttpError<?> error) return sendContent(ex, error.code(), error.json());
		if (o instanceof Error<?> error) return serverError(ex, error.json());
		if (o instanceof JSONObject || o instanceof JSONArray) ex.getResponseHeaders().add(CONTENT_TYPE, MIME_JSON);
		return sendContent(ex, status, o.toString().getBytes(UTF_8));
	}


	/**
	 * send an array of bytes
	 * @param ex the http exchange to respond to
	 * @param bytes the payload to send
	 * @return a boolean that is ignored
	 * @throws IOException if writing to the HttpExchange object fails
	 */
	public boolean sendContent(HttpExchange ex, byte[] bytes) throws IOException {
		return sendContent(ex, HTTP_OK, bytes);
	}

	/**
	 * send an object, thereby trying to guess the correct mime and transmission type.
	 * @param ex the http exchange to respond to
	 * @param o the payload to send
	 * @return a boolean that is ignored
	 * @throws IOException if writing to the HttpExchange object fails
	 */
	public boolean sendContent(HttpExchange ex, Object o) throws IOException {
		return sendContent(ex, HTTP_OK, o);
	}

	/**
	 * create a "internal server error" response
	 * @param ex the HttpExchange object
	 * @param o an additional payload
	 * @return false
	 * @throws IOException if the content cannot be sent
	 */
	public boolean serverError(HttpExchange ex, Object o) throws IOException {
		sendContent(ex, HTTP_INTERNAL_ERROR, o);
		return false;
	}

	/**
	 * recover the URL from an HttpExchange object
	 * @param ex the HttpExchange object
	 * @return the url of this object
	 */
	public static String url(HttpExchange ex) {
		return hostname(ex) + ex.getRequestURI();
	}
}
