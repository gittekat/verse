package com.hosh.verse.server;

import com.smartfoxserver.v2.annotations.Instantiation;
import com.smartfoxserver.v2.annotations.Instantiation.InstantiationMode;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.exceptions.SFSRuntimeException;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

@Instantiation(InstantiationMode.SINGLE_INSTANCE)
public class MathHandler extends BaseClientRequestHandler {

	@Override
	public void handleClientRequest(final User user, final ISFSObject params) {
		// Check params
		if (!params.containsKey("x") || !params.containsKey("y")) {
			throw new SFSRuntimeException("Invalid request, one mandatory param is missing. Required 'x' and 'y'");
		}

		final VerseExtension verseExt = (VerseExtension) getParentExtension();
		// final Verse verse = verseExt.getVerse();

		final Float moveX = params.getFloat("x");
		final Float moveY = params.getFloat("y");

		final ISFSObject res = new SFSObject();
		//
		res.putFloat("sum", moveX + moveY);

		verseExt.send("math", res, user);

		// gameExt.trace(String.format("Handling move from player %s. (%s, %s) = %s ",
		// user.getPlayerId(), moveX, moveY));

	}

}
