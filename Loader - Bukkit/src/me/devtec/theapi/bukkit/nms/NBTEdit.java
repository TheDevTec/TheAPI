package me.devtec.theapi.bukkit.nms;

import java.util.Set;

import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import me.devtec.theapi.bukkit.BukkitLoader;

public class NBTEdit {
	private Object nbt;

	public NBTEdit(Object nbt) {
		if (nbt instanceof Entity)
			this.nbt = BukkitLoader.getNmsProvider().getNBT((Entity) nbt);
		else if (nbt instanceof ItemStack)
			this.nbt = BukkitLoader.getNmsProvider().getNBT((ItemStack) nbt);
		else if (nbt instanceof String)
			this.nbt = BukkitLoader.getNmsProvider().parseNBT((String) nbt);
		else
			this.nbt = nbt;
		if (this.nbt == null)
			this.nbt = BukkitLoader.getNmsProvider().parseNBT("{}");
	}

	public NBTEdit(ItemStack stack) {
		this(BukkitLoader.getNmsProvider().getNBT(stack));
	}

	public Object getNBT() {
		return this.nbt;
	}

	public void set(String path, NBTEdit value) {
		BukkitLoader.getNmsProvider().setNBTBase(this.nbt, path, value.getNBT());
	}

	public void set(String path, Object value) {
		BukkitLoader.getNmsProvider().setNBTBase(this.nbt, path, value);
	}

	public void setString(String path, String value) {
		BukkitLoader.getNmsProvider().setString(this.nbt, path, value);
	}

	public boolean hasKey(String path) {
		return BukkitLoader.getNmsProvider().hasKey(this.nbt, path);
	}

	public void remove(String path) {
		BukkitLoader.getNmsProvider().removeKey(this.nbt, path);
	}

	public void setBoolean(String path, boolean value) {
		BukkitLoader.getNmsProvider().setBoolean(this.nbt, path, value);
	}

	public void setByte(String path, byte value) {
		BukkitLoader.getNmsProvider().setByte(this.nbt, path, value);
	}

	public void setByteArray(String path, byte[] value) {
		BukkitLoader.getNmsProvider().setByteArray(this.nbt, path, value);
	}

	public void setDouble(String path, double value) {
		BukkitLoader.getNmsProvider().setDouble(this.nbt, path, value);
	}

	public void setFloat(String path, float value) {
		BukkitLoader.getNmsProvider().setFloat(this.nbt, path, value);
	}

	public void setInt(String path, int value) {
		BukkitLoader.getNmsProvider().setInteger(this.nbt, path, value);
	}

	public void setIntArray(String path, int[] value) {
		BukkitLoader.getNmsProvider().setIntArray(this.nbt, path, value);
	}

	public void setLong(String path, long value) {
		BukkitLoader.getNmsProvider().setLong(this.nbt, path, value);
	}

	public void setShort(String path, short value) {
		BukkitLoader.getNmsProvider().setShort(this.nbt, path, value);
	}

	public Object get(String path) {
		return BukkitLoader.getNmsProvider().getNBTBase(this.nbt, path);
	}

	public Set<String> getKeys(String path) {
		return BukkitLoader.getNmsProvider().getKeys(this.nbt);
	}

	public String getString(String path) {
		return BukkitLoader.getNmsProvider().getString(this.nbt, path);
	}

	public boolean getBoolean(String path) {
		return BukkitLoader.getNmsProvider().getBoolean(this.nbt, path);
	}

	public byte getByte(String path) {
		return BukkitLoader.getNmsProvider().getByte(this.nbt, path);
	}

	public byte[] getByteArray(String path) {
		return BukkitLoader.getNmsProvider().getByteArray(this.nbt, path);
	}

	public double getDouble(String path) {
		return BukkitLoader.getNmsProvider().getDouble(this.nbt, path);
	}

	public float getFloat(String path) {
		return BukkitLoader.getNmsProvider().getFloat(this.nbt, path);
	}

	public int getInt(String path) {
		return BukkitLoader.getNmsProvider().getInteger(this.nbt, path);
	}

	public int[] getIntArray(String path) {
		return BukkitLoader.getNmsProvider().getIntArray(this.nbt, path);
	}

	public long getLong(String path) {
		return BukkitLoader.getNmsProvider().getLong(this.nbt, path);
	}

	public short getShort(String path) {
		return BukkitLoader.getNmsProvider().getShort(this.nbt, path);
	}
}
