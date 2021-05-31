package me.devtec.theapi.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.utils.nms.NMSAPI;
import me.devtec.theapi.utils.reflections.Ref;

public class Position implements Cloneable {

	public Position() {
	}

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

	public Position(double x, double y, double z) {
		this(x, y, z, 0, 0);
	}

	public Position(double x, double y, double z, float yaw, float pitch) {
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

	public static Position fromString(String stored) {
		try {
			stored = stored.substring(10, stored.length() - 1);
			String[] part = stored.replaceAll(":", ".").split("/");
			return new Position(part[0], StringUtils.getDouble(part[1]), StringUtils.getDouble(part[2]),
					StringUtils.getDouble(part[3]), StringUtils.getFloat(part[4]), StringUtils.getFloat(part[5]));
		} catch (Exception notMat) {
			Location loc = StringUtils.getLocationFromString(stored);
			if (loc != null)
				return new Position(loc);
		}
		return null;
	}

	public static Position fromBlock(Block block) {
		if (block != null)
			return new Position(block.getLocation());
		return null;
	}

	public static Position fromLocation(Location location) {
		if (location != null)
			return new Position(location);
		return null;
	}

	private String w;
	private double x, y, z;
	private float yaw, pitch;

	@Override
	public String toString() {
		return ("[Position:" + w + '/' + x + '/' + y + '/' + z + '/' + yaw + '/' + pitch + ']').replaceAll("\\.", ":");
	}

	public int hashCode() {
		int hashCode = 1;
		hashCode = 31 * hashCode + w.hashCode();
		hashCode = (int) (31 * hashCode + x);
		hashCode = (int) (31 * hashCode + y);
		hashCode = (int) (31 * hashCode + z);
		hashCode = (int) (31 * hashCode + yaw);
		hashCode = (int) (31 * hashCode + pitch);
		return hashCode;
	}

	public Biome getBiome() {
		return getBlock().getBiome();
	}

	public int getData() {
		return TheAPI.isOlderThan(8)?(byte)Ref.invoke(getNMSChunk(), getdata, getBlockX() & 0xF, getBlockY() & 0xF, getBlockZ() & 0xF):getType().getData();
	}

	public Material getBukkitType() {
		return getType().getType();
	}

	private static Method getdata;
	static {
		if(TheAPI.isOlderThan(8))
			getdata=Ref.method(Ref.nms("ChunkSection"), "getData", int.class, int.class, int.class);
	}
	
	public Object getIBlockData() {
		Object sc = ((Object[]) Ref.invoke(c, get))[getBlockY() >> 4];
		if (sc == null)return new TheMaterial(Material.AIR, 0);
		if(TheAPI.isOlderThan(8)) //1.7.10
			return Ref.invoke(sc, getType, getBlockX() & 15, getBlockY() & 15, getBlockZ() & 15);
		return Ref.invoke(sc, getType, getBlockX() & 15, getBlockY() & 15, getBlockZ() & 15);
	}
	
	private static Method getType = Ref.method(Ref.nms("ChunkSection"), "getType", int.class, int.class, int.class);
	
	public TheMaterial getType() {
		Object sc = ((Object[]) Ref.invoke(c, get))[getBlockY() >> 4];
		if (sc == null)return new TheMaterial(Material.AIR, 0);
		if(TheAPI.isOlderThan(8)) //1.7.10
			return TheMaterial.fromData(Ref.invoke(sc, getType, getBlockX() & 15, getBlockY() & 15, getBlockZ() & 15), (byte)Ref.invoke(sc, getdata, getBlockX() & 15, getBlockY() & 15, getBlockZ() & 15));
		return TheMaterial.fromData(Ref.invoke(sc, getType, getBlockX() & 15, getBlockY() & 15, getBlockZ() & 15));
	}

	public Position subtract(double x, double y, double z) {
		this.x -= x;
		this.y -= y;
		this.z -= z;
		return this;
	}

	public Position subtract(Position position) {
		this.x -= position.getX();
		this.y -= position.getY();
		this.z -= position.getZ();
		return this;
	}

	public Position subtract(Location location) {
		this.x -= location.getX();
		this.y -= location.getY();
		this.z -= location.getZ();
		return this;
	}

	public String getWorldName() {
		return w;
	}

	public Position setWorld(World world) {
		w = world.getName();
		return this;
	}

	public Position setX(double x) {
		this.x = x;
		return this;
	}

	public Position setY(double y) {
		this.y = y;
		return this;
	}

	public Position setZ(double z) {
		this.z = z;
		return this;
	}

	public Position setYaw(float yaw) {
		this.yaw = yaw;
		return this;
	}

	public Position setPitch(float pitch) {
		this.pitch = pitch;
		return this;
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
		return square(x) + square(y) + square(z);
	}

	public double distanceSquared(Location location) {
		return square(this.x - location.getX()) + square(this.y - location.getY()) + square(this.z - location.getZ());
	}

	public double distanceSquared(Position position) {
		return square(this.x - position.x) + square(this.y - position.y) + square(this.z - position.z);
	}

	private double square(double d) {
		return d * d;
	}

	public Chunk getChunk() {
		return (Chunk) Ref.get(getNMSChunk(), "bukkitChunk");
	}

	private static int wf = StringUtils.getInt(TheAPI.getServerVersion().split("_")[1]);
	private static Method handle = Ref.method(Ref.craft("CraftChunk"), "getHandle");
	
	public Object getNMSChunk() {
		try {
			return Ref.invoke(getWorld().getChunkAt(getBlockX() >> 4, getBlockZ() >> 4), handle);
		} catch (Exception er) {
			return null;
		}
	}
	
	public Object getBlockPosition() {
		return (wf <= 7 ? Ref.newInstance(old, getBlockX(), getBlockY(), getBlockZ()) : Ref.blockPos(getBlockX(), getBlockY(), getBlockZ()));
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
		int floor = (int) x;
		return (double) floor == x ? floor : floor - (int) (Double.doubleToRawLongBits(x) >>> 63);
	}

	public int getBlockY() {
		int floor = (int) y;
		return (double) floor == y ? floor : floor - (int) (Double.doubleToRawLongBits(y) >>> 63);
	}

	public int getBlockZ() {
		int floor = (int) z;
		return (double) floor == z ? floor : floor - (int) (Double.doubleToRawLongBits(z) >>> 63);
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
		return setType(new TheMaterial(with));
	}

	public long setType(Material with, int data) {
		return setType(new TheMaterial(with, data));
	}

	public long setType(TheMaterial with) {
		return set(this, with);
	}

	public void setTypeAndUpdate(Material with) {
		setTypeAndUpdate(new TheMaterial(with));
	}

	public void setTypeAndUpdate(Material with, int data) {
		setTypeAndUpdate(new TheMaterial(with, data));
	}
	
	public void setTypeAndUpdate(TheMaterial with) {
		setType(with);
		updateBlockAt(this);
		updateLightAt(this);
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

	private static Constructor<?> c;
	private static int t;
	static {
		c=Ref.constructor(Ref.nms("PacketPlayOutBlockChange"), Ref.nms("IBlockAccess"), Ref.nms("BlockPosition"));
		if(c==null) {
			c=Ref.constructor(Ref.nms("PacketPlayOutBlockChange"), Ref.nms("World"), Ref.nms("BlockPosition"));
			if(c==null) {
				c=Ref.constructor(Ref.nms("PacketPlayOutBlockChange"), int.class, int.class, int.class, Ref.nms("World"));
				t=1;
			}
		}
	}
	
	public static void updateBlockAt(Position pos) {
		NMSAPI.refleshBlock(pos);
		Object packet = t==0?Ref.newInstance(c, Ref.world(pos.getWorld()), pos.getBlockPosition()):Ref.newInstance(c, pos.getBlockX(), pos.getBlockY(), pos.getBlockZ(), Ref.world(pos.getWorld()));
		for (Player p : TheAPI.getOnlinePlayers())
			Ref.sendPacket(p, packet);
	}

	private static boolean aww = TheAPI.isOlderThan(8);
	private static Method updateLight = Ref.method(Ref.nms("LightEngine"), "a", Ref.nms("BlockPosition")),getEngine;
	static {
		if(updateLight==null) {
			updateLight = Ref.method(Ref.nms("Chunk"), "initLighting");
		}else {
			getEngine=Ref.method(Ref.nms("Chunk"), "e");
		}
	}

	public static void updateLightAt(Position pos) {
		if (aww)
			Ref.invoke(pos.getNMSChunk(), updateLight);
		else
			Ref.invoke(Ref.invoke(pos.getNMSChunk(), getEngine), updateLight, pos.getBlockPosition());
	}

	public static long set(Position pos, TheMaterial mat) {
		if (wf <= 7)
			setOld(pos, mat.getType().getId(), mat.getData());
		else
			set(pos, wf >= 9, wf >= 14, mat.getIBlockData());
		return pos.getChunkKey();
	}
	
	public long getChunkKey() {
		long k = (getBlockX() >> 4 & 0xFFFF0000L) << 16L | (getBlockX() >> 4 & 0xFFFFL) << 0L;
		k |= (getBlockZ() >> 4 & 0xFFFF0000L) << 32L | (getBlockZ() >> 4 & 0xFFFFL) << 16L;
		return k;
	}

	public void setState(BlockState state) {
		setState(this, state);
	}

	public void setBlockData(BlockData state) {
		setBlockData(this, state);
	}

	public void setStateAndUpdate(BlockState state) {
		setState(this, state);
		updateBlockAt(this);
	}

	public void setBlockDataAndUpdate(BlockData state) {
		setBlockData(this, state);
		updateBlockAt(this);
	}

	private static Object air = new TheMaterial(Material.AIR).getIBlockData();
	public long setAir() {
		if (wf <= 7)
			setOld(this, 0,0);
		else
			set(this, wf >= 9, wf >= 14, air);
		return getChunkKey();
	}

	public void setAirAndUpdate() {
		setAir();
		updateBlockAt(this);
	}
	
	public static long set(Location pos, int id, int data) {
		return set(new Position(pos), new TheMaterial(id, data));
	}

	/**
	 * 
	 * @param pos Location
	 * @param id  int id of Material
	 * @return long ChunkKey
	 */
	@SuppressWarnings("unchecked")
	private static synchronized void setOld(Position pos, int id, int data) { // Uknown - 1.7.10
		Object c = pos.getNMSChunk();
		Object sc = ((Object[]) Ref.invoke(c, get))[pos.getBlockY() >> 4];
		if (sc == null) {
			sc = Ref.newInstance(aw,pos.getBlockY() >> 4 << 4, true);
			((Object[]) Ref.invoke(c, "getSections"))[pos.getBlockY() >> 4] = sc;
		}
		Object ww = Ref.world(pos.getWorld());
		Object p = pos.getBlockPosition();
		//REMOVE TILE ENTITY
		for(Iterator<?> r = ((Collection<?>)Ref.get(ww, "tileEntityList")).iterator(); r.hasNext();) {
			if(Ref.get(r.next(), "position").equals(p)) {
				r.remove();
				break;
			}
		}
		for(Iterator<?> r = ((Collection<?>)Ref.get(ww, aa)).iterator(); r.hasNext();) {
			if(Ref.get(r.next(), "position").equals(p)) {
				r.remove();
				break;
			}
		}
		for(Iterator<?> r = ((Collection<?>)Ref.get(ww, bb)).iterator(); r.hasNext();) {
			if(Ref.get(r.next(), "position").equals(p)) {
				r.remove();
				break;
			}
		}
		//CHANGE BLOCK IN CHUNKSECTION
		Ref.invoke(sc, setId, pos.getBlockX() & 0xF, pos.getBlockY() & 0xF, pos.getBlockZ() & 0xF, id);
		Ref.invoke(sc, setData, pos.getBlockX() & 0xF, pos.getBlockY() & 0xF, pos.getBlockZ() & 0xF, data);
		//ADD TILE ENTITY
		Object tt = Ref.invoke(c, "getType", pos.getBlockX() & 0xF, pos.getBlockY(), pos.getBlockZ() & 0xF);
		if(cont.isInstance(tt)) {
			tt=Ref.invoke(tt, tile, ww, 0);
			Ref.set(tt, "x", pos.getBlockX());
			Ref.set(tt, "y", pos.getBlockY());
			Ref.set(tt, "z", pos.getBlockZ());
			Ref.invoke(tt, "t");
			Ref.set(tt, "h", tt);
			Ref.set(tt, "world", ww);
			((Collection<Object>)Ref.get(ww, "tileEntityList")).add(tt);
			((Collection<Object>)Ref.get(ww, aa)).add(tt);
			((Collection<Object>)Ref.get(ww, bb)).add(tt);
		}
	}

	private static Constructor<?> aw = Ref.constructor(Ref.nms("ChunkSection"), int.class),
			old = Ref.constructor(Ref.nms("ChunkPosition"), double.class, double.class, double.class);
	private static Method a, get = Ref.method(Ref.nms("Chunk"), "getSections"), setId, setData;
	static {
		a = Ref.method(Ref.nms("ChunkSection"), "setType", int.class, int.class, int.class, Ref.nms("IBlockData"), boolean.class);
		if(a==null)
			a = Ref.method(Ref.nms("ChunkSection"), "setType", int.class, int.class, int.class, Ref.nms("IBlockData"));
		if (aw == null)
			aw = Ref.constructor(Ref.nms("ChunkSection"), int.class, boolean.class);
		if(TheAPI.isNewerThan(8)) {
			setId=Ref.method(Ref.nms("ChunkSection"), "setTypeId", int.class, int.class, int.class, Ref.nms("Block"));
			setData=Ref.method(Ref.nms("ChunkSection"), "setData", int.class, int.class, int.class, int.class);
		}
	}
	private static String aa = TheAPI.isNewerThan(7)?"h":"a", bb = TheAPI.isNewerThan(7)?"h":"b";
	private static Method getBlock = Ref.method(Ref.craft("util.CraftMagicNumbers"), "getBlock", Material.class),
			fromLegacyData=Ref.method(Ref.nms("Block"), "fromLegacyData", int.class);
	
	public static void setBlockData(Position pos, BlockData data) {
		if(data==null||!TheAPI.isNewVersion() || pos == null)return;
		set(pos, true, wf >= 14, Ref.invoke(data,"getState"));
	}
	
	public static void setState(Position pos, BlockState state) {
		if(state==null || pos == null)return;
		set(pos, true, wf >= 14, Ref.invoke(Ref.invokeNulled(getBlock, state.getType()),fromLegacyData,(int)state.getRawData()));
	}
	
	/**
	 * 
	 * @param pos   Location
	 * @param palet Is server version newer than 1.8? 1.9+
	 * @param neww  Is server version newer than 1.13? 1.14+
	 * @param id    int id of Material
	 * @param data  int data of Material
	 * @return long ChunkKey
	 */
	@SuppressWarnings("unchecked")
	public static synchronized void set(Position pos, boolean palet, boolean neww, Object cr) { // 1.8 - 1.16
		if(pos==null||cr==null)return;
		Object c = pos.getNMSChunk();
		int y = pos.getBlockY();
		//CHECK IF CHUNKSECTION EXISTS
		Object sc = ((Object[]) Ref.invoke(c, get))[y >> 4];
		if (sc == null) {
			//CREATE NEW CHUNKSECTION
			sc = neww?Ref.newInstance(aw, y >> 4 << 4):Ref.newInstance(aw, y >> 4 << 4, true);
			((Object[]) Ref.invoke(c, get))[y >> 4] = sc;
		}
		
		Object p = pos.getBlockPosition();
		Object ww = Ref.world(pos.getWorld());
		//REMOVE TILE ENTITY FROM CHUNK
		((Map<?,?>)Ref.get(c, "tileEntities")).remove(p);
		if (palet) {
			//CHANGE BLOCK IN CHUNKSECTION (PALLETE)
			Ref.invoke(sc, a, pos.getBlockX() & 0xF, y & 0xF, pos.getBlockZ() & 0xF, cr, false);
		}else {
			//CHANGE BLOCK IN CHUNKSECTION (PALLETE)
			Ref.invoke(sc, a, pos.getBlockX() & 0xF, y & 0xF, pos.getBlockZ() & 0xF, cr);
		}
		//ADD TILE ENTITY
		Object tt = cr.getClass().isAssignableFrom(Ref.nms("Block"))?cr:Ref.invoke(cr, "getBlock");
		if(ver!=-1 && cont.isInstance(tt)) {
			tt=ver==0?Ref.invoke(tt, tile, ww):Ref.invoke(tt, tile, ww, 0);
			((Map<Object, Object>)Ref.get(c, "tileEntities")).put(p, tt);
		}
		if(cont.isInstance(tt)) {
			tt=Ref.invoke(tt, tile, ww, 0);
			((Map<Object, Object>)Ref.get(c, "tileEntities")).put(p, tt);
		}
		setup(ww,p,cr,tt);
	}
	
	private static void setup(Object ww, Object p, Object cr, Object tt) {
		Ref.set(tt, "world", ww);
		Ref.set(tt, "position", p);
		if(TheAPI.isNewVersion())
			Ref.set(tt, TheAPI.isNewerThan(13)?"c":"f", cr);
		else
		Ref.set(tt, "e", Ref.invoke(cr, "getBlock"));
		Ref.set(tt, TheAPI.isNewerThan(13)?"f":"d", false);
		Ref.sendPacket(TheAPI.getOnlinePlayers(), Ref.invoke(tt, "getUpdatePacket"));
	}
	
	static int ver = 0;
	private static Method tile;
	private static Class<?> cont = Ref.nms("ITileEntity") == null ? Ref.nms("IContainer") : Ref.nms("ITileEntity");
	static {
		if(TheAPI.isNewerThan(8)) {
			tile = Ref.method(Ref.nms("ITileEntity"), "createTile", Ref.nms("IBlockAccess"));
			if(tile==null) {
				tile = Ref.method(Ref.nms("ITileEntity"), "a", Ref.nms("World"), int.class);
				if(tile==null) {
					tile = Ref.method(Ref.nms("ITileEntity"), "a", Ref.nms("IBlockAccess"));
				}else ver = 1;
			}
		}else {
			ver=-1;
			tile = Ref.method(Ref.nms("IContainer"), "a", Ref.nms("World"), int.class);
		}
	}

	public Position clone() {
		return new Position(w, x, y, z, yaw, pitch);
	}
}
