package me.Straiker123;

public class NumbersAPI {
	private String fromString;
	public NumbersAPI(String string) {
		if(string!=null)
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
	 * @return double
	 */
	public double calculate() {
		return TheAPI.getStringUtils().calculate(fromString);
	}
	/**
	 * @see see Get double from string
	 * @return double
	 */
	public double getDouble() {
		return TheAPI.getStringUtils().getDouble(fromString);
		}
	/**
	 * @see see Is string, double ?
	 * @return boolean
	 */
	public boolean isDouble() {
		return TheAPI.getStringUtils().isDouble(fromString);
	}
	/**
	 * @see see Get long from string
	 * @return long
	 */
	public long getLong() {
		return TheAPI.getStringUtils().getLong(fromString);
		}
	/**
	 * @see see Is string, long ?
	 * @return
	 */
	public boolean isLong() {
		return TheAPI.getStringUtils().isLong(fromString);
	}
	/**
	 * @see see Get int from string
	 * @return int
	 */
	public int getInt() {
		return TheAPI.getStringUtils().getInt(fromString);
		}
	/**
	 * @see see Is string, int ?
	 * @return boolean
	 */
	public boolean isInt() {
		return TheAPI.getStringUtils().isInt(fromString);
	}
}
