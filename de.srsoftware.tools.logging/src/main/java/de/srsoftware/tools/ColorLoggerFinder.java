/* © SRSoftware 2025 */
package de.srsoftware.tools;

import java.util.HashMap;
import java.util.Map;

/**
 * implements a LoggerFinder, that creates ColorLogger singletons per name
 */
public class ColorLoggerFinder extends System.LoggerFinder {
	private static final Map<String, ColorLogger> LOGGERS = new HashMap<>();

	/**
	 * create new instance
	 */
	public ColorLoggerFinder(){
		super();
	}

	@Override
	public System.Logger getLogger(String name, Module module) {
		return LOGGERS.computeIfAbsent(name, ColorLogger::new);
	}
}
