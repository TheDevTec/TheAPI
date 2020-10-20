package me.DevTec.TheAPI.Utils;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import me.DevTec.TheAPI.Utils.Reflections.Ref;
import me.DevTec.TheAPI.Utils.TheAPIUtils.LoaderClass;

@SuppressWarnings("deprecation")
public class TheMaterial implements Cloneable {

	public TheMaterial(Object IBlockDataOrBlock) {
		ItemStack stack = (ItemStack)Ref.invoke(Ref.invoke(null, Ref.method(Ref.craft("util.CraftMagicNumbers"),"getMaterial", Ref.nms("IBlockData")), IBlockDataOrBlock), "toItemStack");
		m = stack==null?(Material)Ref.invokeNulled(Ref.method(Ref.craft("util.CraftMagicNumbers"),"getMaterial", Ref.nms("Block")), Ref.invoke(IBlockDataOrBlock, "getBlock")):stack.getType();
		this.data = stack==null?(int)Ref.invoke(Ref.invoke(IBlockDataOrBlock, "getBlock"), Ref.method(Ref.nms("Block"), "toLegacyData", Ref.nms("IBlockData")), IBlockDataOrBlock):stack.getData().getData();
		this.amount=stack==null?1:stack.getAmount();
	}
	
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
	
	public int getCombinedId() {
		return (int) Ref.invokeNulled(Ref.method(Ref.nms("Block"), "getCombinedId", Ref.nms("IBlockData")), getIBlockData());
	}
	
	public Object getIBlockData() {
		try {
			Object o = Ref.invokeNulled(Ref.method(Ref.nms("Block"), "getByCombinedId", int.class), (int)((m.getId()+(data>>4))));
			if(o==null)
				o = Ref.invokeNulled(Ref.method(Ref.nms("Block"), "getId", int.class), (int)m.getId());
			else
				o=Ref.invoke(Ref.invoke(o, "getBlock"), Ref.method(Ref.nms("Block"), "fromLegacyData", int.class), (int)data);
			return o;
		}catch(Exception err) {
			try {
			return Ref.invoke(Ref.getNulled(Ref.nms("Blocks"), m.name()),"getBlockData");
			}catch(Exception errr) {
			if(m!=null) {
				Map<?,?> materialToData = (Map<?, ?>) Ref.getNulled(Ref.craft("legacy.CraftLegacy"), "materialToData");
				Map<?,?> materialToBlock = (Map<?, ?>) Ref.getNulled(Ref.craft("legacy.CraftLegacy"), "materialToBlock");
				MaterialData materialData = toItemStack().getData();
				if(materialData!=null) {
			    Object converted = materialToData.getOrDefault(materialData, null);
			    if (converted != null)
			      return converted;
			    Object convertedBlock = materialToBlock.getOrDefault(materialData, null);
			    if (convertedBlock != null)
			    	return Ref.invoke(convertedBlock,"getBlockData");
				}}
			    return LoaderClass.plugin.air;
		}}
	}

	public void setType(Material material) {
		m = material;
	}

	public void setData(int data) {
		this.data = data;
	}
	
	public Object toNMSItemStack() {
		return Ref.invokeNulled(Ref.craft("inventory.CraftItemStack"), "asNMSCopy", toItemStack());
	}
	
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
	
	public TheMaterial clone() {
		try {
			return (TheMaterial) super.clone();
		} catch (Exception e) {
			return null;
		}
	}
}
