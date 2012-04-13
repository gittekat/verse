package com.hosh.verse.server.eventhandler;

import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

public class TestHandler extends BaseClientRequestHandler {

	@Override
	public void handleClientRequest(final User user, final ISFSObject params) {
		final int x = params.getInt("posX");
		final int y = params.getInt("posY");

		System.out.println("TestHandler: " + x + " / " + y);
	}

}
