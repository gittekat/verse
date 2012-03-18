package com.hosh.verse.common.utils;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class VerseUtils {

	public static double vector2angle(final Vector2 orientation) {
		double theta = Math.atan2(orientation.x, orientation.y);
		theta = 360 - MathUtils.radiansToDegrees * theta;
		return theta;
	}

	public static Vector2 angle2vector(final double rotationAngle) {
		return null;
	}

}
