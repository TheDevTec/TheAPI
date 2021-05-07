package me.devtec.theapi.blocksapi.schematic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import me.devtec.theapi.blocksapi.schematic.construct.Schematic;
import me.devtec.theapi.blocksapi.schematic.construct.SchematicCallable;
import me.devtec.theapi.blocksapi.schematic.construct.SchematicSaveCallable;
import me.devtec.theapi.blocksapi.schematic.construct.SerializedBlock;
import me.devtec.theapi.blocksapi.schematic.storage.SchematicData;
import me.devtec.theapi.scheduler.Tasker;
import me.devtec.theapi.utils.BlockMathIterator;
import me.devtec.theapi.utils.Position;
import me.devtec.theapi.utils.TheMaterial;
import me.devtec.theapi.utils.json.Reader;
import me.devtec.theapi.utils.json.Writer;

public class VirtualSchematic implements Schematic {
	private static String air="AIR",air1="CAVE_AIR",air2="VOID_AIR",air3="STRUCTURE_AIR",pall="pallete.",split=".b.",broken=".", fix = "<!>";
	
	protected SchematicData load;
	
	@Override
	public boolean load() {
		return load != null;
	}
	
	public SchematicData data() {
		return load;
	}

	private static int sum(int i, int j, int k) {
		return (j << 8 | (k & 15) << 4 | (i & 15));
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
				boolean st = load.getBoolean("info.standing");
				//PALLETE
				Map<Integer, String> pallete = new HashMap<>();
				for(String key : load.getKeys("pallete"))
					pallete.put(load.getInt(pall+key), key);
				//BLOCKS PER CHUNK: <CHUNK-KEY.BLOCK-SUM>, PALLETE-ID>
				Map<String, Integer> blocks = new HashMap<>();
				for(String key : load.getKeys()) {
					if(!key.equals("info") && !key.equals("pallete")) {
						long c = Long.valueOf(key);
							for(String values : load.getKeys(key+".b")) {
								int val = Integer.valueOf(values);
								if(pallete.get(val)==null)continue; //invalid entry
								for(Double d : (List<Double>)Reader.read(load.getString(key+split+values)))
									blocks.put(c+fix+d.intValue(), val);
							}
					}
				}
				//BLOCKS
				Position sett = new Position(aa.getWorld(),0,0,0);
				if(stand!=null && st) {
					for(double[] aaa : new BlockMathIterator(aa, bb)) {
						sett.setX(aaa[0]+stand.getBlockX());
						sett.setY(aaa[1]+stand.getBlockY());
						sett.setZ(aaa[2]+stand.getBlockZ());
						//BLOCK
						int sum = sum(get(aaa[0]), get(aaa[1]), get(aaa[2]));
						long k = (get(aaa[0]) >> 4 & 0xFFFF0000L) << 16L | (get(aaa[0]) >> 4 & 0xFFFFL) << 0L;
						k |= (get(aaa[2]) >> 4 & 0xFFFF0000L) << 32L | (get(aaa[2]) >> 4 & 0xFFFFL) << 16L;
						String path = k+fix+sum;
						if(blocks.containsKey(path)) {
							ser.fromString(pallete.get(blocks.get(path)).replace(fix,broken)).apply(sett);
						}else if(replaceAir) {
							if(sett.getBukkitType().name().equals(air))continue;
							Object old = sett.getIBlockData();
							sett.setAir();
							Position.updateBlockAt(sett, old);
							Position.updateLightAt(sett);
						}
					}
				}else
					for(double[] aaa : new BlockMathIterator(aa, bb)) {
						sett.setX(aaa[0]);
						sett.setY(aaa[1]);
						sett.setZ(aaa[2]);
						//BLOCK
						int sum = sum(sett.getBlockX(), sett.getBlockY(), sett.getBlockZ());
						String path = sett.getChunkKey()+fix+sum;
						if(blocks.containsKey(path)) {
							ser.fromString(pallete.get(blocks.get(path)).replace(fix,broken)).apply(sett);
						}else if(replaceAir) {
							if(sett.getBukkitType().name().equals(air))continue;
							Object old = sett.getIBlockData();
							sett.setAir();
							Position.updateBlockAt(sett, old);
							Position.updateLightAt(sett);
						}
					}
				if(callable!=null)
					callable.run(VirtualSchematic.this);
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
				save.put("info.corner.b", fromCopy != null ?cornerB.clone().add(-fromCopy.getBlockX(),-fromCopy.getBlockY(),-fromCopy.getBlockZ()).toString() : cornerB.toString());
				SerializedBlock ser = new InitialSerializedBlock();
				int pal = 0;
				Position pos = new Position(cornerA.getWorld(),0,0,0);
				for(double[] aaa : new BlockMathIterator(cornerA, cornerB)) {
					pos.setX(aaa[0]);
					pos.setY(aaa[1]);
					pos.setZ(aaa[2]);
					TheMaterial n = pos.getType();
					String t = n.getType().name();
					if(t.equals(air)||t.equals(air1)||t.equals(air2)||t.equals(air3))continue; //DON'T SAVE AIR BLOCK
					if(fromCopy!=null)pos.add(-fromCopy.getBlockX(),-fromCopy.getBlockY(),-fromCopy.getBlockZ());
					int sum = sum(pos.getBlockX(),pos.getBlockY(),pos.getBlockZ());
					//PALLETE
					String ss = ser.serialize(pos, n).getAsString().replace(broken,fix);
					String path = pall+ss;
					String pallete = (String)save.get(path);
					if(pallete==null)
						save.put(path, pallete=(pal++)+"");
					//BLOCK
					path = pos.getChunkKey()+split+pallete;
					List<Integer> ids = (List<Integer>) save.get(path);
					if(ids==null)
						save.put(path, ids= new ArrayList<>());
					ids.add(sum);
				}
				//SERIALIZE
				SchematicData data = new SchematicData();
				for(Entry<String, Object> key : save.entrySet()) {
					if(key.getKey().contains(split) && !key.getKey().contains("pallete")) {
						data.set(key.getKey(), Writer.write((List<Integer>)key.getValue()));
					}else {
						data.set(key.getKey(), key.getValue());
					}
				}
				VirtualSchematic.this.load=data;
				if(callable!=null)
					callable.run(VirtualSchematic.this, load);
		}}.runTask();
	}
}
