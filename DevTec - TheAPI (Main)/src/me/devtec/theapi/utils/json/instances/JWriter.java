package me.devtec.theapi.utils.json.instances;

public interface JWriter {
	//For complex objects
	public String write(Object object);
	
	public Object writeWithoutParse(Object object);

	//For lists or maps
	public String simpleWrite(Object object);
}
