package com.hosh.verse.tests;

import com.badlogic.gdx.ApplicationListener;

public abstract class GdxTest implements ApplicationListener {
	public boolean needsGL20() {
		return false;
	}

	@Override
	public void create() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void render() {
	}

	@Override
	public void resize(final int width, final int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void dispose() {
	}
}