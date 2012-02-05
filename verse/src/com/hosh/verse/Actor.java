package com.hosh.verse;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;

public class Actor {
	private Vector2 curPos;
	private Vector2 targetPos;

	private Circle bounds;
	private float squaredRadius;

	// TODO slow rotation to designated rotation angle
	private Vector2 curOrientation;
	private Vector2 targetOrientation;
	private float rotationAngle;
	private float rotationSpeed;

	private float maxSpeed;
	private float curSpeed;

	public Actor(final float posX, final float posY, final float radius) {
		curPos = new Vector2(posX, posY);
		targetPos = new Vector2(posX, posY);
		bounds = new Circle(curPos, radius);
		squaredRadius = radius * radius;

		setCurOrientation(new Vector2(0, 0));
		rotationAngle = 0.f;
		rotationSpeed = 0.f;

		setMaxSpeed(50);
		setCurSpeed(0);
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

	public float getRotationAngle() {
		return rotationAngle;
	}

	public void setRotationAngle(final float rotationAngle) {
		this.rotationAngle = rotationAngle;
	}
}
