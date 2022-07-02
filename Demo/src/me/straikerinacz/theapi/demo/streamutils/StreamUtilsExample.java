package me.straikerinacz.theapi.demo.streamutils;

import java.io.File;

import me.devtec.shared.utility.StreamUtils;

public class StreamUtilsExample {
	public static void init() {
		//Read text from File
		String fileText = StreamUtils.fromStream(new File("server.properties"));
		System.out.println(fileText);

		//Read text from InputStream resource
		String resourceText = StreamUtils.fromStream(StreamUtilsExample.class.getClassLoader().getResourceAsStream("config.yml"));
		System.out.println(resourceText);
	}
}
