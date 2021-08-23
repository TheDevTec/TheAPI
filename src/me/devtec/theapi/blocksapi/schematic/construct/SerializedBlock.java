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
import org.bukkit.block.data.BlockData;
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
import org.bukkit.block.data.type.Leaves;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.material.MaterialData;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.utils.Position;
import me.devtec.theapi.utils.TheMaterial;
import me.devtec.theapi.utils.json.Json;
import me.devtec.theapi.utils.reflections.Ref;

public interface SerializedBlock extends Serializable {
	static Method saveNbt = Ref.method(Ref.nmsOrOld("world.level.block.entity.TileEntity","TileEntity"), "save", Ref.nmsOrOld("nbt.NBTTagCompound","NBTTagCompound"))!=null?
			Ref.method(Ref.nmsOrOld("world.level.block.entity.TileEntity","TileEntity"), "save", Ref.nmsOrOld("nbt.NBTTagCompound","NBTTagCompound")):
				Ref.method(Ref.nmsOrOld("world.level.block.entity.TileEntity","TileEntity"), "b", Ref.nmsOrOld("nbt.NBTTagCompound","NBTTagCompound"));
	static Method get = Ref.getClass("com.google.common.collect.ForwardingMultimap")==null?Ref.method(Ref.getClass("net.minecraft.util.com.google.common.collect.ForwardingMultimap"), "get", Object.class):
			Ref.method(Ref.getClass("com.google.common.collect.ForwardingMultimap"), "get", Object.class);

	Map<String, Object> values = new HashMap<>();
	public default SerializedBlock serialize(Position block) {
		return serialize(block, block.getType());
	}
	Object objectNbt= Ref.newInstance(Ref.constructor(Ref.nmsOrOld("nbt.NBTTagCompound","NBTTagCompound")));
	public default SerializedBlock serialize(Position block, TheMaterial type) {
		Block b = block.getBlock();
		if(TheAPI.isNewVersion()) {
			BlockData d = b.getBlockData();
			if(d instanceof Orientable) {
				Orientable dir = (Orientable)d;
				values.put("orient", dir.getAxis().name());
			}
			if(d instanceof Leaves) {
				Leaves dir = (Leaves)d;
				values.put("leaves", dir.isPersistent());
			}
			if(d instanceof Rotatable) {
				Rotatable dir = (Rotatable)d;
				values.put("face", dir.getRotation().name());
			}
			if(d instanceof Directional) {
				Directional dir = (Directional)d;
				values.put("face", dir.getFacing().name());
			}
			if(d instanceof Ageable) {
				Ageable dir = (Ageable)d;
				values.put("age", dir.getAge());
			}
			if(d instanceof AnaloguePowerable) {
				AnaloguePowerable dir = (AnaloguePowerable)d;
				values.put("power", dir.getPower());
			}
			if(d instanceof Attachable) {
				Attachable dir = (Attachable)d;
				values.put("attach", dir.isAttached());
			}
			if(d instanceof Bisected) {
				Bisected dir = (Bisected)d;
				values.put("half", dir.getHalf().name());
			}
			if(TheAPI.isNewerThan(15)) {
			if(d instanceof FaceAttachable) {
				FaceAttachable dir = (FaceAttachable)d;
				values.put("fattach", dir.getAttachedFace().name());
			}}
			if(d instanceof Levelled) {
				Levelled dir = (Levelled)d;
				values.put("level", dir.getLevel());
			}
			if(d instanceof Lightable) {
				Lightable dir = (Lightable)d;
				values.put("lit", dir.isLit());
			}
			if(d instanceof MultipleFacing) {
				MultipleFacing dir = (MultipleFacing)d;
				Map<String, Boolean> map = new HashMap<>(dir.getFaces().size());
				for(BlockFace face : dir.getFaces())
					if(dir.getAllowedFaces().contains(face))map.put(face.name(), true);
					else map.put(face.name(), false);
				values.put("mface", Json.writer().simpleWrite(map));
			}
			if(d instanceof Openable) {
				Openable dir = (Openable)d;
				values.put("open", dir.isOpen());
			}
			if(d instanceof Powerable) {
				Powerable dir = (Powerable)d;
				values.put("power", dir.isPowered());
			}
			if(d instanceof Rail) {
				Rail  dir = (Rail)d;
				values.put("rail", dir.getShape().name());
			}
			if(d instanceof Snowable) {
				Snowable dir = (Snowable)d;
				values.put("snow", dir.isSnowy());
			}
			if(d instanceof Waterlogged) {
				Waterlogged dir = (Waterlogged)d;
				values.put("water", dir.isWaterlogged());
			}
			if(d instanceof Stairs) {
				Stairs dir = (Stairs)d;
				values.put("sshape", dir.getShape().name());
			}
			if(d instanceof Rail) {
				Rail dir = (Rail)d;
				values.put("rshape", dir.getShape().name());
			}
		}else {
			MaterialData data = b.getState().getData();
			if(data instanceof org.bukkit.material.Colorable)
				values.put("data", ((org.bukkit.material.Colorable) data).getColor().name());
			if(data instanceof org.bukkit.material.Directional)
				values.put("face", ((org.bukkit.material.Directional) data).getFacing().name());
			if(data instanceof org.bukkit.material.Attachable)
				values.put("attach", ((org.bukkit.material.Attachable) data).getAttachedFace().name());
			if(data instanceof org.bukkit.material.Openable)
				values.put("open", ((org.bukkit.material.Openable) data).isOpen());
			if(data instanceof org.bukkit.material.Leaves)
				values.put("leaves", ((org.bukkit.material.Leaves) data).isDecaying());
		}
		Object dir = getState(block);
		if(dir!=null) {
			((Map<?,?>)Ref.get(objectNbt, TheAPI.isNewerThan(16)?"x":"map")).clear();
			Object ret = Ref.invoke(dir, saveNbt, objectNbt);
			values.put("nbt", ret.toString());
		}
		return serialize(type, values);
	}
	
	static Object getState(Position block) {
		Map<?,?> map = (Map<?,?>) Ref.get(block.getNMSChunk(), TheAPI.isNewerThan(16)?"l":"tileEntities");
		return map.get(block.getBlockPosition());
	}
	
	public SerializedBlock serialize(TheMaterial material, Map<String, Object> extra);
	
	public String getAsString();
	
	public TheMaterial getType();
	
	public SerializedBlock fromString(String string);
	
	public SerializedBlock apply(Position pos);
}
