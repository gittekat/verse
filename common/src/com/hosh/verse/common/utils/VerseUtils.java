package com.hosh.verse.common.utils;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class VerseUtils {

	public static double vector2angle(final Vector2 orientation) {
		double theta = Math.atan2(orientation.x, orientation.y);
		theta = 360 - MathUtils.radiansToDegrees * theta;
		while (theta > 360) {
			theta -= 360;
		}
		return theta;
	}

	public static Vector2 angle2vector(final float rotationAngle) {
		// TODO compute vector!!
		// XXX now test it!

		final float rad = MathUtils.degreesToRadians * rotationAngle;

		return new Vector2(MathUtils.cos(rad), MathUtils.sin(rad));
	}

}
