package com.hosh.verse;

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
	private Texture shield;
	private TextureRegion shipRegion;
	private TextureRegion shieldRegion;

	Vector3 touchPoint;

	// // .............test...................
	// private static final int TANK_SIZE = 32;
	// private static final int BULLET_SIZE = 5;
	// private static final int MOVEMENT_SPEED = 50;

	// // Tank position
	// private Vector2 tank_pos;
	// // bullet position
	// private Vector2 bullet_pos;
	//
	// // Tank direction
	// private Vector2 objectDirection;
	// // Bullet direction
	// private Vector2 bulletDirection;

	private Pixmap pixmap;
	// int screenWidth, screenHeight;

	private Actor player;

	@Override
	public void create() {
		verse = new Verse(1000000, 1000000);
		player = verse.getPlayer();

		font = new BitmapFont();
		font.setColor(Color.RED);

		batch = new SpriteBatch();

		cam = new OrthographicCamera(WIDTH, HEIGHT);
		cam.position.set(HALF_WIDTH, HALF_HEIGHT, 0);

		ship = new Texture(Gdx.files.internal("avatar_32.png"));
		ship.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		shipRegion = new TextureRegion(ship);

		shield = new Texture(Gdx.files.internal("shield_32.png"));
		shield.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		shieldRegion = new TextureRegion(shield);

		touchPoint = new Vector3();

		// // .............test...................
		// screenWidth = Gdx.graphics.getWidth();
		// screenHeight = Gdx.graphics.getHeight();
		// tank_pos = new Vector2(screenWidth / 2 - 100 / 2, screenHeight / 2 -
		// 100 / 2);
		// bullet_pos = null;
		// objectDirection = new Vector2(1, 0); // Pointing right
		// bulletDirection = new Vector2(1, 0);
		//
		pixmap = new Pixmap(32, 32, Pixmap.Format.Alpha);
		pixmap.setColor(0.f, 0.f, 0.f, 1.f);
		pixmap.fill();
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
			drawHUD();

			drawPlayer();

			for (final Actor a : verse.getActorList()) {
				final Vector2 pos = getScreenCoordinates(a.getPos());

				// System.out.println(pos);
				final int size = (int) a.getBounds().radius;
				pixmap.drawRectangle(0, 0, size, size);
				batch.setColor(0, 0, 0, 1);
				batch.draw(new Texture(pixmap), pos.x - 1, pos.y - 1, size + 2, size + 2);
				batch.setColor(1.f, 0.f, 0.f, 1.f);
				batch.draw(new Texture(pixmap), pos.x, pos.y, size, size);
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
		Gdx.gl.glEnable(GL10.GL_DITHER);
		batch.setColor(0.f, 0.f, 0.f, player.getShieldStrength());
		batch.draw(shieldRegion, HALF_WIDTH - 16, HALF_HEIGHT - 16, 16, 16, 32, 32, 0.9f, 0.9f, 0.f);
		batch.setColor(0.f, 0.f, 0.f, 1.f);
		batch.draw(shipRegion, HALF_WIDTH - 16, HALF_HEIGHT - 16, 16, 16, 32, 32, 0.9f, 0.9f, player.getRotationAngle());
	}

	private void drawHUD() {
		font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 20, 20);
		font.draw(batch, "Pos: " + player.getPos().x + " x " + player.getPos().y, 20, 40);
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
