package com.hosh.verse;

import java.util.HashMap;
import java.util.Map;

public class ActorFactory {
	private static Integer idCounter = 1;
	public static Map<Integer, Actor> actorList = new HashMap<Integer, Actor>();

	public static Actor createActor(final float posX, final float posY, final float radius) {
		final Actor actor = new Actor(idCounter, posX, posY, radius);
		actorList.put(idCounter, actor);

		idCounter++;

		return actor;
	}
}
