package com.hosh.verse.common;

import com.smartfoxserver.v2.protocol.serialization.SerializableSFSType;

public class MovementData implements SerializableSFSType {
	private int id;
	private float posX;
	private float posY;
	private float targetPosX;
	private float targetPosY;
	private float speed;

	public MovementData() {
		// empty constructor needed for SmartfoxSerialization
	}

	public MovementData(final Actor actor) {
		setId(actor.getId());
		setPosX(actor.getX());
		setPosY(actor.getY());
		setTargetPosX(actor.getTargetX());
		setTargetPosY(actor.getTargetY());
		setSpeed(actor.getCurSpeed());
	}

	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public float getPosX() {
		return posX;
	}

	public void setPosX(final float posX) {
		this.posX = posX;
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

	@Override
	public String toString() {
		return "pos:" + posX + " " + posY + " target:" + targetPosX + " " + targetPosY;
	}
}
