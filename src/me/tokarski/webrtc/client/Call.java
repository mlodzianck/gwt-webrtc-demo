package me.tokarski.webrtc.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.media.client.Video;
import com.google.gwt.user.client.ui.ComplexPanel;

import de.csenk.gwt.ws.shared.Connection;

import me.tokarski.webrtc.client.Call.CallStateListener;
import me.tokarski.webrtc.client.Call.Direction;
import me.tokarski.webrtc.client.WSConnection.CallMessageCallback;
import me.tokarski.webrtc.client.wrappers.PeerConnectionCallbacks;
import me.tokarski.webrtc.client.wrappers.PeerConnectionWrapper;
import me.tokarski.webrtc.client.wrappers.RTCConfiguration;
import me.tokarski.webrtc.client.wrappers.RTCSessionDescription;
import me.tokarski.webrtc.client.wrappers.SDPCreateOfferCallback;
import me.tokarski.webrtc.client.wrappers.SDPOfferMediaConstraints;
import me.tokarski.webrtc.client.wrappers.Utils;

public class Call implements PeerConnectionCallbacks, CallMessageCallback{
	private PeerConnectionWrapper pc;
	private Video remoteVideoElement;
	private JavaScriptObject localMediaStream;
	private WSConnection wsConnection;
	private Integer callId;
	private Direction direction;
	private CallStateListener callStateListener;
	private boolean videoEnabled = false;
	public Call(Direction direction,JavaScriptObject localMediaStream,WSConnection wsConnection,Video remoteVideo,CallStateListener listener,Integer callId) {
		this(direction, localMediaStream, wsConnection, remoteVideo, listener,null,callId);
	}
	public Call(Direction direction,JavaScriptObject localMediaStream,WSConnection wsConnection,Video remoteVideo,CallStateListener listener,String callee) {
		this(direction, localMediaStream, wsConnection, remoteVideo, listener,callee,null);
	}
	public Call(Direction direction,JavaScriptObject localMediaStream,WSConnection wsConnection,Video remoteVideo,CallStateListener listener) {
		this(direction, localMediaStream, wsConnection, remoteVideo, listener,null,null);
	}
	public Call(Direction direction,JavaScriptObject localMediaStream,WSConnection wsConnection,Video remoteVideo,CallStateListener listener,String callee,Integer callId) {
		RTCConfiguration conf = new RTCConfiguration(
				new String[] { "stun:stun.l.google.com:19302" });
		pc = new PeerConnectionWrapper(conf, this);
		pc.addStream(localMediaStream);
		
		this.localMediaStream=localMediaStream;
		this.wsConnection=wsConnection;
		this.remoteVideoElement=remoteVideo;
		this.direction=direction;
		this.callStateListener=listener;
		if (this.direction.equals(Direction.OUT)) {
			listener.onCallProceding();
			wsConnection.addInitialMessageCallback(callee, this);
			wsConnection.send(WSMsgsBuilder.callMsg(callee));
		}
		if (callId!= null) {
			wsConnection.addCallMessageCallback(callId, this);
			this.callId=callId;
		}
	}


	public Call(Direction out, JavaScriptObject localStream,
			Connection connection, Video v, CallStateListener listener,
			String text) {
		// TODO Auto-generated constructor stub
	}
	@Override
	public void onicecandidate(JavaScriptObject jso) {
		JSONObject o = new JSONObject(jso);
		if (o.containsKey("candidate") && !(o.get("candidate") instanceof JSONNull)){
			JSONObject candidate = (JSONObject) o.get("candidate");
			JSONObject toRelay= new JSONObject();
			toRelay.put("type", new JSONString("iceCandidate"));
			toRelay.put("cadidate", candidate);
			//Utils.consoleDebug(toRelay.getJavaScriptObject());
			wsConnection.send(WSMsgsBuilder.relayMsg(callId, toRelay));
		}
		
	}


	@Override
	public void onconnecting(JavaScriptObject jso) {
		Utils.consoleLog("Peerconnection onconnecting");
		Utils.consoleDebug(jso);
		
	}


	@Override
	public void onopen(JavaScriptObject jso) {
		Utils.consoleLog("Peerconnection onopen");
		Utils.consoleDebug(jso);
		
	}


	@Override
	public void onaddstream(JavaScriptObject jso) {
		callStateListener.onCallStarted();
		JavaScriptObject stream  = ((JSONObject)(new JSONObject(jso)).get("stream")).getJavaScriptObject();
		String mediaBlobUrl = Utils.createMediaObjectBlobUrl(stream);
		remoteVideoElement.setSrc(mediaBlobUrl);
	}


	@Override
	public void onremovestream(JavaScriptObject jso) {
		
		wsConnection.send(WSMsgsBuilder.endCallMsg(callId));
		
		
	}


	@Override
	public void onMessageReceived(JSONObject jso) {
		this.callId = (int) ((JSONNumber)jso.get("call_id")).doubleValue();
		String cmd = ((JSONString)jso.get("command")).stringValue();
		if (cmd.equals("b_party_confirmation")) {
			handle_b_party_confirmation();
		} else if (cmd.equals("b_party_declined")) {
			handle_b_party_declined();
		} else if (cmd.equals("b_party_confirmation_timeout")) {
			handle_b_party_confirmation_timeout();
		} else if (cmd.equals("termniate")) {
			handle_termniate();
		} else if (cmd.equals("relayed")) {
			handle_relayed(jso);
		} else {
			Utils.consoleLog("Unhandled message");
			Utils.consoleLog(jso.getJavaScriptObject());
		}
		
		
	}


	private void handle_relayed(JSONObject jso) {
		
		
		JSONObject payload = (JSONObject) jso.get("payload");
		String type = ((JSONString)payload.get("type")).stringValue();
		if (type.equals("iceCandidate")) {
			JSONObject cadidate = (JSONObject) payload.get("cadidate");
			Utils.consoleLog("Remote candidate");
			Utils.consoleDebug(jso.getJavaScriptObject());
			
			pc.addIceCandidate(cadidate.getJavaScriptObject());
		} else
		if (type.equals("sdpOffer")) {
			JSONObject sdp = (JSONObject) payload.get("sdp");
			pc.setRemoteDescription(sdp.getJavaScriptObject());
			pc.createAnswer(new SDPOfferMediaConstraints(true, true), new SDPCreateOfferCallback() {
				
				@Override
				public void RTCSessionDescriptionCallback(RTCSessionDescription sdp) {
					Utils.consoleLog("Got local SDP");
					Utils.consoleDebug(sdp);
					pc.setLocalDescription(sdp);
					JSONObject jso = new JSONObject();
					jso.put("type", new JSONString("sdpAnswer"));
					jso.put("sdp", new JSONObject(sdp));
					wsConnection.send(WSMsgsBuilder.relayMsg(callId, jso));
					
				}
				
				@Override
				public void RTCPeerConnectionErrorCallback(JavaScriptObject error) {
					wsConnection.send(WSMsgsBuilder.endCallMsg(callId));
					Utils.consoleLog("Got error while getting local SDP");
					Utils.consoleDebug(error);
					Call.this.callStateListener.onCallTerminate("Error geting local SDP");
					
				}
			});
		} else
		if (type.equals("sdpAnswer")) {
			JSONObject sdp = (JSONObject) payload.get("sdp");
			Utils.consoleLog("Remote SDP");
			Utils.consoleDebug(sdp.getJavaScriptObject());
			pc.setRemoteDescription(sdp.getJavaScriptObject());
		} else {
			Utils.consoleLog("Unhnadled relay message");
			Utils.consoleDebug(jso.getJavaScriptObject());
		}
	}


	private void handle_termniate() {
		Utils.consoleLog("Call "+callId+" terminated");
		callStateListener.onCallTerminate("Normal");
		
		pc.close();
		remoteVideoElement.setSrc("null");
	}
	
	public void hangup() {
		wsConnection.send(WSMsgsBuilder.endCallMsg(callId));
		callStateListener.onCallTerminate("Normal");
		
		pc.close();
		remoteVideoElement.setSrc("null");	
	}


	private void handle_b_party_confirmation_timeout() {
		callStateListener.onCallTerminate("Remote didn't accept our call, timeout");
		
	}


	private void handle_b_party_declined() {
		callStateListener.onCallTerminate("Remote declined our call");
	}


	private void handle_b_party_confirmation() {
		Utils.consoleLog("B-party confirmed our call, lets go!");
		pc.createOffer(new SDPOfferMediaConstraints(true, videoEnabled), new SDPCreateOfferCallback() {
			
			@Override
			public void RTCSessionDescriptionCallback(RTCSessionDescription sdp) {
				Utils.consoleLog("Got local SDP ");
				Utils.consoleDebug(sdp);
				pc.setLocalDescription(sdp);
				JSONObject jso = new JSONObject();
				jso.put("type", new JSONString("sdpOffer"));
				jso.put("sdp", new JSONObject(sdp));
				wsConnection.send(WSMsgsBuilder.relayMsg(callId, jso));
				
			}
			
			@Override
			public void RTCPeerConnectionErrorCallback(JavaScriptObject error) {
				wsConnection.send(WSMsgsBuilder.endCallMsg(callId));
				Utils.consoleLog("Got error while getting local SDP");
				Utils.consoleDebug(error);
				Call.this.callStateListener.onCallTerminate("Error geting local SDP");
				
			}
		});
	}
	public enum Direction {
		IN,OUT
	}
	
	public interface CallStateListener {
		void onCallTerminate(String cause);
		void onCallStarted();
		void onCallProceding();
		
	}
	
	public void setVideoEnabled(boolean videoEnabled) {
		this.videoEnabled = videoEnabled;
	}
	public boolean isVideoEnabled() {
		return videoEnabled;
	};
}
