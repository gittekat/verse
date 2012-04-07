package com.hosh.verse.common;

public class Actor {
	private static final String PREFIX = "Actor";
	public static final String TABLE_NAME = "actors";

	public static final String DBID_OWNER = "owner";
	public static final String DBID_ID = "id";
	public static final String DBID_BLUEPRINT = "blueprint";
	public static final String DBID_HERO = "hero";
	public static final String DBID_NAME = "name";
	public static final String DBID_EXP = "exp";
	public static final String DBID_X = "x";
	public static final String DBID_Y = "y";
	public static final String DBID_HEADING = "heading";
	public static final String DBID_KILLS = "kills";
	public static final String DBID_CURHP = "curHp";
	public static final String DBID_CURSHIELD = "curShield";

	public static final String SFSID_OWNER = PREFIX + DBID_OWNER;
	public static final String SFSID_ID = PREFIX + DBID_ID;
	public static final String SFSID_BLUEPRINT = PREFIX + DBID_BLUEPRINT;
	public static final String SFSID_HERO = PREFIX + DBID_HERO;
	public static final String SFSID_NAME = PREFIX + DBID_NAME;
	public static final String SFSID_EXP = PREFIX + DBID_EXP;
	public static final String SFSID_X = PREFIX + DBID_X;
	public static final String SFSID_Y = PREFIX + DBID_Y;
	public static final String SFSID_HEADING = PREFIX + DBID_HEADING;
	public static final String SFSID_KILLS = PREFIX + DBID_KILLS;
	public static final String SFSID_CUR_HP = PREFIX + DBID_CURHP;
	public static final String SFSID_CUR_SHIELD = PREFIX + DBID_CURSHIELD;

	private String owner;

	public String getOwner() {
		return owner;
	}

	public int getHero() {
		return hero;
	}

	public int getId() {
		return id;
	}

	public int getBlueprint() {
		return blueprint;
	}

	public String getName() {
		return name;
	}

	public int getExp() {
		return exp;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getHeading() {
		return heading;
	}

	public int getCurHp() {
		return curHp;
	}

	public int getCurShield() {
		return curShield;
	}

	public int getKills() {
		return kills;
	}

	public Stats getBaseStats() {
		return baseStats;
	}

	public Stats getStats() {
		return stats;
	}

	public void setOwner(final String owner) {
		this.owner = owner;
	}

	public void setHero(final int hero) {
		this.hero = hero;
	}

	public void setExp(final int exp) {
		this.exp = exp;
	}

	public void setHeading(final float heading) {
		this.heading = heading;
	}

	public void setCurHp(final int curHp) {
		this.curHp = curHp;
	}

	public void setCurShield(final int curShield) {
		this.curShield = curShield;
	}

	public void setKills(final int kills) {
		this.kills = kills;
	}

	private int hero;
	private final int id;
	private final int blueprint;

	private final String name;
	private int exp;
	private float x;
	private float y;
	private float heading;
	private int curHp;
	private int curShield;
	private int kills;

	private final Stats baseStats;
	private Stats stats;
	private boolean statsDirty = true;

	public Actor(final int id, final String owner, final int blueprint, final int hero, final Stats baseStats, final String name,
			final int exp, final float x, final float y, final float heading, final int curHp, final int curShield, final int kills) {
		this.owner = owner;
		this.hero = hero;
		this.blueprint = blueprint;
		this.baseStats = baseStats;
		this.id = id;
		this.name = name;

		this.curHp = curHp;
		this.curShield = curShield;
		this.kills = kills;

		// TODO calc stats
		stats = baseStats;
	}
}
