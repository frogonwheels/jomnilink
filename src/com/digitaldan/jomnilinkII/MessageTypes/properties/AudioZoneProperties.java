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

public class AudioZoneProperties extends ObjectProperties {

	private boolean on;
	private int source;
	private int volume;
	private boolean mute;
	public AudioZoneProperties(int number, boolean on,
			int source, int volume, boolean mute, String name) {
		super(OBJ_TYPE_AUDIO_ZONE, number, name);
		this.on = on;
		this.source = source;
		this.volume = volume;
		this.mute = mute;
		this.name = name;
	}
	public boolean isOn() {
		return on;
	}
	public int getSource() {
		return source;
	}
	public int getVolume() {
		return volume;
	}
	public boolean isMute() {
		return mute;
	}
	public String toString() {
	    final String TAB = "    ";
	    String retValue = "";
	    
	    retValue = "AudioZoneProperties ( "
	    	+ "number = " + this.number + TAB
	        + "on = " + this.on + TAB
	        + "source = " + this.source + TAB
	        + "volume = " + this.volume + TAB
	        + "mute = " + this.mute + TAB
	        + "name = " + this.name + TAB
	        + " )";
	
	    return retValue;
	}
}
