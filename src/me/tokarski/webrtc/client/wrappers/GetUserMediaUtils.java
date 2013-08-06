package me.tokarski.webrtc.client.wrappers;

import com.google.gwt.core.client.JavaScriptObject;

public class GetUserMediaUtils {
	public static native void getUserMedia(boolean audio, boolean video,
			GetUserMedaCallback callback) /*-{
		var cb = function(stream) {
			
			callback.@me.tokarski.webrtc.client.wrappers.GetUserMediaUtils.GetUserMedaCallback::navigatorUserMediaSuccessCallback(Lme/tokarski/webrtc/client/wrappers/MediaStream;)(stream);
		}

		var ecb = function(error) {
			callback.@me.tokarski.webrtc.client.wrappers.GetUserMediaUtils.GetUserMedaCallback::navigatorUserMediaErrorCallback(Lcom/google/gwt/core/client/JavaScriptObject;)(error);
		}
		try {
			navigator.webkitGetUserMedia({
				audio : audio,
				video : video
			}, cb, ecb);
		} catch (err) {
			ecb(err);
		}
	}-*/;

	public interface GetUserMedaCallback {
		public void navigatorUserMediaSuccessCallback(MediaStream localStream);

		public void navigatorUserMediaErrorCallback(JavaScriptObject error);
	}

}
