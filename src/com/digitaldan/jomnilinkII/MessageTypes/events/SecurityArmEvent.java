package com.digitaldan.jomnilinkII.MessageTypes.events;

import com.digitaldan.jomnilinkII.MessageTypes.events.OtherEvent;

public class SecurityArmEvent extends OtherEvent {

	public SecurityArmEvent(int msg) {
		super(msg);
	}
	@Override
	public EventType getEventType() {
		return EventType.SecurityArming;
	}
	public enum DelayState { EndOfDelay, StartOfDelay};
	public enum SecurityMode { Off, Day, Night, Away, Vacation, DayInstant, NightDelayed };
	
	public static String SecurityModeAsString( SecurityMode secMode) {
		switch (secMode) {
		case Off: return "Off";
		case Day: return "Day";
		case Night: return "Night";
		case Away:  return "Away";
		case Vacation: return "Vacation";
		case DayInstant: return "DayInstant";
		case NightDelayed: return "NightDelayed";
		default: return "Unknown";
		}
	}
	
	public DelayState getDelayState() {
		return ((rawMessage & 0x8000)==0x8000)?DelayState.StartOfDelay:DelayState.EndOfDelay; 
	}
	
	public SecurityMode getSecurityMode() {
		switch ((rawMessage &0x7000)>>12) {
		case 0: return SecurityMode.Off;
		case 1: return SecurityMode.Day;
		case 2: return SecurityMode.Night;
		case 3: return SecurityMode.Away;
		case 4: return SecurityMode.Vacation;
		case 5: return SecurityMode.DayInstant;
		case 6: return SecurityMode.NightDelayed;
		default: return null;
		}
	}
	public String getSecurityModeAsString() {
		return SecurityModeAsString(getSecurityMode());
	}
	public int getArea() {
		return (rawMessage &0xf00)>> 8;
	}
	@Override
	public String toString() {
		return String.format(
			"Security Armed '%s' at %s of delay in Area=%d", 
			getSecurityModeAsString(),
			(getDelayState()==DelayState.StartOfDelay)?"start":"end", 
			getArea()); 
	}
	
	
}
