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

import com.digitaldan.jomnilinkII.MessageTypes.properties.UnitProperties;
import com.wheelycreek.jomnilinkII.OmniPart;
import com.wheelycreek.jomnilinkII.OmniSystem.OmniArea;
import com.wheelycreek.jomnilinkII.OmniNotifyListener;

/** Represents a 'Unit' in the omni controller.  This includes flags and units from external (eg UPB) devices.
 * @author michaelg
 */
public class OmniUnit extends OmniPart {
	/** Types of Unit changes.
	 */
	enum ChangeType { Status, State, Time, UnitType};

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

	
	/** Message sent when OmniZone changes.
	 */
	public class UnitChangeMessage extends OmniNotifyListener.ChangeMessage {
		public ChangeType change_type;

		public UnitChangeMessage(OmniArea area, int number, OmniNotifyListener.NotifyType notifyType,
				ChangeType changeType) {
			super(area, number, notifyType);
			change_type = changeType;
		}
	}
	/** Construct an OmniUnit representation for a particular u nit.
	 * @param number
	 */
	public OmniUnit(int number) {
		super(number, OmniArea.Unit);
	}
	
	protected OmniNotifyListener.ChangeMessage createChangeMessage( ChangeType changetype, OmniNotifyListener.NotifyType notifyType) {
		return new UnitChangeMessage(area, number, notifyType, changetype);
	}
	/**
	 * @return the state
	 */
	public int getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(int state) {
		updateState(state, OmniNotifyListener.NotifyType.ChangeRequest);
	}
	public void updateState(int state, OmniNotifyListener.NotifyType notifyType) {
		if (state != this.state) {
			this.state = state;
			notify(createChangeMessage(ChangeType.State, notifyType));
		}
	}

	/**
	 * @return the time_sec
	 */
	public int getTimeSec() {
		return time_sec;
	}

	/**
	 * @param timeSec the time_sec to set
	 */
	public void setTimeSec(int timeSec) {
		updateTimeSec(timeSec, OmniNotifyListener.NotifyType.ChangeRequest);
	}
	public void updateTimeSec(int timeSec, OmniNotifyListener.NotifyType notifyType) {
		if (time_sec != timeSec) {
			time_sec = timeSec;
			notify(createChangeMessage(ChangeType.Time, notifyType));
		}
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
	private int state;
	private int time_sec;
	private UnitType unit_type;

}
// vim: syntax=java.doxygen ts=4 sw=4 noet
