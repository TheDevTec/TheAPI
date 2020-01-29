package me.Straiker123;

public class TimeConventorAPI {
	public TimeConventorAPI() {
		 sec =LoaderClass.config.getConfig().getString("Words.Second");
		 min =LoaderClass.config.getConfig().getString("Words.Minute");
		 h =LoaderClass.config.getConfig().getString("Words.Hour");
		 d =LoaderClass.config.getConfig().getString("Words.Day");
		 w =LoaderClass.config.getConfig().getString("Words.Week");
		 mon = LoaderClass.config.getConfig().getString("Words.Month");
		 y =LoaderClass.config.getConfig().getString("Words.Year");
		 c = LoaderClass.config.getConfig().getString("Words.Century");
		 mil =LoaderClass.config.getConfig().getString("Words.Millenium");
	}
	public static enum EndWords{
		Millenium,
		Century,
		Year,
		Month,
		Week,
		Day,
		Hour,
		Minute,
		Second
	}
	String sec;
	String min;
	String h;
	String d;
	String w ;
	String mon;
	String y;
	String c;
	String mil;
	/**
	 * Return current end word
	 * @param a
	 * @return String
	 */
	public String getEndWord(EndWords a) {
		if(a != null) {
			switch(a) {
			case Second:
				return sec;
			case Minute:
				return min;
			case Hour:
				return h;
			case Day:
				return d;
			case Week:
				return w;
			case Month:
				return mon;
			case Year:
				return y;
			case Century:
				return c;
			case Millenium:
				return mil;
			}
		}
		return null;
	}
	/**
	 * Set end word
	 * @param a
	 * @param string
	 */
	public void setEndWord(EndWords a, String string) {
		if(a != null) {
			switch(a) {
			case Second:
				sec=string;
				LoaderClass.config.getConfig().set("Words.Second", string);
				break;
			case Minute:
				min=string;
				LoaderClass.config.getConfig().set("Words.Minute", string);
				break;
			case Hour:
				h=string;
				LoaderClass.config.getConfig().set("Words.Hour", string);
				break;
			case Day:
				d=string;
				LoaderClass.config.getConfig().set("Words.Day", string);
				break;
			case Week:
				w=string;
				LoaderClass.config.getConfig().set("Words.Week", string);
				break;
			case Month:
				mon=string;
				LoaderClass.config.getConfig().set("Words.Month", string);
				break;
			case Year:
				y=string;
				LoaderClass.config.getConfig().set("Words.Year", string);
				break;
			case Century:
				c=string;
				LoaderClass.config.getConfig().set("Words.Century", string);
				break;
			case Millenium:
				mil=string;
				LoaderClass.config.getConfig().set("Words.Millenium", string);
				break;
			}
			LoaderClass.config.save();
		}
	}
	/**
	 * Get long from string
	 * @param s
	 * @return long
	 */
	public long getTimeFromString(String s) {
		int a = TheAPI.getNumbersAPI(s).getInt();
		if(s.endsWith(min))a=a*60;
		if(s.endsWith(h))a=a*3600;
		if(s.endsWith(d))a=(a*3600)*24;
		if(s.endsWith(w))a=((a*3600)*24)*4;
		if(s.endsWith(mon))a=(((a*3600)*24)*7)*4;
		if(s.endsWith(y))a=((((a*3600)*24)*7)*4)*12;
		if(s.endsWith(c))a=(((((a*3600)*24)*7)*4)*12)*100;
		if(s.endsWith(mil))a=((((((a*3600)*24)*7)*4)*12)*100)*1000;
		return a;
	}
	/**
	 * Set long to string
	 * @param l
	 * @return String
	 */
	public String setTimeToString(long l) {
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
			s = millenniums+mil+" : "+centuries+c+" "+ years +y;
			} else
			if(centuries > 0) {
			s = centuries+c+" : "+ years +y+" : "+ months+mon;
			} else
			if(years > 0) {
			s = years +y+" : "+ months+mon+" : "+ weeks+w+" : "+days +d;
			} else
			if(months > 0) {
			s = months+mon+" : "+ weeks+w+" : "+days + d+" : " +hours + h+" : " +minutes +min;
			} else
			if(weeks > 0) {
				if(minutes != 0)
					s = weeks+w+" : "+days + d+" : " +hours + h+" : " +minutes +min;
				else
				s = weeks+w+" : "+days + d+" : " +hours +h;
			} else
			if(days > 0) {
				if(minutes != 0)
			s = days+d+" : " +hours +h+" : " +minutes +min;
				else
					s = days+d+" : " +hours +h;
			} else
		    if(hours > 0) {
				if(seconds != 0)
			s = hours + h+" : " +minutes+min+" : " +seconds+s;
			else
				s = hours + h+" : " +minutes+min;
			} else
			if(minutes > 0) {
				if(seconds != 0)
				      s = minutes+min+" : " + seconds+s;
				else
			      s = minutes+min;
			 } else {
			s = seconds+s;
		    }
		    return s;
	}
}
