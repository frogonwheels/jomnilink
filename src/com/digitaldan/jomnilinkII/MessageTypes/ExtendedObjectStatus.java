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
import com.digitaldan.jomnilinkII.MessageTypes.statuses.Status;

public class ExtendedObjectStatus extends ObjectStatus implements Message{

	//private int statusType;
	private int recordLength;
	//private Status[] statuses;
	
	/*
	 * 
	OBJECT STATUS 
	 
	This message is sent by the HAI controller in reply to an OBJECT STATUS message.  The HAI controller reports 
	the status for the specified object(s). 
	 */

	
	public int getMessageType() {
		return MESG_TYPE_EXT_OBJ_STATUS;
	}

	public ExtendedObjectStatus(int statusType, int recordLength, Status[] statuses) {
		super(statusType, statuses);
		this.recordLength = recordLength;
	}
	
	public int getRecordLength() {
		return recordLength;
	}

	public String toString() {
	    final String TAB = "    ";
	    String retValue = "";
	    
	    retValue = "ObjectStatus ( "
	        + "statusType = " + this.getStatusType() + TAB
	        + "statuses = " + this.statusString() + TAB
	        + " )";
	
	    return retValue;
	}
	
}
