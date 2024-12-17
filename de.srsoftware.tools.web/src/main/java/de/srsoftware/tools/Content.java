/* © SRSoftware 2024 */
package de.srsoftware.tools;

public class Content implements Result<String> {
	private final String token;

	private Content(String token) {
		this.token = token;
	}

	public static Content of(String token) {
		return new Content(token);
	}

	public String get() {
		return token;
	}
}
