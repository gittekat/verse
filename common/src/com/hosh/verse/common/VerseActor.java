package com.hosh.verse.common;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.hosh.verse.common.utils.VerseUtils;

public class VerseActor {
	public static final String CHAR_ID = "charId";
	public static final String RADIUS = "radius";
	public static final String POS_X = "x";
	public static final String POS_Y = "y";
	public static final String TARGET_POS_X = "targetX";
	public static final String TARGET_POS_Y = "targetY";
	public static final String ORIENTATION_X = "oriX";
	public static final String ORIENTATION_Y = "oriY";
	public static final String SPEED = "speed";

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
	private Vector2 curOrientationVector;
	private Vector2 targetOrientationVector;
	private float rotationAngle;
	private float targetRotationAngle;
	private float rotationSpeed;

	private final float MAX_SPEED = 20.f;
	private final float ROTATION_SPEED = 500.f;

	/** default constructor */
	public VerseActor(final int id, final float posX, final float posY, final float radius) {
		this.setCharId(id);
		curPos = new Vector2(posX, posY);
		targetPos = new Vector2(posX, posY);
		this.radius = radius;
		bounds = new Circle(curPos, radius);
		squaredRadius = radius * radius;

		setCurOrientationVector(new Vector2(1, 0));
		setTargetOrientationVector(new Vector2(getCurOrientationVector()));
		setRotationSpeed(ROTATION_SPEED);

		setMaxSpeed(MAX_SPEED);
		setCurSpeed(0);

		setShieldStrength(0.4f);
	}

	public VerseActor(final int id, final float posX, final float posY, final float targetPosX, final float targetPosY, final float radius,
			final float speed, final float oriX, final float oriY) {
		this(id, posX, posY, radius);
		targetPos = new Vector2(targetPosX, targetPosY);
		setCurSpeed(speed);
		setCurOrientationVector(new Vector2(oriX, oriY));
		setTargetOrientationVector(new Vector2(getCurOrientationVector()));
	}

	/** player constructor */
	public VerseActor(final int charId, final String name, final int exp, final int level, final int maxHp, final int curHp,
			final float posX, final float posY, final float heading, final float radius) {
		this.setCharId(charId);
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

		setCurOrientationVector(new Vector2(0, heading));
		setTargetOrientationVector(new Vector2(getCurOrientationVector()));
		setRotationSpeed(ROTATION_SPEED);

		setMaxSpeed(MAX_SPEED);
		setCurSpeed(0);

		setShieldStrength(0.4f);
	}

	public void update(final float deltaTime) {
		// orientation
		// setCurOrientationVector(getTargetOrientationVector());

		// if (charId < 10) {
		// final int stopHere = 109;
		// }
		//

		// final double rotAngle =
		// VerseUtils.vector2angle(getCurOrientationVector());
		// final double targetAngle =
		// VerseUtils.vector2angle(getTargetOrientationVector());

		// final Vector2 targetOri = targetVector.nor();
		// final double rotAngle =
		// VerseUtils.vector2angle(getCurOrientationVector());
		// final double targetAngle = VerseUtils.vector2angle(targetOri);
		//
		// final double rotDiff = rotAngle - targetAngle;
		// final double rotSpeed = deltaTime * getRotationSpeed() *
		// MathUtils.degreesToRadians;
		// if (Math.abs(rotDiff) >= 1.0f) {
		// final double x = getCurOrientationVector().x;
		// final double y = getCurOrientationVector().y;
		// final double x_new = x * Math.cos(rotSpeed) - y * Math.sin(rotSpeed);
		// final double y_new = x * Math.sin(rotSpeed) + y * Math.cos(rotSpeed);
		// setCurOrientationVector(new Vector2((float) x_new, (float)
		// y_new).nor()); // TODO
		// // nor??
		// // System.err.println("test:" + curPos);
		// return;
		// } else {
		// // System.err.println("test:" + curPos);
		// // setCurOrientationVector(getTargetOrientationVector());
		// setCurOrientationVector(targetOri);
		// }

		// position
		final Vector2 targetVector = curPos.cpy().sub(targetPos);
		if (targetVector.len() > 1.f) {

			rotate(targetVector, deltaTime);

			final float deltaMovement = deltaTime * curSpeed;
			curPos.add(curOrientationVector.cpy().mul(deltaMovement));
		} else {
			curPos = targetPos;
			setCurSpeed(getMaxSpeed());
		}
	}

	private void rotate(final Vector2 targetVector, final float deltaTime) {
		final Vector2 targetOri = targetVector.nor();
		final float len = targetOri.len();
		targetOri.mul(-1);
		final double rotAngle = VerseUtils.vector2angle(getCurOrientationVector());
		final double targetAngle = VerseUtils.vector2angle(targetOri);

		final double rotDiff = rotAngle - targetAngle;
		// final double rotDiffAngle = deltaTime * getRotationSpeed() *
		// MathUtils.degreesToRadians;
		final float rotDiffAngle = -deltaTime * getRotationSpeed();
		// if (rotDiff < 0.0) {
		// rotDiffAngle = -deltaTime * getRotationSpeed();
		// } else {
		// rotDiffAngle = deltaTime * getRotationSpeed();
		// }
		if (Math.abs(rotDiff) >= 1.0f) {
			System.out.println(rotDiff);
			System.out.println("---");
			final double x = getCurOrientationVector().x;
			final double y = getCurOrientationVector().y;
			// final double x_new = x * Math.cos(rotDiffAngle) - y *
			// Math.sin(rotDiffAngle);
			// final double y_new = x * Math.sin(rotDiffAngle) + y *
			// Math.cos(rotDiffAngle);
			// final Vector2 newOri = new Vector2((float) x_new, (float) y_new);
			final Vector2 newOri = getCurOrientationVector().rotate(rotDiffAngle);
			final double newAngle = VerseUtils.vector2angle(newOri);
			setCurOrientationVector(newOri);

			// System.err.println("test:" + curPos);
		} else {
			// System.err.println("test:" + curPos);
			// setCurOrientationVector(getTargetOrientationVector());
			setCurOrientationVector(targetOri);
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

	public Vector2 getTargetOrientationVector() {
		return targetOrientationVector;
	}

	/**
	 * Sets target orientation vector and computes the target rotation angle.
	 * 
	 * @param orientation
	 */
	public void setTargetOrientationVector(final Vector2 targetOrientation) {
		targetOrientationVector = targetOrientation;
		setTargetRotationAngle((float) VerseUtils.vector2angle(targetOrientation));
	}

	public float getRotationAngle() {
		return rotationAngle;
	}

	public void setRotationAngle(final float rotationAngle) {
		this.rotationAngle = rotationAngle;
	}

	public float getTargetRotationAngle() {
		return targetRotationAngle;
	}

	public void setTargetRotationAngle(final float targetRotationAngle) {
		this.targetRotationAngle = targetRotationAngle;
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

	public void setCharId(final int charId) {
		this.charId = charId;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getCharId();
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
		if (getCharId() != other.getCharId()) {
			return false;
		}
		return true;
	}

}
