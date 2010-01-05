package com.digitaldan.jomnilinkII.MessageTypes.events;

import com.digitaldan.jomnilinkII.MessageTypes.events.OtherEvent;

public class UPBLinkEvent extends OtherEvent {

	public UPBLinkEvent(int msg) {
		super(msg);
		// TODO Auto-generated constructor stub
	}
	public EventType getEventType() {
		return EventType.UPBLink;
	}
	public enum LinkCommand { Off, On, Store, FadeStop};
	
	public LinkCommand getCommand() {
		switch ((this.rawMessage & 0x0300) >> 8) {
		case 0: return LinkCommand.Off;
		case 1: return LinkCommand.On;
		case 2: return LinkCommand.Store;
		case 3: return LinkCommand.FadeStop;
		default: return null;
		}
	}
	public String getCommandAsString() {
		switch (getCommand()) {
		case Off: return "off";
		case On: return "on";
		case Store: return "store";
		case FadeStop: return "fade stop";
		default: return String.format("unknown: %d",rawMessage);
		}
	}
	public int getLinkNumber() {
		return this.rawMessage & 0x00ff;
	}
	public String toString() {
		return String.format("UPB Link %d: %s", getLinkNumber(), getCommandAsString());
	}
}
