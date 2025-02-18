/* © SRSoftware 2024 */
package de.srsoftware.tools.jdbc;


import java.security.InvalidParameterException;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.System.Logger.Level.*;

/**
 * Object that wraps an SQL query
 * @author Stephan Richter, 2021-2024
 */
public class Query {
	private static final System.Logger LOG = System.getLogger(Query.class.getSimpleName());
	/**
	 * this mark instance can be during construction of update queries
	 */
	public static final Mark MARK = new Mark();

	/**
	 * represents a delete statement
	 */
	public static class DeleteQuery {
		private String table;
		private final Map<String, List<Condition>> conditions = new HashMap<>();

		private DeleteQuery() {
		}

		/**
		 * set the table, from which to delete
		 * @param table the name of the table to delete from
		 * @return this DeleteQuery instance
		 */
		public DeleteQuery from(String table) {
			this.table = table;
			return this;
		}

		/**
		 * add a where condition
		 * @param field the field in which the condition is to be fulfilled
		 * @param condition the condition to be fulfilled
		 * @return the updated query object
		 */
		public DeleteQuery where(String field, Condition condition) {
			conditions.computeIfAbsent(field, k -> new ArrayList<>()).add(condition);
			return this;
		}

		/**
		 * run this query on the provided database connection
		 *
		 * @param conn the connection to use while running this query
		 * @return the prepared statement that was executed
		 * @throws SQLException if the execution of the query fails
		 */
		public boolean execute(Connection conn) throws SQLException {
			var values = new ArrayList<>();
			var sql    = new StringBuilder();
			sql.append("DELETE FROM ");
			sql.append(table);
			List<String> where = new ArrayList<>();
			for (var field : conditions.keySet()) {
				for (Condition sub : conditions.get(field)) {
					where.add(field + sub.sql());
					values.addAll(sub.values());
				}
			}
			if (!where.isEmpty()) {
				sql.append(" WHERE ");
				sql.append(String.join(" AND ", where));
			}
			var stmt = conn.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
			for (int i = 0; i < values.size(); i++) stmt.setObject(i + 1, values.get(i));
			return stmt.execute();
		}
	}

	/**
	 * This class is used to build INSERT INTO … queries
	 */
	public static class InsertQuery {
		private final String   table;
		private final boolean replace;
		private String[]       fields	 = null;
		private final List<Object[]> valueSets = new ArrayList<>();
		private boolean ignoreDuplicates = false;

		private InsertQuery(String table) {
			this(table,false);
		}

		private InsertQuery(String table, boolean replace){
			this.table = table;
			this.replace = replace;
		}

		/**
		 * run this query on the provided database connection
		 *
		 * @param conn the connection to use while running this query
		 * @return the prepared statement that was executed
		 * @throws SQLException if the execution of the query fails
		 */
		public PreparedStatement execute(Connection conn) throws SQLException {
			var stmt = conn.prepareStatement(sql(), Statement.RETURN_GENERATED_KEYS);
			LOG.log(DEBUG,this);
			conn.setAutoCommit(false);
			for (var arr : valueSets) {
				for (int i = 0; i < arr.length; i++) stmt.setObject(i + 1, arr[i]);
				stmt.execute();
			}
			conn.setAutoCommit(true);
			return stmt;
		}

		private InsertQuery fields(String[] fields) {
			this.fields = fields;
			return this;
		}

		/**
		 * set the ignore flag: duplicates during insert will not cause an error
		 * @return this InsertQuery object
		 */
		public InsertQuery ignoreDuplicates(){
			if (replace) {
				LOG.log(WARNING,"Ignore duplicates is ignored on REPLACE query!");
			} else {
				ignoreDuplicates = true;
			}
			return this;
		}

		/**
		 * generate the sql statement for this query
		 * @return sql query
		 */
		public String sql() {
			var marks = Arrays.stream(fields).map(field -> "?").collect(Collectors.joining(", "));
			var names = String.join(", ", Arrays.asList(fields));
			var verb = replace ? "REPLACE" : (ignoreDuplicates ? "INSERT IGNORE" : "INSERT");
			return "%s INTO %s (%s) VALUES (%s)".formatted(verb,table, names, marks);
		}


		@Override
		public String toString() {
			return sql();
		}

		/**
		 * add a set of values to the query.
		 * @param values the values to insert. must be in the same number and order as the fields provided when creating the query.
		 * @return the query
		 */
		public InsertQuery values(Object... values) {
			if (fields != null && fields.length != values.length) throw new InvalidParameterException("Number of values must match the number of fields!");
			valueSets.add(values);
			return this;
		}
	}

	/**
	 * This class can be used to create SELECT queries
	 */
	public static class SelectQuery {
		private final List<String> sort = new ArrayList<>();
		private final String[]	fields;
		private final StringBuilder	tables = new StringBuilder();
		private String lastTable;
		private Long limit;
		private final Map<String, List<Condition>> conditions = new HashMap<>();
		private Long skip;
		private final List<String> groupFields = new ArrayList<>();

		/**
		 * the fields to select
		 * @param fields the fields to select
		 */
		private SelectQuery(String[] fields) {
			this.fields = fields;
		}

		private String compile(List<Object> values) {
			var sb = new StringBuilder("SELECT ")  //
			             .append(String.join(", ", Arrays.asList(fields)))
			             .append(" ")
			             .append(tables);

			List<String> where = new ArrayList<>();
			for (var field : conditions.keySet()) {
				for (Condition sub : conditions.get(field)) {
					where.add(field + sub.sql());
					values.addAll(sub.values());
				}
			}
			if (!where.isEmpty()) {
				sb.append(" WHERE ");
				sb.append(String.join(" AND ", where));
			}
			if (!groupFields.isEmpty()) sb.append(" GROUP BY ").append(String.join(", ", groupFields));
			if (!sort.isEmpty()) sb.append(" ORDER BY ").append(String.join(", ", sort));
			if (limit != null) sb.append(" LIMIT ").append(limit);
			if (skip != null) sb.append(" OFFSET ").append(skip);
			return sb.toString();
		}

		/**
		 * execute this query
		 * @param conn the database connection to act on
		 * @return the resultset of this execution
		 * @throws SQLException if the request fails
		 */
		public ResultSet exec(Connection conn) throws SQLException {
			var values = new ArrayList<>();
			var sql    = compile(values);
			var stmt   = conn.prepareStatement(sql);
			for (int i = 0; i < values.size(); i++) stmt.setObject(i + 1, values.get(i));
			LOG.log(DEBUG, this::toString);
			return stmt.executeQuery();
		}

		private String fill(String sql, ArrayList<Object> values) {
			while (!values.isEmpty()) {
				var    o = values.removeFirst();
				String s = (o instanceof Number num) ? "" + num : "\"" + o + "\"";
				sql      = sql.replaceFirst("\\?", s);
			}
			return sql;
		}

		/**
		 * define the table to select from
		 * @param table the name of a table
		 * @return this query
		 */
		public SelectQuery from(String table) {
			tables.append("FROM ").append(table);
			lastTable = table;
			return this;
		}

		/**
		 * set fields to group by
		 * @param fields the fields of which group are built
		 * @return this query object
		 */
		public SelectQuery groupBy(String... fields) {
			groupFields.addAll(Arrays.asList(fields));
			return this;
		}

		/**
		 * define a left join with another table
		 * @param joiningColumn the column of the previous table to join with
		 * @param otherTable the name of the added table
		 * @param otherTableColumn the column of the added table to join with
		 * @return this query
		 */
		public SelectQuery leftJoin(String joiningColumn, String otherTable, String otherTableColumn) {
			if (lastTable == null) throw new RuntimeException("Left join without calling from(…) before!");
			tables  //
			    .append(" LEFT JOIN ")
			    .append(otherTable)
			    .append(" ON ")
			    .append(lastTable)
			    .append('.')
			    .append(joiningColumn)
			    .append(" = ")
			    .append(otherTable)
			    .append('.')
			    .append(otherTableColumn);
			lastTable = otherTable;
			return this;
		}

		/**
		 * restict the ResultSet to a given number of entries
		 * @param limit the maximum count of entries the result set shall contain
		 * @return this Query
		 */
		public SelectQuery limit(long limit) {
			this.limit = limit;
			return this;
		}

		/**
		 * do not return the first <em>count</em> elements of the ResultSet
		 * @param count the number of lines to skip
		 * @return this query
		 */
		public SelectQuery skip(long count) {
			this.skip = count;
			return this;
		}

		/**
		 * Sort the entries in the result set by the given fields.
		 * Modifiers as "ASC" or "DESC" may be used
		 * @param fields the fields to sort with
		 * @return this query
		 */
		public SelectQuery sort(String... fields) {
			sort.addAll(Arrays.asList(fields));
			return this;
		}


		@Override
		public String toString() {
			var values = new ArrayList<>();
			return fill(compile(values), values);
		}

		/**
		 * add a where condition
		 * @param field the field in which the condition is to be fulfilled
		 * @param condition the condition to be fulfilled
		 * @return the updated query object
		 */
		public SelectQuery where(String field, Condition condition) {
			conditions.computeIfAbsent(field, k -> new ArrayList<>()).add(condition);
			return this;
		}
	}

	/**
	 * Wrapper for prepared statement with metadata to extract data from input values passed by apply
	 */
	public static class PreparedUpdateQuery {
		private final PreparedStatement stmt;
		private final List<Object> conditionInputs;
		private final long         counter;
		private final List<Integer> fieldInputs;

		private PreparedUpdateQuery(PreparedStatement stmt, List<Integer> fieldInputs, List<Object> conditionInputs) {
			this.stmt	     = stmt;
			this.conditionInputs = conditionInputs;
			this.fieldInputs     = fieldInputs;
			counter	     = fieldInputs.size() + conditionInputs.stream().filter(o -> o instanceof Mark).count();
		}

		/**
		 * execute a database transaction:
		 * values are applied to the placeholders in the order the were presented during Query construction.
		 * @param values values to apply to the query
		 * @return this PreparedUpdateQuery (can be used to repeat the apply process)
		 * @throws SQLException if writing data fails
		 */
		public PreparedUpdateQuery apply(Object... values) throws SQLException {
			if (values.length != counter) throw new InvalidParameterException("apply(…) expected %s arguments, got %s!".formatted(counter, values.length));
			int index = 0;
			for (int fieldInputIndex : fieldInputs) {
				stmt.setObject(++index, values[fieldInputIndex]);
			}
			for (var obj : conditionInputs) {
				if (obj instanceof Mark mark) {
					stmt.setObject(++index, values[mark.position()]);
				} else {
					stmt.setObject(++index, obj);
				}
			}
			LOG.log(TRACE, () -> " → applying (" + String.join(", ", Arrays.stream(values).map(o -> "" + o).toList()) + ")");
			stmt.execute();
			return this;
		}
	}

	/**
	 * This class can be used to create UPDATE queries
	 */
	public static class UpdateQuery {
		private final String  table;
		private final boolean ignore;
		private       int     counter;
		private final List<String>  fields	        = new ArrayList<>();
		private final List<Integer> fieldInputs     = new ArrayList<>();
		private final List<String>  conditions      = new ArrayList<>();
		private final List<Object>  conditionInputs = new ArrayList<>();

		private UpdateQuery(String table, boolean ignore) {
			this.table  = table;
			this.ignore = ignore;
			counter     = 0;
		}

		private void addField(String field) {
			// note: nth field has name {field}
			fields.add(field + " = ?");
			// note input at {counter} goes into nth field
			fieldInputs.add(counter++);
		}

		/**
		 * create a SQL string like the sql()-method does, then add fixed values and information about argument positions
		 * @return the created string
		 */
		public String fill() {
			var sql = sql();
			var pos = 0;
			for (int index : fieldInputs) {	 // skip field inputs
				pos = sql.indexOf("?", pos + 1);
				if (pos < 0) return sql;
				sql = sql.substring(0, pos) + "args[" + index + "]" + sql.substring(pos + 1);
			}

			for (var obj : conditionInputs) {
				pos = sql.indexOf("?", pos + 1);
				if (pos < 0) return sql;
				if (obj instanceof Mark mark) {
					sql = sql.substring(0, pos) + "args[" + mark.position() + "]" + sql.substring(pos + 1);
				} else {
					sql = sql.substring(0, pos) + obj + sql.substring(pos + 1);
				}
			}
			return sql;
		}

		/**
		 * fix fields and conditions, create a prepared statement and prepare for data application
		 * @param conn the connection to act on
		 * @return an object with metadata to do acutal database transactions
		 * @throws SQLException if preparing the statement fails
		 */
		public PreparedUpdateQuery prepare(Connection conn) throws SQLException {
			LOG.log(DEBUG, () -> "preparing " + this);
			var stmt = conn.prepareStatement(sql());
			return new PreparedUpdateQuery(stmt, fieldInputs, conditionInputs);
		}

		/**
		 * adds fields that shall be set with this update query
		 * @param fields the fields to add
		 * @return this UpdateQuery object
		 */
		public UpdateQuery set(String... fields) {
			for (var field : fields) addField(field);
			return this;
		}

		/**
		 * create SQL string from table, fields and conditions
		 * @return compiled sql sting with question marks in place of the inputs
		 */
		public String sql() {
			var sb = new StringBuilder("UPDATE ");
			if (ignore) sb.append("IGNORE ");
			sb.append(table).append(" SET ");
			sb.append(String.join(", ", fields));

			if (!conditions.isEmpty()) sb.append(" WHERE ").append(String.join(" AND ", conditions));
			return sb.toString();
		}

		@Override
		public String toString() {
			return fill();
		}

		/**
		 * add a where condition
		 * @param field the field in which the condition is to be fulfilled
		 * @param condition the condition to be fulfilled
		 * @return the updated query object
		 */
		public UpdateQuery where(String field, Condition condition) {
			conditions.add(field + condition.sql());
			for (var val : condition.values()) {
				if (val instanceof Mark mark) {
					// take note: input at {counter} goes into nth condition input
					conditionInputs.add(mark.set(counter++));
				} else {
					// note: nth condition input has fixed value
					conditionInputs.add(val);
				}
			}
			return this;
		}
	}

	private Query() {
	}

	/**
	 * create a new DeleteQuery
	 * @return the created Query
	 */
	public static DeleteQuery delete() {
		return new DeleteQuery();
	}

	/**
	 * Create a new SelectQuery for the given fields.
	 * Needs to be followed by a call of .from(…)
	 * @param fields the fields to select
	 * @return the SelectQuery, which may be further manupilated.
	 */
	public static SelectQuery select(String... fields) {
		return new SelectQuery(fields);
	}

	/**
	 * create a new InsertQuery for the given fields in the given table
	 * @param table the table to insert into
	 * @param fields the fields to set
	 * @return the new InsertQuery
	 */
	public static InsertQuery insertInto(String table, String... fields) {
		return new InsertQuery(table).fields(fields);
	}

	public static InsertQuery replaceInto(String table, String... fields) {
		return new InsertQuery(table,true).fields(fields);
	}

	/**
	 * create a new UPDATE query
	 * @param table the table to apply updates on
	 * @return the query object
	 */
	public static UpdateQuery update(String table) {
		return new UpdateQuery(table, false);
	}

	/**
	 * create an UPDATE IGNORE query
	 * @param table the table to apply updates on
	 * @return the query object
	 */
	public static UpdateQuery updateIgnore(String table) {
		return new UpdateQuery(table, true);
	}
}
