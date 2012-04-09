package com.hosh.verse.test.database;

import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import junit.framework.Assert;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.hosh.verse.common.Actor;
import com.hosh.verse.common.Stats;
import com.hosh.verse.server.database.DatabaseAccessor;
import com.hosh.verse.test.common.TestUtils;

public class DatabaseTest {
	private static Connection connection;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// final String dbUrl = "jdbc:mysql://127.0.0.1:3306/verse-db";
		//
		// try {
		// Class.forName("com.mysql.jdbc.Driver");
		// connection = DriverManager.getConnection(dbUrl, "root", "ishus109");
		// } catch (final SQLException e) {
		// fail("SQLException");
		// e.printStackTrace();
		// }
		connection = TestUtils.getDBConnection();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		connection.close();
	}

	@Test
	public void testBaseStatsTable() {
		System.out.println("Stats test");
		testTableClass(Stats.TABLE_NAME, DatabaseAccessor.loadBlueprint(connection, 1));
		System.out.println("________________________");
	}

	@Test
	public void testActorTable() {
		System.out.println("Actor table test");
		testTableClass(Actor.TABLE_NAME, DatabaseAccessor.loadActor(connection, 1));
		System.out.println("________________________");
	}

	@Test
	public void testSaveActor() {
		final Actor before = DatabaseAccessor.loadActor(connection, 1);

		final int newValue = before.getCurHp() + 1;
		before.setCurHp(newValue);
		DatabaseAccessor.saveActor(connection, before);

		final Actor after = DatabaseAccessor.loadActor(connection, 1);
		Assert.assertTrue(after.getCurHp() == newValue);
	}

	@Test
	public void testInsertAndDeleteActor() {
		// insert
		final Stats baseStats = DatabaseAccessor.loadBlueprint(connection, 1);
		final String name = "Millenium Falcon";
		final Actor actor = new Actor(null, "hosh", 1, baseStats, name, 0, 1000, 1090, 0, 5, 5, 0);
		final int id = DatabaseAccessor.saveActor(connection, actor);
		final Actor savedActor = DatabaseAccessor.loadActor(connection, id);
		Assert.assertTrue(savedActor.getName().equals(name));

		// delete
		DatabaseAccessor.deleteActor(connection, id);
		final Actor deletedActor = DatabaseAccessor.loadActor(connection, id);
		Assert.assertNull(deletedActor);
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
