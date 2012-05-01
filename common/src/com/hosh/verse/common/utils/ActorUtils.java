package com.hosh.verse.common.utils;

import com.hosh.verse.common.Actor;
import com.hosh.verse.common.MovementData;
import com.hosh.verse.common.Stats;

public class ActorUtils {

	public static Actor createUnidentifiedActor() {
		return new Actor(0, "unknown", 0, new Stats(0, "unscanned", 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0), "unscanned",
				0, 0, 0, 0, 1, 0, 0);
	}

	public static void updateActor(final Actor actor, final MovementData movementData) {
		actor.setX(movementData.getPosX());
		actor.setY(movementData.getPosY());
		actor.setTargetX(movementData.getTargetPosX());
		actor.setTargetY(movementData.getTargetPosY());
		actor.setCurSpeed(movementData.getSpeed());
	}

	public static Actor createUnidentifiedActor(final MovementData movementData) {
		final Actor actor = new Actor(0, "unknown", 0,
				new Stats(0, "unscanned", 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 20, 20, 0, 0, 0, 0, 0), "unscanned", 0,
				movementData.getPosX(), movementData.getPosY(), 0, 1, 0, 0);
		actor.setTargetPos(movementData.getTargetPosX(), movementData.getTargetPosY());
		actor.setCurSpeed(movementData.getSpeed());
		return actor;
	}
}
