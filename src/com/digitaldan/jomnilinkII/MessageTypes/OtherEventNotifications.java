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
import com.digitaldan.jomnilinkII.MessageTypes.events.OtherEvent;
import com.digitaldan.jomnilinkII.MessageTypes.events.UPBLinkEvent;
import com.digitaldan.jomnilinkII.MessageTypes.events.UserMacroButtonEvent;
import com.digitaldan.jomnilinkII.MessageTypes.events.CentraliteSwitchEvent;
import com.digitaldan.jomnilinkII.MessageTypes.events.ProlinkMessageEvent;
import com.digitaldan.jomnilinkII.MessageTypes.events.AlarmEvent;
import com.digitaldan.jomnilinkII.MessageTypes.events.AllSwitchEvent;
import com.digitaldan.jomnilinkII.MessageTypes.events.PhoneLineEvent;
import com.digitaldan.jomnilinkII.MessageTypes.events.PowerEvent;
import com.digitaldan.jomnilinkII.MessageTypes.events.DCMEvent;
import com.digitaldan.jomnilinkII.MessageTypes.events.EnergyCostEvent;
import com.digitaldan.jomnilinkII.MessageTypes.events.X10CodeEvent;
import com.digitaldan.jomnilinkII.MessageTypes.events.SwitchPressEvent;
import com.digitaldan.jomnilinkII.MessageTypes.events.ComposeCodeEvent;
import com.digitaldan.jomnilinkII.MessageTypes.events.SecurityArmEvent;


public class OtherEventNotifications implements Message{
	
	public static OtherEvent.EventType typeOfMessage( int msg) {
		switch (msg & 0xf000) {
		case 0x0000: {
			switch ((msg & 0xff00) >> 8) {
			case 0x0: return OtherEvent.EventType.UserMacroButton;
			case 0x1:
				if ((msg & 0xff800) == 0x01800)
					return OtherEvent.EventType.CentraliteSwitch;
				else
					return OtherEvent.EventType.ProlinkMessage;
			case 0x2:
				return OtherEvent.EventType.Alarm;
			case 0x3:
				if ((msg & 0xe0) == 0xe0) 
					return OtherEvent.EventType.AllSwitch;
				else
					switch (msg & 0xc) {
					case 0x0:
						return OtherEvent.EventType.PhoneLine;
					case 0x4:
						return OtherEvent.EventType.Power; 
					case 0x8:
						if ((msg & 0x2) == 0)
							return OtherEvent.EventType.DCM;
						else
							return OtherEvent.EventType.EnergyCost;
					default:
						if ((msg & 2) == 0)
							return OtherEvent.EventType.EnergyCost;
						else
							return null;
					}
			default:
				if ((msg & 0xfc00) == 0x0c00)
					return OtherEvent.EventType.X10Code;
				else
					return null;		
			}
		}
		case 0xf000:
			if ((msg & 0xfc00) == 0xfc00)
				return OtherEvent.EventType.UPBLink;
			else
				return OtherEvent.EventType.UnitSwitchPress;	
		case 0x7000: return OtherEvent.EventType.ComposeCode;
		default:
			return OtherEvent.EventType.SecurityArming;
		}
	} 
	
	private OtherEvent[] notifications;
	
	public OtherEventNotifications(int[] notifications){
		
		this.notifications = new OtherEvent[notifications.length];
		for(int i=0; i<notifications.length; ++i) {			
			this.notifications[i] = createOtherEvent(notifications[i]); 
		}
	}

	private OtherEvent createOtherEvent(int notification ) {
		
		switch (typeOfMessage(notification)) {
		case UserMacroButton:   return new UserMacroButtonEvent(notification);
		case ProlinkMessage:	return new ProlinkMessageEvent(notification);
		case CentraliteSwitch:	return new CentraliteSwitchEvent(notification);
		case Alarm:				return new AlarmEvent(notification);
		case ComposeCode:		return new ComposeCodeEvent(notification);
		case X10Code:			return new X10CodeEvent(notification);
		case SecurityArming:	return new SecurityArmEvent(notification);
		case UnitSwitchPress:	return new SwitchPressEvent(notification);
		case UPBLink:			return new UPBLinkEvent(notification);
		case AllSwitch:			return new AllSwitchEvent(notification);
		case PhoneLine:			return new PhoneLineEvent(notification);
		case Power:				return new PowerEvent(notification);
		case DCM:				return new DCMEvent(notification);
		case EnergyCost:		return new EnergyCostEvent(notification);
		case LumniaModeChange:
		default: return new OtherEvent(notification);
		
		}
	}
	
	public OtherEvent[] getNotifications(){
		return notifications;
	}
	public int Count() {
		return notifications.length;
	}
	
	public OtherEvent getNotification(int idx) {
		return notifications[idx];
	}
	
	public int getMessageType() {
		return MESG_TYPE_OTHER_EVENT_NOTIFY;
	}

	public String toString() {
	    final String TAB = "    ";
	    String retValue = "";
	    
	    retValue = "OtherEventNotifications ( "
	    	  + "notifications = ";
	    for (int i=0; i< notifications.length; ++i)
	    	retValue += notifications[i].toString();
	    retValue +=TAB  + " )";
	
	    return retValue;
	}
}
