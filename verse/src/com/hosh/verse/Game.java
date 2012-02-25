package com.hosh.verse;

import java.util.HashMap;
import java.util.Set;

import sfs2x.client.SmartFox;
import sfs2x.client.core.BaseEvent;
import sfs2x.client.core.IEventListener;
import sfs2x.client.core.SFSEvent;
import sfs2x.client.entities.User;
import sfs2x.client.requests.ExtensionRequest;
import sfs2x.client.requests.JoinRoomRequest;
import sfs2x.client.requests.LoginRequest;
import sfs2x.client.requests.PublicMessageRequest;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;

import de.exitgames.client.photon.DebugLevel;
import de.exitgames.client.photon.EventData;
import de.exitgames.client.photon.IPhotonPeerListener;
import de.exitgames.client.photon.LiteEventCode;
import de.exitgames.client.photon.LiteEventKey;
import de.exitgames.client.photon.LiteOpCode;
import de.exitgames.client.photon.LiteOpKey;
import de.exitgames.client.photon.OperationResponse;
import de.exitgames.client.photon.PhotonPeer;
import de.exitgames.client.photon.StatusCode;
import de.exitgames.client.photon.TypedHashMap;

public class Game implements ApplicationListener, IEventListener {
	private int WIDTH;
	private int HEIGHT;
	private int HALF_WIDTH;
	private int HALF_HEIGHT;

	private Verse verse;

	private OrthographicCamera cam;

	BitmapFont font;
	private SpriteBatch batch;
	private Texture ship;
	private TextureRegion shipRegion;
	private Texture shield;
	private TextureRegion shieldRegion;
	private Texture planet;
	private TextureRegion planetRegion;
	private Pixmap pixmap;
	private Texture pixmapTexture;
	Vector3 touchPoint;

	private VerseActor player;

	// photon
	private PhotonPeer peer;
	private String photonStatus;
	private String photonMessage;

	// smartfox
	SmartFox sfsClient;
	IEventListener evtListener;

	@Override
	public void create() {
		WIDTH = Gdx.graphics.getWidth();
		HEIGHT = Gdx.graphics.getHeight();
		HALF_WIDTH = WIDTH / 2;
		HALF_HEIGHT = HEIGHT / 2;

		verse = new Verse(1000, 1000);
		player = verse.getPlayer();

		font = new BitmapFont();
		font.setColor(Color.RED);

		batch = new SpriteBatch();

		cam = new OrthographicCamera(WIDTH, HEIGHT);
		cam.position.set(HALF_WIDTH, HALF_HEIGHT, 0);

		// ship = new Texture(Gdx.files.internal("triangle_32.png"));
		ship = new Texture(Gdx.files.internal("avatar_32.png"));
		ship.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		shipRegion = new TextureRegion(ship);

		shield = new Texture(Gdx.files.internal("shield_32.png"));
		shield.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		shieldRegion = new TextureRegion(shield);

		planet = new Texture(Gdx.files.internal("planet_128.png"));
		planet.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		planetRegion = new TextureRegion(planet);

		touchPoint = new Vector3();

		pixmap = new Pixmap(32, 32, Pixmap.Format.Alpha);
		pixmap.setColor(0.f, 0.f, 0.f, 1.f);
		pixmap.fill();
		pixmapTexture = new Texture(pixmap);

		// tests photon
		final boolean photon = false;
		if (photon) {
			final MyPhotonListener listener = new MyPhotonListener();
			peer = new PhotonPeer(listener);
			peer.connect("192.168.178.35:5055", "Lite");
		}

		// tests smartfox
		final boolean smartfox = true;
		if (smartfox) {
			initSmartFox();
			connectToServer("192.168.178.35", 9933);
		}
	}

	@Override
	public void render() {

		// photon
		// peer.service();
		// photon

		handleInput();
		verse.update(Gdx.graphics.getDeltaTime());

		// cam.update();
		// cam.apply(gl);

		Gdx.gl.glClearColor(1.f, 1.f, 1.f, 0.f);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glEnable(GL10.GL_BLEND);

		batch.begin();
		{
			final Set<VerseActor> visibleActors = verse.getVisibleActors();
			font.draw(batch, "visible: " + visibleActors.size(), 20, 60);
			// System.out.println(visibleActors.size());
			drawHUD();

			drawPlayer();

			for (final VerseActor a : visibleActors) {

				final Vector2 pos = getScreenCoordinates(a.getPos());

				// System.out.println(pos);
				final int size = (int) a.getBounds().radius;
				pixmap.drawRectangle(0, 0, size, size);
				batch.setColor(0, 0, 0, 1);
				batch.draw(pixmapTexture, pos.x - 1, pos.y - 1, size + 2, size + 2);
				batch.setColor(1.f, 0.f, 0.f, 1.f);
				batch.draw(pixmapTexture, pos.x, pos.y, size, size);

				// batch.setColor(1.f, 1.f, 1.f, 1.f);
				// batch.draw(planetRegion, pos.x, pos.y, 16, 16, 128, 128,
				// 0.9f, 0.9f, 0.f);
			}

		}
		batch.end();
	}

	private Vector2 getScreenCoordinates(final Vector2 objPos) {
		// player coordinates
		final Vector2 pos = objPos.cpy().sub(player.getPos());

		// screen coordinates
		pos.set(HALF_WIDTH + pos.x, HALF_HEIGHT + pos.y);

		return pos;
	}

	private void drawPlayer() {
		// Gdx.gl.glEnable(GL10.GL_DITHER);
		batch.setColor(0.f, 0.f, 0.f, player.getShieldStrength());
		batch.draw(shieldRegion, HALF_WIDTH - 16, HALF_HEIGHT - 16, 16, 16, 32, 32, 0.95f, 0.95f, 0.f);
		batch.setColor(1.f, 1.f, 1.f, 1.f);
		batch.draw(shipRegion, HALF_WIDTH - 16, HALF_HEIGHT - 16, 16, 16, 32, 32, 0.95f, 0.95f, player.getRotationAngle());
	}

	private void drawHUD() {
		drawDebugInfo();
		drawRadar();
	}

	private void drawDebugInfo() {
		font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 20, 20);
		font.draw(batch, "Pos: " + player.getPos().x + " x " + player.getPos().y, 20, 40);
		font.draw(batch, "Status: " + photonStatus + " - " + photonMessage, 20, 80);
	}

	private void drawRadar() {
		final int size = 80;
		pixmap.drawRectangle(0, 0, size, size);
		batch.setColor(0, 0, 0, 1);
		final int x = HALF_WIDTH - size / 2;
		final int y = 20;
		batch.draw(pixmapTexture, x - 1, y - 1, size + 2, size + 2);
		batch.setColor(0.95f, 0.65f, 0.1f, 1.f);
		batch.draw(pixmapTexture, x, y, size, size);
	}

	private void handleInput() {
		if (Gdx.input.justTouched()) {
			cam.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
			System.out.println(touchPoint);

			final float posX = player.getPos().x;
			final float posY = player.getPos().y;

			final Vector2 targetPos = new Vector2(posX + touchPoint.x, posY + touchPoint.y);

			player.setTargetPos(targetPos);

			touchPoint = touchPoint.nor();
			final Vector2 orientation = new Vector2(touchPoint.x, touchPoint.y);
			player.setCurOrientation(orientation);
			double theta = Math.atan2(orientation.x, orientation.y);
			theta = 360 - MathUtils.radiansToDegrees * theta;
			player.setRotationAngle((float) theta);

			player.setCurSpeed(100);

			sfsClient.send(new PublicMessageRequest("hosh: " + touchPoint.x + " X " + touchPoint.y));

			// final SFSObject obj = new SFSObject();
			// obj.putInt("n1", 90);
			// obj.putInt("n2", 19);
			//
			// final ExtensionRequest req = new ExtensionRequest("math", new
			// SFSObject(), sfsClient.getLastJoinedRoom());
			// sfsClient.send(req); // TODO

			final ISFSObject sfso = new SFSObject();
			sfso.putInt("x", 90);
			sfso.putInt("y", 19);
			sfsClient.send(new ExtensionRequest("move", sfso));
		}
		if (Gdx.input.isKeyPressed(Input.Keys.W)) {
			cam.zoom += 0.02;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.S)) {
			cam.zoom -= 0.02;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			if (cam.position.x > 0) {
				cam.translate(-3, 0, 0);
			}
		}
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			if (cam.position.x < 1024) {
				cam.translate(3, 0, 0);
			}
		}
		if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
			if (cam.position.y > 0) {
				cam.translate(0, -3, 0);
			}
		}
		if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
			if (cam.position.y < 1024) {
				cam.translate(0, 3, 0);
			}
		}
		if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
			// smartfox
			shutdownSmartFox();
			// smartfox
			Gdx.app.exit();
		}
	}

	@Override
	public void resize(final int width, final int height) {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}

	@Override
	public void pause() {
	}

	private class MyPhotonListener implements IPhotonPeerListener {

		private Integer playerId;

		@Override
		public void debugReturn(final DebugLevel arg0, final String arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onEvent(final EventData evt) {
			System.out.println("OnEvent: " + evt.ToString());

			switch (evt.Code) {
			case 101:
				final Integer sourceActorNum = (Integer) evt.Parameters.get(LiteEventKey.ActorNr.value());
				// final TypedHashMap<Byte, Object> evData = (TypedHashMap<Byte,
				// Object>) evt.Parameters.get(LiteEventKey.Data.value());
				// if (evData != null) {
				// System.out.println(" -> Player: " + sourceActorNum +
				// " says: " + evData.get((byte) 1));
				// } else {
				// System.out.println("UhOh");
				// }

				final HashMap<Byte, Object> testMap = (HashMap<Byte, Object>) evt.Parameters.get(LiteEventKey.Data.value());
				if (testMap != null) {
					photonMessage = testMap.get((byte) 1).toString();
					System.out.println(" -> Player: " + sourceActorNum + " says: " + testMap.get((byte) 1));
				} else {
					System.out.println("UhOh2");
				}

				break;
			case LiteEventCode.Join:
				final Integer[] actorsInGame = (Integer[]) evt.Parameters.get(LiteEventKey.ActorList.value());
				for (final int i : actorsInGame) {
					System.out.println("found actor: " + i);
				}
				break;
			}
		}

		@Override
		public void onOperationResponse(final OperationResponse res) {
			if (res.ReturnCode == 0) {
				System.out.println("OpRes: OK - " + res.ToStringFull());
			} else {
				System.out.println("OpRes: NOK - " + res.ToStringFull() + " DebugMessage: " + res.DebugMessage);
				return;
			}

			switch (res.OperationCode) {
			case LiteOpCode.Join:
				if (res.Parameters.containsKey(LiteOpKey.ActorNr.value())) {
					playerId = (Integer) res.Parameters.get(LiteOpKey.ActorNr.value());
					System.out.println("playerId: " + playerId);
				}

				final Integer myActorNum = (Integer) res.Parameters.get(LiteOpKey.ActorNr.value());
				System.out.println(" -> My Player Num is: " + myActorNum);
				System.out.println("Calling OpRaiseEvent ...");

				final TypedHashMap<Byte, Object> opParams = new TypedHashMap<Byte, Object>(Byte.class, Object.class);
				opParams.put(LiteOpKey.Code.value(), (byte) 101);

				final HashMap<Byte, Object> evData = new HashMap<Byte, Object>();
				evData.put((byte) 1, "Hello World!");

				opParams.put(LiteOpKey.Data.value(), evData);

				System.err.println(peer.opCustom(LiteOpCode.RaiseEvent, opParams, true));

				break;
			}
		}

		@Override
		public void peerStatusCallback(final StatusCode statusCode) {
			photonStatus = statusCode.toString();
			System.out.println("status changed to: " + statusCode.toString());

			switch (statusCode) {
			case Connect:
				final TypedHashMap<Byte, Object> ht = new TypedHashMap<Byte, Object>(Byte.class, String.class); // TODO
																												// achtung!!!
				ht.put(LiteOpKey.RoomName.value(), "MyRoomName");
				peer.opCustom(LiteOpCode.Join, ht, true);
				break;
			default:
				break;
			}
		}
	}

	// ///////
	// / smartfox
	// ///////

	private void initSmartFox() {
		// Instantiate SmartFox client
		sfsClient = new SmartFox(true);

		// Add event listeners
		// sfsClient.addEventListener(SFSEvent.CONNECTION, this);
		// sfsClient.addEventListener(SFSEvent.CONNECTION_LOST, this);
		// sfsClient.addEventListener(SFSEvent.LOGIN, this);
		// sfsClient.addEventListener(SFSEvent.ROOM_JOIN, this);
		// sfsClient.addEventListener(SFSEvent.HANDSHAKE, this);

		sfsClient.addEventListener(SFSEvent.CONNECTION, this);
		sfsClient.addEventListener(SFSEvent.CONNECTION_LOST, this);
		sfsClient.addEventListener(SFSEvent.LOGIN, this);
		sfsClient.addEventListener(SFSEvent.ROOM_JOIN, this);
		sfsClient.addEventListener(SFSEvent.USER_ENTER_ROOM, this);
		sfsClient.addEventListener(SFSEvent.USER_EXIT_ROOM, this);
		sfsClient.addEventListener(SFSEvent.PUBLIC_MESSAGE, this);
		sfsClient.addEventListener(SFSEvent.EXTENSION_RESPONSE, this);

		// Displays the connect dialog box so the user can enter the server IP
		// and port.
		// showDialog(DIALOG_CONNECT_ID);
	}

	private void shutdownSmartFox() {
		if (sfsClient != null) {
			// sfsClient.removeEventListener(SFSEvent.CONNECTION, evtListener);
			// sfsClient.removeEventListener(SFSEvent.CONNECTION_LOST,
			// evtListener);
			// sfsClient.removeEventListener(SFSEvent.LOGIN, evtListener);
			// sfsClient.removeEventListener(SFSEvent.ROOM_JOIN, evtListener);
			// sfsClient.removeEventListener(SFSEvent.HANDSHAKE, evtListener);

			sfsClient.removeEventListener(SFSEvent.CONNECTION, this);
			sfsClient.removeEventListener(SFSEvent.CONNECTION_LOST, this);
			sfsClient.removeEventListener(SFSEvent.LOGIN, this);
			sfsClient.removeEventListener(SFSEvent.ROOM_JOIN, this);
			sfsClient.removeEventListener(SFSEvent.USER_ENTER_ROOM, this);
			sfsClient.removeEventListener(SFSEvent.USER_EXIT_ROOM, this);
			sfsClient.removeEventListener(SFSEvent.PUBLIC_MESSAGE, this);
			sfsClient.removeEventListener(SFSEvent.EXTENSION_RESPONSE, this);

			sfsClient.disconnect();
		}
	}

	private void connectToServer(final String ip, final int port) {
		// showDialog(DIALOG_CONNECTING_ID);

		// connect() method is called in separate thread
		// so it does not blocks the UI
		final SmartFox sfs = sfsClient;
		new Thread() {
			@Override
			public void run() {
				sfs.connect(ip, port);
			}
		}.start();
	}

	@Override
	public void dispatch(final BaseEvent event) throws SFSException {
		// new Runnable() {
		// @Override
		// public void run() {
		// if (event.getType().equalsIgnoreCase(SFSEvent.CONNECTION)) {
		// // status = getString(R.string.connected);
		// // handler.sendEmptyMessage(0);
		// if (event.getArguments().get("success").equals(true)) {
		// // Login as guest in current zone
		// sfsClient.send(new LoginRequest("", "", "zone"));
		// // removeDialog(DIALOG_CONNECTING_ID);
		// System.out.println("sfs: connecting...");
		// }
		// // otherwise error message is shown
		// else {
		// // removeDialog(DIALOG_CONNECTING_ID);
		// // showDialog(DIALOG_CONNECTION_ERROR_ID);
		// System.out.println("sfs: connection error");
		// }
		// } else if
		// (event.getType().equalsIgnoreCase(SFSEvent.CONNECTION_LOST)) {
		// // status = getString(R.string.connectionLost);
		// // handler.sendEmptyMessage(0);
		//
		// // // Destroy SFS instance
		// // onDestroy();
		// shutdownSmartFox();
		//
		// System.out.println("sfs: connection lost");
		//
		// } else if (event.getType().equalsIgnoreCase(SFSEvent.LOGIN)) {
		// // status = getString(R.string.login) +
		// // sfsClient.getCurrentZone() + "' zone.\n";
		// // handler.sendEmptyMessage(0);
		//
		// // Join The Lobby room
		// sfsClient.send(new JoinRoomRequest("lobby"));
		// } else if (event.getType().equalsIgnoreCase(SFSEvent.ROOM_JOIN)) {
		// // status = getString(R.string.roomJoin) +
		// // sfsClient.getLastJoinedRoom().getName() + "'.\n";
		// // handler.sendEmptyMessage(0);
		// System.out.println("sfs: " +
		// sfsClient.getLastJoinedRoom().getName());
		// }
		// }
		// };
		if (event.getType().equalsIgnoreCase(SFSEvent.CONNECTION)) {
			// status = getString(R.string.connected);
			// handler.sendEmptyMessage(0);
			if (event.getArguments().get("success").equals(true)) {
				// Login as guest in current zone
				// sfsClient.send(new LoginRequest("", "", "BasicExamples"));
				sfsClient.send(new LoginRequest("", "", "VerseZone"));
				// removeDialog(DIALOG_CONNECTING_ID);
				System.out.println("sfs: connecting...");
			}
			// otherwise error message is shown
			else {
				// removeDialog(DIALOG_CONNECTING_ID);
				// showDialog(DIALOG_CONNECTION_ERROR_ID);
				System.out.println("sfs: connection error");
			}
		} else if (event.getType().equalsIgnoreCase(SFSEvent.CONNECTION_LOST)) {
			// status = getString(R.string.connectionLost);
			// handler.sendEmptyMessage(0);

			// // Destroy SFS instance
			// onDestroy();
			shutdownSmartFox();

			System.out.println("sfs: connection lost");

		} else if (event.getType().equalsIgnoreCase(SFSEvent.LOGIN)) {
			// status = getString(R.string.login) +
			// sfsClient.getCurrentZone() + "' zone.\n";
			// handler.sendEmptyMessage(0);

			// Join The Lobby room
			sfsClient.send(new JoinRoomRequest("The Lobby"));
			// sfsClient.send(new JoinRoomRequest("Verse Lobby"));
		} else if (event.getType().equalsIgnoreCase(SFSEvent.LOGIN_ERROR)) {
			// mLoginError = event.getArguments().get("error").toString();
			// showDialog(DIALOG_LOGIN_ERROR_ID);
			System.out.println(event.getArguments().get("error").toString());
		} else if (event.getType().equalsIgnoreCase(SFSEvent.ROOM_JOIN)) {
			// status = getString(R.string.roomJoin) +
			// sfsClient.getLastJoinedRoom().getName() + "'.\n";
			// handler.sendEmptyMessage(0);
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
			// appendChatMessage("[" + sender.getName() + "]: " + msg + "\n");
			System.out.println("[" + sender.getName() + "]: " + msg + "\n");
			photonMessage = msg;
		}
		// else if (event.getType().equals(SFSEvent.EXTENSION_RESPONSE)) {
		// System.out.println("got response... but dunno what :(");
		// }

		if (event.getType().equalsIgnoreCase(SFSEvent.EXTENSION_RESPONSE)) {

			final String cmd = event.getArguments().get("cmd").toString();
			ISFSObject resObj = new SFSObject();
			resObj = (ISFSObject) event.getArguments().get("params");

			final int dunno = resObj.getInt("sum");

			System.out.println("!!!!got response... " + dunno);
		}
	}

}
