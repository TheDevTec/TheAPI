package me.DevTec.Other;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TheMaterial {

	@SuppressWarnings("deprecation")
	public TheMaterial(ItemStack stack) {
		this(stack.getType(), stack.getData().getData(), stack.getAmount());
	}

	public TheMaterial(String name, String data) {
		this(Material.matchMaterial(name), StringUtils.getInt(data),1);
	}

	public TheMaterial(String name, int data) {
		this(Material.matchMaterial(name), data,1);
	}

	public TheMaterial(Material material, int data) {
		this(material,data,1);
	}

	public TheMaterial(Material material, String data) {
		this(material, StringUtils.getInt(data),1);
	}

	public TheMaterial(String name) {
		this(Material.matchMaterial(name));
	}

	public TheMaterial(Material material) {
		this(material,0,1);
	}

	public TheMaterial(String name, String data, int amount) {
		this(Material.matchMaterial(name), StringUtils.getInt(data),amount);
	}

	public TheMaterial(String name, int data, int amount) {
		this(Material.matchMaterial(name), data,amount);
	}

	public TheMaterial(Material material, int data, int amount) {
		m = material;
		this.data = data;
		this.amount=amount;
	}

	public TheMaterial(Material material, String data, String amount) {
		this(material, StringUtils.getInt(data),StringUtils.getInt(amount));
	}
	
	public TheMaterial(String name, String data, String amount) {
		this(Material.matchMaterial(name), StringUtils.getInt(data),StringUtils.getInt(amount));
	}

	public TheMaterial(String name, int data, String amount) {
		this(Material.matchMaterial(name), data,amount);
	}

	public TheMaterial(Material material, int data, String amount) {
		this(material,data,StringUtils.getInt(amount));
	}

	public TheMaterial(Material material, String data, int amount) {
		this(material, StringUtils.getInt(data),amount);
	}

	private Material m;
	private int data,amount;

	public int getAmount() {
		return amount;
	}
	
	public void setAmount(int amount) {
		this.amount=amount;
	}
	
	public int getData() {
		return data;
	}

	public Material getType() {
		return m;
	}

	public void setType(Material material) {
		m = material;
	}

	public void setData(int data) {
		this.data = data;
	}
	
	@SuppressWarnings("deprecation")
	public ItemStack toItemStack() {
		return new ItemStack(m, amount,(byte)data);
	}

	@Override
	public String toString() {
		return "[TheMaterial:" + m.name() + "/" + data + "/"+amount+"]";
	}

	public static TheMaterial fromString(String stored) {
		if (stored.startsWith("[TheMaterial:")) {
			stored = stored.substring(0, stored.length() - 1).replaceFirst("\\[TheMaterial:", "");
			String[] s = stored.split("/");
			try {
			return new TheMaterial(s[0], s[1], s[2]);
			}catch(Exception | NoSuchMethodError old) {
				return new TheMaterial(s[0], s[1], 1);
			}
		}
		return null;
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
}
