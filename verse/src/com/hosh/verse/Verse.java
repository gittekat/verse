package com.hosh.verse;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.MathUtils;

public class Verse {
	public final long dimensionX;
	public final long dimensionY;
	private Actor player;
	private List<Actor> actorList;
	private List<Actor> actorsToCheckList;

	public Verse(final long dimensionX, final long dimensionY) {
		this.dimensionX = dimensionX;
		this.dimensionY = dimensionY;
		setPlayer(ActorFactory.createActor(1000.f, 1000.f, 16.f));

		actorList = new ArrayList<Actor>();
		actorList.add(ActorFactory.createActor(1100.f, 1100.f, 5.f)); // debug
																		// probe

		actorsToCheckList = new ArrayList<Actor>();
		for (int i = 0; i < 10000; ++i) {
			final Actor actor = ActorFactory.createActor(MathUtils.random(dimensionX), MathUtils.random(dimensionX), 5.f);
			actorsToCheckList.add(actor);
		}
	}

	public void update(final float deltaTime) {
		player.update(deltaTime);
		for (final Actor a : actorList) {
			a.update(deltaTime);
		}

		// final Stopwatch stopwatch = new Stopwatch().start();
		final ArrayList<Actor> collidedList = new ArrayList<Actor>();
		for (final Actor a : actorsToCheckList) {
			if (CollisionChecker.collistionActors(player, a)) {
				// System.err.println("collision!!!");

				// TODO resolve collision

				collidedList.add(a);
			}
		}
		// final long collisionProcessingTime = stopwatch.elapsedMillis();
		// Gdx.app.log("profiling", "collision checking took: " +
		// collisionProcessingTime);
	}

	public Actor getPlayer() {
		return player;
	}

	public void setPlayer(final Actor player) {
		this.player = player;
	}

	public List<Actor> getActorList() {
		return actorList;
	}
}
