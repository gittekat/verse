package com.hosh.verse.server.eventhandler;

import com.hosh.verse.server.Verse;
import com.hosh.verse.server.VerseExtension;
import com.hosh.verse.server.database.DatabaseAccessor;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;

public class LogoutEventHandler extends BaseServerEventHandler {

	@Override
	public void handleServerEvent(final ISFSEvent event) throws SFSException {
		final VerseExtension verseExt = (VerseExtension) getParentExtension();
		final Verse verse = verseExt.getVerse();
		final User user = (User) event.getParameter(SFSEventParam.USER);
		DatabaseAccessor.unmarkAsPlayerControlled(verseExt, verse, user);

		trace("LogoutEventHandler: " + user.getName() + " logged out.");
	}
}
