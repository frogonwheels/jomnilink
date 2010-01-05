/**
 * 
 */
package com.wheelycreek.jomnilinkII.OmniSystem;

import com.digitaldan.jomnilinkII.Message;

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
	ExpEnclosure(Message.OBJ_TYPE_EXP, "Expansion Enclosure");
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