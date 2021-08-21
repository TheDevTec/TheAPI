package me.devtec.theapi.utils.datakeeper.loader;

import me.devtec.theapi.utils.json.JsonReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YamlLoader extends DataLoader {
	private static final Pattern pattern = Pattern.compile("[ ]*(['\"][^'\"]+['\"]|[^\"']?\\w+[^\"']?|.*?):[ ]*(.*)"),
			fixedSplitter=Pattern.compile("^\"(.*)(\"([ ]*#?.*?))$|^'(.*)('([ ]*#?.*?))$|(.*)");
	private final Map<String, Object[]> data = new LinkedHashMap<>();
	private List<String> header = new LinkedList<>(), footer = new LinkedList<>();
	private boolean l;

	public Set<String> getKeys() {
		return data.keySet();
	}

	public void set(String key, Object[] holder) {
		if (key == null)
			return;
		if (holder == null) {
			remove(key);
			return;
		}
		data.put(key, holder);
	}

	public void remove(String key) {
		if (key == null)
			return;
		data.remove(key);
	}

	public void reset() {
		data.clear();
		header.clear();
		footer.clear();
	}
	
	@Override
	public Map<String, Object[]> get() {
		return data;
	}
	
	public void load(File file) {
		reset();
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8), 8192);
			LinkedList<Object> items = new LinkedList<>();
			LinkedList<String> lines = new LinkedList<>();
			String key = "";
			StringBuilder v = null;
			int last = 0, f = 0, c = 0;
			Iterator<String> rr = r.lines().iterator();
			while(rr.hasNext()) {
				String text = rr.next();
				String h = text.trim();
				if (h.isEmpty()||h.startsWith("#")) {
					if (!items.isEmpty()) {
						set(key, items, lines);
						items = new LinkedList<>();
					}
					if (c != 0) {
						if (c == 1) {
							String object = v.toString();
							Matcher m = fixedSplitter.matcher(object);
							if(m.find())
							object=m.group(1)==null?(m.group(4)==null?m.group(7):m.group(4)):m.group(1);
							if(object==null)object="";
							set(key, object, lines);
							v = null;
						} else if (c == 2) {
							set(key, items, lines);
							items = new LinkedList<>();
						}
						c = 0;
					}
					if (f == 0) {
						header.add(text.substring(c(text)));
					} else {
						lines.add(text.substring(c(text)));
					}
					continue;
				}
				Matcher sec = pattern.matcher(text);
				boolean find = sec.find();
				if (c != 0 && find) {
					if (c == 1) {
						String object = v.toString();
						Matcher m = fixedSplitter.matcher(object);
						if(m.find())
						object=m.group(1)==null?(m.group(4)==null?m.group(7):m.group(4)):m.group(1);
						if(object==null)object="";
						set(key, object, lines, object);
						v = null;
					}
					if (c == 2) {
						set(key, items, lines);
						items = new LinkedList<>();
					}
					c = 0;
				}
				if (c == 2 || text.substring(c(text)).startsWith("- ") && !key.equals("")) {
					String object = c != 2 ? text.substring(c(text)+2)
							: text.substring(c(text));
					Matcher m = fixedSplitter.matcher(object);
					if(m.find())
					object=m.group(1)==null?(m.group(4)==null?m.group(7):m.group(4)):m.group(1);
					if(object==null)object="";
					items.add(object);
					continue;
				}
				if (find) {
					if (!items.isEmpty()) {
						set(key, items, lines);
						items = new LinkedList<>();
					}
					int sub = c(text);
					if (c == 1) {
						v.append(text.substring(sub));
						continue;
					}
					if (sub <= last) {
						if (!text.startsWith(" "))
							key = "";
						if (sub == last) {
							String[] ff = key.split("\\.");
							String lastr = ff[ff.length - 1] + 1;
							int remove = key.length() - lastr.length();
							if (remove > 0)
								key = key.substring(0, remove);
						} else {
							for (int i = 0; i < Math.abs(last - sub) / 2 + 1; ++i) {
								String[] ff = key.split("\\.");
								String lastr = ff[ff.length - 1] + 1;
								int remove = key.length() - lastr.length();
								if (remove < 0)
									break;
								key = key.substring(0, remove);
							}
						}
					}
					String split = sec.group(1);
					Matcher m = fixedSplitter.matcher(split);
					if(m.find())
						split=m.group(1)==null?(m.group(4)==null?m.group(7):m.group(4)):m.group(1);
					if(split==null)split="";
					String object;
					String fix = null;
					try {
						object = sec.group(2);
						fix = object.trim();
						m=m.reset(object);
						if(m.find())
							object=m.group(1)==null?(m.group(4)==null?m.group(7):m.group(4)):m.group(1);
					} catch (Exception er) {
						object="";
					}
					if(!key.equals(""))key+=".";
					key += split;
					f = 1;
					last = sub;
					if (fix != null) {
						if (!fix.isEmpty()) {
							if (fix.equals("|")) {
								c = 1;
								if(v==null)
								v = new StringBuilder();
								else v=v.delete(0, v.length());
								continue;
							}
							if (fix.equals("|-")) {
								c = 2;
								if(v==null)
								v = new StringBuilder();
								else v=v.delete(0, v.length());
								continue;
							}
							if (fix.equals("[]")) {
								set(key, new ArrayList<>(), lines);
								continue;
							}
							set(key, object, lines, object);
						} else if (!lines.isEmpty())
							set(key, null, lines);
					}
				}
			}
			r.close();
			if (!items.isEmpty() || c == 2) {
				set(key, items, lines);
			} else if (c == 1) {
				set(key, JsonReader.read(v.toString()), lines, v.toString());
			} else if (!lines.isEmpty()) {
				if(data.isEmpty())header=lines;
				else
				footer = lines;
			}
			l = true;
		} catch (Exception e) {
			reset();
		}
	}
	
	@Override
	public void load(String input) {
		reset();
		try {
			LinkedList<Object> items = new LinkedList<>();
			LinkedList<String> lines = new LinkedList<>();
			String key = "";
			StringBuilder v = null;
			int last = 0, f = 0, c = 0;
			if (!input.equals(""))
				for (String text : input.split(System.lineSeparator())) {
					String h = text.trim();
					if (h.isEmpty()||h.startsWith("#")) {
						if (!items.isEmpty()) {
							set(key, items, lines);
							items = new LinkedList<>();
						}
						if (c != 0) {
							if (c == 1) {
								String object = v.toString();
								Matcher m = fixedSplitter.matcher(object);
								if(m.find())
								object=m.group(1)==null?(m.group(4)==null?m.group(7):m.group(4)):m.group(1);
								if(object==null)object="";
								set(key, object, lines);
								v = null;
							} else if (c == 2) {
								set(key, items, lines);
								items = new LinkedList<>();
							}
							c = 0;
						}
						if (f == 0) {
							header.add(text.substring(c(text)));
						} else {
							lines.add(text.substring(c(text)));
						}
						continue;
					}
					Matcher sec = pattern.matcher(text);
					boolean find = sec.find();
					if (c != 0 && find) {
						if (c == 1) {
							String object = v.toString();
							Matcher m = fixedSplitter.matcher(object);
							if(m.find())
							object=m.group(1)==null?(m.group(4)==null?m.group(7):m.group(4)):m.group(1);
							if(object==null)object="";
							set(key, object, lines, object);
							v = null;
						}
						if (c == 2) {
							set(key, items, lines);
							items = new LinkedList<>();
						}
						c = 0;
					}
					if (c == 2 || text.substring(c(text)).startsWith("- ") && !key.equals("")) {
						String object = c != 2 ? text.substring(c(text)+2)
								: text.substring(c(text));
						Matcher m = fixedSplitter.matcher(object);
						if(m.find())
						object=m.group(1)==null?(m.group(4)==null?m.group(7):m.group(4)):m.group(1);
						if(object==null)object="";
						items.add(object);
						continue;
					}
					if (find) {
						if (!items.isEmpty()) {
							set(key, items, lines);
							items = new LinkedList<>();
						}
						int sub = c(text);
						if (c == 1) {
							v.append(text.substring(sub));
							continue;
						}
						if (sub <= last) {
							if (!text.startsWith(" "))
								key = "";
							if (sub == last) {
								String[] ff = key.split("\\.");
								String lastr = ff[ff.length - 1] + 1;
								int remove = key.length() - lastr.length();
								if (remove > 0)
									key = key.substring(0, remove);
							} else {
								for (int i = 0; i < Math.abs(last - sub) / 2 + 1; ++i) {
									String[] ff = key.split("\\.");
									String lastr = ff[ff.length - 1] + 1;
									int remove = key.length() - lastr.length();
									if (remove < 0)
										break;
									key = key.substring(0, remove);
								}
							}
						}
						String split = sec.group(1);
						Matcher m = fixedSplitter.matcher(split);
						if(m.find())
							split=m.group(1)==null?(m.group(4)==null?m.group(7):m.group(4)):m.group(1);
						if(split==null)split="";
						String object;
						String fix = null;
						try {
							object = sec.group(2);
							fix = object.trim();
							m=m.reset(object);
							if(m.find())
								object=m.group(1)==null?(m.group(4)==null?m.group(7):m.group(4)):m.group(1);
						} catch (Exception er) {
							object="";
						}
						if(!key.equals(""))key+=".";
						key += split;
						f = 1;
						last = sub;
						if (fix != null) {
							if (!fix.isEmpty()) {
								if (fix.equals("|")) {
									c = 1;
									if(v==null)
									v = new StringBuilder();
									else v=v.delete(0, v.length());
									continue;
								}
								if (fix.equals("|-")) {
									c = 2;
									if(v==null)
									v = new StringBuilder();
									else v=v.delete(0, v.length());
									continue;
								}
								if (fix.equals("[]")) {
									set(key, new ArrayList<>(), lines);
									continue;
								}
								set(key, object, lines, object);
							} else if (!lines.isEmpty())
								set(key, null, lines);
						}
					}
				}
			if (!items.isEmpty() || c == 2) {
				set(key, items, lines);
			} else if (c == 1) {
				set(key, JsonReader.read(v.toString()), lines, v.toString());
			} else if (!lines.isEmpty()) {
				if(data.isEmpty())header=lines;
				else
				footer = lines;
			}
			l = true;
		} catch (Exception er) {
			l = false;
		}
	}

	@Override
	public Collection<String> getHeader() {
		return header;
	}

	@Override
	public Collection<String> getFooter() {
		return footer;
	}

	private int c(String s) {
		int i = 0;
		for (byte c : s.getBytes())
			if (c == ' ')
				++i;
			else
				break;
		return i;
	}

	private void set(String key, Object o, LinkedList<String> lines) {
		if(key==null)return;
		Object[] s = data.get(key);
		if (s!=null) {
			s[0]=o;
		} else
			set(key, new Object[] {o, lines.isEmpty()?null:new LinkedList<>(lines), null});
		lines.clear();
	}

	private void set(String key, Object o, LinkedList<String> lines, String original) {
		if(key==null)return;
		Object[] s = data.get(key);
		if(o instanceof String) {
			o= JsonReader.read((String)o);
		}
		if (s!=null) {
			s[0]=o;
		} else
			set(key, new Object[] {o, lines.isEmpty()?null:new LinkedList<>(lines), original, 1});
		lines.clear();
	}

	@Override
	public boolean isLoaded() {
		return l;
	}
}
