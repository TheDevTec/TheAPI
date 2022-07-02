package me.devtec.shared.dataholder.loaders;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import me.devtec.shared.dataholder.loaders.constructor.DataValue;
import me.devtec.shared.json.Json;

public class JsonLoader extends EmptyLoader {

	@SuppressWarnings("unchecked")
	@Override
	public void load(String input) {
		this.reset();
		if (input == null
				|| !input.startsWith("[") && !input.endsWith("]") && !input.startsWith("{") && !input.endsWith("}"))
			return;
		try {
			Object read = Json.reader().read(input.replace(System.lineSeparator(), ""));
			if (read instanceof Map)
				for (Entry<Object, Object> keyed : ((Map<Object, Object>) read).entrySet())
					this.data.put(keyed.getKey() + "",
							DataValue.of(null, Json.reader().read(keyed.getValue().toString()), null));
			else
				for (Object o : (Collection<Object>) read)
					for (Entry<Object, Object> keyed : ((Map<Object, Object>) o).entrySet())
						this.data.put(keyed.getKey() + "",
								DataValue.of(null, Json.reader().read(keyed.getValue().toString()), null));
			this.loaded = true;
		} catch (Exception er) {
			this.reset();
		}
	}
}
