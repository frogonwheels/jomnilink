package com.digitaldan.jomnilinkII.MessageTypes.events;

import com.digitaldan.jomnilinkII.MessageUtils;

public class OtherEvent {
	public enum EventType { UserMacroButton, ProlinkMessage, CentraliteSwitch, Alarm, ComposeCode, X10Code, SecurityArming, LumniaModeChange,
		UnitSwitchPress, UPBLink, AllSwitch, PhoneLine, Power, DCM, EnergyCost
	}
	protected int rawMessage;
	
	public OtherEvent( int msg) {
		super();
		this.rawMessage = msg;
	}
	
	public EventType getEventType() {
		return null; // Override in derived classes.
	}
	
	public int getRawMessage()  {
		return rawMessage;
	}
	
	public String toString() {
		return MessageUtils.getBits(rawMessage);
	}
}
