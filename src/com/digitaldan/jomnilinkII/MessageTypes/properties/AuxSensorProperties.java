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
	        + "status = " + this.status + TAB
	        + "current = " + this.current + TAB
	        + "lowSetpoint = " + this.lowSetpoint + TAB
	        + "highSetpoint = " + this.highSetpoint + TAB
	        + "sensorType = " + this.sensorType + TAB
	        + "name = " + this.name + TAB
	        + " )";
	
	    return retValue;
	}
	
}
