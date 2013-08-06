package me.tokarski.webrtc.client;

import java.util.HashMap;
import java.util.Map;

import me.tokarski.webrtc.client.wrappers.Utils;

import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;

import de.csenk.gwt.ws.client.WebSocketConnection;
import de.csenk.gwt.ws.client.js.JavaScriptWebSocketFactory;
import de.csenk.gwt.ws.shared.Connection;
import de.csenk.gwt.ws.shared.Handler;

public class WSConnection implements Handler {
	private String url;
	private Connection connection;
	private WSConnectionStateCallback callback;
	private Map<Integer, CallMessageCallback> callMessagesCallback;
	private Map<String, CallMessageCallback> initialMessagesCallback;

	public WSConnection(String url, WSConnectionStateCallback cb) {
		this.url = url;
		this.callback = cb;
		callMessagesCallback = new HashMap<Integer, WSConnection.CallMessageCallback>();
		initialMessagesCallback = new HashMap<String, WSConnection.CallMessageCallback>();
		connect();
	}

	public void addInitialMessageCallback(String callee,
			CallMessageCallback callback) {
		initialMessagesCallback.put(callee, callback);
	}

	public void addCallMessageCallback(Integer callId,
			CallMessageCallback callback) {
		callMessagesCallback.put(callId, callback);
	}

	public void removeCallMessageCallback(Integer callId) {
		callMessagesCallback.remove(callId);
	}

	public void connect() {
		connection = new WebSocketConnection(url, this,
				new JavaScriptWebSocketFactory());
	}

	public void send(Object o) {
		connection.send(o);
	}

	@Override
	public void onConnectionOpened(Connection connection) throws Throwable {
		callback.onConnectionOpened();

	}

	@Override
	public void onConnectionClosed(Connection connection) throws Throwable {
		callback.onConnectionClosed();

	}

	@Override
	public void onExceptionCaught(Connection connection, Throwable caught) {
		Utils.consoleLog("Exception caught on web scoket channel " + caught);
		caught.printStackTrace();

	}

	@Override
	public void onMessageReceived(Connection connection, Object message)
			throws Throwable {
		JSONObject jso = (JSONObject) JSONParser.parseLenient((String) message);
		Utils.consoleDebug(jso.getJavaScriptObject());
		if (!jso.containsKey("command")) {
			Utils.consoleLog("Malformed message received " + jso.toString());
			return;
		}
		String command = ((JSONString) jso.get("command")).stringValue();
		if (command.equals("") || command == null) {
			Utils.consoleLog("Malformed message received " + jso.toString());
			return;
		}
		handleMessage(command, jso);

	}

	private void handleMessage(String command, JSONObject jso) {
		
		if (command.equals("call_request")) {
			callback.onCallUnrelatedMessage(jso);
			return;
		}
		if (jso.containsKey("call_id")) {
			Integer callId = (int) ((JSONNumber) jso.get("call_id"))
					.doubleValue();
			if (command.equals("call_request_sent")) {
				String callee = ((JSONString) jso.get("callee")).stringValue();
				if (initialMessagesCallback.containsKey(callee)) {
					initialMessagesCallback.get(callee).onMessageReceived(jso);
					callMessagesCallback.put(callId,
							initialMessagesCallback.get(callee));
					initialMessagesCallback.remove(callee);
					return;
				} else {
					Utils.consoleLog("Can't find recipient of call_request_sent message with call id "
							+ callId
							+ " and callee "
							+ callee);
				}
			}
			if (callMessagesCallback.containsKey(callId)) {
				callMessagesCallback.get(callId).onMessageReceived(jso);
			} else {
				Utils.consoleLog("Can't find recipient of message with call id "
						+ callId);
				callback.onCallUnrelatedMessage(jso);
			}
		} else {
			callback.onCallUnrelatedMessage(jso);
		}

	}

	public interface WSConnectionStateCallback {
		void onConnectionClosed();

		void onConnectionOpened();

		void onCallUnrelatedMessage(JSONObject jso);
	}

	public interface CallMessageCallback {
		void onMessageReceived(JSONObject jso);
	}
}
