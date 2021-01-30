package me.devtec.theapi.blocksapi.schematic.construct;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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
import org.bukkit.inventory.ItemStack;

import me.devtec.theapi.utils.Position;
import me.devtec.theapi.utils.TheMaterial;
import me.devtec.theapi.utils.json.Writer;
import me.devtec.theapi.utils.reflections.Ref;

public interface SerializedBlock extends Serializable {
	static Method get = Ref.getClass("com.google.common.collect.ForwardingMultimap")==null?Ref.method(Ref.getClass("net.minecraft.util.com.google.common.collect.ForwardingMultimap"), "get", Object.class):
			Ref.method(Ref.getClass("com.google.common.collect.ForwardingMultimap"), "get", Object.class);
	
	static Class<?> chest = Ref.nms("TileEntityChest"), breew = Ref.nms("TileEntityBrewingStand")
			, banner = Ref.nms("TileEntityBanner"), beacon = Ref.nms("TileEntityBeacon")
			, command = Ref.nms("TileEntityCommand"), dispenser = Ref.nms("TileEntityDispenser")
			, dropper = Ref.nms("TileEntityDropper"), hopper = Ref.nms("TileEntityHopper")
			, enchant = Ref.nms("TileEntityEnchantingTable"), flowerpot = Ref.nms("TileEntityFlowerPot")
			, furnace = Ref.nms("TileEntityFurnace"), spawner = Ref.nms("TileEntitySpawner")
			, note = Ref.nms("TileEntityNote"), piston = Ref.nms("TileEntityPiston")
			, sign = Ref.nms("TileEntitySign"), skull = Ref.nms("TileEntitySkull");
	
	public default SerializedBlock serialize(Position block) {
		Block b = block.getBlock();
		String t = "";
		try {
			if(b.getBlockData() instanceof Orientable) {
				Orientable dir = (Orientable)b.getBlockData();
				t+=dir.getAxis().name();
			}
		}catch(Exception | NoSuchMethodError | NoClassDefFoundError e) {}
		try {
			if(b.getBlockData() instanceof Rotatable) {
				Rotatable dir = (Rotatable)b.getBlockData();
				t+="!"+dir.getRotation().name();
			}
		}catch(Exception | NoSuchMethodError | NoClassDefFoundError e) {}
		try {
			if(b.getBlockData() instanceof Directional) {
				Directional dir = (Directional)b.getBlockData();
				t+="!"+dir.getFacing().name();
			}
		}catch(Exception | NoSuchMethodError | NoClassDefFoundError e) {}
		try {
			if(b.getBlockData() instanceof Ageable) {
				Ageable dir = (Ageable)b.getBlockData();
				t+="!"+dir.getAge();
			}
		}catch(Exception | NoSuchMethodError | NoClassDefFoundError e) {}
		try {
			if(b.getBlockData() instanceof AnaloguePowerable) {
				AnaloguePowerable dir = (AnaloguePowerable)b.getBlockData();
				t+="!"+dir.getPower();
			}
		}catch(Exception | NoSuchMethodError | NoClassDefFoundError e) {}
		try {
			if(b.getBlockData() instanceof Attachable) {
				Attachable dir = (Attachable)b.getBlockData();
				t+="!"+dir.isAttached();
			}
		}catch(Exception | NoSuchMethodError | NoClassDefFoundError e) {}
		try {
			if(b.getBlockData() instanceof Bisected) {
				Bisected dir = (Bisected)b.getBlockData();
				t+="!"+dir.getHalf().name();
			}
		}catch(Exception | NoSuchMethodError | NoClassDefFoundError e) {}
		try {
			if(b.getBlockData() instanceof FaceAttachable) {
				FaceAttachable dir = (FaceAttachable)b.getBlockData();
				t+="!"+dir.getAttachedFace().name();
			}
		}catch(Exception | NoSuchMethodError | NoClassDefFoundError e) {}
		try {
			if(b.getBlockData() instanceof Levelled) {
				Levelled dir = (Levelled)b.getBlockData();
				t+="!"+dir.getLevel();
			}
		}catch(Exception | NoSuchMethodError | NoClassDefFoundError e) {}
		try {
			if(b.getBlockData() instanceof Lightable) {
				Lightable dir = (Lightable)b.getBlockData();
				t+="!"+dir.isLit();
			}
		}catch(Exception | NoSuchMethodError | NoClassDefFoundError e) {}
		try {
			if(b.getBlockData() instanceof MultipleFacing) {
				MultipleFacing dir = (MultipleFacing)b.getBlockData();
				Map<String, Boolean> map = new HashMap<>(dir.getFaces().size());
				for(BlockFace face : dir.getFaces())
					if(dir.getAllowedFaces().contains(face))map.put(face.name(), true);
					else map.put(face.name(), false);
				t+="!"+Writer.write(map);
			}
		}catch(Exception | NoSuchMethodError | NoClassDefFoundError e) {}
		try {
			if(b.getBlockData() instanceof Openable) {
				Openable dir = (Openable)b.getBlockData();
				t+="!"+dir.isOpen();
			}
		}catch(Exception | NoSuchMethodError | NoClassDefFoundError e) {}
		try {
			if(b.getBlockData() instanceof Powerable) {
				Powerable dir = (Powerable)b.getBlockData();
				t+="!"+dir.isPowered();
			}
		}catch(Exception | NoSuchMethodError | NoClassDefFoundError e) {}
		try {
			if(b.getBlockData() instanceof Rail ) {
				Rail  dir = (Rail)b.getBlockData();
				t+="!"+dir.getShape().name();
			}
		}catch(Exception | NoSuchMethodError | NoClassDefFoundError e) {}
		try {
			if(b.getBlockData() instanceof Snowable) {
				Snowable dir = (Snowable)b.getBlockData();
				t+="!"+dir.isSnowy();
			}
		}catch(Exception | NoSuchMethodError | NoClassDefFoundError e) {}
		try {
			if(b.getBlockData() instanceof Waterlogged) {
				Waterlogged dir = (Waterlogged)b.getBlockData();
				t+="!"+dir.isWaterlogged();
			}
		}catch(Exception | NoSuchMethodError | NoClassDefFoundError e) {
		}
		Map<String, Object> values = new HashMap<>();
		
		Object dir = getState(block);
		if(dir!=null) {
			if(chest.isInstance(dir)) {
				List<ItemStack> items = new ArrayList<>(27);
				for(Object o : (Collection<?>)Ref.get(dir, "items")) {
					items.add((ItemStack)Ref.invokeNulled(Ref.craft("inventory.CraftItemStack"), "asBukkitCopy", o));
				}
				values.put("items", items);
				values.put("open", Ref.get(dir, "opened"));
			}
			if(banner.isInstance(dir)) {
				List<Object> items = new ArrayList<>(20);
				if(Ref.get(dir, "patterns")!=null)
				for(Object o : (Collection<?>)Ref.get(dir, "patterns")) {
					Map<String, Integer> map = new HashMap<>();
					String pattern = (String) Ref.invoke(o, "getString","Pattern");
					int color = (int) Ref.invoke(o, "getInt","Color");
					map.put(pattern, color);
					items.add(map);
				}
				if(!items.isEmpty())
					values.put("pattern", items);
				if( Ref.get(dir, "color")!=null)
				values.put("color", Ref.get(dir, "color").toString());
			}
		/*if(b.getState() instanceof Banner) {
			Banner dir = (Banner)b.getState();
			values.put("dye", dir.getBaseColor().name());
			List<String> pat = new ArrayList<>();
			for(Pattern e : dir.getPatterns())
				pat.add(e.getColor().name()+":"+e.getPattern().name());
		}
		if(b.getState() instanceof Chest) {
			Chest dir = (Chest)b.getState();
			values.put("items", Arrays.asList(dir.getInventory().getContents()));
		}
		if(b.getState() instanceof BrewingStand) {
			BrewingStand dir = (BrewingStand)b.getState();
			values.put("breew", dir.getBrewingTime());
			values.put("fuel", dir.getFuelLevel());
			values.put("lock", dir.getLock());
			values.put("name", dir.getCustomName());
		}
		if(b.getState() instanceof CommandBlock) {
			CommandBlock dir = (CommandBlock)b.getState();
			values.put("command", dir.getCommand());
			values.put("name", dir.getName());
		}
		if(b.getState() instanceof CreatureSpawner) {
			CreatureSpawner dir = (CreatureSpawner)b.getState();
			values.put("creature", dir.getCreatureTypeName());
			values.put("delay", dir.getDelay());
			values.put("nearby", dir.getMaxNearbyEntities());
			values.put("max_spawn", dir.getMaxSpawnDelay());
			values.put("min_spawn", dir.getMinSpawnDelay());
			values.put("range", dir.getRequiredPlayerRange());
			values.put("count", dir.getSpawnCount());
			values.put("spawn_range", dir.getSpawnRange());
			values.put("spawn", dir.getSpawnedType().name());
		}
		if(b.getState() instanceof Dispenser) {
			Dispenser dir = (Dispenser)b.getState();
			values.put("lock", dir.getLock());
			values.put("name", dir.getCustomName());
			values.put("seed", dir.getSeed());
		}
		if(b.getState() instanceof Dropper) {
			Dropper dir = (Dropper)b.getState();
			values.put("lock", dir.getLock());
			values.put("name", dir.getCustomName());
			values.put("seed", dir.getSeed());
			values.put("items", Arrays.asList(dir.getInventory().getContents()));
		}
		if(b.getState() instanceof Furnace) {
			Furnace dir = (Furnace)b.getState();
			values.put("lock", dir.getLock());
			values.put("name", dir.getCustomName());
			values.put("burn", dir.getBurnTime());
			values.put("cook", dir.getCookTime());
			values.put("cook_max", dir.getCookTimeTotal());
			values.put("items", Arrays.asList(dir.getInventory().getContents()));
		}
		if(b.getState() instanceof Hopper) {
			Hopper dir = (Hopper)b.getState();
			values.put("lock", dir.getLock());
			values.put("name", dir.getCustomName());
			values.put("seed", dir.getSeed());
			values.put("items", Arrays.asList(dir.getInventory().getContents()));
		}
		if(b.getState() instanceof Jukebox) {
			Jukebox dir = (Jukebox)b.getState();
			values.put("play", dir.getPlaying().name());
			values.put("record", dir.getRecord());
		}
		if(b.getState() instanceof NoteBlock) {
			NoteBlock dir = (NoteBlock)b.getState();
			values.put("note", dir.getRawNote());
		}
		if(b.getState() instanceof Skull) {
			Skull dir = (Skull)b.getState();
			values.put("skull", dir.getSkullType().name());
			values.put("owner", dir.getOwner());
			values.put("rotate", dir.getRotation().name());
			Object profile = Ref.get(dir, "profile");
			values.put("owner_values", (String)Ref.invoke(Ref.invoke(Ref.invoke(profile, "getProperties"), get, "textures"),"getValue"));
		}
		if(b.getState() instanceof Sign) {
			Sign dir = (Sign)b.getState();
			try {
				values.put("edit", dir.isEditable());
			}catch(Exception | NoSuchMethodError e) {}
			try {
				values.put("dye", dir.getColor().name());
			}catch(Exception | NoSuchMethodError e) {}
			values.put("lines", Arrays.asList(dir.getLines()));
		}
		try {
			if(b.getState() instanceof Dropper) {
				Dropper dir = (Dropper)b.getState();
				values.put("lock", dir.getLock());
				values.put("name", dir.getCustomName());
				values.put("seed", dir.getSeed());
				values.put("items", Arrays.asList(dir.getInventory().getContents()));
			}
		}catch(Exception | NoClassDefFoundError | NoSuchMethodError e) {}
		try {
			if(b.getState() instanceof EnchantingTable) {
				EnchantingTable dir = (EnchantingTable)b.getState();
				values.put("name", dir.getCustomName());
			}
		}catch(Exception | NoClassDefFoundError | NoSuchMethodError e) {}
		if(TheAPI.isNewerThan(8)) {
			try {
				if(b.getState() instanceof ShulkerBox) {
					ShulkerBox dir = (ShulkerBox)b.getState();
					values.put("lock", dir.getLock());
					values.put("name", dir.getCustomName());
					values.put("seed", dir.getSeed());
					values.put("items", Arrays.asList(dir.getInventory().getContents()));
				}
			}catch(Exception | NoClassDefFoundError | NoSuchMethodError e) {}
			try {
				if(b.getState() instanceof Lectern) {
					Lectern dir = (Lectern)b.getState();
					values.put("page", dir.getPage());
					values.put("items", Arrays.asList(dir.getInventory().getContents()));
				}
			}catch(Exception | NoClassDefFoundError | NoSuchMethodError e) {}
			try {
				if(b.getState() instanceof Barrel) {
					Barrel dir = (Barrel)b.getState();
					values.put("lock", dir.getLock());
					values.put("name", dir.getCustomName());
					values.put("seed", dir.getSeed());
					values.put("items", Arrays.asList(dir.getInventory().getContents()));
				}
			}catch(Exception | NoClassDefFoundError | NoSuchMethodError e) {}
			try {
				if(b.getState() instanceof EndGateway) {
					EndGateway dir = (EndGateway)b.getState();
					values.put("age", dir.getAge());
					values.put("teleport", dir.isExactTeleport());
					values.put("exit", dir.getExitLocation());
				}
			}catch(Exception | NoClassDefFoundError | NoSuchMethodError e) {}
			try {
				if(b.getState() instanceof Campfire) {
					Campfire dir = (Campfire)b.getState();
					values.put("camp_0", dir.getCookTime(0));
					values.put("camp_1", dir.getCookTime(1));
					values.put("camp_2", dir.getCookTime(2));
					values.put("camp_3", dir.getCookTime(3));
					values.put("max_camp_0", dir.getCookTimeTotal(0));
					values.put("max_camp_1", dir.getCookTimeTotal(1));
					values.put("max_camp_2", dir.getCookTimeTotal(2));
					values.put("max_camp_3", dir.getCookTimeTotal(3));
					values.put("i_camp_0", dir.getItem(0));
					values.put("i_camp_1", dir.getItem(1));
					values.put("i_camp_2", dir.getItem(2));
					values.put("i_camp_3", dir.getItem(3));
				}
			}catch(Exception | NoClassDefFoundError | NoSuchMethodError e) {}
			try {
				if(b.getState() instanceof Beehive) {
					Beehive dir = (Beehive)b.getState();
					values.put("bee_max", dir.getMaxEntities());
					values.put("bees", dir.getEntityCount());
				}
			}catch(Exception | NoClassDefFoundError | NoSuchMethodError e) {}
			try {
				if(b.getState() instanceof Bed) {
					Bed dir = (Bed)b.getState();
					values.put("dye", dir.getColor().name());
				}
			}catch(Exception | NoClassDefFoundError | NoSuchMethodError e) {}
		}}*/}
		return serialize(block.getType(), t.replaceFirst("!", ""), values);
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
	
	public SerializedBlock serialize(TheMaterial material, String face, Map<String, Object> extra);
	
	public String getAsString();
	
	public TheMaterial getType();
	
	public String getFace();
	
	public SerializedBlock fromString(String string);
	
	public SerializedBlock apply(Position pos);
}
