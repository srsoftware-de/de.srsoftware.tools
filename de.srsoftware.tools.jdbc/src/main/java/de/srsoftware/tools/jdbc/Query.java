/* © SRSoftware 2024 */
package de.srsoftware.tools.jdbc;

import de.srsoftware.tools.Strings;
import java.sql.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static java.lang.System.Logger.Level.*;

/**
 * Object that wraps an SQL query
 * @author Stephan Richter, 2021-2024
 */
public class Query {
	private static final System.Logger LOG = System.getLogger(Query.class.getSimpleName());
	private final String	   query;
	private final SortedMap<String, HashSet<Object>> conditions = new TreeMap<>();
	private final List<String> order	            = new ArrayList<>();

	/**
	 * create a new Query object by serializing the passed object
	 * @param queryObject an object whose toString method delivers a valid SQL query
	 */
	public Query(final Object queryObject) {
		query = queryObject.toString();
	}

	/**
	 * add a condition to this query
	 * @param entry a map entry whose key acts as field selector and whose values are compared against
	 * @return the query string part
	 */
	protected static String condition(final Map.Entry<String, HashSet<Object>> entry) {
		final String key           = entry.getKey();
		final HashSet<Object> vals = entry.getValue();
		return key + " IN (" + vals.stream().map(v -> "?").collect(Collectors.joining(", ")) + ")";
	}

	/**
	 * build this query and execute it using the provided connection
	 * @param conn the connection on which the query is executed
	 * @return the ResultSet of the executed Query
	 * @throws SQLException if the query cannot be executed
	 */
	public ResultSet execute(final Connection conn) throws SQLException {
		try {
			final ResultSet rs = statement(conn).executeQuery();

			if (LOG.isLoggable(DEBUG)) {
				final ResultSetMetaData meta = rs.getMetaData();
				final int	        cnt  = meta.getColumnCount();
				for (int i = 1; i <= cnt; i++) {
					LOG.log(TRACE, "{0}:\t({1})\t{2}", i, meta.getColumnTypeName(i), meta.getColumnName(i));
				}
			}
			return rs;
		} catch (final SQLException e) {
			throw new SQLException(Strings.fill("{} failed", this), e);
		}
	}

	private String fill(String sql, final PreparedStatement p) throws SQLException {
		int idx = 1;
		for (final Entry<String, HashSet<Object>> entry : conditions.entrySet()) {
			for (final Object val : entry.getValue()) {
				if (p != null) p.setObject(idx++, val);
				sql = sql.replaceFirst("\\?", val instanceof Number ? val.toString() : "'" + val + "'");
			}
		}
		return sql;
	}

	/**
	 * create a new Query object from the given object
	 * @param queryObject an object whose toString method delivers a valid SQL query
	 * @return the created Query object
	 */
	public static Query of(Object queryObject) {
		return new Query(queryObject);
	}

	/**
	 * order the results of this query be the provided fields
	 * @param fields the list of fields to sort by
	 * @return the Query object
	 */
	public Query orderBy(final String... fields) {
		Collections.addAll(order, fields);
		return this;
	}


	/**
	 * create the SQL represented by this query
	 * @return the SQL string
	 */
	protected String sql() {
		String sql = query;
		if (!conditions.isEmpty()) {
			sql += sql.toLowerCase().contains("where") ? " AND " : " WHERE ";
			sql += conditions.entrySet().stream().map(Query::condition).collect(Collectors.joining(" AND "));
		}
		if (!order.isEmpty()) sql += " ORDER BY " + String.join(", ", order);
		return sql.trim();
	}

	/**
	 * create a PreparedStatement from this Query object
	 * @param conn use this connection to prepare the result
	 * @return the PreparedStatement build from this Query
	 * @throws SQLException if this Query cannot be translated in a PreparedStatement
	 */
	public PreparedStatement statement(final Connection conn) throws SQLException {
		String	        sql = sql();
		final PreparedStatement p   = conn.prepareStatement(sql);
		sql	            = fill(sql, p);
		LOG.log(DEBUG, "Prepared statement: {0}", sql);
		return p;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append("[");

		try {
			sb.append(query == null ? "undefined query" : fill(query, null));
		} catch (final SQLException ignored) {
		}

		sb.append("]");
		return sb.toString();
	}

	/**
	 * add a where condition
	 * @param key the field against whom the values shall be compared
	 * @param vals the values to look for
	 * @return this query
	 */
	public final Query where(final String key, final Object vals) {
		return whereCollection(key, vals instanceof final Collection<?> collection ? collection : Set.of(vals));
	}

	private Query whereCollection(final String key, final Collection<?> values) {
		final HashSet<Object> cond = conditions.computeIfAbsent(key, k -> new HashSet<>());
		cond.addAll(values);
		return this;
	}
}
