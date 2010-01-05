package com.digitaldan.jomnilinkII.MessageTypes.events;

import com.digitaldan.jomnilinkII.MessageTypes.events.OtherEvent;


public class PowerEvent extends OtherEvent {

	public PowerEvent(int msg) {
		super(msg);
	}
	@Override
	public EventType getEventType() {
		return EventType.Power;
	}
	public enum PowerAction {PowerOff, PowerRestored, BatteryLow, BatteryOK};
	public static String PowerActionAsString( PowerAction action){
		switch (action) {
		case PowerOff:      return "Power Off";
		case PowerRestored: return "Power Restored";
		case BatteryLow:    return "Battery Low";
		case BatteryOK:     return "Battery OK";
		default: return "Unknown";
		}
	}
	public PowerAction getAction() {
		switch (rawMessage & 0x0007) {
		case 4: return PowerAction.PowerOff;
		case 5: return PowerAction.PowerRestored;
		case 6: return PowerAction.BatteryLow;
		case 7: return PowerAction.BatteryOK;
		default: return null;			
		}
	}
	@Override
	public String toString() {
		return PowerActionAsString(getAction());
	}
}
