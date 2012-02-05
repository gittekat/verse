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
		actorList.add(player);
	}

	public void update(final float deltaTime) {
		for (final Actor a : actorList) {
			a.update(deltaTime);
		}
	}

	public Actor getPlayer() {
		return player;
	}

	public void setPlayer(final Actor player) {
		this.player = player;
	}
}
