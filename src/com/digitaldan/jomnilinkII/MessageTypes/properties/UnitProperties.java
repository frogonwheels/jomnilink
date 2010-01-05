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
	
	public static int UNIT_PROP_Standard = 1;
	public static int UNIT_PROP_Extended=2;
	public static int UNIT_PROP_Compose=3;
	public static int UNIT_PROP_UPB =4;
	public static int UNIT_PROP_HLCRoom=5;
	public static int UNIT_PROP_HLCLoad=6;
	public static int UNIT_PROP_LuminaMode=7;
	public static int UNIT_PROP_RadioRA=8;
	public static int UNIT_PROP_CentraLite=9;
	public static int UNIT_PROP_ViziaRFRoom=10;
	public static int UNIT_PROP_ViziaRFLoad=11;
	public static int UNIT_PROP_Flag=12;
	public static int UNIT_PROP_Output=13;
	public static int UNIT_PROP_AudioZone=14;
	public static int UNIT_PROP_AudioSource=15;
	
	public static String unitTypeAsString(int type) {
		switch (type) {
		case 1:  return "Standard";
		case 2:  return "Extended";
		case 3:  return "Compose";
		case 4:  return "UPB";
		case 5:  return "HLC Room";
		case 6:  return "HLC Load";
		case 7:  return "Lumina Mode";
		case 8:  return "RadioRA";
		case 9:  return "CentraLite";
		case 10: return "ViziaRF Room";
		case 11: return "ViziaRF Load";
		case 12: return "Flag";
		case 13: return "Output";
		case 14: return "Audio Zone";
		case 15: return "Audio Source";
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
	public String toString() {
	    final String TAB = "    ";
	    String retValue = "";
	    
	    retValue = "UnitProperties ( "
	    	+ "number = " + this.number + TAB
	        + "state = " + this.state + TAB
	        + "time = " + this.time + TAB
	        + "unitType = " + this.unitType + " ("+unitTypeAsString(unitType)+")"+ TAB
	        + "name = " + this.name + TAB
	        + " )";
	
	    return retValue;
	}
}
