/* © SRSoftware 2024 */
package de.srsoftware.tools;

import java.util.UUID;

/**
 * Utilities for String handling
 */
public class Strings {
	private Strings() {
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
