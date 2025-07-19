/* © SRSoftware 2025 */
package de.srsoftware.tools.jdbc;

import java.util.Arrays;
import java.util.List;

/**
 * This class wraps an SQL condition usable with WHERE
 */
public class Condition {
	private final List<Object> values;
	private final String       sql;

	private Condition(String sql, Object... values) {
		this.sql    = sql;
		this.values = List.of(values);
	}

	/**
	 * create a …= x… condition
	 * @param value the value to compare against
	 * @return the built condition
	 */
	public static Condition equal(Object value) {
		return new Condition(" = ?", value);
	}

	/**
	 * create a IN (…) condition
	 * @param values the values to compare against
	 * @return the built condition
	 */
	public static Condition in(Object... values) {
		var marks = Arrays.stream(values).map(o -> "?").toList();
		return new Condition(" IN (%s)".formatted(String.join(", ", marks)), values);
	}

	/**
	 * create a IS NULL condition
	 * @return  the built condition
	 */
	public static Condition isNull(){
		return new Condition(" IS NULL");
	}

	/**
	 * create a …&lt; x… condition
	 * @param value the value to compare against
	 * @return the built condition
	 */
	public static Condition lessThan(Object value) {
		return new Condition(" < ?", value);
	}

	/**
	 * create a …&gt; x… condition
	 * @param value the value to compare against
	 * @return the built condition
	 */
	public static Condition moreThan(Object value) {
		return new Condition(" > ?", value);
	}

	/**
	 * create a …NOT IN (...)… condition
	 * @param values the values that shall be avoided
	 * @return the built condition
	 */
	public static Condition notIn(Object... values) {
		var marks = Arrays.stream(values).map(o -> "?").toList();
		return new Condition(" NOT IN (%s)".formatted(String.join(", ", marks)), values);
	}

	/**
	 * create a …LIKE… condition
	 * @param txt the text to compare against
	 * @return the built condition
	 */
	public static Condition like(String txt) {
		return new Condition(" LIKE ?", txt);
	}

	/**
	 * get the sql snippet for this WHERE condition
	 * @return the sql snippet for this WHERE condition
	 */
	public String sql() {
		return sql;
	}

	/**
	 * get the values assigned with this condition
	 * @return the values assigned with this condition
	 */
	public List<Object> values() {
		return values;
	}
}
