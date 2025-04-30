/* © SRSoftware 2025 */
package de.srsoftware.tools;

import static de.srsoftware.tools.ConsoleColors.*;
import static java.lang.System.Logger.Level.*;
import static java.lang.Thread.currentThread;

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
	private static HashMap<String, Integer> instanceLevels = new HashMap<>();
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

	/**
	 * create a new ColorLogger, use the name of the provided object as logger name
	 * @param o the object from whose class the logger name is derived
	 */
	public ColorLogger(Object o){
		this(o.getClass().getSimpleName());
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

		var marker = markerFor(currentThread().threadId());
		return sb.append(WHITE).append(TIME.format(date)).append(marker)
				.append(WHITE).append(" [").append(name).append("]: ")
				.append(color).append(message).append(RESET).toString();
	}

	/**
	 * The Systemlogger may replace other loggers which are used with patterns that contain placeholders without index.
	 * To circumvent problems with these patterns, this method introduces indexes.
	 * @param pattern the original pattern
	 * @param args fillers to be inserted at the marks
	 * @return the filled text
	 */
	private String format(String pattern, Object...args){
		for (int i=0; i<args.length; i++){
			var key = "{"+i+"}";
			if (!pattern.contains(key)){
				var pos = pattern.indexOf("{}");
				if (pos>=0)	pattern = pattern.substring(0,pos)+key+pattern.substring(pos+2);
			}
		}
		return MessageFormat.format(pattern,args);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isLoggable(Level level) {
		Integer loggerLevel = instanceLevels.get(name); // search for exact match
		if (loggerLevel == null) {
			for (var entry : instanceLevels.entrySet()){ // search for partial match
				if (name.contains(entry.getKey())) {
					loggerLevel = entry.getValue();
					break;
				}
			}
		}
		if (loggerLevel == null) loggerLevel = rootLevel; // fallback
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
			if (params != null && params.length >0) format = format(format,params);
			System.out.println(colorize(format, level.getSeverity()));
			if (params != null && params.length>0 && params[params.length-1] instanceof Exception e) e.printStackTrace(System.err);
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	private synchronized String markerFor(long thread) {
		var color1 = switch ((int) ((thread / 16) % 16)){
			case 0 -> BLACK;
			case 1 -> RED;
			case 2 -> GREEN;
			case 3 -> BLUE;
			case 4 -> YELLOW;
			case 5 -> PURPLE;
			case 6 -> CYAN;
			case 7 -> WHITE;
			case 8 -> BLACK_BRIGHT;
			case 9 -> RED_BRIGHT;
			case 10 -> GREEN_BRIGHT;
			case 11 -> BLUE_BRIGHT;
			case 12 -> YELLOW_BRIGHT;
			case 13 -> PURPLE_BRIGHT;
			case 14 -> CYAN_BRIGHT;
			default -> WHITE_BRIGHT;
		};
		var color2 = switch ((int) (thread % 16)){
			case 0 -> BLACK;
			case 1 -> RED;
			case 2 -> GREEN;
			case 3 -> BLUE;
			case 4 -> YELLOW;
			case 5 -> PURPLE;
			case 6 -> CYAN;
			case 7 -> WHITE;
			case 8 -> BLACK_BRIGHT;
			case 9 -> RED_BRIGHT;
			case 10 -> GREEN_BRIGHT;
			case 11 -> BLUE_BRIGHT;
			case 12 -> YELLOW_BRIGHT;
			case 13 -> PURPLE_BRIGHT;
			case 14 -> CYAN_BRIGHT;
			default -> WHITE_BRIGHT;
		};
		return " "+color1+"█"+color2+"█";
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
	 * reset the settings for different loggers (set by setLogLevel(…,…))
	 */
	public static void reset() {
		instanceLevels.clear();
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
