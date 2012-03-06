package com.hosh.verse.server.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.hosh.verse.common.VerseActor;
import com.hosh.verse.server.Verse;
import com.hosh.verse.server.VerseExtension;
import com.smartfoxserver.v2.entities.User;

public class DatabaseAccessor {

	public static final String DBID_CHAR_ID = "charId";
	public static final String DBID_CHAR_NAME = "char_name";
	public static final String DBID_EXP = "exp";
	public static final String DBID_LEVEL = "level";
	public static final String DBID_POS_X = "x";
	public static final String DBID_POS_Y = "y";
	public static final String HEADING = "heading";
	public static final String DBID_MAX_HP = "maxHp";
	public static final String DBID_CUR_HP = "curHp";

	public static VerseActor createActor(final Connection dbConnection, final String charId) {
		PreparedStatement stmt;
		try {
			stmt = dbConnection.prepareStatement("SELECT * FROM characters WHERE charId=?");
			stmt.setString(1, charId);

			final ResultSet res = stmt.executeQuery();
			if (!res.first()) {
				return null;
			}

			final String charName = res.getString(DBID_CHAR_NAME);
			final int id = res.getInt(DBID_CHAR_ID);
			final int exp = res.getInt(DBID_EXP);
			final int level = res.getInt(DBID_LEVEL);
			final int x = res.getInt(DBID_POS_X);
			final int y = res.getInt(DBID_POS_Y);
			final int heading = res.getInt(HEADING);
			final int maxHp = res.getInt(DBID_MAX_HP);
			final int curHp = res.getInt(DBID_CUR_HP);

			return new VerseActor(id, charName, exp, level, maxHp, curHp, x, y, heading, 5.0f);

		} catch (final SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public static void addPlayer(final VerseExtension verseExt, final Verse verse, final VerseActor player, final User user) {
		// verseExt.getUserLookupTable().put(player.getCharId(), user);
		verseExt.addPlayer(player.getCharId(), user);
		user.getSession().setProperty(VerseExtension.CHAR_ID, player.getCharId());
		verse.addPlayer(player);
	}

	public static void removePlayer(final VerseExtension verseExt, final Verse verse, final User user) {
		// final Integer charId = (Integer)
		// user.getSession().getProperty(VerseExtension.CHAR_ID);
		// verseExt.getUserLookupTable().remove(charId);
		final Integer charId = verseExt.removePlayer(user);
		verse.removePlayer(charId);
	}
}
