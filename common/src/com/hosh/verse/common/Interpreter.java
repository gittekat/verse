package com.hosh.verse.common;

import java.util.HashMap;
import java.util.Map;

import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

public class Interpreter {
	private static Integer idCounter = 1;
	public static Map<Integer, VerseActor> actorList = new HashMap<Integer, VerseActor>();

	public static VerseActor createActor(final float posX, final float posY, final float radius) {
		final VerseActor actor = new VerseActor(idCounter, posX, posY, radius);
		actorList.put(idCounter, actor);

		idCounter++;

		return actor;
	}

	public static VerseActor createActor(final ISFSObject actorObj) {
		final int charId = actorObj.getInt(VerseActor.CHAR_ID);
		final float x = actorObj.getFloat(VerseActor.POS_X);
		final float y = actorObj.getFloat(VerseActor.POS_Y);
		final float targetX = actorObj.getFloat(VerseActor.TARGET_POS_X);
		final float targetY = actorObj.getFloat(VerseActor.TARGET_POS_Y);
		final float oriX = actorObj.getFloat(VerseActor.ORIENTATION_X);
		final float oriY = actorObj.getFloat(VerseActor.ORIENTATION_Y);
		final float radius = actorObj.getFloat(VerseActor.RADIUS);
		final float speed = actorObj.getFloat(VerseActor.SPEED);

		return new VerseActor(charId, x, y, targetX, targetY, radius, speed, oriX, oriY);
	}

	public static ISFSObject createSFSObject(final VerseActor actor) {
		final ISFSObject actorData = new SFSObject();
		actorData.putInt(VerseActor.CHAR_ID, actor.getCharId());
		actorData.putFloat(VerseActor.POS_X, actor.getPos().x);
		actorData.putFloat(VerseActor.POS_Y, actor.getPos().y);
		actorData.putFloat(VerseActor.TARGET_POS_X, actor.getTargetPos().x);
		actorData.putFloat(VerseActor.TARGET_POS_Y, actor.getTargetPos().y);
		actorData.putFloat(VerseActor.ORIENTATION_X, actor.getCurOrientation().x);
		actorData.putFloat(VerseActor.ORIENTATION_Y, actor.getCurOrientation().y);
		actorData.putFloat(VerseActor.RADIUS, actor.getRadius());
		actorData.putFloat(VerseActor.SPEED, actor.getCurSpeed());

		return actorData;
	}

}
