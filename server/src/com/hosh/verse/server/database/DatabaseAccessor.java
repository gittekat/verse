package com.hosh.verse.server.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.hosh.verse.common.VerseActor;

public class DatabaseAccessor {
	Connection dbConnection;

	public DatabaseAccessor(final Connection dbConnection) {
		this.dbConnection = dbConnection;
	}

	public VerseActor createActor(final int charId) {
		PreparedStatement stmt;
		try {

			stmt = dbConnection.prepareStatement("SELECT * FROM characters WHERE charId=?");
			stmt.setString(1, "" + charId);

			final ResultSet res = stmt.executeQuery();
			final String charName = res.getString("char_name");
			final int exp = res.getInt("exp");
			final int level = res.getInt("level");
			final int x = res.getInt("x");
			final int y = res.getInt("y");
			final int heading = res.getInt("heading");
			final int maxHp = res.getInt("maxHp");
			final int curHp = res.getInt("curHp");

			return new VerseActor(charId, charName, exp, level, maxHp, curHp, x, y, heading, 5.0f);

		} catch (final SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public Connection getDbConnection() {
		return dbConnection;
	}

}
