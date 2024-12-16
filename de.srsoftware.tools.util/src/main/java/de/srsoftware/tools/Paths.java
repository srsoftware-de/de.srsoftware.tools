/* © SRSoftware 2024 */
package de.srsoftware.tools;

import java.io.File;
import java.nio.file.Path;

/**
 * Utilities for working with application configuration
 */
public class Paths {
	private Paths() {
	}

	/**
	 * get a path to the config dir of a given application
	 * @param applicationName the name of the application
	 * @return &lt;user.home&gt;/.config/&lt;applicationName&gt;
	 */
	public static Path configDir(String applicationName) {
		String home = System.getProperty("user.home");
		return Path.of(home, ".config", applicationName);
	}

	/**
	 * get a path to the config dir of a given application
	 * @param clazz the application's main class
	 * @return &lt;user.home&gt;/.config/&lt;clazz.simplename&gt;
	 */
	public static Path configDir(Class clazz) {
		return configDir(clazz.getSimpleName());
	}

	/**
	 * get a path to the config dir of a given application
	 * @param clazz the application's main class
	 * @return &lt;user.home&gt;/.config/&lt;clazz.simplename&gt;
	 */
	public static Path configDir(Object clazz) {
		return configDir(clazz.getClass());
	}

	/**
	 * get the extension part of a file name
	 * @param file the file to tokenize
	 * @return the token after the last dot or the whole string if the filename contains no dot
	 */
	public static String extension(File file) {
		var parts = file.getName().split("\\.");
		return parts.length == 1 ? "" : parts[parts.length - 1];
	}
}
