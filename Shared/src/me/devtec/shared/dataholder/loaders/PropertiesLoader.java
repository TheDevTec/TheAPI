package me.devtec.shared.dataholder.loaders;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.devtec.shared.json.Json;

public class PropertiesLoader extends EmptyLoader {
	private final Pattern pattern = Pattern.compile("(.*?)=(.*)");
	
	public void load(String input) {
		reset();
		if(input==null)return;
		List<String> comments = new LinkedList<>();
		for(String s : input.split(System.lineSeparator())) {
			String f = s.trim();
			if(f.isEmpty()||f.startsWith("#")) { //comment
			}else {
				Matcher m = pattern.matcher(s);
				if(m.find()) {
					data.put(m.group(1), new Object[] {Json.reader().read(m.group(2)), comments.isEmpty()?null:new LinkedList<>(comments),m.group(2)});
					comments.clear();
					continue;
				}
				//comment
			}
			comments.add(s);
		}
		if(!comments.isEmpty()) {
			if(data.isEmpty())header.addAll(comments);
			else
				footer.addAll(comments);
		}
		loaded=!data.isEmpty();
	}

	@Override
	public void reset() {
		super.reset();
		loaded=false;
	}
}
