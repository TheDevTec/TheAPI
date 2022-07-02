package me.straikerinacz.theapi.demo.json;

import java.util.HashMap;
import java.util.Map;

import me.devtec.shared.json.Json;
import me.devtec.shared.json.modern.ModernJsonReader;
import me.devtec.shared.json.modern.ModernJsonWriter;
import me.devtec.shared.utility.PercentageList;

public class JsonExample {

	@SuppressWarnings("unchecked")
	public static void init() {
		Json.init(new ModernJsonReader(), new ModernJsonWriter());

		//Parsing map
		Map<String, String> obj = new HashMap<>();
		obj.put("text", "testing");
		System.out.println(obj);

		String text = Json.writer().write(obj);
		System.out.println(text);

		obj = (Map<String, String>)Json.reader().read(text);
		System.out.println(obj);

		//Parsing array
		String[] array = {"a", "b", "c"};
		System.out.println(array);

		text = Json.writer().write(array);
		System.out.println(text);

		array = (String[])Json.reader().read(text);
		System.out.println(array);

		//Parsing object
		PercentageList<String> percentageList = new PercentageList<>();
		percentageList.add("test", 15.5);
		System.out.println(percentageList);

		text = Json.writer().write(percentageList);
		System.out.println(text);

		percentageList = (PercentageList<String>)Json.reader().read(text);
		System.out.println(percentageList);
	}
}
