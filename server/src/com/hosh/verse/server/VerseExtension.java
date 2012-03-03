package com.hosh.verse.server;

//import com.smartfoxserver.v2.extensions.ISFSExtension;
//import com.smartfoxserver.v2.extensions.SFSExtension;
//
//public class ServerExtension extends SFSExtension implements ISFSExtension {
//
//	@Override
//	public void init() {
//		// TODO Auto-generated method stub
//		trace("Hello, this is my first SFS2X Extension!");
////		this.addEventHandler(SFSEventType.USER_JOIN_ROOM, OnRoomJoinHandler.class);
//		
//		this.addRequestHandler("math", MathHandler.class);
//	}
//	
//	public void destroy() {
//		super.destroy();
//	}
//
//}

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.db.IDBManager;
import com.smartfoxserver.v2.extensions.SFSExtension;

public class VerseExtension extends SFSExtension {
	private final static String version = "0.0.1";

	public static final String DATABASE_ID = "dbID";

	private Verse verse;

	@Override
	public void init() {
		trace("verse server extension, rel. " + version);

		verse = new Verse(1000, 1000);

		addEventHandler(SFSEventType.USER_LOGIN, LoginEventHandler.class);

		addEventHandler(SFSEventType.USER_JOIN_ROOM, OnRoomJoinHandler.class);

		addRequestHandler("move", MathHandler.class);

		dbTest();
	}

	@Override
	public void destroy() {
		super.destroy();
		trace("verse destroyed!");
	}

	public Verse getVerse() {
		return verse;
	}

	private void dbTest() {
		final IDBManager dbManager = getParentZone().getDBManager();
		Connection connection;

		try {
			// Grab a connection from the DBManager connection pool
			connection = dbManager.getConnection();

			// Build a prepared statement
			final PreparedStatement stmt = connection.prepareStatement("SELECT * FROM accounts");

			// Execute query
			final ResultSet res = stmt.executeQuery();

			// Verify that one record was found
			if (!res.first()) {
				trace("noone in db found!");
			} else {
				// final String name = res.getString("name");
				// trace("the kermit: hi my name is the " + name);
				trace("rows found in accounts: " + res.getRow());
			}
		} catch (final Exception e) {
			trace(e.getMessage());
		}
	}

}
