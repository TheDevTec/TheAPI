package me.devtec.shared.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.devtec.shared.API;
import me.devtec.shared.Ref;

public class StringUtils {

	// INIT THIS

	public static ColormaticFactory color;
	public static Pattern rainbowSplit;
	// TIME UTILS
	// string -> time
	// time -> string
	public static final Map<TimeFormat, TimeFormatter> timeConvertor = new ConcurrentHashMap<>();
	// COLOR UTILS
	public static Pattern gradientFinder;

	// VARRIABLE INIT
	public static Map<String, String> colorMap = new ConcurrentHashMap<>();
	public static String tagPrefix = "!";
	public static String timeSplit = " ";

	// DO NOT TOUCH

	public static final Random random = new Random();
	// SPECIAL CHARS
	private static final Pattern special = Pattern.compile("[^A-Z-a-z0-9_]+");
	// CALCULATOR
	private static final Pattern extra = Pattern.compile("((^[-])?[ ]*[0-9.]+)[ ]*([*/])[ ]*(-?[ ]*[0-9.]+)");
	private static final Pattern normal = Pattern.compile("((^[-])?[ ]*[0-9.]+)[ ]*([+-])[ ]*(-?[ ]*[0-9.]+)");

	public enum TimeFormat {
		YEARS(31556926, 0), MONTHS(2629743.83, 12), WEEKS(604800, 4.34812141), DAYS(86400, 30.4368499), HOURS(3600, 24),
		MINUTES(60, 60), SECONDS(1, 60);

		private double multiplier;
		private double cast;

		TimeFormat(double multiplier, double cast) {
			this.multiplier = multiplier;
			this.cast = cast;
		}

		public double multiplier() {
			return multiplier;
		}

		public double cast() {
			return cast;
		}
	}

	public interface TimeFormatter {
		/**
		 * @apiNote Nullable if settings isn't supported
		 */
		public String toString(long value);

		public Matcher matcher(String text);
	}

	public interface ColormaticFactory {

		/**
		 * @apiNote Generates random color depends on software & version
		 */
		public String generateColor();

		/**
		 * @apiNote @see {@link API#basics()}
		 */
		public String[] getLastColors(String text);

		/**
		 * @apiNote Replace #RRGGBB hex color depends on software
		 */
		public String replaceHex(String msg);

		/**
		 * @apiNote @see {@link API#basics()}
		 */
		public String gradient(String msg, String fromHex, String toHex);

		/**
		 * @apiNote @see {@link API#basics()}
		 */
		public String rainbow(String msg, String generateColor, String generateColor2);
	}

	public enum FormatType {
		BASIC, // Basic format - xxx.xx
		NORMAL, // Improved BASIS format - xxx,xxx.xx
		COMPLEX // NORMAL format + balance type
	}

	public static String formatDouble(FormatType type, double value) {
		switch (type) {
		case BASIC: {
			String formatted = String.format(Locale.ENGLISH, "%.2f", value);
			if (formatted.endsWith("00"))
				formatted = formatted.substring(0, formatted.length() - 3); // .00
			else if (formatted.endsWith("0"))
				formatted = formatted.substring(0, formatted.length() - 1); // .X0
			return formatted;
		}
		case NORMAL: {
			String formatted = String.format(Locale.ENGLISH, "%,.2f", value);
			if (formatted.endsWith("00"))
				formatted = formatted.substring(0, formatted.length() - 3); // .00
			else if (formatted.endsWith("0"))
				formatted = formatted.substring(0, formatted.length() - 1); // .X0
			return formatted;
		}
		case COMPLEX: {
			String formatted = String.format(Locale.ENGLISH, "%,.2f", value);
			String[] s = formatted.split(",");
			if (s.length >= 22) { // Why?...
				if (formatted.startsWith("-"))
					return "-∞";
				return "∞";
			}
			if (s.length >= 21)
				return StringUtils.formatDouble(FormatType.NORMAL, value / 1.0E60) + "NOV";
			if (s.length >= 20)
				return StringUtils.formatDouble(FormatType.NORMAL, value / 1.0E57) + "OCT";
			if (s.length >= 19)
				return StringUtils.formatDouble(FormatType.NORMAL, value / 1.0E54) + "SEP";
			if (s.length >= 18)
				return StringUtils.formatDouble(FormatType.NORMAL, value / 1.0E51) + "SED";
			if (s.length >= 17)
				return StringUtils.formatDouble(FormatType.NORMAL, value / 1.0E48) + "QUI";
			if (s.length >= 16)
				return StringUtils.formatDouble(FormatType.NORMAL, value / 1.0E45) + "QUA";
			if (s.length >= 15)
				return StringUtils.formatDouble(FormatType.NORMAL, value / 1.0E42) + "tre";
			if (s.length >= 14)
				return StringUtils.formatDouble(FormatType.NORMAL, value / 1.0E39) + "duo";
			if (s.length >= 13)
				return StringUtils.formatDouble(FormatType.NORMAL, value / 1.0E36) + "und";
			if (s.length >= 12)
				return StringUtils.formatDouble(FormatType.NORMAL, value / 1.0E33) + "dec";
			if (s.length >= 11)
				return StringUtils.formatDouble(FormatType.NORMAL, value / 1.0E30) + "non";
			if (s.length >= 10)
				return StringUtils.formatDouble(FormatType.NORMAL, value / 1.0E27) + "oct";
			if (s.length >= 9)
				return StringUtils.formatDouble(FormatType.NORMAL, value / 1.0E24) + "sep";
			if (s.length >= 8) // No, it's not "sex"...
				return StringUtils.formatDouble(FormatType.NORMAL, value / 1.0E21) + "sex";
			if (s.length >= 7)
				return StringUtils.formatDouble(FormatType.NORMAL, value / 1.0E18) + "qui";
			if (s.length >= 6)
				return StringUtils.formatDouble(FormatType.NORMAL, value / 1.0E15) + "qua";
			if (s.length >= 5)
				return StringUtils.formatDouble(FormatType.NORMAL, value / 1.0E12) + "t";
			if (s.length >= 4)
				return StringUtils.formatDouble(FormatType.NORMAL, value / 1.0E9) + "b";
			if (s.length >= 3)
				return StringUtils.formatDouble(FormatType.NORMAL, value / 1000000) + "m";
			if (s.length >= 2)
				return StringUtils.formatDouble(FormatType.NORMAL, value / 1000) + "k";
			return formatted;
		}
		default:
			break;
		}
		return value + "";
	}

	/**
	 * @apiNote Generate random int within limits
	 * @param max Maximum int (defaulty {@link Integer#MAX_VALUE}
	 */
	public static int generateRandomInt(int max) {
		return StringUtils.generateRandomInt(0, max);
	}

	/**
	 * @apiNote Generate random double within limits
	 * @param max Maximum double (defaulty {@link Double#MAX_VALUE}
	 */
	public static double generateRandomDouble(double max) {
		return StringUtils.generateRandomDouble(0, max);
	}

	/**
	 * @apiNote Generate random double within limits
	 * @param min Minimum double (defaulty 0)
	 * @param max Maximum double (defaulty {@link Double#MAX_VALUE}
	 * @return double
	 */
	public static double generateRandomDouble(double min, double max) {
		if (min == max)
			return min;
		double result = StringUtils.generateRandomInt((int) min, (int) max) + StringUtils.random.nextDouble();
		if (result > max)
			return max;
		return result;
	}

	/**
	 * @apiNote Generate random int within limits
	 * @param min Minimum int (defaulty 0)
	 * @param max Maximum int (defaulty {@link Integer#MAX_VALUE}
	 * @return int
	 */
	public static int generateRandomInt(int min, int max) {
		if (min == max)
			return min;
		return StringUtils.random.nextInt(max - min) + min;
	}

	/**
	 * @apiNote Split text correctly with colors
	 */
	public static List<String> fixedSplit(String text, int lengthOfSplit) {
		if (text == null)
			return null;
		List<String> splitted = new ArrayList<>();
		String split = text;
		String prefix = "";
		while (split.length() > lengthOfSplit) {
			int length = lengthOfSplit - 1 - prefix.length();
			String a = prefix + split.substring(0, length);
			if (a.endsWith("§")) {
				--length;
				a = prefix + split.substring(0, length);
			}
			String[] last = StringUtils.getLastColorsSplitFormats(a);
			prefix = (!last[0].isEmpty() ? "§" + last[0] : "") + (!last[1].isEmpty() ? "§" + last[1] : "");
			splitted.add(a);
			split = split.substring(length);
		}
		if (!(prefix + split).isEmpty())
			splitted.add(prefix + split);
		return splitted;
	}

	/**
	 * @apiNote Copy matches of String from Iterable<String>
	 * @return List<String>
	 */
	public static List<String> copyPartialMatches(String prefix, Iterable<String> originals) {
		List<String> collection = new ArrayList<>();
		for (String string : originals)
			if (string == null
					|| string.length() >= prefix.length() && (string.regionMatches(true, 0, prefix, 0, prefix.length())
							|| string.regionMatches(true, 1, prefix, 0, prefix.length())))
				collection.add(string);
		return collection;
	}

	/**
	 * @apiNote Copy matches of String from Iterable<String>
	 * @return List<String>
	 */
	public static List<String> copySortedPartialMatches(String prefix, Iterable<String> originals) {
		List<String> collection = StringUtils.copyPartialMatches(prefix, originals);
		Collections.sort(collection);
		return collection;
	}

	/**
	 * @apiNote Join Iterable into one String with split {@value split} @see
	 *          {@link StringUtils#join(Iterable<?>, String, int, int)}
	 * @param split Split string (defaulty ' ')
	 * @param args  Arguments
	 * @return String
	 */
	public static String join(Iterable<?> args, String split) {
		return StringUtils.join(args, split, 0, -1);
	}

	/**
	 * @apiNote Join Iterable into one String with split {@value split} @see
	 *          {@link StringUtils#join(Iterable<?>, String, int, int)}
	 * @param split Split string (defaulty ' ')
	 * @param start Start argument (defaulty 0)
	 * @param args  Arguments
	 * @return String
	 */
	public static String join(Iterable<?> args, String split, int start) {
		return StringUtils.join(args, split, start, -1);
	}

	/**
	 * @apiNote Join Iterable into one String with split {@value split} @see
	 *          {@link StringUtils#join(Iterable<?>, String, int, int)}
	 * @param split Split string (defaulty ' ')
	 * @param start Start argument (defaulty 0)
	 * @param end   Last argument (defaultly -1)
	 * @param args  Arguments
	 * @return String
	 */
	public static String join(Iterable<?> args, String split, int start, int end) {
		if (args == null || split == null)
			return null;
		StringBuilder msg = new StringBuilder();
		Iterator<?> iterator = args.iterator();
		for (int i = start; iterator.hasNext() && (end == -1 || i < end); ++i) {
			if (msg.length() != 0)
				msg.append(split);
			msg.append(iterator.next());
		}
		return msg.toString();
	}

	/**
	 * @apiNote Join objects into one String with split {@value split} @see
	 *          {@link StringUtils#join(Object[], String, int, int)}
	 * @param split Split string (defaulty ' ')
	 * @param args  Arguments
	 * @return String
	 */
	public static String join(Object[] args, String split) {
		return StringUtils.join(args, split, 0, args.length);
	}

	/**
	 * @apiNote Join objects into one String with split {@value split} @see
	 *          {@link StringUtils#join(Object[], String, int, int)}
	 * @param split Split string (defaulty ' ')
	 * @param start Start argument (defaulty 0)
	 * @param args  Arguments
	 * @return String
	 */
	public static String join(Object[] args, String split, int start) {
		return StringUtils.join(args, split, start, args.length);
	}

	/**
	 * @apiNote Join objects into one String with split {@value split} @see
	 *          {@link StringUtils#join(Object[], String, int, int)}
	 * @param split Split string (defaulty ' ')
	 * @param start Start argument (defaulty 0)
	 * @param end   Last argument (defaultly args.length)
	 * @param args  Arguments
	 * @return String
	 */
	public static String join(Object[] args, String split, int start, int end) {
		if (args == null || split == null)
			return null;
		StringBuilder msg = new StringBuilder();
		for (int i = start; i < args.length && i < end; ++i) {
			if (msg.length() != 0)
				msg.append(split);
			msg.append(args[i]);
		}
		return msg.toString();
	}

	/**
	 * @apiNote Return joined strings ([0] + [1]) from
	 *          {@link StringUtils#getLastColorsSplitFormats(String)}
	 * @param text Input string
	 * @return String
	 */
	public static String getLastColors(String text) {
		String[] split = StringUtils.color.getLastColors(text);
		return split[0] + split[1];
	}

	/**
	 * @apiNote Get last colors from String (HEX SUPPORT!)
	 * @param text Input string
	 * @return String[]
	 */
	public static String[] getLastColorsSplitFormats(String text) {
		return StringUtils.color.getLastColors(text);
	}

	/**
	 * @apiNote Replace gradients in the List of strings
	 * @param list Input list of strings to colorize
	 * @return List<String>
	 */
	public static List<String> gradient(List<String> list) {
		list.replaceAll(StringUtils::gradient);
		return list;
	}

	/**
	 * @apiNote Replace gradients in the String
	 * @param originalMsg Input string to colorize
	 * @return String
	 */
	public static String gradient(String originalMsg) {
		if (originalMsg == null || StringUtils.gradientFinder == null)
			return originalMsg;

		String legacyMsg = originalMsg;

		String low = legacyMsg.toLowerCase();
		for (Entry<String, String> code : StringUtils.colorMap.entrySet()) {
			String rawCode = (StringUtils.tagPrefix + code.getKey()).toLowerCase();
			if (!low.contains(rawCode))
				continue;
			legacyMsg = legacyMsg.replace(rawCode, code.getValue());
		}
		Matcher matcher = StringUtils.gradientFinder.matcher(legacyMsg);
		while (matcher.find()) {
			if (matcher.groupCount() == 0 || matcher.group().isEmpty())
				continue;
			String replace = StringUtils.color.gradient(matcher.group(2), matcher.group(1), matcher.group(3));
			if (replace == null)
				continue;
			legacyMsg = legacyMsg.replace(matcher.group(), replace);
		}
		return legacyMsg;
	}

	/**
	 * @apiNote Colorize List of strings with colors
	 * @param list Texts to colorize
	 * @return List<String>
	 */
	public static List<String> colorize(List<String> list) {
		list.replaceAll(StringUtils::colorize);
		return list;
	}

	/**
	 * @apiNote Colorize string with colors
	 * @param original Text to colorize
	 * @return String
	 */
	public static String colorize(String original) {
		if (original == null || original.trim().isEmpty())
			return original;

		String msg = original;
		char[] b = msg.toCharArray();
		for (int i = 0; i < b.length - 1; i++)
			if (b[i] == '&' && StringUtils.has(b[i + 1])) {
				b[i] = '§';
				b[i + 1] = StringUtils.lower(b[i + 1]);
			}
		msg = new String(b);
		if (StringUtils.color != null && /** Fast check for working #RRGGBB symbol **/
				(!Ref.serverType().isBukkit() || Ref.isNewerThan(15))) {
			msg = StringUtils.gradient(msg);
			if (msg.contains("#"))
				msg = StringUtils.color.replaceHex(msg);
		}
		if (msg.contains("&u") && StringUtils.color != null)
			msg = StringUtils.color.rainbow(msg, StringUtils.color.generateColor(), StringUtils.color.generateColor());
		return msg;
	}

	private static boolean has(int c) {
		return c <= 102 && c >= 97 || c <= 57 && c >= 48 || c <= 70 && c >= 65 || c <= 79 && c >= 75
				|| c <= 111 && c >= 107 || c == 114 || c == 82 || c == 88 || c == 120;
	}

	private static char lower(int c) {
		switch (c) {
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
			return (char) (c + 32);
		case 120:
			return (char) 88;
		default:
			return (char) c;
		}
	}

	/**
	 * @apiNote Join strings to one String with split ' ' @see
	 *          {@link StringUtils#join(Object[], String, int, int)}
	 * @param args Arguments
	 * @return String
	 *
	 */
	public static String buildString(String[] args) {
		return StringUtils.join(args, " ", 0, args.length);
	}

	/**
	 * @apiNote Join strings to one String with split ' ' @see
	 *          {@link StringUtils#join(Object[], String, int, int)}
	 * @param start Start argument (defaulty 0)
	 * @param args  Arguments
	 * @return String
	 *
	 */
	public static String buildString(int start, String[] args) {
		return StringUtils.join(args, " ", start, args.length);
	}

	/**
	 * @apiNote Join strings to one String with split ' ' @see
	 *          {@link StringUtils#join(Object[], String, int, int)}
	 * @param start Start argument (defaulty 0)
	 * @param end   Last argument (defaultly args.length)
	 * @param args  Arguments
	 * @return String
	 *
	 */
	public static String buildString(int start, int end, String[] args) {
		return StringUtils.join(args, " ", start, end);
	}

	/**
	 * @apiNote Return random object from list
	 */
	public static <T> T getRandomFromList(List<T> list) {
		if (list == null || list.isEmpty())
			return null;
		return list.get(StringUtils.random.nextInt(list.size()));
	}

	/**
	 * @apiNote Return random object from collection
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getRandomFromCollection(Collection<T> list) {
		if (list == null || list.isEmpty())
			return null;
		if (list instanceof List)
			return StringUtils.getRandomFromList((List<T>) list);
		return (T) list.toArray()[StringUtils.random.nextInt(list.size())];
	}

	/**
	 * @apiNote Convert long time to String
	 * @param period long Time to convert
	 * @return String
	 */
	public static String timeToString(long period) {
		return StringUtils.timeToString(period, StringUtils.timeSplit);
	}

	/**
	 * @apiNote Convert long time to String
	 * @param period   long Time to convert
	 * @param split    String Split between time
	 * @param disabled TimeFormat... disabled time formats
	 * @return String
	 */
	public static String timeToString(long period, String split, TimeFormat... disabled) {
		boolean digit = split.equals(":");

		if (period == 0)
			return digit ? "0" : StringUtils.timeConvertor.get(TimeFormat.SECONDS).toString(0);
		List<TimeFormat> disabledList = Arrays.asList(disabled);
		if (disabledList.contains(TimeFormat.YEARS) && disabledList.contains(TimeFormat.MONTHS)
				&& disabledList.contains(TimeFormat.DAYS) && disabledList.contains(TimeFormat.HOURS)
				&& disabledList.contains(TimeFormat.MINUTES))
			return digit ? period + "" : StringUtils.timeConvertor.get(TimeFormat.SECONDS).toString(period); // YOU
																												// DISABLED
																												// EVERYTHING??

		double seconds = period % 60;
		double minutes = period * 0.0166666667;
		boolean modifMin = false;
		double hours = period * 0.0002777778;
		boolean modifHou = false;
		double days = period * 1.15741E-5;
		boolean modifDay = false;
		double months = period * 3.8051750380518E-7;
		boolean modifMon = false;
		double years = period * 3.1688087814029E-8;

		if (disabledList.contains(TimeFormat.YEARS)) {
			modifMon = true;
			months = months % 12;
			months += (long) years * 12;
			years = 0;
		}
		if (disabledList.contains(TimeFormat.MONTHS)) {
			modifDay = true;
			days = days % TimeFormat.DAYS.cast();
			days += (long) months * TimeFormat.DAYS.cast();
			months = 0;
		} else if (!modifMon)
			months = months % 12;
		if (disabledList.contains(TimeFormat.DAYS)) {
			modifHou = true;
			hours = hours % 24;
			hours += (long) days * 24;
			days = 0;
		} else if (!modifDay)
			days = days % 24;
		if (disabledList.contains(TimeFormat.HOURS)) {
			modifMin = true;
			minutes = minutes % 60;
			minutes += (long) hours * 60;
			hours = 0;
		} else if (!modifHou)
			hours = hours % 24;
		if (disabledList.contains(TimeFormat.MINUTES)) {
			seconds += (long) minutes * 60;
			minutes = 0;
		} else if (!modifMin)
			minutes = minutes % 60;
		if (disabledList.contains(TimeFormat.SECONDS))
			seconds = 0;

		StringBuilder builder = new StringBuilder();
		StringUtils.addFormat(builder, split, TimeFormat.YEARS, digit, (long) years);
		StringUtils.addFormat(builder, split, TimeFormat.MONTHS, digit, (long) months);
		StringUtils.addFormat(builder, split, TimeFormat.DAYS, digit, (long) days);
		StringUtils.addFormat(builder, split, TimeFormat.HOURS, digit, (long) hours);
		StringUtils.addFormat(builder, split, TimeFormat.MINUTES, digit, (long) minutes);
		StringUtils.addFormat(builder, split, TimeFormat.SECONDS, digit, (long) seconds);
		return builder.toString();
	}

	private static void addFormat(StringBuilder builder, String split, TimeFormat format, boolean digit, long time) {
		if (time > 0) {
			boolean notFirst = builder.length() != 0;
			if (notFirst)
				builder.append(split);
			if (digit) {
				if (time < 10 && notFirst)
					builder.append('0');
				builder.append(time);
			} else
				builder.append(StringUtils.timeConvertor.get(format).toString(time));
		} else if (digit) {
			boolean notFirst = builder.length() != 0;
			if (notFirst)
				builder.append(split).append("00");
		}
	}

	/**
	 * @apiNote Get long from string
	 * @param period String
	 * @return long
	 */
	public static long timeFromString(String original) {
		if (original == null || original.isEmpty())
			return 0;

		String period = original;

		if (StringUtils.isFloat(period) && !period.endsWith("d") && !period.endsWith("e"))
			return (long) StringUtils.getFloat(period);

		float time = 0;

		if (period.contains(":")) {
			String[] split = period.split(":");
			switch (split.length) {
			case 2: // mm:ss
				time += StringUtils.getFloat(split[0]) * TimeFormat.MINUTES.multiplier();
				time += StringUtils.getFloat(split[1]);
				break;
			case 3: // hh:mm:ss
				time += StringUtils.getFloat(split[0]) * TimeFormat.HOURS.multiplier();
				time += StringUtils.getFloat(split[1]) * TimeFormat.MINUTES.multiplier();
				time += StringUtils.getFloat(split[2]);
				break;
			case 4: // dd:hh:mm:ss
				time += StringUtils.getFloat(split[0]) * TimeFormat.DAYS.multiplier();
				time += StringUtils.getFloat(split[1]) * TimeFormat.HOURS.multiplier();
				time += StringUtils.getFloat(split[2]) * TimeFormat.MINUTES.multiplier();
				time += StringUtils.getFloat(split[3]);
				break;
			case 5: // mm:dd:hh:mm:ss
				time += StringUtils.getFloat(split[0]) * TimeFormat.MONTHS.multiplier();
				time += StringUtils.getFloat(split[1]) * TimeFormat.DAYS.multiplier();
				time += StringUtils.getFloat(split[2]) * TimeFormat.HOURS.multiplier();
				time += StringUtils.getFloat(split[3]) * TimeFormat.MINUTES.multiplier();
				time += StringUtils.getFloat(split[4]);
				break;
			default: // yy:mm:dd:hh:mm:ss
				time += StringUtils.getFloat(split[0]) * TimeFormat.YEARS.multiplier();
				time += StringUtils.getFloat(split[1]) * TimeFormat.MONTHS.multiplier();
				time += StringUtils.getFloat(split[2]) * TimeFormat.DAYS.multiplier();
				time += StringUtils.getFloat(split[3]) * TimeFormat.HOURS.multiplier();
				time += StringUtils.getFloat(split[4]) * TimeFormat.MINUTES.multiplier();
				time += StringUtils.getFloat(split[5]);
				break;
			}
			return (long) time;
		}

		for (int i = TimeFormat.values().length - 1; i > 0; --i) {
			Matcher matcher = StringUtils.timeConvertor.get(TimeFormat.values()[i]).matcher(period);
			while (matcher.find()) {
				time += StringUtils.getFloat(matcher.group()) * TimeFormat.values()[i].multiplier();
				period = matcher.replaceFirst("");
			}
		}
		return (long) time;
	}

	/**
	 * @apiNote Get boolean from string
	 * @return boolean
	 */
	public static boolean getBoolean(String fromString) {
		try {
			return fromString.equalsIgnoreCase("true");
		} catch (Exception er) {
			return false;
		}
	}

	/**
	 * @apiNote Convert String to Math and Calculate exempt
	 * @return double
	 */
	public static double calculate(String original) {

		String val = original;

		if (val.contains("(") && val.contains(")"))
			val = StringUtils.splitter(val);
		if (val.contains("*") || val.contains("/")) {
			Matcher s = StringUtils.extra.matcher(val);
			while (s.find()) {
				double a = StringUtils.getDouble(s.group(1));
				String b = s.group(3);
				double d = StringUtils.getDouble(s.group(4));
				val = val.replace(s.group(), (a == 0 || d == 0 ? 0 : b.equals("*") ? a * d : a / d) + "");
				s.reset(val);
			}
		}
		if (val.contains("+") || val.contains("-")) {
			Matcher s = StringUtils.normal.matcher(val);
			while (s.find()) {
				double a = StringUtils.getDouble(s.group(1));
				String b = s.group(3);
				double d = StringUtils.getDouble(s.group(4));
				val = val.replace(s.group(), (b.equals("+") ? a + d : a - d) + "");
				s.reset(val);
			}
		}
		return StringUtils.getDouble(val.replaceAll("[^0-9+.-]", ""));
	}

	private static String splitter(String s) {
		StringBuilder i = new StringBuilder();
		StringBuilder fix = new StringBuilder();

		int count = 0;
		int waiting = 0;
		for (char c : s.toCharArray()) {
			i.append(c);
			if (c == '(') {
				fix.append(c);
				waiting = 1;
				++count;
			} else if (c == ')') {
				fix.append(c);
				if (--count == 0) {
					waiting = 0;
					i = new StringBuilder(i.toString().replace(fix.toString(),
							"" + StringUtils.calculate(fix.substring(1, fix.length() - 1))));
					fix.delete(0, fix.length());
				}
			} else if (waiting == 1)
				fix.append(c);
		}
		return i.toString();
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
		if (!a.contains(".")) {
			try {
				return Integer.parseInt(a);
			} catch (NumberFormatException e) {
			}
			try {
				return (int) Long.parseLong(a);
			} catch (NumberFormatException e) {
			}
		}
		try {
			return (int) Double.parseDouble(a);
		} catch (NumberFormatException e) {
		}
		return 0;
	}

	/**
	 * @apiNote Is string, integer ?
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
		return StringUtils.isInt(fromString) || StringUtils.isDouble(fromString) || StringUtils.isLong(fromString)
				|| StringUtils.isByte(fromString) || StringUtils.isShort(fromString) || StringUtils.isFloat(fromString);
	}

	/**
	 * @apiNote Is string, boolean ?
	 * @return boolean
	 */
	public static boolean isBoolean(String fromString) {
		if (fromString == null)
			return false;
		return fromString.equalsIgnoreCase("true") || fromString.equalsIgnoreCase("false");
	}

	public static boolean containsSpecial(String value) {
		return StringUtils.special.matcher(value).find();
	}

	public static Number getNumber(String o) {
		if (o == null)
			return null;
		if (!o.contains(".")) {
			if (StringUtils.isInt(o))
				return StringUtils.getInt(o);
			if (StringUtils.isLong(o))
				return StringUtils.getLong(o);
			if (StringUtils.isByte(o))
				return StringUtils.getByte(o);
			if (StringUtils.isShort(o))
				return StringUtils.getShort(o);
		}
		if (StringUtils.isDouble(o))
			return StringUtils.getDouble(o);
		if (StringUtils.isFloat(o))
			return StringUtils.getFloat(o);
		return null;
	}
}
