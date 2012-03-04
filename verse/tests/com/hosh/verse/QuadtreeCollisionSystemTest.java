package com.hosh.verse;

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
import com.hosh.verse.common.ActorFactory;
import com.hosh.verse.common.CollisionChecker;
import com.hosh.verse.common.VerseActor;
import com.hosh.verse.quadtree.AbstractQuadNodeElement;
import com.hosh.verse.quadtree.PointQuadTree;

public class QuadtreeCollisionSystemTest {

	private static VerseActor player;
	private static List<VerseActor> actorList;

	private PointQuadTree<VerseActor> tree;

	private static int width = 1000;
	private static int height = 1000;
	private static int objects = 10000;
	private static float actorPosX; // = MathUtils.random(width);
	private static float actorPosY; // = MathUtils.random(height);
	private static float actorRadius = 5.f;
	private final int depth = 5;
	private static Random rand;
	private static int qtCollisions = 0;

	@BeforeClass
	public static void setUp() throws Exception {
		rand = new Random(0);

		actorList = new ArrayList<VerseActor>();
		for (int i = 0; i < objects; ++i) {
			final VerseActor actor = ActorFactory.createActor(rand.nextInt(width), rand.nextInt(height), 5.f);
			// printActor(actor);
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
		player = ActorFactory.createActor(actorPosX, actorPosY, actorRadius);

		final ArrayList<VerseActor> allCollisionsList = new ArrayList<VerseActor>();
		for (final VerseActor a : actorList) {
			if (CollisionChecker.collistionActors(player, a)) {
				allCollisionsList.add(a);
				// printActor(a);
			}
		}

		tree = new PointQuadTree<VerseActor>(new Vector2(0, 0), new Vector2(width, height), depth, 10);

		for (final VerseActor a : actorList) {
			tree.insert((int) a.getPos().x, (int) a.getPos().y, a);
		}

		final int playerRadius = (int) actorRadius * 2;

		final boolean debug = true;
		final Set<VerseActor> collisionCandidates;
		if (debug) {
			collisionCandidates = tree.getElements((int) actorPosX, (int) actorPosY, playerRadius);
		} else {
			final Vector<AbstractQuadNodeElement<VerseActor>> e1 = (Vector<AbstractQuadNodeElement<VerseActor>>) tree
					.getElements(new Vector2((int) actorPosX - playerRadius, (int) actorPosY - playerRadius));
			final Vector<AbstractQuadNodeElement<VerseActor>> e2 = (Vector<AbstractQuadNodeElement<VerseActor>>) tree
					.getElements(new Vector2((int) actorPosX - playerRadius, (int) actorPosY + playerRadius));
			final Vector<AbstractQuadNodeElement<VerseActor>> e3 = (Vector<AbstractQuadNodeElement<VerseActor>>) tree
					.getElements(new Vector2((int) actorPosX + playerRadius, (int) actorPosY - playerRadius));
			final Vector<AbstractQuadNodeElement<VerseActor>> e4 = (Vector<AbstractQuadNodeElement<VerseActor>>) tree
					.getElements(new Vector2((int) actorPosX + playerRadius, (int) actorPosY + playerRadius));

			collisionCandidates = new HashSet<VerseActor>();
			for (final AbstractQuadNodeElement<VerseActor> e : e1) {
				collisionCandidates.add(e.getElement());
			}
			for (final AbstractQuadNodeElement<VerseActor> e : e2) {
				collisionCandidates.add(e.getElement());
			}
			for (final AbstractQuadNodeElement<VerseActor> e : e3) {
				collisionCandidates.add(e.getElement());
			}
			for (final AbstractQuadNodeElement<VerseActor> e : e4) {
				collisionCandidates.add(e.getElement());
			}
		}

		final ArrayList<VerseActor> qtCollisionsList = new ArrayList<VerseActor>();
		for (final VerseActor a : collisionCandidates) {
			if (CollisionChecker.collistionActors(player, a)) {
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

			System.out.println("player: " + player.getPos().x + " x " + player.getPos().y);
			System.out.println("___________________");
			System.out.println("all collisions");
			for (final VerseActor a : allCollisionsList) {
				printActor(a);
			}
			System.out.println("___________________");
			System.out.println("qt collisions");
			for (final VerseActor a : qtCollisionsList) {
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

	private static void printActor(final VerseActor actor) {
		System.out.println(actor.getPos().x + " x " + actor.getPos().y + " id: " + actor.getCharId());
	}
}
