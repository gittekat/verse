package com.hosh.verse.server;

import com.smartfoxserver.bitswarm.sessions.Session;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;

public class OnRoomJoinHandler extends BaseServerEventHandler {

	private Room curRoom;
	private VerseExtension verseExt;
	private Zone curZone;

	@Override
	public void handleServerEvent(ISFSEvent event) throws SFSException {
		
		trace("OnRoomJoinHandler invoked. helloooo?");
		
//		User player =(User) event.getParameter(SFSEventParam.USER);
//		curRoom = player.getLastJoinedRoom();
//		verseExt = (VerseExtension) getParentExtension();
//		curZone = player.getZone();
//		
//		Session s = (Session) player.getSession();
//		int port = s.getServerPort();
//
//		trace("Welcome to verse ");// + player.getName() + " - You're in the " + curZone.getName() + " zone on port " + port);
//		
////		ISFSObject obj = new SFSObject();
////		obj.putUtfString("msg", "Welcome to verse " + player.getName() + " - You're in the " + curZone.getName() + " zone on port " + port);
////		
////		zoneExt.send("welcome", obj,  player, false);
//		
//		VerseExtension verseExt = (VerseExtension) getParentExtension();
//		Verse verse = verseExt.getVerse();
	}

}
