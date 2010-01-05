package com.digitaldan.jomnilinkII.MessageTypes.events;

import com.digitaldan.jomnilinkII.MessageTypes.events.OtherEvent;

public class X10CodeEvent extends OtherEvent {

	public X10CodeEvent(int msg) {
		super(msg);
	}
	
	@Override
	public EventType getEventType() {
		return EventType.X10Code;
	}
}
