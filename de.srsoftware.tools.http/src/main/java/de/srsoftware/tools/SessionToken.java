/* © SRSoftware 2025 */
package de.srsoftware.tools; /* © SRSoftware 2024 */


import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;


/**
 * a special cookies allowing to keep track of a session
 */
public class SessionToken extends Cookie {
	private final String	       sessionId;
	private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss O");

	/**
	 * create a new sessoin token cookie carrying the given session id
	 * @param sessionId a string to provide as sesstionToken cookie
	 * @param path the path this cookie is valid for
	 * @param expiration the time when the cookie is about to expire
	 * @param trust whether the cookie shall be persistent
	 */
	public SessionToken(String sessionId, String path, Instant expiration, boolean trust) {
		super("sessionToken", sessionToken(sessionId, path, expiration, trust));
		this.sessionId = sessionId;
	}

	private static String sessionToken(String sessionId, String path, Instant expiration, boolean trust) {
		StringBuilder sb = new StringBuilder();
		sb.append(sessionId);
		if (path != null) sb.append("; Path=").append(path);
		if (trust && expiration != null) sb.append("; Expires=").append(FORMAT.format(expiration.atZone(ZoneOffset.UTC)));
		return sb.toString();
	}

	/**
	 * create a new sessoin token cookie carrying the given session id
	 * @param sessionId a string to provide as sesstionToken cookie
	 */
	public SessionToken(String sessionId) {
		super("sessionToken", sessionId);
		this.sessionId = sessionId;
	}

	@Override
	public <T extends Cookie> T addTo(Headers headers) {
		headers.add("session", getValue());
		return super.addTo(headers);
	}

	/**
	 * extract the session token from an HttpExchange
	 * @param ex the exchange to extract from
	 * @return an optional carrying a session token, if one was submitted in the exchange
	 */
	public static Optional<SessionToken> from(HttpExchange ex) {
		return Cookie.of(ex)
		    .stream()
		    .filter(cookie -> cookie.startsWith("sessionToken="))

		    .map(cookie -> cookie.split("=", 2)[1])
		    .map(id -> new SessionToken(id))
		    .findAny();
	}

	/**
	 * return the string carried by this session token instance
	 * @return the carried id
	 */
	public String sessionId() {
		return sessionId;
	}
}