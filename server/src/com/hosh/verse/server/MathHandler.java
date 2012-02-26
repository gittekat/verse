package com.hosh.verse.server;


//public class MathHandler extends BaseClientRequestHandler {
//
//	@Override
//	public void handleClientRequest(User user, ISFSObject obj) {
//		int n1 = obj.getInt("n1");
//		int n2 = obj.getInt("n2");
//		
//		SFSObject res = new SFSObject();
//		
//		res.putInt("sum", n1 + n2);
//		
//		ServerExtension parentExt = (ServerExtension) getParentExtension();
//		parentExt.send("math", res, user);
//	}
//
//}

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
//		
		VerseExtension gameExt = (VerseExtension) getParentExtension();
		
		int moveX = params.getInt("x");
		int	moveY = params.getInt("y");
		
		ISFSObject res = new SFSObject();
//		
		res.putInt("sum", moveX + moveY);
		
		gameExt.send("math", res, user);
		
//		gameExt.trace(String.format("Handling move from player %s. (%s, %s) = %s ", user.getPlayerId(), moveX, moveY));
		
	}
	
}

