package com.hosh.verse;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.math.MathUtils;
import com.hosh.verse.quadtree.PointQuadTree;

public class Verse {
	public final int dimensionX;
	public final int dimensionY;
	private VerseActor player;
	private List<VerseActor> actorList;
	private PointQuadTree<VerseActor> qtTree;

	public Verse(final int dimensionX, final int dimensionY) {
		this.dimensionX = dimensionX;
		this.dimensionY = dimensionY;
		setPlayer(ActorFactory.createActor(500.f, 500.f, 5.f));

		actorList = new ArrayList<VerseActor>();
		// actorList.add(ActorFactory.createActor(1100.f, 1100.f, 5.f)); //
		// debug
		// probe
		for (int i = 0; i < 10000; ++i) {
			final VerseActor actor = ActorFactory.createActor(MathUtils.random(dimensionX), MathUtils.random(dimensionX), 5.f);
			actorList.add(actor);
		}

		final int depth = 5;
		qtTree = new PointQuadTree<VerseActor>(new Point(0, 0), new Dimension(dimensionX, dimensionY), depth, 10);
		for (final VerseActor a : actorList) {
			qtTree.insert((int) a.getPos().x, (int) a.getPos().y, a);
		}
		System.out.println("width of deepest quadtree region: " + dimensionX / Math.pow(2, depth));
	}

	public void update(final float deltaTime) {
		player.update(deltaTime);
		for (final VerseActor a : actorList) {
			a.update(deltaTime);
		}

		// // final Stopwatch stopwatch = new Stopwatch().start();
		// final ArrayList<VerseActor> collidedList = new
		// ArrayList<VerseActor>();
		// for (final VerseActor a : actorsToCheckList) {
		// if (CollisionChecker.collistionActors(player, a)) {
		// // System.err.println("collision!!!");
		//
		// // TODO resolve collision
		//
		// collidedList.add(a);
		// }
		// }
		// // final long collisionProcessingTime = stopwatch.elapsedMillis();
		// // Gdx.app.log("profiling", "collision checking took: " +
		// // collisionProcessingTime);
	}

	public VerseActor getPlayer() {
		return player;
	}

	public void setPlayer(final VerseActor player) {
		this.player = player;
	}

	public List<VerseActor> getActorList() {
		return actorList;
	}

	public Set<VerseActor> getVisibleActors() {
		final int visibleRadius = 10;
		final int actorPosX = (int) player.getPos().x;
		final int actorPosY = (int) player.getPos().y;

		final Set<VerseActor> visibleActors = qtTree.getElements(actorPosX, actorPosY, visibleRadius);
		return visibleActors;
	}
}
