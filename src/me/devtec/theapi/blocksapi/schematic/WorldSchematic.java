package me.devtec.theapi.blocksapi.schematic;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import me.devtec.theapi.blocksapi.BlockIterator;
import me.devtec.theapi.blocksapi.schematic.construct.Schematic;
import me.devtec.theapi.blocksapi.schematic.construct.SchematicCallable;
import me.devtec.theapi.blocksapi.schematic.construct.SchematicSaveCallable;
import me.devtec.theapi.blocksapi.schematic.construct.SerializedBlock;
import me.devtec.theapi.scheduler.Tasker;
import me.devtec.theapi.utils.Position;
import me.devtec.theapi.utils.TheMaterial;
import me.devtec.theapi.utils.datakeeper.Data;
import me.devtec.theapi.utils.datakeeper.DataType;
import me.devtec.theapi.utils.nms.NMSAPI;
import me.devtec.theapi.utils.reflections.Ref;

public class WorldSchematic implements Schematic {
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
	protected Data load;
	
	public WorldSchematic(String name) {
		this.name=name;
	}
	
	public WorldSchematic(VirtualSchematic schem, String name) {
		this.name=name;
		load=schem.load;
	}
	
	@Override
	public boolean load() {
		if(load==null)
		load=new Data("plugins/TheAPI/Schematic/"+name+end);
		else load.reload(new File("plugins/TheAPI/Schematic/"+name+end));
		return !load.getKeys().isEmpty();
	}

	private static String sum(int i, int j, int k) {
		return (i)+""+(j)+""+(k);
	}
	
	@Override
	public void paste(Position stand, boolean pasteEntities, boolean replaceAir, SchematicCallable callable) {
		if(load==null || load.getKeys().isEmpty())return; //isn't loaded or is empty
		new Tasker() {
			public void run() {
				SerializedBlock ser = new InitialSerializedBlock();
				Position aa = Position.fromString(load.getString("info.corner.a")), bb = Position.fromString(load.getString("info.corner.b"));
				aa.setWorld(stand.getWorld());
				bb.setWorld(stand.getWorld());
				boolean st = load.getBoolean("info.standing");
				for(Position pos : new BlockIterator(aa,bb)) {
					//BLOCK
					String setr = load.getString(pos.getChunkKey()+".b."+sum(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ()));
					if(setr!=null) {
						Position sett = pos.clone();
						if(stand!=null && st)
							sett=sett.add(stand.getBlockX(), stand.getBlockY(), stand.getBlockZ());
						ser.fromString(setr).apply(sett);
					}
					if(setr==null && replaceAir)
						(stand!=null && st?pos.clone().add(stand.getBlockX(), stand.getBlockY(), stand.getBlockZ()):pos).setType(new TheMaterial(Material.AIR));
					
					//ENTITIES
					if(pasteEntities) {
						List<String> sset=load.getStringList(pos.getChunkKey()+".e."+sum(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ()));
						if(sset.isEmpty())continue;
						for(String set :sset) {
							String poos = set.split("/:/")[0];
							String ent = set.substring(poos.length()+3);
							Position a = Position.fromString(poos);
							if(stand!=null && st)a.add(stand.getBlockX(), stand.getBlockY(), stand.getBlockZ());
							String type = ent.split("/")[0];
							String serNbt = ent.replaceFirst(type+"/", "");
							Object nbt = Ref.invokeNulled(Ref.nms("MojangsonParser"), "parse", serNbt);
							new Tasker() {
								public void run() {
									Entity e = pos.getWorld().spawnEntity(pos.toLocation(), EntityType.valueOf(type));
									Ref.invoke(NMSAPI.getEntity(e), WorldSchematic.loadd, nbt); //load
								}
							}.runTaskSync();
						}
					}
				}
				if(callable!=null)
					callable.run(WorldSchematic.this);
		}}.runTask();
	}

	@Override
	public void save(Position fromCopy, Position cornerA, Position cornerB, SchematicSaveCallable callable) {
		new Tasker() {
			public void run() {
				final Data save = new Data("plugins/TheAPI/Schematic/"+name+end);
				save.reset();
				save.set("info.version", "1.0");
				save.set("info.created", System.currentTimeMillis());
				save.set("info.standing", fromCopy != null);
				save.set("info.corner.a", fromCopy != null ? cornerA.clone().add(-fromCopy.getBlockX(),-fromCopy.getBlockY(),-fromCopy.getBlockZ()).toString() : cornerA.toString());
				save.set("info.corner.b", fromCopy != null ?cornerB.clone().add(-fromCopy.getBlockX(),-fromCopy.getBlockY(),-fromCopy.getBlockZ()).toString() : cornerB.toString());
				SerializedBlock ser = new InitialSerializedBlock();
				for(Position pos : new BlockIterator(cornerA, cornerB)) {
					//ENTITIES
					String sum = sum(fromCopy != null ? pos.getBlockX()-fromCopy.getBlockX():pos.getBlockX(), fromCopy != null ? pos.getBlockY()-fromCopy.getBlockY():pos.getBlockY(), fromCopy != null ? pos.getBlockZ()-fromCopy.getBlockZ():pos.getBlockZ());
					long key  =(fromCopy != null ? pos.clone().add(-fromCopy.getBlockX(),-fromCopy.getBlockY(),-fromCopy.getBlockZ()).getChunkKey() : pos.getChunkKey());
					List<String> entity = save.getStringList(key+".e");
					for(Entity e : pos.getChunk().getEntities()) {
						if(e.getType()!=EntityType.PLAYER)
						if(e.getLocation().distance(pos.toLocation()) <= 1) {
							Object nbt = Ref.newInstance(Ref.constructor(Ref.nms("NBTTagCompound")));
							Ref.invoke(NMSAPI.getEntity(e), WorldSchematic.save, nbt); //save
							String en = e.getType().name()+"/"+nbt.toString();
							entity.add((fromCopy != null?new Position(e.getLocation()).clone().add(-fromCopy.getBlockX(),-fromCopy.getBlockY(),-fromCopy.getBlockZ()).toString():new Position(e.getLocation()).toString())+"/:/"+en);
						}
					}
					save.set(key+".e."+sum, entity);
					if(pos.getBukkitType()==Material.AIR||pos.getBukkitType()==Material.CAVE_AIR)continue; //DON'T SAVE AIR BLOCK
					
					//BLOCK
					save.set(key+".b."+sum, ser.serialize(pos).getAsString());
				}
				WorldSchematic.this.load=save;
				if(callable!=null)
					callable.run(WorldSchematic.this, save);
				if(!save.getKeys().isEmpty())
				save.save(DataType.BYTE);
		}}.runTask();
	}
}
