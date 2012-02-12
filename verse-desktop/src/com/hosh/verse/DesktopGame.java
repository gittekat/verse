package com.hosh.verse;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.imagepacker.TexturePacker;
import com.badlogic.gdx.tools.imagepacker.TexturePacker.Settings;

public class DesktopGame {
	public static void main(final String[] args) {

		final Settings settings = new Settings();
		settings.padding = 2;
		settings.maxWidth = 512;
		settings.maxHeight = 512;
		settings.alias = true;
		settings.incremental = true;
		TexturePacker.process(settings, "data", "../verse-android/assets");

		// new LwjglApplication(new Game(), "verse", Game.WIDTH, Game.HEIGHT,
		// false);
		final boolean fullscreen = false;
		final LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		if (fullscreen) {
			config.fullscreen = true;
			config.width = 6024;
			config.height = 1080;
			// config.samples = 16;
			config.vSyncEnabled = false;
		} else {
			config.width = Game.WIDTH;
			config.height = Game.HEIGHT;
			config.vSyncEnabled = false;
		}
		new LwjglApplication(new Game(), config);

		// new LwjglApplication(new Gdx2DTest(), "Gdx2DTest", 800, 800, false);
	}

}
