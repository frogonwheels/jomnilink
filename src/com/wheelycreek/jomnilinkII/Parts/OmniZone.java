/** Represent a Zone in the omni controller
 */
/*  Copyright (C) 2010 Michael Geddes
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.wheelycreek.jomnilinkII.Parts;

import com.digitaldan.jomnilinkII.MessageTypes.properties.ZoneProperties;
import com.digitaldan.jomnilinkII.MessageTypes.statuses.ZoneStatus;
import com.wheelycreek.jomnilinkII.OmniNotifyListener;
import com.wheelycreek.jomnilinkII.OmniPart;
import com.wheelycreek.jomnilinkII.OmniSystem.OmniArea;

/** Alarm Zone or System Input.
 * Zones represent many of the methods of direct inputs.  Many of these will correspond to 
 * security triggers.
 * @author michaelg
 */
public class OmniZone extends OmniPart {
	public enum ChangeType { ZoneType, Area, Options, Status, LatchAlarm, Alarm, Loop };
	public enum SecureStatus { Secure, NotReady, Trouble};
	public enum LatchAlarmStatus { AlarmSecure, AlarmTripped, AlarmReset };
	
	public enum AlarmStatus { 
		Disarmed("disarmed"), Armed("armed"), 
		BypassUser("bypass_user"), BypassSystem("bypass_system");
		private final String xmlId;
		private AlarmStatus(String xmlId) {
			this.xmlId = xmlId;
		}
		public String getXMLId() {
			return xmlId;
		} 
	};

	public enum ZoneType {
		EntryExit(0, "entryexit", "Entry/Exit"),
		Perimeter(1, "perim", "Perimeter"),
		NightInt(2,  "int_night", "Night Interior"),
		AwayInt(3, "int_away",  "Away Interior"),
		DoubEntryDelay(4, "entry_2x", "Double Entry Delay"),
		QuadEntryDelay(5, "entry_4x", "Quadruple Entry Delay"),
		LatchPerimeter(6, "perim_latch", "Latching Perimeter"),
		LatchNightInt(7,  "int_night_latch", "Latching Night Interior"),
		LatchAwayInt(8,   "int_away_latch",  "Latching Away Interior"),
		Panic(16, "panic", "Panic"),
		PoliceEm(17, "emerg_police", "Police Emergency"),
		Duress(18, "duress", "Duress"),
		Tamper(19, "tamper", "Tamper"),
		LatchTamper(20, "tamper_latch", "Latching Tamper"),
		Fire(32, "fire", "Fire"),
		FireEm(33, "emerg_fire", "Fire Emergency"),
		GasAlarm(34,"gas", "Gas Alarm"),
		AuxEm(48, "emerg_aux", "Auxiliary Emergency"),
		Trouble(49,"trouble", "Trouble"),
		Freeze(54, "freeze", "Freeze"),
		Water(55, "water", "Water"),
		FireTamper(56, "tamper_fire", "Fire Tamper"),
		Auxiliary(64, "aux", "Auxiliary"),
		KeyswitchInput(65, "keyswitch", "Keyswitch Input"),
		ProgEnergySaver(80, "energysaver", "Programmable Energy Saver Module"),
		OutdoorTemp(81, "temp_ext", "Outdoor Temperature"),
		Temp(82, "temp_int", "Temperature"),
		TempAlarm(83, "temp_alarm", "Temperature Alarm"),
		Humidity(84, "humidity", "Humidity"),
		ExtOutdoorTemp(85, "temp_ext_ex", "Extended Range Outdoor Temperature"),
		ExtTemp(86, "temp_int_ex", "Extended Range Temperature"),
		ExtTempAlarm(87, "temp_alarm_ex", "Extended Range Temperature Alarm");

		private final int rawType;
		private final String desc;
		private final String xmlId;
		
		public int getRawType() { return rawType;}
		public String getDesc() { return  desc; }
		private ZoneType( int rawType, String xmlId, String desc) {
			this.rawType = rawType;
			this.desc = desc;
			this.xmlId = xmlId;
		}

		public static ZoneType typeAsEnum( int rawType ) {
			switch (rawType) {
				case 0: return EntryExit;
				case 1: return Perimeter;
				case 2: return NightInt;
				case 3: return AwayInt;
				case 4: return DoubEntryDelay;
				case 5: return QuadEntryDelay;
				case 6: return LatchPerimeter;
				case 7: return LatchNightInt;
				case 8: return LatchAwayInt;
				case 16: return Panic;
				case 17: return PoliceEm;
				case 18: return Duress;
				case 19: return Tamper;
				case 20: return LatchTamper;
				case 32: return Fire;
				case 33: return FireEm;
				case 34: return GasAlarm;
				case 48: return AuxEm;
				case 49: return Trouble;
				case 54: return Freeze;
				case 55: return Water;
				case 56: return FireTamper;
				case 64: return Auxiliary;
				case 65: return KeyswitchInput;
				case 80: return ProgEnergySaver;
				case 81: return OutdoorTemp;
				case 82: return Temp;
				case 83: return TempAlarm;
				case 84: return Humidity;
				case 85: return ExtOutdoorTemp;
				case 86: return ExtTemp;
				case 87: return ExtTempAlarm;
				default: return null;
			}
		}
		public String getXMLId() {
			return xmlId;
		}
	}
	
	/** Message sent when OmniZone changes.
	 * @author michaelg
	 */
	public class ZoneChangeMessage extends OmniNotifyListener.ChangeMessage {
		public ChangeType change_type;

		public ZoneChangeMessage(int number, ChangeType changeType, OmniNotifyListener.NotifyType notifyType) {
			super(OmniArea.Zone, number, notifyType);
			change_type = changeType;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return String.format(
					"ZoneChangeMessage [area=%s, number=%s, notifyType=%s, change_type=%s, zone_area=%s]",
					area, number, notifyType, change_type, zone_area);
		}
		public ChangeType getChangeType() { return change_type;}
	}
	protected OmniNotifyListener.ChangeMessage createChangeMessage( ChangeType changetype, OmniNotifyListener.NotifyType notifyType) {
		ZoneChangeMessage msg = new ZoneChangeMessage(number,changetype, notifyType );
		return msg;
	}
	
	private SecureStatus zone_status;
	private LatchAlarmStatus latch_alarm_status;
	private AlarmStatus alarm_status;
	private ZoneType zone_type;
	private int loop;
	private int options;
	private int zone_area;

	/** Construct a zone for the specified zone number
	 */
	public OmniZone( int number) {
		super(number, OmniArea.Zone);
	}
	
	/** The zone type.
	  */
	public ZoneType getZoneType() {
		return zone_type;
	}

	/** Set the zone type.
	  */
	public void setZoneType( ZoneType zoneType ) {
		updateZoneType(zoneType, OmniNotifyListener.NotifyType.ChangeRequest);
	}

	/** Set the zone type.
	  */
	public void updateZoneType( ZoneType zoneType, OmniNotifyListener.NotifyType notifyType ) {
		if (zoneType != this.zone_type) {
			this.zone_type = zoneType;
			notify(createChangeMessage(ChangeType.ZoneType, notifyType));
		}
	}
	/** The zone area.
	  */
	public int getArea() {
		return zone_area;
	}
	/** set the zone area.
	  */
	public void setZoneArea( int zone_area ) {
		updateZoneArea(zone_area,  OmniNotifyListener.NotifyType.ChangeRequest );
	}

	/** Set the zone area.
	  */
	public void updateZoneArea( int zone_area, OmniNotifyListener.NotifyType notifyType ) {
		if (zone_area != this.zone_area) {
			this.zone_area = zone_area;
			notify(createChangeMessage(ChangeType.Area, notifyType));
		}
	}

	/** Set from a raw omni status
	 * @param rawstatus The raw omni status.
	 */
	public void updateRawStatus( int rawstatus, OmniNotifyListener.NotifyType notifyType) {
		switch (rawstatus & 0x3) {
		case 0: updateZoneStatus(SecureStatus.Secure, notifyType); break;
		case 1: updateZoneStatus(SecureStatus.NotReady, notifyType); break;
		case 2: updateZoneStatus(SecureStatus.Trouble, notifyType); break;
		default: updateZoneStatus(null, notifyType);
		}
		switch ((rawstatus >>2) & 0x3) {
		case 0: updateLatchAlarmStatus(LatchAlarmStatus.AlarmSecure, notifyType); break;
		case 1: updateLatchAlarmStatus(LatchAlarmStatus.AlarmTripped, notifyType);break; 
		case 2: updateLatchAlarmStatus(LatchAlarmStatus.AlarmReset, notifyType);  break;
		default: updateLatchAlarmStatus(null, notifyType);
		}
		switch ((rawstatus >>4) & 0x3) {
		case 0: updateAlarmStatus(AlarmStatus.Disarmed, notifyType);    break;
		case 1: updateAlarmStatus(AlarmStatus.Armed, notifyType);       break;
		case 2: updateAlarmStatus(AlarmStatus.BypassUser, notifyType);  break;
		case 3: updateAlarmStatus(AlarmStatus.BypassSystem, notifyType);break;
		default: updateAlarmStatus(null, notifyType);
		}
	}
	
	/* Generate a raw omni status
	 * @return Status for ZoneStatus class
	 */
	/*public int getRawStatus() {
		int result = 0;
		switch (zone_status) {
		case Secure:   result = 0x0; break;
		case NotReady: result = 0x1; break;
		case Trouble:  result = 0x2; break;
		}
		switch (latch_alarm_status) {
		case AlarmSecure: break;
		case AlarmTripped: result |= 0x4; break; 
		case AlarmReset:   result |= 0x8; break;
		}
		switch (alarm_status) {
		case Disarmed: break; 
		case Armed:         result |= 0x10; break;
		case BypassUser:    result |= 0x20; break;
		case BypassSystem:  result |= 0x30; break;
		}
		return result;
	}*/
	/** The current status of the zone. (NotReady means the sensor has been tripped)
	 * @return the zone_status
	 */
	public SecureStatus getZoneStatus() {
		return zone_status;
	}
	/**
	 * @param zoneStatus the zone_status to set
	 */
	protected void updateZoneStatus(SecureStatus zoneStatus, OmniNotifyListener.NotifyType notifyType) {
		if (zone_status != zoneStatus)  {
			zone_status = zoneStatus;
			this.notify(createChangeMessage(ChangeType.Status, notifyType));
		}
	}
	/** The Latched alarm status for the zone.
	 * @return the latch_alarm_status
	 */
	public LatchAlarmStatus getLatchAlarmStatus() {
		return latch_alarm_status;
	}
	/** Set The latched alarm status for the zone
	 * @param latchAlarmStatus the latch_alarm_status to set
	 */
	protected void updateLatchAlarmStatus(LatchAlarmStatus latchAlarmStatus, OmniNotifyListener.NotifyType notifyType) {
		if (latch_alarm_status != latchAlarmStatus) {
			latch_alarm_status = latchAlarmStatus;
			notify(createChangeMessage(ChangeType.LatchAlarm, notifyType));
		}
	}
	/** The current status of the alarm 
	 * @return the alarm_status
	 */
	public AlarmStatus getAlarmStatus() {
		return alarm_status;
	}
	/** Update the alaram status
	 * @param alarmStatus the alarm_status to set
	 */
	protected void updateAlarmStatus(AlarmStatus alarmStatus, OmniNotifyListener.NotifyType notifyType) {
		if (alarmStatus != alarm_status) {
			alarm_status = alarmStatus;
			notify( createChangeMessage(ChangeType.Alarm, notifyType));
		}
	}
	/** The current 'loop' value.
	  */
	public int getLoopValue(){
		return loop;
	}
	/** Update the loop value.
	  */
	public void updateLoopValue( int loop, OmniNotifyListener.NotifyType notifyType) {
		if (loop != this.loop) {
			this.loop = loop;
			notify(createChangeMessage(ChangeType.Loop, notifyType));
		}
	}

	/** Is cross zoning allowed.
	  */
	public boolean isCrossZoning() {
		return (options & 0x1) == 0x1;
	}
	public boolean isSwingerShutdown() {
		return (options & 0x2) == 0x2;
	}
	public boolean isDialOutDelay() {
		return (options & 0x4) == 0x4;
	}
	public void setCrossZoning( boolean flag) {
		if (flag)
			updateRawOptions(options | 0x1, OmniNotifyListener.NotifyType.ChangeRequest);
		else
			updateRawOptions(options & ~0x1, OmniNotifyListener.NotifyType.ChangeRequest);
	}
	public void setSwingerShutdown( boolean flag) {
		if (flag)
			updateRawOptions(options | 0x2, OmniNotifyListener.NotifyType.ChangeRequest);
		else
			updateRawOptions(options & ~0x2, OmniNotifyListener.NotifyType.ChangeRequest);
	}
	public void setDialOutDelay( boolean flag) {
		if (flag)
			updateRawOptions(options | 0x4, OmniNotifyListener.NotifyType.ChangeRequest);
		else
			updateRawOptions(options & ~0x4, OmniNotifyListener.NotifyType.ChangeRequest);
	}
	public int getRawOptions() {
		return options;
	}
	public void updateRawOptions( int options, OmniNotifyListener.NotifyType notifyType ) {
		if (options != this.options) {
			options = this.options;
			notify(createChangeMessage(ChangeType.Options, notifyType));
		}
	}

	public void update(ZoneProperties prop,OmniNotifyListener.NotifyType notifyType) {
	
		this.updateName(prop.getName(), notifyType);
		this.updateZoneType(ZoneType.typeAsEnum(prop.getZoneType()), notifyType);
		this.updateRawOptions(prop.getOptions(), notifyType);
		this.updateZoneArea(prop.getArea(), notifyType);
		this.updateLoopValue(prop.getLoop(), notifyType);
		this.updateRawStatus(prop.getStatus(), notifyType);
		
	}

	public void update(ZoneStatus status, OmniNotifyListener.NotifyType notifyType) {
		this.updateRawStatus(status.getStatus(), notifyType);
	}
}
// vim: syntax=java.doxygen ts=4 sw=4 noet
