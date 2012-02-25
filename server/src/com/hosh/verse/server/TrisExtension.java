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

import com.smartfoxserver.v2.extensions.SFSExtension;

public class TrisExtension extends SFSExtension
{
	private final String version = "1.0.5";
	
	@Override
	public void init()
	{
		trace("Tris game Extension for SFS2X started, rel. " + version);
		
	    addRequestHandler("move", MathHandler.class);
	}
	
	@Override
	public void destroy() 
	{
		super.destroy();
		trace("Tris game destroyed!");
	}
		
	
	
}

