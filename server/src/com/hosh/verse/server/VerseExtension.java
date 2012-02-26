package com.hosh.verse.server;

//import com.smartfoxserver.v2.extensions.ISFSExtension;
//import com.smartfoxserver.v2.extensions.SFSExtension;
//
//public class ServerExtension extends SFSExtension implements ISFSExtension {
//
//	@Override
//	public void init() {
//		// TODO Auto-generated method stub
//		trace("Hello, this is my first SFS2X Extension!");
////		this.addEventHandler(SFSEventType.USER_JOIN_ROOM, OnRoomJoinHandler.class);
//		
//		this.addRequestHandler("math", MathHandler.class);
//	}
//	
//	public void destroy() {
//		super.destroy();
//	}
//
//}

import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.extensions.SFSExtension;

public class VerseExtension extends SFSExtension
{
	private final String version = "0.0.1";
	private Verse verse;
	
	@Override
	public void init()
	{
		trace("verse server extension, rel. " + version);
		
		verse = new Verse(1000, 1000);
		
		addEventHandler(SFSEventType.USER_JOIN_ROOM, OnRoomJoinHandler.class);
		
	    addRequestHandler("move", MathHandler.class);
	}
	
	@Override
	public void destroy() 
	{
		super.destroy();
		trace("verse destroyed!");
	}

	public Verse getVerse() {
		return verse;
	}
	
}

