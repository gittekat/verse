package com.hosh.verse.input;

import com.badlogic.gdx.InputProcessor;
import com.hosh.verse.VerseGame;

public interface IVerseInputProcessor extends InputProcessor {
	public void setGame(final VerseGame game);
}
