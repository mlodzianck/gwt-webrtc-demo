package me.tokarski.webrtc.client;

import java.util.UUID;

import org.apache.catalina.util.MD5Encoder;

import me.tokarski.webrtc.client.Call.CallStateListener;
import me.tokarski.webrtc.client.Call.Direction;
import me.tokarski.webrtc.client.PeopleWindow.PeopleCallCallback;
import me.tokarski.webrtc.client.wrappers.GetUserMediaUtils;
import me.tokarski.webrtc.client.wrappers.MediaStream;
import me.tokarski.webrtc.client.wrappers.Utils;

import com.allen_sauer.gwt.log.client.DivLogger;
import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.impl.Md5Digest;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.media.client.Audio;
import com.google.gwt.media.client.Video;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.box.PromptMessageBox;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.info.Info;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class WebRTC_GWT implements EntryPoint {
	String wsServerUrl;
	CheckBox autoanswer;
	CheckBox videoEnabled;
	WSConnection connection;
	MediaStream localStream;
	Video localVideoElement;
	HTML whoAmI;
	boolean localVideoInitialized = false;
	boolean sigChannelConnected = false;
	PeopleWindow peopleWindow;
	static final String DEFAULT_WS_URL = "ws://192.168.56.180:8000/iwrtc";
	static final String WS_CONN_ERROR_MSG = "It looks like the websockets server is down.<br><a href=\"mailto:maciek@tokarski.me\">E-mail me</a> if you want to let me know.";

	private void connectToSigServer() {
		connection = new WSConnection(wsServerUrl,
				new WSConnection.WSConnectionStateCallback() {

					@Override
					public void onConnectionOpened() {
						sigChannelConnected = true;
						Info.display("", "Websocket connection opened");
						
						if (Window.Location.getParameter("user")!=null) {
							connection.send(WSMsgsBuilder.registrationMsg(Window.Location.getParameter("user")));
						} else {
							showLoginBox();
						}
						

					}

					@Override
					public void onConnectionClosed() {
						sigChannelConnected = false;
						Info.display("",
								"Websocket connection closed (server error??)");
						showAlertMessageAndReload("Websocket error", "Disconected from signaling server<br>After you click ok page will be reloaded");
					}

					@Override
					public void onCallUnrelatedMessage(JSONObject jso) {
						Utils.consoleLog("onCallUnrelatedMessage");
						Utils.consoleDebug(jso.getJavaScriptObject());
						if (jso.containsKey("command")) {
							String cmd = ((JSONString) jso.get("command"))
									.stringValue();
							if (cmd.equals("register_response")) {
								process_register_response_msg(jso);
							}
							if (cmd.equals("subscriber_state_update")) {
								process_subscriber_state_update(jso);
							}
							if (cmd.equals("call_request")) {
								process_call_request(jso);
							}
						}

					}
				});
	}

	protected void process_call_request(JSONObject jso) {
		if (!localVideoInitialized ||  !sigChannelConnected) {
			Utils.consoleLog("local media or sig channel unitialized");
			return;
		}
		
		
		Audio a = Audio.createIfSupported();
		if (a!=null) {
			a.setSrc("DingLing.wav");
			a.play();
		}
		final String caller = ((JSONString)jso.get("caller")).stringValue();
		final Integer callId = (int) ((JSONNumber) jso.get("call_id")).doubleValue();
		if (autoanswer.getValue()) {
			connection.send(WSMsgsBuilder.confirmCallMsg(callId));
			createInboundCallWindow(caller, callId);
			return;
		}
		
		
		ConfirmMessageBox box = new ConfirmMessageBox("Accept call?", caller+" is calling you, answer?");
        box.addHideHandler(new HideHandler() {
			
			@Override
			public void onHide(HideEvent event) {
				 Dialog btn = (Dialog) event.getSource();
				 String answer = btn.getHideButton().getText();
				 GWT.log("User clicked "+answer);
				 if (answer.toLowerCase().equals("no")) {
						connection.send(WSMsgsBuilder.declineCallMsg(callId));
				 } else {
					createInboundCallWindow(caller, callId);
					connection.send(WSMsgsBuilder.confirmCallMsg(callId));

				 }
			}
		});
        
        box.show();
		

	}
	
	private void createInboundCallWindow(String caller,Integer callId) {
		Video remotVideo = Video.createIfSupported();
		remotVideo.setAutoplay(true);
		remotVideo.setPoster("facebook-no-face.gif");
		VerticalPanel container = new VerticalPanel();
		container.setSize("100%", "100%");
		container.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		remotVideo.setSize("100%", "100%");

		Button hangup = new Button("End call");

		final com.sencha.gxt.widget.core.client.Window callWindow = new com.sencha.gxt.widget.core.client.Window();
		callWindow.setHeadingText("Call from " + caller);
		container.add(remotVideo);
		container.add(hangup);
		callWindow.add(container);
		callWindow.show();
		CallStateListener l = new CallStateListener() {
			@Override
			public void onCallTerminate(String cause) {
				callWindow.hide();
			}
			@Override
			public void onCallStarted() {
			}
			@Override
			public void onCallProceding() {
			}
		};
		final Call call =new Call(Call.Direction.IN, localStream, connection, remotVideo, l, callId);
		call.setVideoEnabled(videoEnabled.getValue());
		hangup.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				call.hangup();
				
			}
		});
	}

	protected void process_subscriber_state_update(JSONObject jso) {
		String status = ((JSONString) jso.get("status")).stringValue();
		String nick = ((JSONString) jso.get("nickname")).stringValue();
		if (status.equals("remove")) {
			peopleWindow.remove(nick);
		} else {
			peopleWindow.updateState(nick, status);
		}

	}

	protected void process_register_response_msg(JSONObject jso) {
		String status = ((JSONString) jso.get("status")).stringValue();
		Utils.consoleLog("Got login response with status " + status);
		if (status.equals("success")) {
			String name = ((JSONString) jso.get("name")).stringValue();
			Info.display("", "Welcome " + name + "!");
			JSONObject people = (JSONObject) jso.get("people");
			showPeopleWindow(people);
			whoAmI=new HTML("<p style=\"color: white;\">Logged as <b>"+name+"</b></p>");
			RootPanel.get().add(whoAmI, 15, 5);
			
		}
		if (status.equals("failed")) {
			Info.display("", "registering under random name");
			String name = Long.toString(System.currentTimeMillis());
			connection.send(WSMsgsBuilder.registrationMsg(name));
		}

	}

	private void showPeopleWindow(JSONObject people) {
		PeopleCallCallback callback = new PeopleCallCallback() {

			@Override
			public void call(String who) {
				doCall(who);

			}
		};
		peopleWindow = new PeopleWindow(people, callback);
		peopleWindow.show();

	}

	protected void doCall(final String who) {
		if (!localVideoInitialized || !sigChannelConnected) {
			Utils.consoleLog("local media or sig channel unitialized");
			return;
		}
		Video remotVideo = Video.createIfSupported();
		remotVideo.setAutoplay(true);
		remotVideo.setPoster("facebook-no-face.gif");
		VerticalPanel container = new VerticalPanel();
		container.setSize("100%", "100%");
		container.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		remotVideo.setSize("100%", "100%");

		Button hangup = new Button("End call");
		

		final com.sencha.gxt.widget.core.client.Window callWindow = new com.sencha.gxt.widget.core.client.Window();
		callWindow.setHeadingText("Call to " + who);
		container.add(remotVideo);
		container.add(hangup);
		callWindow.add(container);
		callWindow.show();
		CallStateListener listener = new CallStateListener() {

			@Override
			public void onCallTerminate(String cause) {
				callWindow.hide();
				Info.display("", "Call to "+who+" terminated, reason "+cause);

			}

			@Override
			public void onCallStarted() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onCallProceding() {
				// TODO Auto-generated method stub

			}
		};
		final Call call = new Call(Direction.OUT, localStream, connection,
				remotVideo, listener, who);
		call.setVideoEnabled(videoEnabled.getValue());
		hangup.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				call.hangup();
				
			}
		});
	}

	private void showLocalVideoWnd() {
		com.sencha.gxt.widget.core.client.Window localVideoWindow = new com.sencha.gxt.widget.core.client.Window();
		localVideoWindow.setPixelSize(320, 190);
		localVideoWindow.setModal(false);
		localVideoWindow.setHeadingText("Local video preview");
		localVideoWindow.add(localVideoElement);
		localVideoWindow.setClosable(false);
		localVideoWindow.show();
		localVideoElement.setSize("100%", "100%");
		localVideoWindow.setPagePosition(0, Window.getClientHeight() - 190);
	}

	@Override
	public void onModuleLoad() {
		
		
		autoanswer = new CheckBox("Auto-answer incoming calls?");
		autoanswer.setValue(false);
		autoanswer.addStyleName("autoanswer-checkbox");
		
		RootPanel.get().add(autoanswer, 15, 50);
		
		
		videoEnabled = new CheckBox("Video enabled");
		videoEnabled.setValue(false);
		videoEnabled.addStyleName("autoanswer-checkbox");
		RootPanel.get().add(videoEnabled, 15, 80);
		
		
		Button getUserMediaBtn = new Button("getUserMedia");
		getUserMediaBtn.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				getUserMedia();
				
			}
		});
		RootPanel.get().add(getUserMediaBtn, 15, 110);
		
		Button callTest123W = new Button("Call test123");
		callTest123W.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				doCall("test123");
				
			}
		});
		RootPanel.get().add(callTest123W, 15, 140);
		
		
		
		Resources.INSTANCE.css().ensureInjected(); 
		localVideoElement = Video.createIfSupported();
		if (localVideoElement == null) {
			Window.alert("Your browser does not support getUserMedia()");
			return;
		}
		localVideoElement.setPoster("facebook-no-face.gif");
		localVideoElement.setAutoplay(true);
		


		wsServerUrl = Window.Location.getParameter("sigUrl");
		if (wsServerUrl==null) {
			wsServerUrl = DEFAULT_WS_URL;
		}
		Info.display("", "Connecting to sig server @ url : "+wsServerUrl);
		WSServerCheck.checkServerAlive(wsServerUrl,
				new WSServerCheck.CheckCallback() {
					@Override
					public void serverDead() {
						showAlertMessageAndReload("Websocket server error",
								WS_CONN_ERROR_MSG);
					}

					@Override
					public void serverAlive() {
						connectToSigServer();
						Info.display("",
								"Websockets server is alive, connecting");
					}
				});

	}

	public void getUserMedia() {
		Utils.consoleLog("Getting user maedia, video="+videoEnabled.getValue());
		GetUserMediaUtils.getUserMedia(true, videoEnabled.getValue(),
				new GetUserMediaUtils.GetUserMedaCallback() {

					@Override
					public void navigatorUserMediaSuccessCallback(
							MediaStream mediaStream) {
						localVideoElement.addSource(mediaStream
								.createMediaObjectBlobUrl());
						localStream = mediaStream;
						localVideoInitialized = true;
						showLocalVideoWnd();

					}

					@Override
					public void navigatorUserMediaErrorCallback(
							JavaScriptObject error) {
						AlertMessageBox alertBox = new AlertMessageBox(
								"Camera error",
								"Please provide access to your cam and mic");
						alertBox.addHideHandler(new HideHandler() {
							@Override
							public void onHide(HideEvent event) {
								getUserMedia();
							}
						});
						alertBox.show();
						Utils.consoleDebug(error);

					}
				});
	}

	private void showAlertMessageAndReload(String title, String message) {
		AlertMessageBox alertBox = new AlertMessageBox(title, message);
		alertBox.addHideHandler(new HideHandler() {

			@Override
			public void onHide(HideEvent event) {
				Window.Location.reload();

			}
		});
		alertBox.show();
	}

	private void showLoginBox() {
		final PromptMessageBox box = new PromptMessageBox("Name",
				"Please enter your name:");
		box.addHideHandler(new HideHandler() {
			@Override
			public void onHide(HideEvent event) {
				if (box.getValue() == null) {
					showLoginBox();
					return;
				}
				connection.send(WSMsgsBuilder.registrationMsg(box.getValue()));

			}
		});
		box.show();
	}

}