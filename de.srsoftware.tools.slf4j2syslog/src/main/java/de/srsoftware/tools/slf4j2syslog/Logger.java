/* © SRSoftware 2024 */
package de.srsoftware.tools.slf4j2syslog;

import static java.lang.System.Logger.Level.*;

import org.slf4j.Marker;

/**
 * Provides a SL4J logger implementation, that uses System.Logger
 */
public class Logger implements org.slf4j.Logger{
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(Logger.class);
	private final System.Logger logger;

	/**
	 * Create a new Logger instance
	 * @param name the logger instance name
	 */
	public Logger(String name) {
		this.logger = System.getLogger(name);
	}

	@Override
	public String getName() {
		return logger.getName();
	}

	@Override
	public boolean isTraceEnabled() {
		return logger.isLoggable(TRACE);
	}

	@Override
	public void trace(String s) {
		logger.log(TRACE,s);
	}

	@Override
	public void trace(String s, Object o) {
		logger.log(TRACE,s,o);
	}

	@Override
	public void trace(String s, Object o, Object o1) {
		logger.log(TRACE,s,o,o1);
	}

	@Override
	public void trace(String s, Object... objects) {
		logger.log(TRACE,s,objects);
	}

	@Override
	public void trace(String s, Throwable throwable) {
		logger.log(TRACE,s,throwable);
	}

	@Override
	public boolean isTraceEnabled(Marker marker) {
		return isTraceEnabled();
	}

	@Override
	public void trace(Marker marker, String s) {
		trace(s);
	}

	@Override
	public void trace(Marker marker, String s, Object o) {
		trace(s,o);
	}

	@Override
	public void trace(Marker marker, String s, Object o, Object o1) {
		trace(s,o,o1);
	}

	@Override
	public void trace(Marker marker, String s, Object... objects) {
		trace(s,objects);
	}

	@Override
	public void trace(Marker marker, String s, Throwable throwable) {
		trace(s,throwable);
	}

	@Override
	public boolean isDebugEnabled() {
		return logger.isLoggable(DEBUG);
	}

	@Override
	public void debug(String s) {
		logger.log(DEBUG,s);
	}

	@Override
	public void debug(String s, Object o) {
		logger.log(DEBUG,s,o);
	}

	@Override
	public void debug(String s, Object o, Object o1) {
		logger.log(DEBUG,s,o,o1);
	}

	@Override
	public void debug(String s, Object... objects) {
		logger.log(DEBUG,s,objects);
	}

	@Override
	public void debug(String s, Throwable throwable) {
		logger.log(DEBUG,s,throwable);
	}

	@Override
	public boolean isDebugEnabled(Marker marker) {
		return isDebugEnabled();
	}

	@Override
	public void debug(Marker marker, String s) {
		debug(s);
	}

	@Override
	public void debug(Marker marker, String s, Object o) {
		debug(s,o);
	}

	@Override
	public void debug(Marker marker, String s, Object o, Object o1) {
		debug(s,o,o1);
	}

	@Override
	public void debug(Marker marker, String s, Object... objects) {
		debug(s,objects);
	}

	@Override
	public void debug(Marker marker, String s, Throwable throwable) {
		debug(s,throwable);
	}

	@Override
	public boolean isInfoEnabled() {
		return logger.isLoggable(INFO);
	}

	@Override
	public void info(String s) {
		logger.log(INFO,s);
	}

	@Override
	public void info(String s, Object o) {
		logger.log(INFO,s,o);
	}

	@Override
	public void info(String s, Object o, Object o1) {
		logger.log(INFO,s,o,o1);
	}

	@Override
	public void info(String s, Object... objects) {
		logger.log(INFO,s,objects);
	}

	@Override
	public void info(String s, Throwable throwable) {
		logger.log(INFO,s,throwable);
	}

	@Override
	public boolean isInfoEnabled(Marker marker) {
		return isInfoEnabled();
	}

	@Override
	public void info(Marker marker, String s) {
		info(s);
	}

	@Override
	public void info(Marker marker, String s, Object o) {
		info(s,o);
	}

	@Override
	public void info(Marker marker, String s, Object o, Object o1) {
		info(s,o,o1);
	}

	@Override
	public void info(Marker marker, String s, Object... objects) {
		info(s,objects);
	}

	@Override
	public void info(Marker marker, String s, Throwable throwable) {
		info(s,throwable);
	}

	@Override
	public boolean isWarnEnabled() {
		return logger.isLoggable(WARNING);
	}

	@Override
	public void warn(String s) {
		logger.log(WARNING,s);
	}

	@Override
	public void warn(String s, Object o) {
		logger.log(WARNING,s,o);
	}

	@Override
	public void warn(String s, Object... objects) {
		logger.log(WARNING,s,objects);
	}

	@Override
	public void warn(String s, Object o, Object o1) {
		logger.log(WARNING,s,o,o1);
	}

	@Override
	public void warn(String s, Throwable throwable) {
		logger.log(WARNING,s,throwable);
	}

	@Override
	public boolean isWarnEnabled(Marker marker) {
		return isWarnEnabled();
	}

	@Override
	public void warn(Marker marker, String s) {
		warn(s);
	}

	@Override
	public void warn(Marker marker, String s, Object o) {
		warn(s,o);
	}

	@Override
	public void warn(Marker marker, String s, Object o, Object o1) {
		warn(s,o,o1);
	}

	@Override
	public void warn(Marker marker, String s, Object... objects) {
		warn(s,objects);
	}

	@Override
	public void warn(Marker marker, String s, Throwable throwable) {
		warn(s,throwable);
	}

	@Override
	public boolean isErrorEnabled() {
		return logger.isLoggable(ERROR);
	}

	@Override
	public void error(String s) {
		logger.log(ERROR,s);
	}

	@Override
	public void error(String s, Object o) {
		logger.log(ERROR,s,o);
	}

	@Override
	public void error(String s, Object o, Object o1) {
		logger.log(ERROR,s,o,o1);
	}

	@Override
	public void error(String s, Object... objects) {
		logger.log(ERROR,s,objects);
	}

	@Override
	public void error(String s, Throwable throwable) {
		logger.log(ERROR,s,throwable);
	}

	@Override
	public boolean isErrorEnabled(Marker marker) {
		return isErrorEnabled();
	}

	@Override
	public void error(Marker marker, String s) {
		error(s);
	}

	@Override
	public void error(Marker marker, String s, Object o) {
		error(s,o);
	}

	@Override
	public void error(Marker marker, String s, Object o, Object o1) {
		error(s,o,o1);
	}

	@Override
	public void error(Marker marker, String s, Object... objects) {
		error(s,objects);
	}

	@Override
	public void error(Marker marker, String s, Throwable throwable) {
		error(s,throwable);
	}
}
