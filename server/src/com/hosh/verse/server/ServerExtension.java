package com.hosh.verse.server;

import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.extensions.SFSExtension;

public class ServerExtension extends SFSExtension {

	@Override
	public void init() {
		// TODO Auto-generated method stub
		trace("Hello, this is my first SFS2X Extension!");
//		this.addEventHandler(SFSEventType.USER_JOIN_ROOM, theClass)
	}
	
	public void destroy() {
		super.destroy();
	}

}
