package com.hosh.verse.common;

public class StandardActor extends Actor {
	/** mob constructor */
	public StandardActor(final int id, final Stats baseStats, final int exp, final float x, final float y, final float heading) {
		super(id, baseStats, exp, x, y, heading);

		name = baseStats.getType_name();
		curHp = baseStats.getHp();
		curShield = baseStats.getShield();

	}
}
