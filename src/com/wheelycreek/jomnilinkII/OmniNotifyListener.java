/** Notification for the OmniController.
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

import com.wheelycreek.jomnilinkII.OmniSystem.OmniArea;

/** Notification for the OmniController children to say they have changed
 * @author michaelg
 */
public interface OmniNotifyListener {
	/** The type of change.
	  */
	public enum NotifyType {
		/// Initial value
		Initial, 
		/// Change from Omni
		Notify,
		/// User Change request.
		ChangeRequest
	}
	/** Override to provide further information.
	 * @author michaelg
	 */
	public class ChangeMessage {
		public ChangeMessage(OmniArea area, int number, NotifyType notifyType) {
			this.area = area;
			this.number = number;
			this.notifyType = notifyType;
		}
		/** The area that changed.
		 */
		public OmniArea area; 
		/** The number of the part that changed.
		 * eg zone number
		 */
		public int number;
		/** Why is this update sent?
		 */
		public NotifyType notifyType;
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return String.format("ChangeMessage [area=%s, number=%s, notifyType=%s]", area,
					number, notifyType);
		}
	}
	public void objectChangedNotification(ChangeMessage msg);
}
// vim: syntax=java.doxygen ts=4 sw=4 noet
