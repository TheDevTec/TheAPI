package me.devtec.theapi.blocksapi.schematic.construct;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.AnaloguePowerable;
import org.bukkit.block.data.Attachable;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.FaceAttachable;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.Lightable;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.Orientable;
import org.bukkit.block.data.Powerable;
import org.bukkit.block.data.Rail;
import org.bukkit.block.data.Rotatable;
import org.bukkit.block.data.Snowable;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.material.MaterialData;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.utils.Position;
import me.devtec.theapi.utils.TheMaterial;
import me.devtec.theapi.utils.json.Writer;
import me.devtec.theapi.utils.reflections.Ref;

public interface SerializedBlock extends Serializable {
	static Method get = Ref.getClass("com.google.common.collect.ForwardingMultimap")==null?Ref.method(Ref.getClass("net.minecraft.util.com.google.common.collect.ForwardingMultimap"), "get", Object.class):
			Ref.method(Ref.getClass("com.google.common.collect.ForwardingMultimap"), "get", Object.class);
	
	public default SerializedBlock serialize(Position block) {
		Block b = block.getBlock();
		Map<String, Object> values = new HashMap<>();
		if(TheAPI.isNewVersion()) {
			if(b.getBlockData() instanceof Orientable) {
				Orientable dir = (Orientable)b.getBlockData();
				values.put("orient", dir.getAxis().name());
			}
			if(b.getBlockData() instanceof Rotatable) {
				Rotatable dir = (Rotatable)b.getBlockData();
				values.put("rotate", dir.getRotation().name());
			}
			if(b.getBlockData() instanceof Directional) {
				Directional dir = (Directional)b.getBlockData();
				values.put("face", dir.getFacing().name());
			}
			if(b.getBlockData() instanceof Ageable) {
				Ageable dir = (Ageable)b.getBlockData();
				values.put("age", dir.getAge());
			}
			if(b.getBlockData() instanceof AnaloguePowerable) {
				AnaloguePowerable dir = (AnaloguePowerable)b.getBlockData();
				values.put("power", dir.getPower());
			}
			if(b.getBlockData() instanceof Attachable) {
				Attachable dir = (Attachable)b.getBlockData();
				values.put("attach", dir.isAttached());
			}
			if(b.getBlockData() instanceof Bisected) {
				Bisected dir = (Bisected)b.getBlockData();
				values.put("half", dir.getHalf().name());
			}
			if(b.getBlockData() instanceof FaceAttachable) {
				FaceAttachable dir = (FaceAttachable)b.getBlockData();
				values.put("fattach", dir.getAttachedFace().name());
			}
			if(b.getBlockData() instanceof Levelled) {
				Levelled dir = (Levelled)b.getBlockData();
				values.put("level", dir.getLevel());
			}
			if(b.getBlockData() instanceof Lightable) {
				Lightable dir = (Lightable)b.getBlockData();
				values.put("lit", dir.isLit());
			}
			if(TheAPI.isNewerThan(15)) {
				if(b.getBlockData() instanceof MultipleFacing) {
					MultipleFacing dir = (MultipleFacing)b.getBlockData();
					Map<String, Boolean> map = new HashMap<>(dir.getFaces().size());
					for(BlockFace face : dir.getFaces())
						if(dir.getAllowedFaces().contains(face))map.put(face.name(), true);
						else map.put(face.name(), false);
					values.put("mface", Writer.write(map));
				}
			}
			if(b.getBlockData() instanceof Openable) {
				Openable dir = (Openable)b.getBlockData();
				values.put("open", dir.isOpen());
			}
			if(b.getBlockData() instanceof Powerable) {
				Powerable dir = (Powerable)b.getBlockData();
				values.put("power", dir.isPowered());
			}
			if(b.getBlockData() instanceof Rail) {
				Rail  dir = (Rail)b.getBlockData();
				values.put("rail", dir.getShape().name());
			}
			if(b.getBlockData() instanceof Snowable) {
				Snowable dir = (Snowable)b.getBlockData();
				values.put("snow", dir.isSnowy());
			}
			if(b.getBlockData() instanceof Waterlogged) {
				Waterlogged dir = (Waterlogged)b.getBlockData();
				values.put("water", dir.isWaterlogged());
			}
		}else {
			MaterialData data = b.getState().getData();
			if(data instanceof org.bukkit.material.Colorable)
				values.put("color", ((org.bukkit.material.Colorable) data).getColor().name());
			if(data instanceof org.bukkit.material.Directional)
				values.put("face", ((org.bukkit.material.Directional) data).getFacing().name());
			if(data instanceof org.bukkit.material.Attachable)
				values.put("attach", ((org.bukkit.material.Attachable) data).getAttachedFace().name());
			if(data instanceof org.bukkit.material.Openable)
				values.put("open", ((org.bukkit.material.Openable) data).isOpen());
		}
		Object dir = getState(block);
		if(dir!=null) {
			Object ret = Ref.invoke(dir, "save", Ref.newInstance(Ref.constructor(Ref.nms("NBTTagCompound"))));
			if(ret==null) {
				ret = Ref.invoke(dir, "b", Ref.newInstance(Ref.constructor(Ref.nms("NBTTagCompound"))));
			}
			values.put("nbt", ret.toString());
		}
		return serialize(block.getType(), values);
	}
	
	static Object getState(Position block) {
		Object w = Ref.world(block.getWorld());
		@SuppressWarnings("unchecked")
		Map<Object, Object> map = (Map<Object, Object>) Ref.get(w, "capturedTileEntities");
		  if (map.containsKey(block.getBlockPosition()))
		      return map.get(block.getBlockPosition());
		    Object tileentity = null;
		    if ((boolean)Ref.get(w, "tickingTileEntities"))
		      tileentity = Ref.invoke(w,"E",block.getBlockPosition());
		    if (tileentity == null)
		      tileentity = Ref.invoke(Ref.invoke(w,"getChunkAtWorldCoords",block.getBlockPosition()),"a", block.getBlockPosition(), Ref.getNulled(Ref.nms("Chunk$EnumTileEntityState"), "IMMEDIATE")); 
		    if (tileentity == null)
		      tileentity = Ref.invoke(w,"E",block.getBlockPosition()); 
		    return tileentity;
	}
	
	public SerializedBlock serialize(TheMaterial material, Map<String, Object> extra);
	
	public String getAsString();
	
	public TheMaterial getType();
	
	public SerializedBlock fromString(String string);
	
	public SerializedBlock apply(Position pos);
}
