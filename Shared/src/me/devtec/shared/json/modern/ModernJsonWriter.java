package me.devtec.shared.json.modern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import me.devtec.shared.json.JWriter;
import me.devtec.shared.json.JsonUtils;

public class ModernJsonWriter implements JWriter {
	private static final Gson parser = new GsonBuilder().create();

	@Override
	public Object writeWithoutParse(Object s) {
		return JsonUtils.writeWithoutParseStatic(s);
	}

	@Override
	public String write(Object s) {
		try {
			Object parsed = writeWithoutParse(s);
			if (parsed == null)
				return "null";
			return ModernJsonWriter.parser.toJson(parsed);
		} catch (Exception err) {
		}
		return null;
	}

	@Override
	public String simpleWrite(Object s) {
		if (s == null)
			return "null";
		if (s instanceof String || s instanceof CharSequence || s instanceof Boolean || s instanceof Number
				|| s instanceof Character)
			return s.toString();
		return ModernJsonWriter.parser.toJson(s);
	}

	@Override
	public String toString() {
		return "ModernJsonWriter";
	}
}
