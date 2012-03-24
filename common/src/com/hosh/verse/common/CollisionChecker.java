package com.hosh.verse.common;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class CollisionChecker {
	public static boolean collistionActorActor(final VerseActor a1, final VerseActor a2) {
		// final float squaredDistanceThreshold = a1.getSquaredRadius() +
		// a2.getSquaredRadius();
		float squaredDistanceThreshold = a1.getBounds().radius + a2.getBounds().radius;
		squaredDistanceThreshold *= squaredDistanceThreshold;

		// final double sqRoot = Math.sqrt(squaredDistance(a1, a2));

		return squaredDistance(a1, a2) <= squaredDistanceThreshold;
	}

	public static boolean collisionPointActor(final float x, final float y, final VerseActor actor) {
		final Vector2 pos = new Vector2(x, y);

		return squaredDistance(pos, actor.getPos()) <= actor.getSquaredRadius();
	}

	public static float squaredDistance(final Vector2 v1, final Vector2 v2) {
		return v1.dst2(v2);
	}

	public static float squaredDistance(final VerseActor a1, final VerseActor a2) {
		// float dx = a1.getPos().x - a2.getPos().x;
		// dx *= dx;
		// float dy = a1.getPos().y - a2.getPos().y;
		// dy *= dy;
		// return dx + dy;
		return a1.getPos().dst2(a2.getPos());
	}

	public static boolean pointAARect(final Vector2 point, final Rectangle rect) {
		if (point.x < rect.x) {
			return false;
		}
		if (point.y < rect.y) {
			return false;
		}
		if (point.x >= rect.x + rect.width) {
			return false;
		}
		if (point.y >= rect.y + rect.height) {
			return false;
		}
		return true;
	}

	public static int mortonNumber(int x, int y) {
		final int B[] = { 0x55555555, 0x33333333, 0x0F0F0F0F, 0x00FF00FF };
		final int S[] = { 1, 2, 4, 8 };

		// Interleave lower 16 bits of x and y, so the bits of x
		// are in the even positions and bits from y in the odd;
		// z gets the resulting 32-bit Morton Number.
		// x and y must initially be less than 65536.

		int z;
		x = (x | x << S[3]) & B[3];
		x = (x | x << S[2]) & B[2];
		x = (x | x << S[1]) & B[1];
		x = (x | x << S[0]) & B[0];

		y = (y | y << S[3]) & B[3];
		y = (y | y << S[2]) & B[2];
		y = (y | y << S[1]) & B[1];
		y = (y | y << S[0]) & B[0];

		z = x | y << 1;

		return z;
	}
}
