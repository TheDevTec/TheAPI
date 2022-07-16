package me.devtec.shared;

import java.awt.Color;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import me.devtec.shared.Ref.ServerType;
import me.devtec.shared.commands.manager.CommandsRegister;
import me.devtec.shared.commands.manager.SelectorUtils;
import me.devtec.shared.dataholder.Config;
import me.devtec.shared.utility.LibraryLoader;
import me.devtec.shared.utility.OfflineCache;
import me.devtec.shared.utility.StringUtils;
import me.devtec.shared.utility.StringUtils.TimeFormat;
import me.devtec.shared.utility.StringUtils.TimeFormatter;

public class API {
	// Commands api
	public static CommandsRegister commandsRegister;
	public static SelectorUtils selectorUtils;

	// Library
	public static LibraryLoader library;

	// Offline users cache
	private static OfflineCache cache;
	private static Map<UUID, Config> users = new ConcurrentHashMap<>();

	// Other cool things
	private static final Basics basics = new Basics();
	private static boolean enabled = true;

	public static void initOfflineCache(boolean onlineMode, Config rawData) {
		API.cache = new OfflineCache(onlineMode);
		for (String uuid : rawData.getKeys())
			try {
				API.cache.setLookup(UUID.fromString(uuid), rawData.getString(uuid));
			} catch (Exception err) {
			}
	}

	public static OfflineCache offlineCache() {
		return API.cache;
	}

	public static Config getUser(String playerName) {
		if (API.cache == null)
			return null;
		UUID id = API.cache.lookupId(playerName);
		Config cached = API.users.get(id);
		if (cached == null)
			API.users.put(id, cached = new Config("plugins/TheAPI/Users/" + id + ".yml"));
		return cached;
	}

	public static Config getUser(UUID id) {
		if (API.cache == null)
			return null;
		Config cached = API.users.get(id);
		if (cached == null)
			API.users.put(id, cached = new Config("plugins/TheAPI/Users/" + id + ".yml"));
		return cached;
	}

	public static Config removeCache(UUID id) {
		return API.users.remove(id);
	}

	public static void setEnabled(boolean status) {
		API.enabled = status;
	}

	public static boolean isEnabled() {
		return API.enabled;
	}

	public static class Basics {

		public void load() {
			String path = Ref.serverType().isBukkit() || Ref.serverType() == ServerType.BUNGEECORD
					|| Ref.serverType() == ServerType.VELOCITY ? "plugins/TheAPI/" : "TheAPI/";

			Config tags = new Config(path + "tags.yml");
			tags.setIfAbsent("hexTagPrefix", "!", Arrays.asList("# <hexTagPrefix><tagName>", "# For ex.: !fire"));
			tags.setIfAbsent("gradient.firstHex.prefix", "!");
			tags.setIfAbsent("gradient.firstHex.suffix", "");
			tags.setIfAbsent("gradient.secondHex.prefix", "!");
			tags.setIfAbsent("gradient.secondHex.suffix", "");
			if (!tags.exists("tags")) {
				tags.setIfAbsent("tags.baby_blue", "0fd2f6");
				tags.setIfAbsent("tags.beige", "ffc8a9");
				tags.setIfAbsent("tags.blush", "e69296");
				tags.setIfAbsent("tags.amaranth", "e52b50");
				tags.setIfAbsent("tags.brown", "964b00");
				tags.setIfAbsent("tags.crimson", "dc143c");
				tags.setIfAbsent("tags.dandelion", "ffc31c");
				tags.setIfAbsent("tags.eggshell", "f0ecc7");
				tags.setIfAbsent("tags.fire", "ff0000");
				tags.setIfAbsent("tags.ice", "bddeec");
				tags.setIfAbsent("tags.indigo", "726eff");
				tags.setIfAbsent("tags.lavender", "4b0082");
				tags.setIfAbsent("tags.leaf", "618a3d");
				tags.setIfAbsent("tags.lilac", "c8a2c8");
				tags.setIfAbsent("tags.lime", "b7ff00");
				tags.setIfAbsent("tags.midnight", "007bff");
				tags.setIfAbsent("tags.mint", "50c878");
				tags.setIfAbsent("tags.olive", "929d40");
				tags.setIfAbsent("tags.royal_purple", "7851a9");
				tags.setIfAbsent("tags.rust", "b45019");
				tags.setIfAbsent("tags.sky", "00c8ff");
				tags.setIfAbsent("tags.smoke", "708c98");
				tags.setIfAbsent("tags.tangerine", "ef8e38");
				tags.setIfAbsent("tags.violet", "9c6eff");
			}
			tags.save();
			StringUtils.tagPrefix = tags.getString("hexTagPrefix");
			String gradientTagPrefix = tags.getString("gradient.firstHex.prefix");
			String gradientTagPrefixL = tags.getString("gradient.secondHex.prefix");
			String gradientTagSuffix = tags.getString("gradient.firstHex.suffix");
			String gradientTagSuffixL = tags.getString("gradient.secondHex.suffix");

			for (String tag : tags.getKeys("tags"))
				StringUtils.colorMap.put(tag.toLowerCase(), "#" + tags.getString("tags." + tag));

			StringUtils.gradientFinder = Pattern.compile(gradientTagPrefix + "(#[A-Fa-f0-9]{6})" + gradientTagSuffix
					+ "(.*?)" + gradientTagPrefixL + "(#[A-Fa-f0-9]{6})" + gradientTagSuffixL + "|.*?(?=(?:"
					+ gradientTagPrefix + "#[A-Fa-f0-9]{6}" + gradientTagSuffix + ".*?" + gradientTagPrefixL
					+ "#[A-Fa-f0-9]{6}" + gradientTagSuffixL + "))");
			Config config = new Config(path + "config.yml");
			config.setIfAbsent("timeConvertor.settings.defaultlyDigits", false,
					Arrays.asList("# If plugin isn't using own split, use defaulty digitals? 300 -> 5:00"));
			config.setIfAbsent("timeConvertor.settings.defaultSplit", " ",
					Arrays.asList("# If plugin isn't using own split, api'll use this split"));
			config.setIfAbsent("timeConvertor.years.matcher", "y|years?", Arrays.asList("# Pattern matcher (regex)"));
			config.setIfAbsent("timeConvertor.years.convertor", Arrays.asList("<=1 year", ">1 years"),
					Arrays.asList("# >=X value is higher or equals to X", "# <=X value is lower or equals to X",
							"# >X value is higher than X", "# <X value is lower than X", "# ==X value equals to X",
							"# !=X value doesn't equals to X"));
			config.setIfAbsent("timeConvertor.months.matcher", "mo|mon|months?");
			config.setIfAbsent("timeConvertor.months.convertor", Arrays.asList("<=1 month", ">1 months"));
			config.setIfAbsent("timeConvertor.weeks.matcher", "w|weeks?");
			config.setIfAbsent("timeConvertor.weeks.convertor", Arrays.asList("<=1 week", ">1 weeks"), Arrays
					.asList("# Api isn't using this convertor anywhere, but other plugins can use this convertor."));
			config.setIfAbsent("timeConvertor.days.matcher", "d|days?");
			config.setIfAbsent("timeConvertor.days.convertor", Arrays.asList("<=1 day", ">1 days"));
			config.setIfAbsent("timeConvertor.hours.matcher", "h|hours?");
			config.setIfAbsent("timeConvertor.hours.convertor", Arrays.asList("<=1 hour", ">1 hours"));
			config.setIfAbsent("timeConvertor.minutes.matcher", "m|mi|min|minut|minutes?");
			config.setIfAbsent("timeConvertor.minutes.convertor", Arrays.asList("<=1 minute", ">1 minutes"));
			config.setIfAbsent("timeConvertor.seconds.matcher", "s|sec|seconds?");
			config.setIfAbsent("timeConvertor.seconds.convertor", Arrays.asList("<=1 second", ">1 seconds"));
			config.save();

			StringUtils.timeSplit = config.getString("timeConvertor.settings.defaultSplit");

			StringUtils.timeConvertor.put(TimeFormat.SECONDS, new TimeFormatter() {
				Pattern pattern = Pattern
						.compile("[+-]?[ ]*[0-9]+[ ]*(" + config.getString("timeConvertor.seconds.matcher") + ")");

				@Override
				public Matcher matcher(String text) {
					return pattern.matcher(text);
				}

				@Override
				public String toString(long value) {
					for (String action : config.getStringList("timeConvertor.seconds.convertor"))
						if (matchAction(action, value))
							return value + StringUtils.buildString(1, action.split(" "));
					return value + "s";
				}
			});
			StringUtils.timeConvertor.put(TimeFormat.MINUTES, new TimeFormatter() {
				Pattern pattern = Pattern
						.compile("[+-]?[ ]*[0-9]+[ ]*(" + config.getString("timeConvertor.minutes.matcher") + ")");

				@Override
				public Matcher matcher(String text) {
					return pattern.matcher(text);
				}

				@Override
				public String toString(long value) {
					for (String action : config.getStringList("timeConvertor.minutes.convertor"))
						if (matchAction(action, value))
							return value + StringUtils.buildString(1, action.split(" "));
					return value + "m";
				}
			});
			StringUtils.timeConvertor.put(TimeFormat.HOURS, new TimeFormatter() {
				Pattern pattern = Pattern
						.compile("[+-]?[ ]*[0-9]+[ ]*(" + config.getString("timeConvertor.hours.matcher") + ")");

				@Override
				public Matcher matcher(String text) {
					return pattern.matcher(text);
				}

				@Override
				public String toString(long value) {
					for (String action : config.getStringList("timeConvertor.hours.convertor"))
						if (matchAction(action, value))
							return value + StringUtils.buildString(1, action.split(" "));
					return value + "h";
				}
			});
			StringUtils.timeConvertor.put(TimeFormat.DAYS, new TimeFormatter() {
				Pattern pattern = Pattern
						.compile("[+-]?[ ]*[0-9]+[ ]*(" + config.getString("timeConvertor.days.matcher") + ")");

				@Override
				public Matcher matcher(String text) {
					return pattern.matcher(text);
				}

				@Override
				public String toString(long value) {
					for (String action : config.getStringList("timeConvertor.days.convertor"))
						if (matchAction(action, value))
							return value + StringUtils.buildString(1, action.split(" "));
					return value + "d";
				}
			});
			StringUtils.timeConvertor.put(TimeFormat.WEEKS, new TimeFormatter() {
				Pattern pattern = Pattern
						.compile("[+-]?[ ]*[0-9]+[ ]*(" + config.getString("timeConvertor.weeks.matcher") + ")");

				@Override
				public Matcher matcher(String text) {
					return pattern.matcher(text);
				}

				@Override
				public String toString(long value) {
					for (String action : config.getStringList("timeConvertor.weeks.convertor"))
						if (matchAction(action, value))
							return value + StringUtils.buildString(1, action.split(" "));
					return value + "w";
				}
			});
			StringUtils.timeConvertor.put(TimeFormat.MONTHS, new TimeFormatter() {
				Pattern pattern = Pattern
						.compile("[+-]?[ ]*[0-9]+[ ]*(" + config.getString("timeConvertor.months.matcher") + ")");

				@Override
				public Matcher matcher(String text) {
					return pattern.matcher(text);
				}

				@Override
				public String toString(long value) {
					for (String action : config.getStringList("timeConvertor.months.convertor"))
						if (matchAction(action, value))
							return value + StringUtils.buildString(1, action.split(" "));
					return value + "mo";
				}
			});
			StringUtils.timeConvertor.put(TimeFormat.YEARS, new TimeFormatter() {
				Pattern pattern = Pattern
						.compile("[+-]?[ ]*[0-9]+[ ]*(" + config.getString("timeConvertor.years.matcher") + ")");

				@Override
				public Matcher matcher(String text) {
					return pattern.matcher(text);
				}

				@Override
				public String toString(long value) {
					for (String action : config.getStringList("timeConvertor.years.convertor"))
						if (matchAction(action, value))
							return value + StringUtils.buildString(1, action.split(" "));
					return value + "y";
				}
			});
		}

		private boolean matchAction(String action, long value) {
			String[] split = action.split(" ");
			if (action.startsWith("=="))
				return value == StringUtils.getLong(split[0]);
			if (action.startsWith("!="))
				return value != StringUtils.getLong(split[0]);
			if (action.startsWith(">="))
				return value >= StringUtils.getLong(split[0]);
			if (action.startsWith("<="))
				return value <= StringUtils.getLong(split[0]);
			if (action.startsWith(">"))
				return value > StringUtils.getLong(split[0]);
			if (action.startsWith("<"))
				return value < StringUtils.getLong(split[0]);
			return false; // invalid
		}

		public String[] getLastColors(Pattern pattern, String text) {
			Matcher m = pattern.matcher(text);
			String color = null;
			StringBuilder formats = new StringBuilder();
			while (m.find()) {
				String last = m.group(1).toLowerCase();
				if (last.charAt(1) != 'x' && isFormat(last.charAt(1))) {
					if (last.charAt(1) == 'r') {
						formats.delete(0, formats.length());
						continue;
					}
					formats.append(last.charAt(1));
					continue;
				}
				color = last.replace("§", "").replace("&", "");
				formats.delete(0, formats.length());
			}
			return new String[] { color, formats.toString() };
		}

		public String rainbow(String msg, String fromHex, String toHex) {
			if (msg == null || fromHex == null || toHex == null)
				return msg;
			return rawGradient(msg, fromHex, toHex, false);
		}

		public String gradient(String msg, String fromHex, String toHex) {
			if (msg == null || fromHex == null || toHex == null)
				return msg;
			return rawGradient(msg, fromHex, toHex, true);
		}

		private boolean isColor(int charAt) {
			return charAt >= 97 && charAt <= 102 || charAt >= 65 && charAt <= 70 || charAt >= 48 && charAt <= 57;
		}

		private boolean isFormat(int charAt) {
			return charAt >= 107 && charAt <= 111 || charAt == 114;
		}

		private String rawGradient(String msg, String from, String to, boolean defaultRainbow) {
			String split = msg.replace("", "<>");
			String formats = "";

			StringBuilder builder = new StringBuilder();
			boolean inRainbow = defaultRainbow;
			char prev = 0;

			Color fromRGB = Color.decode(from);
			Color toRGB = Color.decode(to);
			double rStep = Math.abs((double) (fromRGB.getRed() - toRGB.getRed()) / msg.length());
			double gStep = Math.abs((double) (fromRGB.getGreen() - toRGB.getGreen()) / msg.length());
			double bStep = Math.abs((double) (fromRGB.getBlue() - toRGB.getBlue()) / msg.length());
			if (fromRGB.getRed() > toRGB.getRed())
				rStep = -rStep;
			if (fromRGB.getGreen() > toRGB.getGreen())
				gStep = -gStep;
			if (fromRGB.getBlue() > toRGB.getBlue())
				bStep = -bStep;

			Color finalColor = new Color(fromRGB.getRGB());
			for (String s : split.split("<>")) {
				if (s.isEmpty())
					continue;
				char c = s.charAt(0);
				if (prev == '&' || prev == '§') {
					char inLower = Character.toLowerCase(s.charAt(0));
					if (prev == '&' && inLower == 'u') {
						builder.deleteCharAt(builder.length() - 1); // remove & char
						inRainbow = true;
						prev = c;
						continue;
					}
					if (inRainbow && prev == '§' && (isColor(inLower) || isFormat(inLower))) { // color,
						// destroy
						// rainbow here
						if (isFormat(inLower)) {
							if (inLower == 'r')
								formats = "§r";
							else
								formats += "§" + inLower;
							prev = inLower;
							builder.deleteCharAt(builder.length() - 1); // remove &<random color> string
							continue;
						}
						builder.delete(builder.length() - 14, builder.length()); // remove &<random color> string
						inRainbow = false;
					}
				}
				if (c != ' ' && inRainbow) {
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
					if (formats.equals("§r")) {
						builder.append(formats); // add formats
						builder.append(StringUtils.color
								.replaceHex("#" + String.format("%08x", finalColor.getRGB()).substring(2))); // add
						// color
						formats = "";
					} else {
						builder.append(StringUtils.color
								.replaceHex("#" + String.format("%08x", finalColor.getRGB()).substring(2))); // add
						// color
						if (!formats.isEmpty())
							builder.append(formats); // add formats
					}
				}
				builder.append(c);
				prev = c;
			}
			return builder.toString();
		}

	}

	public static Basics basics() {
		return API.basics;
	}

	public static double getProcessCpuLoad() {
		try {
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
			AttributeList list = mbs.getAttributes(name, new String[] { "ProcessCpuLoad" });
			if (list.isEmpty())
				return 0.0;
			Attribute att = (Attribute) list.get(0);
			Double value = (Double) att.getValue();
			if (value == -1.0)
				return 0;
			return value * 1000.0 / 10.0;
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * @see see Server up time in long
	 * @return long
	 */
	public static long getServerUpTime() {
		return ManagementFactory.getRuntimeMXBean().getUptime();
	}
}
