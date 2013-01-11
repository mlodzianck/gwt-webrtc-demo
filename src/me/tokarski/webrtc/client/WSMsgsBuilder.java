package me.tokarski.webrtc.client;

import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

public class WSMsgsBuilder {
	public static String registrationMsg(String nickName) {
		JSONObject jso = new JSONObject();
		jso.put("command", new JSONString("register"));
		jso.put("name", new JSONString(nickName));
		return jso.toString();
	}
	
	public static String callMsg(String callee) {
		JSONObject jso = new JSONObject();
		jso.put("command", new JSONString("call"));
		jso.put("name", new JSONString(callee));
		return jso.toString();
	}

	public static String confirmCallMsg(Integer callid) {
		JSONObject jso = new JSONObject();
		jso.put("command", new JSONString("confirm_call"));
		jso.put("call_id", new JSONNumber(callid));
		return jso.toString();
	}
	
	public static String declineCallMsg(Integer callid) {
		JSONObject jso = new JSONObject();
		jso.put("command", new JSONString("decline_call"));
		jso.put("call_id", new JSONNumber(callid));
		return jso.toString();
	}
	
	public static String endCallMsg(Integer callid) {
		JSONObject jso = new JSONObject();
		jso.put("command", new JSONString("end_call"));
		jso.put("call_id", new JSONNumber(callid));
		return jso.toString();
	}
	
	public static String relayMsg(Integer callid,JSONObject toRelay) {
		JSONObject jso = new JSONObject();
		jso.put("command", new JSONString("relay"));
		jso.put("call_id", new JSONNumber(callid));
		jso.put("payload", toRelay);
		return jso.toString();
	}
}
