package com.hosh.verse;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sfs2x.client.SmartFox;
import sfs2x.client.core.BaseEvent;
import sfs2x.client.core.IEventListener;
import sfs2x.client.core.SFSEvent;
import sfs2x.client.requests.ExtensionRequest;
import sfs2x.client.requests.JoinRoomRequest;
import sfs2x.client.requests.LoginRequest;
import sfs2x.client.requests.LogoutRequest;

import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;

public class SmartFoxTest {
	SmartFox sfsClient;
	boolean setup = false;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		initSmartFox();
		// sfsClient.loadConfig(true);
		connectToServer("192.168.178.35", 9933);

		while (!setup) {
			System.out.println("waiting...");
			Thread.sleep(2000);
		}
	}

	@After
	public void tearDown() throws Exception {
		shutdownSmartFox();
	}

	@Test
	public void test1() {
		final int i = 62;
		final ISFSObject sfso = new SFSObject();
		sfso.putInt("posX", i);
		sfso.putInt("posY", i);
		System.out.println("sending: " + i);
		sfsClient.send(new ExtensionRequest("test", sfso));
	}

	@Test
	public void test2() throws InterruptedException {
		for (int i = 0; i < 100; ++i) {
			final ISFSObject sfso = new SFSObject();
			sfso.putInt("posX", i);
			sfso.putInt("posY", i);
			System.out.println("sending: " + i);
			sfsClient.send(new ExtensionRequest("test", sfso));
			Thread.sleep(500);
		}
	}

	private void shutdownSmartFox() {
		if (sfsClient != null) {
			sfsClient.removeAllEventListeners();
			sfsClient.send(new LogoutRequest());
			sfsClient.disconnect();
		}
	}

	private void connectToServer(final String ip, final int port) {
		final SmartFox sfs = sfsClient;
		new Thread() {
			@Override
			public void run() {
				sfs.connect(ip, port);
			}
		}.start();
	}

	private void initSmartFox() {
		sfsClient = new SmartFox(false);

		sfsClient.addEventListener(SFSEvent.CONNECTION, new IEventListener() {

			@Override
			public void dispatch(final BaseEvent event) throws SFSException {
				if (event.getArguments().get("success").equals(true)) {
					sfsClient.send(new LoginRequest("hosh", "109", "VerseZone"));
					System.out.println("sfs: connecting...");
				} else {
					System.out.println("sfs: connection error");
				}
			}
		});
		sfsClient.addEventListener(SFSEvent.CONNECTION_LOST, new IEventListener() {
			@Override
			public void dispatch(final BaseEvent event) throws SFSException {
				shutdownSmartFox();
				System.out.println("sfs: connection lost");
			}
		});
		sfsClient.addEventListener(SFSEvent.LOGIN, new IEventListener() {
			@Override
			public void dispatch(final BaseEvent arg0) throws SFSException {
				sfsClient.send(new JoinRoomRequest("VerseRoom"));
				setup = true;
			}
		});

		sfsClient.addEventListener(SFSEvent.LOGIN_ERROR, new IEventListener() {
			@Override
			public void dispatch(final BaseEvent arg0) throws SFSException {
				System.out.println("LOGIN_ERROR");
			}
		});

		sfsClient.addEventListener(SFSEvent.LOGOUT, new IEventListener() {
			@Override
			public void dispatch(final BaseEvent event) throws SFSException {
				System.out.println("logout");
			}
		});

		sfsClient.addEventListener(SFSEvent.CONFIG_LOAD_FAILURE, new IEventListener() {
			@Override
			public void dispatch(final BaseEvent arg0) throws SFSException {
				System.out.println("config loading failure!");
			}
		});

		sfsClient.addEventListener(SFSEvent.CONFIG_LOAD_SUCCESS, new IEventListener() {
			@Override
			public void dispatch(final BaseEvent arg0) throws SFSException {
				System.out.println("config loaded successfully!");
			}
		});
	}
}
