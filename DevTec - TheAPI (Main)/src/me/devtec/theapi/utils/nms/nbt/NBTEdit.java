package me.devtec.theapi.utils.nms.nbt;

import java.util.Set;

import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import me.devtec.theapi.utils.theapiutils.LoaderClass;

public class NBTEdit {
	private Object nbt;
    public NBTEdit(Object nbt) {
        if(nbt instanceof Entity)
            this.nbt=LoaderClass.nmsProvider.getNBT((Entity)nbt);
        else
        if(nbt instanceof ItemStack)
            this.nbt=LoaderClass.nmsProvider.getNBT((ItemStack)nbt);
        else
        if(nbt instanceof String)
            this.nbt=LoaderClass.nmsProvider.parseNBT((String)nbt);
        else
            this.nbt=nbt;
        if(this.nbt==null)
        	this.nbt=LoaderClass.nmsProvider.parseNBT("{}");
    }
	
	public NBTEdit(ItemStack stack) {
		this(LoaderClass.nmsProvider.getNBT(stack));
	}
	
	public Object getNBT() {
		return nbt;
	}
	
	public void set(String path, NBTEdit value) {
		LoaderClass.nmsProvider.setNBTBase(nbt, path, value.getNBT());
	}
	
	public void set(String path, Object value) {
		LoaderClass.nmsProvider.setNBTBase(nbt, path, value);
	}
	
	public void setString(String path, String value) {
		LoaderClass.nmsProvider.setString(nbt, path, value);
	}
	
	public boolean hasKey(String path) {
		return LoaderClass.nmsProvider.hasKey(nbt, path);
	}
	
	public void remove(String path) {
		LoaderClass.nmsProvider.removeKey(nbt, path);
	}
	
	public void setBoolean(String path, boolean value) {
		LoaderClass.nmsProvider.setBoolean(nbt, path, value);
	}
	
	public void setByte(String path, byte value) {
		LoaderClass.nmsProvider.setByte(nbt, path, value);
	}
	
	public void setByteArray(String path, byte[] value) {
		LoaderClass.nmsProvider.setByteArray(nbt, path, value);
	}
	
	public void setDouble(String path, double value) {
		LoaderClass.nmsProvider.setDouble(nbt, path, value);
	}
	
	public void setFloat(String path, float value) {
		LoaderClass.nmsProvider.setFloat(nbt, path, value);
	}
	
	public void setInt(String path, int value) {
		LoaderClass.nmsProvider.setInteger(nbt, path, value);
	}
	
	public void setIntArray(String path, int[] value) {
		LoaderClass.nmsProvider.setIntArray(nbt, path, value);
	}
	
	public void setLong(String path, long value) {
		LoaderClass.nmsProvider.setLong(nbt, path, value);
	}
	
	public void setShort(String path, short value) {
		LoaderClass.nmsProvider.setShort(nbt, path, value);
	}
	
	public Object get(String path) {
		return LoaderClass.nmsProvider.getNBTBase(nbt, path);
	}
	
	public Set<String> getKeys(String path) {
		return LoaderClass.nmsProvider.getKeys(nbt);
	}
	
	public String getString(String path) {
		return LoaderClass.nmsProvider.getString(nbt, path);
	}
	
	public boolean getBoolean(String path) {
		return LoaderClass.nmsProvider.getBoolean(nbt, path);
	}
	
	public byte getByte(String path) {
		return LoaderClass.nmsProvider.getByte(nbt, path);
	}
	
	public byte[] getByteArray(String path) {
		return LoaderClass.nmsProvider.getByteArray(nbt, path);
	}
	
	public double getDouble(String path) {
		return LoaderClass.nmsProvider.getDouble(nbt, path);
	}
	
	public float getFloat(String path) {
		return LoaderClass.nmsProvider.getFloat(nbt, path);
	}
	
	public int getInt(String path) {
		return LoaderClass.nmsProvider.getInteger(nbt, path);
	}
	
	public int[] getIntArray(String path) {
		return LoaderClass.nmsProvider.getIntArray(nbt, path);
	}
	
	public long getLong(String path) {
		return LoaderClass.nmsProvider.getLong(nbt, path);
	}
	
	public short getShort(String path) {
		return LoaderClass.nmsProvider.getShort(nbt, path);
	}
}
