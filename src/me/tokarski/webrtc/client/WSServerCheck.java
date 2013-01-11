package me.tokarski.webrtc.client;

import me.tokarski.webrtc.client.wrappers.Utils;
import de.csenk.gwt.ws.client.WebSocketConnection;
import de.csenk.gwt.ws.client.js.JavaScriptWebSocketFactory;
import de.csenk.gwt.ws.shared.Connection;
import de.csenk.gwt.ws.shared.Handler;

public class WSServerCheck {
	public static void checkServerAlive(String url, final CheckCallback callback) {
		Handler h = new Handler() {

			private boolean connected=false;

			@Override
			public void onMessageReceived(Connection connection, Object message)
					throws Throwable {
				// TODO Auto-generated method stub

			}

			@Override
			public void onExceptionCaught(Connection connection,
					Throwable caught) {
				callback.serverDead();

			}

			@Override
			public void onConnectionOpened(Connection connection)
					throws Throwable {
				callback.serverAlive();
				connected=true;
				connection.close();

			}

			@Override
			public void onConnectionClosed(Connection connection)
					throws Throwable {
				if (connected) return;
				callback.serverDead();

			}
		};
		Utils.consoleLog("Connecting to "+url);
		new WebSocketConnection(url, h, new JavaScriptWebSocketFactory());

	}

	public interface CheckCallback {
		public void serverAlive();

		public void serverDead();
	}

}
