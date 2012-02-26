package com.hosh.verse.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.smartfoxserver.bitswarm.sessions.Session;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.db.IDBManager;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.exceptions.SFSErrorCode;
import com.smartfoxserver.v2.exceptions.SFSErrorData;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.exceptions.SFSLoginException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;

public class OnRoomJoinHandler extends BaseServerEventHandler {

	private Room curRoom;
	private VerseExtension verseExt;
	private Zone curZone;

	@Override
	public void handleServerEvent(ISFSEvent event) throws SFSException {
		
		trace("OnRoomJoinHandler invoked.");
		
		User player =(User) event.getParameter(SFSEventParam.USER);
		curRoom = player.getLastJoinedRoom();
		verseExt = (VerseExtension) getParentExtension();
		curZone = player.getZone();
		
		Session s = (Session) player.getSession();
		int port = s.getServerPort();

		trace("player joined room: " + player.getName() + " - in zone: " + curZone.getName() + " on port " + port);
		
////		ISFSObject obj = new SFSObject();
////		obj.putUtfString("msg", "Welcome to verse " + player.getName() + " - You're in the " + curZone.getName() + " zone on port " + port);
////		
////		zoneExt.send("welcome", obj,  player, false);
//		
		VerseExtension verseExt = (VerseExtension) getParentExtension();
		Verse verse = verseExt.getVerse();
		
//		dbConnectionTest();
		
	}

	private void dbConnectionTest() throws SFSLoginException {
		// Grab parameters from client request
//				String userName = (String) event.getParameter(SFSEventParam.LOGIN_NAME);
//				String cryptedPass = (String) event.getParameter(SFSEventParam.LOGIN_PASSWORD);
//				ISession session = (ISession) event.getParameter(SFSEventParam.SESSION);
		
		String userName = "kermit";
		String crypedPass = "test";
		
				
				// Get password from DB
//		IDBManager dbManager = getParentExtension().getParentZone().getDBManager();
				IDBManager dbManager = getParentExtension().getParentZone().getDBManager();
				Connection connection;
				
		        try
		        {
		        	// Grab a connection from the DBManager connection pool
			        connection = dbManager.getConnection();
			        
			        // Build a prepared statement
			        PreparedStatement stmt = connection.prepareStatement("SELECT pword,id FROM muppets WHERE name=?");
			        stmt.setString(1, userName);
			        
			        // Execute query
					ResultSet res = stmt.executeQuery();
					
					// Verify that one record was found
					if (!res.first())
					{
						// This is the part that goes to the client
						SFSErrorData errData = new SFSErrorData(SFSErrorCode.LOGIN_BAD_USERNAME);
						errData.addParameter(userName);
						
						// This is logged on the server side
						throw new SFSLoginException("Bad user name: " + userName, errData);
					}
					
					String dbPword = res.getString("pword");
					int dbId = res.getInt("id");
					
//					// Verify the secure password
//					if (!getApi().checkSecurePassword(session, dbPword, cryptedPass))
//					{
//						SFSErrorData data = new SFSErrorData(SFSErrorCode.LOGIN_BAD_PASSWORD);
//						data.addParameter(userName);
//						
//						throw new SFSLoginException("Login failed for user: "  + userName, data);
//					}
//					
//					// Store the client dbId in the session
//					session.setProperty(DBLogin.DATABASE_ID, dbId);
					
					// Return connection to the DBManager connection pool
					connection.close();
		        }
		        
		        // User name was not found
		        catch (SQLException e)
		        {
		        	SFSErrorData errData = new SFSErrorData(SFSErrorCode.GENERIC_ERROR);
		        	errData.addParameter("SQL Error: " + e.getMessage());
		        	
		        	throw new SFSLoginException("A SQL Error occurred: " + e.getMessage(), errData);
		        }
	}
	
}
