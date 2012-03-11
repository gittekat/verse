package com.hosh.verse;

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
import com.hosh.verse.common.Interpreter;
import com.hosh.verse.common.CollisionChecker;
import com.hosh.verse.common.VerseActor;
import com.hosh.verse.quadtree.AbstractQuadNodeElement;
import com.hosh.verse.quadtree.PointQuadTree;

public class ActorCollisionTest {

	private static VerseActor a1;
	private static VerseActor a2;
	private static VerseActor a3;
	private static VerseActor a4;
	private static List<VerseActor> actorList;

	private PointQuadTree<VerseActor> tree;

	private static int width = 1000;
	private static int height = 1000;
	private static float actorPosX = 251.f;
	private static float actorPosY = 251.f;
	private static float actorRadius = 5.f;

	@BeforeClass
	public static void setUp() throws Exception {
		a1 = Interpreter.createActor(100.f, 100.f, 5.f);
		a2 = Interpreter.createActor(119.999f, 100.f, 15.f);
		a3 = Interpreter.createActor(120.001f, 100.f, 15.f);
		a4 = Interpreter.createActor(actorPosX, actorPosY, actorRadius);

		actorList = new ArrayList<VerseActor>();
		for (int i = 0; i < 1000000; ++i) {
			final VerseActor actor = Interpreter.createActor(MathUtils.random(width), MathUtils.random(height), 5.f);
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
		for (final VerseActor a : actorList) {
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
		tree = new PointQuadTree<VerseActor>(new Vector2(0, 0), new Vector2(width, height), depth, 40);

		for (final VerseActor a : actorList) {
			tree.insert((int) a.getPos().x, (int) a.getPos().y, a);
		}

		final Stopwatch stopwatch = new Stopwatch().start();

		final int playerRadius = (int) actorRadius * 2;
		final Vector<AbstractQuadNodeElement<VerseActor>> e1 = (Vector<AbstractQuadNodeElement<VerseActor>>) tree.getElements(new Vector2(
				(int) actorPosX - playerRadius, (int) actorPosY - playerRadius));
		final Vector<AbstractQuadNodeElement<VerseActor>> e2 = (Vector<AbstractQuadNodeElement<VerseActor>>) tree.getElements(new Vector2(
				(int) actorPosX - playerRadius, (int) actorPosY + playerRadius));
		final Vector<AbstractQuadNodeElement<VerseActor>> e3 = (Vector<AbstractQuadNodeElement<VerseActor>>) tree.getElements(new Vector2(
				(int) actorPosX + playerRadius, (int) actorPosY - playerRadius));
		final Vector<AbstractQuadNodeElement<VerseActor>> e4 = (Vector<AbstractQuadNodeElement<VerseActor>>) tree.getElements(new Vector2(
				(int) actorPosX + playerRadius, (int) actorPosY + playerRadius));

		final Set<VerseActor> collisionCandidates = new HashSet<VerseActor>();
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

		int cnt = 0;
		for (final VerseActor a : collisionCandidates) {
			if (CollisionChecker.collistionActors(a4, a)) {
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
