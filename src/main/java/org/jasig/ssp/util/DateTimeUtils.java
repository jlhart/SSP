/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.ssp.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateTimeUtils {

	public static Date midnight() {
		return midnightOn(new Date());
	}

	public static Date midnightOn(Date on) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(on.getTime());
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return new Date(cal.getTimeInMillis());
	}
	
	public static  int daysSince1900(Date date) {
	    Calendar c = new GregorianCalendar();
	    c.setTime(date);

	    int year = c.get(Calendar.YEAR);
	    if (year < 1900 || year > 2099) {
	        throw new IllegalArgumentException("daysSince1900 - Date must be between 1900 and 2099");
	    }
	    year -= 1900;
	    int month = c.get(Calendar.MONTH) + 1;
	    int days = c.get(Calendar.DAY_OF_MONTH);

	    if (month < 3) {
	        month += 12;
	        year--;
	    }
	    int yearDays = (int) (year * 365.25);
	    int monthDays = (int) ((month + 1) * 30.61);

	    return (yearDays + monthDays + days - 63);
	}
	
	public static Date getDateOffsetInDays(Date date, Integer offset ){
		Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
	    cal.add(Calendar.DAY_OF_MONTH, offset);
	    return cal.getTime();
	} 
	
	public static String formatDate(String format, Date date){
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(date);
	}
}
