package com.hosh.verse;

import java.util.ArrayList;

import junit.framework.Assert;

import org.junit.Test;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.google.common.base.Stopwatch;
import com.hosh.verse.common.Interpreter;
import com.hosh.verse.common.CollisionChecker;
import com.hosh.verse.common.VerseActor;

public class StarmapTest {

	@Test
	public void testMortonNumber() {
		Assert.assertEquals(CollisionChecker.mortonNumber(4, 1), 18);
	}

	@Test
	public void testPointAxisAlignedRectangleInclusion() {
		final float worldDimX = 10000.f;
		final float worldDimY = 10000.f;
		final Rectangle rect = new Rectangle(100.f, 100.f, 1000.f, 1000.f);

		final ArrayList<VerseActor> actorList = new ArrayList<VerseActor>();
		for (int i = 0; i < 1000000; ++i) {
			final VerseActor actor = Interpreter.createActor(MathUtils.random(worldDimX), MathUtils.random(worldDimY), 5.f);
			actorList.add(actor);
		}

		Stopwatch stopwatch = new Stopwatch().start();
		final ArrayList<VerseActor> includedList = new ArrayList<VerseActor>();
		int pointRectCnt = 0;
		for (final VerseActor a : actorList) {
			if (CollisionChecker.pointAARect(new Vector2(a.getPos().x, a.getPos().y), rect)) {
				// includedList.add(a);
				pointRectCnt++;
			}
		}
		final long durationPointRectTest = stopwatch.elapsedMillis();
		System.out.println("durationPointRectTest: " + durationPointRectTest + " " + pointRectCnt);

		stopwatch = new Stopwatch().start();
		final ArrayList<VerseActor> collisionList = new ArrayList<VerseActor>();
		int collisionCnt = 0;
		final VerseActor player = Interpreter.createActor(200.f, 200.f, 500.f);
		for (final VerseActor a : actorList) {
			if (CollisionChecker.collisionActorActor(player, a)) {
				// collisionList.add(a);
				collisionCnt++;
			}
		}
		final long durationCollisionTest = stopwatch.elapsedMillis();
		System.out.println("durationCollisionTest: " + durationCollisionTest + " " + collisionCnt);
	}
}
