package me.DevTec;

import me.DevTec.Other.StringUtils;

public class NumbersAPI {
	private String fromString;

	public NumbersAPI(String string) {
		if (string != null)
			fromString = string;
	}

	/**
	 * @see see Return input String
	 * @return String
	 */
	public String getString() {
		return fromString;
	}

	/**
	 * @see see Calculate string
	 * @return float
	 */
	public float calculate() {
		return StringUtils.calculate(fromString).floatValue();
	}

	/**
	 * @see see Get double from string
	 * @return double
	 */
	public double getDouble() {
		return StringUtils.getDouble(fromString);
	}

	/**
	 * @see see Is string, double ?
	 * @return boolean
	 */
	public boolean isDouble() {
		return StringUtils.isDouble(fromString);
	}

	/**
	 * @see see Get long from string
	 * @return long
	 */
	public long getLong() {
		return StringUtils.getLong(fromString);
	}

	/**
	 * @see see Is string, long ?
	 * @return
	 */
	public boolean isLong() {
		return StringUtils.isLong(fromString);
	}

	/**
	 * @see see Get int from string
	 * @return int
	 */
	public int getInt() {
		return StringUtils.getInt(fromString);
	}

	/**
	 * @see see Is string, int ?
	 * @return boolean
	 */
	public boolean isInt() {
		return StringUtils.isInt(fromString);
	}
}
