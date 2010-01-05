package com.digitaldan.jomnilinkII.MessageTypes.events;

import com.digitaldan.jomnilinkII.MessageTypes.events.OtherEvent;

public class UserMacroButtonEvent extends OtherEvent {

	public UserMacroButtonEvent(int msg) {
		super(msg);
	}
	public EventType getEventType() {
		return EventType.UserMacroButton;
	}
	public int getButtonNumber() {
		return rawMessage & 0xff;
	}
	@Override
	public String toString() {
		return String.format("User Macro Button %d",getButtonNumber());
	}
	
}
