package com.digitaldan.jomnilinkII.MessageTypes.events;

import com.digitaldan.jomnilinkII.MessageTypes.events.OtherEvent;

public class ProlinkMessageEvent extends OtherEvent {

	public ProlinkMessageEvent(int msg) {
		super(msg);
	}
	
	@Override
	public EventType getEventType() {
		return EventType.ProlinkMessage;
	}
	
}
