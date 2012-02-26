package com.hosh.verse.server;

import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;

public class OnRoomJoinHandler extends BaseServerEventHandler {

//	private Room curRoom;
//	private ServerExtension zoneExt;
//	private Zone curZone;

	@Override
	public void handleServerEvent(ISFSEvent event) throws SFSException {
		
		trace("OnRoomJoinHandler invoked.");
		
//		User player =(User) event.getParameter(SFSEventParam.USER);
//		curRoom = player.getLastJoinedRoom();
//		zoneExt = (ServerExtension) getParentExtension();
//		curZone = player.getZone();
//		
//		Session s = (Session) player.getSession();
//		int port = s.getServerPort();
//		
//		ISFSObject obj = new SFSObject();
//		obj.putUtfString("msg", "Welcome to verse " + player.getName() + " - You're in the " + curZone.getName() + " zone on port " + port);
//		
//		zoneExt.send("welcome", obj,  player, false);
	}

}
