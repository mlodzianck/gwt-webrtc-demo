package me.tokarski.webrtc.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.media.client.Audio;
import com.google.gwt.user.client.ui.CheckBox;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VBoxLayoutContainer.VBoxLayoutAlign;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;

public class PeopleWindow extends Window {
	private Map<String, PeopleEntryWidget> peopleMap = new HashMap<String, PeopleEntryWidget>();
	private VerticalLayoutContainer container = new VerticalLayoutContainer();
	private CheckBox mute;
	private PeopleCallCallback peopleCallCallback;
	public PeopleWindow(JSONObject people, final PeopleCallCallback callback) {
		mute = new CheckBox("Mute availability sound?");
		container.add(mute);
		this.peopleCallCallback=callback;
		
		setPixelSize(300, 300);
		setHeadingText("Avialable people");
		setClosable(false);
		
		container.setScrollMode(ScrollMode.AUTOY);
		Set<String> nicks = people.keySet();
		for (final String nick : nicks) {
			String state = ((JSONString) people.get(nick)).stringValue();
			PeopleEntryWidget p = new PeopleEntryWidget(nick, state);
			p.getCallBtn().addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					peopleCallCallback.call(nick);

				}
			});
			container.add(p);
			peopleMap.put(nick, p);
		}
		add(container);
		setPagePosition(
				com.google.gwt.user.client.Window.getClientWidth() - 310,
				com.google.gwt.user.client.Window.getClientHeight() - 300);
	}

	public void remove(String nick) {
		if (peopleMap.containsKey(nick)) {
			container.remove(peopleMap.get(nick));
			peopleMap.remove(nick);
			remove(container);
			add(container);
			play("SONAR_low.mp3");
		}
	}

	public void updateState(final String nick, String state) {
		if (peopleMap.containsKey(nick)) {
			PeopleEntryWidget w = peopleMap.get(nick);
			w.setState(state);
		} else {
			play("SONAR.mp3");
			PeopleEntryWidget p = new PeopleEntryWidget(nick, state);
			container.add(p);
			remove(container);
			add(container);
			peopleMap.put(nick, p);
			p.getCallBtn().addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					peopleCallCallback.call(nick);

				}
			});
		}
	}

	public interface PeopleCallCallback {
		void call(String who);
	}
	
	private void play(String soundName) {
		if (!mute.getValue()) {
			Audio a = Audio.createIfSupported();
			if (a!=null) {
				a.setSrc(soundName);
				a.play();
			}
		}
	}
}
