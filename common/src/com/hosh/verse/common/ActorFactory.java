package com.hosh.verse.common;

import java.util.HashMap;
import java.util.Map;

public class ActorFactory {
	private static Integer idCounter = 1;
	public static Map<Integer, VerseActor> actorList = new HashMap<Integer, VerseActor>();

	public static VerseActor createActor(final float posX, final float posY, final float radius) {
		final VerseActor actor = new VerseActor(idCounter, posX, posY, radius);
		actorList.put(idCounter, actor);

		idCounter++;

		return actor;
	}
}
