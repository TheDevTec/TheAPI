package me.DevTec.TheAPI.Utils.DataKeeper.loader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.DevTec.TheAPI.Utils.DataKeeper.Data.DataHolder;
import me.DevTec.TheAPI.Utils.DataKeeper.Maps.UnsortedMap;
import me.DevTec.TheAPI.Utils.Json.Reader;

public class YamlLoader implements DataLoader {
	private static final Pattern pattern = Pattern.compile("[ ]*(['\"][^'\"]+['\"]|[^\"']?\\w+[^\"']?|.*?):[ ]*(.*)");
	private Map<String, DataHolder> data = new UnsortedMap<>();
	private boolean l;
	private List<String> header = new ArrayList<>(), footer = new ArrayList<>();

	public Set<String> getKeys() {
		return data.keySet();
	}

	public void set(String key, DataHolder holder) {
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
	public Map<String, DataHolder> get() {
		return data;
	}

	@Override
	public void load(String input) {
		reset();
		try {
			List<Object> items = new ArrayList<>();
			List<String> lines = new ArrayList<>();
			String key = "";
			StringBuilder v = null;
			int last = 0, f = 0, c = 0;
			if (!input.equals(""))
				for (String text : input.split(System.lineSeparator())) {
					if (text.trim().startsWith("#") || text.trim().isEmpty()) {
						if (!items.isEmpty()) {
							set(key, items, lines);
							items = new ArrayList<>();
						}
						if (c != 0) {
							if (c == 1) {
								String object = v.toString();
								if ((object.startsWith("'") && object.trim().endsWith("'")
										|| object.startsWith("\"") && object.trim().endsWith("\""))
										&& object.length() > 1)
									object = r(object).substring(1, object.length() - 1);
								set(key, Reader.read(object), lines);
								v = null;
							} else if (c == 2) {
								set(key, items, lines);
								items = new ArrayList<>();
							}
							c = 0;
						}
						if (f == 0) {
							header.add(text.substring(c(text)));
						} else
							lines.add(text.substring(c(text)));
						continue;
					}
					Matcher sec = pattern.matcher(text);
					boolean find = sec.find();
					if (c != 0 && find) {
						if (c == 1) {
							String object = v.toString();
							if ((object.startsWith("'") && object.trim().endsWith("'")
									|| object.startsWith("\"") && object.trim().endsWith("\"")) && object.length() > 1)
								object = r(object).substring(1, object.length() - 1);
							set(key, Reader.read(object), lines);
							v = null;
						}
						if (c == 2) {
							set(key, items, lines);
							items = new ArrayList<>();
						}
						c = 0;
					}
					if (c == 2 || text.substring(c(text)).startsWith("- ") && !key.equals("")) {
						String object = c != 2 ? text.replaceFirst(text.split("- ")[0] + "- ", "")
								: text.substring(c(text));
						if ((object.startsWith("'") && object.trim().endsWith("'")
								|| object.startsWith("\"") && object.trim().endsWith("\"")) && object.length() > 1)
							object = r(object).substring(1, object.length() - 1);
						items.add(object);
						continue;
					}
					if (find) {
						if (!items.isEmpty()) {
							set(key, items, lines);
							items = new ArrayList<>();
						}
						if (c == 1) {
							v.append(text.substring(c(text)));
							continue;
						}
						if (c(text) <= last) {
							if (!text.startsWith(" "))
								key = "";
							if (c(text) == last) {
								String lastr = key.split("\\.")[key.split("\\.").length - 1] + 1;
								int remove = key.length() - lastr.length();
								if (remove > 0)
									key = key.substring(0, remove);
							} else {
								for (int i = 0; i < Math.abs(last - c(text)) / 2 + 1; ++i) {
									String lastr = key.split("\\.")[key.split("\\.").length - 1] + 1;
									int remove = key.length() - lastr.length();
									if (remove < 0)
										break;
									key = key.substring(0, remove);
								}
							}
						}
						String split = sec.group(1);
						if ((split.startsWith("'") && split.trim().endsWith("'")
								|| split.startsWith("\"") && split.trim().endsWith("\"")) && split.length() > 1)
							split = r(split).substring(1, split.length() - 1);
						String object = null;
						String org = null;
						try {
							object = sec.group(2);
							org = object;
							if ((object.startsWith("'") && object.trim().endsWith("'")
									|| object.startsWith("\"") && object.trim().endsWith("\"")) && object.length() > 1)
								object = r(object).substring(1, object.length() - 1);
						} catch (Exception er) {
						}
						if ((key.startsWith("'") && key.trim().endsWith("'")
								|| key.startsWith("\"") && key.trim().endsWith("\"")) && key.length() > 1)
							key = r(key).substring(1, key.length() - 1);
						key += (key.equals("") ? "" : ".") + split.trim();
						f = 1;
						last = c(text);
						if (org != null)
							if (!org.isEmpty()) {
								if (object.trim().equals("|")) {
									c = 1;
									v = new StringBuilder();
									continue;
								}
								if (object.trim().equals("|-")) {
									c = 2;
									v = new StringBuilder();
									continue;
								}
								set(key, Reader.read(object), lines);
							} else if (lines.isEmpty() == false)
								set(key, null, lines);
					}
				}
			if (!items.isEmpty() || c == 2) {
				set(key, items, lines);
			} else if (c == 1) {
				set(key, Reader.read(v.toString()), lines);
			} else if (!lines.isEmpty())
				footer = lines;
			l = true;
		} catch (Exception er) {
			l = false;
		}
	}

	private String r(String s) {
		int remove = 0;
		while (s.substring(s.length() - remove, s.length()).startsWith(" "))
			++remove;
		return s.substring(0, s.length() - remove);
	}

	@Override
	public List<String> getHeader() {
		return header;
	}

	@Override
	public List<String> getFooter() {
		return footer;
	}

	private final int c(String s) {
		int i = 0;
		for (char c : s.toCharArray())
			if (c == ' ')
				++i;
			else
				break;
		return i;
	}

	private final void set(String key, Object o, List<String> lines) {
		if ((key.startsWith("'") && key.trim().endsWith("'") || key.startsWith("\"") && key.trim().endsWith("\""))
				&& key.length() > 1)
			key = r(key).substring(1, key.length() - 1);
		if (get().containsKey(key)) {
			get().get(key).setValue(o);
		} else
			set(key, new DataHolder(o, new ArrayList<>(lines)));
		lines.clear();
	}

	@Override
	public boolean loaded() {
		return l;
	}

	@Override
	public String getDataName() {
		return "Data(YamlLoader:" + data.size() + ")";
	}
}
