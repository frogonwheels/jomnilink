package com.digitaldan.jomnilinkII.MessageTypes;

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

import com.digitaldan.jomnilinkII.Message;

public class SystemTroubles implements Message {

	private int [] troubles;
	
	/*
	 *This message is sent by the HAI controller in reply to a REQUEST SYSTEM TROUBLES message. The controller
reports any system troubles. If multiple troubles exist, each trouble is reported in a separate data byte.
         Start character            0x21
         Message length             number of troubles + 1
         Message type               0x1B
         Data 1                     first trouble
         ...
         Data n                     last trouble
         CRC 1                      varies
         CRC 2                      varies
The system trouble conditions are shown below.
 Trouble Byte            Condition
        1         Freeze
        2         Battery low
        3         AC power
        4         Phone line
        5         Digital communicator
        6         Fuse
        7         Freeze
        8         Battery low
 
	 */
	
	public SystemTroubles(int[] troubles) {
		super();
		this.troubles = troubles;
	}

	public enum SystemTrouble {
		FREEZE(1,"Freeze"),
		BATTERY(2,"Battery low"),
		MAINS(3,"AC power"),
		PHONE(4,"Phone line"),
		DIGCOM(5,"Digital communicator"),
		FUSE(6,"Fuse")
		//FREEZE2(7,"Freeze"),
		//BATTERY2(8,"Battery low")	 
		;
		final public int rawID;
		final public String xmlID;
		private SystemTrouble(int rawID, String xmlID) {
			this.rawID = rawID;
			this.xmlID = xmlID;
		}
		static public SystemTrouble rawAsEnum(int id) {
			switch (id) {
			case 1: return FREEZE;
			case 2: return BATTERY;
			case 3: return MAINS;
			case 4: return PHONE;
			case 5: return DIGCOM;
			case 6: return FUSE;
			case 7: return FREEZE;
			case 8: return BATTERY;
			default: return null;
			}
		}
	}
	public int[] getTroubles() {
		return troubles;
	}
	public int getTroubleCount() {
		if (troubles == null) return 0;
		return troubles.length;
	}
	public SystemTrouble getTrouble(int i) {
		if (troubles == null || i < 0 || i >= troubles.length)
			return null;
		return SystemTrouble.rawAsEnum(troubles[i]);
	}
	
	public int getMessageType() {
		return MESG_TYPE_SYS_TROUBLES;
	}

	public String toString() {
	    final String TAB = "    ";
	    String retValue = "";
	    
	    retValue = "SystemTroubles ( "
	        + "troubles = " + this.troubles + TAB
	        + " )";
	
	    return retValue;
	}

}
