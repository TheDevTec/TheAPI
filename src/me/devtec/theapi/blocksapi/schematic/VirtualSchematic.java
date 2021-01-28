package me.devtec.theapi.blocksapi.schematic;

import java.lang.reflect.Method;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import me.devtec.theapi.blocksapi.BlockIterator;
import me.devtec.theapi.blocksapi.BlocksAPI;
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

public class VirtualSchematic implements Schematic {
	private static Method save, loadd;
	static {
		save=Ref.method(Ref.nms("Entity"), "b", Ref.nms("NBTTagCompound"));
		loadd=Ref.method(Ref.nms("Entity"), "a", Ref.nms("NBTTagCompound"));
		if(save==null)
			save=Ref.method(Ref.nms("Entity"), "saveData", Ref.nms("NBTTagCompound"));
		if(loadd==null)
			loadd=Ref.method(Ref.nms("Entity"), "loadData", Ref.nms("NBTTagCompound"));
	}
	
	protected Data load;
	
	@Override
	public boolean load() {
		return true;
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
				for(Position pos : new BlockIterator(aa,bb)) {
					//BLOCKS
					boolean set = false;
					for (String fs : load.getStringList(pos.getChunkKey()+".blocks")) {
						String poos = fs.split("/:/")[0];
						Position a = Position.fromString(poos);
						if(pos.getBlockX()==a.getBlockX() && pos.getBlockY()==a.getBlockY() && pos.getBlockZ()==a.getBlockZ()) {
							String block= fs.substring(poos.length()+3);
							set=true;
							ser.fromString(block);
							Position sett = pos.clone();
							if(stand!=null)
								sett=sett.add(stand.getBlockX(), stand.getBlockY(), stand.getBlockZ());
							BlocksAPI.set(sett, ser.getType());
							break;
						}
					}
					if(!set && replaceAir)
						(stand!=null?pos.clone().add(stand.getBlockX(), stand.getBlockY(), stand.getBlockZ()):pos).setType(new TheMaterial(Material.AIR));
					
					//ENTITIES
					if(pasteEntities) {
						for (String fs : load.getStringList(pos.getChunkKey()+".entities")) {
							String poos = fs.split("/:/")[0], ent = fs.replaceFirst(poos+"/:/", "");
							Position a = Position.fromString(poos);
							if(pos!=null)a.add(stand.getBlockX(), stand.getBlockY(), stand.getBlockZ());
							if(pos.getBlockX()==a.getBlockX() && pos.getBlockY()==a.getBlockY() && pos.getBlockZ()==a.getBlockZ()) {
								set=true;
								String type = ent.split("/")[0];
								String serNbt = ent.replaceFirst(type+"/", "");
								Object nbt = Ref.invokeNulled(Ref.nms("MojangsonParser"), "parse", serNbt);
								new Tasker() {
									public void run() {
										Entity e = pos.getWorld().spawnEntity(pos.toLocation(), EntityType.valueOf(type));
										Ref.invoke(NMSAPI.getEntity(e), VirtualSchematic.loadd, nbt); //load
									}
								}.runTaskSync();
								break;
							}
						}
					}
				}
				if(callable!=null)
					callable.run(VirtualSchematic.this);
		}}.runTask();
	}

	@Override
	public void save(Position fromCopy, Position cornerA, Position cornerB, SchematicSaveCallable callable) {
		new Tasker() {
			public void run() {
				Data save = new Data();
				save.set("info.standing", fromCopy != null);
				save.set("info.corner.a", fromCopy != null ? cornerA.clone().add(-fromCopy.getBlockX(),-fromCopy.getBlockY(),-fromCopy.getBlockZ()).toString() : cornerA.toString());
				save.set("info.corner.b", fromCopy != null ?cornerB.clone().add(-fromCopy.getBlockX(),-fromCopy.getBlockY(),-fromCopy.getBlockZ()).toString() : cornerB.toString());
				SerializedBlock ser = new InitialSerializedBlock();
				for(Position pos : new BlockIterator(cornerA, cornerB)) {
					//ENTITIES
					List<String> saved = save.getStringList((fromCopy != null ? pos.clone().add(-fromCopy.getBlockX(),-fromCopy.getBlockY(),-fromCopy.getBlockZ()).getChunkKey() : pos.getChunkKey())+".entities");
					for(Entity e : pos.getChunk().getEntities()) {
						if(e.getType()!=EntityType.PLAYER)
						if(e.getLocation().distance(pos.toLocation()) <= 1) {
							Object nbt = Ref.newInstance(Ref.constructor(Ref.nms("NBTTagCompound")));
							Ref.invoke(NMSAPI.getEntity(e), VirtualSchematic.save, nbt); //save
							String en = e.getType().name()+"/"+nbt.toString();
							saved.add((fromCopy != null?new Position(e.getLocation()).clone().add(-fromCopy.getBlockX(),-fromCopy.getBlockY(),-fromCopy.getBlockZ()).toString():new Position(e.getLocation()).toString())+"/:/"+en);
						}
					}
					save.set((fromCopy != null ? pos.clone().add(-fromCopy.getBlockX(),-fromCopy.getBlockY(),-fromCopy.getBlockZ()).getChunkKey() : pos.getChunkKey())+".entities", saved);
					if(pos.getBukkitType()==Material.AIR||pos.getBukkitType()==Material.CAVE_AIR)continue; //DON'T SAVE AIR BLOCK
					
					//BLOCKS
					saved = save.getStringList((fromCopy != null ? pos.clone().add(-fromCopy.getBlockX(),-fromCopy.getBlockY(),-fromCopy.getBlockZ()).getChunkKey() : pos.getChunkKey())+".blocks");
					saved.add((fromCopy != null?pos.clone().add(-fromCopy.getBlockX(),-fromCopy.getBlockY(),-fromCopy.getBlockZ()).toString():pos.toString())+"/:/"+ser.serialize(pos).getAsString());
					save.set((fromCopy != null ? pos.clone().add(-fromCopy.getBlockX(),-fromCopy.getBlockY(),-fromCopy.getBlockZ()).getChunkKey() : pos.getChunkKey())+".blocks", saved);
				}
				if(callable!=null)
					callable.run(VirtualSchematic.this, save);
				VirtualSchematic.this.load=save;
				if(save!=null && !save.getKeys().isEmpty())
				save.save(DataType.YAML);
		}}.runTask();
	}
}
