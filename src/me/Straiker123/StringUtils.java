package me.Straiker123;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

public class StringUtils {
	/**
	 * @see see Get Color from String
	 * @return ChatColor
	 */
	public ChatColor getColor(String fromString) {
		char colour = '\u0000';
		char[] chars = fromString.toCharArray();
		for (int i = 0; i < chars.length; ++i) {
    char code;
    char at = chars[i];
    if (at != '\u00a7' && at != '&' || i + 1 >= chars.length || ChatColor.getByChar((char)(code = chars[i + 1])) == null) continue;
    colour = code;
		}
		return colour == '\u0000' ? ChatColor.RESET : ChatColor.getByChar((char)colour);
	}
	/**
	 * @see see Transfer Collection to String
	 * @return HoverMessage
	 */
	public String join(Collection<?> toJoin, String split) {
		String r= "";
		for(Object s : toJoin)r=r+split+s.toString();
		r=r.replaceFirst(split, "");
		return r;
	}

	/**
	 * @see see Transfer List to String
	 * @return HoverMessage
	 */
	public String join(List<?> toJoin, String split) {
		String r= "";
		for(Object s : toJoin)r=r+split+s.toString();
		r=r.replaceFirst(split, "");
		return r;
	}
	/**
	 * @see see Transfer ArrayList to String
	 * @return HoverMessage
	 */
	public String join(ArrayList<?> toJoin, String split) {
		String r= "";
		for(Object s : toJoin)r=r+split+s.toString();
		r=r.replaceFirst(split, "");
		return r;
	}
	/**
	 * @see see Transfer Object[] to String
	 * @return HoverMessage
	 */
	public String join(Object[] toJoin, String split) {
		String r= "";
		for(Object s : toJoin)r=r+split+s.toString();
		r=r.replaceFirst(split, "");
		return r;
	}
	
	/**
	 * @see see Create clickable message
	 * @return HoverMessage
	 */
	public HoverMessage createHoverMessage(String message) {
		return new HoverMessage(message);
	}
	
	/**
	 * @see see Colorize string with colors
	 * @param string
	 * @return String
	 */
	public String colorize(String string) {
		if(string == null)return null;
		return ChatColor.translateAlternateColorCodes('&', string);
	}
	
	/**
	 * @see see Build string from String[]
	 * @param args
	 * @return String
	 * 
	 */
	public String buildString(String[] args) {
		if(args.length>0) {
		String msg = "";
		for (String string : args) {
			msg=msg+" "+string;
		}
		msg = msg.replaceFirst(" ",	"");
		return msg;
	}
	return null;
	}
	/**
	 * @see see Return random object from list
	 * @param list
	 * @return Object
	 */
	public Object getRandomFromList(List<?> list) {
		if(list.isEmpty()||list==null)return null;
		int r = new Random().nextInt(list.size());
		if(r<=0) {
			if(list.get(0)!=null) {
				return list.get(0);
			}
			return null;
		}else
		return list.get(r);
	}
	
	private String sec,min,h,d,w,mon,y,c,mil;
	/**
	 * @see see Get long from string
	 * @param s String
	 * @return long
	 */
	public long getTimeFromString(String s) {
		 sec =LoaderClass.config.getConfig().getString("Words.Second");
		 min =LoaderClass.config.getConfig().getString("Words.Minute");
		 h =LoaderClass.config.getConfig().getString("Words.Hour");
		 d =LoaderClass.config.getConfig().getString("Words.Day");
		 w =LoaderClass.config.getConfig().getString("Words.Week");
		 mon = LoaderClass.config.getConfig().getString("Words.Month");
		 y =LoaderClass.config.getConfig().getString("Words.Year");
		 c = LoaderClass.config.getConfig().getString("Words.Century");
		 mil =LoaderClass.config.getConfig().getString("Words.Millenium");
		long a = getInt(s);
		long t_min = a*60;
		long t_h = t_min*60;
		long t_d = t_h*24;
		long t_w = t_d*7;
		long t_mon = t_w*31;
		long t_y = t_mon*12;
		long t_c = t_y*100;
		long t_mil = t_c*1000;
		if(s.endsWith(min))a=t_min;
		if(s.endsWith(h))a=t_h;
		if(s.endsWith(d))a=t_d;
		if(s.endsWith(w))a=t_w;
		if(s.endsWith(mon))a=t_mon;
		if(s.endsWith(y))a=t_y;
		if(s.endsWith(c))a=t_c;
		if(s.endsWith(mil))a=t_mil;
		return a;
	}
	/**
	 * @see see Set long to string
	 * @param l long
	 * @return String
	 */
	public String setTimeToString(long l) {
		 sec =LoaderClass.config.getConfig().getString("Words.Second");
		 min =LoaderClass.config.getConfig().getString("Words.Minute");
		 h =LoaderClass.config.getConfig().getString("Words.Hour");
		 d =LoaderClass.config.getConfig().getString("Words.Day");
		 w =LoaderClass.config.getConfig().getString("Words.Week");
		 mon = LoaderClass.config.getConfig().getString("Words.Month");
		 y =LoaderClass.config.getConfig().getString("Words.Year");
		 c = LoaderClass.config.getConfig().getString("Words.Century");
		 mil =LoaderClass.config.getConfig().getString("Words.Millenium");
		long seconds = l%60;
		long minutes = l/60;
		long hours = minutes/60;
		long days = hours/24;
		long weeks = days/7;
		long months = weeks/4;
		long years = months/12;
		long centuries = years/100;
		long millenniums = centuries/1000;
		if(minutes>=60)minutes = minutes%60;
		if(hours>=24)hours = hours%24;
		if(days>=7)days = days%7;
		if(weeks>=4)weeks = weeks%4;
		if(months>=12)months = months%12;
		if(years>=100)years = years%100;
		if(centuries>=1000)centuries = centuries%1000;
		    String s = sec;

			if(millenniums > 0) {
			s = millenniums+mil+" "+centuries+c+" "+ years +y;
			} else
			if(centuries > 0) {
			s = centuries+c+" "+ years +y+" "+ months+mon;
			} else
			if(years > 0) {
			s = years +y+" "+ months+mon+" "+ weeks+w+" "+days +d;
			} else
			if(months > 0) {
			s = months+mon+" "+ weeks+w+" "+days + d+" " +hours + h+" " +minutes +min;
			} else
			if(weeks > 0) {
				if(minutes != 0)
					s = weeks+w+" "+days + d+" " +hours + h+" " +minutes +min;
				else
				s = weeks+w+" "+days + d+" " +hours +h;
			} else
			if(days > 0) {
				if(minutes != 0)
			s = days+d+" " +hours +h+" " +minutes +min;
				else
					s = days+d+" " +hours +h;
			} else
		    if(hours > 0) {
				if(seconds != 0)
			s = hours + h+" " +minutes+min+" " +seconds+s;
			else
				s = hours + h+" " +minutes+min;
			} else
			if(minutes > 0) {
				if(seconds != 0)
				      s = minutes+min+" " + seconds+s;
				else
			      s = minutes+min;
			 } else {
			s = seconds+s;
		    }
		    return s;
	}

	/**
	 * @see see Convert Location to String
	 * @return String
	 */
	public String getLocationAsString(Location loc) {
		if(loc == null)return null;
		return (loc.getWorld().getName()+","+loc.getX()+","+loc.getY()+","+loc.getBlockZ()+","+loc.getYaw()+","+loc.getPitch()).replace(".", "_");
	}

	/**
	 * @see see Create Location from String
	 * @return Location
	 */
	public Location getLocationFromString(String savedLocation) {
		if(savedLocation == null)return null;
		try {
			String[] s = savedLocation.replace("_", ".").split(",");
			return new Location(Bukkit.getWorld(s[0]),getDouble(s[1]), getDouble(s[2]), getDouble(s[3]),getFloat(s[4]),getFloat(s[5]));
			}catch(Exception er) {
				return null;
			}
	}
	
	/**
	 * @see see Get boolean from string
	 * @return double
	 */
	public boolean getBoolean(String fromString) {
		try {
		return Boolean.parseBoolean(fromString);
		}catch(Exception er) {
			return false;
		}
	}
	/**
	 * @see see Convert String to Math and Calculate exempt
	 * @return double
	 */
	public double calculate(String fromString) {
		ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("JavaScript");
        try {
			return getDouble(engine.eval(fromString).toString());
		} catch (ScriptException e) {
		}
		return 0;
	}
	/**
	 * @see see Get double from string
	 * @return double
	 */
	public double getDouble(String fromString) {
		String a=fromString.replaceAll("[a-zA-Z]+", "").replace(",", ".");
		if (isDouble(a)) {
		return Double.parseDouble(a);
		}else {
		return 0.0;
	}}

	/**
	 * @see see Is string, double ?
	 * @return boolean
	 */
	public boolean isDouble(String fromString) {
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
	public long getLong(String fromString) {
		String a=fromString.replaceAll("[a-zA-Z]+", "");
		if (isLong(a)) {
		return Long.parseLong(a);
		}
		else {
		return 0;
	}}
	/**
	 * @see see Is string, long ?
	 * @return
	 */
	public boolean isLong(String fromString) {
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
	public int getInt(String fromString) {
		String a=fromString.replaceAll("[a-zA-Z]+", "");
		if (isInt(a)) {
		return Integer.parseInt(a);
		}else {
		return 0;
	}}
	/**
	 * @see see Is string, int ?
	 * @return boolean
	 */
	public boolean isInt(String fromString) {
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
	public boolean isFloat(String fromString) {
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
	public float getFloat(String fromString) {
		String a=fromString.replaceAll("[a-zA-Z]+", "");
		if (isInt(a)) {
		return Float.parseFloat(a);
		}
		else {
		return 0;
	}}
}
