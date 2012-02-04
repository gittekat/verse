package com.hosh.verse;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Game implements ApplicationListener {
	public static final int WIDTH = 800;
	public static final int HEIGHT = 480;
	private static final int HALF_WIDTH = WIDTH / 2;
	private static final int HALF_HEIGHT = HEIGHT / 2;

	private OrthographicCamera cam;

	BitmapFont font;
	private SpriteBatch batch;
	private Texture texture;
	private TextureRegion region;

	private float rotation;

	@Override
	public void create() {

		font = new BitmapFont();
		font.setColor(Color.RED);

		batch = new SpriteBatch();

		cam = new OrthographicCamera(WIDTH, HEIGHT);
		cam.position.set(HALF_WIDTH, HALF_HEIGHT, 0);

		texture = new Texture(Gdx.files.internal("avatar_32.png"));
		texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		region = new TextureRegion(texture);

		rotation = 0.0f;
	}

	@Override
	public void render() {
		handleInput();

		// cam.update();
		// cam.apply(gl);

		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		batch.begin();
		{
			font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 20, 20);
			Gdx.gl.glEnable(GL10.GL_DITHER);
			rotation += .25;
			batch.draw(region, HALF_WIDTH - 16, HALF_HEIGHT - 16, 16, 16, 32, 32, 0.9f, 0.9f, rotation);
		}
		batch.end();
	}

	private void handleInput() {
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
