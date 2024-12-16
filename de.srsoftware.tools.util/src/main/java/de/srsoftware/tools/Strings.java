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
	 * shorthand to create a new UUID
	 * @return a random uuid
	 */
	public static String uuid() {
		return UUID.randomUUID().toString();
	}
}
