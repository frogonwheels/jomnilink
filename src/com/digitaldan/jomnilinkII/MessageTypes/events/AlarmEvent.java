package com.digitaldan.jomnilinkII.MessageTypes.events;

import com.digitaldan.jomnilinkII.MessageTypes.events.OtherEvent;

public class AlarmEvent extends OtherEvent {

	public AlarmEvent(int msg) {
		super(msg);
	}
	public EventType getEventType() {
		return EventType.Alarm;
	}
	public enum AlarmType { Burglary, Fire, Gas, Auxiliary, Freeze, Water, Duress, Temperature};
	public static String AlarmTypeAsString( AlarmType alrm ) {
		switch (alrm) {
		case Burglary:    return "Burglary";
		case Fire:        return "Fire";
		case Gas:         return "Gas";
		case Auxiliary:   return "Auxiliary";
		case Freeze:      return "Freeze";
		case Water:       return "Water";
		case Duress:      return "Duress";
		case Temperature: return "Temperature";
		default: return "Unknown";
		}
	}
	public AlarmType getAlarmType() {
		switch ( (rawMessage & 0x00f0>> 4)) {
		case 1: return AlarmType.Burglary;
		case 2: return AlarmType.Fire;
		case 3: return AlarmType.Gas;
		case 4: return AlarmType.Auxiliary;
		case 5: return AlarmType.Freeze;
		case 6: return AlarmType.Water;
		case 7: return AlarmType.Duress;
		case 8: return AlarmType.Temperature;
		default: return null;
		}
	}
	
	public int getAlarmArea() {
		return rawMessage & 0x000f;
	}
	
	public String getAlarmTypeAsString() {
		return AlarmTypeAsString(getAlarmType());
	}
		
	public String toString() {
		return String.format("%s Alarm in area %d", getAlarmTypeAsString(), getAlarmArea());
	}
}
