package com.hosh.verse;

import java.util.ArrayList;
import java.util.List;

public class Verse {
	public final long dimensionX;
	public final long dimensionY;
	private Actor player;
	private List<Actor> actorList;

	public Verse(final long dimensionX, final long dimensionY) {
		this.dimensionX = dimensionX;
		this.dimensionY = dimensionY;
		setPlayer(new Actor(1000.f, 1000.f, 16.f));

		actorList = new ArrayList<Actor>();
		actorList.add(new Actor(1100.f, 1100.f, 5.f)); // debug probe
		// actorList.add(new Actor(2100.f, 2100.f, 5.f));
	}

	public void update(final float deltaTime) {
		player.update(deltaTime);
		for (final Actor a : actorList) {
			a.update(deltaTime);
		}

		for (final Actor a : actorList) {
			if (CollisionChecker.collistionActors(player, a)) {
				System.err.println("collision!!!");

				// TODO resolve collision

				actorList.remove(a);
				if (actorList.isEmpty()) {
					break;
				}
			}
		}
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
