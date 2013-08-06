package me.tokarski.webrtc.client.wrappers;

import com.allen_sauer.gwt.log.client.DivLogger;
import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;

public class Utils {

	

	// public static native void consoleLog(String message) /*-{
	// console.log(message);
	// }-*/;

	public static void consoleLog(String message) {
		Log.debug(message);
	}

	public static void consoleLog(JavaScriptObject message) {
		Log.debug((new JSONObject(message)).toString());
	}

	// public static native void consoleLog(JavaScriptObject message) /*-{
	// console.debug(message);
	// }-*/;

	public static void consoleDebug(JavaScriptObject message) {
		Log.debug((new JSONObject(message)).toString());
	}

	// public static native void consoleDebug(JavaScriptObject message) /*-{
	// console.debug(message);
	// }-*/;

	public static native String createMediaObjectBlobUrl(JavaScriptObject stream) /*-{
		return URL.createObjectURL(stream);
	}-*/;

}
