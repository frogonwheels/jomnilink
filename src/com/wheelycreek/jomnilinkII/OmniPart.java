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

import com.digitaldan.jomnilinkII.MessageTypes.CommandMessage;
import com.wheelycreek.jomnilinkII.OmniNotifyListener.ActionRequest;
import com.wheelycreek.jomnilinkII.OmniSystem.OmniArea;


/** Omni controller element base class.
 * Handles notification mechanism and name changes.
 * @author michaelg
 */
public class OmniPart extends OmniPartBase {

	public final OmniArea area;

	private String part_name;

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
		
		super(number);
		this.area = area;
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
	
	protected void notifyCmd( CommandMessage cmd ) {
		ActionRequest rqst = new ActionRequest(area, number, cmd);
		notify(rqst);
	}
}
// vim: syntax=java.doxygen ts=4 sw=4 noet
