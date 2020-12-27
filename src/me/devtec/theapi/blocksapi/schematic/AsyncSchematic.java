package me.devtec.theapi.blocksapi.schematic;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.blocksapi.BlockIterator;
import me.devtec.theapi.blocksapi.schematic.construct.Schematic;
import me.devtec.theapi.blocksapi.schematic.construct.SchematicCallable;
import me.devtec.theapi.blocksapi.schematic.construct.SchematicSaveCallable;
import me.devtec.theapi.utils.Position;
import me.devtec.theapi.utils.TheMaterial;
import me.devtec.theapi.utils.datakeeper.Data;
import me.devtec.theapi.utils.datakeeper.DataType;
import me.devtec.theapi.utils.nms.NMSAPI;
import me.devtec.theapi.utils.reflections.Ref;

public class AsyncSchematic implements Schematic {
	private static Constructor<?> cc = Ref.constructor(Ref.nms("PacketPlayOutBlockChange"),Ref.nms("BlockPosition"), Ref.nms("IBlockData"));
	private static Method save, loadd;
	static {
		save=Ref.method(Ref.nms("Entity"), "b", Ref.nms("NBTTagCompound"));
		loadd=Ref.method(Ref.nms("Entity"), "a", Ref.nms("NBTTagCompound"));
		if(save==null)
		save=Ref.method(Ref.nms("Entity"), "saveData", Ref.nms("NBTTagCompound"));
		if(loadd==null)
		loadd=Ref.method(Ref.nms("Entity"), "loadData", Ref.nms("NBTTagCompound"));
	}
	private static String end = ".schem";
	private final String name;
	private Data load;
	
	public AsyncSchematic(String name) {
		this.name=name;
	}
	
	@Override
	public boolean load() {
		if(load==null)
		load=new Data("plugins/TheAPI/Schematic/"+name+end);
		else load.reload(new File("plugins/TheAPI/Schematic/"+name+end));
		return !load.getKeys().isEmpty();
	}

	@Override
	public void paste(Position stand, boolean pasteEntities, boolean replaceAir, SchematicCallable callable) {
		if(load==null || load.getKeys().isEmpty())return; //isn't loaded or is empty
		AsyncSerializedBlock ser = new AsyncSerializedBlock();
		Position aa = load.getBoolean("info.standing")?Position.fromString(load.getString("info.corner.a")):Position.fromString(load.getString("info.corner.a")).add(stand)
		, bb = load.getBoolean("info.standing")?Position.fromString(load.getString("info.corner.b")):Position.fromString(load.getString("info.corner.b")).add(stand);
		aa.setWorld(stand.getWorld());
		bb.setWorld(stand.getWorld());
		for(Position pos : new BlockIterator(aa, load.getBoolean("info.standing")?Position.fromString(load.getString("info.corner.b")):Position.fromString(load.getString("info.corner.b")).add(stand))) {
			//BLOCKS
			boolean set = false;
			for (String fs : load.getStringList(pos.getChunkKey()+".blocks")) {
				String poos = fs.split("/:/")[0], block = fs.replaceFirst(poos+"/:/", "");
				Position a = Position.fromString(poos);
				if(pos.getBlockX()==a.getBlockX() && pos.getBlockY()==a.getBlockY() && pos.getBlockZ()==a.getBlockZ()) {
					set=true;
					ser.fromString(block);
					pos.setType(ser.material);
					Block block1 = pos.getBlock();
					block1.setBiome(ser.biome);
					BlockState state = block1.getState();
					state.setRawData(ser.data);
					state.update(true);
					break;
				}
			}
			if(!set && replaceAir)
				pos.setType(new TheMaterial(Material.AIR));
			
			//ENTITIES
			if(pasteEntities) {
				for (String fs : load.getStringList(pos.getChunkKey()+".blocks")) {
					String poos = fs.split("/:/")[0], ent = fs.replaceFirst(poos+"/:/", "");
					Position a = Position.fromString(poos);
					if(pos.getBlockX()==a.getBlockX() && pos.getBlockY()==a.getBlockY() && pos.getBlockZ()==a.getBlockZ()) {
						set=true;
						String type = ent.split("/")[0];
						String serNbt = ent.replaceFirst(type+"/", "");
						Object nbt = Ref.invokeNulled(Ref.nms("MojangsonParser"), "parse", serNbt);
						Entity e = pos.getWorld().spawnEntity(pos.toLocation(), EntityType.valueOf(type));
						Ref.invoke(NMSAPI.getEntity(e), AsyncSchematic.loadd, nbt); //load
						break;
					}
				}
			}
		}
		for(Position pos : new BlockIterator(aa, load.getBoolean("info.standing")?Position.fromString(load.getString("info.corner.b")):Position.fromString(load.getString("info.corner.b")).add(stand))) {
			Object packet = Ref.newInstance(cc,
							pos.getBlockPosition(), pos.getType().getIBlockData());
			for (Player p : TheAPI.getOnlinePlayers())
				if (p.getWorld().getName().equals(pos.getWorldName()))
					Ref.sendPacket(p, packet);
		}
		if(callable!=null)
			callable.run(this);
	}

	@Override
	public void save(Position fromCopy, Position cornerA, Position cornerB, SchematicSaveCallable callable) {
		Data save = new Data("plugins/TheAPI/Schematic/"+name+end);
		save.set("info.version", "1.0");
		save.set("info.created", System.currentTimeMillis());
		save.set("info.standing", fromCopy != null);
		save.set("info.corner.a", fromCopy != null ? cornerA.subtract(fromCopy).toString() : cornerA.toString());
		save.set("info.corner.b", fromCopy != null ?cornerB.subtract(fromCopy).toString() : cornerB.toString());
		AsyncSerializedBlock ser = new AsyncSerializedBlock();
		for(Position pos : new BlockIterator(cornerA, cornerB)) {
			//ENTITIES
			List<String> saved = save.getStringList(pos.getChunkKey()+".entities");
			for(Entity e : pos.getChunk().getEntities()) {
				if(e.getLocation().distance(pos.toLocation()) <= 1) {
					Object nbt = Ref.newInstanceNms("NBTTagCompound");
					Ref.invoke(NMSAPI.getEntity(e), AsyncSchematic.save, nbt); //save
					String en = e.getType().name()+"/"+nbt.toString();
					saved.add((fromCopy != null?new Position(e.getLocation()).clone().add(-fromCopy.getBlockX(), -fromCopy.getBlockY(), -fromCopy.getBlockZ()).toString():new Position(e.getLocation()).toString())+"/:/"+en);
				}
			}
			save.set(pos.getChunkKey()+".entities", saved);
			
			if(pos.getBukkitType()==Material.AIR)continue; //DON'T SAVE AIR BLOCK
			
			//BLOCKS
			saved = save.getStringList(pos.getChunkKey()+".blocks");
			saved.add((fromCopy != null?pos.clone().add(-fromCopy.getBlockX(), -fromCopy.getBlockY(), -fromCopy.getBlockZ()).toString():pos.toString())+"/:/"+ser.serialize(pos.getBlock()).getAsString());
			save.set(pos.getChunkKey()+".blocks", saved);
		}
		if(callable!=null)
			callable.run(this, save);
		if(save!=null && !save.getKeys().isEmpty())
		save.save(DataType.BYTE);
	}

}
