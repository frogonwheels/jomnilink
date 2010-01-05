package com.wheelycreek.jomnilinkII.OmniSystem;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
//import java.util.ArrayList;
import java.util.Vector;
import java.util.Map.Entry;

import com.digitaldan.jomnilinkII.Connection;
import com.digitaldan.jomnilinkII.Message;
import com.digitaldan.jomnilinkII.NotificationListener;
import com.digitaldan.jomnilinkII.OmniInvalidResponseException;
import com.digitaldan.jomnilinkII.OmniNotConnectedException;
import com.digitaldan.jomnilinkII.OmniUnknownMessageTypeException;
import com.digitaldan.jomnilinkII.MessageTypes.ObjectStatus;
import com.digitaldan.jomnilinkII.MessageTypes.OtherEventNotifications;
import com.digitaldan.jomnilinkII.MessageTypes.events.OtherEvent;
import com.digitaldan.jomnilinkII.MessageTypes.events.UserMacroButtonEvent;
import com.digitaldan.jomnilinkII.MessageTypes.properties.AuxSensorProperties;
import com.digitaldan.jomnilinkII.MessageTypes.properties.ButtonProperties;
import com.digitaldan.jomnilinkII.MessageTypes.properties.UnitProperties;
import com.digitaldan.jomnilinkII.MessageTypes.properties.ZoneProperties;
import com.digitaldan.jomnilinkII.MessageTypes.statuses.AuxSensorStatus;
import com.digitaldan.jomnilinkII.MessageTypes.statuses.Status;
import com.digitaldan.jomnilinkII.MessageTypes.statuses.UnitStatus;
import com.digitaldan.jomnilinkII.MessageTypes.statuses.ZoneStatus;
import com.digitaldan.jomnilinkII.MessageTypes.NameData;
import com.digitaldan.jomnilinkII.MessageTypes.ObjectProperties;
import com.digitaldan.jomnilinkII.MessageTypes.SystemFeatures;
import com.digitaldan.jomnilinkII.MessageTypes.SystemFormats;
import com.digitaldan.jomnilinkII.MessageTypes.SystemInformation;
import com.digitaldan.jomnilinkII.MessageTypes.SystemStatus;
import com.digitaldan.jomnilinkII.MessageTypes.SystemTroubles;
//import com.digitaldan.jomnilinkII.MessageTypes.UploadEventRecord;
//import com.digitaldan.jomnilinkII.MessageTypes.UploadNames;
import com.digitaldan.jomnilinkII.MessageTypes.ZoneReadyStatus;
//import com.digitaldan.jomnilinkII.MessageTypes.statuses.ZoneStatus;
import com.wheelycreek.jomnilinkII.OmniNotifyListener;
import com.wheelycreek.jomnilinkII.OmniPart;
import com.wheelycreek.jomnilinkII.Parts.OmniZone;
import com.wheelycreek.jomnilinkII.Parts.OmniSensor;
import com.wheelycreek.jomnilinkII.Parts.OmniUnit;
import com.wheelycreek.jomnilinkII.Parts.OmniButton;

//import com.wheelycreek.jomnilinkII.OmniPart;

/** Represents a whole controller.
 * 
 * @author michaelg
 */
public class OmniController implements OmniNotifyListener {
	public static void main(String[] args) {
		
		if(args.length != 3){
			System.out.println("Usage:com.digitaldan.jomnilinkII.Main  host port encKey");
			System.exit(-1);
		}
		String host  = args[0];
		int port = Integer.parseInt(args[1]);
		String key = args[2];
		
		try {
			OmniController c = new OmniController(host,port,key);
			c.setDebugChan(dcSensors | dcZones /*dcMessage*/ |dcChildMessage, true);
			c.loadZones();
			c.loadSensors();
			c.loadButtons();
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
	static int dcAll = 0x3f;
	static int dcConnection = 0x1;
	static int dcMessage = 0x2;
	static int dcZones = 0x4;
	static int dcChildMessage = 0x8;
	static int dcSensors = 0x10;
	static int dcUnits = 0x20;
	private int debug_channels;
	public boolean getDebugChan( int channels ) {
		return (debug_channels & channels) == channels;
	}
	public void setDebugChan( int channels, boolean newVal) {
		if (newVal)
			debug_channels |= channels;
		else
			debug_channels &= ~channels;
		if (((channels & dcConnection) == dcConnection) && (omni != null))
			omni.debug = newVal;
	}

	private String omni_host;
	private int omni_port;
	private String omni_key;
	private	Vector<String> zone_names;
	private Vector<String> unit_names;
	private Vector<String> area_names;
	private SortedMap<Integer, OmniZone> zones;
	private SortedMap<Integer, OmniSensor> sensors;
	private SortedMap<Integer, OmniUnit> units;
	private SortedMap<Integer, OmniButton> buttons;
	
	private SystemFeatures    sys_features;
	private SystemFormats     sys_formats;
	private SystemInformation sys_info;
	private SystemStatus      sys_status;
	private SystemTroubles    sys_troubles;
	private ZoneReadyStatus   zones_ready;
	
	private void constructArrays() {
		notificationListeners = new Vector<OmniNotifyListener>();
		zones   = new TreeMap<Integer, OmniZone>();
		sensors = new TreeMap<Integer, OmniSensor>();
		units   = new TreeMap<Integer, OmniUnit>();
		buttons = new TreeMap<Integer, OmniButton>();
	}
	
	public OmniController(String host, int port, String key) throws UnknownHostException, IOException, Exception {
		constructArrays();
		createConnection(host, port, key);
	}
	public OmniController(String host, int port, String key, boolean keepKey) throws UnknownHostException, IOException, Exception {
		constructArrays();
		createConnection(host, port, key);
		if (keepKey) {
			this.omni_host = host;
			this.omni_port = port;
			this.omni_key = key;
		}
	}
	
	protected Connection omni;
	
	protected void createConnection(String host, int port, String key) throws UnknownHostException, IOException, Exception{
		omni = new Connection(host, port, key);
		omni.debug = getDebugChan(dcConnection);
		omni.addNotificationListener(new NotificationListener(){
			@Override
			public void objectStausNotification(ObjectStatus s) { statusNotify(s); }
			@Override
			public void otherEventNotification(OtherEventNotifications o) {	otherEventNotify(o);}
		});
		omni.enableNotifications();
		// Reset Information so that it is read again.
		sys_info = null;
		sys_status = null;
		sys_troubles = null;
		sys_formats = null;
		sys_features = null;
		zones_ready = null;
	}
	protected  boolean reconnect() throws UnknownHostException, IOException, Exception {
		if (omni_key == null)
			return false;
		else {
			if (omni.connected())
				omni.disconnect();
			createConnection(omni_host,omni_port,omni_key);
			return omni.connected();
		}
	}

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
		case Msg:
			
			break;
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
			Status status[]	= s.getStatuses();
			
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
	
	private void unitStatusReceive(UnitStatus status) {

		if (getDebugChan(dcUnits))
			System.out.println("Unit Changed: "+status.toString());
		
		int senseidx = status.getNumber()-1;
		
		OmniUnit Unit = units.get(senseidx);
		if (Unit != null)
			updateUnit(status, Unit, false);
	}
	
	private void updateUnit(UnitStatus status, OmniUnit unit, boolean isInitial) {
		OmniNotifyListener.NotifyType notifyType = msgType(isInitial);
		unit.setState(status.getStatus(), notifyType);
		unit.setTimeSec(status.getTime(), notifyType);
	}
	private void updateUnit(UnitProperties props, OmniUnit unit, boolean isInitial) {
		OmniNotifyListener.NotifyType notifyType = msgType(isInitial);
		unit.setName(props.getName(), notifyType);
		unit.setUnitType( OmniUnit.UnitType.typeAsEnum(props.getUnitType()), notifyType);
		unit.setState(props.getState(), notifyType);
		unit.setTimeSec(props.getTime(), notifyType);
	}
	private void sensorStatusReceive(AuxSensorStatus status) {
		
		if (getDebugChan(dcSensors))
			System.out.println("Sensor Changed: "+status.toString());
		
		int senseidx = status.getNumber()-1;
		
		OmniSensor sensor = sensors.get(senseidx);
		if (sensor != null)
			updateSensor(status, sensor, false);
		
	}
	protected void otherEventNotify(OtherEventNotifications o) {
		for(int k=0;k<o.Count();k++){
			otherEventReceive(o.getNotification(k));
		}
	}
	/** get a zone, load it with information.
	 * @param zonenr The zone to load.
	 * @return a loaded zone.
	 * @throws Exception 
	 * @throws OmniNotConnectedException 
	 */
	protected OmniZone getZone(int zonenr) throws OmniNotConnectedException, Exception {
		OmniZone result = zones.get(zonenr);
		if (result == null) {
			// Build a new 
			loadZones(zonenr, zonenr);
			result = zones.get(zonenr);
		}
		return result;
	}
	protected int getCapacity( OmniArea area) throws IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException {
		return omni.reqObjectTypeCapacities(area.get_objtype_msg()).getCapacity();
	}
	protected void loadZones() throws Exception {
		loadZones(1,0);
	}
	protected void loadZones(int startZone, int endZone) throws Exception {
		
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
			updateZone(zp, zone, true);
		}
	}

	protected void updateZones() throws IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException {
		
		ObjectStatus status = omni.reqObjectStatus(OmniArea.Zone.get_objtype_msg(), 1,getCapacity(OmniArea.Zone));
		ZoneStatus [] zonestats = (ZoneStatus[])status.getStatuses();
		for (ZoneStatus zonestat : zonestats) {
			int zoneidx = zonestat.getNumber();
			updateZone(zonestat, zones.get(zoneidx), false);
		}
	}
	
	protected void zoneStatusReceive( ZoneStatus status) {
		if (getDebugChan(dcZones))
			System.out.println("Zone Changed: "+status.toString());
		int zoneidx = status.getNumber()-1;
		if (zoneidx < zones.size()) {
			updateZone(status, zones.get(zoneidx), false);
		}
	}
	protected void updateZone(ZoneStatus status, OmniZone zone, boolean isInitial ) {
		if (zone != null)  {
			zone.setRawStatus(status.getStatus(), msgType(isInitial));
		}
	}
	
	protected void updateZone(ZoneProperties prop, OmniZone zone, boolean isInitial ) {
		if (zone != null)  {
			OmniNotifyListener.NotifyType notifyType = msgType(isInitial);
			zone.setName(prop.getName(), notifyType);
			zone.setZoneType(OmniZone.ZoneType.typeAsEnum(prop.getZoneType()), notifyType);
			zone.setRawOptions(prop.getOptions(), notifyType);
			zone.setZoneArea(prop.getArea(), notifyType);
			zone.setLoopValue(prop.getLoop(), notifyType);
			zone.setRawStatus(prop.getStatus(), notifyType);
		}
	}
	protected void loadSensors() throws Exception, IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException {
		
		int objnum = 0;
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
				updateSensor(op, sensor, true);
			}
		}
		
	}
	
	protected void updateSensors() throws IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException {
		// Update all sensor values.
		Iterator<OmniSensor> iter = sensors.values().iterator();
		while (iter.hasNext()) {
			OmniSensor sense = iter.next();
			ObjectStatus status = omni.reqObjectStatus(OmniArea.Sensor.get_objtype_msg(), sense.number,sense.number);
			AuxSensorStatus [] sensorstats = (AuxSensorStatus[])status.getStatuses();
			if ((sensorstats[0] != null) && (sense.number == sensorstats[0].getNumber()))
				updateSensor(sensorstats[0], sense, false);
		}
	}
	
	protected void loadUnits() throws IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException {
		int objnum = 0;
		Message m;
		// Get initial properties
		while((m = omni.reqObjectProperties(OmniArea.Unit.get_objtype_msg(), objnum, 1, 
				ObjectProperties.FILTER_1_NAMED, ObjectProperties.FILTER_2_AREA_ALL, ObjectProperties.FILTER_3_ANY_LOAD)).getMessageType() 
				== Message.MESG_TYPE_OBJ_PROP){
			UnitProperties uprop = (UnitProperties)m;
			objnum = uprop.getNumber();
			OmniUnit unit = units.get(objnum);
			if (unit == null) {
				unit = new OmniUnit(objnum);
				units.put(objnum, unit);
				unit.addNotificationListener(this);
			}
			updateUnit(uprop, unit, true);
		}
	}

	protected void loadButtons() throws IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException {
		int objnum = 0;
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
				buttons.put(objnum, button);
				button.addNotificationListener(this);
			}
		}
	}

	protected static OmniNotifyListener.NotifyType msgType(boolean isInitial) {
		return isInitial?OmniNotifyListener.NotifyType.Initial:OmniNotifyListener.NotifyType.Notify;
	}
	/** Update values for the sensor.
	 * @param sensestat  The communication representation of the sensor
	 * @param sense      The exposed omni sensor
	 * @param isInitial  Is this the initial update for the sensor 
	 */
	protected void updateSensor(AuxSensorStatus sensestat, OmniSensor sense, boolean isInitial) {
		if (sense != null) {
			OmniNotifyListener.NotifyType notifyType = msgType(isInitial);
			sense.setCoolSetPoint(new Temperature(sensestat.getCoolSetpoint()), notifyType); 
			sense.setHeatSetPoint(new Temperature(sensestat.getHeatSetpoint()), notifyType);
			sense.setTemperature(new Temperature(sensestat.getTemp()), notifyType);
			sense.setTriggerOutput(sensestat.getStatus() != 0, notifyType);
		}
	}
	protected void updateSensor(AuxSensorProperties senseprop, OmniSensor sense, boolean isInitial) {
		if (sense != null) {
			sense.setName(senseprop.getName());
			sense.setSensorType(OmniSensor.SensorType.typeAsEnum(senseprop.getSensorType()));
			OmniNotifyListener.NotifyType notifyType = msgType(isInitial);
			sense.setCoolSetPoint(new Temperature(senseprop.getLowSetpoint()), notifyType); 
			sense.setHeatSetPoint(new Temperature(senseprop.getHighSetpoint()), notifyType);
			sense.setTemperature(new Temperature(senseprop.getCurrent()), notifyType);
			sense.setTriggerOutput(senseprop.getStatus() != 0, notifyType);
		}
	}

	protected void otherEventReceive( OtherEvent event) {
		if (getDebugChan(dcMessage))
			System.out.println(event.toString());	
		switch (event.getEventType()) {
			case UserMacroButton: {
				OmniButton ob = buttons.get(((UserMacroButtonEvent)event).getButtonNumber());
				if (ob != null) {
					ob.notifyButton();
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
	
	protected void load_vector(OmniArea area, Vector<String> list, boolean reload) throws IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException {
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
	protected Vector<String> create_loaded_vector(OmniArea area) throws OmniNotConnectedException, Exception {
		Vector<String> result = new Vector<String>();
		try {
			load_vector(area, result, false);
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
	
	protected Vector<String> get_vectors(OmniArea area) throws OmniNotConnectedException, Exception {
		switch (area) {
		case Zone:
			if (zone_names == null)
			  zone_names = create_loaded_vector(area);
			return zone_names;
		case Unit:
			if (unit_names == null)
			  unit_names = create_loaded_vector(area);
			return unit_names;
		case Area:
			if (area_names == null)
			  area_names = create_loaded_vector(area);
			return area_names;
		default:
			return null;
		}
	}
	public void setName( OmniArea area, int index, String name ) throws OmniNotConnectedException, Exception {
		Vector<String> vectors = get_vectors(area);
		if (vectors != null) {
			if (index >= vectors.size())
				vectors.setSize(index);
			vectors.set(index-1, name);
			// TODO  Push it up to the omni.
		}
	}
	public String getName(OmniArea area, int index) throws OmniNotConnectedException, Exception {
		if (index < 0)
			return null;
		Vector<String> vectors = get_vectors(area);
		if (vectors == null)
			return null;
		else
			return vectors.get(index-1);
	}
	
	public void setZoneName(int index, String name) throws OmniNotConnectedException, Exception {
		setName(OmniArea.Zone, index, name); 
	}
	public String getZoneName(int index) throws OmniNotConnectedException, Exception {
		return getName(OmniArea.Zone, index);
	}
	public void setUnitName(int index, String name) throws OmniNotConnectedException, Exception {
		setName(OmniArea.Unit, index, name);
	}
	public String getUnitName(int index) throws OmniNotConnectedException, Exception {
		return getName(OmniArea.Unit, index);
	}
	public void setAreaName(int index, String name) throws OmniNotConnectedException, Exception {
		setName(OmniArea.Area, index, name);
	}
	public String getAreaName(int index) throws OmniNotConnectedException, Exception {
		return getName(OmniArea.Area, index);
	}
	/**
	 * @param sys_features the sys_features to set
	 */
	public void setFeatures(SystemFeatures sys_features) {
		this.sys_features = sys_features;
	}
	/**
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
	 * @param sys_formats the sys_formats to set
	 */
	protected void setFormats(SystemFormats sys_formats) {
		this.sys_formats = sys_formats;
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
	 * @param sys_info the sys_info to set
	 */
	protected void setInfo(SystemInformation sys_info) {
		this.sys_info = sys_info;
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
	 * @param sys_status the sys_status to set
	 */
	public void setStatus(SystemStatus sys_status) {
		this.sys_status = sys_status;
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
	 * @param sys_troubles the sys_troubles to set
	 */
	public void setTroubles(SystemTroubles sys_troubles) {
		this.sys_troubles = sys_troubles;
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
		
		return (zones_ready.getZones()[zone/8] >> (zone%8) & 0x1) == 1;
	}

	private Vector<OmniNotifyListener> notificationListeners;
	
	public void addNotificationListener(OmniNotifyListener listener){
		synchronized (notificationListeners) {
			notificationListeners.add(listener);
		}
	}

	public void removeNotificationListener(OmniNotifyListener listener){
		synchronized (notificationListeners) {
			if(notificationListeners.contains(listener))
				notificationListeners.remove(listener);
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
		
	}

}
