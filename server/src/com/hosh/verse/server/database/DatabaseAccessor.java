package com.hosh.verse.server.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.hosh.verse.common.Stats;
import com.hosh.verse.common.VerseActor;
import com.hosh.verse.server.Verse;
import com.hosh.verse.server.VerseExtension;
import com.smartfoxserver.v2.entities.User;

public class DatabaseAccessor {

	public static final String DBID_CHAR_ID = "charId";
	public static final String DBID_CHAR_NAME = "char_name";
	public static final String DBID_EXP = "exp";
	public static final String DBID_POS_X = "x";
	public static final String DBID_POS_Y = "y";
	public static final String HEADING = "heading";
	public static final String DBID_MAX_HP = "maxHp";
	public static final String DBID_CUR_HP = "curHp";
	public static final String DBID_COLLISION_RADIUS = "collision_radius";

	private static final String LOAD_BASE_TYPE_QUERY = "SELECT * FROM " + Stats.TABLE_NAME + " WHERE id=?";

	public static VerseActor loadActor(final Connection dbConnection, final String charId) {
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
			final int x = res.getInt(DBID_POS_X);
			final int y = res.getInt(DBID_POS_Y);
			final int heading = res.getInt(HEADING);
			final int maxHp = res.getInt(DBID_MAX_HP);
			final int curHp = res.getInt(DBID_CUR_HP);
			final float collision_radius = res.getInt(DBID_COLLISION_RADIUS);

			return new VerseActor(id, charName, exp, 0, maxHp, curHp, x, y, heading, collision_radius);

		} catch (final SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public static VerseActor loadHero(final Connection dbConnection, final String charId) {
		PreparedStatement stmt;
		try {
			stmt = dbConnection.prepareStatement("SELECT * FROM heroes WHERE charId=?");
			stmt.setString(1, charId);

			final ResultSet res = stmt.executeQuery();
			if (!res.first()) {
				return null;
			}

			final String charName = res.getString(DBID_CHAR_NAME);
			final int id = res.getInt(DBID_CHAR_ID);
			final int exp = res.getInt(DBID_EXP);
			final int x = res.getInt(DBID_POS_X);
			final int y = res.getInt(DBID_POS_Y);
			final int heading = res.getInt(HEADING);
			final int maxHp = res.getInt(DBID_MAX_HP);
			final int curHp = res.getInt(DBID_CUR_HP);
			final float collision_radius = res.getInt(DBID_COLLISION_RADIUS);

			// return new VerseActor(id, charName, exp, level, maxHp, curHp, x,
			// y, heading, collision_radius);

		} catch (final SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public static Stats loadBaseType(final Connection dbConnection, final String baseTypeId) {
		PreparedStatement stmt;
		try {
			stmt = dbConnection.prepareStatement(LOAD_BASE_TYPE_QUERY);
			stmt.setString(1, baseTypeId);

			final ResultSet res = stmt.executeQuery();
			if (!res.first()) {
				throw new SQLException("base_type not found");
			}

			final int id = res.getInt(Stats.DBID_STATS_ID);
			final String type_name = res.getString(Stats.DBID_STATS_NAME);
			final int type_id = res.getInt(Stats.DBID_STATS_TPYE_ID);
			final int model_id = res.getInt(Stats.DBID_STATS_MODEL_ID);
			final int scale = res.getInt(Stats.DBID_STATS_SCALE);
			final int affiliation = res.getInt(Stats.DBID_STATS_AFFILIATION);
			final int aggro = res.getInt(Stats.DBID_STATS_AGGRO);
			final float color_r = res.getFloat(Stats.DBID_STATS_COLOR_R);
			final float color_g = res.getFloat(Stats.DBID_STATS_COLOR_G);
			final float color_b = res.getFloat(Stats.DBID_STATS_COLOR_B);
			final float color_a = res.getFloat(Stats.DBID_STATS_COLOR_A);
			final float collision_radius = res.getInt(Stats.DBID_STATS_COLLISION_RADIUS);
			final int attack_range = res.getInt(Stats.DBID_STATS_ATTACK_RANGE);
			final int base_hp = res.getInt(Stats.DBID_STATS_HP);
			final int base_shield = res.getInt(Stats.DBID_STATS_SHIELD);
			final int base_speed = res.getInt(Stats.DBID_STATS_SPEED);
			final int base_attack = res.getInt(Stats.DBID_STATS_ATTACK);
			final int base_defense = res.getInt(Stats.DBID_STATS_DEFENSE);
			final int extension_slots = res.getInt(Stats.DBID_STATS_EXTENSION_SLOTS);
			final int fuel_tank = res.getInt(Stats.DBID_STATS_FUEL_TANK);

			return new Stats(id, type_name, type_id, model_id, scale, affiliation, aggro, color_r, color_g, color_b, color_a,
					collision_radius, attack_range, base_hp, base_shield, base_speed, base_attack, base_defense, extension_slots, fuel_tank);

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
