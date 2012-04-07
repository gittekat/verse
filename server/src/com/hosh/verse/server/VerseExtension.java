package com.hosh.verse.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.hosh.verse.common.Interpreter;
import com.hosh.verse.common.VerseActor;
import com.hosh.verse.server.database.DatabaseAccessor;
import com.hosh.verse.server.eventhandler.LoginEventHandler;
import com.hosh.verse.server.eventhandler.LogoutEventHandler;
import com.hosh.verse.server.eventhandler.MoveHandler;
import com.hosh.verse.server.eventhandler.OnRoomJoinHandler;
import com.hosh.verse.server.eventhandler.TestHandler;
import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.db.IDBManager;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.ExtensionLogLevel;
import com.smartfoxserver.v2.extensions.SFSExtension;

public class VerseExtension extends SFSExtension {
	private final static String version = "0.0.2";
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

		verse = new Verse(connection, 1000, 1000); // TODO don't use
													// connection
													// and please close it
													// here!

		final SmartFoxServer sfs = SmartFoxServer.getInstance();
		// Schedule the task to run every second, with no initial delay
		taskHandle = sfs.getTaskScheduler().scheduleAtFixedRate(new TaskRunner(), 0, milliseconds, TimeUnit.MILLISECONDS);

		addEventHandler(SFSEventType.USER_LOGIN, LoginEventHandler.class);
		addEventHandler(SFSEventType.USER_JOIN_ROOM, OnRoomJoinHandler.class);
		addEventHandler(SFSEventType.USER_LOGOUT, LogoutEventHandler.class);
		addEventHandler(SFSEventType.USER_DISCONNECT, LogoutEventHandler.class);

		addRequestHandler("move", MoveHandler.class);

		// testing
		addRequestHandler("test", TestHandler.class);
	}

	private class TaskRunner implements Runnable {
		private int runningCycles = 0;
		private long lastTime = System.nanoTime();

		@Override
		public void run() {
			runningCycles++;
			// trace("Inside the running task. Cycle:  " + runningCycles);

			// delta time calculated like in LwjglGraphics
			final long time = System.nanoTime();
			final float deltaTime = (time - lastTime) / 1000000000.0f;
			lastTime = time;

			verse.update(deltaTime);

			final Map<Integer, VerseActor> playerMap = verse.getPlayerMap();
			if (runningCycles % 200 == 0) {
				trace("TaskRunner alive with player count: " + playerMap.size());
			}

			for (final VerseActor actor : playerMap.values()) {
				final User user = userLookupTable.get(actor.getCharId());

				if (user != null) {
					if (runningCycles % 5 == 0) {
						final ISFSObject playerData = new SFSObject();
						playerData.putFloat("x", actor.getPos().x);
						playerData.putFloat("y", actor.getPos().y);

						send("playerData", playerData, user, false);
					}

					if (runningCycles % 6 == 0) {
						for (final VerseActor others : verse.getVisibleActors(actor)) {
							send("actor", Interpreter.createSFSObject(others), user, false);
						}

						for (final VerseActor player : verse.getPlayerMap().values()) {
							if (actor.getCharId() == player.getCharId()) {
								continue;
							}

							final ISFSObject playerObj = Interpreter.createSFSObject(player);
							if (playerObj.getFloat(VerseActor.TARGET_POS_X) != player.getTargetPos().x) {
								trace("WTF!");
							}
							send("player", playerObj, user, false);
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
			final PreparedStatement stmt = connection.prepareStatement("SELECT * FROM " + DatabaseAccessor.TABLE_OWNERS);

			// Execute query
			final ResultSet res = stmt.executeQuery();

			// check connection
			if (!res.first()) {
				trace("noone in db found!");
				return null;
			} else {
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
