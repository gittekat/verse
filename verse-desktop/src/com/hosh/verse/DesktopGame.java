package com.hosh.verse;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

public class DesktopGame {
	public static void main(String[] args) {
		new LwjglApplication(new Game(), "verse", 480, 320, false); 
	}

}
