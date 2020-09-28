package me.DevTec.TheAPI.Utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.DevTec.TheAPI.Utils.Reflections.Ref;

public class TheMaterial {

	@SuppressWarnings("deprecation")
	public TheMaterial(Object IBlockDataOrBlock) {
		ItemStack stack = (ItemStack)Ref.invoke(Ref.invoke(null, Ref.method(Ref.craft("util.CraftMagicNumbers"),"getMaterial", Ref.nms("IBlockData")), IBlockDataOrBlock), "toItemStack");
		if(stack==null)stack=(ItemStack)new ItemStack((Material)Ref.invokeNulled(Material.class, "getMaterial", (int)Ref.invoke(null, Ref.method(Ref.craft("util.CraftMagicNumbers"),"getId", Ref.nms("Block")), IBlockDataOrBlock)));
		m = stack.getType();
		this.data = stack.getData().getData();
		this.amount=stack.getAmount();
	}
	
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

	public TheMaterial(int id, int data) {
		this(Material.values()[id], data, 1);
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
	
	@SuppressWarnings("deprecation")
	public int getCombinedId() {
		try {
		return getType().getId()+(data<<12);
		}catch(Exception err) {
			return (int)Ref.invoke(Ref.get(null,Ref.field(Ref.nms("Block"), "REGISTRY_ID")), Ref.method(Ref.nms("RegistryID"), "getId", Ref.nms("IBlockData")), getIBlockData());
		}
	}
	
	public Object getIBlockData() {
		@SuppressWarnings("deprecation")
		Object item = Ref.invoke(null, Ref.method(Ref.nms("Block"), "getByCombinedId", int.class), getType().getId()+(data<<12));
		Object o = Ref.invoke(null, Ref.method(Ref.craft("util.CraftMagicNumbers"), "getBlock", Material.class), getType());
		if(item==null)item=Ref.invoke(o, Ref.method(Ref.nms("Block"), "fromLegacyData", byte.class),(byte)data);
		if(item==null) {
		o = Ref.invoke(null, Ref.method(Ref.craft("block.data.CraftBlockData"), "newData", Material.class, String.class), getType(), data+"");
		item=Ref.invoke(o, "getState");}
		return item;
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
}
