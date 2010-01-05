/** A part of an Omni-Controller.
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
package com.wheelycreek.jomnilinkII;

import java.util.Vector;

import com.wheelycreek.jomnilinkII.OmniNotifyListener;
import com.wheelycreek.jomnilinkII.OmniSystem.OmniArea;


/** Omni controller element base class.
 * Handles notification mechanism and name changes.
 * @author michaelg
 */
public class OmniPart {
	public final int number;
	public final OmniArea area;
	
	/** Message indicating a name change.
	  */
	public class NameChangeMessage extends OmniNotifyListener.ChangeMessage {
		
		public NameChangeMessage(OmniArea area, int number, String name, OmniNotifyListener.NotifyType notifyType) {
			super(area, number, notifyType);
			this.name = name;
		}
		public String name;
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return String.format(
				"NameChangeMessage [area=%s, number=%s, notifyType=%s]", area, number, notifyType);
		}
		
	}
	/** Construct with an element number and area.
	 * Identifies an omni component.
	 */
	public OmniPart(int number, OmniArea area) {
		
		this.number = number;
		this.area = area;
		notificationListeners = new Vector<OmniNotifyListener>(); 
	}
	private Vector<OmniNotifyListener> notificationListeners;
	private String part_name;
	
	/** Add a listener for changes to this 'Part'.
	 * @param listener Listener to add.
	 */
	public void addNotificationListener(OmniNotifyListener listener){
		synchronized (notificationListeners) {
			notificationListeners.add(listener);
		}
	}
	/** Remove listener from the list.
	 * @param listener
	 */
	public void removeNotificationListener(OmniNotifyListener listener){
		synchronized (notificationListeners) {
			if(notificationListeners.contains(listener))
				notificationListeners.remove(listener);
		}
	}
	/** Construct a basic change message.
	 * Generally a more specific change message would be constructed.
	 * @param notifyType The type of notification.
	 */
	protected OmniNotifyListener.ChangeMessage createChangeMessage(OmniNotifyListener.NotifyType notifyType) {
		OmniNotifyListener.ChangeMessage msg = new OmniNotifyListener.ChangeMessage(area, number, notifyType);

		return msg;
	}
	/** Construct a name change message.
	  * @param notifyType The type of notification.
	  */
	protected OmniNotifyListener.ChangeMessage createNameMessage(OmniNotifyListener.NotifyType notifyType) {
		return new NameChangeMessage(area, number, part_name, notifyType);
	}
	/** Called by derived class property setters to notify of changes.
	 * @param message
	 */
	protected void notify( OmniNotifyListener.ChangeMessage message ) {
		synchronized (notificationListeners) {
			for (OmniNotifyListener l : notificationListeners) {
				l.objectChangedNotification(message);
			}
		}
	}
	/** Change the name of a part.
	 * @param part_name
	 */
	public void setName(String part_name) {
		updateName(part_name, OmniNotifyListener.NotifyType.ChangeRequest);
	}
	/** Update the name of a part.
	 * @param part_name the name to set
	 * @param notifyType the type of change notification
	 */
	public void updateName(String part_name, OmniNotifyListener.NotifyType notifyType) {
		if (this.part_name != part_name) {
			this.part_name = part_name;
			notify(createNameMessage(notifyType));
		}
	}
	/** The name of the part.
	 * @return part_name
	 */
	public String getName() {
		return part_name;
	}

}
// vim: syntax=java.doxygen ts=4 sw=4 noet
