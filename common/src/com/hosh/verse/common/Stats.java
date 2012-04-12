package com.hosh.verse.common;

import com.smartfoxserver.v2.protocol.serialization.SerializableSFSType;

public class Stats implements SerializableSFSType {
	private static final String PREFIX = "Stats";
	public static final String TABLE_NAME = "blueprints";

	public static final String DBID_STATS_ID = "id";
	public static final String DBID_STATS_NAME = "type_name";
	public static final String DBID_STATS_TPYE_ID = "type_id";
	public static final String DBID_STATS_MODEL_ID = "model_id";
	public static final String DBID_STATS_SCALE = "scale";
	public static final String DBID_STATS_EXTENSION_SLOTS = "extension_slots";
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
	public static final String DBID_STATS_ROTATION_SPEED = "rotation_speed";
	public static final String DBID_STATS_ATTACK = "attack";
	public static final String DBID_STATS_DEFENSE = "defense";
	public static final String DBID_STATS_FUEL_TANK = "fuel_tank";
	public static final String DBID_STATS_CARGO_SPACE = "cargo_space";

	public static final String SFSID_ID = PREFIX + DBID_STATS_ID;
	public static final String SFSID_NAME = PREFIX + DBID_STATS_NAME;
	public static final String SFSID_TYPE_ID = PREFIX + DBID_STATS_TPYE_ID;
	public static final String SFSID_MODEL_ID = PREFIX + DBID_STATS_MODEL_ID;
	public static final String SFSID_SCALE = PREFIX + DBID_STATS_SCALE;
	public static final String SFSID_AGGRO_RANGE = PREFIX + DBID_STATS_AGGRO;
	public static final String SFSID_COLOR_R = PREFIX + DBID_STATS_COLOR_R;
	public static final String SFSID_COLOR_G = PREFIX + DBID_STATS_COLOR_G;
	public static final String SFSID_COLOR_B = PREFIX + DBID_STATS_COLOR_B;
	public static final String SFSID_COLOR_A = PREFIX + DBID_STATS_COLOR_A;
	public static final String SFSID_COLLISION_RADIUS = PREFIX + DBID_STATS_COLLISION_RADIUS;
	public static final String SFSID_ATTACK_RANGE = PREFIX + DBID_STATS_ATTACK_RANGE;
	public static final String SFSID_HP = PREFIX + DBID_STATS_HP;
	public static final String SFSID_SHIELD = PREFIX + DBID_STATS_SHIELD;
	public static final String SFSID_SPEED = PREFIX + DBID_STATS_SPEED;
	public static final String SFSID_ROTATION_SPEED = PREFIX + DBID_STATS_ROTATION_SPEED;
	public static final String SFSID_ATTACK = PREFIX + DBID_STATS_ATTACK;
	public static final String SFSID_DEFENSE = PREFIX + DBID_STATS_DEFENSE;
	public static final String SFSID_EXTENSION_SLOTS = PREFIX + DBID_STATS_EXTENSION_SLOTS;
	public static final String SFSID_FUEL_TANK = PREFIX + DBID_STATS_FUEL_TANK;

	private int id;

	private String type_name;
	private int type_id;
	private int model_id;
	private int scale;
	private int extension_slots;
	private int aggro;
	private float color_r;
	private float color_g;
	private float color_b;
	private float color_a;
	private float collision_radius;
	private float attack_range;
	private int hp;
	private int fuel_tank;
	private int cargo_space;
	private int shield;
	private int speed;
	private int rotation_speed;
	private int attack;
	private int defense;

	private transient float squaredCollisionRadius;

	public Stats() {
		// empty constructor needed for SmartfoxSerialization
	}

	public Stats(final int id, final String name, final int type_id, final int model_id, final int scale, final int aggro,
			final float color_r, final float color_g, final float color_b, final float color_a, final float collision_radius,
			final float attack_range, final int hp, final int shield, final int speed, final int rotation_speed, final int attack,
			final int defense, final int extension_slots, final int fuel_tank, final int cargo_space) {
		this.id = id;
		this.type_name = name;
		this.type_id = type_id;
		this.model_id = model_id;
		this.scale = scale;
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
		this.rotation_speed = rotation_speed;
		this.attack = attack;
		this.defense = defense;
		this.extension_slots = extension_slots;
		this.fuel_tank = fuel_tank;
		this.cargo_space = cargo_space;

		setSquaredCollisionRadius(collision_radius);
	}

	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public String getType_name() {
		return type_name;
	}

	public void setType_name(final String type_name) {
		this.type_name = type_name;
	}

	public int getType_id() {
		return type_id;
	}

	public void setType_id(final int type_id) {
		this.type_id = type_id;
	}

	public int getModel_id() {
		return model_id;
	}

	public void setModel_id(final int model_id) {
		this.model_id = model_id;
	}

	public int getScale() {
		return scale;
	}

	public void setScale(final int scale) {
		this.scale = scale;
	}

	public int getExtension_slots() {
		return extension_slots;
	}

	public void setExtension_slots(final int extension_slots) {
		this.extension_slots = extension_slots;
	}

	public int getAggro() {
		return aggro;
	}

	public void setAggro(final int aggro) {
		this.aggro = aggro;
	}

	public float getColor_r() {
		return color_r;
	}

	public void setColor_r(final float color_r) {
		this.color_r = color_r;
	}

	public float getColor_g() {
		return color_g;
	}

	public void setColor_g(final float color_g) {
		this.color_g = color_g;
	}

	public float getColor_b() {
		return color_b;
	}

	public void setColor_b(final float color_b) {
		this.color_b = color_b;
	}

	public float getColor_a() {
		return color_a;
	}

	public void setColor_a(final float color_a) {
		this.color_a = color_a;
	}

	public float getCollision_radius() {
		return collision_radius;
	}

	public void setCollision_radius(final float collision_radius) {
		this.collision_radius = collision_radius;
		setSquaredCollisionRadius(collision_radius);
	}

	public float getSquaredCollisionRadius() {
		return squaredCollisionRadius;
	}

	private void setSquaredCollisionRadius(final float radius) {
		squaredCollisionRadius = radius * radius;
	}

	public float getAttack_range() {
		return attack_range;
	}

	public void setAttack_range(final float attack_range) {
		this.attack_range = attack_range;
	}

	public int getHp() {
		return hp;
	}

	public void setHp(final int hp) {
		this.hp = hp;
	}

	public int getFuel_tank() {
		return fuel_tank;
	}

	public void setFuel_tank(final int fuel_tank) {
		this.fuel_tank = fuel_tank;
	}

	public int getCargo_space() {
		return cargo_space;
	}

	public void setCargo_space(final int cargo_space) {
		this.cargo_space = cargo_space;
	}

	public int getShield() {
		return shield;
	}

	public void setShield(final int shield) {
		this.shield = shield;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(final int speed) {
		this.speed = speed;
	}

	public int getRotation_speed() {
		return rotation_speed;
	}

	public void setRotation_speed(final int rotation_speed) {
		this.rotation_speed = rotation_speed;
	}

	public int getAttack() {
		return attack;
	}

	public void setAttack(final int attack) {
		this.attack = attack;
	}

	public int getDefense() {
		return defense;
	}

	public void setDefense(final int defense) {
		this.defense = defense;
	}

}
