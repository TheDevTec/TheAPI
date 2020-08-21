package me.DevTec;

import me.DevTec.Other.StringUtils;

public class TimeConventorAPI {

	/**
	 * @see see Get long from string
	 * @param s
	 * @return long
	 */
	public static long getTimeFromString(String s) {
		return StringUtils.timeFromString(s);
	}

	/**
	 * @see see Set long to string
	 * @param l
	 * @return String
	 */
	public static String setTimeToString(long l) {
		return StringUtils.timeToString(l);
	}
}
