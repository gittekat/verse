package com.hosh.verse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.hosh.verse.common.Actor;
import com.hosh.verse.common.CollisionChecker;
import com.hosh.verse.common.Interpreter;
import com.hosh.verse.common.MovementData;
import com.hosh.verse.common.Stats;
import com.hosh.verse.common.utils.ActorUtils;
import com.hosh.verse.common.utils.VerseUtils;
import com.hosh.verse.input.IVerseInputProcessor;
import com.hosh.verse.input.VersePlayerInputProcessor;
import com.smartfoxserver.v2.entities.data.ISFSArray;
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

	private Actor player = new Actor();
	// private Actor debugDrone;
	private Map<Integer, Actor> visiblePlayerMap;
	private Map<Integer, Actor> visibleActorMap;

	private Actor target;

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
	private Actor shootingTarget;

	private Map<Integer, Stats> blueprintCache;
	Map<Integer, Actor> actorMap;
	private boolean initialized = false;

	public VerseGame(final IVerseInputProcessor inputProcessor) {
		this.inputProcessor = inputProcessor;
		this.inputProcessor.setGame(this);
	}

	@Override
	public void create() {
		final Logger logger = LoggerFactory.getLogger(VerseGame.class);
		logger.info("Game creation phase");
		PrintStream out;
		try {
			out = new PrintStream(new FileOutputStream("output.txt"));
			// System.setOut(out);
			// System.setErr(out);
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		}

		Gdx.graphics.setTitle("verse - I played verse...!");
		final Pixmap[] pixmaps = new Pixmap[2];
		pixmaps[0] = new Pixmap(Gdx.files.internal("ship16.png"));
		pixmaps[1] = new Pixmap(Gdx.files.internal("ship01.png"));
		Gdx.graphics.setIcon(pixmaps);

		readConfig();
		// userName = "emmel";
		// password = "109";
		// serverMessage = "id: ?19? user: " + userName;

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

		visiblePlayerMap = new ConcurrentHashMap<Integer, Actor>();
		visibleActorMap = new ConcurrentHashMap<Integer, Actor>();

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

		// player = new VerseActor(0, 100, 100, 100, 100, 5, 20.f);
		// debugDrone = new VerseActor(0, 130, 170, 130, 170, 5, 20.f);

		particleEffect = new ParticleEffect();
		particleEffect.load(Gdx.files.internal(PARTICLE_EFFECT), Gdx.files.internal(""));

		actorMap = new ConcurrentHashMap<Integer, Actor>();
		shooting = false;

		initSmartFox();
		connectToServer("192.168.178.35", 80); // TODO use sfs-config.xml

		while (initialized == false) {
			try {
				Thread.sleep(500);
			} catch (final InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private void readConfig() {
		Wini ini;
		{
			// create file with the following content if verse.ini is missing:
			// [debug_user]
			// users = 3
			// lastId = 3
			// user1 = hosh
			// pw1 = 109
			// user2 = emmel
			// pw2 = 109
			// user3 = tarkin
			// pw3 = 109

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
			} catch (final InvalidFileFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (final IOException e) {
				// android
				userName = "android";
				password = "109";
				e.printStackTrace();
			}
		}
	}

	@Override
	public void render() {
		bg = dayNightCycle(bg, 0.0005f);
		bg = 1.f;
		Gdx.gl.glClearColor(bg, bg, bg, 1.f);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glEnable(GL10.GL_BLEND);

		batch.enableBlending();
		batch.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		batch.begin();
		{
			final float deltaTime = Gdx.graphics.getDeltaTime();

			player.update(deltaTime);
			drawPlayer(player, HALF_WIDTH, HALF_HEIGHT, deltaTime);

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

			particleEffect.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);

			final float angle = player.getRotationAngle() + 270;
			for (final ParticleEmitter emitter : particleEffect.getEmitters()) {
				emitter.getAngle().setHigh(angle);
			}

			for (final Actor actor : actorMap.values()) {
				final float x = actor.getX();
				actor.update(deltaTime);
				if (x != actor.getX()) {
					System.out.println("stops");
				}
				drawActor(actor, deltaTime);
			}

			drawHUD();

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

	private void drawPlayer(final Actor p, final float x, final float y, final float deltaTime) {
		if (p.getCurSpeed() > 0) {
			particleEffect.draw(batch, deltaTime);
		}
		batch.setColor(1.f, 0.f, 0.f, 1.f);
		batch.draw(shipRegion, x - 16, y - 16, 16, 16, 32, 32, 1, 1, p.getRotationAngle());
	}

	private void drawActor(final Actor actor, final float deltaTime) {
		if (actor.getCurSpeed() > 0) {
			particleEffect.draw(batch, deltaTime);
		}
		batch.setColor(1.f, 0.f, 0.f, 1.f);
		final Vector2 screenPos = getScreenCoordinates(actor.getPos());

		// TODO size of model
		batch.draw(shipRegion, screenPos.x - 16, screenPos.y - 16, 16, 16, 32, 32, 1, 1, actor.getRotationAngle());
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
		drawRadar();
	}

	private void drawDebugInfo() {
		if (player != null) {
			batch.setColor(0, 0, 0, 1);
			font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 20, 20);
			font.draw(batch, "Pos: " + (int) player.getPos().x + " x " + (int) player.getPos().y, 20, 40);
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

		final float posX = player.getX();
		final float posY = player.getY();
		final Vector2 targetPos = new Vector2(posX + touchPos.x, posY + touchPos.y);
		serverMessage = "" + (int) targetPos.x + " x " + (int) targetPos.y;

		Actor newTarget = null;
		for (final Actor actor : visibleActorMap.values()) {
			if (CollisionChecker.collisionPointActor(targetPos.x, targetPos.y, actor)) {
				newTarget = actor;
				continue;
			}
		}

		if (newTarget == null) {
			target = null;
		} else if (!newTarget.equals(target)) {
			target = newTarget; // don't move
			serverMessage += " target: " + newTarget.getId();
			return;
		}

		if (target != null) {
			serverMessage += " target: " + target.getId();
		}

		touchPos.nor();
		player.setTargetPos(targetPos);
		final int maxSpeed = player.getStats().getSpeed();
		player.setCurSpeed(maxSpeed);

		// ISFSObject sfso = new SFSObject();
		// sfso.putFloat(VerseActor.TARGET_POS_X, targetPos.x);
		// sfso.putFloat(VerseActor.TARGET_POS_Y, targetPos.y);
		// sfso.putFloat(VerseActor.SPEED, player.getMaxSpeed());
		final ISFSObject sfso = Interpreter.packMoveData(targetPos.x, targetPos.y, maxSpeed);

		sfsClient.send(new ExtensionRequest("move", sfso));
	}

	public void shoot() {
		if (target != null) {
			shootingStart = System.nanoTime();
			shootingEnd = shootingStart + duration;
			System.out.println(shootingStart + " -> " + shootingEnd);
			System.out.println(shootingEnd - shootingStart);
			System.out.println(target.getId());
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

				// ISFSObject params = new SFSObject();
				// // TODO what ship should be used
				// sfsClient.send(new LoginRequest(userName, password,
				// "VerseZone", params));

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

			if (Interpreter.SFS_CMD_INIT.equals(cmd)) {
				ISFSObject resObj = new SFSObject();
				resObj = (ISFSObject) event.getArguments().get("params");

				// get blueprints
				final ISFSArray blueprintData = resObj.getSFSArray(Interpreter.SFS_OBJ_BLUEPRINTS);
				blueprintCache = new HashMap<Integer, Stats>();
				for (int i = 0; i < blueprintData.size(); ++i) {
					final Stats blueprint = (Stats) blueprintData.getClass(i);
					blueprintCache.put(blueprint.getId(), blueprint);
				}

				// get playerData
				player = (Actor) resObj.getClass(Interpreter.SFS_OBJ_PLAYER_DATA);
				player.setBaseStats(blueprintCache.get(player.getBlueprint()));
				initialized = true;
			}

			if (Interpreter.SFS_CMD_MOVEMENT.equals(cmd)) {
				ISFSObject resObj = new SFSObject();
				resObj = (ISFSObject) event.getArguments().get("params");

				final MovementData movementPlayer = (MovementData) resObj.getClass(Interpreter.SFS_OBJ_MOVEMENT_DATA_PLAYER);
				ActorUtils.updateActor(player, movementPlayer);

				final ISFSArray movementDataArray = resObj.getSFSArray(Interpreter.SFS_OBJ_MOVEMENT_DATA);
				for (int i = 0; i < movementDataArray.size(); ++i) {
					final MovementData movementData = (MovementData) movementDataArray.getClass(i);
					final int id = movementData.getId();
					if (actorMap.containsKey(id)) {
						if (movementData.getTargetPosX() != movementData.getPosX() && movementData.getSpeed() > 0) {
							System.out.println("stopHere");
						}
						ActorUtils.updateActor(actorMap.get(id), movementData);
					} else {
						// TODO get/request actor
						actorMap.put(id, ActorUtils.createUnidentifiedActor(movementData));
					}
				}
			}
		}
	}
}
