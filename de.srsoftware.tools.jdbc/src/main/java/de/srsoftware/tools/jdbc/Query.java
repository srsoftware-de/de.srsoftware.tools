/* © SRSoftware 2024 */
package de.srsoftware.tools.jdbc;

import static java.lang.System.Logger.Level.DEBUG;
import static java.lang.System.Logger.Level.INFO;

import de.srsoftware.logging.ColorLogger;
import java.lang.System.Logger;
import java.sql.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * @author Stephan Richter, 2021-2024
 */
public class Query {
	private static final Logger LOG = ColorLogger.of(Query.class);
	private final String        query;
	private final SortedMap<String, HashSet<Object>> conditions = new TreeMap<>();
	private final List<String> order	            = new ArrayList<>();

	public Query(final Object queryObject) {
		query = queryObject.toString();
	}

	public ResultSet execute(final Connection conn) throws SQLException {
		try {
			final ResultSet rs = statement(conn).executeQuery();

			if (LOG.isLoggable(DEBUG)) {
				final ResultSetMetaData meta = rs.getMetaData();
				final int	        cnt  = meta.getColumnCount();
				for (int i = 1; i <= cnt; i++) {
					LOG.log(DEBUG, "{}:\t({})\t{}", i, meta.getColumnTypeName(i), meta.getColumnName(i));
				}
			}
			return rs;
		} catch (final SQLException e) {
			throw new SQLException(Util.fill("{} failed", this), e);
		}
	}

	public String sql() {
		String sql = query;
		if (!conditions.isEmpty()) {
			sql += sql.toLowerCase().contains("where") ? " AND " : " WHERE ";
			sql += conditions.entrySet().stream().map(Query::condition).collect(Collectors.joining(" AND "));
		}
		if (!order.isEmpty()) sql += " ORDER BY " + String.join(", ", order);
		return sql.trim();
	}

	public static String condition(final Map.Entry<String, HashSet<Object>> entry) {
		final String key           = entry.getKey();
		final HashSet<Object> vals = entry.getValue();
		return key + " IN (" + vals.stream().map(v -> "?").collect(Collectors.joining(", ")) + ")";
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

	public Query orderBy(final String... fields) {
		Collections.addAll(order, fields);
		return this;
	}

	public PreparedStatement statement(final Connection conn) throws SQLException {
		String	        sql = sql();
		final PreparedStatement p   = conn.prepareStatement(sql);
		sql	            = fill(sql, p);
		LOG.log(INFO, "Prepared statement: {}", sql);
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

	private Query whereCollection(final String key, final Collection<?> values) {
		final HashSet<Object> cond = conditions.computeIfAbsent(key, k -> new HashSet<>());
		cond.addAll(values);
		return this;
	}

	public final Query where(final String key, final Object vals) {
		return whereCollection(key, vals instanceof final Collection<?> collection ? collection : Set.of(vals));
	}
}
