package com.hosh.verse.server;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hosh.verse.common.Actor;
import com.hosh.verse.common.CollisionChecker;
import com.hosh.verse.common.IPositionable;
import com.hosh.verse.common.uniformgrid.UniformGrid;
import com.hosh.verse.server.database.DatabaseAccessor;

public class Verse {
	Connection dbConnection;

	public final int dimensionX;
	public final int dimensionY;
	private List<Actor> actorList;
	private UniformGrid grid;
	private int gridSize = 1024;

	private Map<Integer, Actor> playerControlled;

	public Verse(final Connection dbConnection, final int dimensionX, final int dimensionY) {
		this.dbConnection = dbConnection;

		this.dimensionX = dimensionX;
		this.dimensionY = dimensionY;
		grid = new UniformGrid(dimensionX, dimensionY, gridSize);

		actorList = DatabaseAccessor.loadActors(dbConnection);
		playerControlled = new HashMap<Integer, Actor>();

		for (final Actor actor : actorList) {
			grid.addEntity(actor);
		}

	}

	public void update(final float deltaTime) {
		for (final Actor actor : actorList) {
			actor.update(deltaTime);

			final List<IPositionable> collidedList = new ArrayList<IPositionable>();
			final List<IPositionable> candidates = grid.getEntitiesFAST((int) actor.getPos().x - gridSize, (int) actor.getPos().y
					- gridSize, 2 * gridSize, 2 * gridSize);
			for (final IPositionable a : candidates) {
				((Actor) a).getName();
				if (!((Actor) a).equals(actor) && CollisionChecker.collisionActorActor(actor, (Actor) a)) {
					collidedList.add(a);
					System.out.println("collision!!!");
				}
			}
		}
	}

	public List<Actor> getActorList() {
		return actorList;
	}

	public List<IPositionable> getVisibleActors(final Actor player) {
		final int visibleRadius = 10;
		final int actorPosX = (int) player.getPos().x;
		final int actorPosY = (int) player.getPos().y;

		final List<IPositionable> visibleActors = grid.getEntities(actorPosX, actorPosY, visibleRadius);

		return visibleActors;
	}

	public void markAsPlayerControlled(final Actor player) {
		playerControlled.put(player.getId(), player);
	}

	public void unmarkAsPlayerControlled(final Integer id) {
		playerControlled.remove(id);
	}

	public Map<Integer, Actor> getPlayerControlledActors() {
		return playerControlled;
	}

	public void movePlayer(final Integer id, final Float targetPosX, final Float targetPosY, final Float speed) {
		final Actor player = playerControlled.get(id);
		// player.setTargetPos(new Vector2(targetPosX, targetPosY));
		player.setTargetPos(targetPosX, targetPosY);
		player.setCurSpeed(speed);
	}
}
