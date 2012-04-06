package com.hosh.verse.common;

public class HeroActor extends Actor {
	private Stats stats;
	private boolean statsDirty = true;

	/** hero constructor */
	public HeroActor(final int id, final Stats baseStats, final String name, final int exp, final float x, final float y,
			final float heading, final int curHp, final int curShield) {
		super(id, baseStats, exp, x, y, heading);

		this.name = name;

		this.curHp = curHp;
		this.curShield = curShield;

		// TODO calc stats
		stats = baseStats;
	}
}
