package com.digitaldan.jomnilinkII.MessageTypes.events;

import com.digitaldan.jomnilinkII.MessageTypes.events.OtherEvent;

public class CentraliteSwitchEvent extends OtherEvent {

	public CentraliteSwitchEvent(int msg) {
		super(msg);
	}
	@Override
	public EventType getEventType() {
		return EventType.CentraliteSwitch;
	}
}
