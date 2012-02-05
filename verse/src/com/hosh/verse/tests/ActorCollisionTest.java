package com.hosh.verse.tests;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.math.MathUtils;
import com.google.common.base.Stopwatch;
import com.hosh.verse.Actor;
import com.hosh.verse.CollisionChecker;

public class ActorCollisionTest {

	private Actor a1;
	private Actor a2;
	private Actor a3;
	private Actor a4;
	private List<Actor> actorList;

	@Before
	public void setUp() throws Exception {
		a1 = new Actor(100.f, 100.f, 5.f);
		a2 = new Actor(119.999f, 100.f, 15.f);
		a3 = new Actor(120.001f, 100.f, 15.f);

		a4 = new Actor(50.f, 50.f, 5.f);
		actorList = new ArrayList<Actor>();
		for (int i = 0; i < 1000000; ++i) {
			final Actor actor = new Actor(MathUtils.random(100.f), MathUtils.random(100.f), 5.f);
			// System.out.println(actor.getPos());
			actorList.add(actor);
		}
	}

	@Test
	public void testTwoActors() {
		Assert.assertEquals("Result", true, CollisionChecker.collistionActors(a1, a2));
		Assert.assertEquals("Result", false, CollisionChecker.collistionActors(a1, a3));
	}

	@Test
	public void testActorList() {
		final Stopwatch stopwatch = new Stopwatch().start();

		int cnt = 0;
		for (final Actor a : actorList) {
			if (CollisionChecker.collistionActors(a4, a)) {
				cnt++;
			}
		}
		final long duration = stopwatch.elapsedMillis();
		System.out.println("collisions: " + cnt);
		System.out.println("that took: " + duration);

		Assert.assertTrue(duration < 60);
	}

}
