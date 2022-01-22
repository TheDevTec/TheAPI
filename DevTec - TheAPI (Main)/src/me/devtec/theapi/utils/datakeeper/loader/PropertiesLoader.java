package me.devtec.theapi.utils.datakeeper.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.devtec.theapi.utils.json.Json;

public class PropertiesLoader extends EmptyLoader {
	private final Pattern pattern = Pattern.compile("(.*?)=(.*)");
	
	public void load(File file) {
		reset();
		if(file==null)return;
		List<String> comments = new LinkedList<>();
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8), 8192);
			String s;
			while((s=r.readLine())!=null) {
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
			r.close();
			if(!comments.isEmpty()) {
				if(data.isEmpty())header.addAll(comments);
				else
					footer.addAll(comments);
			}
			loaded=!data.isEmpty();
		} catch (Exception e) {
			loaded=false;
		}
	}
	
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
