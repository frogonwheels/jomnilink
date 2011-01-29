/**
 * 
 */
package com.wheelycreek.jomnilinkII.Parts;

import com.wheelycreek.jomnilinkII.OmniNotifyListener;
import com.wheelycreek.jomnilinkII.OmniPart;
import com.wheelycreek.jomnilinkII.OmniSystem.OmniArea;

/** A security code for omni
 * @author michaelg
 *
 */
public class OmniCode extends OmniPart {

	/**
	 * @param number
	 * @param area
	 */
	public OmniCode(int number) {
		super(number, OmniArea.Code);
		// TODO Auto-generated constructor stub
	}
	public OmniCode(int number, String name, UserLevel level) {
		super(number, OmniArea.Code);
		this.name = name;
		this.level = level;
	}

	private String name;
	private UserLevel level;
	

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void updateName(String name,  OmniNotifyListener.NotifyType notifyType ) {
		this.name = name;
	}
	
	/**
	 * @param level the level to set
	 */
	public void updateLevel(UserLevel level) {
		this.level = level;
	}

	/**
	 * @return the level
	 */
	public UserLevel getLevel() {
		return level;
	}

	public enum  UserLevel {
		INVALID(0,"Invalid"), MASTER(1, "Master"), MANAGER(2, "Manager"), USER(3,"User"), DURESS(-1,"Duress") ;
		public final int rawType;
		public final String name;
		private UserLevel( int rawType ,String name) {
			this.rawType = rawType;
			this.name = name;
		}
		public static UserLevel typeAsEnum(int rawType) {
			switch(rawType) {
			case 1: return MASTER;
			case 2: return MANAGER;
			case 3: return USER;
			default : return INVALID;
			}
		}
	};
	public static final int DURESS_CODE = 251;
	public static final OmniCode INVALID_USER = new OmniCode(-1, "Invalid", UserLevel.INVALID);
	public static final OmniCode DURESS_USER = new OmniCode(DURESS_CODE, "Duress", UserLevel.DURESS);
}
