package me.devtec.theapi.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.utils.json.Writer;

public class ChatMessage {
	static Pattern url = Pattern.compile("(w{3}\\.|(https?|ftp|file):\\/\\/)[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"),
			colorOrRegex = Pattern.compile("#[A-Fa-f0-9]{6}|[&§]x([&§][A-Fa-f0-9]){6}|[&§][A-Fa-f0-9RrK-Ok-o]");
	private static String fixedHex  ="[&§][xX][&§]([A-Fa-f0-9])[&§]([A-Fa-f0-9])[&§]([A-Fa-f0-9])[&§]([A-Fa-f0-9])[&§]([A-Fa-f0-9])[&§]([A-Fa-f0-9])";
	private String text, color = "";
	boolean bold = false, italic = false, obfuscated = false, strike = false, under = false, change = false;
	
	private List<Map<String, Object>> join = new ArrayList<>();
	
	public ChatMessage(String text) {
		this.text=text;
		convert();
	}
	
	@SuppressWarnings("unchecked")
	static Map<String, Object> fix(Map<String, Object> h){
		if(h==null)return null;
		Map<String, Object> f = new HashMap<>();
		f.put("action", h.get("action"));
		String n = h.containsKey("value")?"value":"contents";
		f.put("value", h.get(n) instanceof Map ? h.get(n):h.get(n) instanceof List ? fixListMap((List<Map<String, Object>>)h.get(n)): fromString(h.get(n).toString()).join);
		return f;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> fixListMap(List<Map<String, Object>> lists) {
		if(lists==null)return null;
		ListIterator<Map<String, Object>> it = lists.listIterator();
		while(it.hasNext()) {
			Map<String, Object> text = it.next();
		Map<String, Object> hover=fix((Map<String, Object>) text.remove("hoverEvent")), click=fix((Map<String, Object>) text.remove("clickEvent"));
		for(Entry<String, Object> s : text.entrySet()) {
			if(s.getValue()instanceof String) {
				ChatMessage c = new ChatMessage((String) s.getValue());
				if(!c.join.isEmpty()) {
					try {
						it.remove();
						}catch(Exception err) {}
					for(Map<String, Object> d : c.join) {
						it.add(d);
						for(Entry<String, Object> g : text.entrySet())
							if(!g.getKey().equals("text") && !d.containsKey(g.getKey()))
								d.put(g.getKey(), g.getValue());
						if(!d.containsKey("color") && text.containsKey("color"))
							d.put("color", text.get("color"));
						if(hover!=null)
							d.put("hoverEvent", hover);
						if(click!=null)
							d.put("clickEvent", click);
					}
				}
			}else
			if(s.getValue()instanceof Map) {
				text.put(s.getKey(), (Map<String, Object>) s.getValue());
			}else
			text.put(s.getKey(), s.getValue());
		}
		if(hover!=null)
		text.put("hoverEvent", hover);
		if(click!=null)
		text.put("clickEvent", click);
		}
		return lists;
	}
	
	public static ChatMessage fromString(String text) {
		return new ChatMessage(text);
	}
	
	public List<Map<String, Object>> get(){
		return new ArrayList<>(join);
	}
	
	public String getJson() {
		return Writer.write(join);
	}
	
	private void convert() {
		if(text.equals("")) {
			return;
		}
		List<Object[]> colors = parse();
		for(int i = 0; i < colors.size(); ++i) {
			if(colors.get(i)[0]==null||colors.get(i)[0].toString().replaceAll("[&§][A-Fa-f0-9K-Ok-oRrXx]|#[A-Fa-f0-9]{6}", "").equals(""))continue;
			HashMap<String, Object> c=new HashMap<>();
			join.add(c);
			c.put("text", colors.get(i)[0]+"");
			if(!(colors.get(i)[0]+"").trim().isEmpty()) {
				if(colors.get(i)[1]!=null && !colors.get(i)[1].equals(""))
					c.put("color", colors.get(i)[1]+"");
				for(int is = 2; is < 7; ++is)
					if(colors.get(i)[is]!=null)
						c.put(is==2?"bold":(is==3?"italic":(is==4?"obfuscated":(is==5?"strikethrough":"underlined"))), (boolean)colors.get(i)[is]);
				if(colors.get(i)[7]!=null && !colors.get(i)[7].equals(""))
					c.put("clickEvent", colors.get(i)[7]);
			}
		}
	}
	
	public void reset() {
		text="";
		bold = false;
		italic = false;
		obfuscated = false;
		strike = false;
		under = false;
		change = false;
		color="";
		join.clear();
		convert();
	}
	
	public void reset(String text) {
		this.text=text;
		bold = false;
		italic = false;
		obfuscated = false;
		strike = false;
		under = false;
		change = false;
		color="";
		join.clear();
		convert();
	}
	
	//text, color, bold, italic, obfus, strike, under, url
	private List<Object[]> parse() {
		List<Object[]> colors = new ArrayList<>();
		Object[] actual = new Object[8];
		colors.add(actual);
		String val = "", url=null;
		for(char c : text.toCharArray()) {
			val+=c;
			Matcher mmm = ChatMessage.url.matcher(val);
			if(mmm.find() && !val.endsWith(" ")) {
				url=mmm.group();
				continue;
			}else if(url!=null) {
				String v = val.replaceAll(url+"[ ]*", "");
				if(!v.equals("")) {
					actual[0]=v;
					if(color!=null && !color.equals(""))
						actual[1]=color;
					actual=new Object[8];
					colors.add(actual);
				}
				setupUrl(actual, url);
				actual[0]=url;
				if(color!=null && !color.equals(""))
					actual[1]=color;
				url=null;
				actual=new Object[8];
				colors.add(actual);
				actual[0]=c;
				if(color!=null && !color.equals(""))
					actual[1]=color;
				val=c+"";
			}
			String last = StringUtils.getLastColors(val);
			if(!last.equals(""))
			val=val.replaceAll("[&§][A-Fa-f0-9K-Ok-oRrXx]|#[A-Fa-f0-9]{6}", "");
			actual[0]=val;
			if(!last.equals(""))
			applyChanges(actual, last);
			if(change) {
				change=false;
				if(!val.replaceAll("[&§][A-Fa-f0-9K-Ok-oRrXx]|#[A-Fa-f0-9]{6}", "").equals("")) {
					val="";
					actual=new Object[8];
					colors.add(actual);
				}
			}
			if(color!=null && !color.equals(""))
				actual[1]=color;
			else
				actual[1]=null;
			if(bold)
				actual[2]=bold;
			else
				actual[2]=null;
			if(italic)
				actual[3]=italic;
			else
				actual[3]=null;
			if(obfuscated)
				actual[4]=obfuscated;
			else
				actual[4]=null;
			if(strike)
				actual[5]=strike;
			else
				actual[5]=null;
			if(under)
				actual[6]=under;
			else
				actual[6]=null;
		}
		if(url!=null) {
			val=val.replace(url, "");
			actual[0]=val;
			actual=new Object[8];
			colors.add(actual);
			setupUrl(actual, url);
			actual[0]=url;
			if(color!=null && !color.equals(""))
				actual[1]=color;
			if(bold)
				actual[2]=bold;
			else
				actual[2]=null;
			if(italic)
				actual[3]=italic;
			else
				actual[3]=null;
			if(obfuscated)
				actual[4]=obfuscated;
			else
				actual[4]=null;
			if(strike)
				actual[5]=strike;
			else
				actual[5]=null;
			if(under)
				actual[6]=under;
			else
				actual[6]=null;
		}
		return colors;
	}
	
	private void setupUrl(Object[] actual, String url) {
		Map<String, Object> map = new HashMap<>();
		map.put("action", "OPEN_URL");
		map.put("value", url);
		actual[7]=map;
	}

	boolean same(Object[] ac, String text) {
		Object[] actual = new Object[6];
		actual[0]=ac[1];
		actual[1]=ac[2];
		actual[2]=ac[3];
		actual[3]=ac[4];
		actual[4]=ac[5];
		actual[5]=ac[6];
		String val = "";
		for(char c : text.toCharArray()) {
			val+=c;
			String last = StringUtils.getLastColors(val);
			if(!last.equals(""))
			val=val.replaceAll("[&§][A-Fa-f0-9K-Ok-oRrXx]|#[A-Fa-f0-9]{6}", "");
			if(!last.equals(""))
			apply(actual, last);
		}
		return actual[0]==null?ac[1]==null:ac[1]==null?true:actual[0].equals(ac[1]) 
				&& actual[1]==ac[2] 
				&& actual[2]==ac[3] 
				&& actual[3]==ac[4] 
				&& actual[4]==ac[5]
				&& actual[5]==ac[6];
	}

	private void apply(Object[] actual, String next) {
		if(next.contains("l")) {
			actual[1]=true;
		}else
		if(next.contains("o")) {
			actual[2]=true;
		}else
		if(next.contains("k")) {
			actual[3]=true;
		}else
		if(next.contains("m")) {
			actual[4]=true;
		}else
		if(next.contains("n")) {
			actual[5]=true;
		}else {
			if(!next.contains("r"))
				if(actual[1]==null || !actual[1].equals(getColorName(next))) {
					if(getColorName(next).equals("WHITE"))actual[0]="";
					else
					actual[0]=getColorName(next);
				}
			if(actual[1]==null || (boolean)actual[1]==true) {
				actual[1]=null;
			}
			if(actual[2]==null || (boolean)actual[2]==true) {
				actual[2]=null;
			}
			if(actual[3]==null || (boolean)actual[3]==true) {
				actual[3]=null;
			}
			if(actual[4]==null || (boolean)actual[4]==true) {
				actual[4]=null;
			}
			if(actual[5]==null || (boolean)actual[5]==true) {
				actual[5]=null;
			}
		}
	}

	private void applyChanges(Object[] actual, String next) {
		if(next.contains("l")) {
			if(bold)return;
			bold=true;
			change=true;
		}else
		if(next.contains("o")) {
			if(italic)return;
			italic=true;
			change=true;
		}else
		if(next.contains("k")) {
			if(obfuscated)return;
			obfuscated=true;
			change=true;
		}else
		if(next.contains("m")) {
			if(strike)return;
			strike=true;
			change=true;
		}else
		if(next.contains("n")) {
			if(under)return;
			under=true;
			change=true;
		}else {
			if(!next.contains("r"))
				if(color==null || !color.equals(getColorName(next))) {
					color=getColorName(next);
					change=true;
				}
			if(bold) {
				bold=false;
				change=true;
			}
			if(italic) {
				italic=false;
				change=true;
			}
			if(obfuscated) {
				obfuscated=false;
				change=true;
			}
			if(strike) {
				strike=false;
				change=true;
			}
			if(under) {
				under=false;
				change=true;
			}
		}
	}
	
	private String getColorName(String next) {
		if(next.equals(""))return null;
		if(TheAPI.isNewerThan(15)) {
		if(next.startsWith("#")) {
			return next;
		}
		if(next.startsWith("&x")||next.startsWith("§x")) {
			return next.replaceAll(fixedHex, "#$2$3$4$5$6");
		}}
		switch(next.substring(1)) {
		case "0":
			return "black";
		case "1":
			return "dark_blue";
		case "2":
			return "dark_green";
		case "3":
			return "dark_aqua";
		case "4":
			return "dark_red";
		case "5":
			return "dark_purple";
		case "6":
			return "gold";
		case "7":
			return "gray";
		case "8":
			return "dark_gray";
		case "9":
			return "blue";
		case "a":
			return "green";
		case "b":
			return "aqua";
		case "c":
			return "red";
		case "d":
			return "light_purple";
		case "e":
			return "yellow";
		case "f":
			return "white";
		}
		return "white";
	}
}
