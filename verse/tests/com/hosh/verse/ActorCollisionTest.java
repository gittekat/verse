package com.hosh.verse;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.badlogic.gdx.math.MathUtils;
import com.google.common.base.Stopwatch;
import com.hosh.verse.quadtree.AbstractQuadNodeElement;
import com.hosh.verse.quadtree.PointQuadTree;

public class ActorCollisionTest {

	private static Actor a1;
	private static Actor a2;
	private static Actor a3;
	private static Actor a4;
	private static List<Actor> actorList;

	private PointQuadTree<Actor> tree;

	private static int width = 1000;
	private static int height = 1000;
	private static float actorPosX = 251.f;
	private static float actorPosY = 251.f;
	private static float actorRadius = 5.f;

	@BeforeClass
	public static void setUp() throws Exception {
		a1 = ActorFactory.createActor(100.f, 100.f, 5.f);
		a2 = ActorFactory.createActor(119.999f, 100.f, 15.f);
		a3 = ActorFactory.createActor(120.001f, 100.f, 15.f);
		a4 = ActorFactory.createActor(actorPosX, actorPosY, actorRadius);

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

		final int playerRadius = (int) actorRadius * 2;
		final Vector<AbstractQuadNodeElement<Actor>> e1 = (Vector<AbstractQuadNodeElement<Actor>>) tree.getElements(new Point(
				(int) actorPosX - playerRadius, (int) actorPosY - playerRadius));
		final Vector<AbstractQuadNodeElement<Actor>> e2 = (Vector<AbstractQuadNodeElement<Actor>>) tree.getElements(new Point(
				(int) actorPosX - playerRadius, (int) actorPosY + playerRadius));
		final Vector<AbstractQuadNodeElement<Actor>> e3 = (Vector<AbstractQuadNodeElement<Actor>>) tree.getElements(new Point(
				(int) actorPosX + playerRadius, (int) actorPosY - playerRadius));
		final Vector<AbstractQuadNodeElement<Actor>> e4 = (Vector<AbstractQuadNodeElement<Actor>>) tree.getElements(new Point(
				(int) actorPosX + playerRadius, (int) actorPosY + playerRadius));
		final Set<AbstractQuadNodeElement<Actor>> elements2 = new HashSet<AbstractQuadNodeElement<Actor>>();
		elements2.addAll(e1);
		elements2.addAll(e2);
		elements2.addAll(e3);
		elements2.addAll(e4);

		int cnt = 0;
		for (final AbstractQuadNodeElement<Actor> e : elements2) {
			if (CollisionChecker.collistionActors(a4, e.getElement())) {
				cnt++;
			}
		}

		final long duration = stopwatch.elapsedMillis();
		System.out.println("quadtree");
		System.out.println("player radius: " + actorRadius);
		System.out.println("width of deepest quadtree region: " + width / Math.pow(2, depth));
		System.out.println("collisions checked: " + elements2.size() + "/" + actorList.size() + " => " + (double) elements2.size()
				/ (double) actorList.size());
		System.out.println("that took: " + duration);
		System.out.println(">collisions: " + cnt);
		System.out.println("___________________");
	}
}
