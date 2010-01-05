/**
 * 
 */
package com.wheelycreek.jomnilinkII;

import java.util.Vector;

import com.wheelycreek.jomnilinkII.OmniNotifyListener;
import com.wheelycreek.jomnilinkII.OmniSystem.OmniArea;


/** Omni controller element base class.
 * handles notification mechanism. 
 * @author michaelg
 */
public class OmniPart {
	public final int number;
	public final OmniArea area;
	
	/** Message indicating a name change.
	  */
	public class NameChangeMessage extends OmniNotifyListener.ChangeMessage {
		
		public NameChangeMessage(OmniArea area, int number, OmniNotifyListener.NotifyType notifyType) {
			super(area, number, notifyType);
		}

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
	 * @param changeType
	 * @return
	 */
	protected OmniNotifyListener.ChangeMessage createChangeMessage(OmniNotifyListener.NotifyType notifyType) {
		OmniNotifyListener.ChangeMessage msg = new OmniNotifyListener.ChangeMessage(area, number, notifyType);

		return msg;
	}
	protected OmniNotifyListener.ChangeMessage createNameMessage(OmniNotifyListener.NotifyType notifyType) {
		return new NameChangeMessage(area, number, notifyType);
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
	/** Change the name of a unit.
	 * @param part_name
	 */
	public void setName(String part_name) {
		setName(part_name, OmniNotifyListener.NotifyType.ChangeRequest);
	}
	/**
	 * @param part_name the name to set
	 */
	public void setName(String part_name, OmniNotifyListener.NotifyType notifyType) {
		if (this.part_name != part_name) {
			this.part_name = part_name;
			notify(createNameMessage(notifyType));
		}
	}
	/**
	 * @return the zone_name
	 */
	public String getName() {
		return part_name;
	}

}
// vim: syntax=java.doxygen ts=4 sw=4 noet
