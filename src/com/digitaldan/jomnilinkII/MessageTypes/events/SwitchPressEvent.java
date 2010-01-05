package com.digitaldan.jomnilinkII.MessageTypes.events;

import com.digitaldan.jomnilinkII.MessageTypes.events.OtherEvent;

public class SwitchPressEvent extends OtherEvent {

	public SwitchPressEvent(int msg) {
		super(msg);
	}
	
	@Override
	public EventType getEventType() {
		return EventType.UnitSwitchPress;
	}
}
