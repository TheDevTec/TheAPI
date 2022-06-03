package me.devtec.shared.json.modern;

import java.util.Collection;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import me.devtec.shared.json.JReader;
import me.devtec.shared.json.JsonUtils;
import me.devtec.shared.utility.StringUtils;

public class ModernJsonReader implements JReader {
	private static final Gson parser = new GsonBuilder().create();

	@Override
	public Object read(String json) {
		if (json == null || json.trim().isEmpty())
			return json;
		try {
			if (json.equals("null"))
				return null;
			if (json.equalsIgnoreCase("true"))
				return true;
			if (json.equalsIgnoreCase("false"))
				return false;
			if (StringUtils.isNumber(json))
				return StringUtils.getNumber(json);
			Map<?, ?> map = ModernJsonReader.parser.fromJson(json, Map.class);
			if (map == null) {
				Collection<?> list = ModernJsonReader.parser.fromJson(json, Collection.class);
				return list != null ? list : json;
			}
			return JsonUtils.read(map);
		} catch (Exception err) {
			try {
				Collection<?> list = ModernJsonReader.parser.fromJson(json, Collection.class);
				return list != null ? list : json;
			} catch (Exception er) {
			}
		}
		return json;
	}

	@Override
	public Object simpleRead(String json) {
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
			read = ModernJsonReader.parser.fromJson(json, Map.class);
			if (read == null)
				read = ModernJsonReader.parser.fromJson(json, Collection.class);
		} catch (Exception er) {
			try {
				read = ModernJsonReader.parser.fromJson(json, Collection.class);
			} catch (Exception err) {

			}
		}
		return read == null ? json : read;
	}

	@Override
	public String toString() {
		return "ModernJsonReader";
	}
}