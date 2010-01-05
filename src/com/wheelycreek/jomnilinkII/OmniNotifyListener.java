/** Notification for the OmniController.
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
