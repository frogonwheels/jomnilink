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
	public enum LinkCommand { Off(0,"off"), On(1,"on"), Store(2,"store"), FadeStop(3,"fade stop");
		public final int msgNo;
		public final String Name;
		LinkCommand(int msgNo, String msgName){
			this.msgNo = msgNo;
			this.Name  = msgName;
		}
	};
	
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
		LinkCommand val = getCommand();
		if (val == null)
		  return String.format("unknown: %d",rawMessage);
		else	
		  return val.Name;
	}
	public int getLinkNumber() {
		return this.rawMessage & 0x00ff;
	}
	public String toString() {
		return String.format("UPB Link %d: %s", getLinkNumber(), getCommandAsString());
	}
}
