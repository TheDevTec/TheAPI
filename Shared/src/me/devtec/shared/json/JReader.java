package me.devtec.shared.json;

public interface JReader {
	//For complex objects
	public Object read(String json);

	//For lists or maps
	public Object simpleRead(String json);
}
