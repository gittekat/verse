package com.hosh.verse;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import com.hosh.verse.quadtree.AbstractQuadNodeElement;
import com.hosh.verse.quadtree.PointQuadTree;

public class QuadtreeCollisionSystemTest {

	private static Actor player;
	private static List<Actor> actorList;

	private PointQuadTree<Actor> tree;

	private static int width = 200;
	private static int height = 200;
	private static int objects = 20000;
	private static float actorPosX; // = MathUtils.random(width);
	private static float actorPosY; // = MathUtils.random(height);
	private static float actorRadius = 2.f;
	private final int depth = 3;
	private static Random rand;
	private static List<Double> percentages;

	@BeforeClass
	public static void setUp() throws Exception {
		rand = new Random(0);
		percentages = new ArrayList<Double>();

		actorList = new ArrayList<Actor>();
		for (int i = 0; i < objects; ++i) {
			final Actor actor = ActorFactory.createActor(rand.nextInt(width), rand.nextInt(height), 2.f);
			// printActor(actor);
			actorList.add(actor);
		}
	}

	@Test
	public void testMultipleRuns() {
		actorPosX = rand.nextInt(width);
		actorPosY = rand.nextInt(height);
		System.out.println("width of deepest quadtree region: " + width / Math.pow(2, depth));
		System.out.println("player radius: " + actorRadius);
		if (width / Math.pow(2, depth) < actorRadius * 2) {
			System.err.println("WARNING: quadtree is too fine grained!");
		}
		final int tests = 1000;
		int fails = 0;
		for (int i = 0; i < tests; ++i) {
			rand = new Random(i);
			if (testCollisionSystem()) {
				fails++;
			}
		}

		Double average = 0.0;
		for (final Double percent : percentages) {
			average += percent;
		}
		average /= percentages.size();
		System.out.println("average percent of checked objects: " + average);

		System.out.println(fails + "/" + tests);

		Assert.assertTrue(average < 0.1);
		Assert.assertTrue(fails != 0);
	}

	public boolean testCollisionSystem() {
		actorPosX = rand.nextInt(width);
		actorPosY = rand.nextInt(height);
		player = ActorFactory.createActor(actorPosX, actorPosY, actorRadius);

		final ArrayList<Actor> allCollisionsList = new ArrayList<Actor>();
		for (final Actor a : actorList) {
			if (CollisionChecker.collistionActors(player, a)) {
				allCollisionsList.add(a);
				// printActor(a);
			}
		}

		tree = new PointQuadTree<Actor>(new Point(0, 0), new Dimension(width, height), depth, 10);

		for (final Actor a : actorList) {
			tree.insert((int) a.getPos().x, (int) a.getPos().y, a);
		}

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

		final ArrayList<Actor> qtCollisionsList = new ArrayList<Actor>();
		for (final AbstractQuadNodeElement<Actor> e : elements2) {
			if (CollisionChecker.collistionActors(player, e.getElement())) {
				qtCollisionsList.add(e.getElement());
				// printActor(e.getElement());
			}
		}

		if (allCollisionsList.size() != qtCollisionsList.size()) {
			System.out.println("___________________");
			System.out.println("tree size: " + tree.size());
			System.out.println("all checks: " + actorList.size());
			System.out.println("qt checked: " + elements2.size());
			System.out.println(">all collisions: " + allCollisionsList.size());
			System.out.println(">qt  collisions: " + qtCollisionsList.size());
			System.out.println("___________________");

			System.out.println("player: " + player.getPos().x + " x " + player.getPos().y);
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

		percentages.add((double) elements2.size() / (double) actorList.size());

		Assert.assertEquals("element count", actorList.size(), tree.size());
		return allCollisionsList.size() == qtCollisionsList.size();
	}

	private static void printActor(final Actor actor) {
		System.out.println(actor.getPos().x + " x " + actor.getPos().y + " id: " + actor.getId());
	}
}
