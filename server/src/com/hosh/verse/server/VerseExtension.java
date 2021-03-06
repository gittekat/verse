package com.hosh.verse.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.hosh.verse.common.Actor;
import com.hosh.verse.common.IPositionable;
import com.hosh.verse.common.Interpreter;
import com.hosh.verse.common.MovementData;
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
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.ExtensionLogLevel;
import com.smartfoxserver.v2.extensions.SFSExtension;

public class VerseExtension extends SFSExtension {
	private final static String version = "0.0.2";
	public static final String DATABASE_ID = "dbID";
	public static final String OWNER = "owner";
	public static final String CHAR_ID = "charId";
	public static final String LAST_ACTOR = "lastActor";

	private Verse verse;

	private Map<Integer, User> userLookupTable = new HashMap<Integer, User>();
	private Map<User, Integer> actorIdLookupTable = new HashMap<User, Integer>();

	// Keeps a reference to the task execution
	private ScheduledFuture<?> taskHandle;
	private int milliseconds = 50;

	@Override
	public void init() {
		trace("verse server extension, rel. " + version);

		final Connection connection = getDbConnection();
		if (connection == null) {
			trace(ExtensionLogLevel.ERROR, "database connection failed!");
		}

		verse = new Verse(connection, 10000, 10000);

		final SmartFoxServer sfs = SmartFoxServer.getInstance();
		// Schedule the task to run every second, with no initial delay
		taskHandle = sfs.getTaskScheduler().scheduleAtFixedRate(new TaskRunner(), 0, milliseconds, TimeUnit.MILLISECONDS);

		addEventHandler(SFSEventType.USER_LOGIN, LoginEventHandler.class);
		addEventHandler(SFSEventType.USER_JOIN_ROOM, OnRoomJoinHandler.class);
		addEventHandler(SFSEventType.USER_LOGOUT, LogoutEventHandler.class);
		addEventHandler(SFSEventType.USER_DISCONNECT, LogoutEventHandler.class);

		addRequestHandler("move", MoveHandler.class);
		addRequestHandler("test", TestHandler.class);

		DatabaseAccessor.preloadActors(connection);
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

			final Map<Integer, Actor> playerMap = verse.getPlayerControlledActors();
			if (runningCycles % 200 == 0) {
				trace("TaskRunner alive with player count: " + playerMap.size());
			}

			if (runningCycles % 5 == 0) {
				// send movement data
				for (final Actor actor : playerMap.values()) {
					final User user = userLookupTable.get(actor.getId());

					if (user != null) {
						final ISFSObject movementData = new SFSObject();
						movementData.putClass(Interpreter.SFS_OBJ_MOVEMENT_DATA_PLAYER, new MovementData(actor));

						final SFSArray movementDataArray = SFSArray.newInstance();
						for (final IPositionable other : verse.getVisibleActors(actor)) {
							if (((Actor) other).getId() == actor.getId()) {
								continue;
							}

							movementDataArray.addClass(new MovementData((Actor) other));
						}

						// should be true aka udp
						movementData.putSFSArray(Interpreter.SFS_OBJ_MOVEMENT_DATA, movementDataArray);
						send(Interpreter.SFS_CMD_MOVEMENT, movementData, user, false);
					}
				}
			}
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

	public void addUser(final int actorId, final User user) {
		userLookupTable.put(actorId, user);
		actorIdLookupTable.put(user, actorId);
	}

	public Integer removeUser(final User user) {
		final Integer actorId = actorIdLookupTable.get(user);
		userLookupTable.remove(actorId);
		actorIdLookupTable.remove(user);
		return actorId;
	}

	public Integer getActorId(final User user) {
		return actorIdLookupTable.get(user);
	}

	public User getUser(final Integer actorId) {
		return userLookupTable.get(actorId);
	}
}
