package com.hosh.verse.common;

import com.badlogic.gdx.math.Vector2;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;
import com.hosh.verse.common.utils.VerseUtils;
import com.smartfoxserver.v2.protocol.serialization.SerializableSFSType;

public class Actor implements IPositionable, SerializableSFSType {
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

	public static final String SFSID_TARGET_X = PREFIX + "targetX";
	public static final String SFSID_TARGET_Y = PREFIX + "targetY";
	public static final String SFSID_CUR_SPEED = PREFIX + "curSpeed";

	private String owner;

	private int hero;
	private Integer id;
	private int blueprint;

	private String name;
	private int exp;
	private float heading;
	private int curHp;
	private int curShield;
	private int kills;

	private Stats baseStats;
	private Stats stats;
	// private boolean statsDirty = true;

	private float x;
	private float y;
	private float targetX;
	private float targetY;

	private float curSpeed;

	private float directionalVectorX;
	private float directionalVectorY;

	// TODO temp
	private transient EventBus eventBus;

	public EventBus getEventBus() {
		return eventBus;
	}

	public Actor() {
		// empty constructor needed for SmartfoxSerialization
	}

	public Actor(final Integer id, final String owner, final int hero, final Stats baseStats, final String name, final int exp,
			final float x, final float y, final float heading, final int curHp, final int curShield, final int kills) {
		Preconditions.checkArgument(baseStats != null);
		this.owner = owner;
		this.hero = hero;
		this.baseStats = baseStats;
		this.blueprint = baseStats.getId();
		this.id = id;
		this.name = name;

		this.exp = exp;
		this.x = x;
		this.y = y;
		setPos(x, y);
		setTargetPos(x, y);

		this.heading = heading;

		this.curHp = curHp;
		this.curShield = curShield;
		this.kills = kills;

		// TODO real heading!!!
		setDirectionalVector(1, 1);

		updateStats();

		setCurSpeed(0);
	}

	public Actor(final EventBus eventBus, final Integer id, final String owner, final int hero, final Stats baseStats, final String name,
			final int exp, final float x, final float y, final float heading, final int curHp, final int curShield, final int kills) {
		this(id, owner, hero, baseStats, name, exp, x, y, heading, curHp, curShield, kills);
		Preconditions.checkArgument(eventBus != null);
		this.eventBus = eventBus;
	}

	private void updateStats() {
		// TODO recalculate stats
		stats = baseStats;
	}

	public void update(final float deltaTime) {
		// position
		final Vector2 pos = getPos();
		final Vector2 targetVector = pos.cpy().sub(targetX, targetY);
		if (targetVector.len() > 1.f) {

			rotate(targetVector, deltaTime);

			final float deltaMovement = deltaTime * curSpeed;
			pos.add(getDirectionalVector().mul(deltaMovement));
			setX(pos.x);
			setY(pos.y);

			if (eventBus != null) {
				eventBus.post(this);
			}
		} else {
			// pos = targetPos;
			setX(targetX);
			setY(targetY);
			setCurSpeed(0);
		}
	}

	private void rotate(final Vector2 targetVector, final float deltaTime) {
		final Vector2 targetOri = targetVector.nor().mul(-1);
		final Vector2 directionalVector = getDirectionalVector();
		final double rotAngle = VerseUtils.vector2angle(directionalVector);
		final double targetAngle = VerseUtils.vector2angle(targetOri);

		double rotDiff = rotAngle - targetAngle;
		if (rotDiff > 180) {
			rotDiff -= 360;
		} else if (rotDiff < -180) {
			rotDiff += 360;
		}

		if (Math.abs(rotDiff) < 1.0f) {
			setDirectionalVector(targetOri);
			return;
		}

		final float rotDiffAngle = deltaTime * stats.getRotation_speed();
		if (rotDiff > 0.0f) {
			setDirectionalVector(directionalVector.rotate(-rotDiffAngle));
		} else {
			setDirectionalVector(directionalVector.rotate(rotDiffAngle));
		}

	}

	public float getDirectionalVectorX() {
		return directionalVectorX;
	}

	public void setDirectionalVectorX(final float directionalVectorX) {
		this.directionalVectorX = directionalVectorX;
	}

	public float getDirectionalVectorY() {
		return directionalVectorY;
	}

	public void setDirectionalVectorY(final float directionalVectorY) {
		this.directionalVectorY = directionalVectorY;
	}

	public Vector2 getDirectionalVector() {
		return new Vector2(directionalVectorX, directionalVectorY);
	}

	public void setDirectionalVector(final float x, final float y) {
		directionalVectorX = x;
		directionalVectorY = y;
	}

	public void setDirectionalVector(final Vector2 direction) {
		directionalVectorX = direction.x;
		directionalVectorY = direction.y;
	}

	public float getRotationAngle() {
		return (float) VerseUtils.vector2angle(directionalVectorX, directionalVectorY);
	}

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(final String owner) {
		this.owner = owner;
	}

	public int getHero() {
		return hero;
	}

	public void setHero(final int hero) {
		this.hero = hero;
	}

	public int getBlueprint() {
		return blueprint;
	}

	public void setBlueprint(final int blueprint) {
		this.blueprint = blueprint;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(final int exp) {
		this.exp = exp;
	}

	public Vector2 getPos() {
		return new Vector2(x, y);
	}

	public void setPos(final float x, final float y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public float getX() {
		return x;
	}

	public void setX(final float x) {
		this.x = x;
	}

	@Override
	public float getY() {
		return y;
	}

	public void setY(final float y) {
		this.y = y;
	}

	public float getTargetX() {
		return targetX;
	}

	public void setTargetX(final float targetX) {
		this.targetX = targetX;
	}

	public float getTargetY() {
		return targetY;
	}

	public void setTargetY(final float targetY) {
		this.targetY = targetY;
	}

	public Vector2 getTargetPos() {
		return new Vector2(targetX, targetY);
	}

	public void setTargetPos(final Vector2 targetPos) {
		targetX = targetPos.x;
		targetY = targetPos.y;
	}

	public void setTargetPos(final float x, final float y) {
		setTargetPos(new Vector2(x, y));
	}

	public float getHeading() {
		return heading;
	}

	public void setHeading(final float heading) {
		this.heading = heading;
	}

	public int getCurHp() {
		return curHp;
	}

	public void setCurHp(final int curHp) {
		this.curHp = curHp;
	}

	public int getCurShield() {
		return curShield;
	}

	public void setCurShield(final int curShield) {
		this.curShield = curShield;
	}

	public int getKills() {
		return kills;
	}

	public void setKills(final int kills) {
		this.kills = kills;
	}

	public Stats getBaseStats() {
		return baseStats;
	}

	public void setBaseStats(final Stats blueprint) {
		baseStats = blueprint;
		updateStats();
	}

	public Stats getStats() {
		return stats;
	}

	public float getCurSpeed() {
		return curSpeed;
	}

	public void setCurSpeed(final float curSpeed) {
		if (curSpeed < 0 || stats == null) {
			this.curSpeed = 0;
			return;
		}

		if (curSpeed > stats.getSpeed()) {
			this.curSpeed = stats.getSpeed();
			return;
		}

		this.curSpeed = curSpeed;
	}

	@Override
	public String toString() {
		return "Name:" + getName() + " Owner:" + getOwner() + " Pos:" + getPos() + " Hp:" + getCurHp() + " Shield:" + getCurShield();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (id == null ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Actor other = (Actor) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

}
