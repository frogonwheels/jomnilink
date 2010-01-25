/**
 * 
 */
package com.wheelycreek.jomnilinkII.Parts;

import com.digitaldan.jomnilinkII.MessageTypes.CommandMessage;
import com.digitaldan.jomnilinkII.MessageTypes.CommandMessage.MessageLevel;
import com.digitaldan.jomnilinkII.MessageTypes.properties.MessageProperties;
import com.digitaldan.jomnilinkII.MessageTypes.statuses.MessageStatus;
import com.wheelycreek.jomnilinkII.OmniNotifyListener;
import com.wheelycreek.jomnilinkII.OmniPart;
import com.wheelycreek.jomnilinkII.OmniNotifyListener.NotifyType;
import com.wheelycreek.jomnilinkII.OmniSystem.OmniArea;

/** An Omni 'Message' that can be sent on comms or displayed.
 * @author michaelg
 */
public class OmniMessage extends OmniPart {
	enum DisplayStatus {
		Off, Displayed, Unacknowledged ;
		public static DisplayStatus fromRawStatus( int status) {
			switch (status) {
			case 0: return Off;
			case 1: return Displayed;
			case 2: return Unacknowledged;
			default: return null;
			}
		}
	};
	
	/** Message changed Notification message.
	 */
	public class MessageStatusMessage extends OmniNotifyListener.ChangeMessage {
		public MessageStatusMessage(int number, OmniNotifyListener.NotifyType notifyType) {
			super(OmniArea.Msg, number, notifyType);
		}
	}
	
	private DisplayStatus status;
	
	/** Create a Message Change message.
	 */
	protected OmniNotifyListener.ChangeMessage createChangeMessage(OmniNotifyListener.NotifyType notifyType) {
		return new MessageStatusMessage(number, notifyType);
	}


	/** Construct a message object for the given number
	 * @param number
	 */
	public OmniMessage(int number) {
		super(number, OmniArea.Msg );
	}
	
	public DisplayStatus getStatus() {
		return status;
	}
	
	/** Called by controller to update the object from the properties.
	 * @param mprop  Message properties from controller.
	 * @param notifyType  
	 */
	public void update(MessageProperties mprop, NotifyType notifyType) {
		
		updateName(mprop.getName(), notifyType);
	}
	/** Called by controller to update the object from the message status.
	 * @param mstat  Message status property.
	 * @param notifyType
	 */
	public void update(MessageStatus mstat, NotifyType notifyType) {
		updateStatus(DisplayStatus.fromRawStatus( mstat.getStatus()),notifyType);
	}
	
	private void updateStatus( DisplayStatus newStatus, NotifyType notifyType ) {
		if (newStatus != status) {
			this.status = newStatus;
			notify(createChangeMessage(notifyType));
		}
	}
	public enum ShowMethod { Log, Show, ShowLed, ShowLedBeep, Say};
	/** Show this message in the given location.
	 * @param method
	 */
	public void show( ShowMethod method) {
		switch (method) {
		case Log: notifyCmd(CommandMessage.messageLogCmd(number));
		case Say: notifyCmd(CommandMessage.messageSayCmd(number));
		case Show: notifyCmd(CommandMessage.messageDisplayCmd(number, MessageLevel.None));
		case ShowLed: notifyCmd(CommandMessage.messageDisplayCmd(number, MessageLevel.Led));
		case ShowLedBeep: notifyCmd(CommandMessage.messageDisplayCmd(number, MessageLevel.BeepLed));
		}
	}
	/** Dial the phone number provided by the index and say this message.
	 * @param phoneNoIndex
	 */
	public void phoneAndSay( int phoneNoIndex ) {
		notifyCmd(CommandMessage.messagePhoneSayCmd(phoneNoIndex, number));
	}
	/** Send this message over the serial port.
	 * @param serialPort
	 */
	public void sendSerial( int serialPort ) {
		notifyCmd(CommandMessage.messageSerialSendCmd(serialPort, number));
	}

}
