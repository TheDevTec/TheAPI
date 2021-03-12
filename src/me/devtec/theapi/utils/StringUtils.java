package me.devtec.theapi.utils;

import java.awt.Color;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.utils.json.Reader;
import me.devtec.theapi.utils.json.Writer;
import me.devtec.theapi.utils.theapiutils.LoaderClass;

public class StringUtils {
	private static Random random = new Random();

	public static interface ColormaticFactory {
		public String colorize(String text);

		public String getNextColor();
	}

	@SuppressWarnings("unchecked")
	public static String colorizeJson(String json) {
		return Writer.write(colorizeMap((Map<Object, Object>) Reader.read(json)));
	}

	@SuppressWarnings("unchecked")
	public static Map<Object, Object> colorizeMap(Map<Object, Object> json) {
		Map<Object, Object> colorized = new HashMap<>();
		for(Entry<Object, Object> e : json.entrySet()) {
			if(e.getKey() instanceof Collection) {
				if(e.getValue() instanceof Collection) {
					colorized.put(colorizeList((Collection<Object>) e.getKey()), colorizeList((Collection<Object>) e.getValue()));
				}
				if(e.getValue() instanceof Map) {
					colorized.put(colorizeList((Collection<Object>) e.getKey()), colorizeMap((Map<Object, Object>) e.getValue()));
				}
				if(e.getValue() instanceof Object[]) {
					colorized.put(colorizeList((Collection<Object>) e.getKey()), colorizeArray((Object[]) e.getValue()));
				}
				if(e.getValue() instanceof String) {
					colorized.put(colorizeList((Collection<Object>) e.getKey()), colorize((String) e.getValue()));
				}else {
					colorized.put(colorizeList((Collection<Object>) e.getKey()), e.getValue());
				}
			}
			if(e.getKey() instanceof Map) {
				if(e.getValue() instanceof Collection) {
					colorized.put(colorizeMap((Map<Object, Object>) e.getKey()), colorizeList((Collection<Object>) e.getValue()));
				}
				if(e.getValue() instanceof Map) {
					colorized.put(colorizeMap((Map<Object, Object>) e.getKey()), colorizeMap((Map<Object, Object>) e.getValue()));
				}
				if(e.getValue() instanceof Object[]) {
					colorized.put(colorizeMap((Map<Object, Object>) e.getKey()), colorizeArray((Object[]) e.getValue()));
				}
				if(e.getValue() instanceof String) {
					colorized.put(colorizeMap((Map<Object, Object>) e.getKey()), colorize((String) e.getValue()));
				}else {
					colorized.put(colorizeMap((Map<Object, Object>) e.getKey()), e.getValue());
				}
			}
			if(e.getKey() instanceof Object[]) {
				if(e.getValue() instanceof Collection) {
					colorized.put(colorizeArray((Object[]) e.getKey()), colorizeList((Collection<Object>) e.getValue()));
				}
				if(e.getValue() instanceof Map) {
					colorized.put(colorizeArray((Object[]) e.getKey()), colorizeMap((Map<Object, Object>) e.getValue()));
				}
				if(e.getValue() instanceof Object[]) {
					colorized.put(colorizeArray((Object[]) e.getKey()), colorizeArray((Object[]) e.getValue()));
				}
				if(e.getValue() instanceof String) {
					colorized.put(colorizeArray((Object[]) e.getKey()), colorize((String) e.getValue()));
				}else {
					colorized.put(colorizeArray((Object[]) e.getKey()), e.getValue());
				}
			}
			if(e.getKey() instanceof String) {
				if(e.getValue() instanceof Collection) {
					colorized.put(colorize((String) e.getKey()), colorizeList((Collection<Object>) e.getValue()));
				}
				if(e.getValue() instanceof Map) {
					colorized.put(colorize((String) e.getKey()), colorizeMap((Map<Object, Object>) e.getValue()));
				}
				if(e.getValue() instanceof Object[]) {
					colorized.put(colorize((String) e.getKey()), colorizeArray((Object[]) e.getValue()));
				}
				if(e.getValue() instanceof String) {
					colorized.put(colorize((String) e.getKey()), colorize((String) e.getValue()));
				}else {
					colorized.put(colorize((String) e.getKey()), e.getValue());
				}
			}else {
				if(e.getValue() instanceof Collection) {
					colorized.put(e.getKey(), colorizeList((Collection<Object>) e.getValue()));
				}
				if(e.getValue() instanceof Map) {
					colorized.put(e.getKey(), colorizeMap((Map<Object, Object>) e.getValue()));
				}
				if(e.getValue() instanceof Object[]) {
					colorized.put(e.getKey(), colorizeArray((Object[]) e.getValue()));
				}
				if(e.getValue() instanceof String) {
					colorized.put(e.getKey(), colorize((String) e.getValue()));
				}else {
					colorized.put(e.getKey(), e.getValue());
				}
			}
		}
		return colorized;
	}
	
	@SuppressWarnings("unchecked")
	public static Collection<Object> colorizeList(Collection<Object> json) {
		List<Object> colorized = new ArrayList<>();
		for(Object e : json) {
			if(e instanceof Collection) {
				colorized.add(colorizeList((Collection<Object>) e));
			}
			if(e instanceof Map) {
				colorized.add(colorizeMap((Map<Object, Object>) e));
			}
			if(e instanceof Object[]) {
				colorized.add(colorizeArray((Object[]) e));
			}
			if(e instanceof String)
				colorized.add(colorize((String) e));
			else
				colorized.add(e);
		}
		return colorized;
	}
	
	@SuppressWarnings("unchecked")
	public static Object[] colorizeArray(Object[] json) {
		List<Object> colorized = new ArrayList<>();
		for(Object e : json) {
			if(e instanceof Collection) {
				colorized.add(colorizeList((Collection<Object>) e));
			}
			if(e instanceof Map) {
				colorized.add(colorizeMap((Map<Object, Object>) e));
			}
			if(e instanceof Object[]) {
				colorized.add(colorizeArray((Object[]) e));
			}
			if(e instanceof String)
				colorized.add(colorize((String) e));
			else
				colorized.add(e);
		}
		return colorized.toArray();
	}

	/**
	 * @see see Split text correctly with colors
	 */
	public static List<String> fixedSplit(String text, int lengthOfSplit) {
		List<String> splitted = new ArrayList<>();
		String split = text;
		String prefix = "";
		while (split.length() > lengthOfSplit) {
			int length = lengthOfSplit - 1 - prefix.length();
			String a = prefix + split.substring(0, length);
			if (a.endsWith("&") || a.endsWith("§")) {
				--length;
				a = prefix + split.substring(0, length);
				prefix = ChatColor.getLastColors(a);
			} else
				prefix = ChatColor.getLastColors(a);
			splitted.add(a);
			split = split.substring(length);
		}
		splitted.add(prefix + split);
		return splitted;
	}

	/**
	 * @see see Copy matches of String from Iterable<String> Examples:
	 *      StringUtils.copyPartialMatches("hello", Arrays.asList("helloWorld",
	 *      "hiHouska")) -> helloWorld StringUtils.copyPartialMatches("hello",
	 *      Arrays.asList("helloWorld", "hiHouska", "this_is_list_of_words",
	 *      "helloDevs", "hell")) -> helloWorld, helloDevs
	 * @return List<String>
	 */
	public static List<String> copyPartialMatches(String prefix, Iterable<String> originals) {
		List<String> collection = new ArrayList<>();
		for (String string : originals)
			if (string.length() < prefix.length() ? false : string.regionMatches(true, 0, prefix, 0, prefix.length()))
				collection.add(string);
		return collection;
	}

	/**
	 * @see see Copy matches of String from Iterable<String> Examples:
	 *      StringUtils.copyPartialMatches("hello", Arrays.asList("helloWorld",
	 *      "hiHouska")) -> helloWorld StringUtils.copyPartialMatches("hello",
	 *      Arrays.asList("helloWorld", "hiHouska", "this_is_list_of_words",
	 *      "helloDevs", "hell")) -> helloWorld, helloDevs
	 * @return List<String>
	 */
	public static List<String> copySortedPartialMatches(String prefix, Iterable<String> originals) {
		List<String> collection = new ArrayList<>();
		for (String string : originals)
			if (string.length() < prefix.length() ? false : string.regionMatches(true, 0, prefix, 0, prefix.length()))
				collection.add(string);
		Collections.sort(collection);
		return collection;
	}

	/**
	 * @see see Transfer Collection to String
	 * @return String
	 */
	public static String join(Iterable<?> toJoin, String split) {
		if (toJoin == null || split == null)
			return null;
		String r = "";
		for (Object s : toJoin)
			if (s == null)
				continue;
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
		if (toJoin == null || split == null)
			return null;
		String r = "";
		for (Object s : toJoin)
			if (s == null)
				continue;
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
		return new HoverMessage(message);
	}

	/**
	 * @see see Get last colors from String (HEX SUPPORT!)
	 * @return String
	 */
	public static String getLastColors(String last) {
		String color = "";
		String format = "";
		
		char was = 0;
		int count = 0;
		String hex = "";
		boolean hexPart = false;
		for(char c : last.toCharArray()) {
			if(c=='§'||c=='#') {
				was=c;
				continue;
			}
			if((was=='§'||was=='#')&&(Character.isDigit(c)||c=='a'||c=='b'||c=='c'||c=='d'||c=='e'||c=='f')) {
				if(was=='#'||hexPart) {
					hex+=c;
					if(count++==5) {
						was=c;
						color="#"+hex;
						hex="";
						hexPart=false;
						format="";
						count=0;
					}
					continue;
				}else {
					format="";
					color="&"+c;
					hex="";
					count=0;
				}
			}
			if((was=='§')&&(c=='r'||c=='n'||c=='m'||c=='l'||c=='o'||c=='k'||c=='x')) {
				if(c=='r') {
					format=was+"r";
					count=0;
				}else
				if(was=='#') {
					color="";
					hex="";
					count=0;
					format="§"+c;
				}else {
					if(c=='x') {
						hexPart=true;
						count=0;
						hex="";
						continue;
					}else
						if(!format.contains(("§"+c).toLowerCase()))
					format+=("§"+c).toLowerCase();
					count=0;
				}
			}
			was=c;
		}
		return color+format;
	}

	private static final Pattern hex = Pattern.compile("#[a-fA-F0-9]{6}");
	public static ColormaticFactory color;
	static {
		if (TheAPI.isNewerThan(15)) {
			color = new ColormaticFactory() {
				private List<String> list = Arrays.asList("#FF0000", "#FF005C", "#9300A1", "#6400FF", "#1900FF",
						"#0040FF", "#009EFF", "#00F8FF", "#00FF9A", "#00FF37", "#56FF00", "#D1FF00", "#FFB800",
						"#FF6C00", "#FF3800", "#FF0000", "#A62D60", "#54A62D", "#9FB427", "#89400D", "#248698",
						"#6C1599", "#159962", "#74D912");

				@Override
				public String colorize(String text) {
					String one = getNextColor(), second = getNextColor();
					int tries = 0;
					while (one.equals(second)) {
						second = getNextColor();
						if(tries++>=10)break;
					}
					return gradient(text, second, one);
				}

				@Override
				public String getNextColor() {
					return TheAPI.getRandomFromList(list);
				}
			};
		} else
			color = new ColormaticFactory() {
				private List<String> list = Arrays.asList("&4", "&c", "&6", "&e", "&5", "&d", "&9", "&3", "&b", "&2",
						"&a");
				private int i = 0;

				@Override
				public String colorize(String text) {
					String ff = text;
					int length = ff.length();
					HashMap<Integer, String> l = new HashMap<>();
					ff = ff.replace("", "<!>");
					Matcher ma = old.matcher(ff);
					while (ma.find()) {
						switch (ma.group(3).toLowerCase()) {
						case "o":
						case "l":
						case "m":
						case "n":
						case "k":
							l.put((ff.indexOf(ma.group()) / 4 + 1), ma.group(3).toLowerCase());
							break;
						}
						ff = ff.replaceFirst(ma.group(), "");
					}
					boolean bold = false, strikethrough = false, underlined = false, italic = false, magic = false,
							br = false;
					for (int index = 0; index <= length; index++) {
						String formats = "";
						if (l.containsKey(index)) {
							switch (l.get(index)) {
							case "l":
								bold = true;
								break;
							case "m":
								strikethrough = true;
								break;
							case "n":
								underlined = true;
								break;
							case "o":
								italic = true;
								break;
							case "k":
								magic = true;
								break;
							case "u":
							case "r":
								bold = false;
								strikethrough = false;
								italic = false;
								magic = false;
								underlined = false;
								break;
							default:
								br = true;
								bold = false;
								strikethrough = false;
								italic = false;
								magic = false;
								underlined = false;
								break;
							}
						}
						if (bold)
							formats += ChatColor.BOLD;
						if (italic)
							formats += ChatColor.ITALIC;
						if (underlined)
							formats += ChatColor.UNDERLINE;
						if (strikethrough)
							formats += ChatColor.STRIKETHROUGH;
						if (magic)
							formats += ChatColor.MAGIC;
						ff = ff.replaceFirst("<!>", br ? "&" + l.get(index) + formats : color.getNextColor() + formats);
					}
					return ff;
				}

				@Override
				public String getNextColor() {
					if (i >= list.size())
						i = 0;
					return list.get(i++);
				}
			};
	}

	private static Pattern reg = Pattern.compile("&((<!>)*)([Rrk-oK-O])"),
			old = Pattern.compile("&((<!>)*)([XxA-Za-zUu0-9Rrk-oK-O])");

	public static Pattern gradientFinder;
	
	private static String gradient(String msg, String fromHex, String toHex) {
		int length = msg.length();
		boolean bold = false, italic = false, underlined = false, strikethrough = false, magic = false;
		Color fromRGB = Color.decode(fromHex), toRGB = Color.decode(toHex);
		double rStep = Math.abs((double) (fromRGB.getRed() - toRGB.getRed()) / length);
		double gStep = Math.abs((double) (fromRGB.getGreen() - toRGB.getGreen()) / length);
		double bStep = Math.abs((double) (fromRGB.getBlue() - toRGB.getBlue()) / length);
		if (fromRGB.getRed() > toRGB.getRed())
			rStep = -rStep;
		if (fromRGB.getGreen() > toRGB.getGreen())
			gStep = -gStep;
		if (fromRGB.getBlue() > toRGB.getBlue())
			bStep = -bStep;
		Color finalColor = new Color(fromRGB.getRGB());
		HashMap<Integer, String> l = new HashMap<>();
		msg = msg.replaceAll("#[A-Fa-f0-9]{6}", "");
		msg = msg.replace("", "<!>");
		Matcher ma = reg.matcher(msg);
		while (ma.find()) {
			l.put((msg.indexOf(ma.group()) / 4 + 1), ma.group(3).toLowerCase());
			msg = msg.replaceFirst(ma.group(), "");
		}
		for (int index = 0; index <= length; index++) {
			int red = (int) Math.round(finalColor.getRed() + rStep);
			int green = (int) Math.round(finalColor.getGreen() + gStep);
			int blue = (int) Math.round(finalColor.getBlue() + bStep);
			if (red > 255)
				red = 255;
			if (red < 0)
				red = 0;
			if (green > 255)
				green = 255;
			if (green < 0)
				green = 0;
			if (blue > 255)
				blue = 255;
			if (blue < 0)
				blue = 0;
			finalColor = new Color(red, green, blue);
			String hex = "#" + Integer.toHexString(finalColor.getRGB()).substring(2);
			String formats = "";
			if (l.containsKey(index))
				switch (l.get(index)) {
				case "l":
					bold = true;
					break;
				case "m":
					strikethrough = true;
					break;
				case "n":
					underlined = true;
					break;
				case "o":
					italic = true;
					break;
				case "k":
					magic = true;
					break;
				default:
					bold = false;
					strikethrough = false;
					italic = false;
					magic = false;
					underlined = false;
					break;
				}
			if (bold)
				formats += ChatColor.BOLD;
			if (italic)
				formats += ChatColor.ITALIC;
			if (underlined)
				formats += ChatColor.UNDERLINE;
			if (strikethrough)
				formats += ChatColor.STRIKETHROUGH;
			if (magic)
				formats += ChatColor.MAGIC;
			msg = msg.replaceFirst("<!>", hex + formats);
		}
		return msg;
	}
	
	public static String gradient(String legacyMsg) {
		for (String code : LoaderClass.colorMap.keySet()) {
			String rawCode = LoaderClass.tagG + code;
			if (!legacyMsg.toLowerCase().contains(rawCode))
				continue;
			legacyMsg = legacyMsg.replace(rawCode, LoaderClass.colorMap.get(code));
		}
		if(gradientFinder!=null) {
		Matcher matcher = gradientFinder.matcher(legacyMsg);
		while(matcher.find()) {
			if(matcher.groupCount()==0 || matcher.group(1)==null)
				continue;
			String hexA = matcher.group(1);
			String hexB = matcher.group(3);
			String text = matcher.group(2);
			legacyMsg=legacyMsg.replaceFirst(matcher.group(),gradient(text, hexA, hexB));
		}}
		return legacyMsg;
	}

	private static boolean neww = TheAPI.isNewerThan(15);
	
	/**
	 * @see see Colorize string with colors (&eHello world -> {YELLOW}Hello world)
	 * @param string
	 * @return String
	 */
	public static String colorize(String msg) {
		if (msg == null)
			return null;
		msg=msg.replace("§", "&");
		if (msg.toLowerCase().contains("&u")) {
			List<String> s = new ArrayList<>();
			StringBuffer d = new StringBuffer();
			int found = 0;
			for (char c : msg.toCharArray()) {
				if (c == '&') {
					if (found == 1)
						d.append(c);
					found = 1;
					continue;
				}
				if (found == 1 && Pattern.compile("[XxA-Fa-fUu0-9]").matcher(c + "").find()) {
					found = 0;
					s.add(d.toString());
					d = d.delete(0, d.length());
					d.append("&" + c);
					continue;
				}
				if (found == 1) {
					found = 0;
					d.append("&" + c);
					continue;
				}
				found = 0;
				d.append(c);
			}
			if (d.length() != 0)
				s.add(d.toString());
			d = d.delete(0, d.length());
			for (String ff : s) {
				if (ff.toLowerCase().startsWith("&u"))
					ff=color.colorize(ff.substring(2));
				d.append(ff);
			}
			s.clear();
			msg = d.toString();
		}
		if (neww) {
			msg = gradient(msg);
			if (msg.contains("#") || msg.contains("&x")) {
				msg = msg.replace("&x", "§x");
				Matcher match = hex.matcher(msg);
				while (match.find()) {
					String color = match.group();
					StringBuilder magic = new StringBuilder("§x");
					char[] c = color.substring(1).toCharArray();
					for (int i = 0; i < c.length; ++i)
						magic.append(("&" + c[i]).toLowerCase());
					msg = msg.replace(color, magic.toString() + "");
				}
			}
		}
		return ChatColor.translateAlternateColorCodes('&', msg);
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
			msg += (msg.equals("") ? "" : " ") + args[i];
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

	private static final Pattern periodPattern = Pattern.compile(
			"([+-]*[0-9]+)(m[o]+[n]*[t]*[h]*[s]*|m[i]*[n]*[u]*[t]*[e]*[s]*|y[e]*[a]*[r]*[s]*|w[e]*[k]*[s]*|h[o]*[u]*[r]*[s]*|s[e]*[c]*[o]*[n]*[d]*[s]*|d[a]*[y]*[s]*)",
			Pattern.CASE_INSENSITIVE);

	/**
	 * @see see Get long from string
	 * @param s String
	 * @return long
	 */
	public static long getTimeFromString(String period) {
		return timeFromString(period);
	}

	/**
	 * @see see Get long from string
	 * @param s String
	 * @return long
	 */
	public static long timeFromString(String period) { // New shorter name of method
		if (period == null || period.trim().isEmpty())
			return 0;
		period = period.toLowerCase(Locale.ENGLISH);
		if (isLong(period))
			return getLong(period);
		Matcher matcher = periodPattern.matcher(period);
		float time = 0;
		while (matcher.find()) {
			long num = getLong(matcher.group(1));
			if (num == 0)
				continue;
			String typ = matcher.group(2);
			if (typ.toLowerCase().startsWith("s")) {
				time += num;
			}
			if (typ.toLowerCase().equals("m") || typ.toLowerCase().startsWith("mi")) {
				time += num * 60;
			}
			if (typ.toLowerCase().startsWith("h")) {
				time += num * 3600;
			}
			if (typ.toLowerCase().startsWith("d")) {
				time += num * 86400;
			}
			if (typ.toLowerCase().startsWith("w")) {
				time += num * 86400 * 7;
			}
			if (typ.toLowerCase().startsWith("mo")) {
				time += num * 86400 * 31;
			}
			if (typ.toLowerCase().startsWith("y")) {
				time += num * 86400 * 31 * 12;
			}
		}
		return (long) time;
	}

	/**
	 * @see see Set long to string
	 * @param period long
	 * @return String
	 */
	public static String setTimeToString(long period) {
		return timeToString(period);
	}

	/**
	 * @see see Set long to string
	 * @param time long
	 * @return String
	 */
	public static String timeToString(long time) {
		if(time==0)
			return "0s";
		long minutes = (time / 60) % 60;
		long hours = (time / 3600) % 24;
		long days = (time / 86400) % 31;
		long month = 0;
		long year = 0;
		try{
			month = (time / 86400 / 31) % 12;
			year=time / 86400 / 31 / 12;
		}catch(Exception er) {}
		String date = "";
		if (year > 0)
			date = year + "y";
		if (month > 0)
			date += month + "mo";
		if (days > 0)
			date += days + "d";
		if (hours > 0)
			date += hours + "h";
		if (minutes > 0)
			date += minutes + "m";
		if (time % 60 > 0)
			date += (time % 60) + "s";
		return date;
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
	public static String locationAsString(Location loc) { // New shorter name of method
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
	public static Location locationFromString(String savedLocation) { // New shorter name of method
		return TheCoder.locationFromString(savedLocation);
	}

	/**
	 * @see see Get boolean from string
	 * @return boolean
	 */
	public static boolean getBoolean(String fromString) {
		try {
			return fromString.equalsIgnoreCase("true") || fromString.equalsIgnoreCase("yes")
					|| fromString.equalsIgnoreCase("on");
		} catch (Exception er) {
			return false;
		}
	}

	private static ScriptEngine engine;
	static{
		try {
			engine= new ScriptEngineManager().getEngineFactories().get(0).getScriptEngine();
		}catch(Exception err) {
			engine= new ScriptEngineManager().getEngineByName("JavaScript");
		}
	}
	
	/**
	 * @see see Convert String to Math and Calculate exempt
	 * @return double
	 */
	public static BigDecimal calculate(String fromString) {
		if (fromString == null)
			return new BigDecimal(0);
		String a = fromString.replaceAll("[^0-9E+.,()*/-]+", "").replace(",", ".");
		try {
			return new BigDecimal("" + engine.eval(a));
		} catch (Exception e) {
		}
		return new BigDecimal(0);
	}

	/**
	 * @see see Get double from string
	 * @return double
	 */
	public static double getDouble(String fromString) {
		if (fromString == null)
			return 0.0D;
		String a = fromString.replaceAll("[^+0-9E.,-]+", "").replace(",", ".");
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
			return true;
		} catch (Exception err) {
			return false;
		}
	}

	/**
	 * @see see Get long from string
	 * @return long
	 */
	public static long getLong(String fromString) {
		return (long) getFloat(fromString);
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
		return (int) (getDouble(fromString) == 0 ? getLong(fromString) : getDouble(fromString));
	}

	/**
	 * @see see Is string, int ?
	 * @return boolean
	 */
	public static boolean isInt(String fromString) {
		try {
			Integer.parseInt(fromString);
			return true;
		} catch (Exception err) {
			return false;
		}
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
		if (fromString == null)
			return 0F;
		String a = fromString.replaceAll("[^+0-9E.,-]+", "").replace(",", ".");
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
		if (fromString == null)
			return (byte) 0;
		String a = fromString.replaceAll("[^+0-9E-]+", "");
		if (isByte(a)) {
			return Byte.parseByte(a);
		} else {
			return (byte) 0;
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
		if (fromString == null)
			return (short) 0;
		String a = fromString.replaceAll("[^+0-9E-]+", "");
		if (isShort(a)) {
			return Short.parseShort(a);
		} else {
			return (short) 0;
		}
	}

	/**
	 * @see see Is string, number ?
	 * @return boolean
	 */
	public static boolean isNumber(String fromString) {
		return isInt(fromString) || isDouble(fromString) || isLong(fromString) || isByte(fromString)
				|| isShort(fromString) || isFloat(fromString);
	}

	/**
	 * @see see Is string, boolean ?
	 * @return boolean
	 */
	public static boolean isBoolean(String fromString) {
		if (fromString == null)
			return false;
		return fromString.equalsIgnoreCase("true") || fromString.equalsIgnoreCase("false")
				|| fromString.equalsIgnoreCase("yes") || fromString.equalsIgnoreCase("no");
	}

	private static Pattern special = Pattern.compile("[^A-Z-a-z0-9_]+");

	public static boolean containsSpecial(String value) {
		return special.matcher(value).find();
	}

	public static Number getNumber(String o) {
		if (isInt(o))
			return getInt(o);
		if (isDouble(o))
			return getDouble(o);
		if (isLong(o))
			return getLong(o);
		if (isByte(o))
			return getByte(o);
		if (isShort(o))
			return getShort(o);
		if (isFloat(o))
			return getFloat(o);
		return null;
	}
}
