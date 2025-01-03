/* © SRSoftware 2024 */
package de.srsoftware.tools;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class MimeType {
	public static final String MIME_DIA     = "image/dia";
	public static final String MIME_GIF     = "image/gif";
	public static final String MIME_HTML    = "text/html";
	public static final String MIME_JPG     = "image/jpeg";
	public static final String MIME_LATEX   = "text/x-tex";
	public static final String MIME_PDF     = "image/pdf";
	public static final String MIME_PNG     = "image/png";
	public static final String MIME_SVG     = "image/svg";
	public static final String MIME_TXT     = "text/plain";
	public static final String MIME_UNKNOWN = "content/unknown";
	public static final String MIME_XML     = "application/xml";

	public static String guessMime(URL url) {
		var parts     = url.toString().split("\\.");
		var extension = parts[parts.length - 1].toLowerCase();
		try {
			return switch (extension) {
				case "dia" -> MIME_DIA;
				case "gif" -> MIME_GIF;
				case "jpg", "jpeg" -> MIME_JPG;
				case "png" -> MIME_PNG;
				case "tex" -> MIME_LATEX;
				default -> url.openConnection().getContentType();
			};
		} catch (IOException e) {
			return MIME_UNKNOWN;
		}
	}

	public static String guessMime(File file){
		try {
			return guessMime(file.toURI().toURL());
		} catch (IOException e){
			return MIME_UNKNOWN;
		}
	}
}
