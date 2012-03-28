package com.hosh.verse;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.hosh.verse.input.VersePlayerInputProcessor;

public class AndroidGame extends AndroidApplication {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// final AndroidApplicationConfiguration config = new
		// AndroidApplicationConfiguration();
		// config.numSamples = 2;
		// initialize(new Game(), config);

		initialize(new VerseGame(new VersePlayerInputProcessor()), false);
	}
}