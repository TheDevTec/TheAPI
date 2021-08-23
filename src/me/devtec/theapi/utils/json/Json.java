package me.devtec.theapi.utils.json;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.utils.json.instances.JReader;
import me.devtec.theapi.utils.json.instances.JWriter;
import me.devtec.theapi.utils.json.instances.legacy.LegacyJsonReader;
import me.devtec.theapi.utils.json.instances.legacy.LegacyJsonWriter;
import me.devtec.theapi.utils.json.instances.modern.ModernJsonReader;
import me.devtec.theapi.utils.json.instances.modern.ModernJsonWriter;

public class Json {
	private static JReader reader;
	private static JWriter writer;
	static {
		//load default json reader & writer built in code
		if(TheAPI.isOlderThan(8)) {
			reader=new LegacyJsonReader();
			writer=new LegacyJsonWriter();
		}else {
			reader=new ModernJsonReader();
			writer=new ModernJsonWriter();
		}
	}
	
	public static JReader reader() {
		return reader;
	}
	
	public static JWriter writer() {
		return writer;
	}
	
	public static JReader setReader(JReader reader) {
		return Json.reader=reader;
	}
	
	public static JWriter setWriter(JWriter writer) {
		return Json.writer=writer;
	}
}
