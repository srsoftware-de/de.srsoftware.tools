/* © SRSoftware 2024 */
package de.srsoftware.tools;

import static de.srsoftware.tools.ConsoleColors.*;
import static java.lang.System.Logger.Level.*;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Provides colorful logging to System.out
 */
public class ColorLogger implements System.Logger {
	private final String      name;
	private static int        rootLevel = INFO.getSeverity();
	private static final DateFormat TIME	    = new SimpleDateFormat("hh:mm:ss.SSS");
	private static final DateFormat DATE	    = new SimpleDateFormat("yyyy-MM-dd");
	private static String     lastDate  = null;

	/**
	 * create a new ColorLogger with a given name
	 * @param name the name for this logger
	 */
	public ColorLogger(String name) {
		this.name = name;
	}

	private static String colorize(String message, int severity) {
		var           color = severity >= ERROR.getSeverity() ? RED : severity >= WARNING.getSeverity() ? YELLOW : severity >= INFO.getSeverity() ? WHITE_BRIGHT : WHITE;
		var           date  = new Date();
		var           day   = DATE.format(date);
		StringBuilder sb    = new StringBuilder();
		if (!day.equals(lastDate)) {
			lastDate = day;
			sb.append(WHITE).append(day).append("\n");
		}
		return sb.append(WHITE).append(TIME.format(date)).append(" ").append(color).append(message).append(RESET).toString();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isLoggable(Level level) {
		return level.getSeverity() >= rootLevel;
	}

	@Override
	public void log(Level level, ResourceBundle bundle, String msg, Throwable thrown) {
		if (isLoggable(level)) {
			System.out.println(colorize(msg, level.getSeverity()));
			thrown.printStackTrace(System.err);
		}
	}

	@Override
	public void log(Level level, ResourceBundle bundle, String format, Object... params) {
		if (isLoggable(level)) try {
			if (params != null && params.length >0) format = MessageFormat.format(format,params);
			System.out.println(colorize(format, level.getSeverity()));
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	/**
	 * Conveniance method to create a new ColorLogger
	 * @param clazz SimpleName of this class will be used as name for the ColorLogger
	 * @return the created ColorLogger
	 */
	public static ColorLogger of(Class<?> clazz) {
		return new ColorLogger(clazz.getSimpleName());
	}

	/**
	 * alte the log level of this logger
	 * @param level the new log level
	 * @return this ColorLogger instance
	 */
	public ColorLogger setLogLevel(Level level) {
		rootLevel = level.getSeverity();
		return this;
	}
}
