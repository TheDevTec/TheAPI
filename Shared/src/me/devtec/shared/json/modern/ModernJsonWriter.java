package me.devtec.shared.json.modern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import me.devtec.shared.json.JWriter;

public class ModernJsonWriter implements JWriter {
	private static final Gson parser = new GsonBuilder().create();

	@Override
	public String toGson(Object object) {
		return ModernJsonWriter.parser.toJson(object);
	}

	@Override
	public String toString() {
		return "ModernJsonWriter";
	}
}
