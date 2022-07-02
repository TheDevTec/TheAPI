package me.devtec.shared.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Json {

	private static List<DataReader> readers = new ArrayList<>();
	private static List<DataWriter> writers = new ArrayList<>();

	private static JReader reader;
	private static JWriter writer;

	public static void init(JReader reader, JWriter writer) {
		Json.reader = reader;
		Json.writer = writer;
	}

	public static Object processDataReaders(Map<String, Object> map) {
		Object result = null;
		for (DataReader reader : Json.readers)
			if (reader.isAllowed(map)) {
				result = reader.read(map);
				if (result != null)
					return result;
			}
		return result;
	}

	public static Map<String, Object> processDataWriters(Object obj) {
		Map<String, Object> result = null;
		for (DataWriter reader : Json.writers)
			if (reader.isAllowed(obj)) {
				result = reader.write(obj);
				if (result != null && !result.isEmpty())
					return result;
			}
		return result;
	}

	public static JReader reader() {
		return Json.reader;
	}

	public static JWriter writer() {
		return Json.writer;
	}

	public static JReader setReader(JReader reader) {
		return Json.reader = reader;
	}

	public static JWriter setWriter(JWriter writer) {
		return Json.writer = writer;
	}

	public static void registerDataReader(DataReader reader) {
		Json.readers.add(reader);
	}

	public static void unregisterDataReader(DataReader reader) {
		Json.readers.remove(reader);
	}

	public static void registerDataWriter(DataWriter writer) {
		Json.writers.add(writer);
	}

	public static void unregisterDataWriter(DataWriter writer) {
		Json.writers.remove(writer);
	}

	public static interface DataReader {
		public boolean isAllowed(Map<String, Object> map);

		public Object read(Map<String, Object> map);
	}

	public static interface DataWriter {
		public boolean isAllowed(Object object);

		public Map<String, Object> write(Object object);
	}
}
