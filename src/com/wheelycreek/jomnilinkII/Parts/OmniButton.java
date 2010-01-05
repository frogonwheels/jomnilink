/** Represents a Button in the omni controller
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
		public ButtonPressMessage(int number) {
			super(OmniArea.Button, number, OmniNotifyListener.NotifyType.Notify);
		}
	}
	private OmniNotifyListener.ChangeMessage createChangeMessage() {
		return new ButtonPressMessage(number);
	}
	public void notifyButton() {
		notify(createChangeMessage());
	};
	public OmniButton(int number) {
		super(number, OmniArea.Button);
	}
};

// vim: syntax=java.doxygen ts=4 sw=4 noet
