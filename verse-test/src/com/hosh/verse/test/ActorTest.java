package com.hosh.verse.test;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.hosh.verse.common.Actor;
import com.hosh.verse.common.MovementData;
import com.hosh.verse.common.utils.ActorUtils;

public class ActorTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testUpdateActor() {
		// receive movementData
		final MovementData moveData = movementDataHelper(0, 100, 100, 200, 200, 20);

		// create unidentified actor
		final Actor actor = ActorUtils.createUnidentifiedActor(moveData);

		// update actor
		while (actor.getX() != actor.getTargetX()) {
			actor.update(0.000314944f);
		}

		// receive another movement package
		// MovementData moveData2 = movementDataHelper(0, 110, 110, 200, 200,
		// 20);

	}

	private MovementData movementDataHelper(final int id, final float posX, final float posY, final float targetPosX,
			final float targetPosY, final float speed) {
		final MovementData moveData = new MovementData();
		moveData.setId(id);
		moveData.setPosX(posX);
		moveData.setPosY(posY);
		moveData.setTargetPosX(targetPosX);
		moveData.setTargetPosY(targetPosY);
		moveData.setSpeed(speed);

		return moveData;
	}

}
