package com.hosh.verse.common;

public class MovementData {
	private int id;
	private float posx;
	private float posY;
	private float targetPosX;
	private float targetPosY;
	private float speed;

	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public float getPosx() {
		return posx;
	}

	public void setPosx(final float posx) {
		this.posx = posx;
	}

	public float getPosY() {
		return posY;
	}

	public void setPosY(final float posY) {
		this.posY = posY;
	}

	public float getTargetPosX() {
		return targetPosX;
	}

	public void setTargetPosX(final float targetPosX) {
		this.targetPosX = targetPosX;
	}

	public float getTargetPosY() {
		return targetPosY;
	}

	public void setTargetPosY(final float targetPosY) {
		this.targetPosY = targetPosY;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(final float speed) {
		this.speed = speed;
	}
}
