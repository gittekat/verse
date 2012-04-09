package com.hosh.verse.common;

import com.badlogic.gdx.math.Vector2;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;
import com.hosh.verse.common.utils.VerseUtils;

public class Actor implements IPositionable {
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

	private int hero;
	private Integer id;
	private final int blueprint;

	private final String name;
	private int exp;
	private float heading;
	private int curHp;
	private int curShield;
	private int kills;

	private final Stats baseStats;
	private Stats stats;
	private boolean statsDirty = true;

	private Vector2 curPos;
	private Vector2 targetPos;

	private float curSpeed;

	private Vector2 curOrientationVector;
	private float rotationAngle;

	// TODO temp
	private EventBus eventBus;

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
		curPos = new Vector2(x, y);
		targetPos = new Vector2(x, y);
		this.heading = heading;

		this.curHp = curHp;
		this.curShield = curShield;
		this.kills = kills;

		// setCurOrientationVector(VerseUtils.angle2vector(heading));
		setCurOrientationVector(new Vector2(0, 1));

		// TODO calc stats
		stats = baseStats;

		setCurSpeed(0);
	}

	// TODO temp
	public void setEventBus(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	public void update(final float deltaTime) {
		// position
		final Vector2 targetVector = curPos.cpy().sub(targetPos);
		if (targetVector.len() > 1.f) {

			rotate(targetVector, deltaTime);

			final float deltaMovement = deltaTime * curSpeed;
			curPos.add(getCurOrientationVector().cpy().mul(deltaMovement));

			eventBus.post(this);
		} else {
			curPos = targetPos;
			setCurSpeed(0);
		}
	}

	private void rotate(final Vector2 targetVector, final float deltaTime) {
		final Vector2 targetOri = targetVector.nor().mul(-1);
		final double rotAngle = VerseUtils.vector2angle(getCurOrientationVector());
		final double targetAngle = VerseUtils.vector2angle(targetOri);

		double rotDiff = rotAngle - targetAngle;
		if (rotDiff > 180) {
			rotDiff -= 360;
		} else if (rotDiff < -180) {
			rotDiff += 360;
		}

		if (Math.abs(rotDiff) < 1.0f) {
			setCurOrientationVector(targetOri);
			return;
		}

		final float rotDiffAngle = deltaTime * stats.getRotation_speed();
		if (rotDiff > 0.0f) {
			setCurOrientationVector(getCurOrientationVector().rotate(-rotDiffAngle));
		} else {
			setCurOrientationVector(getCurOrientationVector().rotate(rotDiffAngle));
		}

	}

	public Vector2 getCurOrientationVector() {
		return curOrientationVector;
	}

	/**
	 * Sets orientation vector and computes the rotation angle.
	 * 
	 * @param orientationVector
	 */
	public void setCurOrientationVector(final Vector2 orientation) {
		curOrientationVector = orientation;
		setRotationAngle((float) VerseUtils.vector2angle(orientation));
	}

	public float getRotationAngle() {
		return rotationAngle;
	}

	public void setRotationAngle(final float rotationAngle) {
		this.rotationAngle = rotationAngle;
	}

	public String getOwner() {
		return owner;
	}

	public int getHero() {
		return hero;
	}

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
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

	@Override
	public Vector2 getPos() {
		return curPos;
	}

	public float getX() {
		return curPos.x;
	}

	public float getY() {
		return curPos.y;
	}

	public Vector2 getTargetPos() {
		return targetPos;
	}

	// public void setCurPos(final Vector2 curPos) {
	// this.curPos = curPos;
	// }

	public void setTargetPos(final Vector2 targetPos) {
		this.targetPos = targetPos;
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

	public float getCurSpeed() {
		return curSpeed;
	}

	public void setCurSpeed(final float curSpeed) {
		if (curSpeed < 0) {
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
