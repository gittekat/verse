package com.hosh.verse.test.collision;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import com.badlogic.gdx.math.Vector2;
import com.hosh.verse.common.Actor;
import com.hosh.verse.common.CollisionChecker;
import com.hosh.verse.common.Stats;
import com.hosh.verse.common.quadtree.AbstractQuadNodeElement;
import com.hosh.verse.common.quadtree.PointQuadTree;
import com.hosh.verse.server.database.DatabaseAccessor;
import com.hosh.verse.test.common.TestUtils;

public class QuadtreeCollisionSystemTest {

	private static Actor player;
	private static List<Actor> actorList;

	private PointQuadTree<Actor> tree;

	private static int width = 1000;
	private static int height = 1000;
	private static int objects = 10000;
	private static float actorPosX; // = MathUtils.random(width);
	private static float actorPosY; // = MathUtils.random(height);
	private static float actorRadius;
	private final int depth = 4;
	private static Random rand;
	private static int qtCollisions = 0;

	private static Connection connection;
	private static Stats baseStats;

	@BeforeClass
	public static void setUp() throws Exception {
		connection = TestUtils.getDBConnection();

		baseStats = DatabaseAccessor.loadBlueprint(connection, 1);
		actorRadius = baseStats.getCollision_radius();

		rand = new Random(0);

		actorList = new ArrayList<Actor>();
		for (int i = 0; i < objects; ++i) {
			final int x = rand.nextInt(width);
			final int y = rand.nextInt(height);
			final Actor actor = new Actor(2, "hosh", 0, baseStats, "a1", 0, x, y, 0, 10, 10, 0);
			actorList.add(actor);
		}
	}

	@Test
	public void testMultipleRuns() {
		actorPosX = rand.nextInt(width);
		actorPosY = rand.nextInt(height);
		final int tests = 1000;
		int fails = 0;
		for (int i = 0; i < tests; ++i) {
			rand = new Random(i);
			if (testCollisionSystem()) {
				fails++;
			}
		}

		System.out.println("___________________");
		System.out.println("___________________");
		System.out.println("width of deepest quadtree region: " + width / Math.pow(2, depth));
		System.out.println("player radius: " + actorRadius);
		if (width / Math.pow(2, depth) < actorRadius * 2) {
			System.err.println("WARNING: quadtree is too fine grained!");
		}

		final double avgCollisions = (double) qtCollisions / (double) tests;
		System.out.println("average collisions: " + avgCollisions);
		final double avgCollisionRate = avgCollisions / actorList.size();
		System.out.println("average percent of checked objects: " + avgCollisionRate);

		System.out.println("success rate: " + fails + "/" + tests);

		Assert.assertTrue(avgCollisionRate < 0.1);
		Assert.assertTrue(fails != 0);
	}

	public boolean testCollisionSystem() {
		actorPosX = rand.nextInt(width);
		actorPosY = rand.nextInt(height);
		player = new Actor(2, "hosh", 0, baseStats, "a1", 0, actorPosX, actorPosX, 0, 10, 10, 0);

		final ArrayList<Actor> allCollisionsList = new ArrayList<Actor>();
		for (final Actor a : actorList) {
			if (CollisionChecker.collisionActorActor(player, a)) {
				allCollisionsList.add(a);
				// printActor(a);
			}
		}

		tree = new PointQuadTree<Actor>(new Vector2(0, 0), new Vector2(width, height), depth, 10);

		for (final Actor a : actorList) {
			tree.insert((int) a.getX(), (int) a.getY(), a);
		}

		final int playerRadius = (int) actorRadius * 2;

		final boolean debug = true;
		final Set<Actor> collisionCandidates;
		if (debug) {
			collisionCandidates = tree.getElements((int) actorPosX, (int) actorPosY, playerRadius);
		} else {
			final Vector<AbstractQuadNodeElement<Actor>> e1 = (Vector<AbstractQuadNodeElement<Actor>>) tree.getElements(new Vector2(
					(int) actorPosX - playerRadius, (int) actorPosY - playerRadius));
			final Vector<AbstractQuadNodeElement<Actor>> e2 = (Vector<AbstractQuadNodeElement<Actor>>) tree.getElements(new Vector2(
					(int) actorPosX - playerRadius, (int) actorPosY + playerRadius));
			final Vector<AbstractQuadNodeElement<Actor>> e3 = (Vector<AbstractQuadNodeElement<Actor>>) tree.getElements(new Vector2(
					(int) actorPosX + playerRadius, (int) actorPosY - playerRadius));
			final Vector<AbstractQuadNodeElement<Actor>> e4 = (Vector<AbstractQuadNodeElement<Actor>>) tree.getElements(new Vector2(
					(int) actorPosX + playerRadius, (int) actorPosY + playerRadius));

			collisionCandidates = new HashSet<Actor>();
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
		}

		final ArrayList<Actor> qtCollisionsList = new ArrayList<Actor>();
		for (final Actor a : collisionCandidates) {
			if (CollisionChecker.collisionActorActor(player, a)) {
				qtCollisionsList.add(a);
			}
		}

		if (allCollisionsList.size() != qtCollisionsList.size()) {
			// if (allCollisionsList.size() != qtCollisionsList.size() ||
			// allCollisionsList.size() > 0){
			System.out.println("___________________");
			System.out.println("tree size: " + tree.size());
			System.out.println("all checks: " + actorList.size());
			System.out.println("qt checked: " + collisionCandidates.size());
			System.out.println(">all collisions: " + allCollisionsList.size());
			System.out.println(">qt  collisions: " + qtCollisionsList.size());
			System.out.println("___________________");

			System.out.println("player: " + player.getX() + " x " + player.getY());
			System.out.println("___________________");
			System.out.println("all collisions");
			for (final Actor a : allCollisionsList) {
				printActor(a);
			}
			System.out.println("___________________");
			System.out.println("qt collisions");
			for (final Actor a : qtCollisionsList) {
				printActor(a);
			}
			System.out.println("___________________");
		}

		// System.out.println((double) collisionCandidates.size() + "/" +
		// actorList.size());
		qtCollisions += collisionCandidates.size();

		Assert.assertEquals("element count", actorList.size(), tree.size());
		return allCollisionsList.size() == qtCollisionsList.size();
	}

	private static void printActor(final Actor actor) {
		System.out.println(actor.getX() + " x " + actor.getY() + " id: " + actor.getId());
	}
}
