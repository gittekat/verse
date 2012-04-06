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

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.hosh.verse.common.CollisionChecker;
import com.hosh.verse.common.Interpreter;
import com.hosh.verse.common.VerseActor;
import com.hosh.verse.common.utils.VerseUtils;
import com.hosh.verse.input.IVerseInputProcessor;
import com.hosh.verse.input.VersePlayerInputProcessor;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;

public class VerseGame implements ApplicationListener, IEventListener {
	private static final String PARTICLE_EFFECT = "engine_effect14.p";
	private int WIDTH;
	private int HEIGHT;
	private int HALF_WIDTH;
	private int HALF_HEIGHT;

	private OrthographicCamera cam;
	IVerseInputProcessor inputProcessor;

	private BitmapFont font;
	private SpriteBatch batch;
	private Texture ship;
	private TextureRegion shipRegion;
	private Texture shield;
	private TextureRegion shieldRegion;
	private Texture cloud;
	private TextureRegion cloudRegion;
	private Texture planet;
	private TextureRegion planetRegion;
	// private AtlasRegion
	private Texture beam;
	private TextureRegion beamRegion;

	private Pixmap pixmap;
	private Texture pixmapTexture;

	Vector3 touchPoint;

	private VerseActor player;
	private VerseActor debugDrone;
	private Map<Integer, VerseActor> visiblePlayerMap;
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
	private boolean shooting;
	private long shootingStart;
	private long shootingEnd;
	final long duration = 500000000l;
	private VerseActor shootingTarget;

	public VerseGame(final IVerseInputProcessor inputProcessor) {
		this.inputProcessor = inputProcessor;
		this.inputProcessor.setGame(this);
	}

	@Override
	public void create() {
		PrintStream out;
		try {
			out = new PrintStream(new FileOutputStream("output.txt"));
			// System.setOut(out);
			System.setErr(out);
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		}

		Gdx.graphics.setTitle("verse - I played verse...!");
		final Pixmap[] pixmaps = new Pixmap[2];
		pixmaps[0] = new Pixmap(Gdx.files.internal("ship16.png"));
		pixmaps[1] = new Pixmap(Gdx.files.internal("ship01.png"));
		Gdx.graphics.setIcon(pixmaps);

		readConfig();
		WIDTH = Gdx.graphics.getWidth();
		HEIGHT = Gdx.graphics.getHeight();
		HALF_WIDTH = WIDTH / 2;
		HALF_HEIGHT = HEIGHT / 2;

		Gdx.input.setInputProcessor(inputProcessor);
		if (Gdx.input.getInputProcessor() == null) {
			final IVerseInputProcessor inputProcessor = new VersePlayerInputProcessor();
			inputProcessor.setGame(this);
			Gdx.input.setInputProcessor(inputProcessor);
		}

		visiblePlayerMap = new ConcurrentHashMap<Integer, VerseActor>();
		visibleActorMap = new ConcurrentHashMap<Integer, VerseActor>();

		font = new BitmapFont(Gdx.files.getFileHandle("consolas_11b.fnt", FileType.Internal), Gdx.files.getFileHandle("consolas_11b.png",
				FileType.Internal), false);
		font.setColor(Color.BLACK);

		batch = new SpriteBatch();

		cam = new OrthographicCamera(WIDTH, HEIGHT);
		cam.position.set(HALF_WIDTH, HALF_HEIGHT, 0);

		ship = new Texture(Gdx.files.internal("ship02.png"));
		ship.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		shipRegion = new TextureRegion(ship);

		shield = new Texture(Gdx.files.internal("shield_32.png"));
		shield.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		shieldRegion = new TextureRegion(shield);

		cloud = new Texture(Gdx.files.internal("planet_128_02.png"));
		cloud.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		cloudRegion = new TextureRegion(cloud);

		planet = new Texture(Gdx.files.internal("planet1024_01.png"));
		planet.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		planetRegion = new TextureRegion(planet);

		beam = new Texture(Gdx.files.internal("beam02.png"));
		beam.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		beamRegion = new TextureRegion(beam);

		pixmap = new Pixmap(32, 32, Pixmap.Format.Alpha);
		pixmap.setColor(0.f, 0.f, 0.f, 1.f);
		pixmap.fill();
		pixmapTexture = new Texture(pixmap);

		touchPoint = new Vector3();

		player = new VerseActor(0, 100, 100, 100, 100, 5, 20.f);
		debugDrone = new VerseActor(0, 130, 170, 130, 170, 5, 20.f);

		initSmartFox();
		connectToServer("192.168.178.35", 9933); // TODO use sfs-config.xml

		particleEffect = new ParticleEffect();
		particleEffect.load(Gdx.files.internal(PARTICLE_EFFECT), Gdx.files.internal(""));

		shooting = false;
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

		// handleInput();

		// cam.update();
		// cam.apply(gl);

		// dayNightCycle();

		// bg = 0.99f;
		// Gdx.gl.glClearColor(bg, bg, bg, 1.f);
		Gdx.gl.glClearColor(1, 1, 1, 1.f);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glEnable(GL10.GL_BLEND);

		batch.enableBlending();
		batch.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		batch.begin();
		{
			final float deltaTime = Gdx.graphics.getDeltaTime();

			// font.draw(batch, "visible: " + visibleActors.size(), 20, 60);
			drawHUD();

			player.update(deltaTime);
			drawPlayer(player, HALF_WIDTH, HALF_HEIGHT, deltaTime);

			// draw drone
			debugDrone.update(Gdx.graphics.getDeltaTime());
			final Vector2 dronePos = getScreenCoordinates(debugDrone.getPos());
			drawPlayer(debugDrone, dronePos.x, dronePos.y, deltaTime);

			bg = dayNightCycle(bg, 0.0005f);
			bg = 1;

			// final int beamWidth = beam.getWidth();
			// final float dist = player.getPos().dst(debugDrone.getPos()) /
			// beamWidth;
			//
			// final Vector2 ori =
			// player.getPos().cpy().sub(debugDrone.getPos());
			// final Vector2 screenPos = getScreenCoordinates(player.getPos());
			// screenPos.sub(ori.cpy().mul(0.5f));
			//
			// batch.setColor(1, 0, 1, bg);
			// batch.draw(beamRegion, screenPos.x - 16, screenPos.y - 16, 16,
			// 16, beamWidth, beamWidth, 1f, dist,
			// (float) VerseUtils.vector2angle(ori));

			if (shooting) {
				final long time = System.nanoTime() - shootingStart;

				final float beamStage = MathUtils.PI / duration * time;

				final Vector3 color = new Vector3(0.2f, 0, 1);
				drawBeam(player.getPos(), shootingTarget.getPos(), color, MathUtils.sin(beamStage));

				if (time > duration) {
					shooting = false;
					shootingTarget = null;
				}
			}

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

				// drawObject(cloudRegion, pos.x, pos.y);

				drawActor(a);
			}

			for (final VerseActor p : visiblePlayerMap.values()) {
				// System.out.println("ohter player: " + p.getTargetPos());
				if (p.getCharId() == 1) {
					System.out.println("stop2");
				}
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

	private float dayNightCycle(float bg, final float speed) {
		if (dawn) {
			bg -= speed;
		} else {
			bg += speed;
		}
		if (bg < 0.0f) {
			bg = 0.0f;
			dawn = !dawn;
		}
		if (bg > 1.0f) {
			bg = 1.0f;
			dawn = !dawn;
		}
		return bg;
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
		// batch.setColor(0.f, 0.f, 0.f, p.getShieldStrength());
		// batch.draw(shieldRegion, x - 16, y - 16, 16, 16, 32, 32, 0.95f,
		// 0.95f, 0.f);
		if (p.getCurSpeed() > 0) {
			particleEffect.draw(batch, deltaTime);
		}
		batch.setColor(1.f, 0.f, 0.f, 1.f);
		batch.draw(shipRegion, x - 16, y - 16, 16, 16, 32, 32, 1, 1, p.getRotationAngle());
	}

	private void drawObject(final TextureRegion textureRegion, final float x, final float y) {
		batch.setColor(0.9f, 0.5f, 0.0f, 0.9f);
		batch.draw(textureRegion, x - 64, y - 64, 64, 64, 128, 128, 1, 1, 0.0f);
		// batch.setColor(0.9f, 0.9f, 0.9f, 0.9f);
		// batch.draw(textureRegion, x - 64, y - 64, 64, 64, 128, 128, 0.15f,
		// 0.15f, 0.0f);
	}

	private void drawActor(final VerseActor actor) {
		final Vector2 pos = getScreenCoordinates(actor.getPos());
		final float x = pos.x;
		final float y = pos.y;

		batch.setColor(0.9f, 0.5f, 0.0f, 0.9f);

		final TextureRegion textureRegion = planetRegion;

		final float radius = actor.getRadius();
		final float diameter = radius * 2.f;
		batch.draw(textureRegion, x - radius, y - radius, radius, radius, diameter, diameter, 1, 1, 0.0f);
	}

	private void drawBeam(final Vector2 startPos, final Vector2 endPos, final Vector3 color, final float alpha) {
		batch.setColor(color.x, color.y, color.z, alpha);

		final Vector2 ori = startPos.cpy().sub(endPos);
		final Vector2 screenPos = getScreenCoordinates(startPos);
		screenPos.sub(ori.cpy().mul(0.5f));

		final int beamRegionSize = beamRegion.getRegionWidth();
		final float dist = startPos.dst(endPos) / beamRegionSize;
		batch.draw(beamRegion, screenPos.x - 16, screenPos.y - 16, 16, 16, beamRegionSize, beamRegionSize, 1f, dist,
				(float) VerseUtils.vector2angle(ori));
	}

	private void drawHUD() {
		drawDebugInfo();
		// drawRadar();
	}

	private void drawDebugInfo() {
		if (player != null) {
			batch.setColor(0, 0, 0, 1);
			font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 20, 20);
			font.draw(batch, "Pos: " + player.getPos().x + " x " + player.getPos().y, 20, 40);
			font.draw(batch, "Status: " + serverStatus + " msg: " + serverMessage, 20, 80);
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
		shutdown();
	}

	@Override
	public void pause() {
	}

	public void move(final int touchX, final int touchY) {
		final Vector3 touchPoint = new Vector3(touchX, touchY, 0);
		cam.unproject(touchPoint);
		final Vector2 touchPos = new Vector2(touchPoint.x, touchPoint.y);
		System.out.println(touchPoint.x + " x " + touchPoint.y);

		final float posX = player.getPos().x;
		final float posY = player.getPos().y;
		final Vector2 targetPos = new Vector2(posX + touchPos.x, posY + touchPos.y);
		serverMessage = "" + (int) targetPos.x + " x " + (int) targetPos.y;

		VerseActor newTarget = null;
		for (final VerseActor actor : visibleActorMap.values()) {
			if (CollisionChecker.collisionPointActor(targetPos.x, targetPos.y, actor)) {
				newTarget = actor;
				continue;
			}
		}

		if (newTarget == null) {
			target = null;
		} else if (!newTarget.equals(target)) {
			target = newTarget; // don't move
			serverMessage += " target: " + newTarget.getCharId();
			return;
		}

		if (target != null) {
			serverMessage += " target: " + target.getCharId();
		}

		touchPos.nor();
		player.setTargetPos(targetPos);
		player.setCurSpeed(player.getMaxSpeed());

		final ISFSObject sfso = new SFSObject();
		sfso.putFloat(VerseActor.TARGET_POS_X, targetPos.x);
		sfso.putFloat(VerseActor.TARGET_POS_Y, targetPos.y);
		sfso.putFloat(VerseActor.SPEED, player.getMaxSpeed());
		sfsClient.send(new ExtensionRequest("move", sfso));
	}

	public void shoot() {
		if (target != null) {
			shootingStart = System.nanoTime();
			shootingEnd = shootingStart + duration;
			System.out.println(shootingStart + " -> " + shootingEnd);
			System.out.println(shootingEnd - shootingStart);
			System.out.println(target.getCharId());
			shooting = true;
			shootingTarget = target;
		}
	}

	public void shutdown() {
		shutdownSmartFox();
		Gdx.app.exit();
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

				final Float x = resObj.getFloat("x");
				final Float y = resObj.getFloat("y");
				final Vector2 posVector = new Vector2(x, y);
				player.setPos(posVector);
			}

			if ("actor".equals(cmd)) {
				ISFSObject resObj = new SFSObject();
				resObj = (ISFSObject) event.getArguments().get("params");
				final int charId = resObj.getInt(VerseActor.CHAR_ID);

				if (!visibleActorMap.containsKey(charId)) {
					// TODO wenn unbekannt, dann beim server details nachfrage,
					// anstatt immer alles zu versenden!
					final VerseActor actor = Interpreter.updateActor(null, resObj);
					visibleActorMap.put(actor.getCharId(), actor);
				} else {
					Interpreter.updateActor(visibleActorMap.get(charId), resObj);
				}
			}

			if ("player".equals(cmd)) {
				ISFSObject resObj = new SFSObject();
				resObj = (ISFSObject) event.getArguments().get("params");
				final int charId = resObj.getInt(VerseActor.CHAR_ID);

				if (!visiblePlayerMap.containsKey(charId)) {
					// TODO wenn unbekannt, dann beim server details nachfrage,
					// anstatt immer alles zu versenden!
					final VerseActor actor = Interpreter.updateActor(null, resObj);
					visiblePlayerMap.put(actor.getCharId(), actor);
				} else {
					Interpreter.updateActor(visiblePlayerMap.get(charId), resObj);
				}

			}
		}
	}
}
