package com.hosh.verse.server.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.hosh.verse.common.Actor;
import com.hosh.verse.common.Stats;
import com.hosh.verse.common.VerseActor;
import com.hosh.verse.server.Verse;
import com.hosh.verse.server.VerseExtension;
import com.smartfoxserver.v2.entities.User;

public class DatabaseAccessor {
	public static final String TABLE_OWNERS = "owners";

	public static final String DBID_CHAR_ID = "charId";
	public static final String DBID_CHAR_NAME = "char_name";
	public static final String DBID_EXP = "exp";
	public static final String DBID_POS_X = "x";
	public static final String DBID_POS_Y = "y";
	public static final String HEADING = "heading";
	public static final String DBID_MAX_HP = "maxHp";
	public static final String DBID_CUR_HP = "curHp";
	public static final String DBID_COLLISION_RADIUS = "collision_radius";

	private static final String LOAD_BLUEPRINT_QUERY = "SELECT * FROM " + Stats.TABLE_NAME + " WHERE id=?";
	private static final String LOAD_ACTOR_QUERY = "SELECT * FROM " + Actor.TABLE_NAME + " WHERE id=?";
	private static final String ACTOR_INSERT_QUERY = "INSERT INTO " + Actor.TABLE_NAME
			+ "(owner, name, blueprint, hero, exp, x, y, heading, curHp, curShield, kills) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String ACTOR_UPDATE_QUERY = "UPDATE "
			+ Actor.TABLE_NAME
			+ " SET owner = ?, name = ?, blueprint = ?, hero = ?, exp = ?, x = ?, y = ?, heading = ?, curHp = ?, curShield = ?, kills = ? WHERE id = ?";

	public static VerseActor loadVerseActor(final Connection dbConnection, final String charId) {
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

	public static Actor loadActor(final Connection dbConnection, final int actorId) {
		PreparedStatement stmt;
		try {
			stmt = dbConnection.prepareStatement(LOAD_ACTOR_QUERY);
			stmt.setInt(1, actorId);

			final ResultSet res = stmt.executeQuery();
			if (!res.first()) {
				return null;
			}

			final String owner = res.getString(Actor.DBID_OWNER);
			final int id = res.getInt(Actor.DBID_ID);
			final int blueprint = res.getInt(Actor.DBID_BLUEPRINT);
			final int hero = res.getInt(Actor.DBID_HERO);
			final String name = res.getString(Actor.DBID_NAME);
			final int exp = res.getInt(Actor.DBID_EXP);
			final int x = res.getInt(Actor.DBID_X);
			final int y = res.getInt(Actor.DBID_Y);
			final int heading = res.getInt(Actor.DBID_HEADING);
			final int kills = res.getInt(Actor.DBID_KILLS);
			final int curHp = res.getInt(Actor.DBID_CURHP);
			final int curShield = res.getInt(Actor.DBID_CURSHIELD);

			final Stats blueprintStats = loadBlueprint(dbConnection, blueprint);

			return new Actor(id, owner, hero, blueprintStats, name, exp, x, y, heading, curHp, curShield, kills);

		} catch (final SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static int saveActor(final Connection dbConnection, final Actor actor) {
		final PreparedStatement stmt;
		try {
			if (actor.getId() == null) {
				// insert new
				stmt = dbConnection.prepareStatement(ACTOR_INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
			} else {
				// update
				stmt = dbConnection.prepareStatement(ACTOR_UPDATE_QUERY, Statement.RETURN_GENERATED_KEYS);
				stmt.setInt(12, actor.getId());
			}

			stmt.setString(1, actor.getOwner());
			stmt.setString(2, actor.getName());
			stmt.setInt(3, actor.getBlueprint());
			stmt.setInt(4, actor.getHero());
			stmt.setInt(5, actor.getExp());
			stmt.setFloat(6, actor.getX());
			stmt.setFloat(7, actor.getY());
			stmt.setFloat(8, actor.getHeading());
			stmt.setInt(9, actor.getCurHp());
			stmt.setInt(10, actor.getCurShield());
			stmt.setInt(11, actor.getKills());

			stmt.executeUpdate();

			final ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				actor.setId(rs.getInt(1));
			}

		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return actor.getId();
	}

	public static void deleteActor(final Connection dbConnection, final int id) {
		PreparedStatement stmt;
		try {
			stmt = dbConnection.prepareStatement("DELETE FROM " + Actor.TABLE_NAME + " WHERE id = ?");
			stmt.setInt(1, id);

			stmt.executeUpdate();

		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	private void prepareStatementForActor(final Actor actor, final PreparedStatement stmt) throws SQLException {
		stmt.setString(1, actor.getOwner());
		stmt.setString(2, actor.getName());
		stmt.setInt(3, actor.getBlueprint());
		stmt.setInt(4, actor.getHero());
		stmt.setInt(5, actor.getExp());
		stmt.setFloat(6, actor.getX());
		stmt.setFloat(7, actor.getY());
		stmt.setFloat(8, actor.getHeading());
		stmt.setInt(9, actor.getCurHp());
		stmt.setInt(10, actor.getCurShield());
		stmt.setInt(11, actor.getKills());
	}

	public static Stats loadBlueprint(final Connection dbConnection, final int baseTypeId) {
		PreparedStatement stmt;
		try {
			stmt = dbConnection.prepareStatement(LOAD_BLUEPRINT_QUERY);
			stmt.setInt(1, baseTypeId);

			final ResultSet res = stmt.executeQuery();
			if (!res.first()) {
				throw new SQLException(Stats.TABLE_NAME + " not found");
			}

			final int id = res.getInt(Stats.DBID_STATS_ID);
			final String type_name = res.getString(Stats.DBID_STATS_NAME);
			final int type_id = res.getInt(Stats.DBID_STATS_TPYE_ID);
			final int model_id = res.getInt(Stats.DBID_STATS_MODEL_ID);
			final int scale = res.getInt(Stats.DBID_STATS_SCALE);
			final int aggro = res.getInt(Stats.DBID_STATS_AGGRO);
			final float color_r = res.getFloat(Stats.DBID_STATS_COLOR_R);
			final float color_g = res.getFloat(Stats.DBID_STATS_COLOR_G);
			final float color_b = res.getFloat(Stats.DBID_STATS_COLOR_B);
			final float color_a = res.getFloat(Stats.DBID_STATS_COLOR_A);
			final float collision_radius = res.getInt(Stats.DBID_STATS_COLLISION_RADIUS);
			final int attack_range = res.getInt(Stats.DBID_STATS_ATTACK_RANGE);
			final int hp = res.getInt(Stats.DBID_STATS_HP);
			final int shield = res.getInt(Stats.DBID_STATS_SHIELD);
			final int speed = res.getInt(Stats.DBID_STATS_SPEED);
			final int rotation_speed = res.getInt(Stats.DBID_STATS_ROTATION_SPEED);
			final int attack = res.getInt(Stats.DBID_STATS_ATTACK);
			final int defense = res.getInt(Stats.DBID_STATS_DEFENSE);
			final int extension_slots = res.getInt(Stats.DBID_STATS_EXTENSION_SLOTS);
			final int fuel_tank = res.getInt(Stats.DBID_STATS_FUEL_TANK);
			final int cargo_space = res.getInt(Stats.DBID_STATS_CARGO_SPACE);

			return new Stats(id, type_name, type_id, model_id, scale, aggro, color_r, color_g, color_b, color_a, collision_radius,
					attack_range, hp, shield, speed, rotation_speed, attack, defense, extension_slots, fuel_tank,
					cargo_space);

		} catch (final SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static void addPlayer(final VerseExtension verseExt, final Verse verse, final VerseActor player, final User user) {
		verseExt.addPlayer(player.getCharId(), user);
		user.getSession().setProperty(VerseExtension.CHAR_ID, player.getCharId());
		verse.addPlayer(player);
	}

	public static void removePlayer(final VerseExtension verseExt, final Verse verse, final User user) {
		final Integer charId = verseExt.removePlayer(user);
		verse.removePlayer(charId);
	}
}
