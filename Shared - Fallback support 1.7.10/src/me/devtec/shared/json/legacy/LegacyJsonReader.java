package me.devtec.shared.json.legacy;

import java.util.Collection;
import java.util.Map;

import me.devtec.shared.json.JReader;
import me.devtec.shared.json.JsonUtils;
import me.devtec.shared.utility.StringUtils;
import net.minecraft.util.com.google.gson.Gson;
import net.minecraft.util.com.google.gson.GsonBuilder;

public class LegacyJsonReader implements JReader {
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
			Map<?, ?> map = LegacyJsonReader.parser.fromJson(json, Map.class);
			if (map == null) {
				Collection<?> list = LegacyJsonReader.parser.fromJson(json, Collection.class);
				return list != null ? list : json;
			}
			return JsonUtils.read(map);
		} catch (Exception err) {
			try {
				Collection<?> list = LegacyJsonReader.parser.fromJson(json, Collection.class);
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
			read = LegacyJsonReader.parser.fromJson(json, Map.class);
			if (read == null)
				read = LegacyJsonReader.parser.fromJson(json, Collection.class);
		} catch (Exception er) {
			try {
				read = LegacyJsonReader.parser.fromJson(json, Collection.class);
			} catch (Exception err) {

			}
		}
		return read == null ? json : read;
	}

	@Override
	public String toString() {
		return "LegacyJsonReader";
	}
}