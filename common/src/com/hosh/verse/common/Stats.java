package com.hosh.verse.common;

public class Stats {
	public static final String TABLE_NAME = "base_types";

	public static final String DBID_STATS_ID = "id";
	public static final String DBID_STATS_NAME = "type_name";
	public static final String DBID_STATS_TPYE_ID = "type_id";
	public static final String DBID_STATS_MODEL_ID = "model_id";
	public static final String DBID_STATS_SCALE = "scale";
	public static final String DBID_STATS_EXTENSION_SLOTS = "extension_slots";
	public static final String DBID_STATS_AFFILIATION = "affiliation";
	public static final String DBID_STATS_AGGRO = "aggro";
	public static final String DBID_STATS_COLOR_R = "color_r";
	public static final String DBID_STATS_COLOR_G = "color_g";
	public static final String DBID_STATS_COLOR_B = "color_b";
	public static final String DBID_STATS_COLOR_A = "color_a";
	public static final String DBID_STATS_COLLISION_RADIUS = "collision_radius";
	public static final String DBID_STATS_ATTACK_RANGE = "attack_range";
	public static final String DBID_STATS_HP = "hp";
	public static final String DBID_STATS_SHIELD = "shield";
	public static final String DBID_STATS_SPEED = "speed";
	public static final String DBID_STATS_ATTACK = "attack";
	public static final String DBID_STATS_DEFENSE = "defense";
	public static final String DBID_STATS_FUEL_TANK = "fuel_tank";

	private static final String BASE_TYPE = "BaseType";
	public static final String SFSID_ID = BASE_TYPE + DBID_STATS_ID;
	public static final String SFSID_NAME = BASE_TYPE + DBID_STATS_NAME;
	public static final String SFSID_TYPE_ID = BASE_TYPE + DBID_STATS_TPYE_ID;
	public static final String SFSID_MODEL_ID = BASE_TYPE + DBID_STATS_MODEL_ID;
	public static final String SFSID_SCALE = BASE_TYPE + DBID_STATS_SCALE;
	public static final String SFSID_SOCIAL_RANGE = BASE_TYPE + DBID_STATS_AFFILIATION;
	public static final String SFSID_AGGRO_RANGE = BASE_TYPE + DBID_STATS_AGGRO;
	public static final String SFSID_COLOR_R = BASE_TYPE + DBID_STATS_COLOR_R;
	public static final String SFSID_COLOR_G = BASE_TYPE + DBID_STATS_COLOR_G;
	public static final String SFSID_COLOR_B = BASE_TYPE + DBID_STATS_COLOR_B;
	public static final String SFSID_COLOR_A = BASE_TYPE + DBID_STATS_COLOR_A;
	public static final String SFSID_COLLISION_RADIUS = BASE_TYPE + DBID_STATS_COLLISION_RADIUS;
	public static final String SFSID_ATTACK_RANGE = BASE_TYPE + DBID_STATS_ATTACK_RANGE;
	public static final String SFSID_HP = BASE_TYPE + DBID_STATS_HP;
	public static final String SFSID_SHIELD = BASE_TYPE + DBID_STATS_SHIELD;
	public static final String SFSID_SPEED = BASE_TYPE + DBID_STATS_SPEED;
	public static final String SFSID_ATTACK = BASE_TYPE + DBID_STATS_ATTACK;
	public static final String SFSID_DEFENSE = BASE_TYPE + DBID_STATS_DEFENSE;
	public static final String SFSID_EXTENSION_SLOTS = BASE_TYPE + DBID_STATS_EXTENSION_SLOTS;
	public static final String SFSID_FUEL_TANK = BASE_TYPE + DBID_STATS_FUEL_TANK;

	private final int id;
	private final String type_name;
	private final int type_id;
	private final int model_id;
	private final int scale;
	private final int extension_slots;
	private final int affiliation;
	private final int aggro;
	private final float color_r;
	private final float color_g;
	private final float color_b;
	private final float color_a;
	private final float collision_radius;
	private final float attack_range;
	private final int hp;
	private final int fuel_tank;
	private final int shield;
	private final int speed;
	private final int attack;
	private final int defense;

	public Stats(final int id, final String name, final int type_id, final int model_id, final int scale, final int social,
			final int aggro, final float color_r, final float color_g, final float color_b, final float color_a,
			final float collision_radius, final float attack_range, final int hp, final int shield, final int speed, final int attack,
			final int defense, final int extension_slots, final int fuel_tank) {
		this.id = id;
		this.type_name = name;
		this.type_id = type_id;
		this.model_id = model_id;
		this.scale = scale;
		this.affiliation = social;
		this.aggro = aggro;
		this.color_r = color_r;
		this.color_g = color_g;
		this.color_b = color_b;
		this.color_a = color_a;
		this.collision_radius = collision_radius;
		this.attack_range = attack_range;
		this.hp = hp;
		this.shield = shield;
		this.speed = speed;
		this.attack = attack;
		this.defense = defense;
		this.extension_slots = extension_slots;
		this.fuel_tank = fuel_tank;
	}

	public int getId() {
		return id;
	}

	public String getType_name() {
		return type_name;
	}

	public int getType_id() {
		return type_id;
	}

	public int getModel_id() {
		return model_id;
	}

	public int getScale() {
		return scale;
	}

	public int getAffiliation() {
		return affiliation;
	}

	public int getAggro() {
		return aggro;
	}

	public float getColor_r() {
		return color_r;
	}

	public float getColor_g() {
		return color_g;
	}

	public float getColor_b() {
		return color_b;
	}

	public float getColor_a() {
		return color_a;
	}

	public float getCollision_radius() {
		return collision_radius;
	}

	public float getAttack_range() {
		return attack_range;
	}

	public int getExtension_slots() {
		return extension_slots;
	}

	public int getFuel_tank() {
		return fuel_tank;
	}

	public int getHp() {
		return hp;
	}

	public int getShield() {
		return shield;
	}

	public int getSpeed() {
		return speed;
	}

	public int getAttack() {
		return attack;
	}

	public int getDefense() {
		return defense;
	}
}
