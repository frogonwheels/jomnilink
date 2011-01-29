/** A part of an omni controller that fires events.
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

import java.util.Date;

import com.digitaldan.jomnilinkII.MessageTypes.CommandMessage;
import com.digitaldan.jomnilinkII.MessageTypes.CommandMessage.TimeUnit;
import com.digitaldan.jomnilinkII.MessageTypes.properties.UnitProperties;
import com.digitaldan.jomnilinkII.MessageTypes.statuses.UnitStatus;
import com.wheelycreek.jomnilinkII.OmniNotifyListener;
import com.wheelycreek.jomnilinkII.OmniPart;
import com.wheelycreek.jomnilinkII.OmniNotifyListener.NotifyType;
import com.wheelycreek.jomnilinkII.OmniSystem.OmniArea;

/** Represents a 'Unit' in the omni controller.  This includes flags and units from external (eg UPB) devices.
 * @author michaelg
 */
public class OmniUnit extends OmniPart {
	/** Types of Unit changes.
	 */
	public enum ChangeType { UnitType, RawState, Status, Scene};

	/** Type of Omni units.
	*/
	public enum UnitType {
		Standard(UnitProperties.UNIT_PROP_Standard, "Standard"),
		Extended(UnitProperties.UNIT_PROP_Extended, "Extended"),
		Compose(UnitProperties.UNIT_PROP_Compose, "Compose"),
		UPB(UnitProperties.UNIT_PROP_UPB, "UPB"),
		HLCRoom(UnitProperties.UNIT_PROP_HLCRoom, "HLC Room"),
		HLCLoad(UnitProperties.UNIT_PROP_HLCLoad, "HLC Load"),
		LuminaMode(UnitProperties.UNIT_PROP_LuminaMode, "Lumina Mode"),
		RadioRA(UnitProperties.UNIT_PROP_RadioRA, "Radio RA"),
		CentraLite(UnitProperties.UNIT_PROP_CentraLite, "Centra Lite"),
		ViziaRFRoom(UnitProperties.UNIT_PROP_ViziaRFRoom, "Vizia RF Room"),
		ViziaRFLoad(UnitProperties.UNIT_PROP_ViziaRFLoad, "Vizia RF Load"),
		Flag(UnitProperties.UNIT_PROP_Flag, "Flag"),
		Output(UnitProperties.UNIT_PROP_Output, "Output"),
		AudioZone(UnitProperties.UNIT_PROP_AudioZone, "Audio Zone"),
		AudioSource(UnitProperties.UNIT_PROP_AudioSource, "Audio Source");
		public int rawType;
		public String desc;
		private UnitType( int rawType, String descType ) {
			this.rawType = rawType;
			this.desc = descType;
		}
		
		public static UnitType typeAsEnum( int type) {
			switch (type) {
			case 1: return UnitType.Standard;
			case 2: return UnitType.Extended;
			case 3: return UnitType.Compose;
			case 4: return UnitType.UPB;
			case 5: return UnitType.HLCRoom;
			case 6: return UnitType.HLCLoad;
			case 7: return UnitType.LuminaMode;
			case 8: return UnitType.RadioRA;
			case 9: return UnitType.CentraLite;
			case 10: return UnitType.ViziaRFRoom;
			case 11: return UnitType.ViziaRFLoad;
			case 12: return UnitType.Flag;
			case 13: return UnitType.Output;
			case 14: return UnitType.AudioZone;
			case 15: return UnitType.AudioSource;
			default: return null;
			}		
		}
	};
	
	public enum  UnitVariant { Unit, Device, Room, Output, Flag};

	
	/** Message sent when OmniZone changes.
	 */
	public class UnitChangeMessage extends OmniNotifyListener.ChangeMessage {
		public UnitVariant variantType;
		public ChangeType changeType;

		public UnitChangeMessage(OmniArea area, UnitVariant unitvar, int number, OmniNotifyListener.NotifyType notifyType,
				ChangeType changeType) {
			super(area, number, notifyType);
			this.changeType = changeType;
			this.variantType = unitvar;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return String.format(
				"UnitChangeMessage [area=%s, variant=%s, number=%s, notifyType=%s, change_type=%s]",
				area, variantType, number, notifyType, changeType);
		}
		
	}
	/** The derived unit class variant.
	  */
	final protected UnitVariant unit_variant;
	/** The derived unit class variant.
	  */
	protected UnitType unit_type;
	/** There's an operation pending. 
	 *  Causes events to fire when changes come back from omni..
	 */
	protected boolean op_pending;
	/** Raw value of 'status' from controller.
	 */
	protected int raw_status;
	/** Time remaining in seconds from when command was received.
	 */
	protected int time_remain_sec;
	protected java.util.Date when_set;
	protected boolean switched_on = false;
	protected int value = 0;
	

	/** Construct an OmniUnit representation for a particular u nit.
	 * @param number
	 */
	public OmniUnit(int number) {
		super(number, OmniArea.Unit);
		op_pending = false;
		unit_variant = UnitVariant.Unit;
	}
	/** Construct an OmniUnit representation for a particular u nit.
	 * @param number
	 */
	public OmniUnit(UnitVariant unitvariant, int number) {
		super(number, OmniArea.Unit);
		this.op_pending = false;
		this.unit_variant = unitvariant;
	}
	
	protected OmniNotifyListener.ChangeMessage createChangeMessage( ChangeType changetype, OmniNotifyListener.NotifyType notifyType) {
		return new UnitChangeMessage(area, unit_variant, number, notifyType, changetype);
	}
	/** The raw Status of the unit.
	 * @return the status
	 */
	public int getRawStatus() {
		return raw_status;
	}

	/** set the state of unit.
	 * @param state the state to set
	 * The current condition of the unit depends on the type of the unit.
For X-10 units, the possible conditions are:
         0                 Last commanded off
         1                 Last commanded on
         17-25             Last commanded dim 1-9, respectively
         33-41             Last commanded brighten 1-9, respectively
         100-200           Last commanded level 0%-100%, respectively
For Lightolier Compose PLC units:
         0                 Off
         1                 On
         2-13              Scene A-L, respectively
         17-25             Last commanded dim 1-9, respectively
         33-41             Last commanded brighten 1-9, respectively
For Advanced Lighting Control (ALC) relay modules:
         0                 Off
         1                 On
For Advanced Lighting Control (ALC) dimmer modules:
         0               Off
         1               On
         100-200         Level 0%-100%, respectively
For Universal Powerline Bus (UPB) units:
         0               Off
         1               On
         100-200         Level 0%-100%, respectively
For voltage outputs:
         0               Off
         1               On
For flags:
         0               Off
         Non-zero        On
For counters:
         0-255           Counter value

	 */
	public void setStatus(int state) {
		updateStatus(state, 0, OmniNotifyListener.NotifyType.ChangeRequest);
	}
	/** Set the state/time remaining for the unit.
	 */
	public void setStatus(int state, int timeRemain) {
		updateStatus(state, timeRemain, OmniNotifyListener.NotifyType.ChangeRequest);
	}
	
	protected boolean forceChange( int timeSec, NotifyType notifyType, boolean pendFlag) {
		return ( (pendFlag && notifyType != NotifyType.ChangeRequest)
				|| timeSec != 0
				|| this.time_remain_sec != 0 );
	}
	/** Update the state (and time remaining) for the unit.
	 * @param state    The value of the unit.
	 * @param timeRemain  The time it is set for
	 * @param notifyType The type of notification (initial/update/ChangeRequest)
	 */
	public void updateStatus(int status,int timeRemain, OmniNotifyListener.NotifyType notifyType) {
		if (status != this.raw_status || forceChange(timeRemain,notifyType, op_pending)) {
			this.raw_status = status;
			op_pending = (notifyType == NotifyType.ChangeRequest);
			rawStatusChanged(timeRemain, notifyType);
		}
	}

	/** Notify the listeners and the derived units something has changed.
	 * @param notifyType  The type of notification
	 */
	protected void rawStatusChanged(int timeRemain, OmniNotifyListener.NotifyType notifyType) {
		if (timeRemain != this.time_remain_sec) {
			// just in case it wasn't set by the override
			this.time_remain_sec = timeRemain;
			if (timeRemain == 0)
				this.when_set = null;
			else
				this.when_set = new Date();
		}
		notify(createChangeMessage(ChangeType.RawState, notifyType));
	}

	/** The number of seconds the item was set for.  
	 */
	public int getTimeSec() {
		return time_remain_sec;
	}
	/** The calculated number of seconds remaining.
	 */
	public int getTimeSecRemain() {
		int result = time_remain_sec;
		if (when_set != null && result > 0){
			result -= (int)((when_set.getTime() - new Date().getTime()) /1000);
			if (result < 0) result = 0;
		}
		return result;
	}

	/**
	 * @return the unit_type
	 */
	public UnitType getUnitType() {
		return unit_type;
	}

	/**
	 * @param unitType the unit_type to set
	 */
	public void setUnitType(UnitType unitType) {
		updateUnitType(unitType, OmniNotifyListener.NotifyType.ChangeRequest);
	}

	public void updateUnitType(UnitType unitType, OmniNotifyListener.NotifyType notifyType) {
		if (unitType != this.unit_type) {
			unit_type = unitType;
			notify(createChangeMessage(ChangeType.UnitType, notifyType));
		}
	}

	/** Update the unit from the unit Properties.
	 * @param props   The unit properties object
	 * @param unit    The destination OmniUnit object
	 * @param isInitial True if this is the initial value.
	 */
	public void update(UnitProperties props, OmniNotifyListener.NotifyType notifyType) {
		this.updateName(props.getName(), notifyType);
		this.updateUnitType( UnitType.typeAsEnum(props.getUnitType()), notifyType);
		this.updateStatus(props.getState(), props.getTime(), notifyType);
	}

	/** Update the unit from the UnitStatus
	 * @param status    The unit status object.
	 * @param unit      The destination OmniUnit object.
	 * @param isInitial  True if this is the initial value.
	 */
	public void update(UnitStatus status,OmniNotifyListener.NotifyType notifyType) {
		this.updateStatus(status.getStatus(), status.getTime(), notifyType);
	}
	public void sendSetLevel(int levelPerc, int timeSec) {
		if (levelPerc < 0) levelPerc = 0;
		else if (levelPerc > 100) levelPerc = 100;
		notifyCmd(  CommandMessage.unitLevelCmd(number, levelPerc, TimeUnit.Seconds, timeSec));
	}
	/** Update a value
	 * @param newValue
	 * @param timeSec
	 * @param notifyType
	 */
	protected void updateValue(int newValue, int timeSec, NotifyType notifyType) {
		if (  newValue != value ||  forceChange(timeSec, notifyType, op_pending)) {
	
			this.value = newValue;
			this.switched_on = newValue!=0;
			this.time_remain_sec = timeSec;
			this.when_set = new Date();
			notify(createChangeMessage(ChangeType.Status, notifyType));
		}
	}
	protected void updateSwitchedOn(boolean b, int timeSec, NotifyType notifyType) {
		if (b != this.switched_on ||  forceChange(timeSec, notifyType, op_pending)) {
			this.switched_on = b;
			this.value = b?((unit_variant == UnitVariant.Device)?100:255):0;
			this.time_remain_sec = timeSec;
			this.when_set = new Date();
			notify(createChangeMessage(ChangeType.Status, notifyType));
		}		
	}

}
// vim: syntax=java.doxygen ts=4 sw=4 noet
