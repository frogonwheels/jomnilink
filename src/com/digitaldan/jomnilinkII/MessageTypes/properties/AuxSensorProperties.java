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
/*
 * Sensor Type  Description
 * 80           Programmable Energy Saver Module
 * 81           Outdoor Temperature
 * 82           Temperature
 * 83           Temperature Alarm
 * 84           Humidity
 * 85           Extended Range Outdoor Temperature
 * 86           Extended Range Temperature
 * 87           Extended Range Temperature Alarm
 */

import com.digitaldan.jomnilinkII.MessageTypes.ObjectProperties;

public class AuxSensorProperties extends ObjectProperties{

	private int status;
	private int current;
	private int lowSetpoint;
	private int highSetpoint;
	private int sensorType;
	public AuxSensorProperties(int number, int status,
			int current, int lowSetpoint, int highSetpoint, int sensorType,
			String name) {
		super(OBJ_TYPE_AUX_SENSOR, number,name);
		this.status = status;
		this.current = current;
		this.lowSetpoint = lowSetpoint;
		this.highSetpoint = highSetpoint;
		this.sensorType = sensorType;
	}
	public static String getSensorTypeDesc( int type) {
		switch(type) {
		case 80: return "Programmable Energy Saver Module";
		case 81: return "Outdoor Temperature";
		case 82: return "Temperature";
		case 83: return "Temperature Alarm";
		case 84: return "Humidity";
		case 85: return "Extended Range Outdoor Temperature";
		case 86: return "Extended Range Temperature";
		case 87: return "Extended Range Temperature Alarm";
		default: return "Unknown";
		}
	}
	public int getStatus() {
		return status;
	}
	public int getCurrent() {
		return current;
	}
	public int getLowSetpoint() {
		return lowSetpoint;
	}
	public int getHighSetpoint() {
		return highSetpoint;
	}
	public int getSensorType() {
		return sensorType;
	}
	public String toString() {
	    final String TAB = "    ";
	    String retValue = "";

	    retValue = "AuxSensorProperties ( "
	    	+ "number = " + this.number + TAB
	        + "name = " + this.name + TAB
	        + "sensorType = " + this.sensorType
			+ " ("+getSensorTypeDesc(getSensorType())+")"+ TAB
	        + "status = " + this.status + TAB
	        + "current = " + this.current + TAB
	        + "lowSetpoint = " + this.lowSetpoint + TAB
	        + "highSetpoint = " + this.highSetpoint + TAB
	        + " )";
	
	    return retValue;
	}
	
}
// vim: syntax=java.doxygen ts=4 sw=4 noet
