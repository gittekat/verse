package com.hosh.verse.test.common;

import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestUtils {
	public static Connection getDBConnection() {
		final String dbUrl = "jdbc:mysql://127.0.0.1:3306/verse-db";
		Connection connection = null;

		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(dbUrl, "root", "ishus109");
		} catch (final SQLException e) {
			fail("SQLException");
			e.printStackTrace();
		} catch (final ClassNotFoundException e) {
			fail("mysql driver not found");
			e.printStackTrace();
		}

		return connection;
	}
}
