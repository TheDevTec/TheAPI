package me.devtec.theapi.utils;

import java.lang.reflect.Method;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.utils.reflections.Ref;
import me.devtec.theapi.utils.theapiutils.LoaderClass;

public class TheMaterial implements Cloneable {
	static Method mm;
	static Method mat;
	static final Method create;
	static final Class<?> block = Ref.nmsOrOld("world.level.block.Block","Block");
	static final Class<?> item = Ref.nmsOrOld("world.item.Item","Item");

	static {
		create = Ref.method(Ref.craft("inventory.CraftItemStack"), "asNewCraftStack", Ref.nmsOrOld("world.item.Item","Item"));
		if(!TheAPI.isNewVersion()) {
			mm=Ref.method(Ref.craft("util.CraftMagicNumbers"), "getId", Ref.nmsOrOld("world.level.block.Block","Block"));
			mat=Ref.method(Material.class, "getMaterial", int.class);
		}
	}

	public static TheMaterial fromData(Object blockData) {
		return new TheMaterial(blockData);
	}

	public static TheMaterial fromData(Object blockData, int data) {
		return new TheMaterial(blockData, data);
	}
	
	private TheMaterial(Object blockData, int data) { //1.7.10 support
		this.data=data;
		if(TheAPI.isNewVersion()) {
			if(block.isInstance(blockData)) {
				ItemStack stack = (ItemStack)Ref.invokeNulled(create, Ref.invoke(blockData,"getItem"));
				m = stack.getType();
				this.amount = stack.getAmount();
				return;
			}
			if(item.isInstance(blockData)) {
				ItemStack stack = (ItemStack)Ref.invokeNulled(create, blockData);
				m = stack.getType();
				this.amount = stack.getAmount();
				return;
			}
			ItemStack stack = (ItemStack)Ref.invokeNulled(create, Ref.invoke(Ref.invoke(blockData, "getBlock"),"getItem"));
			m = stack.getType();
			this.amount = stack.getAmount();
		}else {
			if(TheAPI.isOlderThan(8)) { //1.7.10
				if(item.isInstance(blockData)) {
					ItemStack stack = (ItemStack)Ref.invokeNulled(create, blockData);
					m = stack.getType();
					this.amount = stack.getAmount();
					return;
				}
				if(block.isInstance(blockData)) {
					int id = (int)Ref.invokeNulled(mm, blockData);
					m=(Material) Ref.invokeNulled(mat, id);
					amount=1;
					return;
				}
				m=Material.AIR;
				amount=1;
				return;
			}
			if(item.isInstance(blockData)) {
				ItemStack stack = (ItemStack)Ref.invokeNulled(create, blockData);
				m = stack.getType();
				this.data = stack.getData().getData();
				this.amount = stack.getAmount();
				return;
			}
			if(block.isInstance(blockData)) {
				int id = (int)Ref.invokeNulled(mm, blockData);
				m=(Material) Ref.invokeNulled(mat, id);
				amount=1;
				return;
			}
			int id = (int)Ref.invokeNulled(mm, Ref.invoke(blockData, "getBlock"));
			m=(Material) Ref.invokeNulled(mat, id);
			amount=1;
		}
	}

	private static final Method bb = Ref.method(Ref.craft("util.CraftMagicNumbers"), "getMaterial", Ref.nmsOrOld("world.level.block.Block","Block"));
	
	private TheMaterial(Object blockData) {
		if(blockData==null)return;
		if(TheAPI.isNewVersion()) { //1.13+
			if(block.isInstance(blockData)) {
				ItemStack stack = new ItemStack((Material)Ref.invokeNulled(bb, blockData));
				m = stack.getType();
				this.data = stack.getData().getData();
				this.amount = stack.getAmount();
				return;
			}
			if(item.isInstance(blockData)) {
				ItemStack stack = (ItemStack)Ref.invokeNulled(create, blockData);
				m = stack.getType();
				this.data = stack.getData().getData();
				this.amount = stack.getAmount();
				return;
			}
			ItemStack stack = new ItemStack((Material)Ref.invokeNulled(bb, Ref.invoke(blockData,"getBlock")));
			m = stack.getType();
			this.data = stack.getData().getData();
			this.amount = stack.getAmount();
		}else { //1.7 - 1.12.2
			if(block.isInstance(blockData)) {
				ItemStack stack = new ItemStack((Material)Ref.invokeNulled(bb, blockData));
				m = stack.getType();
				this.data = stack.getData().getData();
				if(TheAPI.isNewerThan(7))
				try {
					data=(byte)Ref.invoke(blockData, "toLegacyData",Ref.invoke(blockData, "getBlockData"));
				}catch(Exception | NoSuchMethodError outDated) {
				}
				this.amount = stack.getAmount();
				return;
			}
			if(item.isInstance(blockData)) {
				ItemStack stack = (ItemStack)Ref.invokeNulled(create, blockData);
				m = stack.getType();
				this.data = stack.getData().getData();
				this.amount = stack.getAmount();
				return;
			}
			ItemStack stack = new ItemStack((Material)Ref.invokeNulled(bb, Ref.invoke(blockData,"getBlock")));
			m = stack.getType();
			if(TheAPI.isNewerThan(7))
			try {
				data=(byte)Ref.invoke(Ref.invoke(blockData,"getBlock"), "toLegacyData",blockData);
			}catch(Exception | NoSuchMethodError outDated) {
			}
			this.amount = stack.getAmount();
		}
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

	private Material m;
	private int data, amount;

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getData() {
		return data;
	}

	public Material getType() {
		return m;
	}

	public int getCombinedId() {
		return (int) Ref.invokeNulled(Ref.method(Ref.nmsOrOld("world.level.block.Block","Block"), "getCombinedId", Ref.nmsOrOld("world.level.block.state.IBlockData","IBlockData")),
				getIBlockData());
	}

	private static final Method fromLegacy;
	private static Method id = Ref.method(Ref.nmsOrOld("world.level.block.Block","Block"), "getByCombinedId", int.class);
	static {
		if(id==null)id=Ref.method(Ref.nmsOrOld("world.level.block.Block","Block"), "getById", int.class);
		fromLegacy=Ref.method(Ref.nmsOrOld("world.level.block.Block","Block"), "fromLegacyData", int.class);
	}
	
	public Object getIBlockData() {
		if(TheAPI.isNewerThan(12))
			return Ref.get(Bukkit.createBlockData(m), "state");
		try {
			if(TheAPI.isOlderThan(8)) {
				return Ref.invokeNulled(id, (int) m.getId());
			}
			Object o = Ref.invokeNulled(id,
					(int) ((m.getId() + (data >> 4))));
			if (o == null)
				o = Ref.invokeNulled(id, (int) m.getId());
			else
				o = Ref.invoke(Ref.invoke(o, "getBlock"), fromLegacy, (int) data);
			return o;
		} catch (Exception err) {
			if (m != null) {
				Map<?, ?> materialToData = (Map<?, ?>) Ref.getNulled(Ref.craft("legacy.CraftLegacy"),
						"materialToData");
				Map<?, ?> materialToBlock = (Map<?, ?>) Ref.getNulled(Ref.craft("legacy.CraftLegacy"),
						"materialToBlock");
				MaterialData materialData = toItemStack().getData();
				if (materialData != null) {
					Object converted = materialToData.get(materialData);
					if (converted != null)
						return converted;
					Object convertedBlock = materialToBlock.get(materialData);
					if (convertedBlock != null)
						return Ref.invoke(convertedBlock, "getBlockData");
				}
			}
			return LoaderClass.air;
		}
	}

	public void setType(Material material) {
		m = material;
	}

	public void setData(int data) {
		this.data = data;
	}

	public Object toNMSItemStack() {
		return LoaderClass.nmsProvider.asNMSItem(toItemStack());
	}

	public ItemStack toItemStack() {
		return new ItemStack(m, amount, (byte) data);
	}

	@Override
	public String toString() {
		return "[TheMaterial:" + m +'/' + data + '/' + amount + ']';
	}

	public static TheMaterial fromString(String stored) {
		try {
			stored = stored.substring(13, stored.length() - 1);
			String[] s = stored.split("/");
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

	private static final Class<?> bab = Ref.nmsOrOld("world.level.block.Block", "Block");
	private static final Method getBlock = Ref.method(Ref.nmsOrOld("world.level.block.state.IBlockData", "IBlockData"), "getBlock");
	public Object getBlock() {
		Object ib = getIBlockData();
		if(bab.isInstance(ib))return ib;
		return Ref.invoke(ib, getBlock);
	}
}
