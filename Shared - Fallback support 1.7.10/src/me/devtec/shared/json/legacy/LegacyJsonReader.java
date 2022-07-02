package me.devtec.shared.json.legacy;

import me.devtec.shared.json.JReader;
import net.minecraft.util.com.google.gson.Gson;
import net.minecraft.util.com.google.gson.GsonBuilder;

public class LegacyJsonReader implements JReader {
	private static final Gson parser = new GsonBuilder().create();

	@Override
	public Object fromGson(String json, Class<?> clazz) {
		return LegacyJsonReader.parser.fromJson(json, clazz);
	}

	@Override
	public String toString() {
		return "LegacyJsonReader";
	}
}