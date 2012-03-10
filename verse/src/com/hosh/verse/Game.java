package com.hosh.verse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.ini4j.Wini;

import sfs2x.client.SmartFox;
import sfs2x.client.core.BaseEvent;
import sfs2x.client.core.IEventListener;
import sfs2x.client.core.SFSEvent;
import sfs2x.client.entities.User;
import sfs2x.client.requests.ExtensionRequest;
import sfs2x.client.requests.JoinRoomRequest;
import sfs2x.client.requests.LoginRequest;
import sfs2x.client.requests.LogoutRequest;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.hosh.verse.common.ActorFactory;
import com.hosh.verse.common.VerseActor;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;

public class Game implements ApplicationListener, IEventListener {
	private int WIDTH;
	private int HEIGHT;
	private int HALF_WIDTH;
	private int HALF_HEIGHT;

	// private Verse verse;

	private OrthographicCamera cam;

	BitmapFont font;
	private SpriteBatch batch;
	private Texture ship;
	private TextureRegion shipRegion;
	private Texture shield;
	private TextureRegion shieldRegion;
	// private Texture planet;
	// private TextureRegion planetRegion;
	private Pixmap pixmap;
	private Texture pixmapTexture;
	Vector3 touchPoint;

	private VerseActor player;
	private VerseActor debugDrone;
	// private Vector2 playerPos = new Vector2(100, 100);
	private Set<VerseActor> visiblePlayers;
	private Set<VerseActor> visibleActors;

	// smartfox
	SmartFox sfsClient;
	IEventListener evtListener;

	private String serverStatus = "not yet set!";
	private String serverMessage;

	private String userName;
	private String password;
	private ArrayList<Sprite> sprites;

	@Override
	public void create() {
		PrintStream out;
		try {
			out = new PrintStream(new FileOutputStream("output.txt"));
			// System.setOut(out);
			System.setErr(out);
		} catch (final FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		readConfig();
		WIDTH = Gdx.graphics.getWidth();
		HEIGHT = Gdx.graphics.getHeight();
		HALF_WIDTH = WIDTH / 2;
		HALF_HEIGHT = HEIGHT / 2;

		// verse = new Verse(1000, 1000);
		// player = verse.getPlayer();
		visibleActors = new CopyOnWriteArraySet<VerseActor>();
		visiblePlayers = new CopyOnWriteArraySet<VerseActor>();

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

		// planet = new Texture(Gdx.files.internal("planet_128.png"));
		// planet.setFilter(Texture.TextureFilter.Linear,
		// Texture.TextureFilter.Linear);
		// planetRegion = new TextureRegion(planet);

		pixmap = new Pixmap(32, 32, Pixmap.Format.Alpha);
		pixmap.setColor(0.f, 0.f, 0.f, 1.f);
		pixmap.fill();
		pixmapTexture = new Texture(pixmap);

		touchPoint = new Vector3();

		player = new VerseActor(0, 100, 100, 5);
		debugDrone = new VerseActor(1, 130, 100, 5);

		initSmartFox();
		connectToServer("127.0.0.1", 9933); // TODO use sfs-config.xml

		// ///
		// /// Tests
		// ///

		// final Gdx2DPixmap alpha = createPixmap(32, 32,
		// Gdx2DPixmap.GDX2D_FORMAT_ALPHA);
		// Gdx2DPixmap.setBlend(1);
		//
		// alpha.clear(Color.alpha(0.1f));
		// alpha.setPixel(16, 16, Color.alpha(1.0f));
		// if (alpha.getPixel(16, 16) != 0xff) {
		// throw new RuntimeException("alpha error");
		// }
		// if (alpha.getPixel(15, 16) != Color.alpha(0.1f)) {
		// throw new RuntimeException("alpha error");
		// }
		// alpha.drawLine(0, 0, 31, 31, Color.alpha(1.0f));
		// alpha.drawRect(10, 10, 5, 7, Color.alpha(1.0f));
		// alpha.fillRect(20, 10, 5, 7, Color.alpha(1.0f));
		// alpha.drawCircle(16, 16, 10, Color.alpha(1.0f));
		// alpha.fillCircle(16, 16, 6, Color.alpha(1.0f));
		//
		// sprites = new ArrayList<Sprite>();
		// sprites.add(new Sprite(textureFromPixmap(alpha)));
		// sprites.get(0).setPosition(10, 10);
	}

	// Gdx2DPixmap createPixmap(final int width, final int height, final int
	// format) {
	// return Gdx2DPixmap.newPixmap(width, height, format);
	// }
	//
	// Texture textureFromPixmap(final Gdx2DPixmap pixmap) {
	// final Texture texture =
	// Gdx.graphics.newUnmanagedTexture(pixmap.getWidth(), pixmap.getHeight(),
	// Format.RGB565,
	// TextureFilter.Nearest, TextureFilter.Nearest, TextureWrap.ClampToEdge,
	// TextureWrap.ClampToEdge);
	// texture.bind();
	// Gdx.gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, pixmap.getGLInternalFormat(),
	// pixmap.getWidth(), pixmap.getHeight(), 0,
	// pixmap.getGLFormat(), pixmap.getGLType(), pixmap.getPixels());
	// return texture;
	// }

	private void readConfig() {
		Wini ini;
		try {
			ini = new Wini(new File("verse.ini"));
			final int users = ini.get("debug_user", "users", int.class);
			int id = ini.get("debug_user", "lastId", int.class);
			id += 1;
			if (id > users) {
				id = 1;
			}
			ini.put("debug_user", "lastId", id);
			ini.store();

			userName = ini.get("debug_user", "user" + id);
			password = ini.get("debug_user", "pw" + id);
			serverMessage = "id: " + id + " user: " + userName;
			// System.out.println(serverMessage);
		} catch (final Exception e) {
			// android
			userName = "android";
			password = "109";
			e.printStackTrace();
		}
	}

	@Override
	public void render() {

		handleInput();
		// verse.update(Gdx.graphics.getDeltaTime());

		// cam.update();
		// cam.apply(gl);

		Gdx.gl.glClearColor(1.f, 1.f, 1.f, 0.f);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glEnable(GL10.GL_BLEND);

		batch.begin();
		{
			// final Set<VerseActor> visibleActors = verse.getVisibleActors();
			// final Set<VerseActor> visibleActors = new HashSet<VerseActor>();
			font.draw(batch, "visible: " + visibleActors.size(), 20, 60);
			drawHUD();

			player.update(Gdx.graphics.getDeltaTime());
			drawPlayer(HALF_WIDTH, HALF_HEIGHT);

			// draw drone
			debugDrone.update(Gdx.graphics.getDeltaTime());
			final Vector2 dronePos = getScreenCoordinates(debugDrone.getPos());
			drawPlayer(dronePos.x, dronePos.y);

			for (final VerseActor a : visibleActors) {

				final Vector2 pos = getScreenCoordinates(a.getPos());

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

			for (final VerseActor p : visiblePlayers) {
				if (!(p.getCharId() == player.getCharId())) {
					final Vector2 pos = getScreenCoordinates(p.getPos());
					drawPlayer(pos.x, pos.y);
				}

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

	private void drawPlayer(final float x, final float y) {
		// Gdx.gl.glEnable(GL10.GL_DITHER);
		batch.setColor(0.f, 0.f, 0.f, player.getShieldStrength());
		batch.draw(shieldRegion, x - 16, y - 16, 16, 16, 32, 32, 0.95f, 0.95f, 0.f);
		batch.setColor(1.f, 1.f, 1.f, 1.f);
		batch.draw(shipRegion, x - 16, y - 16, 16, 16, 32, 32, 0.95f, 0.95f, player.getRotationAngle());
	}

	private void drawHUD() {
		drawDebugInfo();
		// drawRadar();
	}

	private void drawDebugInfo() {
		font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 20, 20);
		font.draw(batch, "Pos: " + player.getPos().x + " x " + player.getPos().y, 20, 40);
		font.draw(batch, "Status: " + serverStatus + " - " + serverMessage, 20, 80);
	}

	@SuppressWarnings("unused")
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

			final ISFSObject sfso = new SFSObject();
			// sfso.putInt(VerseActor.POS_X, (int) targetPos.x);
			// sfso.putInt(VerseActor.POS_Y, (int) targetPos.y);
			sfso.putFloat(VerseActor.POS_X, targetPos.x);
			sfso.putFloat(VerseActor.POS_Y, targetPos.y);
			// sfso.putIntArray("pos", ImmutableList.of(250, 190));

			serverMessage = "" + (int) targetPos.x + " x " + (int) targetPos.y;

			touchPoint = touchPoint.nor();
			final Vector2 orientation = new Vector2(touchPoint.x, touchPoint.y);
			player.setCurOrientation(orientation); // TODO send to server
			double theta = Math.atan2(orientation.x, orientation.y);
			theta = 360 - MathUtils.radiansToDegrees * theta;
			player.setRotationAngle((float) theta); // TODO send to server

			sfso.putFloat(VerseActor.ORIENTATION_X, orientation.x);
			sfso.putFloat(VerseActor.ORIENTATION_Y, orientation.y);
			sfsClient.send(new ExtensionRequest("move", sfso));

			player.setCurSpeed(player.getMaxSpeed()); // TODO send to server
			player.setTargetPos(targetPos);
			player.setTargetOrientation(orientation);

			// sfsClient.send(new PublicMessageRequest("player: " + touchPoint.x
			// + " X " + touchPoint.y));
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
		shutdownSmartFox();
	}

	@Override
	public void pause() {
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
				sfs.connect(ip, port);
			}
		}.start();
	}

	@Override
	public void dispatch(final BaseEvent event) throws SFSException {
		if (event.getType().equalsIgnoreCase(SFSEvent.CONNECTION)) {
			if (event.getArguments().get("success").equals(true)) {
				sfsClient.send(new LoginRequest(userName, password, "VerseZone"));
				System.out.println("sfs: connecting...");
			}
			// otherwise error message is shown
			else {
				System.out.println("sfs: connection error");
				serverMessage = "sfs: connection error";
			}
		} else if (event.getType().equalsIgnoreCase(SFSEvent.CONNECTION_LOST)) {
			shutdownSmartFox();

			System.out.println("sfs: connection lost");
			serverMessage = "sfs: connection lost";

		} else if (event.getType().equalsIgnoreCase(SFSEvent.LOGIN)) {
			sfsClient.send(new JoinRoomRequest("VerseRoom"));
			serverStatus = "entered VerseRoom";
			// } else if
			// (event.getType().equalsIgnoreCase(SFSEvent.LOGIN_ERROR)) {
			// System.out.println(event.getArguments().get("error").toString());
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
			serverMessage = msg;
		}
		if (event.getType().equalsIgnoreCase(SFSEvent.EXTENSION_RESPONSE)) {

			final String cmd = event.getArguments().get("cmd").toString();

			if ("initialPlayerData".equals(cmd)) {
				ISFSObject resObj = new SFSObject();
				resObj = (ISFSObject) event.getArguments().get("params");

				final int charId = resObj.getInt(VerseActor.CHAR_ID);
				final Float x = resObj.getFloat("x");
				final Float y = resObj.getFloat("y");

				player.setCharId(charId);
				final Vector2 posVector = new Vector2(x, y);
				player.setPos(posVector);
				player.setTargetPos(posVector);
			}

			if ("playerData".equals(cmd)) {
				ISFSObject resObj = new SFSObject();
				resObj = (ISFSObject) event.getArguments().get("params");

				final int charId = resObj.getInt(VerseActor.CHAR_ID);
				final Float x = resObj.getFloat("x");
				final Float y = resObj.getFloat("y");

				player.setCharId(charId);
				final Vector2 posVector = new Vector2(x, y);
				player.setPos(posVector);
				// player.setTargetPos(posVector);

				// System.out.println("recv. posData: " + x + " X " + y);
			}

			if ("actor".equals(cmd)) {
				ISFSObject resObj = new SFSObject();
				resObj = (ISFSObject) event.getArguments().get("params");

				visibleActors.add(ActorFactory.createActor(resObj));
			}

			if ("player".equals(cmd)) {
				ISFSObject resObj = new SFSObject();
				resObj = (ISFSObject) event.getArguments().get("params");

				visiblePlayers.add(ActorFactory.createActor(resObj));
			}
		}
	}
}
