/* © SRSoftware 2024 */
package de.srsoftware.tools;

public class ContentTag implements Result<String> {
	private final String token;

	private ContentTag(String token) {
		this.token = token;
	}

	public static ContentTag of(String token) {
		return new ContentTag(token);
	}

	public String get() {
		return token;
	}
}
