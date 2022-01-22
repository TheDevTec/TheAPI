package me.devtec.theapi.utils.datakeeper.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.devtec.theapi.utils.json.Json;

public class YamlLoader extends EmptyLoader {
	private static final Pattern pattern = Pattern.compile("([ ]*)(['\\\"][^'\\\"]+['\\\"]|[^\\\"']?\\\\w+[^\\\"']?|.*?):[ ]*(.*)");

	public void reset() {
		super.reset();
		loaded=false;
	}
	
	public void load(File file) {
		reset();
		if(file==null)return;
		try {
			String key = "";
			int last = 0;
			BuilderType type = null;
			List<Object> items=null;
			StringBuilder builder=null;
			boolean wasEmpty = false;
			LinkedList<String> comments = new LinkedList<>();
			
			BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8), 8192);
			String line;
			while((line=r.readLine())!=null) {
				String trim = line.trim();
				if(trim.isEmpty()) {
					comments.add(trim);
					continue;
				}
				if(trim.charAt(0)=='#') {
					comments.add(line.substring(removeSpaces(line)));
					continue;
				}

				String e = line.substring(removeSpaces(line));
				if(wasEmpty && e.startsWith("- ")) {
					if(items==null)items=new LinkedList<>();
					items.add(Json.reader().read(r(e.substring(2))));
					continue;
				}
				
				Matcher match = pattern.matcher(line);
				if(match.find()) {
					if(type!=null) {
						if(type==BuilderType.LIST) {
							data.put(key, new Object[] {new LinkedList<>(items), comments.isEmpty()?null:new LinkedList<>(comments)});
							comments.clear();
							items=null;
						}else {
							data.put(key, new Object[] {builder.toString(), comments.isEmpty()?null:new LinkedList<>(comments)});
							comments.clear();
							builder=null;
						}
						type=null;
					}else {
						if(items!=null) {
							data.put(key, new Object[] {new LinkedList<>(items), comments.isEmpty()?null:new LinkedList<>(comments)});
							comments.clear();
							items=null;
						}
					}
					
					int sub = match.group(1).length();
					String keyr = r(match.group(2));
					String value = match.group(3);
					
					if (sub <= last) {
						if (sub==0)
							key = "";
						else {
							if (sub == last) {
								String[] ff = split(key);
								String lastr = ff[ff.length - 1] + 1;
								int remove = key.length() - lastr.length();
								if (remove > 0)
									key = key.substring(0, remove);
							} else {
								for (int i = 0; i < Math.abs(last - sub) / 2 + 1; ++i) {
									String[] ff = split(key);
									String lastr = ff[ff.length - 1] + 1;
									int remove = key.length() - lastr.length();
									if (remove < 0)
										break;
									key = key.substring(0, remove);
								}
							}
						}
					}
					
					last=sub;
					if(!key.isEmpty())key+=".";
					key += keyr;

					if(wasEmpty)comments.clear();
					if(wasEmpty=value.trim().isEmpty())
						continue;
					
					value=r(value);
					
					if (value.equals("|")) {
						type=BuilderType.STRING;
						builder=new StringBuilder();
						continue;
					}
					if (value.equals("|-")) {
						type=BuilderType.LIST;
						items=new LinkedList<>();
						continue;
					}
					if (value.equals("[]")) {
						data.put(key, new Object[] {Collections.EMPTY_LIST, comments.isEmpty()?null:new LinkedList<>(comments),value});
						comments.clear();
						continue;
					}
					data.put(key, new Object[] {Json.reader().read(value), comments.isEmpty()?null:new LinkedList<>(comments),value});
					comments.clear();
				}else {
					if(type!=null) {
						if(type==BuilderType.LIST) {
							items.add(Json.reader().read(r(line.substring(removeSpaces(line)))));
						}else {
							builder.append(line.substring(removeSpaces(line)));
						}
					}
				}
			}
			if(type!=null) {
				if(type==BuilderType.LIST) {
					data.put(key, new Object[] {items, comments.isEmpty()?null:comments});
				}else {
					data.put(key, new Object[] {builder.toString(), comments.isEmpty()?null:comments});
				}
			}else if(items!=null)
				data.put(key, new Object[] {items, comments.isEmpty()?null:comments});
			loaded = true;
			r.close();
		} catch (Exception e) {
			loaded = false;
		}
	}
	
	@Override
	public void load(String input) {
		reset();
		if(input==null)return;
		try {
			String key = "";
			int last = 0;
			BuilderType type = null;
			List<Object> items=null;
			StringBuilder builder=null;
			boolean wasEmpty = false;
			LinkedList<String> comments = new LinkedList<>();
			
			for(String line : input.split(System.lineSeparator())) {
				String trim = line.trim();
				if(trim.isEmpty()) {
					comments.add(trim);
					continue;
				}
				if(trim.charAt(0)=='#') {
					comments.add(line.substring(removeSpaces(line)));
					continue;
				}

				String e = line.substring(removeSpaces(line));
				if(wasEmpty && e.startsWith("- ")) {
					if(items==null)items=new LinkedList<>();
					items.add(Json.reader().read(r(e.substring(2))));
					continue;
				}
				
				Matcher match = pattern.matcher(line);
				if(match.find()) {
					if(type!=null) {
						if(type==BuilderType.LIST) {
							data.put(key, new Object[] {new LinkedList<>(items), comments.isEmpty()?null:new LinkedList<>(comments)});
							comments.clear();
							items=null;
						}else {
							data.put(key, new Object[] {builder.toString(), comments.isEmpty()?null:new LinkedList<>(comments)});
							comments.clear();
							builder=null;
						}
						type=null;
					}else {
						if(items!=null) {
							data.put(key, new Object[] {new LinkedList<>(items), comments.isEmpty()?null:new LinkedList<>(comments)});
							comments.clear();
							items=null;
						}
					}
					
					int sub = match.group(1).length();
					String keyr = r(match.group(2));
					String value = match.group(3);
					
					if (sub <= last) {
						if (sub==0)
							key = "";
						else {
							if (sub == last) {
								String[] ff = split(key);
								String lastr = ff[ff.length - 1] + 1;
								int remove = key.length() - lastr.length();
								if (remove > 0)
									key = key.substring(0, remove);
							} else {
								for (int i = 0; i < Math.abs(last - sub) / 2 + 1; ++i) {
									String[] ff = split(key);
									String lastr = ff[ff.length - 1] + 1;
									int remove = key.length() - lastr.length();
									if (remove < 0)
										break;
									key = key.substring(0, remove);
								}
							}
						}
					}
					
					last=sub;
					if(!key.isEmpty())key+=".";
					key += keyr;

					if(wasEmpty)comments.clear();
					if(wasEmpty=value.trim().isEmpty())
						continue;
					
					value=r(value);
					
					if (value.equals("|")) {
						type=BuilderType.STRING;
						builder=new StringBuilder();
						continue;
					}
					if (value.equals("|-")) {
						type=BuilderType.LIST;
						items=new LinkedList<>();
						continue;
					}
					if (value.equals("[]")) {
						data.put(key, new Object[] {Collections.EMPTY_LIST, comments.isEmpty()?null:new LinkedList<>(comments),value});
						comments.clear();
						continue;
					}
					data.put(key, new Object[] {Json.reader().read(value), comments.isEmpty()?null:new LinkedList<>(comments),value});
					comments.clear();
				}else {
					if(type!=null) {
						if(type==BuilderType.LIST) {
							items.add(Json.reader().read(r(line.substring(removeSpaces(line)))));
						}else {
							builder.append(line.substring(removeSpaces(line)));
						}
					}
				}
			}
			if(type!=null) {
				if(type==BuilderType.LIST) {
					data.put(key, new Object[] {items, comments.isEmpty()?null:comments});
				}else {
					data.put(key, new Object[] {builder.toString(), comments.isEmpty()?null:comments});
				}
			}else if(items!=null)
				data.put(key, new Object[] {items, comments.isEmpty()?null:comments});
			loaded = true;
		} catch (Exception er) {
			loaded = false;
		}
	}
	
	public enum BuilderType {
		STRING, LIST
	}

	public static int removeSpaces(String s) {
		int i = 0;
		for(int d = 0; d < s.length(); ++d) {
			if(s.charAt(d)==' ') {
				++i;
			}else break;
		}
		return i;
	}
	
	private static String[] split(String text) {
        int off = 0, next = text.indexOf('.', off);
        if(next==-1)
            return new String[] {text};
        ArrayList<String> list = new ArrayList<>();
        while (next != -1) {
            list.add(text.substring(off, next));
            off = next + 1;
            next = text.indexOf('.', off);
        }
        list.add(text.substring(off, text.length()));
        return list.toArray(new String[list.size()]);
	}

	private static String r(String key) {
		String k = key.trim();
		return k.length() > 1 && (k.startsWith("\"") && k.endsWith("\"")||k.startsWith("'") && k.endsWith("'"))?key.substring(1, key.length()-1-removeLastSpaces(key)):key;
	}

	public static int removeLastSpaces(String s) {
		int i = 0;
		for(int d = s.length()-1; d > 0; --d) {
			if(s.charAt(d)==' ') {
				++i;
			}else break;
		}
		return i;
	}
}
