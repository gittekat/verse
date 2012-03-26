package com.hosh.verse;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sfs2x.client.SmartFox;
import sfs2x.client.core.BaseEvent;
import sfs2x.client.core.IEventListener;
import sfs2x.client.core.SFSEvent;
import sfs2x.client.entities.User;
import sfs2x.client.requests.ExtensionRequest;
import sfs2x.client.requests.JoinRoomRequest;
import sfs2x.client.requests.LoginRequest;
import sfs2x.client.requests.LogoutRequest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.hosh.verse.common.Interpreter;
import com.hosh.verse.common.VerseActor;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;

public class SmartFoxTest implements IEventListener {
	SmartFox sfsClient;
	boolean setup = false;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		initSmartFox();
		connectToServer("192.168.178.35", 9933);

		while (!setup) {
			System.out.println("waiting...");
			Thread.sleep(2000);
		}
	}

	@After
	public void tearDown() throws Exception {
		if (sfsClient != null) {
			sfsClient.removeAllEventListeners();
			sfsClient.send(new LogoutRequest());
			sfsClient.disconnect();
		}
	}

	@Test
	public void test1() {
		final int i = 62;
		final ISFSObject sfso = new SFSObject();
		sfso.putInt("posX", i);
		sfso.putInt("posY", i);
		System.out.println("sending: " + i);
		sfsClient.send(new ExtensionRequest("test", sfso));
	}

	@Test
	public void test2() throws InterruptedException {
		for (int i = 0; i < 100; ++i) {
			final ISFSObject sfso = new SFSObject();
			sfso.putInt("posX", i);
			sfso.putInt("posY", i);
			System.out.println("sending: " + i);
			sfsClient.send(new ExtensionRequest("test", sfso));
			Thread.sleep(1000);
		}
	}

	private void initSmartFox() {
		// Instantiate SmartFox client
		sfsClient = new SmartFox(false);

		sfsClient.addEventListener(SFSEvent.CONNECTION, this);
		sfsClient.addEventListener(SFSEvent.CONNECTION_LOST, this);
		sfsClient.addEventListener(SFSEvent.LOGIN, this);

		sfsClient.addEventListener(SFSEvent.LOGIN_ERROR, new IEventListener() {

			@Override
			public void dispatch(final BaseEvent arg0) throws SFSException {
				System.out.println("woss homms gsagt??");
				Gdx.app.exit();
			}
		});

		sfsClient.addEventListener(SFSEvent.LOGOUT, new IEventListener() {

			@Override
			public void dispatch(final BaseEvent event) throws SFSException {
				System.out.println("logout - I played verse");
			}
		});

		sfsClient.addEventListener(SFSEvent.ROOM_JOIN, this);
		sfsClient.addEventListener(SFSEvent.USER_ENTER_ROOM, this);
		sfsClient.addEventListener(SFSEvent.USER_EXIT_ROOM, this);
		sfsClient.addEventListener(SFSEvent.PUBLIC_MESSAGE, this);
		sfsClient.addEventListener(SFSEvent.EXTENSION_RESPONSE, this);

		sfsClient.addEventListener(SFSEvent.CONFIG_LOAD_FAILURE, new IEventListener() {

			@Override
			public void dispatch(final BaseEvent arg0) throws SFSException {
				System.out.println("config loading failure!");
			}
		});

		sfsClient.addEventListener(SFSEvent.CONFIG_LOAD_SUCCESS, new IEventListener() {

			@Override
			public void dispatch(final BaseEvent arg0) throws SFSException {
				System.out.println("config loaded successfully!");
			}
		});
	}

	private void shutdownSmartFox() {
		if (sfsClient != null) {
			sfsClient.removeAllEventListeners();
			sfsClient.send(new LogoutRequest());
			sfsClient.disconnect();
		}
	}

	private void connectToServer(final String ip, final int port) {
		// connect() method is called in separate thread
		// so it does not block the UI
		final SmartFox sfs = sfsClient;
		new Thread() {
			@Override
			public void run() {
				// sfs.loadConfig();
				// sfs.connect();
				sfs.connect(ip, port);
			}
		}.start();
	}

	@Override
	public void dispatch(final BaseEvent event) throws SFSException {
		if (event.getType().equalsIgnoreCase(SFSEvent.CONNECTION)) {
			if (event.getArguments().get("success").equals(true)) {
				sfsClient.send(new LoginRequest("hosh", "109", "VerseZone"));
				System.out.println("sfs: connecting...");
			}
			// otherwise error message is shown
			else {
				System.out.println("sfs: connection error");
			}
		} else if (event.getType().equalsIgnoreCase(SFSEvent.CONNECTION_LOST)) {
			shutdownSmartFox();

			System.out.println("sfs: connection lost");

		} else if (event.getType().equalsIgnoreCase(SFSEvent.LOGIN)) {
			sfsClient.send(new JoinRoomRequest("VerseRoom"));
			setup = true;
		} else if (event.getType().equalsIgnoreCase(SFSEvent.ROOM_JOIN)) {
			System.out.println("sfs: " + sfsClient.getLastJoinedRoom().getName());
		} else if (event.getType().equalsIgnoreCase(SFSEvent.ROOM_JOIN_ERROR)) {
			System.out.println("room join error");
		} else if (event.getType().equals(SFSEvent.USER_ENTER_ROOM)) {
			final User user = (User) event.getArguments().get("user");
			System.out.println(user.getId() + " entered the room");
		} else if (event.getType().equals(SFSEvent.USER_EXIT_ROOM)) {
			final User user = (User) event.getArguments().get("user");
			System.out.println(user.getId() + " left the room");
		} else if (event.getType().equals(SFSEvent.PUBLIC_MESSAGE)) {
			final User sender = (User) event.getArguments().get("sender");
			final String msg = event.getArguments().get("message").toString();
			System.out.println("[" + sender.getName() + "]: " + msg + "\n");
		}
		if (event.getType().equalsIgnoreCase(SFSEvent.EXTENSION_RESPONSE)) {

			final String cmd = event.getArguments().get("cmd").toString();

			if ("initialPlayerData".equals(cmd)) {
				ISFSObject resObj = new SFSObject();
				resObj = (ISFSObject) event.getArguments().get("params");

				final int charId = resObj.getInt(VerseActor.CHAR_ID);
				final Float posX = resObj.getFloat(VerseActor.POS_X);
				final Float posY = resObj.getFloat(VerseActor.POS_Y);
				final String name = resObj.getUtfString(VerseActor.NAME);
				final int exp = resObj.getInt(VerseActor.EXP);
				final int level = resObj.getInt(VerseActor.LEVEL);
				final float maxHp = resObj.getFloat(VerseActor.MAX_HP);
				final float curHp = resObj.getFloat(VerseActor.CUR_HP);
				final float radius = resObj.getFloat(VerseActor.RADIUS);

			}

			if ("playerData".equals(cmd)) {
				ISFSObject resObj = new SFSObject();
				resObj = (ISFSObject) event.getArguments().get("params");

				final Float x = resObj.getFloat("x");
				final Float y = resObj.getFloat("y");
				final Vector2 posVector = new Vector2(x, y);
			}

			if ("actor".equals(cmd)) {
				ISFSObject resObj = new SFSObject();
				resObj = (ISFSObject) event.getArguments().get("params");

				final VerseActor actor = Interpreter.createActor(resObj);
			}

			if ("player".equals(cmd)) {
				ISFSObject resObj = new SFSObject();
				resObj = (ISFSObject) event.getArguments().get("params");

				final VerseActor actor = Interpreter.createActor(resObj);

			}
		}
	}

}
