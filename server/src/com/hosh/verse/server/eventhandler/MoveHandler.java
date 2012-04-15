package com.hosh.verse.server.eventhandler;

import com.hosh.verse.common.Actor;
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

		final Float targetX = params.getFloat(Actor.SFSID_TARGET_X);
		final Float targetY = params.getFloat(Actor.SFSID_TARGET_Y);
		final Float speed = params.getFloat(Actor.SFSID_CUR_SPEED);

		final Integer actorId = verseExt.getActorId(user);
		verse.movePlayer(actorId, targetX, targetY, speed);
		trace("target pos set to (" + actorId + "): " + targetX + " x " + targetY);

	}
}
