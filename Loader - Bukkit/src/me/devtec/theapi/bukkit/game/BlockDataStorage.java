package me.devtec.theapi.bukkit.game;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;

import me.devtec.shared.Ref;
import me.devtec.shared.json.Json;
import me.devtec.theapi.bukkit.BukkitLoader;

public class BlockDataStorage implements Cloneable {

	private Material type;
	private byte itemData;
	private String data;
	private String nbt;

	public static BlockDataStorage fromData(Object blockData) {
		return BukkitLoader.getNmsProvider().toMaterial(blockData);
	}

	@Deprecated
	public static BlockDataStorage fromData(Object blockData, byte data) {
		return BukkitLoader.getNmsProvider().toMaterial(blockData).setItemData(data);
	}

	public static BlockDataStorage fromItemStack(ItemStack stack) {
		return new BlockDataStorage(stack.getType(), stack.getData().getData());
	}

	public BlockDataStorage(Material material) {
		this(material, (byte) 0, null);
	}

	public BlockDataStorage(Material material, byte itemData) {
		this(material, itemData, null);
	}

	public BlockDataStorage(Material material, byte itemData, String data) {
		type = material;
		this.itemData = itemData;
		this.data = data == null ? "" : data;
	}

	public BlockDataStorage(Material material, byte itemData, String data, String nbt) {
		type = material;
		this.itemData = itemData;
		this.data = data == null ? "" : data;
		this.nbt = nbt;
	}

	public Material getType() {
		return type;
	}

	public byte getItemData() {
		return itemData;
	}

	public String getData() {
		return data;
	}

	public String getNBT() {
		return nbt;
	}

	public BlockDataStorage setType(Material material) {
		type = material;
		return this;
	}

	public BlockDataStorage setItemData(byte data) {
		itemData = data;
		return this;
	}

	public BlockDataStorage setData(String data) {
		this.data = data == null ? "" : data;
		return this;
	}

	public BlockDataStorage setNBT(String nbt) {
		this.nbt = nbt;
		return this;
	}

	public ItemStack toItemStack() {
		return BukkitLoader.getNmsProvider().toItemStack(this);
	}

	public int getCombinedId() {
		return BukkitLoader.getNmsProvider().getCombinedId(Ref.isNewerThan(7) ? getIBlockData() : getBlock());
	}

	public Object getIBlockData() {
		return BukkitLoader.getNmsProvider().toIBlockData(this);
	}

	public Object getBlock() {
		return BukkitLoader.getNmsProvider().toBlock(this);
	}

	@Override
	public String toString() {
		Map<String, Object> map = new HashMap<>();
		map.put("type", type.name());
		if (itemData != 0)
			map.put("itemData", itemData);
		if (!data.isEmpty())
			map.put("data", data);
		if (nbt != null && !nbt.isEmpty())
			map.put("nbt", nbt);
		return Json.writer().simpleWrite(map);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BlockDataStorage) {
			BlockDataStorage material = (BlockDataStorage) obj;
			return material.getData().equals(data) && material.getItemData() == itemData && material.getType() == type || material.getNBT() == null ? nbt == null : material.getNBT().equals(nbt);
		}
		if (obj instanceof BlockState) {
			BlockState blockState = (BlockState) obj;
			return type == blockState.getType() && itemData == blockState.getRawData();
		}
		if (obj.getClass().equals(Ref.getClass("org.bukkit.block.data.BlockData"))) {
			BlockData blockData = (BlockData) obj;
			String asString = blockData.getAsString();
			if (asString.contains("["))
				asString = asString.substring(asString.indexOf('[') - 1);
			else
				asString = "";
			return type == blockData.getMaterial() && asString.equals(data);
		}
		return false;
	}

	@Override
	public BlockDataStorage clone() {
		return new BlockDataStorage(type, itemData, data, nbt);
	}
}
