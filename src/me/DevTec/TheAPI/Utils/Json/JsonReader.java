package me.DevTec.TheAPI.Utils.Json;

public interface JsonReader {
	public Object deserilize(java.io.Reader reader);
	
	public Object deserilize(String json);
}
