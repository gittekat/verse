package com.hosh.verse.common;

import com.badlogic.gdx.math.Vector2;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

public class Interpreter {
	public static final String SFS_CMD_INIT = "cmdInitialData";
	public static final String SFS_CMD_MOVEMENT = "cmdMoveData";

	public static final String SFS_OBJ_BLUEPRINTS = "objBlueprints";
	public static final String SFS_OBJ_PLAYER_DATA = "objPlayerData";
	public static final String SFS_OBJ_MOVEMENT_DATA = "objMoveData";
	public static final String SFS_OBJ_MOVEMENT_DATA_PLAYER = "objMovePlayer";

	@Deprecated
	public static VerseActor updateActor(final VerseActor actor, final ISFSObject actorObj) {
		final int charId = actorObj.getInt(VerseActor.CHAR_ID);

		final float x = actorObj.getFloat(VerseActor.POS_X);
		final float y = actorObj.getFloat(VerseActor.POS_Y);
		final float targetX = actorObj.getFloat(VerseActor.TARGET_POS_X);
		final float targetY = actorObj.getFloat(VerseActor.TARGET_POS_Y);
		final float radius = actorObj.getFloat(VerseActor.RADIUS);
		final float speed = actorObj.getFloat(VerseActor.SPEED);

		if (actor != null) {
			actor.setPos(new Vector2(x, y));
			actor.setTargetPos(new Vector2(targetX, targetY));
			actor.setRadius(radius);
			actor.setCurSpeed(speed);

			return null;
		} else {
			return new VerseActor(charId, x, y, targetX, targetY, radius, speed);
		}
	}

	@Deprecated
	public static ISFSObject actorToSFSObject(final Actor actor) {
		final ISFSObject actorData = new SFSObject();
		actorData.putUtfString(Actor.SFSID_OWNER, actor.getOwner());
		actorData.putInt(Actor.SFSID_ID, actor.getId());
		actorData.putInt(Actor.SFSID_BLUEPRINT, actor.getBlueprint());
		actorData.putInt(Actor.SFSID_HERO, actor.getHero());
		actorData.putUtfString(Actor.SFSID_NAME, actor.getName());
		actorData.putInt(Actor.SFSID_EXP, actor.getExp());
		actorData.putFloat(Actor.SFSID_X, actor.getX());
		actorData.putFloat(Actor.SFSID_Y, actor.getY());
		actorData.putFloat(Actor.SFSID_HEADING, actor.getHeading());
		actorData.putInt(Actor.SFSID_KILLS, actor.getKills());
		actorData.putInt(Actor.SFSID_CUR_HP, actor.getCurHp());
		actorData.putInt(Actor.SFSID_CUR_SHIELD, actor.getCurShield());

		return actorData;
	}

	@Deprecated
	public static Actor sfsobjectToActor(final SFSObject sfsObj, final Stats blueprint) {
		final String owner = sfsObj.getUtfString(Actor.SFSID_OWNER);
		final String name = sfsObj.getUtfString(Actor.SFSID_NAME);
		final float x = sfsObj.getFloat(Actor.SFSID_X);
		final float y = sfsObj.getFloat(Actor.SFSID_Y);
		final float heading = sfsObj.getFloat(Actor.SFSID_HEADING);
		final int id = sfsObj.getInt(Actor.SFSID_ID);
		final int blueprintId = sfsObj.getInt(Actor.SFSID_BLUEPRINT);
		final int hero = sfsObj.getInt(Actor.SFSID_HERO);
		final int exp = sfsObj.getInt(Actor.SFSID_EXP);
		final int kills = sfsObj.getInt(Actor.SFSID_KILLS);
		final int curHp = sfsObj.getInt(Actor.SFSID_CUR_HP);
		final int curShield = sfsObj.getInt(Actor.SFSID_CUR_SHIELD);

		assert blueprint.getId() == blueprintId;

		return new Actor(id, owner, hero, blueprint, name, exp, exp, y, heading, curHp, curShield, kills);
	}

	public static ISFSObject packMoveData(final float targetX, final float targetY, final float speed) {
		final ISFSObject moveData = new SFSObject();
		moveData.putFloat(Actor.SFSID_TARGET_X, targetX);
		moveData.putFloat(Actor.SFSID_TARGET_Y, targetY);
		moveData.putFloat(Actor.SFSID_CUR_SPEED, speed);
		return moveData;
	}
}
