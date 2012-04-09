package com.hosh.verse.test.eventbus;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import com.badlogic.gdx.math.Vector2;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class EventBusTest {
	private static EventBus eventBus;

	@BeforeClass
	public static void setUp() {
		eventBus = new EventBus("EventBusTest");
	}

	@Test
	public void shouldReceiveEvent() throws Exception {

		// given
		// final EventBus eventBus = new EventBus("test");
		final EventListener listener = new EventListener();

		eventBus.register(listener);

		// when
		eventBus.post(new OurTestEvent(200));

		// then
		Assert.assertTrue(listener.getLastMessage() == 200);
	}

	@Test
	public void shouldReceiveMultipleEvents() throws Exception {

		// given
		// final EventBus eventBus = new EventBus("test");
		final MultipleListener multiListener = new MultipleListener();

		eventBus.register(multiListener);

		// when
		eventBus.post(new Integer(100));
		eventBus.post(new Long(800));

		// then
		Assert.assertTrue(multiListener.getLastInteger().equals(100));
		Assert.assertTrue(multiListener.getLastLong().equals(800L));
	}

	@Test
	public void testPosChange() {
		final TestActor actor = new TestActor();
		final TestGrid grid = new TestGrid();
		eventBus.register(grid);

		grid.addActor(actor);

		actor.update();
	}

	public class PosChangeEvent {
		private int actorId;

		private Vector2 newPos;

		public PosChangeEvent(final int actorId, final Vector2 newPos) {
			this.actorId = actorId;
			this.newPos = newPos;
		}

		public int getActorId() {
			return actorId;
		}

		public Vector2 getNewPos() {
			return newPos;
		}
	}

	public class TestActor {
		public int id = 109;
		private Vector2 pos = new Vector2(1009, 109);

		public void update() {
			pos.add(1, 2);
			eventBus.post(new PosChangeEvent(id, pos));
		}

		public Vector2 getPos() {
			return pos;
		}

		public void setPos(final Vector2 pos) {
			this.pos = pos;
		}
	}

	public class TestGrid { // listener
		Map<Integer, TestActor> actorMap = new HashMap<Integer, EventBusTest.TestActor>();

		public void addActor(final TestActor actor) {
			actorMap.put(actor.id, actor);
		}

		@Subscribe
		public void listen2PositionChange(final PosChangeEvent posChange) {
			final TestActor actor = actorMap.get(posChange.getActorId());
			System.out.println(posChange.getNewPos());
			System.out.println(actor.getPos());
		}
	}

	public class OurTestEvent {

		private final int message;

		public OurTestEvent(final int message) {
			this.message = message;
		}

		public int getMessage() {
			return message;
		}
	}

	public class EventListener {

		public int lastMessage = 0;

		@Subscribe
		public void listen(final OurTestEvent event) {
			lastMessage = event.getMessage();
		}

		public int getLastMessage() {
			return lastMessage;
		}
	}

	public class MultipleListener {

		public Integer lastInteger;
		public Long lastLong;

		@Subscribe
		public void listenInteger(final Integer event) {
			lastInteger = event;
		}

		@Subscribe
		public void listenLong(final Long event) {
			lastLong = event;
		}

		public Integer getLastInteger() {
			return lastInteger;
		}

		public Long getLastLong() {
			return lastLong;
		}
	}

}
