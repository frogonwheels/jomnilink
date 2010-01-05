package com.digitaldan.jomnilinkII.MessageTypes.properties;

/**
*  Copyright (C) 2009  Dan Cunningham                                         
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

import com.digitaldan.jomnilinkII.MessageTypes.ObjectProperties;

public class UnitProperties extends ObjectProperties {
	public enum UnitType {
		Standard,
		Extended,
		Compose,
		UPB,
		HLCRoom,
		HLCLoad,
		LuminaMode,
		RadioRA,
		CentraLite,
		ViziaRFRoom,
		ViziaRFLoad,
		Flag,
		Output,
		AudioZone,
		AudioSource };
	public static UnitType unitTypeAsEnum( int type) {
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
	public static String unitTypeAsString(UnitType type) {
		switch (type) {
		case Standard:    return "Standard";
		case Extended:    return "Extended";
		case Compose:     return "Compose";
		case UPB:         return "UPB";
		case HLCRoom:     return "HLC Room";
		case HLCLoad:     return "HLC Load";
		case LuminaMode:  return "Lumina Mode";
		case RadioRA:     return "RadioRA";
		case CentraLite:  return "CentraLite";
		case ViziaRFRoom: return "ViziaRF Room";
		case ViziaRFLoad: return "ViziaRF Load";
		case Flag:        return "Flag";
		case Output:      return "Output";
		case AudioZone:   return "Audio Zone";
		case AudioSource: return "Audio Source";
		default: return "unknown";
		}
	}

	private int state;
	private int time;
	private int unitType;
	public UnitProperties(int number, int state, int time,
			int unitType, String name) {
		super(OBJ_TYPE_UNIT, number, name);
		this.state = state;
		this.time = time;
		this.unitType = unitType;
	}
	public int getState() {
		return state;
	}
	public int getTime() {
		return time;
	}
	public int getUnitType() {
		return unitType;
	}
	public UnitType getTypeOfUnit() {
		return unitTypeAsEnum(unitType);
	}
	public String toString() {
	    final String TAB = "    ";
	    String retValue = "";
	    
	    retValue = "UnitProperties ( "
	    	+ "number = " + this.number + TAB
	        + "state = " + this.state + TAB
	        + "time = " + this.time + TAB
	        + "unitType = " + this.unitType + " ("+unitTypeAsString(getTypeOfUnit())+")"+ TAB
	        + "name = " + this.name + TAB
	        + " )";
	
	    return retValue;
	}
}
