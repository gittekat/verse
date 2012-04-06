package com.hosh.verse.test.database;

import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.hosh.verse.common.Stats;
import com.hosh.verse.server.database.DatabaseAccessor;

public class DatabaseTest {
	private static Connection connection;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		final String dbUrl = "jdbc:mysql://127.0.0.1:3306/verse-db";

		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(dbUrl, "root", "ishus109");
		} catch (final SQLException e) {
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

			final int expectedColumnCount = 20;

			final PreparedStatement statement = connection.prepareStatement("DESCRIBE " + Stats.TABLE_NAME);
			final ResultSet resSet = statement.executeQuery();

			if (!resSet.first()) {
				fail("no columns found in " + Stats.TABLE_NAME);
			}

			int rowCount = 0;
			final Stats baseType = DatabaseAccessor.loadBaseType(connection, "1");
			do {
				System.out.print(resSet.getString("Field") + ": ");
				Object value;
				try {
					value = PropertyUtils.getProperty(baseType, resSet.getString("Field"));
					System.out.println(value);
				} catch (final IllegalAccessException e) {
					e.printStackTrace();
				} catch (final InvocationTargetException e) {
					e.printStackTrace();
				} catch (final NoSuchMethodException e) {
					e.printStackTrace();
				}
				rowCount++;
			} while (resSet.next());

			System.out.println(rowCount);
			Assert.assertTrue(rowCount == expectedColumnCount);

		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}
}
