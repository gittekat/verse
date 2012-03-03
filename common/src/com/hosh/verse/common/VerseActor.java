package com.hosh.verse.common;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;

public class VerseActor {
	private int charId;
	private String name;

	private int exp;
	private int level;

	private float maxHp;
	private float curHp;

	private float shieldStrength;

	private float maxSpeed;
	private float curSpeed;

	private Vector2 curPos;
	private Vector2 targetPos;

	private Circle bounds;
	private float radius;
	private float squaredRadius;

	// TODO slow rotation to designated rotation angle
	private Vector2 curOrientation;
	private Vector2 targetOrientation;
	private float rotationAngle;
	private float rotationSpeed;

	/** default constructor */
	public VerseActor(final int id, final float posX, final float posY, final float radius) {
		this.setId(id);
		curPos = new Vector2(posX, posY);
		targetPos = new Vector2(posX, posY);
		this.radius = radius;
		bounds = new Circle(curPos, radius);
		squaredRadius = radius * radius;

		setCurOrientation(new Vector2(0, 0));
		rotationAngle = 0.f;
		setRotationSpeed(0.f);

		setMaxSpeed(50);
		setCurSpeed(0);

		setShieldStrength(0.4f);
	}

	/** player constructor */
	public VerseActor(final int charId, final String name, final int exp, final int level, final int maxHp, final int curHp,
			final float posX, final float posY, final float heading, final float radius) {
		this.setId(charId);
		this.setName(name);
		this.setExp(exp);
		this.setLevel(level);
		this.setMaxHp(maxHp);
		this.setCurHp(curHp);

		curPos = new Vector2(posX, posY);
		targetPos = new Vector2(posX, posY);
		this.radius = radius;
		bounds = new Circle(curPos, radius);
		squaredRadius = radius * radius;

		setCurOrientation(new Vector2(0, heading));
		rotationAngle = 0.f;
		setRotationSpeed(0.f);

		setMaxSpeed(50);
		setCurSpeed(0);

		setShieldStrength(0.4f);
	}

	public void update(final float deltaTime) {
		if (curPos.cpy().sub(targetPos).len() > 1.f) {
			final float deltaMovement = deltaTime * curSpeed;
			curPos.add(curOrientation.cpy().mul(deltaMovement));
		} else {
			curPos = targetPos;
		}
	}

	public Vector2 getPos() {
		return curPos;
	}

	public void setPos(final Vector2 pos) {
		this.curPos = pos;
	}

	public Vector2 getTargetPos() {
		return targetPos;
	}

	public void setTargetPos(final Vector2 targetPos) {
		this.targetPos = targetPos;
	}

	public Circle getBounds() {
		return bounds;
	}

	public float getSquaredRadius() {
		return squaredRadius;
	}

	public float getRadius() {
		return radius;
	}

	public float getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(final float maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public float getCurSpeed() {
		return curSpeed;
	}

	public void setCurSpeed(final float curSpeed) {
		if (curSpeed < 0) {
			this.curSpeed = 0;
			return;
		}

		if (curSpeed > maxSpeed) {
			this.curSpeed = maxSpeed;
			return;
		}

		this.curSpeed = curSpeed;
	}

	public Vector2 getCurOrientation() {
		return curOrientation;
	}

	public void setCurOrientation(final Vector2 orientation) {
		curOrientation = orientation;
	}

	public Vector2 getTargetOrientation() {
		return targetOrientation;
	}

	public void setTargetOrientation(final Vector2 targetOrientation) {
		this.targetOrientation = targetOrientation;
	}

	public float getRotationAngle() {
		return rotationAngle;
	}

	public void setRotationAngle(final float rotationAngle) {
		this.rotationAngle = rotationAngle;
	}

	public float getRotationSpeed() {
		return rotationSpeed;
	}

	public void setRotationSpeed(final float rotationSpeed) {
		this.rotationSpeed = rotationSpeed;
	}

	public float getShieldStrength() {
		return shieldStrength;
	}

	public int getCharId() {
		return charId;
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

	public int getLevel() {
		return level;
	}

	public float getMaxHp() {
		return maxHp;
	}

	public float getCurHp() {
		return curHp;
	}

	public Vector2 getCurPos() {
		return curPos;
	}

	public void setExp(final int exp) {
		this.exp = exp;
	}

	public void setLevel(final int level) {
		this.level = level;
	}

	public void setMaxHp(final float maxHp) {
		this.maxHp = maxHp;
	}

	public void setCurHp(final float curHp) {
		this.curHp = curHp;
	}

	public void setCurPos(final Vector2 curPos) {
		this.curPos = curPos;
	}

	public void setShieldStrength(final float shieldStrength) {
		this.shieldStrength = shieldStrength;
	}

	public int getId() {
		return charId;
	}

	public void setId(final int id) {
		this.charId = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getId();
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
		final VerseActor other = (VerseActor) obj;
		if (getId() != other.getId()) {
			return false;
		}
		return true;
	}

}
