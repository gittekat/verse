package com.hosh.verse;

import java.util.Set;

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

public class Game implements ApplicationListener {
	public static final int WIDTH = 500;
	public static final int HEIGHT = 500;
	private static final int HALF_WIDTH = WIDTH / 2;
	private static final int HALF_HEIGHT = HEIGHT / 2;

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

	@Override
	public void create() {
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

		// tests
		// final Gdx2DPixmap alpha = createPixmap(32, 32,
		// Gdx2DPixmap.GDX2D_FORMAT_ALPHA);
		// final Gdx2DPixmap luminanceAlpha = createPixmap(32, 32,
		// Gdx2DPixmap.GDX2D_FORMAT_LUMINANCE_ALPHA);

	}

	@Override
	public void render() {
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
	}

	private void drawRadar() {
		final int size = 50;
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
	}

	@Override
	public void pause() {
	}

}
