package com.hosh.verse.tests;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.badlogic.gdx.math.MathUtils;
import com.google.common.base.Stopwatch;
import com.hosh.verse.Actor;
import com.hosh.verse.ActorFactory;
import com.hosh.verse.CollisionChecker;
import com.hosh.verse.quadtree.AbstractNodeElement;
import com.hosh.verse.quadtree.PointQuadTree;

public class ActorCollisionTest {

	private static Actor a1;
	private static Actor a2;
	private static Actor a3;
	private static Actor a4;
	private static List<Actor> actorList;

	private PointQuadTree<Actor> tree;

	// private static int width = 2500; // works
	// private static int height = 2500;
	private static int width = 2000;
	private static int height = 2000;
	private static float actorPosX = 251.f;
	private static float actorPosY = 251.f;

	@BeforeClass
	public static void setUp() throws Exception {
		a1 = ActorFactory.createActor(100.f, 100.f, 5.f);
		a2 = ActorFactory.createActor(119.999f, 100.f, 15.f);
		a3 = ActorFactory.createActor(120.001f, 100.f, 15.f);
		a4 = ActorFactory.createActor(actorPosX, actorPosY, 5.f);

		actorList = new ArrayList<Actor>();
		for (int i = 0; i < 1000000; ++i) {
			final Actor actor = ActorFactory.createActor(MathUtils.random(width), MathUtils.random(height), 5.f);
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
		System.out.println("testActorList");
		System.out.println("that took: " + duration);
		System.out.println(">collisions: " + cnt);
		System.out.println("___________________");

		Assert.assertTrue(duration < 60);
	}

	@Test
	public void testQuadTree() {

		final int depth = 6;
		tree = new PointQuadTree<Actor>(new Point(0, 0), new Dimension(width, height), depth, 40);

		for (final Actor a : actorList) {
			tree.insert((int) a.getPos().x, (int) a.getPos().y, a);
		}

		final Stopwatch stopwatch = new Stopwatch().start();

		final Vector<AbstractNodeElement<Actor>> elements = (Vector<AbstractNodeElement<Actor>>) tree.getElements(new Point(
				(int) actorPosX, (int) actorPosY));

		int cnt = 0;
		for (final AbstractNodeElement<Actor> e : elements) {
			if (CollisionChecker.collistionActors(a4, e.getElement())) {
				cnt++;
			}
		}

		final long duration = stopwatch.elapsedMillis();
		System.out.println("quadtree");
		System.out.println("width of deepest quadtree region: " + width / Math.pow(2, depth));
		System.out.println("elements found: " + elements.size());
		System.out.println("that took: " + duration);
		System.out.println(">collisions: " + cnt);
		System.out.println("___________________");
	}
}
