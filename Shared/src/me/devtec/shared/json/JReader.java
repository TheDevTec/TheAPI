package me.devtec.shared.json;

import java.util.Collection;
import java.util.Map;

import me.devtec.shared.utility.StringUtils;

public interface JReader {
	// For complex objects
	public default Object read(String json) {
		if (json == null || json.trim().isEmpty())
			return json;
		Object simpleRead = simpleRead(json);
		if (simpleRead instanceof Map)
			return JsonUtils.read(simpleRead);
		return simpleRead;
	}

	// For lists or maps
	public default Object simpleRead(String json) {
		if (json == null || json.trim().isEmpty())
			return json;
		if (json.equals("null"))
			return null;
		if (json.equalsIgnoreCase("true"))
			return true;
		if (json.equalsIgnoreCase("false"))
			return false;
		if (StringUtils.isNumber(json))
			return StringUtils.getNumber(json);
		Object read = null;
		try {
			read = fromGson(json, Map.class);
		} catch (Exception er) {
		}
		if (read == null)
			try {
				read = fromGson(json, Collection.class);
			} catch (Exception err) {

			}
		return read == null ? json : read;
	}

	public Object fromGson(String json, Class<?> clazz);
}
