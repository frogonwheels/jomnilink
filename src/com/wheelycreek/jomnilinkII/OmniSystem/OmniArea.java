/** Enumeration for an Area in an omni controller.
 *
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
package com.wheelycreek.jomnilinkII.OmniSystem;

import com.digitaldan.jomnilinkII.Message;
/** Represents each area of an omni controller.
  */
public enum OmniArea {
	Zone  (Message.OBJ_TYPE_ZONE, "Zone"),
	Unit  (Message.OBJ_TYPE_UNIT, "Unit"),
	Button(Message.OBJ_TYPE_BUTTON, "Button"),
	Code  (Message.OBJ_TYPE_CODE,"Code"),
	Area  (Message.OBJ_TYPE_AREA,"Area"),
	Thermo(Message.OBJ_TYPE_THERMO,"Thermo"),
	Msg   (Message.OBJ_TYPE_MESG,"Message"),
	Sensor(Message.OBJ_TYPE_AUX_SENSOR,"Aux Sensor"),
	AudioSrc(Message.OBJ_TYPE_AUDIO_SOURCE,"Audio Source"),
	AudioZone(Message.OBJ_TYPE_AUDIO_ZONE, "Audio Zone"),
	UserSetting(Message.OBJ_TYPE_USER_SETTING,"User Setting"),
	ControlReader(Message.OBJ_TYPE_CONTROL_READER, "Control Reader"),
	ControlLock(Message.OBJ_TYPE_CONTROL_LOCK, "Control Lock"),
	ExpEnclosure(Message.OBJ_TYPE_EXP, "Expansion Enclosure"),
	UPBLink (0/*no object type*/, "UPBLink");
	private final int obj_type;
	private final String obj_name;

	OmniArea(int objtype, String name) {
		this.obj_type = objtype;
		this.obj_name = name;
	}
	@Override
	public String toString(){
		return obj_name;
	}
	public int get_objtype_msg() {
		return obj_type;
	}
	/** Convert an omni message type to an area type.
	  */
	public static OmniArea fromMessageType( int objType ) {
		switch (objType) {
		case Message.OBJ_TYPE_ZONE:        return Zone;
		case Message.OBJ_TYPE_UNIT:        return Unit;
		case Message.OBJ_TYPE_BUTTON:      return Button;
		case Message.OBJ_TYPE_CODE:        return Code;
		case Message.OBJ_TYPE_AREA:        return Area;
		case Message.OBJ_TYPE_THERMO:      return Thermo;
		case Message.OBJ_TYPE_AUDIO_SOURCE:return AudioSrc;
		case Message.OBJ_TYPE_AUDIO_ZONE:  return AudioZone;
		case Message.OBJ_TYPE_USER_SETTING:return UserSetting;
		case Message.OBJ_TYPE_AUX_SENSOR:  return Sensor;
		case Message.OBJ_TYPE_MESG:        return Msg;
		case Message.OBJ_TYPE_CONTROL_READER: return ControlReader;
		case Message.OBJ_TYPE_CONTROL_LOCK:return ControlLock;
		case Message.OBJ_TYPE_EXP:         return ExpEnclosure;
		default:
			return null;
		}
	}
}
// vim: syntax=java.doxygen ts=4 sw=4 noet
