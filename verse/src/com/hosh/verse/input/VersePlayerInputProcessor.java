package com.hosh.verse.input;

import com.badlogic.gdx.Input;
import com.hosh.verse.VerseGame;

public class VersePlayerInputProcessor implements IVerseInputProcessor {

	VerseGame game;

	@Override
	public void setGame(final VerseGame game) {
		this.game = game;
	}

	@Override
	public boolean keyDown(final int keycode) {
		// TODO Auto-generated method stub
		System.out.println(keycode);
		switch (keycode) {
		case Input.Keys.ESCAPE:
			game.shutdown();
			break;
		}
		return false;
	}

	@Override
	public boolean keyUp(final int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(final char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(final int x, final int y, final int pointer, final int button) {
		game.move(x, y);
		return false;
	}

	@Override
	public boolean touchUp(final int x, final int y, final int pointer, final int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(final int x, final int y, final int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchMoved(final int x, final int y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(final int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}
