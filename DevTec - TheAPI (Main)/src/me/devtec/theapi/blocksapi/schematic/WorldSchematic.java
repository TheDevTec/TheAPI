package me.devtec.theapi.blocksapi.schematic;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import me.devtec.theapi.blocksapi.schematic.construct.Schematic;
import me.devtec.theapi.blocksapi.schematic.construct.SchematicCallable;
import me.devtec.theapi.blocksapi.schematic.construct.SchematicSaveCallable;
import me.devtec.theapi.blocksapi.schematic.construct.SerializedBlock;
import me.devtec.theapi.scheduler.Tasker;
import me.devtec.theapi.utils.BlockMathIterator;
import me.devtec.theapi.utils.Position;
import me.devtec.theapi.utils.TheMaterial;
import me.devtec.theapi.utils.datakeeper.Data;
import me.devtec.theapi.utils.json.Json;

public class WorldSchematic implements Schematic {
	private static final String pall="pallete.";
    private static final String split=".b.";
    private static final String broken=".";
    private static final String fix = "<!>";
	
	private static final String end = ".schem";
	private final String name;
	protected Data load;
	
	public WorldSchematic(String name) {
		this.name=name;
	}
	
	public WorldSchematic(VirtualSchematic schem, String name) {
		this.name=name;
		load=new Data(schem.load);
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
		load=new Data();
		load.reload(new File("plugins/TheAPI/Schematic/"+name+end));
		return !load.getKeys().isEmpty();
	}
	
	public Data data() {
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
				Position aa = Position.fromString(load.getString("info.corner.a"));
				Position bb = Position.fromString(load.getString("info.corner.b"));
				aa.setWorld(stand.getWorld());
				boolean st = load.getBoolean("info.standing");
				//PALLETE
				Map<Integer, String> pallete = new HashMap<>();
				for(String key : load.getKeys("pallete"))
					pallete.put(load.getInt(pall+key), key);
				//BLOCKS PER CHUNK: <CHUNK-KEY.BLOCK-SUM>, BLOCK>
				Map<String, String> blocks = new HashMap<>();
				for(String key : load.getKeys()) {
					if(!key.equals("info") && !key.equals("pallete")) {
						long c = Long.parseLong(key);
							for(String values : load.getKeys(key+".b")) {
								int val = Integer.parseInt(values);
								String i = pallete.get(val);
								if(i==null)continue; //invalid entry
								for(Double d : (List<Double>) Json.reader().simpleRead(load.getString(key+split+values)))
									blocks.put(c+fix+d.intValue(), i.replaceAll("\\.",broken));
							}
					}
				}
				pallete.clear();
				//BLOCKS
				Position sett = new Position(aa.getWorld(),0,0,0);
				if(stand!=null && st) {
					for(double[] aaa : new BlockMathIterator(aa, bb)) {
						sett.setX(aaa[0]+stand.getBlockX());
						sett.setY(aaa[1]+stand.getBlockY());
						sett.setZ(aaa[2]+stand.getBlockZ());
						//BLOCK
						int sum = sum(get(aaa[0]), get(aaa[1]), get(aaa[2]));
						long k = (get(aaa[0]) >> 4 & 0xFFFF0000L) << 16L | (get(aaa[0]) >> 4 & 0xFFFFL);
						k |= (get(aaa[2]) >> 4 & 0xFFFF0000L) << 32L | (get(aaa[2]) >> 4 & 0xFFFFL) << 16L;
						String path = new StringBuilder(String.valueOf(k)).append(fix).append(sum).toString();
						String i = blocks.get(path);
						if(i!=null) {
							ser.fromString(i).apply(sett);
						}else if(replaceAir) {
							sett.setAirAndUpdate();
						}
					}
				}else {
					for(double[] aaa : new BlockMathIterator(aa, bb)) {
						sett.setX(aaa[0]);
						sett.setY(aaa[1]);
						sett.setZ(aaa[2]);
						//BLOCK
						int sum = sum(sett.getBlockX(), sett.getBlockY(), sett.getBlockZ());
						String path = new StringBuilder(String.valueOf(sett.getChunkKey())).append(fix).append(sum).toString();
						String i = blocks.get(path);
						if(i!=null) {
							ser.fromString(i).apply(sett);
						}else if(replaceAir) {
							sett.setAirAndUpdate();
						}
					}
				}
				if(callable!=null)
					callable.run(WorldSchematic.this);
		}
			private int get(double x) {
				int floor = (int) x;
				return (double) floor == x ? floor : floor - (int) (Double.doubleToRawLongBits(x) >>> 63);
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
				save.put("info.corner.b", fromCopy != null ? cornerB.clone().add(-fromCopy.getBlockX(),-fromCopy.getBlockY(),-fromCopy.getBlockZ()).toString() : cornerB.toString());
				SerializedBlock ser = new InitialSerializedBlock();
				int pal = 0;
				Position pos = new Position(cornerA.getWorld(),0,0,0);
				for(double[] aaa : new BlockMathIterator(cornerA, cornerB)) {
					pos.setX(aaa[0]);
					pos.setY(aaa[1]);
					pos.setZ(aaa[2]);
					TheMaterial n = pos.getType();
					if(n.getType().isAir())continue; //DON'T SAVE AIR BLOCK
					//PALLETE
					String ss = ser.serialize(pos, n).getAsString().replace(broken, fix);
					if(fromCopy!=null)pos.add(-fromCopy.getBlockX(),-fromCopy.getBlockY(),-fromCopy.getBlockZ());
					int sum = sum(pos.getBlockX(),pos.getBlockY(),pos.getBlockZ());
					String path = pall+ss;
					String pallete = (String)save.get(path);
					if(pallete==null)
						save.put(path, pallete=String.valueOf(pal++));
					//BLOCK
					path = pos.getChunkKey()+split+pallete;
					List<Integer> ids = (List<Integer>) save.get(path);
					if(ids==null)
						save.put(path, ids= new ArrayList<>());
					ids.add(sum);
				}
				//SERIALIZE
				Data data = new Data();
				for(Entry<String, Object> key : save.entrySet()) {
					if(key.getKey().contains(split) && !key.getKey().contains("pallete")) {
						data.set(key.getKey(), Json.writer().simpleWrite((List<Integer>)key.getValue()));
					}else {
						data.set(key.getKey(), key.getValue());
					}
				}
				WorldSchematic.this.load=data;
				if(callable!=null)
					callable.run(WorldSchematic.this, load);
				load.save();
		}}.runTask();
	}
}
