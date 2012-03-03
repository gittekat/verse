package com.hosh.verse.server;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.hosh.verse.common.ActorFactory;
import com.hosh.verse.common.VerseActor;
import com.hosh.verse.quadtree.PointQuadTree;

public class Verse {
	Connection dbConnection;

	public final int dimensionX;
	public final int dimensionY;
	private List<VerseActor> playerList;
	private List<VerseActor> actorList;
	private PointQuadTree<VerseActor> qtTree;

	public Verse(final Connection dbConnection, final int dimensionX, final int dimensionY) {
		this.dbConnection = dbConnection;

		this.dimensionX = dimensionX;
		this.dimensionY = dimensionY;

		actorList = new ArrayList<VerseActor>();
		for (int i = 0; i < 10000; ++i) {
			final VerseActor actor = ActorFactory.createActor(MathUtils.random(dimensionX), MathUtils.random(dimensionX), 5.f);
			actorList.add(actor);
		}

		final int depth = 5;
		qtTree = new PointQuadTree<VerseActor>(new Vector2(0, 0), new Vector2(dimensionX, dimensionY), depth, 10);
		for (final VerseActor a : actorList) {
			qtTree.insert((int) a.getPos().x, (int) a.getPos().y, a);
		}
		System.out.println("width of deepest quadtree region: " + dimensionX / Math.pow(2, depth));

		playerList = new ArrayList<VerseActor>();
	}

	public void update(final float deltaTime) {
		for (final VerseActor player : playerList) {
			player.update(deltaTime);
		}

		for (final VerseActor a : actorList) {
			a.update(deltaTime);
		}

		// final ArrayList<VerseActor> collidedList = new
		// ArrayList<VerseActor>();
		// for (final VerseActor a : playerList) {
		// if (CollisionChecker.collistionActors(player, a)) {
		// // TODO resolve collision
		// collidedList.add(a);
		// }
		// }
	}

	public List<VerseActor> getActorList() {
		return actorList;
	}

	public Set<VerseActor> getVisibleActors(final VerseActor player) {
		final int visibleRadius = 10;
		final int actorPosX = (int) player.getPos().x;
		final int actorPosY = (int) player.getPos().y;

		final Set<VerseActor> visibleActors = qtTree.getElements(actorPosX, actorPosY, visibleRadius);
		return visibleActors;
	}

	public void addPlayer(final VerseActor player) {
		playerList.add(player);
	}
}
