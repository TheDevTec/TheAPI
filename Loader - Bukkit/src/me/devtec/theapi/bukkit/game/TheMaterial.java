package me.devtec.theapi.bukkit.game;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.devtec.shared.Ref;
import me.devtec.shared.utility.StringUtils;
import me.devtec.theapi.bukkit.BukkitLoader;

public class TheMaterial implements Cloneable {

	private Material m;
	private int data;
	private int amount;

	public static TheMaterial fromData(Object blockData) {
		return BukkitLoader.getNmsProvider().toMaterial(blockData);
	}

	public static TheMaterial fromData(Object blockData, int data) {
		return BukkitLoader.getNmsProvider().toMaterial(blockData).setData(data);
	}
	
	public TheMaterial(ItemStack stack) {
		this(stack.getType(), stack.getData().getData(), stack.getAmount());
	}

	public TheMaterial(String name, String data) {
		this(Material.getMaterial(name), StringUtils.getInt(data), 1);
	}

	public TheMaterial(String name, int data) {
		this(Material.getMaterial(name), data, 1);
	}

	public TheMaterial(Material material, int data) {
		this(material, data, 1);
	}

	public TheMaterial(Material material, String data) {
		this(material, StringUtils.getInt(data), 1);
	}

	public TheMaterial(String name) {
		this(Material.getMaterial(name));
	}

	public TheMaterial(Material material) {
		this(material, 0, 1);
	}

	public TheMaterial(String name, String data, int amount) {
		this(Material.getMaterial(name), StringUtils.getInt(data), amount);
	}

	public TheMaterial(String name, int data, int amount) {
		this(Material.getMaterial(name), data, amount);
	}

	public TheMaterial(Material material, int data, int amount) {
		m = material;
		this.data = data;
		this.amount = amount;
	}

	public TheMaterial(Material material, String data, String amount) {
		this(material, StringUtils.getInt(data), StringUtils.getInt(amount));
	}

	public TheMaterial(String name, String data, String amount) {
		this(Material.getMaterial(name), StringUtils.getInt(data), StringUtils.getInt(amount));
	}

	public TheMaterial(String name, int data, String amount) {
		this(Material.getMaterial(name), data, amount);
	}

	public TheMaterial(Material material, int data, String amount) {
		this(material, data, StringUtils.getInt(amount));
	}

	public TheMaterial(Material material, String data, int amount) {
		this(material, StringUtils.getInt(data), amount);
	}

	public TheMaterial(int id, int data) {
		this(Material.values()[id], data, 1);
	}

	public int getAmount() {
		return amount;
	}

	public int getData() {
		return data;
	}

	public Material getType() {
		return m;
	}
	public TheMaterial setType(Material material) {
		m = material;
		return this;
	}

	public TheMaterial setData(int data) {
		this.data = data;
		return this;
	}

	public TheMaterial setAmount(int amount) {
		this.amount = amount;
		return this;
	}

	public int getCombinedId() {
		return BukkitLoader.getNmsProvider().getCombinedId(Ref.isNewerThan(7)?getIBlockData():getBlock());
	}
	
	public Object getIBlockData() {
		return BukkitLoader.getNmsProvider().toIBlockData(this);
	}


	public Object toNMSItemStack() {
		return BukkitLoader.getNmsProvider().asNMSItem(toItemStack());
	}

	public Object getBlock() {
		return BukkitLoader.getNmsProvider().toBlock(this);
	}

	public ItemStack toItemStack() {
		return new ItemStack(m, amount, (short) 0, (byte) data);
	}

	@Override
	public String toString() {
		return "[TheMaterial:" + m +'/' + data + '/' + amount + ']';
	}

	public static TheMaterial fromString(String stored) {
		try {
			String[] s = stored.substring(13, stored.length() - 1).split("/");
			try {
				return new TheMaterial(s[0], s[1], s[2]);
			} catch (Exception | NoSuchMethodError old) {
				return new TheMaterial(s[0], s[1], 1);
			}
		} catch (Exception notMat) {
			return null;
		}
	}

	public static TheMaterial fromItemStack(ItemStack stack) {
		return new TheMaterial(stack);
	}

	public static TheMaterial fromMaterial(Material material, int amount, int data) {
		return new TheMaterial(material, amount, data);
	}

	public static TheMaterial fromMaterial(Material material, int amount) {
		return new TheMaterial(material, amount);
	}

	public static TheMaterial fromMaterial(Material material) {
		return new TheMaterial(material);
	}

	@Override
	public boolean equals(Object a) {
		if (a instanceof TheMaterial) {
			TheMaterial material = (TheMaterial) a;
			return material.getData() == data && material.getType() == m;
		}
		if (a instanceof Material) {
			return m == (Material) a;
		}
		return false;
	}

	public TheMaterial clone() {
		return new TheMaterial(m, data, amount);
	}
}
