package me.devtec.shared.json.legacy;

import me.devtec.shared.json.JWriter;
import me.devtec.shared.json.modern.ModernJsonWriter;
import net.minecraft.util.com.google.gson.Gson;
import net.minecraft.util.com.google.gson.GsonBuilder;

public class LegacyJsonWriter implements JWriter {
	private static final Gson parser = new GsonBuilder().create();

	@Override
	public Object writeWithoutParse(Object s) {
		return ModernJsonWriter.writeWithoutParseStatic(s);
	}

	@Override
	public String write(Object s) {
		try {
			Object parsed = writeWithoutParse(s);
			if (parsed == null)
				return "null";
			return LegacyJsonWriter.parser.toJson(parsed);
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
		return LegacyJsonWriter.parser.toJson(s);
	}

	@Override
	public String toString() {
		return "LegacyJsonWriter";
	}
}
