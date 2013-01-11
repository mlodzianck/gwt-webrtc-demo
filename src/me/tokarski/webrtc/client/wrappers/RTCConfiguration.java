package me.tokarski.webrtc.client.wrappers;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

public class RTCConfiguration {
	private String[] iceServers;

	public RTCConfiguration(String[] iceServers) {
		super();
		this.iceServers = iceServers;
	}

	public JavaScriptObject asJavaScriptObject() {
		return _asJavaScriptObject(wrapArray(iceServers));

	}

	private native JavaScriptObject _asJavaScriptObject(JsArrayString iceServers) /*-{
		
		var is = new Array();
		for ( var i = 0; i < iceServers.length; i++) {
			is.push({url: iceServers[i]});
		}
		var ret= {
			iceServers: is
		};
		
		return ret;

	}-*/;

	// From http://www.java2s.com/Code/Java/GWT/JsArrayUtil.htm
	private static JsArrayString wrapArray(String[] srcArray) {
		JsArrayString result = JavaScriptObject.createArray().cast();
		for (int i = 0; i < srcArray.length; i++) {
			result.set(i, srcArray[i]);
		}
		return result;
	}

}
