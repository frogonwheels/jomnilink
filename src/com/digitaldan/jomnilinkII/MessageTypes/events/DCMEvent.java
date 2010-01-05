package com.digitaldan.jomnilinkII.MessageTypes.events;

import com.digitaldan.jomnilinkII.MessageTypes.events.OtherEvent;

public class DCMEvent extends OtherEvent {

	public DCMEvent(int msg) {
		super(msg);
	}
	
	@Override
	public EventType getEventType() {
		return EventType.DCM;
	}
}
