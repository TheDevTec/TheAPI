package me.devtec.shared.dataholder.loaders.constructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.devtec.shared.json.Json;

public class DataValue {
	public Object value;
	public String writtenValue;

	public String commentAfterValue;
	public List<String> comments;

	public static DataValue of(String writtenValue, Object value, String commentAfterValue, List<String> comments) {
		DataValue data = new DataValue();
		data.value=value;
		data.writtenValue=writtenValue;
		data.commentAfterValue=commentAfterValue;
		data.comments=comments;
		return data;
	}

	public static DataValue of(String writtenValue, Object value, String commentAfterValue) {
		DataValue data = new DataValue();
		data.value=value;
		data.writtenValue=writtenValue;
		data.commentAfterValue=commentAfterValue;
		return data;
	}

	public static DataValue of(String writtenValue, Object value) {
		DataValue data = new DataValue();
		data.value=value;
		data.writtenValue=writtenValue;
		return data;
	}

	public static DataValue of(Object value) {
		DataValue data = new DataValue();
		data.value=value;
		return data;
	}

	public static DataValue empty() {
		return new DataValue();
	}

	private DataValue() {

	}

	@Override
	public String toString() {
		Map<String, Object> values = new HashMap<>();
		values.put("value", value+"");
		if(writtenValue!=null)
			values.put("writtenValue", writtenValue);
		if(commentAfterValue!=null)
			values.put("commentAfterValue", commentAfterValue);
		if(comments!=null)
			values.put("comments", comments);
		return Json.writer().simpleWrite(values);
	}
}
