package me.devtec.theapi.bukkit.nms;

import me.devtec.theapi.bukkit.BukkitLoader;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

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
		return nbt;
	}

	public void set(String path, NBTEdit value) {
		BukkitLoader.getNmsProvider().setNBTBase(nbt, path, value.getNBT());
	}

	public void set(String path, Object value) {
		BukkitLoader.getNmsProvider().setNBTBase(nbt, path, value);
	}

	public void setString(String path, String value) {
		BukkitLoader.getNmsProvider().setString(nbt, path, value);
	}

	public boolean hasKey(String path) {
		return BukkitLoader.getNmsProvider().hasKey(nbt, path);
	}

	public void remove(String path) {
		BukkitLoader.getNmsProvider().removeKey(nbt, path);
	}

	public void setBoolean(String path, boolean value) {
		BukkitLoader.getNmsProvider().setBoolean(nbt, path, value);
	}

	public void setByte(String path, byte value) {
		BukkitLoader.getNmsProvider().setByte(nbt, path, value);
	}

	public void setByteArray(String path, byte[] value) {
		BukkitLoader.getNmsProvider().setByteArray(nbt, path, value);
	}

	public void setDouble(String path, double value) {
		BukkitLoader.getNmsProvider().setDouble(nbt, path, value);
	}

	public void setFloat(String path, float value) {
		BukkitLoader.getNmsProvider().setFloat(nbt, path, value);
	}

	public void setInt(String path, int value) {
		BukkitLoader.getNmsProvider().setInteger(nbt, path, value);
	}

	public void setIntArray(String path, int[] value) {
		BukkitLoader.getNmsProvider().setIntArray(nbt, path, value);
	}

	public void setLong(String path, long value) {
		BukkitLoader.getNmsProvider().setLong(nbt, path, value);
	}

	public void setShort(String path, short value) {
		BukkitLoader.getNmsProvider().setShort(nbt, path, value);
	}

	public Object get(String path) {
		return BukkitLoader.getNmsProvider().getNBTBase(nbt, path);
	}

	public Set<String> getKeys() {
		return BukkitLoader.getNmsProvider().getKeys(nbt);
	}

	public String getString(String path) {
		return BukkitLoader.getNmsProvider().getString(nbt, path);
	}

	public boolean getBoolean(String path) {
		return BukkitLoader.getNmsProvider().getBoolean(nbt, path);
	}

	public byte getByte(String path) {
		return BukkitLoader.getNmsProvider().getByte(nbt, path);
	}

	public byte[] getByteArray(String path) {
		return BukkitLoader.getNmsProvider().getByteArray(nbt, path);
	}

	public double getDouble(String path) {
		return BukkitLoader.getNmsProvider().getDouble(nbt, path);
	}

	public float getFloat(String path) {
		return BukkitLoader.getNmsProvider().getFloat(nbt, path);
	}

	public int getInt(String path) {
		return BukkitLoader.getNmsProvider().getInteger(nbt, path);
	}

	public int[] getIntArray(String path) {
		return BukkitLoader.getNmsProvider().getIntArray(nbt, path);
	}

	public long getLong(String path) {
		return BukkitLoader.getNmsProvider().getLong(nbt, path);
	}

	public short getShort(String path) {
		return BukkitLoader.getNmsProvider().getShort(nbt, path);
	}
}
