package com.hosh.verse.server.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hosh.verse.common.Actor;
import com.hosh.verse.common.Stats;
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
	private static final String LOAD_ALL_BLUEPRINTS_QUERY = "SELECT * FROM " + Stats.TABLE_NAME;
	private static final String LOAD_ACTOR_QUERY = "SELECT * FROM " + Actor.TABLE_NAME + " WHERE id=?";
	private static final String LOAD_ALL_ACTORS_QUERY = "SELECT * FROM " + Actor.TABLE_NAME;
	private static final String ACTOR_INSERT_QUERY = "INSERT INTO " + Actor.TABLE_NAME
			+ "(owner, name, blueprint, hero, exp, x, y, heading, curHp, curShield, kills) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String ACTOR_UPDATE_QUERY = "UPDATE "
			+ Actor.TABLE_NAME
			+ " SET owner = ?, name = ?, blueprint = ?, hero = ?, exp = ?, x = ?, y = ?, heading = ?, curHp = ?, curShield = ?, kills = ? WHERE id = ?";

	static Map<Integer, Stats> blueprintCache = new HashMap<Integer, Stats>();
	static Map<Integer, Actor> actorCache = new HashMap<Integer, Actor>();
	private static boolean blueprintsLoaded = false;
	private static boolean actorsLoaded = false;

	public static void preloadActors(final Connection dbConnection) {
		loadAllBlueprints(dbConnection);
		loadActors(dbConnection);
	}

	public static Actor loadActor(final Connection dbConnection, final int actorId) {
		if (actorCache.containsKey(actorId)) {
			return actorCache.get(actorId);
		}

		PreparedStatement stmt;
		try {
			stmt = dbConnection.prepareStatement(LOAD_ACTOR_QUERY);
			stmt.setInt(1, actorId);

			final ResultSet res = stmt.executeQuery();
			if (!res.first()) {
				return null;
			}

			return loadActor(dbConnection, res);

		} catch (final SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static Actor loadActor(final Connection dbConnection, final ResultSet res) {
		try {
			final int id = res.getInt(Actor.DBID_ID);

			if (actorCache.containsKey(id)) {
				return actorCache.get(id);
			}

			final String owner = res.getString(Actor.DBID_OWNER);
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

			Stats blueprintStats;
			if (blueprintCache.containsKey(blueprint)) {
				blueprintStats = blueprintCache.get(blueprint);
			} else {
				blueprintStats = loadBlueprint(dbConnection, blueprint);
			}

			final Actor actor = new Actor(id, owner, hero, blueprintStats, name, exp, x, y, heading, curHp, curShield, kills);

			actorCache.put(id, actor);

			return actor;

		} catch (final SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static List<Actor> loadActors(final Connection dbConnection) {
		final List<Actor> actorList = new ArrayList<Actor>();
		PreparedStatement stmt;
		try {
			stmt = dbConnection.prepareStatement(LOAD_ALL_ACTORS_QUERY);

			final ResultSet res = stmt.executeQuery();
			if (!res.first()) {
				return null;
			}

			do {
				actorList.add(loadActor(dbConnection, res));
			} while (res.next());

		} catch (final SQLException e) {
			e.printStackTrace();
		}

		actorsLoaded = true;
		return actorList;
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

	public static Stats loadBlueprint(final ResultSet res) {
		try {
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

			final Stats blueprintStats = new Stats(id, type_name, type_id, model_id, scale, aggro, color_r, color_g, color_b, color_a,
					collision_radius, attack_range, hp, shield, speed, rotation_speed, attack, defense, extension_slots, fuel_tank,
					cargo_space);

			blueprintCache.put(id, blueprintStats);

			return blueprintStats;

		} catch (final SQLException e) {
			e.printStackTrace();
		}

		return null;
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

			final Stats blueprintStats = loadBlueprint(res);

			return blueprintStats;

		} catch (final SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static List<Stats> loadAllBlueprints(final Connection dbConnection) {
		final List<Stats> blueprints = new ArrayList<Stats>();
		PreparedStatement stmt;
		try {
			stmt = dbConnection.prepareStatement(LOAD_ALL_BLUEPRINTS_QUERY);

			final ResultSet res = stmt.executeQuery();
			if (!res.first()) {
				throw new SQLException(Stats.TABLE_NAME + " not found");
			}

			do {
				blueprints.add(loadBlueprint(res));
			} while (res.next());

		} catch (final SQLException e) {
			e.printStackTrace();
		}

		blueprintsLoaded = true;
		return blueprints;
	}

	public static Map<Integer, Stats> getBlueprintCache(final Connection dbConnection) {
		if (!blueprintsLoaded) {
			loadAllBlueprints(dbConnection);
		}
		return blueprintCache;
	}

	public static Map<Integer, Actor> getActorCache(final Connection dbConnection) {
		if (!actorsLoaded) {
			loadActors(dbConnection);
		}
		return actorCache;
	}

	// TODO doesn't belong here
	public static void markAsPlayerControlled(final VerseExtension verseExt, final Verse verse, final Actor player, final User user) {
		verseExt.addUser(player.getId(), user);
		user.getSession().setProperty(VerseExtension.CHAR_ID, player.getId());
		verse.markAsPlayerControlled(player);
	}

	// TODO doesn't belong here
	public static void unmarkAsPlayerControlled(final VerseExtension verseExt, final Verse verse, final User user) {
		final Integer charId = verseExt.removeUser(user);
		verse.unmarkAsPlayerControlled(charId);
	}
}
