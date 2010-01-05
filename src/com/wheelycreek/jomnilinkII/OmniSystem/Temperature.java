/** Temperature utils.
 * 
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

import com.digitaldan.jomnilinkII.MessageUtils;

/** An omni sensor temperature.
 * @author michaelg
 *
 */
public class Temperature {
	private int raw_temp;
	/** Construct a temperature from a raw temperature
	 * @param omniTemp Temperature in omni units.
	 */
	public Temperature( int omniTemp ) {
		raw_temp = omniTemp;
	}
	/** Construct a temperature with 'unset' temperature of 0
	 */
	public Temperature() {
		raw_temp = 0;
	}
	/** Create a new temperature from degrees celcius. 
	 */
	public static Temperature fromCelcius( double degC) {
		return new Temperature( (int)((degC+0.5)*2)+40);
	}
	/** Create a new temperature from degree farenheit.
	 */
	public static Temperature fromFarenheit( double degF ) {
		return new Temperature( (int)(0.5+((10 * ( degF + 40)) - 4)/9));
	}
	public double inCelcius() {
		return MessageUtils.TempInCelcius(raw_temp);
	}
	public double inFarenheit() {
		return (((((raw_temp) * 9) + 4)- 400 )/ 10.0 );
	}
	
	public String asStringC() {
		return MessageUtils.TempInCelciusString(raw_temp);
	}
	
	/** Return sum of temperatures.
	 */
	public Temperature add( Temperature tmp ) {
		return new Temperature(tmp.raw_temp+this.raw_temp);
	}
	/** return difference of temperatures.
	 */
	public Temperature subtract( Temperature tmp) {
		return new Temperature(this.raw_temp - tmp.raw_temp);
	}
	
	/** return true if value less than this.
	 */
	public boolean less( Temperature tmp) {
		return this.raw_temp < tmp.raw_temp;
	}
	/** return true if value equals this.
	 *
	 */
	public boolean equals(Temperature tmp) {
		return this.raw_temp == tmp.raw_temp;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("Temperature [%s (%s)]", raw_temp, asStringC());
	}
	
}
