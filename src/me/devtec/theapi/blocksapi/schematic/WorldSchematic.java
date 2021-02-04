package me.devtec.theapi.blocksapi.schematic;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;

import me.devtec.theapi.blocksapi.BlockIterator;
import me.devtec.theapi.blocksapi.BlocksAPI;
import me.devtec.theapi.blocksapi.schematic.construct.Schematic;
import me.devtec.theapi.blocksapi.schematic.construct.SchematicCallable;
import me.devtec.theapi.blocksapi.schematic.construct.SchematicSaveCallable;
import me.devtec.theapi.blocksapi.schematic.construct.SerializedBlock;
import me.devtec.theapi.blocksapi.schematic.storage.SchematicData;
import me.devtec.theapi.scheduler.Tasker;
import me.devtec.theapi.utils.Position;
import me.devtec.theapi.utils.TheMaterial;
import me.devtec.theapi.utils.datakeeper.maps.MultiMap;
import me.devtec.theapi.utils.json.Reader;
import me.devtec.theapi.utils.json.Writer;
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
	protected SchematicData load;
	
	public WorldSchematic(String name) {
		this.name=name;
	}
	
	public WorldSchematic(VirtualSchematic schem, String name) {
		this.name=name;
		load=new SchematicData(schem.load);
		File f = new File("plugins/TheAPI/Schematic/"+name+end);

		if (!f.exists()) {
			try {
				if(f.getParentFile()!=null)
					f.getParentFile().mkdirs();
			} catch (Exception e) {
			}
			try {
				f.createNewFile();
			} catch (Exception e) {
			}
		}
		load.setFile(f);
	}
	
	@Override
	public boolean load() {
		if(load==null)
		load=new SchematicData();
		load.reload(new File("plugins/TheAPI/Schematic/"+name+end));
		return !load.getKeys().isEmpty();
	}
	
	public SchematicData data() {
		return load;
	}

	private static int sum(int i, int j, int k) {
		return (j << 8 | (k & 15) << 4 | (i & 15));
	}
	
	public void save() {
		new Tasker() {
			public void run() {
				load.save();
			}
		}.runTask();
	}
	
	public void save(SchematicSaveCallable callable) {
		new Tasker() {
			public void run() {
				callable.run(WorldSchematic.this, load);
				load.save();
			}
		}.runTask();
	}
	
	@Override
	public void paste(Position stand, boolean pasteEntities, boolean replaceAir, SchematicCallable callable) {
		if(load==null || load.getKeys().isEmpty())return; //isn't loaded or is empty
		new Tasker() {
			@SuppressWarnings("unchecked")
			public void run() {
				SerializedBlock ser = new InitialSerializedBlock();
				Position aa = Position.fromString(load.getString("info.corner.a")), bb = Position.fromString(load.getString("info.corner.b"));
				aa.setWorld(stand.getWorld());
				bb.setWorld(stand.getWorld());
				boolean st = load.getBoolean("info.standing");
				//PALLETE
				Map<Integer, String> pallete = new HashMap<>();
				for(String key : load.getKeys("pallete"))
					pallete.put(load.getInt("pallete."+key), key);

				//BLOCKS PER CHUNK: CHUNK-KEY, BLOCK-SUM, PALLETE-ID>
				MultiMap<Long, Integer, Integer> blocks = new MultiMap<>();
				for(String key : load.getKeys()) {
					if(!key.equals("info") && !key.equals("pallete")) {
						long c = Long.valueOf(key);
							for(String values : load.getKeys(key+".b")) {
								int val = Integer.valueOf(values);
								if(pallete.get(val)==null)continue;
								for(Double d : (List<Double>)Reader.read(load.getString(key+".b."+values)))
									blocks.put(c, d.intValue(), val);
							}
					}
				}
				
				TheMaterial air = new TheMaterial(Material.AIR);
				
				//BLOCKS
				for(Position pos : new BlockIterator(aa,bb)) {
					//BLOCK
					int sum = sum(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
					
					Position sett = pos.clone();
					if(stand!=null && st)
						sett=sett.add(stand.getBlockX(), stand.getBlockY(), stand.getBlockZ());
					
					if(blocks.containsThread(pos.getChunkKey(), sum)) {
						ser.fromString(pallete.get(blocks.get(pos.getChunkKey(), sum)).replace("<!>", ".")).apply(sett);
					}else if(replaceAir)
						BlocksAPI.set(sett, air);
					
				}
				if(callable!=null)
					callable.run(WorldSchematic.this);
		}}.runTask();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void save(Position fromCopy, Position cornerA, Position cornerB, SchematicSaveCallable callable) {
		new Tasker() {
			public void run() {
				final Map<String, Object> save = new HashMap<>();
				save.put("info.version", 1.1);
				save.put("info.created", System.currentTimeMillis());
				save.put("info.standing", fromCopy != null);
				save.put("info.corner.a", fromCopy != null ? cornerA.clone().add(-fromCopy.getBlockX(),-fromCopy.getBlockY(),-fromCopy.getBlockZ()).toString() : cornerA.toString());
				save.put("info.corner.b", fromCopy != null ?cornerB.clone().add(-fromCopy.getBlockX(),-fromCopy.getBlockY(),-fromCopy.getBlockZ()).toString() : cornerB.toString());
				SerializedBlock ser = new InitialSerializedBlock();
				int pal = 0;
				for(Position pos : new BlockIterator(cornerA, cornerB)) {
					TheMaterial n = pos.getType();
					String ss = ser.serialize(pos, n).getAsString().replace(".", "<!>");
					if(fromCopy!=null)pos.add(-fromCopy.getBlockX(),-fromCopy.getBlockY(),-fromCopy.getBlockZ());
					int sum = sum(pos.getBlockX(),pos.getBlockY(),pos.getBlockZ());
					long key  = pos.getChunkKey();
					
					if(n.getType().name().equals("AIR")||n.getType().name().equals("CAVE_AIR")||n.getType().name().equals("VOID_AIR"))continue; //DON'T SAVE AIR BLOCK
					
					//PALLETE
					String pallete;
					if(!save.containsKey("pallete."+ss)) {
						pallete=(pal++)+"";
						save.put("pallete."+ss, pallete);
					}else
						pallete=(String)save.get("pallete."+ss);
					//BLOCK
					if(save.containsKey(key+".b."+pallete)) {
						List<Integer> ids = (List<Integer>)save.get(key+".b."+pallete);
						ids.add(sum);
					}else {
						List<Integer> ids = new ArrayList<>();
						ids.add(sum);
						save.put(key+".b."+pallete, ids);
					}
				}
				//SERIALIZE
				SchematicData data = new SchematicData();
				for(Entry<String, Object> key : save.entrySet()) {
					if(key.getKey().contains(".b.") && !key.getKey().contains("pallete")) {
						data.set(key.getKey(), Writer.write((List<Integer>)key.getValue()));
					}else
						data.set(key.getKey(), key.getValue());
				}
				WorldSchematic.this.load=data;
				if(callable!=null)
					callable.run(WorldSchematic.this, load);
				load.save();
		}}.runTask();
	}
}
