package com.hosh.verse.server;

import com.smartfoxserver.v2.annotations.Instantiation;
import com.smartfoxserver.v2.annotations.Instantiation.InstantiationMode;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.exceptions.SFSRuntimeException;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

@Instantiation(InstantiationMode.SINGLE_INSTANCE)
public class MathHandler extends BaseClientRequestHandler
{
	
	@Override
	public void handleClientRequest(User user, ISFSObject params)
	{
		// Check params
		if (!params.containsKey("x") || !params.containsKey("y"))
			throw new SFSRuntimeException("Invalid request, one mandatory param is missing. Required 'x' and 'y'");
		
		VerseExtension verseExt = (VerseExtension) getParentExtension();
		Verse verse = verseExt.getVerse();
		
		Float moveX = params.getFloat("x");
		Float moveY = params.getFloat("y");
		
		ISFSObject res = new SFSObject();
//		
		res.putFloat("sum", moveX + moveY);
		
		verseExt.send("math", res, user);
		
//		gameExt.trace(String.format("Handling move from player %s. (%s, %s) = %s ", user.getPlayerId(), moveX, moveY));
		
	}
	
}

