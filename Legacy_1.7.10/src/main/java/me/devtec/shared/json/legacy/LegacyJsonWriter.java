package me.devtec.shared.json.legacy;

import me.devtec.shared.json.JWriter;
import net.minecraft.util.com.google.gson.Gson;
import net.minecraft.util.com.google.gson.GsonBuilder;

public class LegacyJsonWriter implements JWriter {
	private static final Gson parser = new GsonBuilder().create();

	@Override
	public String toGson(Object object) {
		return LegacyJsonWriter.parser.toJson(object);
	}

	@Override
	public String toString() {
		return "LegacyJsonWriter";
	}
}
