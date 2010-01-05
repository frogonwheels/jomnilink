package com.digitaldan.jomnilinkII.MessageTypes.events;

import com.digitaldan.jomnilinkII.MessageTypes.events.OtherEvent;

public class AllSwitchEvent extends OtherEvent {

	public AllSwitchEvent(int msg) {
		super(msg);
	}
	@Override
	public EventType getEventType() {
		return EventType.Alarm;
	}
}
