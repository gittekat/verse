package com.hosh.verse.common;

import com.badlogic.gdx.math.Vector2;

public class CollisionChecker {

	// @Deprecated
	// public static boolean collisionVActorVActor(final VerseActor a1, final
	// VerseActor a2) {
	// float squaredDistanceThreshold = a1.getBounds().radius +
	// a2.getBounds().radius;
	// squaredDistanceThreshold *= squaredDistanceThreshold;
	//
	// return squaredDistanceV(a1, a2) <= squaredDistanceThreshold;
	// }

	public static boolean collisionActorActor(final Actor a1, final Actor a2) {
		float squaredDistanceThreshold = a1.getStats().getCollision_radius() + a2.getStats().getCollision_radius();
		squaredDistanceThreshold *= squaredDistanceThreshold;

		return squaredDistance(a1, a2) <= squaredDistanceThreshold;
	}

	// @Deprecated
	// public static boolean collisionPointVActor(final float x, final float y,
	// final VerseActor actor) {
	// final Vector2 pos = new Vector2(x, y);
	//
	// return squaredDistance(pos, actor.getPos()) <= actor.getSquaredRadius();
	// }

	public static boolean collisionPointActor(final float x, final float y, final Actor actor) {
		final Vector2 pos = new Vector2(x, y);

		return squaredDistance(pos, actor.getPos()) <= actor.getStats().getSquaredCollisionRadius();
	}

	public static float squaredDistance(final Vector2 v1, final Vector2 v2) {
		return v1.dst2(v2);
	}

	public static float squaredDistance(final float x1, final float y1, final float x2, final float y2) {
		final Vector2 v1 = new Vector2(x1, y1);
		final Vector2 v2 = new Vector2(x2, y2);

		return v1.dst2(v2);
	}

	// @Deprecated
	// public static float squaredDistanceV(final VerseActor a1, final
	// VerseActor a2) {
	// return a1.getPos().dst2(a2.getPos());
	// }

	public static float squaredDistance(final Actor a1, final Actor a2) {
		return a1.getPos().dst2(a2.getPos());
	}

	// @Deprecated
	// public static boolean pointAARect(final Vector2 point, final Rectangle
	// rect) {
	// if (point.x < rect.x) {
	// return false;
	// }
	// if (point.y < rect.y) {
	// return false;
	// }
	// if (point.x >= rect.x + rect.width) {
	// return false;
	// }
	// if (point.y >= rect.y + rect.height) {
	// return false;
	// }
	// return true;
	// }

	public static boolean pointRect(final float x, final float y, final float rectX, final float rectY, final float width,
			final float height) {
		if (x < rectX) {
			return false;
		}
		if (y < rectY) {
			return false;
		}
		if (x >= rectX + width) {
			return false;
		}
		if (y >= rectY + height) {
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
