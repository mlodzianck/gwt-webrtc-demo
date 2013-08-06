package me.tokarski.webrtc.client.wrappers;

import com.google.gwt.core.client.JavaScriptObject;

public class SDPOfferMediaConstraints {
	private boolean mandadoryOfferToReceiveAudio;
	private boolean mandadoryOfferToReceiveVideo;
	
	
	
	
	
	public SDPOfferMediaConstraints(boolean mandadoryOfferToReceiveAudio,
			boolean mandadoryOfferToReceiveVideo) {
		super();
		this.mandadoryOfferToReceiveAudio = mandadoryOfferToReceiveAudio;
		this.mandadoryOfferToReceiveVideo = mandadoryOfferToReceiveVideo;
	}





	public native JavaScriptObject asJavaScriptObject() /*-{
		var instance = this;
		return {
			'mandatory' : {
				'OfferToReceiveAudio' : instance.@me.tokarski.webrtc.client.wrappers.SDPOfferMediaConstraints::mandadoryOfferToReceiveAudio,
				'OfferToReceiveVideo' : instance.@me.tokarski.webrtc.client.wrappers.SDPOfferMediaConstraints::mandadoryOfferToReceiveVideo,
			}
		}
	}-*/;
}
