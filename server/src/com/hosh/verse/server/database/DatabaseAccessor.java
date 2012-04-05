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
	public static final String DBID_COLLISION_RADIUS = "collision_radius";

	public static final String DBID_NPC_ID = "id";
	public static final String DBID_NPC_NAME = "name";
	public static final String DBID_NPC_TPYE_ID = "type_id";
	public static final String DBID_NPC_MODEL_ID = "model_id";
	public static final String DBID_NPC_SOCIAL = "social";
	public static final String DBID_NPC_AGGRO = "aggro";
	public static final String DBID_NPC_COLOR_R = "color_r";
	public static final String DBID_NPC_COLOR_G = "color_g";
	public static final String DBID_NPC_COLOR_B = "color_b";
	public static final String DBID_NPC_COLOR_A = "color_a";
	public static final String DBID_NPC_COLLISION_RADIUS = "collision_radius";
	public static final String DBID_NPC_ATTACK_RANGE = "attack_range";
	public static final String DBID_NPC_POS_X = "x";
	public static final String DBID_NPC_POS_Y = "y";
	public static final String DBID_NPC_HEADING = "heading";
	public static final String DBID_NPC_BASE_HP = "base_hp";
	public static final String DBID_NPC_BASE_SHIELD = "base_shield";
	public static final String DBID_NPC_BASE_SPEED = "base_speed";
	public static final String DBID_NPC_BASE_ATTACK = "base_attack";
	public static final String DBID_NPC_BASE_DEFENSE = "base_defense";
	public static final String DBID_NPC_EXTENSION_SLOTS = "extension_slots";

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
			final int level = res.getInt(DBID_LEVEL);
			final int x = res.getInt(DBID_POS_X);
			final int y = res.getInt(DBID_POS_Y);
			final int heading = res.getInt(HEADING);
			final int maxHp = res.getInt(DBID_MAX_HP);
			final int curHp = res.getInt(DBID_CUR_HP);
			final float collision_radius = res.getInt(DBID_COLLISION_RADIUS);

			return new VerseActor(id, charName, exp, level, maxHp, curHp, x, y, heading, collision_radius);

		} catch (final SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public VerseActor loadNPC(final Connection dbConnection, final String id) {
		PreparedStatement stmt;
		try {
			stmt = dbConnection.prepareStatement("SELECT * FROM npc WHERE id=?");
			stmt.setString(1, id);

			final ResultSet res = stmt.executeQuery();
			if (!res.first()) {
				return null;
			}

			final int npcId = res.getInt(DBID_NPC_ID);
			final String name = res.getString(DBID_NPC_NAME);
			final int typeId = res.getInt(DBID_NPC_TPYE_ID);
			final int modelId = res.getInt(DBID_NPC_MODEL_ID);
			final int socialRange = res.getInt(DBID_NPC_SOCIAL);
			final int aggroRange = res.getInt(DBID_NPC_AGGRO);
			final int colorR = res.getInt(DBID_NPC_COLOR_R);
			final int colorG = res.getInt(DBID_NPC_COLOR_G);
			final int colorB = res.getInt(DBID_NPC_COLOR_B);
			final int colorA = res.getInt(DBID_NPC_COLOR_A);
			final int collisionRadius = res.getInt(DBID_NPC_COLLISION_RADIUS);
			final int attackRange = res.getInt(DBID_NPC_ATTACK_RANGE);
			final int posX = res.getInt(DBID_NPC_POS_X);
			final int posY = res.getInt(DBID_NPC_POS_Y);
			final int heading = res.getInt(DBID_NPC_HEADING);
			final int baseHp = res.getInt(DBID_NPC_BASE_HP);
			final int baseShield = res.getInt(DBID_NPC_BASE_SHIELD);
			final int baseSpeed = res.getInt(DBID_NPC_BASE_SPEED);
			final int baseAttack = res.getInt(DBID_NPC_BASE_ATTACK);
			final int baseDefense = res.getInt(DBID_NPC_BASE_DEFENSE);
			final int extensionSlots = res.getInt(DBID_NPC_EXTENSION_SLOTS);

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
