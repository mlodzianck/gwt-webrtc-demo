package me.tokarski.webrtc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;

public class PeopleEntryWidget extends HorizontalPanel {
	private HTML state;
	private Button callBtn; 
	public PeopleEntryWidget(String nickName,String state) {
		setSpacing(3);
		setVerticalAlignment(ALIGN_MIDDLE);
		Image img = new Image("buddy.png");
		img.setSize("15px", "15px");
		add(img);
		add(new HTML("<b>"+nickName+"</b>"));
		this.state = new  HTML(state);
		add(this.state);
		callBtn = new Button("Call");
		add(callBtn);
	}
	public void setState(String state) {
		GWT.log("updating state");
		remove(this.state);
		this.state=new HTML(state);
		insert(this.state, 2);
		
	}
	public Button getCallBtn() {
		return callBtn;
	}
}
