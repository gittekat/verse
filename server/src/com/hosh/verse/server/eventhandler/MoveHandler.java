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
		// Check params
		if (!params.containsKey(VerseActor.POS_X) || !params.containsKey(VerseActor.POS_Y)) {
			// throw new
			// SFSRuntimeException("Invalid request, one mandatory param is missing. Required 'x' and 'y'");
			return;
		}
		// if (!params.containsKey(VerseActor.ORIENTATION_X) ||
		// !params.containsKey(VerseActor.POS_Y)) {
		// return;
		// }

		final VerseExtension verseExt = (VerseExtension) getParentExtension();
		final Verse verse = verseExt.getVerse();

		// final Float x = params.getFloat(VerseActor.POS_X);
		// final Float y = params.getFloat(VerseActor.POS_Y);
		// final int x = params.getInt(VerseActor.POS_X);
		// final int y = params.getInt(VerseActor.POS_Y);

		// TODO set orientation??
		// final Float orientationX = params.getFloat(VerseActor.ORIENTATION_X);
		// final Float orientationY = params.getFloat(VerseActor.ORIENTATION_Y);

		final Integer charId = verseExt.getCharId(user);
		// verse.movePlayer(charId, x, y, orientationX, orientationY);
		// verse.movePlayer(charId, (float) x, (float) y, 1.f, 1.f);

	}
}
