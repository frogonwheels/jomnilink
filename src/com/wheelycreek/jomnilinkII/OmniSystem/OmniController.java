/** Object model for Omni system.
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
package com.wheelycreek.jomnilinkII.OmniSystem;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;

import com.digitaldan.jomnilinkII.Connection;
import com.digitaldan.jomnilinkII.Message;
import com.digitaldan.jomnilinkII.NotificationListener;
import com.digitaldan.jomnilinkII.OmniInvalidResponseException;
import com.digitaldan.jomnilinkII.OmniNotConnectedException;
import com.digitaldan.jomnilinkII.OmniUnknownMessageTypeException;
import com.digitaldan.jomnilinkII.MessageTypes.CommandMessage;
import com.digitaldan.jomnilinkII.MessageTypes.NameData;
import com.digitaldan.jomnilinkII.MessageTypes.ObjectProperties;
import com.digitaldan.jomnilinkII.MessageTypes.ObjectStatus;
import com.digitaldan.jomnilinkII.MessageTypes.OtherEventNotifications;
import com.digitaldan.jomnilinkII.MessageTypes.SecurityCodeValidation;
import com.digitaldan.jomnilinkII.MessageTypes.SystemFeatures;
import com.digitaldan.jomnilinkII.MessageTypes.SystemFormats;
import com.digitaldan.jomnilinkII.MessageTypes.SystemInformation;
import com.digitaldan.jomnilinkII.MessageTypes.SystemStatus;
import com.digitaldan.jomnilinkII.MessageTypes.SystemTroubles;
import com.digitaldan.jomnilinkII.MessageTypes.ZoneReadyStatus;
import com.digitaldan.jomnilinkII.MessageTypes.events.OtherEvent;
import com.digitaldan.jomnilinkII.MessageTypes.events.UserMacroButtonEvent;
import com.digitaldan.jomnilinkII.MessageTypes.properties.AuxSensorProperties;
import com.digitaldan.jomnilinkII.MessageTypes.properties.ButtonProperties;
import com.digitaldan.jomnilinkII.MessageTypes.properties.MessageProperties;
import com.digitaldan.jomnilinkII.MessageTypes.properties.UnitProperties;
import com.digitaldan.jomnilinkII.MessageTypes.properties.ZoneProperties;
import com.digitaldan.jomnilinkII.MessageTypes.statuses.AuxSensorStatus;
import com.digitaldan.jomnilinkII.MessageTypes.statuses.MessageStatus;
import com.digitaldan.jomnilinkII.MessageTypes.statuses.Status;
import com.digitaldan.jomnilinkII.MessageTypes.statuses.UnitStatus;
import com.digitaldan.jomnilinkII.MessageTypes.statuses.ZoneStatus;
import com.wheelycreek.jomnilinkII.OmniNotifyListener;
import com.wheelycreek.jomnilinkII.OmniPart;
import com.wheelycreek.jomnilinkII.OmniPart.NameChangeMessage;
import com.wheelycreek.jomnilinkII.Parts.OmniButton;
import com.wheelycreek.jomnilinkII.Parts.OmniCode;
import com.wheelycreek.jomnilinkII.Parts.OmniDevice;
import com.wheelycreek.jomnilinkII.Parts.OmniFlag;
import com.wheelycreek.jomnilinkII.Parts.OmniMessage;
import com.wheelycreek.jomnilinkII.Parts.OmniOutput;
import com.wheelycreek.jomnilinkII.Parts.OmniRoom;
import com.wheelycreek.jomnilinkII.Parts.OmniSensor;
import com.wheelycreek.jomnilinkII.Parts.OmniUnit;
import com.wheelycreek.jomnilinkII.Parts.OmniZone;
import com.wheelycreek.jomnilinkII.Parts.OmniCode.UserLevel;



/** Root of object model for an Omni controller.
 *
 * @author michaelg
 */
public class OmniController implements OmniNotifyListener {
	public static void main(String[] args) {
		OmniController c = new OmniController();
		c.setDebugChan(dcSensors | dcZones /*dcMessage*/ |dcChildMessage, true);
		runMain(args, c, true);
	}
	public static void runMain(String[] args, OmniController c, boolean keepKey) {
		
		if(args.length != 3){
			System.out.println("Usage: "+c.getClass().getCanonicalName()+"  host port encKey");
			System.exit(-1);
		}
		String host  = args[0];
		int port = Integer.parseInt(args[1]);
		String key = args[2];
		
		try {
			c.connectTo(host, port, key, keepKey);
			c.reloadProperties();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	/** Debug channels (all) */
	public static final int dcAll = 0x3f;
	/** Debug channel for connections */
	public static final int dcConnection = 0x1;
	/** Debug channel for messages */
	public static final int dcMessage = 0x2;
	/** Debug channel for zones*/
	public static final int dcZones = 0x4;
	/** Debug channel for child messages */
	public static final int dcChildMessage = 0x8;
	/** Debug channel for sensors */
	public static final int dcSensors = 0x10;
	/** Debug channel for units*/
	public static final int dcUnits = 0x20;
	/** Debug channel for messages*/
	public static final int dcMsgs = 0x40;
	private int debug_channels;

	/** Check all the specified debug channels are set.
	  */
	public boolean getDebugChan( int channels ) {
		return (debug_channels & channels) == channels;
	}
	/** Set the specified debug channels.
	  */
	public void setDebugChan( int channels, boolean newVal) {
		if (newVal)
			debug_channels |= channels;
		else
			debug_channels &= ~channels;
		if (((channels & dcConnection) == dcConnection) && (omni != null))
			omni.debug = newVal;
	}
	/** Connection to the host.
	 */
	protected Connection omni;
	/** The specified omni host.
	 */
	private String omni_host;
	/** The specified omni port.
	 */
	private int omni_port;
	/** The current key (for use with reconnect)
	 */
	private String omni_key;

	// Collections of names. Used for doing lookups.
	protected SortedMap<OmniArea, Vector<String> > names;

	// Collections of Omni parts.
	protected SortedMap<Integer, OmniZone> zones;
	protected SortedMap<Integer, OmniSensor> sensors;
	protected SortedMap<Integer, OmniUnit> units;
	protected SortedMap<Integer, OmniOutput> outputs;
	protected SortedMap<Integer, OmniDevice> devices;
	protected SortedMap<Integer, OmniRoom> rooms;
	protected SortedMap<Integer, OmniFlag> flags;
	protected SortedMap<Integer, OmniButton> buttons;
	protected SortedMap<Integer, OmniMessage> messages;
	
	// Various one-off bits of system information.
	protected SystemFeatures    sys_features;
	protected SystemFormats     sys_formats;
	protected SystemInformation sys_info;
	protected SystemStatus      sys_status;
	protected SystemTroubles    sys_troubles;
	protected ZoneReadyStatus   zones_ready;
	
	/** Construct required arrays.
	  * Called by constructors.
	  */
	private void constructArrays() {
		notificationListeners = new Vector<OmniNotifyListener>();
		zones   = new TreeMap<Integer, OmniZone>();
		sensors = new TreeMap<Integer, OmniSensor>();
		units   = new TreeMap<Integer, OmniUnit>();
		outputs = new TreeMap<Integer, OmniOutput>();
		devices = new TreeMap<Integer, OmniDevice>();
		rooms   = new TreeMap<Integer, OmniRoom>();
		flags   = new TreeMap<Integer, OmniFlag>();
		buttons = new TreeMap<Integer, OmniButton>();
		messages = new TreeMap<Integer, OmniMessage>();
	}
	
	/** Construct an omni controller.
	 * @see connectTo  to create the connection.
	  */
	public OmniController() {
		constructArrays();
	}
	
	/** Connect to a controller.
	 * @param host The name of the host
	 * @param port The name of the host
	 * @param key  The secret key to use
	 * @param keepKey True to keep the key for reconnect.
	 * @throws Exception 
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public void connectTo(String host, int port, String key, boolean keepKey) throws UnknownHostException, IOException, Exception {
		createConnection(host, port, key);
		this.omni_host = host;
		if (keepKey) {
			this.omni_port = port;
			this.omni_key = key;
		} else {
			this.omni_port = 0;
			this.omni_key = "";
		}
	}
	/** Override-able Method called when omni is successfully connected.  
	 * @param reconnect Called with true if this is a 'reconnect' scenario.
	 */
	protected void connected( boolean reconnect ) {
		try {
			if (reconnect)
				reloadStatus();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OmniNotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OmniInvalidResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OmniUnknownMessageTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/** Create a connection to an omni.
	  */
	protected void createConnection(String host, int port, String key) throws UnknownHostException, IOException, Exception{
		boolean reconnect=(omni != null);
		omni = new Connection(host, port, key);
		omni.debug = getDebugChan(dcConnection);
		omni.addNotificationListener(new NotificationListener(){
			@Override
			public void objectStausNotification(ObjectStatus s) { statusNotify(s); }
			@Override
			public void otherEventNotification(OtherEventNotifications o) {	otherEventNotify(o);}
		});
		omni.enableNotifications();
		
		connected(reconnect);
	}
	/** Reconnect to the omni if allowed.
	  */
	protected  boolean reconnect() throws UnknownHostException, IOException, Exception {
		if (omni_key == null)
			return false;
		else {
			if (omni.connected())
				omni.disconnect();
			createConnection(omni_host,omni_port,omni_key);
			// TODO: Reload the status properties. 
			
			return omni.connected();
			
		}
	}
	/** Get the capacity of an omni area.
	 * @param area
	 * @return The number of objects of the specified type.
	 */
	protected int getCapacity( OmniArea area) throws IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException {
		// Capacity for sensors is the same for zones. It's just about the types.
		// (Querying for sensors will result in an error).
		if (area == OmniArea.Sensor)
			area = OmniArea.Zone;
		return omni.reqObjectTypeCapacities(area.get_objtype_msg()).getCapacity();
	}
	
	/** Reload all properties (including names and types).
	 * @throws Exception 
	 * @throws OmniUnknownMessageTypeException 
	 * @throws OmniInvalidResponseException 
	 * @throws OmniNotConnectedException 
	 * @throws IOException 
	  */
	public void reloadProperties() throws IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException, Exception {
		loadZones();
		loadSensors();
		loadUnits();
		loadButtons();
		loadMessages();
	}
	/** Reload the status for the parts.
	 * @throws OmniUnknownMessageTypeException 
	 * @throws OmniInvalidResponseException 
	 * @throws OmniNotConnectedException 
	 * @throws IOException 
	  */
	public void reloadStatus() throws IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException {
		updateZones();
		updateSensors();
		updateUnits();
		updateOutputs();
		updateDevices();
		updateRooms();
		updateFlags();
		updateMessages(null);
	}
	
	/** Receive status notifications from the communications layer.
	 * @param s The status object for the area.
	 */
	protected void statusNotify(ObjectStatus s) {
		OmniArea area = OmniArea.fromMessageType(s.getStatusType());
		if (getDebugChan(dcMessage)) {
			System.out.println( area.toString()+" changed");
			System.out.println(s.toString());
		}
				
		switch (area) {
		case Area:
			break;
		case AudioZone:
			break;
		case Sensor: {
			Status status[]  = s.getStatuses();
			for (int i=0; i< status.length; ++i) {
				AuxSensorStatus ass = (AuxSensorStatus)status[i];
				sensorStatusReceive(ass);
			}
		} break;
		case ExpEnclosure:
			break;
		case Msg: {
			Status status[] = s.getStatuses();
			for (int i=0; i < status.length; ++i) {
				MessageStatus ms = (MessageStatus)status[i];
				messageStatusReceive(ms);
			}
			
		} break;
		case Thermo:
			break;
		case Unit: {
			Status status[]	= s.getStatuses();

			for (int i = 0; i< status.length; ++i) {
				UnitStatus zs = (UnitStatus) status[i];
				unitStatusReceive(zs);
			}
		}
		break;
		case Zone: {
			Status status[] = s.getStatuses();

			for (int i = 0; i< status.length; ++i) {
				ZoneStatus zs = (ZoneStatus) status[i];
				zoneStatusReceive(zs);
			}
		}
		break;
		default:
			System.out.println("Unknown type " + s.getStatusType());
		break;
		}
	}
	
	/** Receive a Unit status change message.
	 * @param status
	 */
	private void unitStatusReceive(UnitStatus status) {
		if (getDebugChan(dcUnits))
			System.out.println("Unit Changed: "+status.toString());
		
		OmniUnit unit = getUnit(status.getNumber());
		if (unit != null)
			unit.update(status, NotifyType.Notify);
	}

	/** Receive a sensor status change.
	  */
	private void sensorStatusReceive(AuxSensorStatus status) {
		
		if (getDebugChan(dcSensors))
			System.out.println("Sensor Changed: "+status.toString());

		OmniSensor sensor = sensors.get(status.getNumber());
		if (sensor != null)
			sensor.update(status, NotifyType.Notify);
		
	}
	/** Receive a message status change.
	 */
	private void messageStatusReceive(MessageStatus status) {
		
		if (getDebugChan(dcMsgs))
			System.out.println("message Changed: "+status.toString());

		OmniMessage message = messages.get(status.getNumber());
		if (message != null)
			message.update(status, NotifyType.Notify);
		
	}	
	
	/** Receive a list 'other event' notification.
	  * calls otherEventReceive for each one.
	  */
	protected void otherEventNotify(OtherEventNotifications o) {
		for(int k=0;k<o.Count();k++){
			otherEventReceive(o.getNotification(k));
		}
	}
	/** Get an omni part given area and number.
	 * 
	 * @param area
	 * @return
	 * @throws Exception 
	 * @throws OmniNotConnectedException 
	 */
	public OmniPart getPart(OmniArea area, int objNumber) throws OmniNotConnectedException, Exception {
		switch (area) {
		case Zone: return getZone(objNumber);
		case Unit: return getUnit(objNumber);
		case Sensor: return getSensor(objNumber);
		case Button: return getButton(objNumber);
		default: return null;
		}
	}
	
	
	/** get a zone, load it with information.
	 * @param zonenr The zone to load.
	 * @return a loaded zone.
	 * @throws Exception 
	 * @throws OmniNotConnectedException 
	 * @throws OmniUnknownMessageTypeException 
	 * @throws OmniInvalidResponseException 
	 * @throws IOException 
	 */
	public OmniZone getZone(int zonenr) throws OmniNotConnectedException, IOException, OmniInvalidResponseException, OmniUnknownMessageTypeException {
		OmniZone result = zones.get(zonenr);
		if (result == null) {
			// Build a new 
			loadZones(zonenr, zonenr);
			result = zones.get(zonenr);
		}
		return result;
	}
	/** Get a Zone by name.
	 * @throws Exception 
	 * @throws OmniNotConnectedException 
	 */
	public OmniZone getZone( String name) throws OmniNotConnectedException, Exception {
		return getByName(name, OmniArea.Zone, zones);
	}
	
	
	@SuppressWarnings("unchecked")
	protected < T extends OmniPart > T getByName( String name, OmniArea area, SortedMap<Integer, T> partmap) throws OmniNotConnectedException, Exception {
		T part = findNameInMap(name, partmap);
		if (part != null)
			return part;
		return (T)findInPartNames(name, area);
	}
	/** 
	 * @param name
	 * @param area
	 * @return
	 * @throws OmniNotConnectedException
	 * @throws Exception
	 */
	private OmniPart findInPartNames(String name, OmniArea area)
			throws OmniNotConnectedException, Exception {
		Vector<String> namelist = get_vectors(area);
		for (int idx = 0; idx < namelist.size(); ++idx)
			if (namelist.get(idx).equalsIgnoreCase(name)) {
				// Get the part. We can't check if the returned type is a 'T' because of type erasure!
				return getPart(area, idx);
			}
	
		return null;
	}
	/** Find a name in a sorted map
	 * @param name    String to search
	 * @param partmap  SortedMap of OmniPart to search in.
	 * @return The found part or null.
	 */
	protected <T extends OmniPart> T findNameInMap(String name, SortedMap<Integer, T> partmap) {
		Iterator<T> iter = partmap.values().iterator();
		while (iter.hasNext()) {
			T part = iter.next();
			if (part != null && part.getName().equalsIgnoreCase(name) ) {
				return part;
			}
		}
		return null;
	}

	/** Load all available zones as objects.
	 * @throws OmniUnknownMessageTypeException 
	 * @throws OmniInvalidResponseException 
	 * @throws OmniNotConnectedException 
	 * @throws IOException 
	  */
	protected void loadZones() throws IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException {
		loadZones(1,0);
	}
	protected  Set<Entry<Integer, OmniZone>> getZonesSet() throws IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException {
		if (zones.isEmpty())
			loadZones();
		return zones.entrySet();
	}
	
	/** Load a range of zones as objects
	 * @throws OmniUnknownMessageTypeException 
	 * @throws OmniInvalidResponseException 
	 * @throws OmniNotConnectedException 
	 * @throws IOException 
	  */
	protected void loadZones(int startZone, int endZone) throws IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException{
		
		if (endZone <= 0)
			endZone = getCapacity(OmniArea.Zone);
		else if (endZone < startZone)
			endZone =  startZone;

		int objnum = startZone-1;
		Message m;
		while((m = omni.reqObjectProperties(Message.OBJ_TYPE_ZONE, objnum, 1, 
				ObjectProperties.FILTER_1_NAMED, ObjectProperties.FILTER_2_AREA_ALL, ObjectProperties.FILTER_3_ANY_LOAD)).getMessageType() 
				== Message.MESG_TYPE_OBJ_PROP){
			ZoneProperties zp = (ZoneProperties)m;
			objnum = zp.getNumber();
			OmniZone zone = zones.get(objnum);
			if (zone == null) {
				zone = new OmniZone(objnum);
				zones.put(objnum, zone);
				zone.addNotificationListener(this);
			}
			zone.update(zp, NotifyType.Initial);
		}
	}

	/** Update status of all loaded zones.
	  */
	protected void updateZones() throws IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException {
		ObjectStatus status = omni.reqObjectStatus(OmniArea.Zone.get_objtype_msg(), 1,getCapacity(OmniArea.Zone));
		ZoneStatus [] zonestats = (ZoneStatus[])status.getStatuses();
		for (ZoneStatus zonestat : zonestats) {
			int zoneidx = zonestat.getNumber();
			OmniZone zone = zones.get(zoneidx);
			if (zone != null)
				zone.update(zonestat, NotifyType.Notify );
		}
	}
	
	/** Receive a zone status.
	  */
	protected void zoneStatusReceive( ZoneStatus status) {
		if (getDebugChan(dcZones))
			System.out.println("Zone Changed: "+status.toString());
		OmniZone zone = zones.get(status.getNumber());
		if (zone != null)
			zone.update(status,NotifyType.Notify);
	}

	/** Get a sensor.
	  load if necessary.
	  */
	public OmniSensor getSensor(int sensorNo) throws Exception, IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException {
		OmniSensor ret=sensors.get(sensorNo);
		if (ret == null) {
			loadSensors(sensorNo,sensorNo);
			ret = sensors.get(sensorNo);
		}
		return ret;
	}
	
	/** Get a Sensor by name.
	 * @throws Exception 
	 * @throws OmniNotConnectedException 
	 */
	public OmniSensor  getSensor( String name) throws OmniNotConnectedException, Exception {
		return getByName(name, OmniArea.Sensor, sensors);
	}
	/** Load all available (named) sensors.
	 */
	protected void loadSensors() throws Exception, IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException {
		loadSensors(1,-1);
	}
	protected void loadSensors(int fromObj, int toObj) throws Exception, IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException {
		int objnum = fromObj-1;
		if (objnum < 0) objnum = 0;
		Message m;
		while((m = omni.reqObjectProperties(Message.OBJ_TYPE_AUX_SENSOR, objnum, 1, 
				ObjectProperties.FILTER_1_NAMED, ObjectProperties.FILTER_2_AREA_ALL, ObjectProperties.FILTER_3_NONE)).getMessageType() 
				== Message.MESG_TYPE_OBJ_PROP){
			AuxSensorProperties op = (AuxSensorProperties)m; 	
			objnum = op.getNumber();
			OmniSensor sensor= sensors.get(objnum);
			if (sensor == null) {
				sensor = new OmniSensor(objnum);
				sensors.put(objnum, sensor);
				sensor.addNotificationListener(this);
				sensor.update(op, NotifyType.Initial);
			}
			if (toObj > 0 && objnum >= toObj)
				break;
		}
		
	}
	
	/** Update the status of all loaded sensors.
	 */
	protected void updateSensors() throws IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException {
		// Update all sensor values.
		Iterator<OmniSensor> iter = sensors.values().iterator();
		while (iter.hasNext()) {
			OmniSensor sense = iter.next();
			if (sense != null) {
				ObjectStatus status = omni.reqObjectStatus(OmniArea.Sensor.get_objtype_msg(), sense.number,sense.number);
				AuxSensorStatus [] sensorstats = (AuxSensorStatus[])status.getStatuses();
				if (sense.number == sensorstats[0].getNumber())
					sense.update(sensorstats[0], NotifyType.Notify);
			}
		}
	}
	
	/** Get at a unit object.
	  This includes outputs, rooms, devices and flags.
	  */
	public OmniUnit getUnit(int unitNo) {
		OmniUnit ret = units.get(unitNo);
		if (ret == null) {
			ret = outputs.get(unitNo);
			if (ret == null) {
				ret = rooms.get(unitNo);
				if (ret == null ) {
					ret = devices.get(unitNo);
					if (ret == null ) {
						ret = flags.get(unitNo);
					}
				}
			}
		}
		return ret;
	}
	/** Get a unit by  name.
	 * @param name  Name of the unit.
	 * @return
	 * @throws OmniNotConnectedException
	 * @throws Exception
	 */
	public OmniUnit getUnit( String name) throws OmniNotConnectedException, Exception {
		OmniPart unit;
		unit = findInPartNames(name, OmniArea.Unit);
		if (unit instanceof OmniUnit)
			return (OmniUnit)unit;
		return null;
	}
	
	/** Get an Output object by number.
	  */
	public OmniOutput getOutput(int outputNo) throws IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException {
		OmniOutput ret=outputs.get(outputNo);
		if (ret == null) {
			loadUnits(outputNo,outputNo);
			ret = outputs.get(outputNo);
		}
		return ret;
	}

	/** Get a Room object by number.
	 * @throws OmniUnknownMessageTypeException 
	 * @throws OmniInvalidResponseException 
	 * @throws OmniNotConnectedException 
	 * @throws IOException 
	  */
	public OmniRoom getRoom(int roomNo) throws IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException {
		OmniRoom ret=rooms.get(roomNo);
		if (ret == null) {
			loadUnits(roomNo,roomNo);
			ret = rooms.get(roomNo);
		}
		return ret;
	}
	public OmniRoom getRoom(String name) throws OmniNotConnectedException, Exception {
		OmniRoom room = findNameInMap(name, rooms);
		if (room != null)
			return room;
		OmniPart part = findInPartNames(name, OmniArea.Unit);
		if (part instanceof OmniRoom)
			return (OmniRoom)part;
		else
			return null;
	}
	
	public Iterator<Entry<Integer, OmniDevice>> getDeviceIter() throws IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException {
		if (devices.isEmpty() && units.isEmpty())
			loadUnits();
		return devices.entrySet().iterator();
	}
	
	/** Get a Device object by number.
	 * @throws OmniUnknownMessageTypeException 
	 * @throws OmniInvalidResponseException 
	 * @throws OmniNotConnectedException 
	 * @throws IOException 
	  */
	public OmniDevice getDevice(int deviceNo) throws IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException {
		OmniDevice ret=devices.get(deviceNo);
		if (ret == null) {
			loadUnits(deviceNo,deviceNo);
			ret = devices.get(deviceNo);
		}
		return ret;
	}
	/** Get a device by name.
	 * @param name  Name to find
	 * @return  Device object of that name.
	 * @throws OmniNotConnectedException
	 * @throws Exception
	 */
	public OmniDevice getDevice(String name) throws OmniNotConnectedException, Exception {
		OmniDevice device = findNameInMap(name, devices);
		if (device != null)
			return device;
		OmniPart part = findInPartNames(name, OmniArea.Unit);
		if (part instanceof OmniDevice)
			return (OmniDevice)part;
		else
			return null;
	}
	/** Get a flag object by number.
	 * @throws OmniUnknownMessageTypeException 
	 * @throws OmniInvalidResponseException 
	 * @throws OmniNotConnectedException 
	 * @throws IOException 
	  */
	public OmniFlag getFlag(int flagNo) throws IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException {
		OmniFlag ret=flags.get(flagNo);
		if (ret == null) {
			loadUnits(flagNo,flagNo);
			ret = flags.get(flagNo);
		}
		return ret;
	}
	public OmniFlag getFlag(String name) throws OmniNotConnectedException, Exception {
		OmniFlag flag = findNameInMap(name, flags);
		if (flag != null)
			return flag;
		OmniPart part = findInPartNames(name, OmniArea.Unit);
		if (part instanceof OmniFlag)
			return (OmniFlag)part;
		else
			return null;
	}
	
	/** Load all units.
	  */
	protected void loadUnits() throws IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException {
		loadUnits(1,-1);
	}
	
	/** Load  a range of unit objects.
	    Includes all types of units.
	  */
	protected void loadUnits( int fromUnit, int toUnit ) throws IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException {
		int objnum = fromUnit-1;
		if (objnum < 0) objnum = 0;
		Message m;
		// Get initial properties
		while((m = omni.reqObjectProperties(OmniArea.Unit.get_objtype_msg(), objnum, 1, 
				ObjectProperties.FILTER_1_NAMED, ObjectProperties.FILTER_2_AREA_ALL, ObjectProperties.FILTER_3_ANY_LOAD)).getMessageType() 
				== Message.MESG_TYPE_OBJ_PROP){
			UnitProperties uprop = (UnitProperties)m;
			objnum = uprop.getNumber();
			OmniUnit unit = null;
			switch (OmniUnit.UnitType.typeAsEnum(uprop.getUnitType())) {
				case UPB:
				case HLCLoad:
				case RadioRA:
				case ViziaRFLoad:
				case CentraLite: {
					OmniDevice device = devices.get(objnum);
					if (device == null) {
						device = new OmniDevice(objnum);
						devices.put(objnum, device);
						device.addNotificationListener(this);
					}
					unit = device;
				} break;
				case Output: {
					OmniOutput output = outputs.get(objnum);
					if (output == null) {
						output = new OmniOutput(objnum);
						outputs.put(objnum, output);
						output.addNotificationListener(this);
					}
					unit = output;
				}break;
				case HLCRoom:
				case ViziaRFRoom:{
					OmniRoom room = rooms.get(objnum);
					if (room == null) {
						room = new OmniRoom(objnum);
						rooms.put(objnum, room);
						room.addNotificationListener(this);
					}
					unit = room;
				} break;
				case Flag: {
					OmniFlag flag = flags.get(objnum);
					if (flag == null) {
						flag = new OmniFlag(objnum);
						flags.put(objnum, flag);
						flag.addNotificationListener(this);
					}
					unit = flag;
				} break;		
				case AudioZone:
				case AudioSource: 
				default:{
					unit = units.get(objnum);
					if (unit == null) {
						unit = new OmniUnit(objnum);
						units.put(objnum, unit);
						unit.addNotificationListener(this);
					}
				}
			}
			if (unit != null)
				unit.update(uprop, NotifyType.Initial);
			if (toUnit > 0 && objnum >= toUnit)
				break;
		}
	}
	

	/** Update the status of all loaded units.
	 * @throws OmniUnknownMessageTypeException 
	 * @throws OmniInvalidResponseException 
	 * @throws OmniNotConnectedException 
	 * @throws IOException 
	 */	
	private <T extends OmniUnit>  void updateUnitCollection( SortedMap<Integer, T> unitsmap ) throws IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException  {
		Iterator<T> iter = unitsmap.values().iterator();
		while (iter.hasNext()) {
			OmniUnit unit = iter.next();
			if (unit != null) {
				ObjectStatus status = omni.reqObjectStatus(OmniArea.Unit.get_objtype_msg(), unit.number, unit.number);
				UnitStatus [] unitstats = (UnitStatus[])status.getStatuses();
				if (unit.number == unitstats[0].getNumber())
					unit.update(unitstats[0], NotifyType.Notify);
			}
		}
	}
	public void updateUnits() throws IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException {
		updateUnitCollection( units);
	}
	public void updateOutputs() throws IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException {
		updateUnitCollection(outputs);
	}
	public void updateDevices() throws IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException {
		updateUnitCollection(devices);
	}
	public void updateRooms() throws IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException {
		updateUnitCollection(rooms);
	}
	public void updateFlags() throws IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException {
		updateUnitCollection(flags);
	}
	
	/** get a macro button object.
	 * @param buttonNo
	 * @return a loaded button object.
	 * @throws Exception 
	 * @throws OmniNotConnectedException 
	 */
	public OmniButton getButton(int buttonNo) throws OmniNotConnectedException, Exception {
		OmniButton result = buttons.get(buttonNo);
		if (result == null && buttonNo >= 1) {
			// Build a new 
			loadButtons(buttonNo, buttonNo);
			result = buttons.get(buttonNo);
		}
		return result;
	}
	/** Get a Button by name.
	 * @param name Name of button to get.
	 * @throws Exception 
	 * @throws OmniNotConnectedException 
	 */
	public OmniButton getButton(String name) throws OmniNotConnectedException, Exception {
		return getByName(name, OmniArea.Button, buttons);
	}
	/** Load buttons.
	  */
	protected void loadButtons() throws IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException {
		loadButtons(1,-1);
	}
	

	/** Load a range of buttons.
	  */
	protected void loadButtons(int objFrom, int objTo) throws IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException {
		int objnum = objFrom-1;
		Message m;
		// Get initial button properties
		while((m = omni.reqObjectProperties(OmniArea.Button.get_objtype_msg(), objnum, 1, 
				ObjectProperties.FILTER_1_NAMED, ObjectProperties.FILTER_2_AREA_ALL, ObjectProperties.FILTER_3_NONE)).getMessageType() 
				== Message.MESG_TYPE_OBJ_PROP){
			ButtonProperties bprop = (ButtonProperties)m;
			objnum = bprop.getNumber();
			OmniButton button = buttons.get(objnum);
			if (button == null) {
				button = new OmniButton(objnum);
				button.updateName(bprop.getName(), NotifyType.Initial);
				buttons.put(objnum, button);
				button.addNotificationListener(this);
			}

			if (objTo > 0 && objnum >= objTo )
				break;
		}
	}

	/** get a macro Message object.
	 * @param MessageNo
	 * @return a loaded Message object.
	 * @throws Exception 
	 * @throws OmniNotConnectedException 
	 */
	public OmniZone getMessage(int messageNo) throws OmniNotConnectedException, Exception {
		OmniZone result = zones.get(messageNo);
		if (result == null) {
			// Build a new 
			loadMessages(messageNo, messageNo);
			result = zones.get(messageNo);
		}
		return result;
	}
	/** Get a Message by name.
	 * @param name Name of message to get.
	 * @throws Exception 
	 * @throws OmniNotConnectedException 
	 */
	public OmniMessage getMessage(String name) throws OmniNotConnectedException, Exception {
		return getByName(name, OmniArea.Msg, messages);
	}
	/** Load messages.
	  */
	protected void loadMessages() throws IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException {
		loadMessages(1,-1);
	}
	

	/** Load a range of messages.
	  */
	protected void loadMessages(int objFrom, int objTo) throws IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException {
		int objnum = objFrom-1;
		Message m;
		// Get initial message properties
		while((m = omni.reqObjectProperties(OmniArea.Msg.get_objtype_msg(), objnum, 1, 
				ObjectProperties.FILTER_1_NAMED, ObjectProperties.FILTER_2_AREA_ALL, ObjectProperties.FILTER_3_NONE)).getMessageType() 
				== Message.MESG_TYPE_OBJ_PROP){
			MessageProperties mprop = (MessageProperties)m;
			objnum = mprop.getNumber();
			OmniMessage message = messages.get(objnum);
			if (message == null) {
				message = new OmniMessage(objnum);
				messages.put(objnum, message);
				message.addNotificationListener(this);
			}
			message.update(mprop, NotifyType.Initial);
			// for some reason message properties don't contain their current state. 
			updateMessage(message, NotifyType.Initial);
			if (objTo > 0 && objnum >= objTo )
				break;
		}
		
	}
	
	/** Update the status of all loaded sensors.
	 */
	protected void updateMessages() throws IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException {
		updateMessages(NotifyType.Notify);	
		
	}
	/** Update the status of all loaded sensors.
	 * @param notifyType The notification type.
	 */
	protected void updateMessages(NotifyType notifyType) throws IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException {
		// Update all sensor values.
		Iterator<OmniMessage> iter = messages.values().iterator();
		while (iter.hasNext()) {
			OmniMessage msgObj = iter.next();
			if (msgObj != null) 
				updateMessage(msgObj, notifyType );
		}
	}
	/**  Update the status a single message object.
	 * @param notifyType
	 * @param msgObj
	 * @throws IOException
	 * @throws OmniNotConnectedException
	 * @throws OmniInvalidResponseException
	 * @throws OmniUnknownMessageTypeException
	 */
	private void updateMessage( OmniMessage msgObj, NotifyType notifyType)
			throws IOException, OmniNotConnectedException,
			OmniInvalidResponseException, OmniUnknownMessageTypeException {
		ObjectStatus status = omni.reqObjectStatus(OmniArea.Msg.get_objtype_msg(), msgObj.number,msgObj.number);
		MessageStatus [] sensorstats = (MessageStatus[])status.getStatuses();
		if (msgObj.number == sensorstats[0].getNumber())
			msgObj.update(sensorstats[0], notifyType);
	}

	/** Receive a single 'OtherEvent' type message.
	  */
	protected void otherEventReceive( OtherEvent event) {
		if (getDebugChan(dcMessage))
			System.out.println(event.toString());	
		switch (event.getEventType()) {
			case UserMacroButton: {
				OmniButton ob = buttons.get(((UserMacroButtonEvent)event).getButtonNumber());
				if (ob != null) {
					ob.notifyPress();
				}
			}
			case ProlinkMessage:
			case CentraliteSwitch:
			case Alarm:
			case ComposeCode:
			case X10Code:
			case SecurityArming:
			case LumniaModeChange:
			case UnitSwitchPress:
			case UPBLink:
			case AllSwitch:
			case PhoneLine:
			case Power:
			case DCM:
			case EnergyCost:
				break;
		}
	}
	/** load up a vector with area name data.
	 * @param area  Area the names are for
	 * @param list  List to populate 
	 * @param reload  Force reload of whole lot.
	 */
	protected void load_name_vector(OmniArea area, Vector<String> list, boolean reload) throws IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException {
		int max_number = getCapacity(area);
		int first= reload?list.size():0;
		list.setSize(max_number);
		for(int i = first; i < max_number; ++i) {
			Message msg = omni.receiveName(area.get_objtype_msg(), i);
			if (msg instanceof NameData ) {
				NameData nameMsg = (NameData)msg;
				list.set(i, nameMsg.getName());
			}
		}
	}
	protected Vector<String> create_loaded_name_vector(OmniArea area) throws OmniNotConnectedException, Exception {
		Vector<String> result = new Vector<String>();
		try {
			load_name_vector(area, result, false);
		} catch (OmniNotConnectedException e) {
			// connect again?
			if (!reconnect())
				throw e;
			
		} catch (OmniUnknownMessageTypeException e) {
			// Really shouldn't get this
			e.printStackTrace();
		}
		return result;
	}
	
	/** Get at vectors of names, loaded with names.
	 * This allows access to just the names.
	 * @param area
	 * @return
	 * @throws OmniNotConnectedException
	 * @throws Exception
	 */
	protected Vector<String> get_vectors(OmniArea area) throws OmniNotConnectedException, Exception {
		Vector<String> strings = null; 
		if (names == null)
			names = new TreeMap< OmniArea, Vector<String> >();
		else
			strings = names.get(area);
		if (strings == null) {
			strings = create_loaded_name_vector(area);
			names.put(area, strings);
		}
		return strings;
	}

	/** Update the name.
	 *  
	 * @param area  Which name to update
	 * @param index Index of item (1 based).
	 * @param name  New name of item
	 * @throws OmniNotConnectedException
	 * @throws Exception
	 */
	protected void setName( OmniArea area, int index, String name ) throws OmniNotConnectedException, Exception {
		boolean force = false;

		// Update the part if possible.
		OmniPart part = getPart(area, index);
		if (part == null)
			force = true;
		else
			part.setName(name);

		// Now update the vectors.
		Vector<String> vectors = null;
		if (force)
			vectors = get_vectors(area);
		else if (names != null)
			vectors = names.get(area);
		
		if (vectors != null) {
			if (index >= vectors.size())
				vectors.setSize(index);
			vectors.set(index-1, name);
		}
		
	}
	
	/** Get the name of an Omni Part. 
	 * @param area  The part type
	 * @param index  The part # (from 1)
	 * @return 
	 * @throws OmniNotConnectedException
	 * @throws Exception
	 */
	public String getName(OmniArea area, int index) throws OmniNotConnectedException, Exception {
		if (index < 0)
			return null;
		OmniPart part = getPart(area, index);
		
		if (part != null)
			return part.getName();
		
		Vector<String> vectors = get_vectors(area);
		if (vectors != null && (index <= vectors.size() ))
			return vectors.get(index-1);
		else
			return String.format("%s %d", area.name(), index);
	}
	

	public String getZoneName(int index) throws OmniNotConnectedException, Exception {
		return getName(OmniArea.Zone, index);
	}
	public String getUnitName(int index) throws OmniNotConnectedException, Exception {
		return getName(OmniArea.Unit, index);
	}
	public String getAreaName(int index) throws OmniNotConnectedException, Exception {
		return getName(OmniArea.Area, index);
	}

	/** Get at various system features information.
	 * @return the sys_features
	 * @throws OmniUnknownMessageTypeException 
	 * @throws OmniInvalidResponseException 
	 * @throws OmniNotConnectedException 
	 * @throws IOException 
	 */
	public SystemFeatures getFeatures() throws IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException {
		if (sys_features == null)
			sys_features = omni.reqSystemFeatures();
		return sys_features;
	}

	/**
	 * @return the sys_formats
	 * @throws OmniUnknownMessageTypeException 
	 * @throws OmniInvalidResponseException 
	 * @throws OmniNotConnectedException 
	 * @throws IOException 
	 */
	public SystemFormats getFormats() throws IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException {
		if (sys_formats == null)
			sys_formats = omni.reqSystemFormats();
		return sys_formats;
	}

	/**
	 * @return the sys_info
	 * @throws OmniUnknownMessageTypeException 
	 * @throws OmniInvalidResponseException 
	 * @throws OmniNotConnectedException 
	 * @throws IOException 
	 */
	public SystemInformation getInfo() throws IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException {
		if (sys_info == null)
			sys_info = omni.reqSystemInformation();
		return sys_info;
	}

	/**
	 * @return the sys_status
	 * @throws OmniUnknownMessageTypeException 
	 * @throws OmniInvalidResponseException 
	 * @throws OmniNotConnectedException 
	 * @throws IOException 
	 */
	public SystemStatus getStatus() throws IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException {
		if (sys_status == null)
			sys_status = omni.reqSystemStatus();
		return sys_status;
	}

	/**
	 * @return the sys_troubles
	 * @throws OmniUnknownMessageTypeException 
	 * @throws OmniInvalidResponseException 
	 * @throws OmniNotConnectedException 
	 * @throws IOException 
	 */
	public SystemTroubles getTroubles() throws IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException {
		if (sys_troubles == null)
			sys_troubles = omni.reqSystemTroubles();
		return sys_troubles;
	}
	private void initZoneReady() throws Exception, IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException {
		
		try {
			zones_ready = omni.reqZoneReadyStatus();
		} catch (OmniNotConnectedException e) {
			if (reconnect())
				zones_ready = omni.reqZoneReadyStatus();
		}
	}
	/**
	 * @return the zones_ready
	 * @throws OmniUnknownMessageTypeException 
	 * @throws OmniInvalidResponseException 
	 * @throws OmniNotConnectedException 
	 * @throws IOException 
	 */
	public boolean getZoneReady( int zone) throws Exception, IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException {
		if (zones_ready == null)
			initZoneReady();
		
		return (zones_ready.getZoneReady(zone));
	}

	
	public OmniCode validateSecurity( int area, String code) throws OmniNotConnectedException {
		if (code == null || code.length() != 4)
			return OmniCode.INVALID_USER;
		
		int codes[] = new int[4];
		
		for (int idx = 0; idx < 4 ; ++idx) {
			int chDiff = code.charAt(idx) - '0';
			codes[idx] = chDiff;
			if (chDiff < 0 || chDiff > 9)
				return OmniCode.INVALID_USER;
		}
		try {
			SecurityCodeValidation validate = omni.reqSecurityCodeValidation(area, codes[0],codes[1],codes[2],codes[3]);
			int codeNumber = validate.getCodeNumber();
			if (codeNumber == OmniCode.DURESS_CODE)
				return OmniCode.DURESS_USER;
			if (validate.getAuthorityLevel() == 0)
				return OmniCode.INVALID_USER;
			String codename = getName(OmniArea.Code,codeNumber);
			return new OmniCode(validate.getCodeNumber(),codename,UserLevel.typeAsEnum(validate.getAuthorityLevel()));
		} catch (OmniInvalidResponseException e) {
			return OmniCode.INVALID_USER;
		} catch (OmniUnknownMessageTypeException e) {
			e.printStackTrace();
			return OmniCode.INVALID_USER;
		} catch (Exception e) {
			e.printStackTrace();
			return OmniCode.INVALID_USER;
		}
	}

	private Vector<OmniNotifyListener> notificationListeners;
	
	/** Add a notification handler for property changes.
	 * @param listener
	 */
	public void addNotificationListener(OmniNotifyListener listener){
		synchronized (notificationListeners) {
			notificationListeners.add(listener);
		}
	}
	
	/** Remove the notification handler.
	 * @param listener
	 */
	public void removeNotificationListener(OmniNotifyListener listener){
		synchronized (notificationListeners) {
			if(notificationListeners.contains(listener))
				notificationListeners.remove(listener);
		}
	}
	
	/** Pass on an command from and ActionRequest message.
	 */
	protected void sendAction( ActionRequest msg ) throws IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException {
		omni.controllerCommand(msg.getCommand());
	}

	/** Respond to a change type request from an OmniPart
	 * @param msg  Message from omni-part with notifyType==ChangeRequest
	 */
	protected void objectChangeRequest(ChangeMessage msg) throws IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException {
		if (msg instanceof NameChangeMessage ) {
			// Special type; name change applies to all areas.
			omni.sendName(msg.area.get_objtype_msg(), msg.number, ((NameChangeMessage) msg).name);
		} else if (msg instanceof ActionRequest) {
			sendAction((ActionRequest)msg);
		} else { 
			switch (msg.area) {
			case Button: {
				// Create a CommandMessage to send a macro button press.
				sendAction(new ActionRequest(msg.area, msg.number, CommandMessage.macroButtonCmd(msg.number)));
			} break;	
			case Unit: {
				OmniUnit.UnitChangeMessage ucm = (OmniUnit.UnitChangeMessage)msg;
				switch (ucm.changeType) {
				case RawState:
					OmniUnit unit = units.get(ucm.number);
					if (unit != null) { // which it should never be.
						switch ( unit.getUnitType()) {
						case Flag:
							sendAction( new ActionRequest(msg.area, msg.number, CommandMessage.unitSetCounterCmd(unit.number, unit.getRawStatus())));
						}
					}
				}
				// TODO: Change the value of a unit. (optionally for a specified time)
			} break;
			case Sensor:{
				//OmniSensor.SensorChangeMessage scm = (OmniSensor.SensorChangeMessage)msg;
				// TODO: Change min/max on the sensors.
				
			} break;
			}
		}
	}
	
	@Override
	public void objectChangedNotification(ChangeMessage msg) {
		if (getDebugChan(dcChildMessage))
			try {
				String areaname = getName(msg.area, msg.number);
				System.out.println(msg.area.toString()+" '"+areaname +"': "+ msg.toString());
			} catch (OmniNotConnectedException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		if (msg.notifyType == NotifyType.ChangeRequest) {
			// A change has been requested to be sent to the omni.
			try {
				objectChangeRequest(msg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OmniNotConnectedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OmniInvalidResponseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OmniUnknownMessageTypeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
// vim: syntax=java.doxygen ts=4 sw=4 noet
