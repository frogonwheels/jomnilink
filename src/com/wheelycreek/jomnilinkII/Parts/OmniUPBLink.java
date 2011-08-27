/**
 * 
 */
package com.wheelycreek.jomnilinkII.Parts;

import com.wheelycreek.jomnilinkII.OmniNotifyListener;
import com.wheelycreek.jomnilinkII.OmniPartBase;
import com.wheelycreek.jomnilinkII.OmniNotifyListener.NotifyType;
import com.wheelycreek.jomnilinkII.OmniSystem.OmniArea;

/**
 * @author michaelg
 *
 */
public class OmniUPBLink extends OmniPartBase {
	
	public enum LinkState {UNKNOWN, ACTIVE, INACTIVE};
	
	private LinkState last_state = LinkState.UNKNOWN;
	/**
	 * @param number  The Link number
	 */
	public OmniUPBLink(int number) {
		super(number);
	}

	/**
	 * @return the last_state
	 */
	public LinkState getLastState() {
		return last_state;
	}
	
	public void rawStatusChange( Integer newVal) {
		
	}
	public void sendSetState( boolean activate) {
		notify( new UPBActionMessage(number, activate?ChangeType.ACTIVATE:ChangeType.DEACTIVATE, NotifyType.ChangeRequest));
	}
	public void sendSetLevel() {
		notify( new UPBActionMessage(number, ChangeType.SETLEVEL, NotifyType.ChangeRequest));
	}
	
	public enum ChangeType { ACTIVATE, DEACTIVATE, SETLEVEL, FADESTOP }; 
	
	public void updateState(int linkRawVal, NotifyType notifyType) {
		switch (linkRawVal) {
		case 0: { /*off*/
			if (last_state != LinkState.INACTIVE) {
				last_state = LinkState.INACTIVE;
				notify(new UPBChangeMessage(number, notifyType));
			}
			notify(new UPBActionMessage(number,ChangeType.DEACTIVATE, notifyType));
		} break;
		case 1:{ /*on*/
			if (last_state != LinkState.ACTIVE){
				last_state = LinkState.ACTIVE;
				notify(new UPBChangeMessage(number, notifyType));
			}
			notify(new UPBActionMessage(number, ChangeType.ACTIVATE, notifyType));
		} break;
		case 2: {/*set*/
			notify(new UPBActionMessage(number, ChangeType.SETLEVEL, notifyType));
		} break;
		case 3: {/*fade stop*/
			notify(new UPBActionMessage(number, ChangeType.FADESTOP , notifyType));
		}break;
		}
		
	}
	static public class UPBChangeMessage extends OmniNotifyListener.ChangeMessage {
		public UPBChangeMessage(int number, NotifyType notifyType) {
			super(OmniArea.UPBLink, number, notifyType);
		}
	}
	static public class UPBActionMessage extends OmniNotifyListener.ChangeMessage {
		private ChangeType change_type;
		public ChangeType getChangeType() { return change_type; }
		public UPBActionMessage(int number, ChangeType changeType, NotifyType notifyType) {
			super(OmniArea.UPBLink, number, notifyType);
			change_type = changeType;
		}
	}
}
