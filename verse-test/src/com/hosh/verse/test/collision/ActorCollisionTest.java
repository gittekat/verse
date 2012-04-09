package com.hosh.verse.test.collision;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.google.common.base.Stopwatch;
import com.hosh.verse.common.Actor;
import com.hosh.verse.common.CollisionChecker;
import com.hosh.verse.common.Stats;
import com.hosh.verse.common.quadtree.AbstractQuadNodeElement;
import com.hosh.verse.common.quadtree.PointQuadTree;
import com.hosh.verse.server.database.DatabaseAccessor;
import com.hosh.verse.test.common.TestUtils;

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
	private static float actorRadius;

	private static Connection connection;

	@BeforeClass
	public static void setUp() throws Exception {
		connection = TestUtils.getDBConnection();

		final Stats baseStats = DatabaseAccessor.loadBlueprint(connection, 1);
		actorRadius = baseStats.getCollision_radius();

		a1 = new Actor(2, "hosh", 0, baseStats, "a1", 0, 100, 100, 0, 10, 10, 0);
		a2 = new Actor(3, "hosh", 0, baseStats, "a1", 0, 131.999f, 100, 0, 10, 10, 0);
		a3 = new Actor(4, "hosh", 0, baseStats, "a1", 0, 132.001f, 100, 0, 10, 10, 0);
		a4 = new Actor(5, "hosh", 0, baseStats, "a1", 0, actorPosX, actorPosY, 0, 10, 10, 0);

		actorList = new ArrayList<Actor>();
		for (int i = 0; i < 1000000; ++i) {
			final int w = MathUtils.random(width);
			final int h = MathUtils.random(height);
			final Actor actor = new Actor(6, "hosh", 0, baseStats, "a1", 0, w, h, 0, 10, 10, 0);
			actorList.add(actor);
		}
	}

	@Test
	public void testTwoActors() {
		Assert.assertEquals("Result", true, CollisionChecker.collisionActorActor(a1, a2));
		Assert.assertEquals("Result", false, CollisionChecker.collisionActorActor(a1, a3));
	}

	@Test
	public void testActorList() {

		final Stopwatch stopwatch = new Stopwatch().start();

		int cnt = 0;
		for (final Actor a : actorList) {
			if (CollisionChecker.collisionActorActor(a4, a)) {
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
		tree = new PointQuadTree<Actor>(new Vector2(0, 0), new Vector2(width, height), depth, 40);

		for (final Actor a : actorList) {
			tree.insert((int) a.getX(), (int) a.getY(), a);
		}

		final Stopwatch stopwatch = new Stopwatch().start();

		final int playerRadius = (int) actorRadius * 2;
		final Vector<AbstractQuadNodeElement<Actor>> e1 = (Vector<AbstractQuadNodeElement<Actor>>) tree.getElements(new Vector2(
				(int) actorPosX - playerRadius, (int) actorPosY - playerRadius));
		final Vector<AbstractQuadNodeElement<Actor>> e2 = (Vector<AbstractQuadNodeElement<Actor>>) tree.getElements(new Vector2(
				(int) actorPosX - playerRadius, (int) actorPosY + playerRadius));
		final Vector<AbstractQuadNodeElement<Actor>> e3 = (Vector<AbstractQuadNodeElement<Actor>>) tree.getElements(new Vector2(
				(int) actorPosX + playerRadius, (int) actorPosY - playerRadius));
		final Vector<AbstractQuadNodeElement<Actor>> e4 = (Vector<AbstractQuadNodeElement<Actor>>) tree.getElements(new Vector2(
				(int) actorPosX + playerRadius, (int) actorPosY + playerRadius));

		final Set<Actor> collisionCandidates = new HashSet<Actor>();
		for (final AbstractQuadNodeElement<Actor> e : e1) {
			collisionCandidates.add(e.getElement());
		}
		for (final AbstractQuadNodeElement<Actor> e : e2) {
			collisionCandidates.add(e.getElement());
		}
		for (final AbstractQuadNodeElement<Actor> e : e3) {
			collisionCandidates.add(e.getElement());
		}
		for (final AbstractQuadNodeElement<Actor> e : e4) {
			collisionCandidates.add(e.getElement());
		}

		int cnt = 0;
		for (final Actor a : collisionCandidates) {
			if (CollisionChecker.collisionActorActor(a4, a)) {
				cnt++;
			}
		}

		final long duration = stopwatch.elapsedMillis();
		System.out.println("quadtree");
		System.out.println("player radius: " + actorRadius);
		System.out.println("width of deepest quadtree region: " + width / Math.pow(2, depth));
		System.out.println("collisions checked: " + collisionCandidates.size() + "/" + actorList.size() + " => "
				+ (double) collisionCandidates.size() / actorList.size());
		System.out.println("that took: " + duration);
		System.out.println(">collisions: " + cnt);
		System.out.println("___________________");
	}
}
