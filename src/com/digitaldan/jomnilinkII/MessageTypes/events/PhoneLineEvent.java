package com.digitaldan.jomnilinkII.MessageTypes.events;

import com.digitaldan.jomnilinkII.MessageTypes.events.OtherEvent;


public class PhoneLineEvent extends OtherEvent {

	public PhoneLineEvent(int msg) {
		super(msg);
	}
	@Override
	public EventType getEventType() {
		return EventType.PhoneLine;
	}
	public enum RingAction { PhoneDead, PhoneRing, PhoneOffHook, PhoneOnHook };
	public static String RingActionAsString(RingAction action) {
		switch( action ) {
		case PhoneDead: return "line dead";
		case PhoneRing: return "line ringing";
		case PhoneOffHook: return "off hook";
		case PhoneOnHook:  return "on hook";
		default: return "Unknown";
		}
	}
	public RingAction getAction() {
		switch (rawMessage & 0x000f) {
		case 0: return RingAction.PhoneDead;
		case 1: return RingAction.PhoneRing;
		case 2: return RingAction.PhoneOffHook;
		case 3: return RingAction.PhoneOnHook;
		default: return null;
		}
	}
	@Override
	public String toString() {
		return "Phone: "+RingActionAsString(getAction());
	}
}	
