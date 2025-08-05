/* © SRSoftware 2025 */
package de.srsoftware.tools.jdbc;

import static de.srsoftware.tools.jdbc.Condition.*;
import static de.srsoftware.tools.jdbc.Query.Dialect.*;
import static de.srsoftware.tools.jdbc.Query.MARK;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;


public class QueryTest {
	@Test
	public void testSelectAll() {
		var query = Query.select("id", "title", "year").from("movies");
		assertEquals("SELECT id, title, year FROM movies", query.toString());
	}

	@Test
	void testJoin() {
		var query = Query
			.select("id", "title", "year", "firstname", "lastname")  //
			.from("movies")
			.leftJoin("id", "cast", "id");
		assertEquals("SELECT id, title, year, firstname, lastname FROM movies LEFT JOIN cast ON movies.id = cast.id", query.toString());
	}

	@Test
	void testLimit() {
		var query = Query.select("id", "title", "year").from("movies").limit(5);
		assertEquals("SELECT id, title, year FROM movies LIMIT 5", query.toString());
	}

	@Test
	void testSort() {
		var query = Query.select("id", "title", "year").from("movies").sort("year ASC", "title DESC");
		assertEquals("SELECT id, title, year FROM movies ORDER BY year ASC, title DESC", query.toString());
	}

	@Test
	void testFilter() {
		var query = Query.select("id", "title", "year").from("movies").where("year", Condition.in(2020, 2021));
		assertEquals("SELECT id, title, year FROM movies WHERE year IN (2020, 2021)", query.toString());
	}

	@Test
	void testMultiFilter() {
		var query = Query.select("id", "title", "year").from("movies").where("year", Condition.notIn(2020)).where("title", Condition.in("Wall:E")).where("year", Condition.notIn(2021, 2022));
		assertEquals("SELECT id, title, year FROM movies WHERE year NOT IN (2020) AND year NOT IN (2021, 2022) AND title IN (\"Wall:E\")", query.toString());
	}

	@Test
	void testLess() {
		var query = Query.select("id", "title", "year").from("movies").where("year", Condition.lessThan(2020));
		assertEquals("SELECT id, title, year FROM movies WHERE year < 2020", query.toString());
	}

	@Test
	void testMore() {
		var query = Query.select("id", "title", "year").from("movies").where("year", moreThan(2020));
		assertEquals("SELECT id, title, year FROM movies WHERE year > 2020", query.toString());
	}

	@Test
	void testNotIn() {
		var query = Query.select("id", "title", "year").from("movies").where("year", Condition.notIn(2020, 2021));
		assertEquals("SELECT id, title, year FROM movies WHERE year NOT IN (2020, 2021)", query.toString());
	}

	@Test
	void testCombined() {
		var query = Query  //
			.select("id", "title", "year", "firstname")
			.from("movies")
			.leftJoin("id", "cast", "movie")
			.where("year", notIn(1999, 1998))
			.where("year", moreThan(1990))
			.where("title", like("%Space%"))
			.sort("title ASC", "year DESC")
			.limit(5)
			.skip(5);
		assertEquals("SELECT id, title, year, firstname FROM movies LEFT JOIN cast ON movies.id = cast.movie WHERE year NOT IN (1999, 1998) AND year > 1990 AND title LIKE \"%Space%\" ORDER BY title ASC, year DESC LIMIT 5 OFFSET 5", query.toString());
	}

	@Test
	void testInsert() {
		var query = Query  //
			.insertInto("movies", "title", "year")
			.values("Per Anhalter", "2005")
			.values("2001: A Space Odyssey", "1968");
		assertEquals("INSERT INTO movies (title, year) VALUES (?, ?)", query.sql());
	}

	@Test
	void testInsertIgnore() {
		var query = Query  //
				.insertInto("movies", "title", "year")
				.ignoreDuplicates(MARIADB)
				.values("Per Anhalter", "2005")
				.values("2001: A Space Odyssey", "1968");
		assertEquals("INSERT IGNORE INTO movies (title, year) VALUES (?, ?)", query.sql());

		query = Query  //
				.insertInto("movies", "title", "year")
				.ignoreDuplicates(MYSQL)
				.values("Per Anhalter", "2005")
				.values("2001: A Space Odyssey", "1968");
		assertEquals("INSERT IGNORE INTO movies (title, year) VALUES (?, ?)", query.sql());

		query = Query  //
				.insertInto("movies", "title", "year")
				.ignoreDuplicates(SQLITE)
				.values("Per Anhalter", "2005")
				.values("2001: A Space Odyssey", "1968");
		assertEquals("INSERT OR IGNORE INTO movies (title, year) VALUES (?, ?)", query.sql());
	}

	@Test
	void testReplace() {
		var query = Query  //
				.replaceInto("movies", "title", "year")
				.values("Per Anhalter", "2005")
				.values("2001: A Space Odyssey", "1968");
		assertEquals("REPLACE INTO movies (title, year) VALUES (?, ?)", query.sql());
	}

	@Test
	void testReplaceIgnore() {
		var query = Query  //
				.replaceInto("movies", "title", "year")
				.ignoreDuplicates(MARIADB)
				.values("Per Anhalter", "2005")
				.values("2001: A Space Odyssey", "1968");
		assertEquals("REPLACE INTO movies (title, year) VALUES (?, ?)", query.sql());

		query = Query  //
				.replaceInto("movies", "title", "year")
				.ignoreDuplicates(MYSQL)
				.values("Per Anhalter", "2005")
				.values("2001: A Space Odyssey", "1968");
		assertEquals("REPLACE INTO movies (title, year) VALUES (?, ?)", query.sql());

		query = Query  //
				.replaceInto("movies", "title", "year")
				.ignoreDuplicates(SQLITE)
				.values("Per Anhalter", "2005")
				.values("2001: A Space Odyssey", "1968");
		assertEquals("REPLACE INTO movies (title, year) VALUES (?, ?)", query.sql());
	}

	@Test
	void testUpdate() {
		var query = Query.update("movies").set("title", "year").where("year", equal(MARK)).set("id").where("id", notIn(5));
		assertEquals("UPDATE movies SET title = args[0], year = args[1], id = args[3] WHERE year = args[2] AND id NOT IN (5)", query.toString());
	}

	@Test
	void testUpdateIgnore() {
		var query = Query.updateIgnore("movies").set("title", "year").where("year", equal(MARK)).set("id").where("id", notIn(5));
		assertEquals("UPDATE IGNORE movies SET title = args[0], year = args[1], id = args[3] WHERE year = args[2] AND id NOT IN (5)", query.toString());
	}

	@Test
	void testWithSQLite() throws SQLException {
		var url  = "jdbc:sqlite:/tmp/test.db";
		var conn = DriverManager.getConnection(url);
		conn.prepareStatement("CREATE TABLE IF NOT EXISTS movies (id INT PRIMARY KEY, year INT, title TEXT)").execute();
		// conn.prepareStatement("CREATE TABLE IF NOT EXISTS cast (id INT PRIMARY KEY, movie INT, firstname TEXT, lastname TEXT)").execute();
		conn.prepareStatement("DELETE FROM movies").execute();
		Query.insertInto("movies", "id", "year", "title").values(1, 2001, "first").values(2, 2002, "second").values(3, 2003, "third").execute(conn);
		var rs    = Query.select("*").from("movies").exec(conn);
		int count = 0;
		while (rs.next()) count++;
		rs.close();
		assertEquals(3, count);
		var query = Query.update("movies").set("year").where("year", notIn(5, MARK)).set("title").where("id", equal(MARK));
		assertEquals("UPDATE movies SET year = args[0], title = args[2] WHERE year NOT IN (5, args[1]) AND id = args[3]", query.fill());
		query.prepare(conn).apply(1995, 42, "Test", 3);

		rs = Query.select("*").from("movies").where("year", equal(1995)).exec(conn);
		assertTrue(rs.next());
		assertEquals("Test", rs.getString("title"));
		assertEquals(1995, rs.getLong("year"));
		assertFalse(rs.next());
		rs.close();
	}
}
