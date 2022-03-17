package me.devtec.shared.dataholder.loaders;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import me.devtec.shared.json.Json;

public class JsonLoader extends EmptyLoader {

	public void reset() {
		super.reset();
		loaded=false;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void load(String input) {
		reset();
		if (input == null)
			return;
		try {
			Object read = Json.reader().read(input.replace(System.lineSeparator(), ""));
			if (read instanceof Map) {
				for (Entry<Object, Object> keyed : ((Map<Object, Object>) read).entrySet()) {
					data.put((String) keyed.getKey(), new Object[] {Json.reader().read(keyed.getValue().toString()), null});
				}
			} else {
				for (Object o : (Collection<Object>) read) {
					for (Entry<Object, Object> keyed : ((Map<Object, Object>) o).entrySet()) {
						data.put((String) keyed.getKey(), new Object[] {Json.reader().read(keyed.getValue().toString()), null});
					}
				}
			}
			loaded = true;
		} catch (Exception er) {
			reset();
		}
	}
}
