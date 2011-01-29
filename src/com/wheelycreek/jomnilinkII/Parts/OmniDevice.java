/**Omni controller part, a Device (light
 * 
 */
package com.wheelycreek.jomnilinkII.Parts;


import com.wheelycreek.jomnilinkII.OmniNotifyListener.NotifyType;

/**
 * @author michaelg
 *
 */
public class OmniDevice extends OmniUnit {
	private char scene = ' ';
	/**
	 * @param number
	 */
	public OmniDevice(int number) {
		super(UnitVariant.Device, number);
	}
	/**
	 * @return the switched_on
	 */
	public boolean isSwitchedOn() {
		return switched_on;
	}
	/**
	 * @param switchedOn the switched_on to set
	 */
	public void setSwitchedOn(boolean switchedOn) {
		switched_on = switchedOn;
	}
	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(int value) {
		this.value = value;
	}
	/* (non-Javadoc)
	 * @see com.wheelycreek.jomnilinkII.Parts.OmniUnit#rawStatusChanged(com.wheelycreek.jomnilinkII.OmniNotifyListener.NotifyType)
	 */
	@Override
	protected void rawStatusChanged(int timeRemain, NotifyType notifyType) {
		if (raw_status <= 1) {
			updateSwitchedOn( (raw_status == 1), timeRemain, notifyType );
		} else if (raw_status <= 13) {
			updateScene( (char)('A' + (raw_status-2)), notifyType );
		} else if ( raw_status <= 25) {
			if (raw_status >= 17) 
				updatePerformDim( -(raw_status - 17), notifyType);
		} else if (raw_status <= 41) {
			if (raw_status >= 33)
				updatePerformDim( raw_status-33, notifyType);
		} else if (raw_status >= 100) {
			if (raw_status <= 200) {
				updateLevelPerc(raw_status-100, timeRemain, notifyType);
			}
		}
		super.rawStatusChanged(timeRemain, notifyType);
	}

	protected void updateLevelPerc(int perc, int timeSec, NotifyType notifyType) {
		if (perc< 0) 
			perc = 0;
		else if (perc>100)
			perc = 100;
		updateValue(perc, timeSec, notifyType);
		
	}

	protected void updatePerformDim(int i, NotifyType notifyType) {
		updateLevelPerc(value+(i*10), 0, notifyType);
	}

	protected void updateScene(char  scn, NotifyType notifyType) {
		if (scn != scene ) {
			this.scene = scn;
			notify(createChangeMessage( ChangeType.Scene, notifyType));
		}
	}
	
}
