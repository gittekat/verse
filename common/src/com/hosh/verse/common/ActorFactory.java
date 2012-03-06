package com.hosh.verse.common;

import java.util.HashMap;
import java.util.Map;

import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

public class ActorFactory {
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
		final float radius = actorObj.getFloat(VerseActor.RADIUS);

		return new VerseActor(charId, x, y, radius);
	}

	public static ISFSObject createSFSObject(final VerseActor actor) {
		final ISFSObject actorData = new SFSObject();
		actorData.putInt(VerseActor.CHAR_ID, actor.getCharId());
		actorData.putFloat(VerseActor.POS_X, actor.getPos().x);
		actorData.putFloat(VerseActor.POS_Y, actor.getPos().y);
		actorData.putFloat(VerseActor.RADIUS, actor.getRadius());

		return actorData;
	}

}
