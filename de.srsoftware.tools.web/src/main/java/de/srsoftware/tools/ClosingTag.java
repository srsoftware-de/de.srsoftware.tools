/* © SRSoftware 2024 */
package de.srsoftware.tools;

public class ClosingTag implements Result<String> {
	private final String token;

	private ClosingTag(String token) {
		this.token = token;
	}

	public static ClosingTag of(String token) {
		return new ClosingTag(token);
	}

	public boolean matches(Tag tag){
		if (tag == null) return false;
		return token.equals(tag.type());
	}

	public String token() {
		return token;
	}
}
