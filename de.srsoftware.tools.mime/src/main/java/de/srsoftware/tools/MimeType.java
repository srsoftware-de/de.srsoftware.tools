/* © SRSoftware 2025 */
package de.srsoftware.tools;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class MimeType {
	public static final String MIME_DIA      = "image/dia";
	public static final String MIME_FORM_DATA = "multipart/form-data";
	public static final String MIME_FORM_URL = "application/x-www-form-urlencoded";
	public static final String MIME_GIF      = "image/gif";
	public static final String MIME_HTML     = "text/html";
	public static final String MIME_JPG      = "image/jpeg";
	public static final String MIME_JSON     = "application/json";
	public static final String MIME_LATEX    = "text/x-tex";
	public static final String MIME_PDF      = "image/pdf";
	public static final String MIME_PNG      = "image/png";
	public static final String MIME_SVG      = "image/svg";
	public static final String MIME_TXT      = "text/plain";
	public static final String MIME_UNKNOWN  = "content/unknown";
	public static final String MIME_XML      = "application/xml";

	/**
	 * discourage entity creation
	 */
	private MimeType(){

	}

	/**
	 * try to guess the mime type based on the url
	 * @param url the url used to guess the mime type
	 * @return the mime type string
	 */
	public static String guessMime(URL url) {
		var parts     = url.toString().split("\\.");
		var extension = parts[parts.length - 1].toLowerCase();
		try {
			return switch (extension) {
				case "dia" -> MIME_DIA;
				case "gif" -> MIME_GIF;
				case "jpg", "jpeg" -> MIME_JPG;
				case "json" -> MIME_JSON;
				case "htm", "html" -> MIME_HTML;
				case "pdf" -> MIME_PDF;
				case "png" -> MIME_PNG;
				case "svg" -> MIME_SVG;
				case "tex" -> MIME_LATEX;
				case "txt" -> MIME_TXT;
				case "xml" -> MIME_XML;
				default -> url.openConnection().getContentType();
			};
		} catch (IOException e) {
			return MIME_UNKNOWN;
		}
	}

	/**
	 * try to gues the mime type of a file
	 * @param file the file for which the mime type is requested
	 * @return the mime type string
	 */
	public static String guessMime(File file){
		try {
			return guessMime(file.toURI().toURL());
		} catch (IOException e){
			return MIME_UNKNOWN;
		}
	}
}
