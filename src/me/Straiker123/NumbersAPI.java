package me.Straiker123;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class NumbersAPI {
	String fromString;
	public NumbersAPI(String string) {
		if(string!=null)
		fromString = string;
	}
	/**
	 * Calculate string
	 * @return double
	 */
	public double calculate() {
		ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("JavaScript");
        try {
			return getDouble(engine.eval(fromString).toString());
		} catch (ScriptException e) {
		}
		return 0;
	}
	/**
	 * Get double from string
	 * @return double
	 */
	public double getDouble() {
		String a=fromString.replaceAll("[a-zA-Z]+", "").replace(",", ".");
		if (isDouble(a)) {
		return Double.parseDouble(a);
		}else {
		return 0.0;
	}}

	private double getDouble(String s) {
		 String aa =s.replaceAll("[a-zA-Z]+", "").replace(",", ".");
		if (isDouble(aa)) {
			return Double.parseDouble(aa);
			}
			else {
			return 0.0;
		}
	}
	private boolean isDouble(String a) {
		try {
			Double.parseDouble(a);
		} catch (NumberFormatException e) {
		return false;
		}
		return true;
	}
	/**
	 * Is string, double
	 * @return boolean
	 */
	public boolean isDouble() {
		try {
			Double.parseDouble(fromString);
		} catch (NumberFormatException e) {
		return false;
		}
		return true;
	}
	/**
	 * Get long from string
	 * @return long
	 */
	public long getLong() {
		String a=fromString.replaceAll("[a-zA-Z]+", "");
		if (isLong(a)) {
		return Long.parseLong(a);
		}
		else {
		return 0;
	}}
	/**
	 * Is string, long
	 * @return
	 */
	public boolean isLong() {
		try {
		Long.parseLong(fromString);
		} catch (NumberFormatException e) {
		return false;
		}
		return true;
	}
	private boolean isLong(String a) {
		try {
		Long.parseLong(a);
		} catch (NumberFormatException e) {
		return false;
		}
		return true;
	}
	/**
	 * Get int from string
	 * @return int
	 */
	public int getInt() {
		String a=fromString.replaceAll("[a-zA-Z]+", "");
		if (isInt(a)) {
		return Integer.parseInt(a);
		}
		else {
		return 0;
	}}
	/**
	 * Is string, int
	 * @return boolean
	 */
	private boolean isInt(String a) {
		try {
		Integer.parseInt(a);
		} catch (NumberFormatException e) {
		return false;
		}
		return true;
	}
	public boolean isInt() {
		try {
		Integer.parseInt(fromString);
		} catch (NumberFormatException e) {
		return false;
		}
		return true;
	}
}
