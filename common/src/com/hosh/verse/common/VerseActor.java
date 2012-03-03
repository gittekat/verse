package com.hosh.verse.common;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;

public class VerseActor {
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

	private float maxSpeed;
	private float curSpeed;

	private float shieldStrength;
	private int id;

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

	public void setTargetOrientation(Vector2 targetOrientation) {
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

	public void setRotationSpeed(float rotationSpeed) {
		this.rotationSpeed = rotationSpeed;
	}

	public float getShieldStrength() {
		return shieldStrength;
	}

	public void setShieldStrength(final float shieldStrength) {
		this.shieldStrength = shieldStrength;
	}

	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
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
