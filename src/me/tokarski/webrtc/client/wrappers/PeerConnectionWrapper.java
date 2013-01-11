package me.tokarski.webrtc.client.wrappers;

import com.google.gwt.core.client.JavaScriptObject;

public class PeerConnectionWrapper {
	JavaScriptObject pc;

	public PeerConnectionWrapper(RTCConfiguration conf,
			PeerConnectionCallbacks callbacks) {
		init(conf, callbacks);
	}

	public native void init(RTCConfiguration conf,
			PeerConnectionCallbacks callbacks) /*-{
		var theInstance = this;
		var c = conf.@me.tokarski.webrtc.client.wrappers.RTCConfiguration::asJavaScriptObject(*)();

		theInstance.@me.tokarski.webrtc.client.wrappers.PeerConnectionWrapper::pc = new webkitRTCPeerConnection(
				c);

		var _onicecandidate = function(e) {
			callbacks.@me.tokarski.webrtc.client.wrappers.PeerConnectionCallbacks::onicecandidate(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
		}
		var _onconnecting = function(e) {
			callbacks.@me.tokarski.webrtc.client.wrappers.PeerConnectionCallbacks::onconnecting(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
		}
		var _onopen = function(e) {
			callbacks.@me.tokarski.webrtc.client.wrappers.PeerConnectionCallbacks::onopen(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
		}
		var _onaddstream = function(e) {
			callbacks.@me.tokarski.webrtc.client.wrappers.PeerConnectionCallbacks::onaddstream(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
		}
		var _onremovestream = function(e) {
			callbacks.@me.tokarski.webrtc.client.wrappers.PeerConnectionCallbacks::onremovestream(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
		}

		theInstance.@me.tokarski.webrtc.client.wrappers.PeerConnectionWrapper::pc.onicecandidate = _onicecandidate;
		theInstance.@me.tokarski.webrtc.client.wrappers.PeerConnectionWrapper::pc.onconnecting = _onconnecting;
		theInstance.@me.tokarski.webrtc.client.wrappers.PeerConnectionWrapper::pc.onopen = _onopen;
		theInstance.@me.tokarski.webrtc.client.wrappers.PeerConnectionWrapper::pc.onaddstream = _onaddstream;
		theInstance.@me.tokarski.webrtc.client.wrappers.PeerConnectionWrapper::pc.onremovestream = _onremovestream;

	}-*/;

	public native void createAnswer(SDPOfferMediaConstraints mediaConstraints,
			SDPCreateOfferCallback callback) /*-{
		var theInstance = this;

		var offerCallback = function(event) {
			callback.@me.tokarski.webrtc.client.wrappers.SDPCreateOfferCallback::RTCSessionDescriptionCallback(Lme/tokarski/webrtc/client/wrappers/RTCSessionDescription;)(event);
		}
		var errorCallback = function(event) {
			callback.@me.tokarski.webrtc.client.wrappers.SDPCreateOfferCallback::RTCPeerConnectionErrorCallback(Lcom/google/gwt/core/client/JavaScriptObject;)(event);
		}

		var mc = mediaConstraints.@me.tokarski.webrtc.client.wrappers.SDPOfferMediaConstraints::asJavaScriptObject(*)();

		theInstance.@me.tokarski.webrtc.client.wrappers.PeerConnectionWrapper::pc
				.createAnswer(offerCallback, errorCallback, mc);
	}-*/;

	public native void createOffer(SDPOfferMediaConstraints mediaConstraints,
			SDPCreateOfferCallback callback) /*-{
		var theInstance = this;

		var offerCallback = function(event) {
			callback.@me.tokarski.webrtc.client.wrappers.SDPCreateOfferCallback::RTCSessionDescriptionCallback(Lme/tokarski/webrtc/client/wrappers/RTCSessionDescription;)(event);
		}
		var errorCallback = function(event) {
			callback.@me.tokarski.webrtc.client.wrappers.SDPCreateOfferCallback::RTCPeerConnectionErrorCallback(Lcom/google/gwt/core/client/JavaScriptObject;)(event);
		}

		var mc = mediaConstraints.@me.tokarski.webrtc.client.wrappers.SDPOfferMediaConstraints::asJavaScriptObject(*)();

		theInstance.@me.tokarski.webrtc.client.wrappers.PeerConnectionWrapper::pc
				.createOffer(offerCallback, errorCallback, mc);
	}-*/;

	public native JavaScriptObject getLocalDescription()/*-{
		var theInstance = this;
		return theInstance.@me.tokarski.webrtc.client.wrappers.PeerConnectionWrapper::pc.localDescription;
	}-*/;

	public native JavaScriptObject getRemoteDescription()/*-{
		var theInstance = this;
		return theInstance.@me.tokarski.webrtc.client.wrappers.PeerConnectionWrapper::pc.remoteDescription;
	}-*/;

	public native void setLocalDescription(JavaScriptObject jso)/*-{
		var theInstance = this;
		theInstance.@me.tokarski.webrtc.client.wrappers.PeerConnectionWrapper::pc
				.setLocalDescription(jso);
	}-*/;

	public native void addStream(JavaScriptObject jso)/*-{
		var theInstance = this;
		console.debug(jso);
		theInstance.@me.tokarski.webrtc.client.wrappers.PeerConnectionWrapper::pc
				.addStream(jso);
	}-*/;

	public native void setRemoteDescription(JavaScriptObject jso)/*-{
		var theInstance = this;
		console.debug(jso);
		theInstance.@me.tokarski.webrtc.client.wrappers.PeerConnectionWrapper::pc
				.setRemoteDescription(new RTCSessionDescription(jso));
	}-*/;

	public native void close()/*-{
		var theInstance = this;
		try {
			theInstance.@me.tokarski.webrtc.client.wrappers.PeerConnectionWrapper::pc
				.close();
		} catch(err) {
			console.log('Exeption while closing peer connection');
			console.debug(err);
		}
	}-*/;

	public native void dumpPCToConsole() /*-{
		console
				.debug(this.@me.tokarski.webrtc.client.wrappers.PeerConnectionWrapper::pc);
	}-*/;

	public native void addIceCandidate(JavaScriptObject jso) /*-{
		var theInstance = this;
		theInstance.@me.tokarski.webrtc.client.wrappers.PeerConnectionWrapper::pc
				.addIceCandidate(new RTCIceCandidate(jso));
	}-*/;

}
