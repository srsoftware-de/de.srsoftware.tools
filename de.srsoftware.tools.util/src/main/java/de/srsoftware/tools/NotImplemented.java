/* © SRSoftware 2025 */
package de.srsoftware.tools;

/**
 * helper class to easily create no-implementd excaptions
 */
public class NotImplemented {
	private NotImplemented() {
		// discourage instatiation
	}

	/**
	 * create a runtime exception with a "not implemented: …" message
	 * @param message the message to append after the colon
	 * @return the created runtime exception
	 */
	public static RuntimeException notImplemented(String message) {
		return new RuntimeException("not implemented: %s".formatted(message));
	}

	/**
	 * create a runtime exception with a "not implemented: [class].[method]" message
	 * @param clazz the replacement for [class]
	 * @param method the replacement for [method]
	 * @return the created runtime exception
	 */
	public static RuntimeException notImplemented(Class<?> clazz, String method) {
		return new RuntimeException("not implemented: %s.%s".formatted(clazz.getSimpleName(), method));
	}

	/**
	 * create a runtime exception with a "not implemented: [class].[method]" message
	 * @param o the object whose class will be listed
	 * @param method the replacement for [method]
	 * @return the created runtime exception
	 */
	public static RuntimeException notImplemented(Object o, String method) {
		return new RuntimeException("not implemented: %s.%s".formatted(o.getClass().getSimpleName(), method));
	}
}
