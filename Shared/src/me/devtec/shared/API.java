package me.devtec.shared;

import java.awt.Color;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.google.common.collect.Lists;

import me.devtec.shared.dataholder.Config;
import me.devtec.shared.utility.LibraryLoader;
import me.devtec.shared.utility.StringUtils;

public class API {
	public static LibraryLoader library = null;
	private static final Basics basics = new Basics();
	public static boolean enabled = true;
	
	public static void setEnabled(boolean status) {
		enabled=status;
	}
	
	public static class Basics {
		Pattern reg = Pattern.compile("[&§]([Rrk-oK-O])");
		Pattern colorMatic = Pattern.compile("(<!>)*([&§])<!>([A-Fa-f0-9RrK-Ok-oUuXx])");
		
		public void load() {
			Config tags = new Config("plugins/TheAPI/Tags.yml");
			tags.setIfAbsent("TagPrefix", "!");
			tags.setIfAbsent("Gradient.Prefix.First", "!");
			tags.setIfAbsent("Gradient.Prefix.Second", "!");
			tags.setIfAbsent("Gradient.Suffix.First", "");
			tags.setIfAbsent("Gradient.Suffix.Second", "");
			if (!tags.exists("Tags")) {
				tags.setIfAbsent("Tags.baby_blue", "0fd2f6");
				tags.setIfAbsent("Tags.beige", "ffc8a9");
				tags.setIfAbsent("Tags.blush", "e69296");
				tags.setIfAbsent("Tags.amaranth", "e52b50");
				tags.setIfAbsent("Tags.brown", "964b00");
				tags.setIfAbsent("Tags.crimson", "dc143c");
				tags.setIfAbsent("Tags.dandelion", "ffc31c");
				tags.setIfAbsent("Tags.eggshell", "f0ecc7");
				tags.setIfAbsent("Tags.fire", "ff0000");
				tags.setIfAbsent("Tags.ice", "bddeec");
				tags.setIfAbsent("Tags.indigo", "726eff");
				tags.setIfAbsent("Tags.lavender", "4b0082");
				tags.setIfAbsent("Tags.leaf", "618a3d");
				tags.setIfAbsent("Tags.lilac", "c8a2c8");
				tags.setIfAbsent("Tags.lime", "b7ff00");
				tags.setIfAbsent("Tags.midnight", "007bff");
				tags.setIfAbsent("Tags.mint", "50c878");
				tags.setIfAbsent("Tags.olive", "929d40");
				tags.setIfAbsent("Tags.royal_purple", "7851a9");
				tags.setIfAbsent("Tags.rust", "b45019");
				tags.setIfAbsent("Tags.sky", "00c8ff");
				tags.setIfAbsent("Tags.smoke", "708c98");
				tags.setIfAbsent("Tags.tangerine", "ef8e38");
				tags.setIfAbsent("Tags.violet", "9c6eff");
			}
			tags.save();
			StringUtils.tagPrefix = tags.getString("TagPrefix");
			String gradientTagPrefix = tags.getString("Gradient.Prefix.First");
			String gradientTagPrefixL = tags.getString("Gradient.Prefix.Second");
			String gradientTagSuffix = tags.getString("Gradient.Suffix.First");
			String gradientTagSuffixL = tags.getString("Gradient.Suffix.Second");
			for (String tag : tags.getKeys("Tags"))
				StringUtils.colorMap.put(tag.toLowerCase(), "#"+tags.getString("Tags." + tag));
			StringUtils.gradientFinder=Pattern.compile((gradientTagPrefix+"(#[A-Fa-f0-9]{6})"+gradientTagSuffix+"(.*?)"+gradientTagPrefixL+"(#[A-Fa-f0-9]{6})"+gradientTagSuffixL)+"|.*?(?=(?:"+gradientTagPrefix+"#[A-Fa-f0-9]{6}"+gradientTagSuffix+".*?"+gradientTagPrefixL+"#[A-Fa-f0-9]{6}"+gradientTagSuffixL+"))");
			Config config = new Config("plugins/TheAPI/Config.yml");
			config.setComments("Options.TimeConvertor", Arrays.asList("","# Convertor Actions:","# action, amount, translation","# = (equals)","# < (lower than)","# > (more than)"));
			config.setIfAbsent("Options.TimeConvertor.Split", " ");
			config.setIfAbsent("Options.TimeConvertor.Format", "%time% %format%");
			config.setIfAbsent("Options.TimeConvertor.Seconds.Convertor", Arrays.asList("=,0,sec","=,1,sec",">,1,secs"));
			if(!(config.get("Options.TimeConvertor.Seconds.Convertor") instanceof Collection))
				config.set("Options.TimeConvertor.Seconds.Convertor", Arrays.asList("=,1,sec",">,1,secs"));
			config.setIfAbsent("Options.TimeConvertor.Seconds.Lookup", Arrays.asList("s","sec","second","seconds"));
			
			config.setIfAbsent("Options.TimeConvertor.Minutes.Convertor", Arrays.asList("=,1,min",">,1,mins"));
			if(!(config.get("Options.TimeConvertor.Minutes.Convertor") instanceof Collection))
				config.set("Options.TimeConvertor.Minutes.Convertor", Arrays.asList("=,1,min",">,1,mins"));
			config.setIfAbsent("Options.TimeConvertor.Minutes.Lookup", Arrays.asList("m","mi","min","minu","minut","minute","minutes"));
			
			if(!(config.get("Options.TimeConvertor.Hours.Convertor") instanceof Collection))
				config.set("Options.TimeConvertor.Hours.Convertor", Arrays.asList("=,1,hour",">,1,hours"));
			config.setIfAbsent("Options.TimeConvertor.Hours.Convertor", Arrays.asList("=,1,hour",">,1,hours"));
			config.setIfAbsent("Options.TimeConvertor.Hours.Lookup", Arrays.asList("h","ho","hou","hour","hours"));
			
			config.setIfAbsent("Options.TimeConvertor.Days.Convertor", Arrays.asList("=,1,day",">,1,days"));
			if(!(config.get("Options.TimeConvertor.Days.Convertor") instanceof Collection))
				config.set("Options.TimeConvertor.Days.Convertor", Arrays.asList("=,1,day",">,1,days"));
			config.setIfAbsent("Options.TimeConvertor.Days.Lookup", Arrays.asList("d","da","day","days"));
			
			config.setIfAbsent("Options.TimeConvertor.Weeks.Lookup", Arrays.asList("w","we","wee","week","weeks"));
			
			config.setIfAbsent("Options.TimeConvertor.Months.Convertor", Arrays.asList("=,1,month",">,1,months"));
			if(!(config.get("Options.TimeConvertor.Months.Convertor") instanceof Collection))
				config.set("Options.TimeConvertor.Months.Convertor", Arrays.asList("=,1,month",">,1,months"));
			config.setIfAbsent("Options.TimeConvertor.Months.Lookup", Arrays.asList("mo","mon","mont","month","months"));
			
			config.setIfAbsent("Options.TimeConvertor.Years.Convertor", Arrays.asList("=,1,year",">,1,years"));
			if(!(config.get("Options.TimeConvertor.Years.Convertor") instanceof Collection))
				config.set("Options.TimeConvertor.Years.Convertor", Arrays.asList("=,1,year",">,1,years"));
			config.setIfAbsent("Options.TimeConvertor.Years.Lookup", Arrays.asList("y","ye","yea","year","years"));
			config.save();

			StringUtils.timeFormat=config.getString("Options.TimeConvertor.Format");
			
			List<String> sec = new ArrayList<>();
			StringUtils.actions.put("Seconds",sec);
			sec.addAll(config.getStringList("Options.TimeConvertor.Seconds.Convertor"));
			if(sec.isEmpty())
				sec.addAll(Arrays.asList("=,0,sec","=,1,sec",">,1,secs"));
			
			List<String> min = new ArrayList<>();
			StringUtils.actions.put("Minutes",min);
			min.addAll(config.getStringList("Options.TimeConvertor.Minutes.Convertor"));
			if(min.isEmpty())
				min.addAll(Arrays.asList("=,1,min",">,1,s"));
			
			List<String> hours = new ArrayList<>();
			StringUtils.actions.put("Hours",hours);
			hours.addAll(config.getStringList("Options.TimeConvertor.Hours.Convertor"));
			if(hours.isEmpty())
				hours.addAll(Arrays.asList("=,1,hour",">,1,hours"));
			
			List<String> days = new ArrayList<>();
			StringUtils.actions.put("Days",days);
			days.addAll(config.getStringList("Options.TimeConvertor.Days.Convertor"));
			if(days.isEmpty())
				days.addAll(Arrays.asList("=,1,day",">,1,days"));
			
			List<String> weeks = new ArrayList<>();
			StringUtils.actions.put("Weeks",weeks);
			weeks.addAll(config.getStringList("Options.TimeConvertor.Weeks.Convertor"));
			if(weeks.isEmpty())
				weeks.addAll(Arrays.asList("=,1,week",">,1,weeks"));

			List<String> month = new ArrayList<>();
			StringUtils.actions.put("Months",month);
			for(String action : config.getStringList("Options.TimeConvertor.Months.Convertor"))
				month.add(action);
			if(month.isEmpty())
				month.addAll(Arrays.asList("=,1,month",">,1,months"));
			
			List<String> years = new ArrayList<>();
			StringUtils.actions.put("Years",years);
			years.addAll(config.getStringList("Options.TimeConvertor.Years.Convertor"));
			if(years.isEmpty())
				years.addAll(Arrays.asList("=,1,year",">,1,years"));
			
			StringUtils.sec=Pattern.compile("([+-]?[0-9]+)("+StringUtils.join(Lists.reverse(config.getStringList("Options.TimeConvertor.Seconds.Lookup")), "|")+")",Pattern.CASE_INSENSITIVE);
			StringUtils.min=Pattern.compile("([+-]?[0-9]+)("+StringUtils.join(Lists.reverse(config.getStringList("Options.TimeConvertor.Minutes.Lookup")), "|")+")",Pattern.CASE_INSENSITIVE);
			StringUtils.hour=Pattern.compile("([+-]?[0-9]+)("+StringUtils.join(Lists.reverse(config.getStringList("Options.TimeConvertor.Hours.Lookup")), "|")+")",Pattern.CASE_INSENSITIVE);
			StringUtils.day=Pattern.compile("([+-]?[0-9]+)("+StringUtils.join(Lists.reverse(config.getStringList("Options.TimeConvertor.Days.Lookup")), "|")+")",Pattern.CASE_INSENSITIVE);
			StringUtils.week=Pattern.compile("([+-]?[0-9]+)("+StringUtils.join(Lists.reverse(config.getStringList("Options.TimeConvertor.Weeks.Lookup")), "|")+")",Pattern.CASE_INSENSITIVE);
			StringUtils.mon=Pattern.compile("([+-]?[0-9]+)("+StringUtils.join(Lists.reverse(config.getStringList("Options.TimeConvertor.Months.Lookup")), "|")+")",Pattern.CASE_INSENSITIVE);
			StringUtils.year=Pattern.compile("([+-]?[0-9]+)("+StringUtils.join(Lists.reverse(config.getStringList("Options.TimeConvertor.Years.Lookup")), "|")+")",Pattern.CASE_INSENSITIVE);
		}
		
		public String[] getLastColors(Pattern pattern, String text) {
			Matcher m = pattern.matcher(text);
			String color = null;
			StringBuilder formats = new StringBuilder();
			while (m.find()) {
				String last = m.group(1).toLowerCase();
				if(last.charAt(1)!='x' && isFormat((int)last.charAt(1))) {
					if(last.charAt(1)=='r') {
						formats.delete(0, formats.length());
						continue;
					}
					formats.append(last.charAt(1));
					continue;
				}
				color = last.replace("§", "").replace("&", "");
				formats.delete(0, formats.length());
			}
			return new String[] {color,formats.toString()};
		}
		
		public String gradient(String msg, String fromHex, String toHex) {
			if(msg==null||fromHex==null||toHex==null)return msg;
			Matcher ma = reg.matcher(msg);
			HashMap<Integer, String> l = new HashMap<>();
			while (ma.find()) {
				l.put(msg.indexOf(ma.group()), ma.group(1).toLowerCase());
				msg = msg.replaceFirst(ma.group(), "");
			}
			int length = msg.length();
			Color fromRGB = Color.decode(fromHex);
			Color toRGB = Color.decode(toHex);
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
			msg = msg.replaceAll("#[A-Fa-f0-9]{6}", "");
			msg = msg.replace("", "<!>");
			msg = msg.substring(0, msg.length() - 3);
			msg = msg.replace("<!> "," ");
			Matcher fixColors = colorMatic.matcher(msg);
			String formats = "";
			while (fixColors.find())
				msg = msg.replace(fixColors.group(), fixColors.group(2) + fixColors.group(3));
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
				StringBuilder hex = new StringBuilder("#");
				char[] c = Integer.toHexString(finalColor.getRGB()).substring(2).toCharArray();
				for (char value : c) hex.append(value);
				if (l.containsKey(index))
					switch (l.get(index).charAt(0)) {
					case 'l':
						if (!formats.contains("§l"))
							formats += "§l";
						break;
					case 'm':
						if (!formats.contains("§m"))
							formats += "§m";
						break;
					case 'n':
						if (!formats.contains("§n"))
							formats += "§n";
						break;
					case 'o':
						if (!formats.contains("§o"))
							formats += "§o";
						break;
					case 'k':
						if (!formats.contains("§k"))
							formats += "§k";
						break;
					default:
						formats = "";
						break;
					}
				msg = msg.replaceFirst("<!>", hex.append(formats).toString());
			}
			return msg;
		}
		
		private boolean isFormat(int charAt) {
			return charAt >= 107 && 107 <= 111 || charAt == 114;
		}
	}
	
	public static Basics basics() {
		return basics;
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
			return ((value * 1000.0) / 10.0);
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * @see see Generate random int with limit
	 * @param maxInt
	 * @return int
	 */
	public static int generateRandomInt(int maxInt) {
		return generateRandomInt(0, maxInt);
	}

	/**
	 * @see see Generate random double with limit
	 * @param maxDouble
	 * @return double
	 */
	public static double generateRandomDouble(double maxDouble) {
		return generateRandomDouble(0, maxDouble);
	}

	/**
	 * @see see Generate random double with limit
	 * @param maxDouble
	 * @return double
	 */
	public static double generateRandomDouble(double min, double maxDouble) {
		return StringUtils.generateRandomDouble(min, maxDouble);
	}

	/**
	 * @see see Generate random double with limit
	 * @param maxDouble
	 * @return double
	 */
	public static int generateRandomInt(int min, int maxInt) {
		return StringUtils.generateRandomInt(min, maxInt);
	}

	/**
	 * @see see Server up time in long
	 * @return long
	 */
	public static long getServerUpTime() {
		return ManagementFactory.getRuntimeMXBean().getUptime();
	}
}
