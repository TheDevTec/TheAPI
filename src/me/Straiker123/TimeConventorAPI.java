package me.Straiker123;

public class TimeConventorAPI {

	/**
	 * @see see Get long from string
	 * @param s
	 * @return long
	 */
	public long getTimeFromString(String s) {
		return TheAPI.getStringUtils().getTimeFromString(s);
	}

	/**
	 * @see see Set long to string
	 * @param l
	 * @return String
	 */
	public String setTimeToString(long l) {
		return TheAPI.getStringUtils().setTimeToString(l);
	}
}
