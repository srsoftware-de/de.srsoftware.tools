/* © SRSoftware 2024 */
package de.srsoftware.tools.slf4j2syslog;

import java.util.HashMap;

/**
 * Creates a Logger factory for Slf4J2SysLog loggers
 */
public class LoggerFactory implements org.slf4j.ILoggerFactory{

	private HashMap<String,Logger> loggers = new HashMap<>();

	@Override
	public org.slf4j.Logger getLogger(String key) {
		var logger = loggers.get(key);
		if (logger == null) loggers.put(key,logger = new Logger(key));
		return logger;
	}
}
