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

	// private VerseExtension verseExt;
	private Room curRoom;
	private Zone curZone;

	@Override
	public void handleServerEvent(final ISFSEvent event) throws SFSException {
		// verseExt = (VerseExtension) getParentExtension();

		trace("OnRoomJoinHandler invoked.");

		final User player = (User) event.getParameter(SFSEventParam.USER);
		curRoom = player.getLastJoinedRoom();
		curZone = player.getZone();

		final Session s = (Session) player.getSession();
		final int port = s.getServerPort();

		trace("player joined room (" + curRoom + "): " + player.getName() + " - in zone: " + curZone.getName() + " on port " + port);

		// final VerseExtension verseExt = (VerseExtension)
		// getParentExtension();
		// final Verse verse = verseExt.getVerse();

	}
}
