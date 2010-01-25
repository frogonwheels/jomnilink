/**
 * 
 */
package com.wheelycreek.jomnilinkII.Parts;

import com.wheelycreek.jomnilinkII.OmniNotifyListener.NotifyType;

/**
 * @author michaelg
 *
 */
public class OmniOutput extends OmniUnit {

	/**
	 * @param number
	 */
	public OmniOutput(int number) {
		super(UnitVariant.Output, number);
		// TODO Auto-generated constructor stub
	}
	/* (non-Javadoc)
	 * @see com.wheelycreek.jomnilinkII.Parts.OmniUnit#rawStatusChanged(com.wheelycreek.jomnilinkII.OmniNotifyListener.NotifyType)
	 */
	@Override
	protected void rawStatusChanged(int timeRemain, NotifyType notifyType) {
		updateSwitchedOn( (raw_status != 0), timeRemain, notifyType ); 
		super.rawStatusChanged(timeRemain, notifyType);
	}

}
