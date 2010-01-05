/** Represents a Button in the omni controller
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

import com.wheelycreek.jomnilinkII.OmniNotifyListener;
import com.wheelycreek.jomnilinkII.OmniPart;
import com.wheelycreek.jomnilinkII.OmniSystem.OmniArea;

/** User Button.
  * @author michaelg 
  */
public class OmniButton extends OmniPart {
	
	public class ButtonPressMessage extends OmniNotifyListener.ChangeMessage {
		public ButtonPressMessage(int number, OmniNotifyListener.NotifyType notifyType) {
			super(OmniArea.Button, number, notifyType);
		}
	}
	protected OmniNotifyListener.ChangeMessage createChangeMessage(OmniNotifyListener.NotifyType notifyType) {
		return new ButtonPressMessage(number, notifyType);
	}

	/** Construct a button for a number.
	  */
	public OmniButton(int number) {
		super(number, OmniArea.Button);
	}

	/** Trigger a button.
	  */
	public void trigger() {
		notify(createChangeMessage(OmniNotifyListener.NotifyType.ChangeRequest));
	}

	/** Notify a button was pressed.
	  */
	public void notifyPress() {
		notify(createChangeMessage(OmniNotifyListener.NotifyType.Notify));
	};
};

// vim: syntax=java.doxygen ts=4 sw=4 noet
