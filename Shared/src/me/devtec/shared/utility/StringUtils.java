package me.devtec.shared.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
	
	// INIT THIS
	
	public static ColormaticFactory color;
	public static Pattern rainbowSplit;
	//TIME UTILS
	public static Pattern sec;
	public static Pattern min;
	public static Pattern hour;
	public static Pattern day;
	public static Pattern week;
	public static Pattern mon;
	public static Pattern year;
	public static final Map<String, List<String>> actions = new ConcurrentHashMap<>();
	//COLOR UTILS
	public static Pattern gradientFinder;
	
	//VARRIABLE INIT
	public static Map<String, String> colorMap = new ConcurrentHashMap<>();
	public static String tagPrefix = "!";
	public static String timeSplit = ":";
	public static String timeFormat = "%time% %format%";
	
	//DO NOT TOUCH
	
	private static final Random random = new Random();
	//SPECIAL CHARS
	private static final Pattern special = Pattern.compile("[^A-Z-a-z0-9_]+");
	//FORMAT
	private static final Pattern mat = Pattern.compile("\\.([0-9])([0-9])?");
	//CALCULATOR
	private static final Pattern extra = Pattern.compile("((^[-])?[ ]*[0-9.]+)[ ]*([*/])[ ]*(-?[ ]*[0-9.]+)");
	private static final Pattern normal = Pattern.compile("((^[-])?[ ]*[0-9.]+)[ ]*([+-])[ ]*(-?[ ]*[0-9.]+)");
	

	public interface ColormaticFactory {
		public String gradient(String msg, String fromHex, String toHex);

		public String generateColor();
		
		public String[] getLastColors(String text);

		public String replaceHex(String msg);
	}

	/**
	 * @apiNote Generate random int with limit
	 * @param maxInt
	 * @return int
	 */
	public static int generateRandomInt(int maxInt) {
		return generateRandomInt(0, maxInt);
	}

	/**
	 * @apiNote Generate random double with limit
	 * @param maxDouble
	 * @return double
	 */
	public static double generateRandomDouble(double maxDouble) {
		return generateRandomDouble(0, maxDouble);
	}

	/**
	 * @apiNote Generate random double with limit
	 * @param maxDouble
	 * @return double
	 */
	public static double generateRandomDouble(double min, double maxDouble) {
		if (maxDouble == 0)
			return maxDouble;
		boolean a = maxDouble < 0;
		if (a)
			maxDouble *= -1;
		double i = random.nextInt((int) maxDouble) + random.nextDouble();
		if (i < (min < 0 ? min * -1 : min))
			return min;
		if (i > maxDouble)
			i = maxDouble;
		return a ? -1 * i : i;
	}

	/**
	 * @apiNote Generate random double with limit
	 * @param maxDouble
	 * @return double
	 */
	public static int generateRandomInt(int min, int maxInt) {
		if (maxInt == 0)
			return maxInt;
		boolean a = maxInt < 0;
		if (a)
			maxInt *= -1;
		int i = random.nextInt(maxInt);
		if (i < (min < 0 ? min * -1 : min))
			return min;
		if (i > maxInt)
			i = maxInt;
		return a ? -1 * i : i;
	}
	
	/**
	 * @apiNote Split text correctly with colors
	 */
	public static List<String> fixedSplit(String text, int lengthOfSplit) {
		if(text==null)return null;
		List<String> splitted = new ArrayList<>();
		String split = text;
		String prefix = "";
		while (split.length() > lengthOfSplit) {
			int length = lengthOfSplit - 1 - prefix.length();
			String a = prefix + split.substring(0, length);
			if (a.endsWith("§")||a.endsWith("&")) {
				--length;
				a = prefix + split.substring(0, length);
			}
			prefix = getLastColors(a);
			splitted.add(a);
			split = split.substring(length);
		}
		if(!(prefix + split).isEmpty())
		splitted.add(prefix + split);
		return splitted;
	}

	/**
	 * @apiNote Copy matches of String from Iterable<String> Examples:
	 *      StringUtils.copyPartialMatches("hello", Arrays.asList("helloWorld",
	 *      "hiHouska")) -> helloWorld StringUtils.copyPartialMatches("hello",
	 *      Arrays.asList("helloWorld", "hiHouska", "this_is_list_of_words",
	 *      "helloDevs", "hell")) -> helloWorld, helloDevs
	 * @return List<String>
	 */
	public static List<String> copyPartialMatches(String prefix, Iterable<String> originals) {
		List<String> collection = new ArrayList<>();
		for (String string : originals)
			if (string == null || (string.length() >= prefix.length()) && (string.regionMatches(true, 0, prefix, 0, prefix.length())||string.regionMatches(true, 1, prefix, 0, prefix.length())))
				collection.add(string);
		return collection;
	}

	/**
	 * @apiNote Copy matches of String from Iterable<String> Examples:
	 *      StringUtils.copyPartialMatches("hello", Arrays.asList("helloWorld",
	 *      "hiHouska")) -> helloWorld StringUtils.copyPartialMatches("hello",
	 *      Arrays.asList("helloWorld", "hiHouska", "this_is_list_of_words",
	 *      "helloDevs", "hell")) -> helloWorld, helloDevs
	 * @return List<String>
	 */
	public static List<String> copySortedPartialMatches(String prefix, Iterable<String> originals) {
		List<String> collection = new ArrayList<>();
		for (String string : originals)
			if (string == null || (string.length() >= prefix.length()) && (string.regionMatches(true, 0, prefix, 0, prefix.length())||string.regionMatches(true, 1, prefix, 0, prefix.length())))
				collection.add(string);
		Collections.sort(collection);
		return collection;
	}

	/**
	 * @apiNote Transfer Collection to String
	 * @return String
	 */
	public static String join(Iterable<?> toJoin, String split) {
		if (toJoin == null || split == null)
			return null;
		StringBuilder r = new StringBuilder();
		for (Object s : toJoin)
			if (s == null)
				continue;
			else
				r.append(split).append(s);
		if(r.length()!=0)
			r.delete(0,split.length());
		return r.toString();
	}

	/**
	 * @apiNote Transfer Object[] to String
	 * @return String
	 */
	public static String join(Object[] toJoin, String split) {
		if (toJoin == null || split == null)
			return null;
		StringBuilder r = new StringBuilder();
		for (Object s : toJoin)
			if (s == null)
				continue;
			else
				r.append(split).append(s);
		if(r.length()!=0)
			r.delete(0,split.length());
		return r.toString();
	}
	
	/**
	 * @apiNote Get last colors from String (HEX SUPPORT!)
	 * @return String
	 */
	public static String getLastColors(String text) {
		String[] split = StringUtils.color.getLastColors(text);
		return split[0]+split[1];
	}
	
	/**
	 * @apiNote Get last colors from String (HEX SUPPORT!)
	 * @return String
	 */
	public static String[] getLastColorsSplitFormats(String text) {
		return StringUtils.color.getLastColors(text);
	}

	public static String gradient(String legacyMsg) {
		if(legacyMsg==null||gradientFinder == null)
			return legacyMsg;
		String low = legacyMsg.toLowerCase();
		for (Entry<String, String> code : colorMap.entrySet()) {
			String rawCode = (tagPrefix + code.getKey()).toLowerCase();
			if (!low.contains(rawCode))
				continue;
			legacyMsg = legacyMsg.replace(rawCode, code.getValue());
		}
		Matcher matcher = gradientFinder.matcher(legacyMsg);
		while (matcher.find()) {
			if (matcher.groupCount() == 0||matcher.group().isEmpty())continue;
			String replace = StringUtils.color.gradient(matcher.group(2), matcher.group(1), matcher.group(3));
			if(replace==null)continue;
			legacyMsg = legacyMsg.replace(matcher.group(), replace);
		}
		return legacyMsg;
	}
	
	/**
	 * @apiNote Colorize string with colors (&eHello world -> {YELLOW}Hello world)
	 * @param msg
	 * @return String
	 */
	public static String colorize(String msg) {
		if (msg == null||msg.trim().isEmpty())
			return msg;
		if (msg.toLowerCase().contains("&u")) {
			String[] split = rainbowSplit.split(msg);
			// atempt to add colors to split
			Matcher m = rainbowSplit.matcher(msg);
			int id = 1;
			while (m.find()) {
				try {
					split[id] = m.group(1) + split[id++];
				} catch (Exception err) {
				}
			}
			// colors
			StringBuilder d = new StringBuilder(msg.length());
			for (String ff : split) {
				if (ff.contains("&u"))
					ff = StringUtils.color.gradient(ff.replace("&u", ""), StringUtils.color.generateColor(), StringUtils.color.generateColor());
				d.append(ff);
			}
			msg = d.toString();
		}
		msg = gradient(msg);
		if (msg.contains("#")) {
			msg = StringUtils.color.replaceHex(msg);
		}
		char[] b = msg.toCharArray();
	    for (int i = 0; i < b.length - 1; i++) {
	      if (b[i] == '&' && has(b[i + 1])) {
	        b[i] = '§';
	        b[i + 1] = lower(b[i + 1]);
	      }
	    }
	    return new String(b);
	}

	private static boolean has(int c) {
		return c<=102 && c>=97 || c<=57 && c>=48 || c<=70 && c>=65 || c<=79 && c>=75 || c<=111 && c>=107 || c==114 || c==82 || c==88 || c==120;
	}

	private static char lower(int c) {
		switch(c) {
		case 65:
		case 66:
		case 67:
		case 68:
		case 69:
		case 70:
		case 75:
		case 76:
		case 77:
		case 78:
		case 79:
		case 82:
			return (char)(c+32);
		case 120:
			return (char)88;
			default:
				return (char)c;
		}
	}

	/**
	 * @apiNote Build string from String[]
	 * @param args
	 * @return String
	 * 
	 */
	public static String buildString(String[] args) {
		return buildString(0, args);
	}

	/**
	 * @apiNote Build string from String[]
	 * @param args
	 * @return String
	 * 
	 */
	public static String buildString(int start, String[] args) {
		return buildString(start, args.length, args);
	}

	/**
	 * @apiNote Build string from String[]
	 * @param args
	 * @return String
	 * 
	 */
	public static String buildString(int start, int end, String[] args) {
		StringBuilder msg = new StringBuilder();
		for (int i = start; i < args.length && i < end; ++i)
			msg.append(' ').append(args[i]);
		if(msg.length()!=0)
			msg.delete(0,1);
		return msg.toString();
	}

	/**
	 * @apiNote Return random object from list
	 */
	public static <T> T getRandomFromList(List<T> list) {
		if (list==null ||list.isEmpty())
			return null;
		return list.get(random.nextInt(list.size()));
	}

	/**
	 * @apiNote Return random object from collection
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getRandomFromCollection(Collection<T> list) {
		if (list==null ||list.isEmpty())
			return null;
		if(list instanceof List)return getRandomFromList((List<T>)list);
		return (T) list.toArray()[random.nextInt(list.size())];
	}

	/**
	 * @apiNote Get long from string
	 * @param period String
	 * @return long
	 */
	public static long getTimeFromString(String period) {
		return timeFromString(period);
	}

	/**
	 * @apiNote Get long from string
	 * @param period String
	 * @return long
	 */
	public static long timeFromString(String period) {
		if (period == null || period.isEmpty())
			return 0;
		if (isFloat(period) && !period.endsWith("d") && !period.endsWith("e"))
			return (long)getFloat(period);
		float time = 0;
		
		Matcher matcher = sec.matcher(period);
		while (matcher.find()) {
			time+=getFloat(matcher.group());
			period=period.replace(matcher.group(), "");
		}
		matcher = min.matcher(period);
		while (matcher.find()) {
			time+=getFloat(matcher.group())*60;
			period=period.replace(matcher.group(), "");
		}
		matcher = hour.matcher(period);
		while (matcher.find()) {
			time+=getFloat(matcher.group())*3600;
			period=period.replace(matcher.group(), "");
		}
		matcher = day.matcher(period);
		while (matcher.find()) {
			time+=getFloat(matcher.group())*86400;
			period=period.replace(matcher.group(), "");
		}
		matcher = week.matcher(period);
		while (matcher.find()) {
			time+=getFloat(matcher.group())* 86400 * 7;
			period=period.replace(matcher.group(), "");
		}
		matcher = mon.matcher(period);
		while (matcher.find()) {
			time+=getFloat(matcher.group())* 86400 * 31;
			period=period.replace(matcher.group(), "");
		}
		matcher = year.matcher(period);
		while (matcher.find()) {
			time+=getFloat(matcher.group())* 86400 * 31 * 12;
			period=period.replace(matcher.group(), "");
		}
		return (long) time;
	}

	/**
	 * @apiNote Set long to string
	 * @param period long
	 * @return String
	 */
	public static String setTimeToString(long period) {
		return timeToString(period);
	}

	private static String findCorrectFormat(int i, String string) {
		String result = i+string;
		for(String s : actions.get(string)) {
			if(s.startsWith("=,") ? getInt(s.substring(1).split(",")[1]) == i :
				s.startsWith("<,") ? getInt(s.substring(1).split(",")[1]) >= i : 
					s.startsWith(">,") ? getInt(s.substring(1).split(",")[1]) <= i : false)return s.substring(3+s.substring(1).split(",")[1].length());
		}
		return result;
	}
	
	private static String format(int time, String section) {
		return timeFormat.replace("%time%", ""+time).replace("%format%", findCorrectFormat(time,section));
	}

	/**
	 * @apiNote Set long to string
	 * @param time long
	 * @return String
	 */
	public static String timeToString(long time) {
		if (time == 0)
			return format(0, "Seconds");
		int minutes = (int)(time / 60) % 60;
		int hours = (int)(time / 3600) % 24;
		int days = (int)(time / 86400) % 31;
		int month = 0;
		int year = 0;
		try {
			month = (int)(time / 86400 / 31) % 12;
			year = (int)time / 86400 / 31 / 12;
		} catch (Exception er) {
		}
		StringBuilder date = new StringBuilder(64);
		if (year > 0)
			date.append(format(year, "Years"));
		if (month > 0) {
			if(date.length()!=0)
				date.append(timeSplit);
			date.append(format(month, "Months"));
		}
		if (days > 0) {
			if(date.length()!=0)
				date.append(timeSplit);
			date.append(format(days, "Days"));
		}
		if (hours > 0) {
			if(date.length()!=0)
				date.append(timeSplit);
			date.append(format(hours, "Hours"));
		}
		if (minutes > 0) {
			if(date.length()!=0)
				date.append(timeSplit);
			date.append(format(minutes, "Minutes"));
		}
		if (time % 60 > 0) {
			if(date.length()!=0)
				date.append(timeSplit);
			date.append(format((int)(time % 60), "Seconds"));
		}
		return date.toString();
	}

	/**
	 * @apiNote Get boolean from string
	 * @return boolean
	 */
	public static boolean getBoolean(String fromString) {
		try {
			return fromString.equalsIgnoreCase("true") || fromString.equalsIgnoreCase("yes");
		} catch (Exception er) {
			return false;
		}
	}
	
	/**
	 * @apiNote Convert String to Math and Calculate exempt
	 * @return double
	 */
	public static double calculate(String val) {
		if(val.contains("(")&&val.contains(")")) {
			val=splitter(val);
		}
		if(val.contains("*")||val.contains("/")) {
			Matcher s = extra.matcher(val);
			while(s.find()) {
				double a = getDouble(s.group(1));
				String b = s.group(3);
				double d = getDouble(s.group(4));
				val=val.replace(s.group(), (a==0||d==0?0:((b.equals("*")?a*d:a/d)))+"");
				s.reset(val);
			}
		}
		if(val.contains("+")||val.contains("-")) {
			Matcher s = normal.matcher(val);
			while(s.find()) {
				double a = getDouble(s.group(1));
				String b = s.group(3);
				double d = getDouble(s.group(4));
				val=val.replace(s.group(), (b.equals("+")?a+d:a-d)+"");
				s.reset(val);
			}
		}
		return getDouble(val.replaceAll("[^0-9+.-]", ""));
	}

	private static String splitter(String s) {
		StringBuilder i = new StringBuilder();
		StringBuilder fix = new StringBuilder();

		int count = 0;
		int waiting = 0;
		for(char c : s.toCharArray()) {
			i.append(c);
			if(c=='(') {
				fix.append(c);
				waiting=1;
				++count;
			}else
			if(c==')') {
				fix.append(c);
				if(--count==0) {
					waiting=0;
					i = new StringBuilder(i.toString().replace(fix.toString(), "" + calculate(fix.substring(1, fix.length() - 1))));
					fix.delete(0, fix.length());
				}
			}else
			if(waiting==1)
				fix.append(c);
		}
		return i.toString();
	}
	
	public static String fixedFormatDouble(double val) {
		String text = String.format(Locale.ENGLISH, "%.2f", val);
		Matcher m = mat.matcher(text);
		if (m.find()) {
			if (m.groupCount() != 2) {
				if (m.group(1).equals("0")) {
					return m.replaceFirst("");
				}
				return m.replaceFirst(".$1");
			}
			if (m.group(1).equals("0")) {
				if (m.group(2).equals("0")) {
					return m.replaceFirst("");
				}
				return m.replaceFirst(".$1$2");
			}
			if (m.group(2).equals("0")) {
				return m.replaceFirst(".$1");
			}
			return m.replaceFirst(".$1$2");
		}
		return text;
	}

	/**
	 * @apiNote Get double from string
	 * @return double
	 */
	public static double getDouble(String fromString) {
		if (fromString == null)
			return 0.0D;
		String a = fromString.replaceAll("[^+0-9E.,-]+", "").replace(",", ".");
		try {
			return Double.parseDouble(a);
		} catch (NumberFormatException e) {
		}
		return 0.0D;
	}

	/**
	 * @apiNote Is string, double ?
	 * @return boolean
	 */
	public static boolean isDouble(String fromString) {
		try {
			Double.parseDouble(fromString);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * @apiNote Get long from string
	 * @return long
	 */
	public static long getLong(String fromString) {
		if (fromString == null)
			return 0L;
		String a = fromString.replaceAll("[^+0-9E.,-]+", "").replace(",", ".");
		try {
			return Long.parseLong(a);
		} catch (NumberFormatException e) {
		}
		return 0L;
	}

	/**
	 * @apiNote Is string, long ?
	 * @return boolean
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
	 * @apiNote Get int from string
	 * @return int
	 */
	public static int getInt(String fromString) {
		if (fromString == null)
			return 0;
		String a = fromString.replaceAll("[^+0-9E.,-]+", "").replace(",", ".");
		if(!a.contains(".")) {
			try {
				return Integer.parseInt(a);
			} catch (NumberFormatException e) {
			}
			try {
				return (int)Long.parseLong(a);
			} catch (NumberFormatException e) {
			}
		}
		try {
			return (int)Double.parseDouble(a);
		} catch (NumberFormatException e) {
		}
		return 0;
	}

	/**
	 * @apiNote Is string, int ?
	 * @return boolean
	 */
	public static boolean isInt(String fromString) {
		try {
			Integer.parseInt(fromString);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * @apiNote Is string, float ?
	 * @return boolean
	 */
	public static boolean isFloat(String fromString) {
		try {
			Float.parseFloat(fromString);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * @apiNote Get float from string
	 * @return float
	 */
	public static float getFloat(String fromString) {
		if (fromString == null)
			return 0F;
		String a = fromString.replaceAll("[^+0-9E.,-]+", "").replace(",", ".");
		try {
			return Float.parseFloat(a);
		} catch (NumberFormatException e) {
		}
		return 0;
	}

	/**
	 * @apiNote Is string, float ?
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
	 * @apiNote Get float from string
	 * @return float
	 */
	public static byte getByte(String fromString) {
		if (fromString == null)
			return (byte) 0;
		String a = fromString.replaceAll("[^+0-9E-]+", "");
		try {
			return Byte.parseByte(a);
		} catch (NumberFormatException e) {
		}
		return 0;
	}

	/**
	 * @apiNote Is string, float ?
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
	 * @apiNote Get float from string
	 * @return float
	 */
	public static short getShort(String fromString) {
		if (fromString == null)
			return (short) 0;
		String a = fromString.replaceAll("[^+0-9E-]+", "");
		try {
			return Short.parseShort(a);
		} catch (NumberFormatException e) {
		}
		return 0;
	}

	/**
	 * @apiNote Is string, number ?
	 * @return boolean
	 */
	public static boolean isNumber(String fromString) {
		return isInt(fromString) || isDouble(fromString) || isLong(fromString) || isByte(fromString)
				|| isShort(fromString) || isFloat(fromString);
	}

	/**
	 * @apiNote Is string, boolean ?
	 * @return boolean
	 */
	public static boolean isBoolean(String fromString) {
		if (fromString == null)
			return false;
		return fromString.equalsIgnoreCase("true") || fromString.equalsIgnoreCase("false")
				|| fromString.equalsIgnoreCase("yes") || fromString.equalsIgnoreCase("no");
	}
	
	public static boolean containsSpecial(String value) {
		return special.matcher(value).find();
	}

	public static Number getNumber(String o) {
		if(o==null)return null;
		if(!o.contains(".")) {
			if (isInt(o))
				return getInt(o);
			if (isLong(o))
				return getLong(o);
			if (isByte(o))
				return getByte(o);
			if (isShort(o))
				return getShort(o);
		}
		if (isDouble(o))
			return getDouble(o);
		if (isFloat(o))
			return getFloat(o);
		return null;
	}
}
