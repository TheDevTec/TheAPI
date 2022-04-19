package me.devtec.shared.dataholder.loaders;

import java.util.Collections;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.devtec.shared.json.Json;

public class YamlLoader extends EmptyLoader {
	private static final Pattern pattern = Pattern.compile("([ ]*)(['\\\"][^'\\\"]+['\\\"]|[^\\\"']?\\\\w+[^\\\"']?|.*?):[ ]*(.*)");
	
	@Override
	public void load(String input) {
		reset();
		if(input==null)return;
		try {
			//SPACES - POSITION
			int last = 0;
			
			//EXTRA BUILDER TYPE
			BuilderType type = null;
			//LIST OR EXTRA BUILDER
			LinkedList<Object> items = null;
			//EXTRA BUILDER
			StringBuilder builder = null;
			
			//BUILDER
			String key = "";
			String value = null;
			
			//COMMENTS
			LinkedList<String> comments = new LinkedList<>();
			
			for(String line : input.split(System.lineSeparator())) {
				String trim = line.trim();
				if(trim.isEmpty()) {
					comments.add("");
					continue;
				}
				String e = line.substring(removeSpaces(line));
				if(trim.charAt(0)=='#') {
					comments.add(e);
					continue;
				}
				
				if(!key.equals("") && e.startsWith("- ")) {
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
							data.get(key)[0]=new LinkedList<>(items);
							items=null;
						}
					}
					
					int sub = match.group(1).length();
					String keyr = r(match.group(2));
					value = match.group(3);
					
					if (sub <= last) {
						if (sub==0)
							key = "";
						else {
							if (sub == last) {
								int remove = key.lastIndexOf('.');
								if (remove > 0)
									key = key.substring(0, remove);
							} else {
								for (int i = 0; i < Math.abs(last - sub) / 2 + 1; ++i) {
									int remove = key.lastIndexOf('.');
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
					
					if(value.toString().trim().isEmpty()) {
						value = null;
						data.put(key, new Object[] {null, comments.isEmpty()?null:new LinkedList<>(comments)});
						comments.clear();
						continue;
					}
					
					value=r(value.toString());
					
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
						data.put(key, new Object[] {Collections.emptyList(), comments.isEmpty()?null:new LinkedList<>(comments),value});
						comments.clear();
						continue;
					}
					data.put(key, new Object[] {Json.reader().read(value.toString()), comments.isEmpty()?null:new LinkedList<>(comments),value});
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
			loaded = true;
			if(type!=null) {
				if(type==BuilderType.LIST) {
					data.put(key, new Object[] {items, comments.isEmpty()?null:comments});
				}else {
					data.put(key, new Object[] {builder.toString(), comments.isEmpty()?null:comments});
				}
				return;
			}else if(items!=null) {
				data.put(key, new Object[] {items, comments.isEmpty()?null:comments});
				return;
			}
			if(data.isEmpty() && !comments.isEmpty()) {
				footer.addAll(comments);
			}
		} catch (Exception er) {
			er.printStackTrace();
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

	protected static String r(String key) {
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
