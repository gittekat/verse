package com.hosh.verse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.hosh.verse.common.CollisionChecker;
import com.hosh.verse.common.Interpreter;
import com.hosh.verse.common.VerseActor;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;

public class Game implements ApplicationListener, IEventListener {
	private static final String PARTICLE_EFFECT = "engine_effect14.p";
	private int WIDTH;
	private int HEIGHT;
	private int HALF_WIDTH;
	private int HALF_HEIGHT;

	private OrthographicCamera cam;

	private BitmapFont font;
	private SpriteBatch batch;
	private Texture ship;
	private TextureRegion shipRegion;
	private Texture shield;
	private TextureRegion shieldRegion;
	private Texture cloud;
	private TextureRegion cloudRegion;
	private Pixmap pixmap;
	private Texture pixmapTexture;
	Vector3 touchPoint;

	private VerseActor player;
	private Map<Integer, VerseActor> visiblePlayerMap;
	// private Set<VerseActor> visibleActors;
	private Map<Integer, VerseActor> visibleActorMap;

	private VerseActor target;

	// smartfox
	SmartFox sfsClient;
	IEventListener evtListener;

	private String serverStatus = "not yet set!";
	private String serverMessage;

	private String userName;
	private String password;

	private float bg = 1.f;
	private boolean dawn = true;
	private ParticleEffect particleEffect;

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
		// visibleActors = new CopyOnWriteArraySet<VerseActor>();
		visiblePlayerMap = new ConcurrentHashMap<Integer, VerseActor>();
		visibleActorMap = new ConcurrentHashMap<Integer, VerseActor>();

		font = new BitmapFont();
		font.setColor(Color.RED);
		// font = new BitmapFont(Gdx.files.getFileHandle("default.fnt",
		// FileType.Internal), Gdx.files.getFileHandle("default.png",
		// FileType.Internal), true);

		batch = new SpriteBatch();

		cam = new OrthographicCamera(WIDTH, HEIGHT);
		cam.position.set(HALF_WIDTH, HALF_HEIGHT, 0);

		// ship = new Texture(Gdx.files.internal("triangle_32.png"));
		ship = new Texture(Gdx.files.internal("ship01.png"));
		ship.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		shipRegion = new TextureRegion(ship);

		shield = new Texture(Gdx.files.internal("shield_32.png"));
		shield.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		shieldRegion = new TextureRegion(shield);

		cloud = new Texture(Gdx.files.internal("cloud.png"));
		cloud.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		cloudRegion = new TextureRegion(cloud);

		pixmap = new Pixmap(32, 32, Pixmap.Format.Alpha);
		pixmap.setColor(0.f, 0.f, 0.f, 1.f);
		pixmap.fill();
		pixmapTexture = new Texture(pixmap);

		touchPoint = new Vector3();

		player = new VerseActor(0, 100, 100, 5); // TODO should be removed!

		initSmartFox();
		connectToServer("127.0.0.1", 9933); // TODO use sfs-config.xml

		particleEffect = new ParticleEffect();
		particleEffect.load(Gdx.files.internal(PARTICLE_EFFECT), Gdx.files.internal(""));
	}

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
			System.out.println("ini: " + serverMessage);
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

		// dayNightCycle();

		bg = 0.99f;

		Gdx.gl.glClearColor(bg, bg, bg, 1.f);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glEnable(GL10.GL_BLEND);

		batch.enableBlending();
		// batch.setColor(1, 1, 1, 0);
		// System.out.println(batch.getColor());
		batch.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		batch.begin();
		{
			final float deltaTime = Gdx.graphics.getDeltaTime();

			// font.draw(batch, "visible: " + visibleActors.size(), 20, 60);
			drawHUD();

			player.update(deltaTime);
			drawPlayer(player, HALF_WIDTH, HALF_HEIGHT, deltaTime);

			// draw drone
			// debugDrone.update(Gdx.graphics.getDeltaTime());
			// final Vector2 dronePos =
			// getScreenCoordinates(debugDrone.getPos());
			// drawPlayer(dronePos.x, dronePos.y);

			for (final VerseActor a : visibleActorMap.values()) {

				final Vector2 pos = getScreenCoordinates(a.getPos());

				// final int size = (int) a.getBounds().radius;
				// pixmap.drawRectangle(0, 0, size, size);
				// batch.setColor(0, 0, 0, 1);
				// batch.draw(pixmapTexture, pos.x - 1, pos.y - 1, size + 2,
				// size + 2);
				// batch.setColor(1.f, 0.f, 0.f, 1.f);
				// batch.draw(pixmapTexture, pos.x, pos.y, size, size);

				// batch.setColor(1.f, 1.f, 1.f, 1.f);
				// batch.draw(planetRegion, pos.x, pos.y, 16, 16, 128, 128,
				// 0.9f, 0.9f, 0.f);

				drawObject(cloudRegion, pos.x, pos.y);
			}

			for (final VerseActor p : visiblePlayerMap.values()) {
				p.update(deltaTime);
				final Vector2 pos = getScreenCoordinates(p.getPos());
				drawPlayer(p, pos.x, pos.y, deltaTime);
			}

			particleEffect.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
			// particleEffect.getEmitters().get(0).getAngle().setHigh(90, 80);
			// particleEffect.getEmitters().get(0).getAngle().setLow(-90, -80);
			// particleEffect.getEmitters().get(1).getAngle().setHigh(90, 80);
			// particleEffect.getEmitters().get(1).getAngle().setLow(-90, -80);

			final float angle = player.getRotationAngle() + 270;
			for (final ParticleEmitter emitter : particleEffect.getEmitters()) {
				emitter.getAngle().setHigh(angle);
			}
			// particleEffect.getEmitters().get(0).getAngle().setHigh(angle);
			// particleEffect.getEmitters().get(1).getAngle().setHigh(angle);

			// particleEffect.draw(batch, deltaTime);

		}
		batch.end();
	}

	private void dayNightCycle() {
		if (dawn) {
			bg -= 0.00001f;
		} else {
			bg += 0.00001f;
		}
		if (bg < 0.0f) {
			bg = 0.0f;
			dawn = !dawn;
		}
		if (bg > 1.0f) {
			bg = 1.0f;
			dawn = !dawn;
		}
	}

	private Vector2 getScreenCoordinates(final Vector2 objPos) {
		// player coordinates
		final Vector2 pos = objPos.cpy().sub(player.getPos());

		// screen coordinates
		pos.set(HALF_WIDTH + pos.x, HALF_HEIGHT + pos.y);

		return pos;
	}

	private void drawPlayer(final VerseActor p, final float x, final float y, final float deltaTime) {
		// Gdx.gl.glEnable(GL10.GL_DITHER);
		batch.setColor(0.f, 0.f, 0.f, p.getShieldStrength());
		batch.draw(shieldRegion, x - 16, y - 16, 16, 16, 32, 32, 0.95f, 0.95f, 0.f);
		particleEffect.draw(batch, deltaTime);
		batch.setColor(1.f, 1.f, 1.f, 1.f);
		batch.draw(shipRegion, x - 16, y - 16, 16, 16, 32, 32, 0.95f, 0.95f, p.getRotationAngle());
	}

	private void drawObject(final TextureRegion textureRegion, final float x, final float y) {
		batch.setColor(0.5f, 0.5f, 0.5f, 0.5f);
		batch.draw(textureRegion, x - 64, y - 64, 64, 64, 128, 128, 0.95f, 0.95f, 0.0f);
		batch.setColor(0.9f, 0.9f, 0.9f, 0.9f);
		batch.draw(textureRegion, x - 64, y - 64, 64, 64, 128, 128, 0.15f, 0.15f, 0.0f);
	}

	private void drawHUD() {
		drawDebugInfo();
		// drawRadar();
	}

	private void drawDebugInfo() {
		if (player != null) {
			font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 20, 20);
			font.draw(batch, "Pos: " + player.getPos().x + " x " + player.getPos().y, 20, 40);
			font.draw(batch, "Status: " + serverStatus + " - " + serverMessage, 20, 80);
		}
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

			for (final VerseActor actor : visibleActorMap.values()) {
				if (CollisionChecker.collisionPointActor(targetPos.x, targetPos.y, actor)) {
					System.out.println("picked actor: " + actor.getCharId());
					if (target != actor) {
						target = actor;
						return;
					}
					target = actor;
				}
			}

			target = null;

			serverMessage = "" + (int) targetPos.x + " x " + (int) targetPos.y;

			touchPoint = touchPoint.nor();
			final Vector2 orientation = new Vector2(touchPoint.x, touchPoint.y);

			player.setTargetPos(targetPos);
			// player.setCurOrientationVector(orientation); // XXX
			// player.setTargetOrientationVector(orientation);
			player.setCurSpeed(player.getMaxSpeed()); // TODO send to server

			final ISFSObject sfso = new SFSObject();
			sfso.putFloat(VerseActor.TARGET_POS_X, targetPos.x);
			sfso.putFloat(VerseActor.TARGET_POS_Y, targetPos.y);
			sfso.putFloat(VerseActor.SPEED, player.getMaxSpeed());
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
			Gdx.app.exit();
		}
		if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
			particleEffect.load(Gdx.files.internal(PARTICLE_EFFECT), Gdx.files.internal(""));
			particleEffect.start();
		}
		if (Gdx.input.isKeyPressed(Input.Keys.C)) {
			sfsClient.send(new PublicMessageRequest("hulu"));
		}
	}

	@Override
	public void resize(final int width, final int height) {
		Gdx.gl.glViewport(0, 0, width, height);
		batch.getProjectionMatrix().setToOrtho(0, width, 0, height, 0, 1);

		particleEffect.setPosition(width / 2, height / 2);
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
				final Float posX = resObj.getFloat(VerseActor.POS_X);
				final Float posY = resObj.getFloat(VerseActor.POS_Y);
				final String name = resObj.getUtfString(VerseActor.NAME);
				final int exp = resObj.getInt(VerseActor.EXP);
				final int level = resObj.getInt(VerseActor.LEVEL);
				final float maxHp = resObj.getFloat(VerseActor.MAX_HP);
				final float curHp = resObj.getFloat(VerseActor.CUR_HP);
				final float radius = resObj.getFloat(VerseActor.RADIUS);

				player = new VerseActor(charId, name, exp, level, maxHp, curHp, posX, posY, 0.0f, radius);
			}

			if ("playerData".equals(cmd)) {
				ISFSObject resObj = new SFSObject();
				resObj = (ISFSObject) event.getArguments().get("params");

				// final int charId = resObj.getInt(VerseActor.CHAR_ID);
				final Float x = resObj.getFloat("x");
				final Float y = resObj.getFloat("y");

				// player.setCharId(charId);
				final Vector2 posVector = new Vector2(x, y);
				player.setPos(posVector);
				// player.setTargetPos(posVector);

				// System.out.println("recv. posData: " + x + " X " + y);
			}

			if ("actor".equals(cmd)) {
				ISFSObject resObj = new SFSObject();
				resObj = (ISFSObject) event.getArguments().get("params");

				// visibleActors.add(Interpreter.createActor(resObj));

				final VerseActor actor = Interpreter.createActor(resObj);
				visibleActorMap.put(actor.getCharId(), actor);
			}

			if ("player".equals(cmd)) {
				ISFSObject resObj = new SFSObject();
				resObj = (ISFSObject) event.getArguments().get("params");

				final VerseActor actor = Interpreter.createActor(resObj);

				visiblePlayerMap.put(actor.getCharId(), actor);

			}
		}
	}
}
