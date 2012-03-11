package com.hosh.verse;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.swing.ImageIcon;

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
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter.GradientColorValue;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.hosh.verse.common.Interpreter;
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

	// private BitmapFont font;
	private SpriteBatch batch;
	private Texture ship;
	private TextureRegion shipRegion;
	private Texture shield;
	private TextureRegion shieldRegion;
	private Texture cloud;
	private TextureRegion cloudRegion;
	// private Texture planet;
	// private TextureRegion planetRegion;
	private Pixmap pixmap;
	private Texture pixmapTexture;
	Vector3 touchPoint;

	private VerseActor player;
	private VerseActor debugDrone;
	// private Vector2 playerPos = new Vector2(100, 100);
	private Set<VerseActor> visiblePlayers;
	private Map<Integer, VerseActor> visiblePlayerMap;
	private Set<VerseActor> visibleActors;

	// smartfox
	SmartFox sfsClient;
	IEventListener evtListener;

	private String serverStatus = "not yet set!";
	private String serverMessage;

	private String userName;
	private String password;

	List<Sprite> sprites;
	private Mesh mesh;
	private float bg = 1.f;
	private boolean dawn = true;
	private ParticleEffect particleEffect;
	private Array<ParticleEmitter> emmiter;

	public void create2() {
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
		visiblePlayerMap = new ConcurrentHashMap<Integer, VerseActor>();

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

		cloud = new Texture(Gdx.files.internal("cloud.png"));
		cloud.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		cloudRegion = new TextureRegion(cloud);

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

		mesh = new Mesh(true, 3, 3, new VertexAttribute(Usage.Position, 3, "a_position"));

		mesh.setVertices(new float[] { -100.f, -100.f, 0, 100.f, -100.f, 0, 0, 100.f, 0 });
		mesh.setIndices(new short[] { 0, 1, 2 });

		// laserPurple.load(Gdx.files.internal("engine_effect01.p"),
		// Gdx.files.internal("data"));
		// laserPurple.setPosition(HALF_WIDTH + 80, HALF_HEIGHT);
		// laserPEmitters = new Array(laserPurple.getEmitters());

		particleEffect = new ParticleEffect();
		particleEffect.load(Gdx.files.internal("engine_effect05.p"), Gdx.files.internal(""));
		emmiter = particleEffect.getEmitters();
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

	public void create3() {
		if (batch != null) {
			return;
		}

		Texture.setEnforcePotImages(false);

		batch = new SpriteBatch();

		particleEffect = new ParticleEffect();
		particleEffect.load(Gdx.files.internal("engine_effect05.p"), Gdx.files.internal(""));
		// emmiter = particleEffect.getEmitters();

		// font = new BitmapFont(Gdx.files.getFileHandle("default.fnt",
		// FileType.Internal), Gdx.files.getFileHandle("default.png",
		// FileType.Internal), true);
		// effectPanel.newEmitter("Untitled", true);
		// // if (resources.openFile("/editor-bg.png") != null) bgImage = new
		// // Image(gl, "/editor-bg.png");
		// Gdx.input.setInputProcessor(this);
	}

	public void render3() {
		// Gdx.gl.glEnable(GL10.GL_BLEND);

		Gdx.gl.glClearColor(0.f, 0.f, 0.f, 0.f);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		// Gdx.gl.glEnable(GL10.GL_BLEND);
		// Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_DST_ALPHA); // TODO ??
		// Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

		// Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		// Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

		// Gdx.gl.glEnable(GL10.GL_DEPTH_TEST);
		// Gdx.gl.glEnable(GL10.GL_ALPHA_TEST);
		//
		// Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		// Gdx.gl10.glAlphaFunc(GL10.GL_GREATER, 0);

		// batch.begin();
		// // batch.enableBlending();
		// // batch.setBlendFunction(GL10.GL_SRC_ALPHA,
		// // GL10.GL_ONE_MINUS_SRC_ALPHA);
		// // batch.setBlendFunction(GL10.GL_ONE_MINUS_DST_COLOR, GL10.GL_ONE);
		// // batch.setBlendFunction(GL10.GL_SRC_COLOR,
		// // GL10.GL_ONE_MINUS_SRC_COLOR);
		// {
		// particleEffect.setPosition(HALF_WIDTH + 300, HALF_HEIGHT);
		// // emmiter.get(0).getAngle().setLow(value)
		// particleEffect.draw(batch, Gdx.graphics.getDeltaTime() * 2);
		// }
		// batch.end();

		final int viewWidth = Gdx.graphics.getWidth();
		final int viewHeight = Gdx.graphics.getHeight();

		// Texture.setEnforcePotImages(false);

		final float delta = Gdx.graphics.getDeltaTime();

		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		// Gdx.gl.glDepthMask(false);

		batch.begin();
		batch.enableBlending();
		batch.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

		// if (bgImage != null) {
		// bgImage.setPosition(viewWidth / 2 - bgImage.getWidth() / 2,
		// viewHeight / 2 - bgImage.getHeight() / 2);
		// bgImage.draw(batch);
		// }

		// particleEffect.setPosition(HALF_WIDTH + 300, HALF_HEIGHT);

		// particleEffect.start();
		boolean complete = true;
		for (final ParticleEmitter emitter : particleEffect.getEmitters()) {
			if (emitter.getSprite() != null) {
				emitter.draw(batch, delta);
			}
			activeCount += emitter.getActiveCount();
			if (emitter.isContinuous()) {
				complete = false;
			}
			if (!emitter.isComplete()) {
				complete = false;
			}
		}
		if (complete) {
			particleEffect.start();
		}
		// particleEffect.start();

		// activeCount = 0;
		// final boolean complete = true;
		// for (final ParticleEmitter emitter : particleEffect.getEmitters()) {
		// // if (emitter.getSprite() == null && emitter.getImagePath() !=
		// // null) {
		// // loadImage(emitter);
		// // }
		// // final boolean enabled = isEnabled(emitter);
		// final boolean enabled = true;
		// if (enabled) {
		// if (emitter.getSprite() != null) {
		// emitter.draw(batch, delta);
		// }
		// activeCount += emitter.getActiveCount();
		// // if (emitter.isContinuous()) {
		// // complete = false;
		// // }
		// // if (!emitter.isComplete()) {
		// // complete = false;
		// // }
		// }
		// }
		// if (complete) {
		// effect.start();
		// }

		// maxActive = Math.max(maxActive, activeCount);
		// maxActiveTimer += delta;
		// if (maxActiveTimer > 3) {
		// maxActiveTimer = 0;
		// lastMaxActive = maxActive;
		// maxActive = 0;
		// }

		// particleEffect.draw(batch, delta);

		// Gdx.gl.glDepthMask(true);

		// font = new BitmapFont(Gdx.files.getFileHandle("default.fnt",
		// FileType.Internal), Gdx.files.getFileHandle("default.png",
		// FileType.Internal), true);
		// font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 5, 15);
		// font.draw(batch, "Count: " + activeCount, 5, 35);
		// font.draw(batch, "Max: " + lastMaxActive, 5, 55);
		// font.draw(batch, (int) (getEmitter().getPercentComplete() *
		// 100) + "%", 5, 75);

		batch.end();

	}

	private float maxActiveTimer;
	private int maxActive, lastMaxActive;
	private boolean mouseDown;
	private int activeCount;
	private int mouseX, mouseY;
	private BitmapFont font;
	private SpriteBatch spriteBatch;
	private Sprite bgImage; // BOZO - Add setting background image to UI.
	ParticleEffect effect = new ParticleEffect();
	final HashMap<ParticleEmitter, ParticleData> particleData = new HashMap();

	static class ParticleData {
		public ImageIcon icon;
		public String imagePath;
		public boolean enabled = true;
	}

	@Override
	public void create() {
		if (spriteBatch != null) {
			return;
		}

		Texture.setEnforcePotImages(false);

		spriteBatch = new SpriteBatch();

		font = new BitmapFont(Gdx.files.getFileHandle("default.fnt", FileType.Internal), Gdx.files.getFileHandle("default.png",
				FileType.Internal), true);
		final ParticleEmitter emmiter2 = newEmitter("Untitled", true);
		effect.getEmitters().add(emmiter2);

		particleEffect = new ParticleEffect();
		particleEffect.load(Gdx.files.internal("engine_effect05.p"), Gdx.files.internal(""));
	}

	public ParticleEmitter newEmitter(final String name, final boolean select) {
		final ParticleEmitter emitter = new ParticleEmitter();

		emitter.getDuration().setLow(3000, 3000);

		emitter.getEmission().setHigh(10, 10);

		emitter.getLife().setHigh(1000, 1000);

		emitter.getScale().setHigh(32, 32);

		emitter.getRotation().setLow(1, 360);
		emitter.getRotation().setHigh(180, 180);
		emitter.getRotation().setTimeline(new float[] { 0, 1 });
		emitter.getRotation().setScaling(new float[] { 0, 1 });
		emitter.getRotation().setRelative(true);

		emitter.getAngle().setHigh(10, 1);
		emitter.getAngle().setLow(-10);
		emitter.getAngle().setActive(true);

		emitter.getVelocity().setHigh(80, 80);
		emitter.getVelocity().setActive(true);

		emitter.getTransparency().setHigh(1, 1);
		emitter.getTransparency().setTimeline(new float[] { 0, 0.2f, 0.8f, 1 });
		emitter.getTransparency().setScaling(new float[] { 0, 1, 1, 0 });

		emitter.setFlip(false, true);
		emitter.setMaxParticleCount(15);
		emitter.setImagePath("particle.png");

		final GradientColorValue tint = emitter.getTint();
		// colorsCount: 3
		// colors0:
		tint.setColors(new float[] { 0.43529412f, 0.9490196f, 0.16470589f });
		tint.setTimeline(new float[] { 0.f });

		emitter.setContinuous(true);
		// emitter.setAligned(true);

		// Array<ParticleEmitter> emitters = editor.effect.getEmitters();
		// if (emitters.size == 0)
		emitter.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
		// else {
		// ParticleEmitter p = emitters.get(0);
		// emitter.setPosition(p.getX(), p.getY());
		// }
		// emitters.add(emitter);
		//
		// emitterTableModel.addRow(new Object[] {name, true});
		// if (select) {
		// editor.reloadRows();
		// int row = emitterTableModel.getRowCount() - 1;
		// emitterTable.getSelectionModel().setSelectionInterval(row, row);
		// }
		return emitter;
	}

	@Override
	public void resize(final int width, final int height) {
		Gdx.gl.glViewport(0, 0, width, height);
		spriteBatch.getProjectionMatrix().setToOrtho(0, width, height, 0, 0, 1);

		effect.setPosition(width / 2, height / 2);
	}

	@Override
	public void render() {
		final int viewWidth = Gdx.graphics.getWidth();
		final int viewHeight = Gdx.graphics.getHeight();

		final float delta = Gdx.graphics.getDeltaTime();

		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		spriteBatch.begin();
		spriteBatch.enableBlending();
		spriteBatch.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

		if (bgImage != null) {
			bgImage.setPosition(viewWidth / 2 - bgImage.getWidth() / 2, viewHeight / 2 - bgImage.getHeight() / 2);
			bgImage.draw(spriteBatch);
		}

		activeCount = 0;
		boolean complete = true;
		for (final ParticleEmitter emitter : effect.getEmitters()) {
			if (emitter.getSprite() == null && emitter.getImagePath() != null) {
				loadImage(emitter);
			}
			final boolean enabled = isEnabled(emitter);
			if (enabled) {
				if (emitter.getSprite() != null) {
					emitter.draw(spriteBatch, delta);
				}
				activeCount += emitter.getActiveCount();
				if (emitter.isContinuous()) {
					complete = false;
				}
				if (!emitter.isComplete()) {
					complete = false;
				}
			}
		}
		if (complete) {
			effect.start();
		}

		particleEffect.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2 + 200);
		particleEffect.draw(spriteBatch, delta);

		maxActive = Math.max(maxActive, activeCount);
		maxActiveTimer += delta;
		if (maxActiveTimer > 3) {
			maxActiveTimer = 0;
			lastMaxActive = maxActive;
			maxActive = 0;
		}

		if (mouseDown) {
			// gl.drawLine(mouseX - 6, mouseY, mouseX + 5, mouseY);
			// gl.drawLine(mouseX, mouseY - 5, mouseX, mouseY + 6);
		}

		font.draw(spriteBatch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 5, 15);
		font.draw(spriteBatch, "Count: " + activeCount, 5, 35);
		font.draw(spriteBatch, "Max: " + lastMaxActive, 5, 55);
		font.draw(spriteBatch, (int) (getEmitter().getPercentComplete() * 100) + "%", 5, 75);

		spriteBatch.end();

		// gl.drawLine((int)(viewWidth *
		// getCurrentParticles().getPercentComplete()), viewHeight - 1,
		// viewWidth, viewHeight -
		// 1);
	}

	public boolean isEnabled(final ParticleEmitter emitter) {
		final ParticleData data = particleData.get(emitter);
		if (data == null) {
			return true;
		}
		return data.enabled;
	}

	public ParticleEmitter getEmitter() {
		return effect.getEmitters().get(0);
	}

	private void loadImage(final ParticleEmitter emitter) {
		final String imagePath = emitter.getImagePath();
		final String imageName = new File(imagePath.replace('\\', '/')).getName();
		try {
			FileHandle file;
			if (imagePath.equals("particle.png")) {
				file = Gdx.files.classpath(imagePath);
			} else {
				file = Gdx.files.absolute(imagePath);
			}
			emitter.setSprite(new Sprite(new Texture(file)));
		} catch (final GdxRuntimeException ex) {
			ex.printStackTrace();
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					System.err.println("error");
				}
			});
			emitter.setImagePath(null);
		}
	}

	public void render2() {

		handleInput();
		// verse.update(Gdx.graphics.getDeltaTime());

		// cam.update();
		// cam.apply(gl);

		// if (dawn) {
		// bg -= 0.00001f;
		// } else {
		// bg += 0.00001f;
		// }
		// if (bg < 0.0f) {
		// bg = 0.0f;
		// dawn = !dawn;
		// }
		// if (bg > 1.0f) {
		// bg = 1.0f;
		// dawn = !dawn;
		// }

		Gdx.gl.glClearColor(bg, bg, bg, 0.f);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glEnable(GL10.GL_BLEND);
		// Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_DST_ALPHA); // TODO ??
		// Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

		// Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		// Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

		// Gdx.gl.glEnable(GL10.GL_DEPTH_TEST);
		// Gdx.gl.glEnable(GL10.GL_ALPHA_TEST);
		//
		// Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		// Gdx.gl10.glAlphaFunc(GL10.GL_GREATER, 0);

		batch.begin();
		{
			final float deltaTime = Gdx.graphics.getDeltaTime();

			font.draw(batch, "visible: " + visibleActors.size(), 20, 60);
			drawHUD();

			player.update(deltaTime);
			drawPlayer(player, HALF_WIDTH, HALF_HEIGHT);

			// particleEffect.setPosition(HALF_WIDTH + 300, HALF_HEIGHT);
			// // emmiter.get(0).getAngle().setLow(value)
			// particleEffect.draw(batch, deltaTime);

			// draw drone
			// debugDrone.update(Gdx.graphics.getDeltaTime());
			// final Vector2 dronePos =
			// getScreenCoordinates(debugDrone.getPos());
			// drawPlayer(dronePos.x, dronePos.y);

			for (final VerseActor a : visibleActors) {

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
				drawPlayer(p, pos.x, pos.y);
			}

			// batch.setColor(0.f, 0.f, 0.f, 0.5f);
			Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 0.f);
			mesh.render(GL10.GL_TRIANGLES, 0, 3);

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

	private void drawPlayer(final VerseActor p, final float x, final float y) {
		// Gdx.gl.glEnable(GL10.GL_DITHER);
		batch.setColor(0.f, 0.f, 0.f, p.getShieldStrength());
		batch.draw(shieldRegion, x - 16, y - 16, 16, 16, 32, 32, 0.95f, 0.95f, 0.f);
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
			sfso.putFloat(VerseActor.TARGET_POS_X, targetPos.x);
			sfso.putFloat(VerseActor.TARGET_POS_Y, targetPos.y);
			// sfso.putIntArray("pos", ImmutableList.of(250, 190));

			serverMessage = "" + (int) targetPos.x + " x " + (int) targetPos.y;

			touchPoint = touchPoint.nor();
			final Vector2 orientation = new Vector2(touchPoint.x, touchPoint.y);
			player.setCurOrientation(orientation);
			sfso.putFloat(VerseActor.ORIENTATION_X, orientation.x);
			sfso.putFloat(VerseActor.ORIENTATION_Y, orientation.y);
			sfso.putFloat(VerseActor.SPEED, player.getMaxSpeed());
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
		if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
			particleEffect.start();
		}
	}

	// @Override
	// public void resize(final int width, final int height) {
	// }

	// @Override
	// public void resize(final int width, final int height) {
	// Gdx.gl.glViewport(0, 0, width, height);
	// batch.getProjectionMatrix().setToOrtho(0, width, 0, height, 0, 1);
	//
	// particleEffect.setPosition(width / 2, height / 2);
	// }

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

				visibleActors.add(Interpreter.createActor(resObj));
			}

			if ("player".equals(cmd)) {
				ISFSObject resObj = new SFSObject();
				resObj = (ISFSObject) event.getArguments().get("params");

				final VerseActor actor = Interpreter.createActor(resObj);

				visiblePlayers.add(actor);

				visiblePlayerMap.put(actor.getCharId(), actor);

			}
		}
	}
}
