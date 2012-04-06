package com.hosh.verse.common;

public class Actor {
	private final Stats baseStats;
	private final int id;

	protected int exp;
	protected float x;
	protected float y;
	protected float heading;

	protected String name;
	protected int curHp;
	protected int curShield;

	public Actor(final int id, final Stats baseStats, final int exp, final float x, final float y, final float heading) {
		this.baseStats = baseStats;

		this.id = id; // spawn id
		name = baseStats.getType_name();
		curHp = baseStats.getHp();
		curShield = baseStats.getShield();

	}
}
