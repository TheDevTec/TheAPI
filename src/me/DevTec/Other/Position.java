package me.DevTec.Other;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

import me.DevTec.TheAPI;

public class Position implements Cloneable {
	public Position() {}

	public Position(World world) {
		w = world.getName();
	}

	public Position(String world) {
		w = world;
	}

	public Position(World world, double x, double y, double z) {
		this(world, x, y, z, 0, 0);
	}

	public Position(World world, double x, double y, double z, float yaw, float pitch) {
		w = world.getName();
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public Position(String world, double x, double y, double z) {
		this(world, x, y, z, 0, 0);
	}

	public Position(String world, double x, double y, double z, float yaw, float pitch) {
		w = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public Position(Location location) {
		w = location.getWorld().getName();
		this.x = location.getX();
		this.y = location.getY();
		this.z = location.getZ();
		this.yaw = location.getYaw();
		this.pitch = location.getPitch();
	}

	public Position(Block b) {
		this(b.getLocation());
	}

	private String w;
	private double x, y, z;
	private float yaw, pitch;

	@Override
	public String toString() {
		return ("[Position:" + w + "/" + x + "/" + y + "/" + z + "/" + yaw + "/" + pitch + "]").replace(".", ":");
	}

	public Biome getBiome() {
		return getBlock().getBiome();
	}

	public int getData() {
		return getType().getData();
	}

	public Material getBukkitType() {
		return getType().getType();
	}

	@SuppressWarnings("deprecation")
	public TheMaterial getType() {
		Block b = getBlock();
		return new TheMaterial(b.getType(), b.getData());
	}

	public Position subtract(double x, double y, double z) {
		this.x-= x; this.y-= y; this.z-= z;
		return this;
	}

	public Position subtract(Position position) {
		this.x-=position.getX(); this.y-=position.getY(); this.z-=position.getZ();
		yaw-=position.getYaw(); pitch-=position.getPitch();
		return this;
	}

	public Position subtract(Location location) {
		this.x-=location.getX(); this.y-=location.getY(); this.z-=location.getZ();
		yaw-=location.getYaw(); pitch-=location.getPitch();
		return this;
	}

	public void setWorld(World world) {
		w = world.getName();
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public static Position fromString(String stored) {
		if (stored.startsWith("[Position:")) {
			stored = stored.substring(0, stored.length() - 1).replaceFirst("\\[Position:", "");
			String[] part = stored.replace(":", ".").split("/");
			return new Position(Bukkit.getWorld(part[0]), StringUtils.getDouble(part[1]), StringUtils.getDouble(part[2]),
					StringUtils.getDouble(part[3]), StringUtils.getFloat(part[4]), StringUtils.getFloat(part[5]));
		}
		Location loc = StringUtils.getLocationFromString(stored);
		if(loc!=null)return new Position(loc);
		return null;
	}

	public double distance(Location location) {
		return Math.sqrt(distanceSquared(location));
	}

	public double distance(Position position) {
		return Math.sqrt(distanceSquared(position));
	}
	
	public Position multiply(double m) {
		x *= m;
		y *= m;
		z *= m;
	return this;
	}
		  
	 public Position zero() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
		return this;
	}
	
	public double length() {
		return Math.sqrt(lengthSquared());
	}
		  
	public double lengthSquared() {
		return x*x + y*y + z*z;
	}
	  
	public double distanceSquared(Location location) {
		return (x - location.getX()*x - location.getX()) + (y - location.getY()*y - location.getY()) + (z - location.getZ()*z - location.getZ());
	}

	public double distanceSquared(Position position) {
		return (x - position.getX()*x - position.getX()) + (y - position.getY()*y - position.getY()) + (z - position.getZ()*z - position.getZ());
	}
	
	public synchronized Chunk getChunk() {
		return (Chunk)Ref.cast(Ref.craft("CraftChunk"), Ref.get(getNMSChunk(), "bukkitChunk"));
	}
	
	private static int wf = StringUtils.getInt(TheAPI.getServerVersion().split("_")[1]);
	public Object getNMSChunk() {
		Object w = Ref.world( getWorld());
		return !(wf>=9)?Ref.invoke(Ref.get(w, "chunkProviderServer"),Ref.method(Ref.get(w, "chunkProviderServer").getClass(),"getOrCreateChunk",int.class,int.class), getBlockX()>>4, getBlockZ()>>4):(wf>=14?Ref.invoke(Ref.cast(Ref.nms("ChunkProviderServer"), Ref.invoke(w, "getChunkProvider")), "getChunkAt", getBlockX()>>4, getBlockZ()>>4, true):Ref.invoke(Ref.cast(Ref.nms("ChunkProviderServer"), Ref.invoke(w, "getChunkProvider")), "getOrLoadChunkAt", getBlockX()>>4, getBlockZ()>>4));
	}

	public ChunkSnapshot getChunkSnapshot() {
		return getChunk().getChunkSnapshot();
	}

	public Block getBlock() {
		return getWorld().getBlockAt(getBlockX(), getBlockY(), getBlockZ());
	}

	public World getWorld() {
		return Bukkit.getWorld(w);
	}

	public Position add(double x, double y, double z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}

	public Position add(Position position) {
		this.x += position.getX();
		this.y += position.getY();
		this.z += position.getZ();
		return this;
	}

	public Position add(Location location) {
		this.x += location.getX();
		this.y += location.getY();
		this.z += location.getZ();
		return this;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public int getBlockX() {
		return (int) x;
	}

	public int getBlockY() {
		return (int) y;
	}

	public int getBlockZ() {
		return (int) z;
	}

	public float getYaw() {
		return yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public Location toLocation() {
		return new Location(Bukkit.getWorld(w), x, y, z, yaw, pitch);
	}

	public long setType(Material with) {
		return setType(with, 0);
	}

	public long setType(TheMaterial with) {
		return setType(with.getType(), with.getData());
	}

	@SuppressWarnings("deprecation")
	public long setType(Material with, int data) {
		return set(this, with.getId(), data);
	}

	@Override
	public boolean equals(Object a) {
		if (a instanceof Position) {
			Position s = (Position) a;
			return w.equals(s.getWorld().getName()) && s.getX() == x && s.getY() == y && s.getZ() == z
					&& s.getPitch() == pitch && s.getYaw() == yaw;
		}
		if (a instanceof Location) {
			Location s = (Location) a;
			return w.equals(s.getWorld().getName()) && s.getX() == x && s.getY() == y && s.getZ() == z
					&& s.getPitch() == pitch && s.getYaw() == yaw;
		}
		return false;
	}
	
	public static long set(Position pos, int id, int data) {
		int w = StringUtils.getInt(TheAPI.getServerVersion().split("_")[1]);
		if(w<= 7)setOld(pos, id); else set(pos, w>=9, w>=14, id, data);
		return pos.getChunkKey();
	}

	public long getChunkKey() {
		long k = (getBlockX()>>4 & 0xFFFF0000L) << 16L | (getBlockX()>>4 & 0xFFFFL) << 0L;
	    k |= (getBlockZ()>>4 & 0xFFFF0000L) << 32L | (getBlockZ()>>4 & 0xFFFFL) << 16L;
		return k;
	}
	
	public static long set(Location pos, int id, int data) {
		return set(new Position(pos), id, data);
	}
	
	/**
	 * 
	 * @param pos Location
	 * @param id int id of Material
	 * @return long ChunkKey
	 */
	private static synchronized void setOld(Position pos, int id) { //Uknown - 1.7.10
		Object w = Ref.world(pos.getWorld());
		Object c = pos.getNMSChunk();
		Object sc = ((Object[])Ref.invoke(c, "getSections"))[(pos.getBlockY()<0 && pos.getBlockY()!=0? -1*pos.getBlockY() : pos.getBlockY()) >> 4];
		if(sc==null) {
				sc=Ref.createByClass("ChunkSection", (pos.getBlockY()<0 && pos.getBlockY()!=0? -1*pos.getBlockY() : pos.getBlockY()) >> 4, true);
			((Object[])Ref.invoke(c, "getSections"))[(pos.getBlockY()<0 && pos.getBlockY()!=0? -1*pos.getBlockY() : pos.getBlockY()) >> 4]=sc;
		}
		try {
			HashMap<?,?> aw = ((HashMap<?,?>)Ref.get(c, "tileEntities"));
			if(aw.containsKey(Ref.blockPos(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ()))) {
				Ref.invoke(w, "setTypeAndData", Ref.createNms("ChunkPosition",pos.getBlockX(), pos.getBlockY(), pos.getBlockZ()),
						Ref.invoke(Ref.get(null, Ref.field(Ref.nms("Block"), "AIR")), "getBlockData"),3);
				aw.remove(Ref.createNms("ChunkPosition",pos.getBlockX(), pos.getBlockY(), pos.getBlockZ()));
		}}catch(Exception kill) {}
		if((int)Ref.invoke(sc, "getData", pos.getBlockX() & 15, pos.getBlockY() & 15, pos.getBlockZ() & 15)!=id) {
			Ref.invoke(sc, "setData", pos.getBlockX() & 15, pos.getBlockY() & 15, pos.getBlockZ() & 15, id);
		}
	}
	
	/**
	 * 
	 * @param pos Location
	 * @param palet Is server version newer than 1.8? 1.9+
	 * @param neww Is server version newer than 1.13? 1.14+
	 * @param id int id of Material
	 * @param data int data of Material
	 * @return long ChunkKey
	 */
	private static synchronized void set(Position pos, boolean palet, boolean neww, int id, int data) { //1.8 - 1.16
		if(id==31||id==175)++data;
		Object c = pos.getNMSChunk();
		Object sc = ((Object[])Ref.invoke(c, "getSections"))[(pos.getBlockY()<0 && pos.getBlockY()!=0? -1*pos.getBlockY() : pos.getBlockY()) >> 4];
		if(sc==null) {
			if(neww)
				sc=Ref.newInstance(Ref.constructor(Ref.nms("ChunkSection"), int.class), (int)((pos.getBlockY()<0 && pos.getBlockY()!=0? -1*pos.getBlockY() : pos.getBlockY()) >> 4)); //1.14+
			else
				sc=Ref.newInstance(Ref.constructor(Ref.nms("ChunkSection"), int.class,boolean.class), (int)((pos.getBlockY()<0 && pos.getBlockY()!=0? -1*pos.getBlockY() : pos.getBlockY()) >> 4), true); //1.8 - 1.13
			((Object[])Ref.invoke(c, "getSections"))[(pos.getBlockY()<0 && pos.getBlockY()!=0? -1*pos.getBlockY() : pos.getBlockY()) >> 4]=sc;
		}
		Object cr = Ref.invoke(null, Ref.method(Ref.nms("Block"), "getByCombinedId", int.class), id+(data<<12));
		try {
			HashMap<?,?> aw = ((HashMap<?,?>)Ref.invoke(c, "getTileEntities"));
			if(aw.containsKey(Ref.blockPos(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ()))) {
				Ref.invoke(Ref.world(pos.getWorld()), "setTypeAndData", Ref.blockPos(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ()),
						Ref.invoke(Ref.get(null, Ref.field(Ref.nms("Block"), "AIR")), "getBlockData"),3);
				aw.remove(Ref.blockPos(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ()));
		}}catch(Exception kill) {}
			if(palet)
				Ref.invoke(Ref.invoke(sc, "getBlocks"),Ref.method(sc.getClass(), "setBlock", int.class, int.class, int.class, Ref.nms("IBlockData")), pos.getBlockX() & 15, pos.getBlockY() & 15, pos.getBlockZ() & 15, cr);
			else
			Ref.invoke(sc, Ref.method(sc.getClass(), "setType", int.class, int.class, int.class, Ref.nms("IBlockData")), pos.getBlockX() & 15, pos.getBlockY() & 15, pos.getBlockZ() & 15, cr);
			((Object[])Ref.invoke(c, "getSections"))[(pos.getBlockY()<0 && pos.getBlockY()!=0? -1*pos.getBlockY() : pos.getBlockY()) >> 4]=sc;
	}
	
	public Position clone() {
		try {
			return (Position)super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
}
