package com.hosh.verse.server.eventhandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.hosh.verse.common.VerseActor;
import com.hosh.verse.server.Verse;
import com.hosh.verse.server.VerseExtension;
import com.hosh.verse.server.database.DatabaseAccessor;
import com.smartfoxserver.bitswarm.sessions.ISession;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.db.IDBManager;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;

public class OnRoomJoinHandler extends BaseServerEventHandler {

	private Room curRoom;
	private Zone curZone;
	private ISession session;
	private User user;

	@Override
	public void handleServerEvent(final ISFSEvent event) throws SFSException {
		// verseExt = (VerseExtension) getParentExtension();

		trace("OnRoomJoinHandler invoked.");

		user = (User) event.getParameter(SFSEventParam.USER);
		curRoom = user.getLastJoinedRoom();
		curZone = user.getZone();

		session = user.getSession();
		final int port = session.getServerPort();

		trace("player joined room (" + curRoom + "): " + user.getName() + " - in zone: " + curZone.getName() + " on port " + port);

		characterSelection();

	}

	private void characterSelection() {
		final IDBManager dbManager = getParentExtension().getParentZone().getDBManager();
		Connection connection;
		try {
			connection = dbManager.getConnection();

			final String accountName = (String) session.getProperty(VerseExtension.ACCOUNT_NAME);
			final PreparedStatement psChars = connection.prepareStatement("SELECT charId, char_name FROM characters WHERE account_name=?");
			psChars.setString(1, accountName);

			final ResultSet resChars = psChars.executeQuery();

			if (!resChars.first()) {
				// TODO create new character
				return;
			}

			final Map<String, String> charMap = new HashMap<String, String>();
			do {
				final String charId = resChars.getString("charId");
				final String char_name = resChars.getString("char_name");
				charMap.put(charId, char_name);
				trace("[DEBUG:] room join: " + charId + " " + char_name);
			} while (resChars.next());

			// TODO character selection => for now just use the first char
			String charId = "";
			for (final Map.Entry<String, String> entry : charMap.entrySet()) {
				charId = entry.getKey();
			}

			final VerseActor actor = DatabaseAccessor.createActor(connection, charId);
			final VerseExtension verseExt = (VerseExtension) getParentExtension();
			final Verse verse = verseExt.getVerse();

			DatabaseAccessor.addPlayer(verseExt, verse, actor, user);

			connection.close();
		} catch (final SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
