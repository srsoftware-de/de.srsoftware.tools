/* © SRSoftware 2024 */
package de.srsoftware.tools;

import java.io.File;
import java.io.IOException;

public class MimeType {
	public static final String MIME_DIA     = "image/dia";
	public static final String MIME_JPG     = "image/jpeg";
	public static final String MIME_LATEX   = "text/x-tex";
	public static final String MIME_PDF     = "image/pdf";
	public static final String MIME_PNG     = "image/png";
	public static final String MIME_SVG     = "image/svg";
	public static final String MIME_UNKNOWN = "content/unknown";

	public static String guessMime(File file) {
		var parts     = file.getName().split("\\.");
		var extension = parts[parts.length - 1].toLowerCase();
		try {
			return switch (extension) {
				case "dia" -> MIME_DIA;
				case "tex" -> MIME_LATEX;
				default -> file.toURI().toURL().openConnection().getContentType();
			};
		} catch (IOException e) {
			return MIME_UNKNOWN;
		}
	}
}
