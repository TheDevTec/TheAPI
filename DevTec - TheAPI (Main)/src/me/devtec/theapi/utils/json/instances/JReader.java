package me.devtec.theapi.utils.json.instances;

public interface JReader {
	//For complex objects
	public Object read(String json);

	//For lists or maps
	public Object simpleRead(String json);
}
