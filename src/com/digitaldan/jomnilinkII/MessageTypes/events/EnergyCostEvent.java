package com.digitaldan.jomnilinkII.MessageTypes.events;

import com.digitaldan.jomnilinkII.MessageTypes.events.OtherEvent;

public class EnergyCostEvent extends OtherEvent {

	public EnergyCostEvent(int msg) {
		super(msg);
	}
	
	@Override
	public EventType getEventType() {
		return EventType.EnergyCost;
	}
}
