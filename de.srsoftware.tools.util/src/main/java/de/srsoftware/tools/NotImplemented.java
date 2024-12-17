/* © SRSoftware 2024 */
package de.srsoftware.tools;

public class NotImplemented {
	public static RuntimeException notImplemented(String message) {
		return new RuntimeException("not implemented: %s".formatted(message));
	}
	public static RuntimeException notImplemented(Class<?> clazz, String method) {
		return new RuntimeException("not implemented: %s.%s".formatted(clazz.getSimpleName(), method));
	}
}
