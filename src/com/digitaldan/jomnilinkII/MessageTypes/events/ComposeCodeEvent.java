package com.digitaldan.jomnilinkII.MessageTypes.events;

import com.digitaldan.jomnilinkII.MessageTypes.events.OtherEvent;

public class ComposeCodeEvent extends OtherEvent {

	public ComposeCodeEvent(int msg) {
		super(msg);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public EventType getEventType() {
		return EventType.ComposeCode;
	}

}
