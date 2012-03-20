package com.hosh.verse.server.eventhandler;

import com.hosh.verse.common.VerseActor;
import com.hosh.verse.server.Verse;
import com.hosh.verse.server.VerseExtension;
import com.smartfoxserver.v2.annotations.Instantiation;
import com.smartfoxserver.v2.annotations.Instantiation.InstantiationMode;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

@Instantiation(InstantiationMode.SINGLE_INSTANCE)
public class MoveHandler extends BaseClientRequestHandler {

	@Override
	public void handleClientRequest(final User user, final ISFSObject params) {
		final VerseExtension verseExt = (VerseExtension) getParentExtension();
		final Verse verse = verseExt.getVerse();

		final Float targetX = params.getFloat(VerseActor.TARGET_POS_X);
		final Float targetY = params.getFloat(VerseActor.TARGET_POS_Y);
		final Float speed = params.getFloat(VerseActor.SPEED);

		final Integer charId = verseExt.getCharId(user);
		verse.movePlayer(charId, targetX, targetY, speed);
		trace("target pos set to (" + charId + "): " + targetX + " x " + targetY);

	}
}
