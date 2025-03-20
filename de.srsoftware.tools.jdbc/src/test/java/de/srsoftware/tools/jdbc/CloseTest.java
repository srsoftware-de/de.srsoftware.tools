/* © SRSoftware 2025 */
package de.srsoftware.tools.jdbc;

import static de.srsoftware.tools.jdbc.Query.insertInto;
import static java.text.MessageFormat.format;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CloseTest {
	private static final String TABLE = "foofoo";
	private static final String CREATE_TABLE = format("CREATE TABLE {0} (k INT NOT NULL PRIMARY KEY AUTO_INCREMENT, i INT, s TEXT)",TABLE);
	private static final String TRUNCATE = format("DELETE FROM {0}",TABLE);
	private static Connection connection;
	private static DB db;

	@BeforeAll
	public static void launchDb() throws Exception {
		DBConfigurationBuilder config = DBConfigurationBuilder.newBuilder();
		config.setPort(3307);
		db = DB.newEmbeddedDB(config.build());
		db.start();
		connection = DriverManager.getConnection("jdbc:mysql://localhost:3307/test", "root", "");
		var stmt = connection.prepareStatement(CREATE_TABLE);
		stmt.execute();
		stmt.close();
	}

	@AfterAll
	public static void tearDown() throws Exception {
		if (connection != null) connection.close();
		if (db != null)	db.stop();
	}

	@BeforeEach
	public void prepareDb() throws SQLException {
		var stmt = connection.prepareStatement(TRUNCATE);
		stmt.execute();
		stmt.close();
	}

	@Test
	public void testInsert() throws SQLException {
		var map = new HashMap<Integer,String>();
		var stmt = insertInto(TABLE, "i", "s").values(1, "test1").values(2, "test2").execute(connection);
		var rs = stmt.getGeneratedKeys();
		var meta = rs.getMetaData();
		var count = meta.getColumnCount();
		for (int i = 1; i <= count; i++) System.out.println(format("Column {0}: {1}",i,meta.getColumnName(i)));
		while (rs.next()) System.out.println(rs.getObject(1));
		rs.close();
		stmt.close();
		rs = Query.select("k","i","s").from(TABLE).exec(connection);
		while (rs.next()) map.put(rs.getInt("i"),rs.getString("s"));
		rs.getStatement().close();
		rs.close();
		assertEquals(Map.of(1,"test1",2,"test2"),map);
	}
}
