package com.hosh.verse.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.smartfoxserver.bitswarm.sessions.ISession;
import com.smartfoxserver.v2.annotations.Instantiation;
import com.smartfoxserver.v2.annotations.Instantiation.InstantiationMode;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.db.IDBManager;
import com.smartfoxserver.v2.exceptions.SFSErrorCode;
import com.smartfoxserver.v2.exceptions.SFSErrorData;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.exceptions.SFSLoginException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;

@Instantiation(InstantiationMode.SINGLE_INSTANCE)
public class LoginEventHandler extends BaseServerEventHandler {

	int cnt = 0;
	private VerseExtension verseExt;
	private Verse verse;

	@Override
	public void handleServerEvent(final ISFSEvent event) throws SFSException {
		final String userName = (String) event.getParameter(SFSEventParam.LOGIN_NAME);
		final String cryptedPass = (String) event.getParameter(SFSEventParam.LOGIN_PASSWORD);
		final ISession session = (ISession) event.getParameter(SFSEventParam.SESSION);

		trace("LoginEventHandler: " + cryptedPass);

		login(userName, cryptedPass, session);

		cnt++;

		verseExt = (VerseExtension) getParentExtension();
		verse = verseExt.getVerse();
	}

	private void login(final String userName, final String cryptedPass, final ISession session) throws SFSException {

		trace("LoginEventHandler invoked: loggin in " + userName + "...");

		// Get password from DB
		final IDBManager dbManager = getParentExtension().getParentZone().getDBManager();
		Connection connection;

		try {
			// Grab a connection from the DBManager connection pool
			connection = dbManager.getConnection();

			// Build a prepared statement
			final PreparedStatement stmt = connection.prepareStatement("SELECT password FROM accounts WHERE login=?");
			stmt.setString(1, userName);

			// Execute query
			final ResultSet res = stmt.executeQuery();

			// Verify that one record was found
			if (!res.first()) {
				// This is the part that goes to the client
				final SFSErrorData errData = new SFSErrorData(SFSErrorCode.LOGIN_BAD_USERNAME);
				errData.addParameter(userName);

				// This is logged on the server side
				throw new SFSLoginException("Bad user name: " + userName, errData);
			}

			final String dbPword = res.getString("password");

			// Verify the secure password
			if (!getApi().checkSecurePassword(session, dbPword, cryptedPass)) {
				final SFSErrorData data = new SFSErrorData(SFSErrorCode.LOGIN_BAD_PASSWORD);
				data.addParameter(userName);

				throw new SFSLoginException("Login failed for user: " + userName, data);
			}

			// get first char for this account //TODO charater selection
			final PreparedStatement stmtChar = connection.prepareStatement("SELECT charId, char_name FROM characters WHERE account_name=?");
			stmtChar.setString(1, userName);

			final ResultSet resChar = stmtChar.executeQuery();

			// Verify that one record was found
			if (!resChar.first()) {
				// This is the part that goes to the client
				final SFSErrorData errData = new SFSErrorData(SFSErrorCode.LOGIN_GUEST_NOT_ALLOWED);
				errData.addParameter(userName);

				// This is logged on the server side
				throw new SFSLoginException("no character found for this account: " + userName, errData);
			}

			final String charName = resChar.getString("char_name");
			final int charId = resChar.getInt("charId");
			trace("LoginEventHandler: " + charName + " logged in");

			// Store the client dbId in the session
			session.setProperty(VerseExtension.DATABASE_ID, charId);

			// Return connection to the DBManager connection pool
			connection.close();
		}

		// User name was not found
		catch (final SQLException e) {
			final SFSErrorData errData = new SFSErrorData(SFSErrorCode.GENERIC_ERROR);
			errData.addParameter("SQL Error: " + e.getMessage());

			throw new SFSLoginException("A SQL Error occurred: " + e.getMessage(), errData);
		}
	}
}
