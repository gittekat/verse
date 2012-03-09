package com.hosh.verse.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.hosh.verse.common.ActorFactory;
import com.hosh.verse.common.VerseActor;
import com.hosh.verse.server.eventhandler.LoginEventHandler;
import com.hosh.verse.server.eventhandler.LogoutEventHandler;
import com.hosh.verse.server.eventhandler.MoveHandler;
import com.hosh.verse.server.eventhandler.OnRoomJoinHandler;
import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.db.IDBManager;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.ExtensionLogLevel;
import com.smartfoxserver.v2.extensions.SFSExtension;

public class VerseExtension extends SFSExtension {
	private final static String version = "0.0.1";
	public static final String DATABASE_ID = "dbID";
	public static final String ACCOUNT_NAME = "accountName";
	public static final String CHAR_ID = "charId";
	private Verse verse;

	private Map<Integer, User> userLookupTable = new HashMap<Integer, User>();
	private Map<User, Integer> charIdLookupTable = new HashMap<User, Integer>();

	// Keeps a reference to the task execution
	private ScheduledFuture<?> taskHandle;
	private int milliseconds = 50;
	private float seconds = milliseconds / 1000.f;

	@Override
	public void init() {
		trace("verse server extension, rel. " + version);

		final Connection connection = getDbConnection();
		if (connection == null) {
			trace(ExtensionLogLevel.ERROR, "database connection failed!");
		}

		verse = new Verse(connection, 1000, 1000); // TODO don't use connection
													// and please close it here!

		final SmartFoxServer sfs = SmartFoxServer.getInstance();
		// Schedule the task to run every second, with no initial delay
		taskHandle = sfs.getTaskScheduler().scheduleAtFixedRate(new TaskRunner(), 0, milliseconds, TimeUnit.MILLISECONDS);

		addEventHandler(SFSEventType.USER_LOGIN, LoginEventHandler.class);
		addEventHandler(SFSEventType.USER_JOIN_ROOM, OnRoomJoinHandler.class);
		addEventHandler(SFSEventType.USER_LOGOUT, LogoutEventHandler.class);
		addRequestHandler("YouBitch", MoveHandler.class);
	}

	private class TaskRunner implements Runnable {
		private int runningCycles = 0;

		@Override
		public void run() {
			runningCycles++;
			// trace("Inside the running task. Cycle:  " + runningCycles);

			verse.update(seconds);

			final Map<Integer, VerseActor> playerMap = verse.getPlayerMap();
			// if (runningCycles % 100 == 0) {
			// trace("TaskRunner alive with player count: " + playerMap.size());
			// }

			for (final VerseActor actor : playerMap.values()) {
				final User user = userLookupTable.get(actor.getCharId());

				if (user != null) {
					if (runningCycles % 20 == 0) {
						final ISFSObject playerData = new SFSObject();
						playerData.putInt(VerseActor.CHAR_ID, actor.getCharId());
						playerData.putFloat("x", actor.getPos().x);
						playerData.putFloat("y", actor.getPos().y);
						playerData.putInt("testx", 250);
						playerData.putInt("testy", 190);

						send("playerData", playerData, user, false);
					}

					if (runningCycles % 100 == 0) {
						for (final VerseActor others : verse.getVisibleActors(actor)) {
							send("actor", ActorFactory.createSFSObject(others), user, false);
						}

						for (final VerseActor player : verse.getPlayerMap().values()) {
							send("player", ActorFactory.createSFSObject(player), user, false);
						}
					}
				}
			}

			// if (runningCycles >= 200) {
			// trace("Time to stop the task!");
			// taskHandle.cancel(true);
			// }
		}
	}

	@Override
	public void destroy() {
		super.destroy();
		trace("verse destroyed!");
		taskHandle.cancel(true);
	}

	private Connection getDbConnection() {
		final IDBManager dbManager = getParentZone().getDBManager();
		try {
			// Grab a connection from the DBManager connection pool
			final Connection connection = dbManager.getConnection();

			// Build a prepared statement
			final PreparedStatement stmt = connection.prepareStatement("SELECT * FROM accounts");

			// Execute query
			final ResultSet res = stmt.executeQuery();

			// check connection
			if (!res.first()) {
				trace("noone in db found!");
				return null;
			} else {
				trace("rows found in accounts: " + res.getRow());
				return connection;
			}
		} catch (final Exception e) {
			trace(e.getMessage());
		}

		return null;
	}

	public Verse getVerse() {
		return verse;
	}

	// public Map<Integer, User> getUserLookupTable() {
	// return userLookupTable;
	// }

	public void addPlayer(final int charId, final User user) {
		userLookupTable.put(charId, user);
		charIdLookupTable.put(user, charId);
	}

	public Integer removePlayer(final User user) {
		final Integer charId = charIdLookupTable.get(user);
		userLookupTable.remove(charId);
		charIdLookupTable.remove(user);
		return charId;
	}

	public Integer getCharId(final User user) {
		return charIdLookupTable.get(user);
	}

	public User getUser(final Integer charId) {
		return userLookupTable.get(charId);
	}
}
