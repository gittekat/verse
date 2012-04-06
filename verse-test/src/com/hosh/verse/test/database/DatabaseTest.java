package com.hosh.verse.test.database;

import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class DatabaseTest {
	private static Connection connection;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		final String dbUrl = "jdbc:mysql://127.0.0.1:3306/verse-db";

		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(dbUrl, "root", "ishus109");

			final PreparedStatement psChars = connection.prepareStatement("SELECT charId, char_name FROM characters WHERE account_name=?");
			psChars.setString(1, "hosh");

			final ResultSet resChars = psChars.executeQuery();

			if (!resChars.first()) {
				// TODO create new character
				return;
			}

			final Map<String, String> charMap = new HashMap<String, String>();
			do {
				final String charId = resChars.getString("charId");
				final String char_name = resChars.getString("char_name");
				charMap.put(charId, char_name);
				System.out.println("[DEBUG:] room join: " + charId + " " + char_name);
			} while (resChars.next());

			// TODO character selection => for now just use the first char
			String charId = "";
			for (final Map.Entry<String, String> entry : charMap.entrySet()) {
				charId = entry.getKey();
			}

		} catch (final SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		connection.close();
	}

	@Test
	public void testBaseTypeTable() {
		try {
			final String table = "base_type";
			final int expectedColumnCount = 18;

			final PreparedStatement statement = connection.prepareStatement("DESCRIBE " + table);
			final ResultSet resSet = statement.executeQuery();

			if (!resSet.first()) {
				fail("no columns found in " + table);
			}

			int rowCount = 0;
			do {
				System.out.println(resSet.getString("Field"));
				rowCount++;
			} while (resSet.next());

			System.out.println(rowCount);
			Assert.assertTrue(rowCount == expectedColumnCount);

			DatabaseAccessor.loadBaseType(connection, 0);

		} catch (final SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
