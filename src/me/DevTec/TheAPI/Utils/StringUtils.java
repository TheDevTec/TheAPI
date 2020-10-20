package me.DevTec.TheAPI.Utils;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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

import me.DevTec.TheAPI.TheAPI;
import me.DevTec.TheAPI.Utils.DataKeeper.Abstract.TheList;

public class StringUtils {  
	private static final Pattern INTEGER = Pattern.compile("([0-9+-]+)")
			, DOUBLE = Pattern.compile("([0-9+-]+\\.*[0-9]+)");
	private static Random random = new Random();
	
	public static interface ColormaticFactory {
		public String getColor();
	}
	
	/**
	 * @see see Split text correctly with colors
	 */
	public static List<String> fixedSplit(String text, int lengthOfSplit) {
		List<String> splitted = new ArrayList<>();
		String split = text;
		String prefix = "";
		while(split.length() > lengthOfSplit) {
			int length = lengthOfSplit-1-prefix.length();
			String a = prefix+split.substring(0, length);
			if(a.endsWith("&")||a.endsWith("§")) {
				--length;
				a=prefix+split.substring(0, length);
				prefix=ChatColor.getLastColors(a);
			}else prefix = "";
			splitted.add(a);
			split=split.substring(length);
		}
		splitted.add(prefix+split);
		return splitted;
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
	 * @see see Copy matches of String from Iterable<String>
	 * Examples: 
	 *   StringUtils.copyPartialMatches("hello", Arrays.asList("helloWorld", "hiHouska")) -> helloWorld
	 *   StringUtils.copyPartialMatches("hello", Arrays.asList("helloWorld", "hiHouska", "this_is_list_of_words", "helloDevs", "hell")) -> helloWorld, helloDevs
	 * @return List<String>
	 */
	public static List<String> copyPartialMatches(String prefix, Iterable<String> originals) {
		List<String> collection = new ArrayList<>();  
		for (String string : originals)
			if (string.length() < prefix.length()?false:string.regionMatches(true, 0, prefix, 0, prefix.length()))
				collection.add(string);
		return collection;
	}

	/**
	 * @see see Copy matches of String from Iterable<String>
	 * Examples: 
	 *   StringUtils.copyPartialMatches("hello", Arrays.asList("helloWorld", "hiHouska")) -> helloWorld
	 *   StringUtils.copyPartialMatches("hello", Arrays.asList("helloWorld", "hiHouska", "this_is_list_of_words", "helloDevs", "hell")) -> helloWorld, helloDevs
	 * @return List<String>
	 */
	public static List<String> copySortedPartialMatches(String prefix, Iterable<String> originals) {
		List<String> collection = new ArrayList<>();  
		for (String string : originals)
			if (string.length() < prefix.length()?false:string.regionMatches(true, 0, prefix, 0, prefix.length()))
				collection.add(string);
		Collections.sort(collection);
		return collection;
	}
	
	/**
	 * @see see Transfer Collection to String
	 * @return String
	 */
	public static String join(Collection<?> toJoin, String split) {
		if(toJoin == null || split == null)return null;
		String r = "";
		for (Object s : toJoin)
			if(s==null)continue;
			else
			r = r + split + s.toString();
		r = r.replaceFirst(split, "");
		return r;
	}

	/**
	 * @see see Transfer List to String
	 * @return String
	 */
	public static String join(List<?> toJoin, String split) {
		if(toJoin == null || split == null)return null;
		String r = "";
		for (Object s : toJoin)
			if(s==null)continue;
			else
			r = r + split + s.toString();
		r = r.replaceFirst(split, "");
		return r;
	}

	/**
	 * @see see Transfer ArrayList to String
	 * @return String
	 */
	public static String join(ArrayList<?> toJoin, String split) {
		if(toJoin == null || split == null)return null;
		String r = "";
		for (Object s : toJoin)
			if(s==null)continue;
			else
			r = r + split + s.toString();
		r = r.replaceFirst(split, "");
		return r;
	}

	/**
	 * @see see Transfer Object[] to String
	 * @return String
	 */
	public static String join(Object[] toJoin, String split) {
		if(toJoin == null || split == null)return null;
		String r = "";
		for (Object s : toJoin)
			if(s==null)continue;
			else
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
			if(s==null)continue;
			else
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

	private static final Pattern hex = Pattern.compile("#[a-fA-F0-9]{6}");
	public static ColormaticFactory color = new ColormaticFactory() {
		private List<Character> list = Arrays.asList('4','c','6','e','5','d','9','3', 'b','2','a');
		private int i = 0;
		@Override
		public String getColor() {
			if(i>=list.size())i=0;
			return "&"+list.get(i++);
		}
	};
	/**
	 * @see see Colorize string with colors (&eHello world -> {YELLOW}Hello world)
	 * @param string
	 * @return String
	 */
	public static String colorize(String string) {
		if (string == null)return null;
		if(string.toLowerCase().contains("&u")) {
			String recreate = "";
			int mode = 0;
			for(char c : string.toCharArray()) {
				if(c=='&') {
					if(mode==1) { //&&
						recreate+="&"+c;
						mode=0;
					}else
					mode=1;
				}else {
					if(mode==1) {
						mode=0;
						if(Character.toLowerCase(c)=='u') { // &u
							mode=2;
						}else { // ...
							recreate+="&"+c;
						}
					}else {
						if(mode==2) { //&uText..
							if(c==' ')
								recreate+=c;
							else
							recreate+=color.getColor()+c;
						}else
						recreate+=c;
					}
				}
			}
			string=recreate;
		}
		if (TheAPI.isNewerThan(15) && string.contains("#")) {
			string = string.replace("&x", "§x");
			Matcher match = hex.matcher(string);
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
		return buildString(0, args);
	}

	/**
	 * @see see Build string from String[]
	 * @param args
	 * @return String
	 * 
	 */
	public static String buildString(int start, String[] args) {
		return buildString(start, args.length, args);
	}

	/**
	 * @see see Build string from String[]
	 * @param args
	 * @return String
	 * 
	 */
	public static String buildString(int start, int end, String[] args) {
		String msg = "";
		for (int i = start; i < args.length && i < end; ++i)
			msg += (msg.equals("")?"":" ") + args[i];
		return msg;
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
		int r = random.nextInt(list.size());
		if (r <= 0) {
			if (list.get(0) != null) {
				return list.get(0);
			}
			return null;
		} else
			return list.get(r);
	}

	/**
	 * @see see Return random object from list
	 * @param list
	 * @return 
	 * @return Object
	 */
	public static <T> T getRandomFromList(TheList<T> list) {
		if (list.isEmpty() || list == null)
			return null;
		int r = random.nextInt(list.size());
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
		return TheCoder.locationToString(loc);
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
		return TheCoder.locationFromString(savedLocation);
	}

	/**
	 * @see see Get boolean from string
	 * @return boolean
	 */
	public static boolean getBoolean(String fromString) {
		try {
			return fromString.equalsIgnoreCase("true")||fromString.equalsIgnoreCase("yes")||fromString.equalsIgnoreCase("on");
		} catch (Exception er) {
			return false;
		}
	}

	/**
	 * @see see Convert String to Math and Calculate exempt
	 * @return double
	 */
	public static BigDecimal calculate(String fromString) {
		if(fromString==null)return new BigDecimal(0);
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
		if(fromString==null)return 0.0D;
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
		return DOUBLE.matcher(fromString).matches();
	}

	/**
	 * @see see Get long from string
	 * @return long
	 */
	public static long getLong(String fromString) {
		if(fromString==null)return 0L;
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
		if(fromString==null)return 0;
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
		return INTEGER.matcher(fromString).matches();
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
		if(fromString==null)return 0F;
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
		if(fromString==null)return (byte)0;
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
		if(fromString==null)return (short)0;
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
		return fromString.equalsIgnoreCase("true")||fromString.equalsIgnoreCase("false")||fromString.equalsIgnoreCase("yes")||fromString.equalsIgnoreCase("no");
	}
	private static Pattern special = Pattern.compile("[^A-Z-a-z0-9_]+");
	public static boolean containsSpecial(String value) {
		return special.matcher(value).find();
	}

	public static Number getNumber(String o) {
		if(isInt(o))return getInt(o);
		if(isDouble(o))return getDouble(o);
		if(isLong(o))return getLong(o);
		if(isByte(o))return getByte(o);
		if(isShort(o))return getShort(o);
		if(isFloat(o))return getFloat(o);
		return null;
	}
}
