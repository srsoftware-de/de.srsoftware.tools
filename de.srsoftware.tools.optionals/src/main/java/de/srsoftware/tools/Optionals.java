/* © SRSoftware 2024 */
package de.srsoftware.tools;

import java.util.Optional;

public class Optionals {
	public static <T> Optional<T> nullable(T payload) {
		return Optional.ofNullable(payload);
	}
}
