package com.hosh.verse;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Rectangle;

public class Game implements ApplicationListener {
	static final int WIDTH = 480;
	static final int HEIGHT = 320;

	private OrthographicCamera cam;
	BitmapFont font;
	private Texture texObj1;
	private Texture texObj2;
	private Texture texObj3;
	private SpriteBatch batch;
	private Sprite avatar;
	private Mesh avatarMesh;
	private Rectangle glViewport;
	private float rotation;

	private float m_fboScaler = 2.f;
	private boolean m_fboEnabled = true;
	private FrameBuffer m_fbo = null;
	private TextureRegion m_fboRegion = null;
	private SpriteBatch spriteBatch;
	private Texture texture;
	private TextureRegion region;

	@Override
	public void create() {

		font = new BitmapFont();
		font.setColor(Color.RED);

		batch = new SpriteBatch();
		// texObj1 = new Texture(Gdx.files.internal("human.gif"));
		// texObj2 = new Texture(Gdx.files.internal("archer.gif"));
		// texObj3 = new Texture(Gdx.files.internal("alchemist.gif"));
		texObj1 = new Texture(Gdx.files.internal("avatar_32.png"));
		avatar = new Sprite(texObj1);

		avatarMesh = new Mesh(true, 4, 6, new VertexAttribute(VertexAttributes.Usage.Position, 3, "attr_Position"), new VertexAttribute(
				Usage.TextureCoordinates, 2, "attr_texCoords"));
		avatarMesh.setVertices(new float[] { -16f, -16f, 0, 0, 1, 16f, -16f, 0, 1, 1, 16f, 16f, 0, 1, 0, -16f, 16f, 0, 0, 0 });
		avatarMesh.setIndices(new short[] { 0, 1, 2, 2, 3, 0 });
		rotation = 0.0f;

		cam = new OrthographicCamera(WIDTH, HEIGHT);
		cam.position.set(WIDTH / 2, HEIGHT / 2, 0);

		glViewport = new Rectangle(0, 0, WIDTH, HEIGHT);

		spriteBatch = new SpriteBatch();

		texture = new Texture(Gdx.files.internal("avatar_32.png"));
		texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		region = new TextureRegion(texture);
	}

	// @Override
	// public void render() {
	// render(spriteBatch);
	// }

	@Override
	public void render() {
		handleInput();
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		batch.begin();
		font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 20, 20);
		Gdx.gl.glEnable(GL10.GL_DITHER);
		batch.draw(region, 100, 100, 16, 16, 32, 32, 0.9f, 0.9f, rotation);
		batch.end();
		rotation += .25;
	}

	public void render(final SpriteBatch spriteBatch) {
		final int width = Gdx.graphics.getWidth();
		final int height = Gdx.graphics.getHeight();

		if (m_fboEnabled) // enable or disable the supersampling
		{
			if (m_fbo == null) {
				// m_fboScaler increase or decrease the antialiasing quality

				m_fbo = new FrameBuffer(Format.RGB565, (int) (width * m_fboScaler), (int) (height * m_fboScaler), false);
				m_fboRegion = new TextureRegion(m_fbo.getColorBufferTexture());
				m_fboRegion.flip(false, true);
			}

			m_fbo.begin();
		}

		// this is the main render function
		my_render_impl();

		if (m_fbo != null) {
			m_fbo.end();

			spriteBatch.begin();
			spriteBatch.draw(m_fboRegion, 0, 0, width, height);
			spriteBatch.end();
		}
	}

	private void my_render_impl() {
		handleInput();
		final GL10 gl = Gdx.graphics.getGL10();

		// Camera --------------------- /
		// Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		gl.glViewport((int) glViewport.x, (int) glViewport.y, (int) glViewport.width, (int) glViewport.height);

		// ......................
		// Gdx.gl.glLineWidth(12f);
		// Gdx.gl.glHint(GL10.GL_LINE_SMOOTH_HINT, GL10.GL_NICEST);
		// Gdx.gl.glEnable(GL10.GL_LINE_SMOOTH);
		// Gdx.gl.glEnable(GL10.GL_BLEND);

		cam.update();
		cam.apply(gl);

		batch.begin();
		font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 20, 20);
		// batch.draw(texObj3, 90, 110);
		// batch.draw(texObj1, 100, 100);
		// batch.draw(texObj2, 120, 120);
		avatar.setPosition(150.0f, 150.0f);
		avatar.rotate(0.2f);
		avatar.draw(batch);
		batch.end();

		gl.glActiveTexture(GL10.GL_TEXTURE0);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnable(GL10.GL_BLEND);
		gl.glTranslatef(100.f, 100.f, 0.f);
		rotation += 0.2f;
		gl.glRotatef(rotation, 0.f, 0.f, 1.f);
		texObj1.bind();

		avatarMesh.render(GL10.GL_TRIANGLES);
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
