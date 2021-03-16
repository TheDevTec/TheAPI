package me.devtec.theapi.utils.nms.nbt;

import java.lang.reflect.Method;
import java.util.Set;

import org.bukkit.inventory.ItemStack;

import me.devtec.theapi.utils.nms.NMSAPI;
import me.devtec.theapi.utils.reflections.Ref;

public class NBTEdit {
	//setter
	private static Method set=Ref.method(Ref.nms("NBTTagCompound"), "set", String.class, Ref.nms("NBTBase")),
			setString=Ref.method(Ref.nms("NBTTagCompound"), "setString", String.class, String.class),
			setBoolean=Ref.method(Ref.nms("NBTTagCompound"), "setBoolean", String.class, boolean.class),
			setByte=Ref.method(Ref.nms("NBTTagCompound"), "setByte", String.class, byte.class),
			setByteArray=Ref.method(Ref.nms("NBTTagCompound"), "setString", String.class, byte[].class),
			setDouble=Ref.method(Ref.nms("NBTTagCompound"), "setDouble", String.class, double.class),
			setFloat=Ref.method(Ref.nms("NBTTagCompound"), "setFloat", String.class, float.class),
			setInt=Ref.method(Ref.nms("NBTTagCompound"), "setInt", String.class, int.class),
			setIntArray=Ref.method(Ref.nms("NBTTagCompound"), "setIntArray", String.class, int[].class),
			setLong=Ref.method(Ref.nms("NBTTagCompound"), "setLong", String.class, long.class),
			setShort=Ref.method(Ref.nms("NBTTagCompound"), "setShort", String.class, short.class);
	//getter
	private static Method get=Ref.method(Ref.nms("NBTTagCompound"), "get", String.class), //NBTEdit or other value
			getKeys=Ref.method(Ref.nms("NBTTagCompound"), "getKeys"), //Set<String> of keys
			getTypeId=Ref.method(Ref.nms("NBTTagCompound"), "getTypeId"), //byte
			getCompound=Ref.method(Ref.nms("NBTTagCompound"), "getCompound", String.class), //NBTEdit
			getList=Ref.method(Ref.nms("NBTTagCompound"), "getList", String.class, int.class),
			getString=Ref.method(Ref.nms("NBTTagCompound"), "getString", String.class),
			getBoolean=Ref.method(Ref.nms("NBTTagCompound"), "getBoolean", String.class),
			getByte=Ref.method(Ref.nms("NBTTagCompound"), "getByte", String.class),
			getByteArray=Ref.method(Ref.nms("NBTTagCompound"), "getString", String.class),
			getDouble=Ref.method(Ref.nms("NBTTagCompound"), "getDouble", String.class),
			getFloat=Ref.method(Ref.nms("NBTTagCompound"), "getFloat", String.class),
			getInt=Ref.method(Ref.nms("NBTTagCompound"), "getInt", String.class),
			getIntArray=Ref.method(Ref.nms("NBTTagCompound"), "getIntArray", String.class),
			getLong=Ref.method(Ref.nms("NBTTagCompound"), "getLong", String.class),
			getShort=Ref.method(Ref.nms("NBTTagCompound"), "getShort", String.class);
	
	private Object nbt;
    public NBTEdit(Object nbt) {
        if(nbt instanceof ItemStack)
            this.nbt=NMSAPI.getNBT((ItemStack)nbt);
        else

        if(nbt instanceof String)
            this.nbt=NMSAPI.getNBT((String)nbt);
        else
            this.nbt=nbt;
    }
	
	public NBTEdit(ItemStack stack) {
		this(NMSAPI.getNBT(stack));
	}
	
	public Object getNBT() {
		return nbt;
	}
	
	public void set(String path, NBTEdit edit) {
		Ref.invoke(nbt, set, path, edit.getNBT());
	}
	
	public void set(String path, Object nbt) {
		Ref.invoke(nbt, set, path, nbt);
	}
	
	public void setString(String path, String value) {
		Ref.invoke(nbt, setString, path, value);
	}
	
	public void setBoolean(String path, boolean value) {
		Ref.invoke(nbt, setBoolean, path, value);
	}
	
	public void setByte(String path, byte value) {
		Ref.invoke(nbt, setByte, path, value);
	}
	
	public void setByteArray(String path, byte[] value) {
		Ref.invoke(nbt, setByteArray, path, value);
	}
	
	public void setDouble(String path, double value) {
		Ref.invoke(nbt, setDouble, path, value);
	}
	
	public void setFloat(String path, float value) {
		Ref.invoke(nbt, setFloat, path, value);
	}
	
	public void setInt(String path, int value) {
		Ref.invoke(nbt, setInt, path, value);
	}
	
	public void setIntArray(String path, int[] value) {
		Ref.invoke(nbt, setIntArray, path, value);
	}
	
	public void setLong(String path, long value) {
		Ref.invoke(nbt, setLong, path, value);
	}
	
	public void setShort(String path, short value) {
		Ref.invoke(nbt, setShort, path, value);
	}
	
	public Object get(String path) {
		return Ref.invoke(nbt, get, path);
	}
	
	public Object getList(String path, int size) {
		return Ref.invoke(nbt, getList, path, size);
	}
	
	public Object getCompound(String path) {
		return Ref.invoke(nbt, getCompound, path);
	}
	
	public byte getTypeId(String path) {
		return (byte)Ref.invoke(nbt, getTypeId, path);
	}
	
	@SuppressWarnings("unchecked")
	public Set<String> getKeys(String path) {
		return (Set<String>) Ref.invoke(nbt, getKeys, path);
	}
	
	public String getString(String path) {
		return (String)Ref.invoke(nbt, getString, path);
	}
	
	public boolean getBoolean(String path) {
		return (boolean)Ref.invoke(nbt, getBoolean, path);
	}
	
	public byte getByte(String path) {
		return (byte)Ref.invoke(nbt, getByte, path);
	}
	
	public byte[] getByteArray(String path) {
		return (byte[])Ref.invoke(nbt, getByteArray, path);
	}
	
	public double getDouble(String path) {
		return (double)Ref.invoke(nbt, getDouble, path);
	}
	
	public float getFloat(String path) {
		return (float)Ref.invoke(nbt, getFloat, path);
	}
	
	public int getInt(String path) {
		return (int)Ref.invoke(nbt, getInt, path);
	}
	
	public int[] getIntArray(String path) {
		return (int[])Ref.invoke(nbt, getIntArray, path);
	}
	
	public long getLong(String path) {
		return (long)Ref.invoke(nbt, getLong, path);
	}
	
	public short getShort(String path) {
		return (short)Ref.invoke(nbt, getShort, path);
	}
}
