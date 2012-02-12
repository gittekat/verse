package com.hosh.verse;

public class CollisionChecker {
	public static boolean collistionActors(final VerseActor a1, final VerseActor a2) {
		// final float squaredDistanceThreshold = a1.getSquaredRadius() +
		// a2.getSquaredRadius();
		float squaredDistanceThreshold = a1.getBounds().radius + a2.getBounds().radius;
		squaredDistanceThreshold *= squaredDistanceThreshold;

		// final double sqRoot = Math.sqrt(squaredDistance(a1, a2));

		return squaredDistance(a1, a2) <= squaredDistanceThreshold;
	}

	public static float squaredDistance(final VerseActor a1, final VerseActor a2) {
		// float dx = a1.getPos().x - a2.getPos().x;
		// dx *= dx;
		// float dy = a1.getPos().y - a2.getPos().y;
		// dy *= dy;
		// return dx + dy;
		return a1.getPos().dst2(a2.getPos());
	}

}
