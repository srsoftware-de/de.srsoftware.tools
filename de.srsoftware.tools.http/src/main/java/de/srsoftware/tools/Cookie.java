/* © SRSoftware 2025 */
package de.srsoftware.tools; /* © SRSoftware 2024 */

import static de.srsoftware.tools.Optionals.nullable;
import static java.lang.System.Logger.Level.INFO;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * This class allows to add cookies to a header object
 */
public abstract class Cookie implements Map.Entry<String, String> {
	static final System.Logger LOG = System.getLogger(SessionToken.class.getSimpleName());
	private final String       key;
	private String	           value = null;

	Cookie(String key, String value) {
		this.key = key;
		setValue(value);
	}

	/**
	 * add this cookie instance to a Headers object
	 * @param headers the headers object, to which this cookie is added
	 * @return this cookie
	 * @param <T> the type of this cookie
	 */
	@SuppressWarnings("unchecked")
	public <T extends Cookie> T addTo(Headers headers) {
		LOG.log(INFO, "sending cookie {0}={1}", key, value);
		headers.add("Set-Cookie", "%s=%s".formatted(key, value));
		return (T)this;
	}

	/**
	 * add this cookie instance to a Headers object
	 * @param ex the HttpExchange object, to which this cookie is added
	 * @return this cookie
	 * @param <T> the type of this cookie
	 */
	public <T extends Cookie> T addTo(HttpExchange ex) {
		return this.addTo(ex.getResponseHeaders());
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public String getValue() {
		return value;
	}

	/**
	 * list the cookies of a given HttpExchange object
	 * @param ex the HttpExchange object
	 * @return the list of cookies
	 */
	protected static List<String> of(HttpExchange ex) {
		return nullable(ex.getRequestHeaders().get("Cookie")).stream().flatMap(List::stream).flatMap(s -> Arrays.stream(s.split(";"))).map(String::trim).peek(cookie -> LOG.log(INFO, "received cookie {0}", cookie)).toList();
	}

	@Override
	public String setValue(String s) {
		var oldVal = value;
		value      = s;
		return oldVal;
	}
}
