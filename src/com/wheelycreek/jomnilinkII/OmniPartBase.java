package com.wheelycreek.jomnilinkII;

import java.util.Vector;

public class OmniPartBase {
	
	public final int number;
	
	public OmniPartBase(int number) {
		this.number = number;
		notificationListeners = new Vector<OmniNotifyListener>();
	}
	protected Vector<OmniNotifyListener> notificationListeners;
	
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

}
