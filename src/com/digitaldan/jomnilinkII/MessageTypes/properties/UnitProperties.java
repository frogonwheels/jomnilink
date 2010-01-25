package com.digitaldan.jomnilinkII.MessageTypes.properties;

/*
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
/*
 * See UnitStatus for description of status.
 */
import com.digitaldan.jomnilinkII.MessageTypes.ObjectProperties;

public class UnitProperties extends ObjectProperties {
	
	public static final int UNIT_PROP_Standard = 1;
	public static final int UNIT_PROP_Extended=2;
	public static final int UNIT_PROP_Compose=3;
	public static final int UNIT_PROP_UPB =4;
	public static final int UNIT_PROP_HLCRoom=5;
	public static final int UNIT_PROP_HLCLoad=6;
	public static final int UNIT_PROP_LuminaMode=7;
	public static final int UNIT_PROP_RadioRA=8;
	public static final int UNIT_PROP_CentraLite=9;
	public static final int UNIT_PROP_ViziaRFRoom=10;
	public static final int UNIT_PROP_ViziaRFLoad=11;
	public static final int UNIT_PROP_Flag=12;
	public static final int UNIT_PROP_Output=13;
	public static final int UNIT_PROP_AudioZone=14;
	public static final int UNIT_PROP_AudioSource=15;
	
	public static String unitTypeAsString(int type) {
		switch (type) {
		case UNIT_PROP_Standard:    return "Standard";
		case UNIT_PROP_Extended:    return "Extended";
		case UNIT_PROP_Compose:     return "Compose";
		case UNIT_PROP_UPB:         return "UPB";
		case UNIT_PROP_HLCRoom:     return "HLC Room";
		case UNIT_PROP_HLCLoad:     return "HLC Load";
		case UNIT_PROP_LuminaMode:  return "Lumina Mode";
		case UNIT_PROP_RadioRA:     return "RadioRA";
		case UNIT_PROP_CentraLite:  return "CentraLite";
		case UNIT_PROP_ViziaRFRoom: return "ViziaRF Room";
		case UNIT_PROP_ViziaRFLoad: return "ViziaRF Load";
		case UNIT_PROP_Flag:        return "Flag";
		case UNIT_PROP_Output:      return "Output";
		case UNIT_PROP_AudioZone:   return "Audio Zone";
		case UNIT_PROP_AudioSource: return "Audio Source";
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

	@Override
	public String toString() {
	    final String TAB = "    ";
	    String retValue = "";
	    
	    retValue = "UnitProperties ( "
	    	+ "number = " + this.number + TAB
	    	+ "name = " + this.name + TAB
	    	+ "unitType = " + this.unitType + " ("+unitTypeAsString(unitType)+")"+ TAB
	    	+ "state = " + this.state + TAB
	        + "time = " + this.time + TAB
	        + " )";
	
	    return retValue;
	}
}
