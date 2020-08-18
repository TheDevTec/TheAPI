package me.DevTec.Other;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import me.DevTec.TheAPI;

public class StringUtils {

	/**
	 * @see see Transfer Runnable to String and back by Base64
	 * @return TheCoder
	 */
	public static TheCoder getTheCoder() {
		return new TheCoder();
	}

	/**
	 * @see see Get Color from String
	 * @return ChatColor
	 */
	public static ChatColor getColor(String fromString) {
		char colour = '\u0000';
		char[] chars = fromString.toCharArray();
		for (int i = 0; i < chars.length; ++i) {
			char code;
			char at = chars[i];
			if (at != '\u00a7' && at != '&' || i + 1 >= chars.length
					|| ChatColor.getByChar(code = chars[i + 1]) == null)
				continue;
			colour = code;
		}
		return colour == '\u0000' ? ChatColor.RESET : ChatColor.getByChar(colour);
	}

	/**
	 * @see see Transfer Collection to String
	 * @return String
	 */
	public static String join(Collection<?> toJoin, String split) {
		String r = "";
		for (Object s : toJoin)
			r = r + split + s.toString();
		r = r.replaceFirst(split, "");
		return r;
	}

	/**
	 * @see see Transfer List to String
	 * @return String
	 */
	public static String join(List<?> toJoin, String split) {
		String r = "";
		for (Object s : toJoin)
			r = r + split + s.toString();
		r = r.replaceFirst(split, "");
		return r;
	}

	/**
	 * @see see Transfer ArrayList to String
	 * @return String
	 */
	public static String join(ArrayList<?> toJoin, String split) {
		String r = "";
		for (Object s : toJoin)
			r = r + split + s.toString();
		r = r.replaceFirst(split, "");
		return r;
	}

	/**
	 * @see see Transfer Object[] to String
	 * @return String
	 */
	public static String join(Object[] toJoin, String split) {
		String r = "";
		for (Object s : toJoin)
			r = r + split + s.toString();
		r = r.replaceFirst(split, "");
		return r;
	}

	/**
	 * @see see Transfer Iterator<?> to String
	 * @return String
	 */
	public static String join(Iterator<?> toJoin, String split) {
		String r = "";
		for (Object s = toJoin.next(); toJoin.hasNext();)
			r = r + split + s.toString();
		r = r.replaceFirst(split, "");
		return r;
	}

	/**
	 * @see see Create clickable message
	 * @return HoverMessage
	 */
	public static HoverMessage getHoverMessage(String... message) {
		/**
		 * Example:
		 * 
		 * StringUtils.getHoverMessage("&eHello "+player.getName()+" ").setHoverEvent("&cHoooooveeer messaagee")
		 .addText("&1A").setHoverEvent("&1A").setClickEvent(ClickAction.RUN_COMMAND, "a")
		 .addText("&2B").setHoverEvent("&2B").setClickEvent(ClickAction.RUN_COMMAND, "b")
		 .addText("&3C").setHoverEvent("&3C").setClickEvent(ClickAction.RUN_COMMAND, "c")
		 .addText("&4D").setHoverEvent("&4D").setClickEvent(ClickAction.RUN_COMMAND, "d")
		 .addText("&5E").setHoverEvent("&5E").setClickEvent(ClickAction.RUN_COMMAND, "e").send(player);
		 * 
		 */
		return new HoverMessage(message);
	}

	private static final Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
	
	/**
	 * @see see Colorize string with colors
	 * @param string
	 * @return String
	 */
	public static String colorize(String string) {
		if (string == null)return null;
		if(string.contains("#"))
			if (TheAPI.isNewerThan(15)) {
				string = string.replace("&x", "§x");
				Matcher match = pattern.matcher(string);
	            while (match.find()) {
	                String color = match.group();
	                StringBuilder magic = new StringBuilder("§x");
	                char[] c = color.substring(1).toCharArray();
	                for(int i = 0; i < c.length; ++i) {
	                    magic.append(("&"+c[i]).toLowerCase());
	                }
	                string = string.replace(color, magic.toString() + "");
	            }
	        }
		return ChatColor.translateAlternateColorCodes('&', string);
	}

	/**
	 * @see see Build string from String[]
	 * @param args
	 * @return String
	 * 
	 */
	public static String buildString(String[] args) {
		if (args.length > 0) {
			String msg = "";
			for (String string : args) {
				msg = msg + " " + string;
			}
			msg = msg.replaceFirst(" ", "");
			return msg;
		}
		return null;
	}

	/**
	 * @see see Return random object from list
	 * @param list
	 * @return 
	 * @return Object
	 */
	public static <T> T getRandomFromList(List<T> list) {
		if (list.isEmpty() || list == null)
			return null;
		int r = new Random().nextInt(list.size());
		if (r <= 0) {
			if (list.get(0) != null) {
				return list.get(0);
			}
			return null;
		} else
			return list.get(r);
	}

	private static final Pattern periodPattern = Pattern.compile("([0-9]+)((mon)|(min)|([ywhs]))");
	

	/**
	 * @see see Get long from string
	 * @param s String
	 * @return long
	 */
	public static long getTimeFromString(String period){
		return timeFromString(period);
	}
	
	/**
	 * @see see Get long from string
	 * @param s String
	 * @return long
	 */
	public static long timeFromString(String period){ //New shorter name of method
	    if(period == null||period.trim().isEmpty()) return 0;
	    period = period.toLowerCase(Locale.ENGLISH);
	    if(isInt(period))return getInt(period);
	    Matcher matcher = periodPattern.matcher(period);
	    Instant instant=Instant.EPOCH;
	    while(matcher.find()) {
	        int num = getInt(matcher.group(1));
	        String typ = matcher.group(2);
	        switch (typ) {
        		case "s":
        			instant=instant.plus(Duration.ofSeconds(num));
        			break;
	        	case "min":
	        		instant=instant.plus(Duration.ofMinutes(num));
	        		break;
	            case "h":
	                instant=instant.plus(Duration.ofHours(num));
	                break;
	            case "d":
	                instant=instant.plus(Duration.ofDays(num));
	                break;
	            case "w":
	                instant=instant.plus(Period.ofWeeks(num));
	                break;
	            case "mon":
	                instant=instant.plus(Period.ofMonths(num));
	                break;
	            case "y":
	                instant=instant.plus(Period.ofYears(num));
	                break;
	        }
	    }
	    return instant.toEpochMilli()/1000;
	}

	/**
	 * @see see Set long to string
	 * @param l long
	 * @return String
	 */
	public static String setTimeToString(long period){
		return timeToString(period);
	}
	
	/**
	 * @see see Set long to string
	 * @param l long
	 * @return String
	 */
	public static String timeToString(long l) { //New shorter name of method
		long seconds = l % 60;
		long minutes = l / 60;
		long hours = minutes / 60;
		long days = hours / 24;
		long weeks = days / 7;
		long months = weeks / 4;
		long years = months / 12;
		long millenniums = years / 100000;
		if (minutes >= 60)
			minutes = minutes % 60;
		if (hours >= 24)
			hours = hours % 24;
		if (days >= 7)
			days = days % 7;
		if (weeks >= 4)
			weeks = weeks % 4;
		if (months >= 12)
			months = months % 12;
		if (years >= 100)
			years = years % 100;
		String s = "s";

		if (millenniums > 0) {
			s = millenniums + "mil "  + years + "y";
		} else if (years > 0) {
			s = years +  "y " + months + "mon " + weeks +  "w " + days + "d";
		} else if (months > 0) {
			s = months + "mon " + weeks + "w " + days +  "d " + hours + "h " + minutes + "min";
		} else if (weeks > 0) {
			if (minutes != 0)
				s = weeks +  "w " + days + "d " + hours +  "h " + minutes + "min";
			else
				s = weeks +  "w " + days + "d " + hours + "h";
		} else if (days > 0) {
			if (minutes != 0)
				s = days +  "d " + hours + "h " + minutes + "min";
			else
				s = days +  "d " + hours + "h";
		} else if (hours > 0) {
			if (seconds != 0)
				s = hours +  "h " + minutes +  "min " + seconds + s;
			else
				s = hours +  "h " + minutes + "min";
		} else if (minutes > 0) {
			if (seconds != 0)
				s = minutes + "min " + seconds + s;
			else
				s = minutes + "min";
		} else {
			s = seconds + s;
		}
		return s;
	}

	/**
	 * @see see Convert Location to String
	 * @return String
	 */
	public static String getLocationAsString(Location loc) {
		return locationAsString(loc);
	}

	/**
	 * @see see Convert Location to String
	 * @return String
	 */
	public static String locationAsString(Location loc) { //New shorter name of method
		return getTheCoder().locationToString(loc);
	}

	/**
	 * @see see Create Location from String
	 * @return Location
	 */
	public static Location getLocationFromString(String savedLocation) {
		return locationFromString(savedLocation);
	}

	/**
	 * @see see Create Location from String
	 * @return Location
	 */
	public static Location locationFromString(String savedLocation) { //New shorter name of method
		return getTheCoder().locationFromString(savedLocation);
	}

	/**
	 * @see see Get boolean from string
	 * @return boolean
	 */
	public static boolean getBoolean(String fromString) {
		try {
			return Boolean.parseBoolean(fromString);
		} catch (Exception er) {
			return false;
		}
	}

	/**
	 * @see see Convert String to Math and Calculate exempt
	 * @return double
	 */
	public static BigDecimal calculate(String fromString) {
		String a = fromString.replaceAll("[A-z]+", "").replace(",", ".");
		Matcher c = Pattern.compile("[0-9.]+").matcher(a);
		if(c.find())
		for(String s = c.group(); c.find();)
			a=a.replace(s, new BigDecimal(s)+"");
		try {
			return new BigDecimal(new ScriptEngineManager().getEngineByName("JavaScript").eval(a).toString());
		} catch (ScriptException e) {
		}
		return new BigDecimal(0);
	}

	/**
	 * @see see Get double from string
	 * @return double
	 */
	public static double getDouble(String fromString) {
		String a = fromString.replaceAll("[a-zA-Z]+", "").replace(",", ".");
		if (isDouble(a)) {
			return Double.parseDouble(a);
		} else {
			return 0.0;
		}
	}

	/**
	 * @see see Is string, double ?
	 * @return boolean
	 */
	public static boolean isDouble(String fromString) {
		try {
			Double.parseDouble(fromString);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	/**
	 * @see see Get long from string
	 * @return long
	 */
	public static long getLong(String fromString) {
		String a = fromString.replaceAll("[a-zA-Z]+", "");
		if (isLong(a)) {
			return Long.parseLong(a);
		} else {
			return 0;
		}
	}

	/**
	 * @see see Is string, long ?
	 * @return
	 */
	public static boolean isLong(String fromString) {
		try {
			Long.parseLong(fromString);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	/**
	 * @see see Get int from string
	 * @return int
	 */
	public static int getInt(String fromString) {
		String a = fromString.replaceAll("[a-zA-Z]+", "");
		if (isInt(a)) {
			return Integer.parseInt(a);
		} else {
			return 0;
		}
	}

	/**
	 * @see see Is string, int ?
	 * @return boolean
	 */
	public static boolean isInt(String fromString) {
		try {
			Integer.parseInt(fromString);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	/**
	 * @see see Is string, float ?
	 * @return boolean
	 */
	public static boolean isFloat(String fromString) {
		try {
			Float.parseFloat(fromString);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	/**
	 * @see see Get float from string
	 * @return float
	 */
	public static float getFloat(String fromString) {
		String a = fromString.replaceAll("[a-zA-Z]+", "");
		if (isFloat(a)) {
			return Float.parseFloat(a);
		} else {
			return 0;
		}
	}

	/**
	 * @see see Is string, float ?
	 * @return boolean
	 */
	public static boolean isByte(String fromString) {
		try {
			Byte.parseByte(fromString);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	/**
	 * @see see Get float from string
	 * @return float
	 */
	public static byte getByte(String fromString) {
		String a = fromString.replaceAll("[a-zA-Z]+", "");
		if (isByte(a)) {
			return Byte.parseByte(a);
		} else {
			return (byte)0;
		}
	}

	/**
	 * @see see Is string, float ?
	 * @return boolean
	 */
	public static boolean isShort(String fromString) {
		try {
			Short.parseShort(fromString);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	/**
	 * @see see Get float from string
	 * @return float
	 */
	public static short getShort(String fromString) {
		String a = fromString.replaceAll("[a-zA-Z]+", "");
		if (isShort(a)) {
			return Short.parseShort(a);
		} else {
			return (short)0;
		}
	}

	/**
	 * @see see Is string, number ?
	 * @return boolean
	 */
	public static boolean isNumber(String fromString) {
		return isInt(fromString)||isDouble(fromString)||isLong(fromString)||isByte(fromString)||isShort(fromString)||isFloat(fromString);
	}

	/**
	 * @see see Is string, boolean ?
	 * @return boolean
	 */
	public static boolean isBoolean(String fromString) {
		if(fromString==null)return false;
		return fromString.equalsIgnoreCase("true")||fromString.equalsIgnoreCase("false");
	}
	private static Pattern special = Pattern.compile("[^A-Z-a-z0-9_]+");
	public static boolean containsSpecial(String value) {
		return special.matcher(value).find();
	}
}
