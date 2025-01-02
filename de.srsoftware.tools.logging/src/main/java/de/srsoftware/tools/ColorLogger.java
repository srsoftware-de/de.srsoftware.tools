/* © SRSoftware 2024 */
package de.srsoftware.tools;

import static de.srsoftware.tools.ConsoleColors.*;
import static java.lang.System.Logger.Level.*;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Provides colorful logging to System.out
 */
public class ColorLogger implements System.Logger {
	private static final DateFormat TIME	    = new SimpleDateFormat("hh:mm:ss.SSS");
	private static final DateFormat DATE	    = new SimpleDateFormat("yyyy-MM-dd");
	private static int        rootLevel = INFO.getSeverity();
	private static HashMap<String,Integer> instanceLevels = new HashMap<>();
	private static String     lastDate  = null;
	private final String      name;

	/**
	 * create a new ColorLogger with a given name
	 * @param name the name for this logger
	 */
	public ColorLogger(String name) {
		this.name = name;
	}

	/**
	 * crete a new Colorlogger, use the name of the class as logger name
	 * @param clazz a class
	 */
	public ColorLogger(Class<?> clazz) {
		this(clazz.getSimpleName());
	}

	private String colorize(String message, int severity) {
		var           color = severity >= ERROR.getSeverity() ? RED : severity >= WARNING.getSeverity() ? YELLOW : severity >= INFO.getSeverity() ? WHITE_BRIGHT : WHITE;
		var           date  = new Date();
		var           day   = DATE.format(date);
		StringBuilder sb    = new StringBuilder();
		if (!day.equals(lastDate)) {
			lastDate = day;
			sb.append(WHITE).append(day).append("\n");
		}
		return sb.append(WHITE).append(TIME.format(date))
				.append(" [").append(name).append("]: ")
				.append(color).append(message).append(RESET).toString();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isLoggable(Level level) {
		Integer loggerLevel = instanceLevels.get(name);
		if (loggerLevel == null) loggerLevel = rootLevel;
		return level.getSeverity() >= loggerLevel;
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
	 * alter the log level of all ColorLogger instances
	 * @param newLevel the new log level
	 */
	public static void setRootLogLevel(Level newLevel) {
		rootLevel = newLevel.getSeverity();
	}

	/**
	 * alter the log level for a specific logger
	 * @param loggerName the logger whose level is to update
	 * @param newLevel the new log level
	 */
	public static void setLogLevel(String loggerName, Level newLevel){
		int severity = newLevel.getSeverity();
		instanceLevels.put(loggerName,severity);
	}
}
