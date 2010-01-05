/** Temperature sensors.
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
package com.wheelycreek.jomnilinkII.Parts;

import com.wheelycreek.jomnilinkII.OmniPart;
import com.wheelycreek.jomnilinkII.OmniSystem.OmniArea;
import com.wheelycreek.jomnilinkII.OmniSystem.Temperature;
import com.wheelycreek.jomnilinkII.OmniNotifyListener;

/** Omni Temperature sensor.
 * @author michaelg
 */
public class OmniSensor extends OmniPart {

	public enum SensorType {
		EnergySave(80, "Programmable Energy Saver Module"),
		OutdoorTemp(81, "Outdoor Temperature"),
		Temp(82, "Temperature"),
		TempAlarm(83, "Temperature Alarm"),
		Humidity(84, "Humidity"),
		ExtOutdoorTemp(85, "Extended Range Outdoor Temperature"),
		ExtTemp(86, "Extended Range Temperature"),
		ExtTempAlarm(87, "Extended Range Temperature Alarm");
		private int rawType;
		private String name;

		public String getName() {
			return name;
		}
		public int getRawType() {
			return rawType;
		}
		private SensorType(int rawType, String name) {
			this.rawType = rawType;
			this.name = name;
		}

		public static SensorType typeAsEnum( int rawType ) {
			switch (rawType) {
			case 80: return EnergySave;
			case 81: return OutdoorTemp;
			case 82: return Temp;
			case 83: return TempAlarm;
			case 84: return Humidity;
			case 85: return ExtOutdoorTemp;
			case 86: return ExtTemp;
			case 87: return ExtTempAlarm;
			default: return null;
			}
		}
	}

	/** Create an omni sensor with given number.
	 * @param number
	 * @param area
	 */
	public OmniSensor(int number) {
		super(number, OmniArea.Sensor);
	}
	/** Represent types of Sensor changes.
	 */
	enum ChangeType { Status, Type, Temperature, HeatSet, CoolSet};

	/** Message sent when OmniZone changes.
	 */
	public class SensorChangeMessage extends OmniNotifyListener.ChangeMessage {
		public ChangeType change_type;

		public SensorChangeMessage(OmniArea area, int number, OmniNotifyListener.NotifyType notifyType,
				ChangeType changeType) {
			super(area, number, notifyType);
			change_type = changeType;
		}
	}
	protected OmniNotifyListener.ChangeMessage createChangeMessage( ChangeType changetype, OmniNotifyListener.NotifyType notifyType) {
		return new SensorChangeMessage(area, number, notifyType, changetype);
	}
	
	
	private boolean trigger_output;
	private Temperature temp, heatSet, coolSet;
	private SensorType sensor_type;
	
	/**
	 * @return the trigger_output
	 */
	public boolean isTriggerOutput() {
		return trigger_output;
	}
	public void setTriggerOutput(boolean triggerOutput) {
		setTriggerOutput(triggerOutput, OmniNotifyListener.NotifyType.ChangeRequest);
	}
	/**
	 * @param triggerOutput the trigger_output to set
	 */
	public void setTriggerOutput(boolean triggerOutput, OmniNotifyListener.NotifyType notifyType) {
		if (trigger_output != triggerOutput) {
			trigger_output = triggerOutput;
			notify(createChangeMessage(ChangeType.Status, notifyType ));
		}
	}
	/**
	 * @return the temperature
	 */
	public Temperature getTemperature() {
		if (temp == null)
			temp = new Temperature();
		return temp;
	}
	/** Set the current temperature
	 * @param temp the temperature to set
	 */
	public void setTemperature(Temperature temp) {
		setTemperature(temp, OmniNotifyListener.NotifyType.ChangeRequest);
	}
	/** Return true if two temperatures are different.
	  * Allows for nulls.
	  */
	private static boolean isDifferent(Temperature lhs, Temperature rhs) {
		return (lhs != rhs) && (lhs == null || lhs == null || !lhs.equals(rhs));
	}
	/** Update the temperature.
	  */
	public void setTemperature(Temperature temp, OmniNotifyListener.NotifyType notifyType) {
		if (isDifferent(this.temp,temp)) {
			this.temp = temp;
			notify(createChangeMessage(ChangeType.Temperature, notifyType));
		}
	}
	/**
	 * @return the heatSet
	 */
	public Temperature getHeatSetPoint() {
		if (heatSet == null)
			heatSet = new Temperature();
		return heatSet;
	}
	/**
	 * @param heatSet the heatSet to set
	 */
	public void setHeatSetPoint(Temperature heatSet) {
		setHeatSetPoint(heatSet, OmniNotifyListener.NotifyType.ChangeRequest);
	}
	public void setHeatSetPoint(Temperature temp, OmniNotifyListener.NotifyType notifyType) {
		if (isDifferent(this.heatSet,temp)) {
			this.heatSet = temp;
			notify(createChangeMessage(ChangeType.HeatSet, notifyType));
		}
	}
	/**
	 * @return the coolSet
	 */
	public Temperature getCoolSetPoint() {
		if (coolSet == null)
			coolSet = new Temperature();
		return coolSet;
	}
	/**
	 * @param coolSet the coolSet to set
	 */
	public void setCoolSetPoint(Temperature coolSet) {
		setCoolSetPoint(coolSet, OmniNotifyListener.NotifyType.ChangeRequest);
	}
	public void setCoolSetPoint(Temperature temp, OmniNotifyListener.NotifyType notifyType) {
		if (isDifferent(this.coolSet,temp)) {
			this.coolSet = temp;
			notify(createChangeMessage(ChangeType.CoolSet, notifyType));
		}
	}

	/** Get the sensor type.
	  */
	public SensorType getSensorType() {
	   return sensor_type;
	}
	/** Set the sensor type.
	 * @param type The type of sensor.
	 */
	public void setSensorType( SensorType type) {
		setSensorType(type, OmniNotifyListener.NotifyType.ChangeRequest);
	}
	/** Set the sensor type.
	 * @param type The type of sensor.
	 * @param notifyType Is this the initial setting.
	 */
	public void setSensorType( SensorType type, OmniNotifyListener.NotifyType notifyType) {
		if (type != sensor_type) {
			sensor_type = type;
			notify(createChangeMessage(ChangeType.Type, notifyType));
		}
	}
}
// vim: syntax=java.doxygen ts=4 sw=4 noet
