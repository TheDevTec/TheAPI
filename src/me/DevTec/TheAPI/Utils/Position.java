package me.DevTec.TheAPI.Utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import me.DevTec.TheAPI.TheAPI;
import me.DevTec.TheAPI.Utils.NMS.NMSAPI;
import me.DevTec.TheAPI.Utils.Reflections.Ref;
import me.DevTec.TheAPI.Utils.TheAPIUtils.LoaderClass;

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

	public TheMaterial getType() {
		Object c = getNMSChunk();
		return new TheMaterial(Ref.invoke(c, Ref.method(Ref.nms("Chunk"), "getType", Ref.nms("BlockPosition")), Ref.blockPos(x, y, z)));
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

	public String getWorldName() {
		return w;
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
			return new Position(part[0], StringUtils.getDouble(part[1]), StringUtils.getDouble(part[2]),
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
		return square(x)+square(y)+square(z);
	}
	  
	public double distanceSquared(Location location) {
		return square(this.x - location.getX()) + square(this.y - location.getY()) + square(this.z - location.getZ());
	}

	public double distanceSquared(Position position) {
		return square(this.x - position.x) + square(this.y - position.y) + square(this.z - position.z);
	}
	
	private double square(double d) {
		return d*d;
	}

	public Chunk getChunk() {
		return (Chunk)Ref.get(getNMSChunk(), "bukkitChunk");
	}

	private static int wf = StringUtils.getInt(TheAPI.getServerVersion().split("_")[1]);
	
	public Object getNMSChunk() {
		try {
			return Ref.invoke(Ref.get(Ref.cast(Ref.nms("ChunkProviderServer"), Ref.invoke(Ref.world(getWorld()), "getChunkProvider")), "chunkGenerator"),
					Ref.method(Ref.nms("ChunkGenerator"), "getOrCreateChunk", int.class, int.class), getBlockX()>>4, getBlockZ()>>4);
		}catch(Exception er){
			return Ref.handle(Ref.cast(Ref.craft("CraftChunk"), getWorld().getChunkAt(getBlockX()>>4, getBlockZ()>>4)));
		}
	}
	
	private Object o;
	public Object getBlockPosition() {
		if(o==null)o=(wf<=7?Ref.newInstance(old,x, y, z):Ref.blockPos(x, y, z));
		return o;
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

	public long setType(Material with, int data) {
		return set(this, new TheMaterial(with, data));
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
	
	public static void updateBlockAt(Position pos) {
		updateBlockAt(pos, true);
	}
	
	public static void updateBlockAt(Position pos, boolean updateLight) {
		Object cr = pos.getType().getIBlockData();
		NMSAPI.refleshBlock(Ref.world(pos.getWorld()), pos.getBlockPosition(), cr, cr);
		Object packet = Ref.newInstance(Ref.constructor(Ref.nms("PacketPlayOutBlockChange"), Ref.nms("BlockPosition"), Ref.nms("IBlockData")),pos.getBlockPosition(), cr);
		for(Player s : TheAPI.getOnlinePlayers())
			Ref.sendPacket(s, packet);
		if(updateLight)
			Ref.invoke(Ref.invoke(pos.getNMSChunk(), "e"),"a",pos.getBlockPosition());
	}
	
	@SuppressWarnings("deprecation")
	public static long set(Position pos, TheMaterial mat) {
		if(wf<= 7)setOld(pos, mat.getType().getId()); else set(pos, wf>=9, wf>=14, mat);
		return pos.getChunkKey();
	}

	public long getChunkKey() {
		long k = (getBlockX()>>4 & 0xFFFF0000L) << 16L | (getBlockX()>>4 & 0xFFFFL) << 0L;
	    k |= (getBlockZ()>>4 & 0xFFFF0000L) << 32L | (getBlockZ()>>4 & 0xFFFFL) << 16L;
		return k;
	}
	
	public static long set(Location pos, int id, int data) {
		return set(new Position(pos), new TheMaterial(id, data));
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
			sc=Ref.newInstance(aw, (pos.getBlockY()<0 && pos.getBlockY()!=0? -1*pos.getBlockY() : pos.getBlockY()) >> 4, true);
			((Object[])Ref.invoke(c, "getSections"))[(pos.getBlockY()<0 && pos.getBlockY()!=0? -1*pos.getBlockY() : pos.getBlockY()) >> 4]=sc;
		}
		try {
			HashMap<?,?> aw = ((HashMap<?,?>)Ref.get(c, "tileEntities"));
			if(aw.containsKey(pos.getBlockPosition())) {
				Ref.invoke(w, "setTypeAndData", pos.getBlockPosition(),LoaderClass.plugin.air,3);
				aw.remove(pos.getBlockPosition());
		}}catch(Exception kill) {}
		if((int)Ref.invoke(sc, "getData", pos.getBlockX() & 15, pos.getBlockY() & 15, pos.getBlockZ() & 15)!=id) {
			Ref.invoke(sc, "setData", pos.getBlockX() & 15, pos.getBlockY() & 15, pos.getBlockZ() & 15, id);
		}
	}

	private static Constructor<?> aw = Ref.constructor(Ref.nms("ChunkSection"), int.class), old = Ref.constructor(Ref.nms("Position"), double.class, double.class, double.class);
	private static Method a = Ref.method(Ref.nms("DataPaletteBlock"), "setBlock", int.class, int.class, int.class, Ref.nms("IBlockData")),
			setAir=Ref.method(Ref.nms("World"), "setTypeAndData", Ref.nms("BlockPosition"), Ref.nms("IBlockData"), int.class),
			type=Ref.method(Ref.nms("ChunkSection"), "setType", int.class, int.class, int.class, Ref.nms("IBlockData"));
	static {
		if(a==null)
			a=Ref.method(Ref.nms("DataPaletteBlock"), "setBlock", int.class, int.class, int.class, Object.class);
		if(aw==null)
			aw=Ref.constructor(Ref.nms("ChunkSection"), int.class,boolean.class);
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
	private static synchronized void set(Position pos, boolean palet, boolean neww, TheMaterial ma) { //1.8 - 1.16
		TheMaterial material = ma.clone();
		Object c = pos.getNMSChunk();
		Object sc = ((Object[])Ref.invoke(c, "getSections"))[((pos.getBlockY()<0 && pos.getBlockY()!=0? -1*pos.getBlockY() : pos.getBlockY()) >> 4)];
		if(sc==null) {
			if(neww)
				sc=Ref.newInstance(aw, (int)((pos.getBlockY()<0 && pos.getBlockY()!=0? -1*pos.getBlockY() : pos.getBlockY()) >> 4)); //1.14+
			else
				sc=Ref.newInstance(aw, (int)((pos.getBlockY()<0 && pos.getBlockY()!=0? -1*pos.getBlockY() : pos.getBlockY()) >> 4), true); //1.8 - 1.13
			((Object[])Ref.invoke(c, "getSections"))[((pos.getBlockY()<0 && pos.getBlockY()!=0? -1*pos.getBlockY() : pos.getBlockY()) >> 4)]=sc;
		}
		Object cr = material.getIBlockData();
		try {
			HashMap<?,?> aw = ((HashMap<?,?>)Ref.invoke(c, "getTileEntities"));
			if(aw.containsKey(pos.getBlockPosition())) {
				Ref.invoke(Ref.world(pos.getWorld()), setAir, pos.getBlockPosition(),LoaderClass.plugin.air,3);
				aw.remove(pos.getBlockPosition());
		}}catch(Exception kill) {}
			if(palet)
				Ref.invoke(Ref.invoke(sc, "getBlocks"),a, pos.getBlockX() & 15, pos.getBlockY() & 15, pos.getBlockZ() & 15, cr);
			else
			Ref.invoke(sc, type, pos.getBlockX() & 15, pos.getBlockY() & 15, pos.getBlockZ() & 15, cr);
	}
	
	public Position clone() {
		try {
			return (Position)super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
}
