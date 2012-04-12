package com.hosh.verse.test;

import java.sql.Connection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.google.common.base.Stopwatch;
import com.google.common.eventbus.EventBus;
import com.hosh.verse.common.Actor;
import com.hosh.verse.common.CollisionChecker;
import com.hosh.verse.common.IPositionable;
import com.hosh.verse.common.Stats;
import com.hosh.verse.common.VerseActor;
import com.hosh.verse.common.uniformgrid.UniformGrid;
import com.hosh.verse.server.database.DatabaseAccessor;
import com.hosh.verse.test.common.TestUtils;

public class UniformGridTest {

	private static Connection connection;
	private static Stats baseStats;

	@BeforeClass
	public static void setUp() throws Exception {
		connection = TestUtils.getDBConnection();
		baseStats = DatabaseAccessor.loadBlueprint(connection, 1);
	}

	@Test
	public void testUniformGridCreation() {
		final int w = 10000;
		final int h = 10240;
		final int gridSize = 1024;
		final UniformGrid grid = new UniformGrid(w, h, gridSize);

		Assert.assertTrue(grid.getDimensionX() % gridSize == 0);
		Assert.assertTrue(grid.getDimensionY() % gridSize == 0);
	}

	@Test
	public void testMortonNumbers() {
		final UniformGrid grid = new UniformGrid(100, 100, 50);

		printPossibleMortonNumbers(grid);

		Assert.assertEquals(CollisionChecker.mortonNumber(grid.getGridX() - 1, grid.getGridY() - 1),
				grid.getMortonNumber(grid.getDimensionX() - 1, grid.getDimensionY() - 1));
	}

	@Test
	public void testActorInsertion() {
		final UniformGrid grid = new UniformGrid(10240, 10240, 1024);

		final Actor a1 = new Actor(0, "hosh", 0, baseStats, "a1", 0, 100, 100, 0, 10, 10, 0);
		grid.addEntity(a1);
		Assert.assertEquals(1, grid.size());

		grid.addEntity(a1);
		Assert.assertEquals(1, grid.size());

		// insert actor that is out of bounds
		final Actor a2 = new Actor(1, "hosh", 0, baseStats, "a1", 0, 10240, 10240, 0, 10, 10, 0);
		grid.addEntity(a2);
		Assert.assertEquals(1, grid.size());

		Assert.assertTrue(grid.checkIntegrity());
	}

	@Test
	public void testMultipleActorInsertion() {
		final UniformGrid grid = new UniformGrid(10240, 10240, 1024);

		final int actorCnt = 1000000;
		for (int i = 0; i < actorCnt; ++i) {
			final int w = MathUtils.random(grid.getDimensionX() - 1);
			final int h = MathUtils.random(grid.getDimensionY() - 1);
			final Actor actor = new Actor(i, "hosh", 0, baseStats, "a1", 0, w, h, 0, 10, 10, 0);
			grid.addEntity(actor);
		}

		Assert.assertTrue(grid.checkIntegrity());

		final Set<IPositionable> actors = grid.getEntities();

		Assert.assertEquals(actorCnt, actors.size());
	}

	@Test
	public void testUpdateActor() {
		final EventBus eventBus = new EventBus("verse");
		final UniformGrid grid = new UniformGrid(10240, 10240, 1024);
		eventBus.register(grid);

		final Actor a1 = new Actor(eventBus, 109, "hosh", 0, baseStats, "a1", 0, 1000, 1000, 0, 10, 10, 0);
		grid.addEntity(a1);
		final int oldMortonNumber = grid.getMortonNumber(a1);

		final VerseActor a2 = new VerseActor(1009, "superkato", 0, 0, 1, 1, 1000, 1000, 0, 16);

		final Vector2 targetPos = new Vector2(1000, 1200);
		a1.setCurSpeed(20);
		a2.setCurSpeed(20);
		a1.setTargetPos(targetPos);
		a2.setTargetPos(targetPos);

		Assert.assertTrue(a1.getStats().getRotation_speed() == a2.getRotationSpeed());
		while (a1.getPos().y < 1030) {
			a1.update(0.005f);
			a2.update(0.005f);
		}
		Assert.assertTrue(a1.getPos().equals(a2.getCurPos()));

		final int newMortonNumber = grid.getMortonNumber(a1);
		Assert.assertTrue(oldMortonNumber != newMortonNumber);
	}

	@Test
	public void testGetProximity() {
		final UniformGrid grid = new UniformGrid(10240, 10240, 1024);
		final Actor a1 = new Actor(0, "hosh", 0, baseStats, "a1", 0, 100, 100, 0, 10, 10, 0);
		final Actor a2 = new Actor(1, "hosh", 0, baseStats, "a2", 0, 1000, 1000, 0, 10, 10, 0);
		grid.addEntity(a1);
		grid.addEntity(a2);

		Assert.assertTrue(grid.size() == 2);

		List<IPositionable> entities = grid.getEntitiesFAST(1000, 1000, 1500, 1500);
		for (final IPositionable entity : entities) {
			final Actor actor = (Actor) entity;
			System.out.println(actor.getName() + " @" + actor.getPos());
		}
		Assert.assertTrue(entities.size() == 2);

		entities = grid.getEntities(1000, 1000, 1500, 1500);
		for (final IPositionable entity : entities) {
			final Actor actor = (Actor) entity;
			System.out.println(actor.getName() + " @" + actor.getPos());
		}
		Assert.assertTrue(entities.size() == 1);
	}

	@Test
	public void testProximitySpeed() {
		final UniformGrid grid = new UniformGrid(10240, 10240, 1024);

		final int actorCnt = 1000000;
		for (int i = 0; i < actorCnt; ++i) {
			final int w = MathUtils.random(grid.getDimensionX() - 1);
			final int h = MathUtils.random(grid.getDimensionY() - 1);
			final Actor actor = new Actor(i, "hosh", 0, baseStats, "a1", 0, w, h, 0, 10, 10, 0);
			grid.addEntity(actor);
		}

		Assert.assertTrue(grid.checkIntegrity());

		// warmup
		grid.getEntities(5000, 5000, 2000, 2000);

		final int x = 1000;
		final int y = 1000;
		final int w = 1500;
		final int h = 1500;

		// ----- rectFAST
		final Stopwatch stopwatchRectFAST = new Stopwatch().start();

		final List<IPositionable> fastEntities = grid.getEntitiesFAST(x, y, w, h);

		final long durationRectFAST = stopwatchRectFAST.elapsedMillis();
		stopwatchRectFAST.stop();
		System.out.println("rectFAST time: " + durationRectFAST + " found: " + fastEntities.size());

		// ----- rect
		final Stopwatch stopwatchRect = new Stopwatch().start();

		final List<IPositionable> entitiesRect = grid.getEntities(x, y, w, h);

		final long durationRect = stopwatchRect.elapsedMillis();
		stopwatchRect.stop();
		System.out.println("rect     time: " + durationRect + " found: " + entitiesRect.size());

		// ----- radius
		final Stopwatch stopwatchRadius = new Stopwatch().start();

		final List<IPositionable> entitiesRadius = grid.getEntities(x, y, w / 2);

		final long durationRadius = stopwatchRadius.elapsedMillis();
		stopwatchRadius.stop();
		System.out.println("radius   time: " + durationRadius + " found: " + entitiesRadius.size());
	}

	private int printPossibleMortonNumbers(final UniformGrid grid) {
		final Set<Integer> mNums = new HashSet<Integer>();
		for (int i = 0; i < grid.getDimensionX(); ++i) {
			for (int j = 0; j < grid.getDimensionY(); ++j) {
				mNums.add(grid.getMortonNumber(i, j));
			}
		}

		for (final Integer num : mNums) {
			System.out.println(num);
		}

		System.out.println("different morton numbers: " + mNums.size());

		return mNums.size();
	}
}
