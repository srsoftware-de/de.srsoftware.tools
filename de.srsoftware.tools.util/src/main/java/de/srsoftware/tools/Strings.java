/* © SRSoftware 2024 */
package de.srsoftware.tools;

import java.util.UUID;

/**
 * Utilities for String handling
 */
public class Strings {
	private Strings() {
	}

	public static String fill(final int indent, final String text, final Object... fills) {
		final StringBuilder sb = new StringBuilder(" ".repeat(indent));

		if (fills == null || fills.length < 1) return sb.append(text).toString();

		final var parts = text.split("\\{\\}", fills.length + 1);

		for (int i = 0; i < parts.length - 1; i++) {
			sb.append(parts[i]);
			sb.append(fills[i]);
		}

		sb.append(parts[parts.length - 1]);
		return sb.toString();
	}

	public static String fill(final String text, final Object... fills) {
		return fill(0, text, fills);
	}


	/**
	 * get a hexadecimal representation of the byte array
	 * @param bytes a byte array to convert to a hex string
	 * @return a string containing a hexadecimal representation of the given byte array
	 */
	public static String hex(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for (byte b : bytes) sb.append(String.format("%02x", b));
		return sb.toString();
	}


	/**
	 * shorthand to create a new UUID
	 * @return a random uuid
	 */
	public static String uuid() {
		return UUID.randomUUID().toString();
	}
}
