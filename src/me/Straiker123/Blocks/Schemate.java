package me.Straiker123.Blocks;

import me.Straiker123.ConfigAPI;
import me.Straiker123.Position;
import me.Straiker123.TheAPI;
import me.Straiker123.Scheduler.Tasker;

public class Schemate {
	private final String s;
	private final ConfigAPI c;
	private Schema schem;
	public Schemate(String name) {
		s=name;
		 c =new ConfigAPI("TheAPI/Schematic", s);
		 c.create();
	}
	
	public String getName() {
		return s;
	}

	public Schema load() {
		return load(null);
	}
	
	public Schema load(Runnable onFinish) {
		if(schem==null)schem=new Schema(onFinish,this);
		return schem;
	}
	
	public void save(Position fromCopy, Position a, Position b) {
		save(fromCopy, a, b,null);
	}
	
	public ConfigAPI getFile() {
		return c;
	}
	
	public boolean isSetStandingPosition() {
		return c.getBoolean("info.standing");
	}
	
	public float getBlocks() {
		return (float)c.get("info.blocks");
	}
	
	public Position[] getCorners() {
		String[] s= c.getString("info.corners").split("/!/");
		return new Position[] {Position.fromString(s[0]),Position.fromString(s[1])};
	}
	
	public void save(Position fromCopy, Position a, Position b, Runnable onFinish) {
		for(String key : c.getKeys(false))c.remove(key);
		c.save();
		int xMin = Math.min(a.getBlockX(), b.getBlockX());
		 int yMin = Math.min(a.getBlockY(), b.getBlockY());
		 int zMin = Math.min(a.getBlockZ(), b.getBlockZ());
		 int sizeX = Math.abs(Math.max(a.getBlockX(), b.getBlockX()) - xMin) + 1;
		 int sizeY = Math.abs(Math.max(a.getBlockY(), b.getBlockY()) - yMin) + 1;
		 int sizeZ = Math.abs(Math.max(a.getBlockZ(), b.getBlockZ()) - zMin) + 1;
		new Tasker() {
			boolean run = false;
			boolean end = false;
			int x= 0, y= 0, z = 0;
			int id = 0;
			public void run() {
				for(int i = 0; i < 5000; ++i) {
					if(x < sizeX && y < sizeY && z < sizeZ) {
						Position pos = new Position(a.getWorld(), xMin + x, yMin + y, zMin + z);
						String save = new BlockSave(pos).toString();
						if(fromCopy!=null)
						pos.add(-fromCopy.getBlockX(),-fromCopy.getBlockY(),-fromCopy.getBlockZ());
						c.set(id+"",pos.toString()+"/!!!/"+save);
						++id;
						if (++x >= sizeX) {
							x = 0;
							if (++y >= sizeY) {
								y = 0;
								++z;
							}}}else {
								end=true;
								break;
							}
					}
				if(end && !run) {
					run=true;
						cancel();
						c.set("info.standing", fromCopy!=null); //path info is protected!
						c.set("info.corners", a.toString()+"/!/"+b.toString()); //path info is protected!
						c.set("info.blocks", (""+TheAPI.getBlocksAPI().count(a, b)).replaceFirst("\\.0", "")); //path info is protected!
						c.save();
						if(onFinish!=null)
							onFinish.run();
					}
			}
		}.repeatingAsync(0,1);
	}

	public void delete() {
		schem=null;
		c.delete();
	}
}
