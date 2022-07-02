package me.devtec.shared.json.modern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import me.devtec.shared.json.JReader;

public class ModernJsonReader implements JReader {
	private static final Gson parser = new GsonBuilder().create();

	@Override
	public Object fromGson(String json, Class<?> clazz) {
		return ModernJsonReader.parser.fromJson(json, clazz);
	}

	@Override
	public String toString() {
		return "ModernJsonReader";
	}
}