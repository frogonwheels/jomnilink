/** Unit configured/used as a flag.
 */
package com.wheelycreek.jomnilinkII.Parts;

import com.wheelycreek.jomnilinkII.OmniNotifyListener.NotifyType;

public class OmniFlag extends OmniUnit {

	public OmniFlag(int number) {
		super(UnitVariant.Flag, number);
	}


	/* (non-Javadoc)
	 * @see com.wheelycreek.jomnilinkII.Parts.OmniUnit#rawStatusChanged(com.wheelycreek.jomnilinkII.OmniNotifyListener.NotifyType)
	 */
	@Override

	protected void rawStatusChanged(int timeRemain, NotifyType notifyType) {
		updateSwitchedOn( (raw_status != 0), timeRemain, notifyType ); 
		super.rawStatusChanged(timeRemain, notifyType);
	}
	
	public boolean getSwitchedOn() {
		return switched_on;
	}
	public int getValue() {
		return getRawStatus();
	}
}