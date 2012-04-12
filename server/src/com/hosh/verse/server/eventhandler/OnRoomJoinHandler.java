package com.hosh.verse.server.eventhandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import com.hosh.verse.common.Actor;
import com.hosh.verse.common.Interpreter;
import com.hosh.verse.common.Stats;
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
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;

public class OnRoomJoinHandler extends BaseServerEventHandler {

	private Room curRoom;
	private Zone curZone;
	private ISession session;
	private User user;

	@Override
	public void handleServerEvent(final ISFSEvent event) throws SFSException {
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

			final String owner = (String) session.getProperty(VerseExtension.OWNER);
			final Integer lastActor = (Integer) session.getProperty(VerseExtension.LAST_ACTOR);

			final PreparedStatement psChars;
			final String QUERY = "SELECT * FROM actors WHERE owner=?";
			if (lastActor == 0) {
				psChars = connection.prepareStatement(QUERY);
				psChars.setString(1, owner);
			} else {
				psChars = connection.prepareStatement(QUERY + " AND id=?");
				psChars.setString(1, owner);
				psChars.setInt(2, lastActor);
			}

			final ResultSet res = psChars.executeQuery();

			if (!res.first()) {
				// TODO create new character
				return;
			}

			final VerseExtension verseExt = (VerseExtension) getParentExtension();
			final Verse verse = verseExt.getVerse();

			final Actor actor = DatabaseAccessor.loadActor(connection, res);
			DatabaseAccessor.markAsPlayerControlled(verseExt, verse, actor, user);

			final Map<Integer, Stats> blueprints = DatabaseAccessor.getBlueprintCache(connection);
			final SFSArray blueprintArray = SFSArray.newInstance();
			for (final Stats stats : blueprints.values()) {
				blueprintArray.addClass(stats);
			}

			// final ISFSObject actorData = Interpreter.actorToSFSObject(actor);
			// send("initialPlayerData", actorData, user, false);

			final ISFSObject initialData = new SFSObject();
			initialData.putSFSArray(Interpreter.SFS_OBJ_BLUEPRINTS, blueprintArray);
			initialData.putClass(Interpreter.SFS_OBJ_PLAYER_DATA, actor);
			send(Interpreter.SFS_CMD_INIT_DATA, initialData, user, false);

			final Actor temp = (Actor) initialData.getClass(Interpreter.SFS_OBJ_PLAYER_DATA);

			// final ISFSObject playerData = new SFSObject();
			// playerData.putFloat("x", actor.getPos().x);
			// playerData.putFloat("y", actor.getPos().y);
			// send(Interpreter.SFS_CMD_INIT_DATA, playerData, user, false);

			connection.close();
		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}
}
