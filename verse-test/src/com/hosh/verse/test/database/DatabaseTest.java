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
import org.junit.BeforeClass;
import org.junit.Test;

import com.hosh.verse.common.Actor;
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
			fail("SQLException");
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		connection.close();
	}

	@Test
	public void testBaseStatsTable() {
		System.out.println("Stats test");
		testTableClass(Stats.TABLE_NAME, DatabaseAccessor.loadBlueprint(connection, 1));
		System.out.println();
	}

	@Test
	public void testActorTable() {
		System.out.println("Actor table test");
		testTableClass(Actor.TABLE_NAME, DatabaseAccessor.loadActor(connection, 1));
		System.out.println();
	}

	public void testTableClass(final String tableName, final Object sampleInstance) {
		try {
			final PreparedStatement statement = connection.prepareStatement("DESCRIBE " + tableName);
			final ResultSet resSet = statement.executeQuery();

			if (!resSet.first()) {
				fail("no columns found in " + tableName);
			}

			int rowCount = 0;
			do {
				System.out.print(resSet.getString("Field") + ": ");
				Object value;
				try {
					value = PropertyUtils.getProperty(sampleInstance, resSet.getString("Field"));
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

		} catch (final SQLException e) {
			fail("SQLException");
			e.printStackTrace();
		}
	}
}
