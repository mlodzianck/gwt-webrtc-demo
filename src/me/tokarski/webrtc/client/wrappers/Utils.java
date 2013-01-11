package me.tokarski.webrtc.client.wrappers;

import com.google.gwt.core.client.JavaScriptObject;

public class Utils {

	public static native void consoleLog(String message) /*-{
		console.log(message);
	}-*/;

	public static native void consoleLog(JavaScriptObject message) /*-{
		console.debug(message);
	}-*/;

	public static native void consoleDebug(JavaScriptObject message) /*-{
		console.debug(message);
	}-*/;

	public static native String createMediaObjectBlobUrl(JavaScriptObject stream) /*-{
		return URL.createObjectURL(stream);
	}-*/;

}
