package me.devtec.theapi.bukkit.game;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import me.devtec.shared.Ref;
import me.devtec.shared.json.Json;
import me.devtec.theapi.bukkit.BukkitLoader;

public class Position implements Cloneable {
	private String world;
	private double x;
	private double y;
	private double z;
	private float yaw;
	private float pitch;

	private Object cachedChunk;

	public Position() {
	}

	public Position(String world) {
		this.world = world;
	}

	public Position(World world) {
		this(world.getName());
	}

	public Position(World world, double x, double y, double z) {
		this(world, x, y, z, 0, 0);
	}

	public Position(World world, double x, double y, double z, float yaw, float pitch) {
		this(world.getName(), x, y, z, yaw, pitch);
	}

	public Position(String world, double x, double y, double z) {
		this(world, x, y, z, 0, 0);
	}

	public Position(String world, double x, double y, double z, float yaw, float pitch) {
		this(x, y, z, yaw, pitch);
		this.world = world;
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
		world = location.getWorld().getName();
		x = location.getX();
		y = location.getY();
		z = location.getZ();
		yaw = location.getYaw();
		pitch = location.getPitch();
	}

	public Position(Block b) {
		this(b.getLocation());
	}

	public Position(Entity b) {
		this(b.getLocation());
	}

	public Position(Position cloneable) {
		world = cloneable.getWorldName();
		x = cloneable.getX();
		y = cloneable.getY();
		z = cloneable.getZ();
		yaw = cloneable.getYaw();
		pitch = cloneable.getPitch();
		cachedChunk = cloneable.cachedChunk;
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

	public static Position fromEntity(Entity entity) {
		if (entity != null)
			return new Position(entity);
		return null;
	}

	public Biome getBiome() {
		return getBlock().getBiome();
	}

	public int getData() {
		return Ref.isOlderThan(8) ? BukkitLoader.getNmsProvider().getData(getNMSChunk(), getBlockX(), getBlockY(), getBlockZ()) : (int) getType().getItemData();
	}

	public Material getBukkitType() {
		return getType().getType();
	}

	public Object getIBlockData() {
		return BukkitLoader.getNmsProvider().getBlock(getNMSChunk(), getBlockX(), getBlockY(), getBlockZ());
	}

	public BlockDataStorage getType() {
		BlockDataStorage storage = BlockDataStorage.fromData(getIBlockData()).setItemData(BukkitLoader.getNmsProvider().getData(getNMSChunk(), getBlockX(), getBlockY(), getBlockZ()));
		if (BukkitLoader.getNmsProvider().isTileEntity(getNMSChunk(), getBlockX(), getBlockY(), getBlockZ()))
			storage.setNBT(BukkitLoader.getNmsProvider().getNBTOfTile(getNMSChunk(), getBlockX(), getBlockY(), getBlockZ()));
		return storage;
	}

	public Position subtract(double x, double y, double z) {
		return add(-x, -y, -z);
	}

	public Position subtract(Position position) {
		return add(-position.getX(), -position.getY(), -position.getZ());
	}

	public Position subtract(Location location) {
		return add(-location.getX(), -location.getY(), -location.getZ());
	}

	public String getWorldName() {
		return world;
	}

	public Position setWorld(World world) {
		if (!world.getName().equals(this.world)) {
			this.world = world.getName();
			cachedChunk = null;
		}
		return this;
	}

	public Position setWorld(String worldName) {
		if (worldName == null ? world != null : !worldName.equals(world)) {
			world = worldName;
			cachedChunk = null;
		}
		return this;
	}

	public Position setX(double x) {
		int prevChunkX = (int) this.x >> 4;
		this.x = x;
		if (prevChunkX != (int) this.x >> 4)
			cachedChunk = null;
		return this;
	}

	public Position setY(double y) {
		this.y = y;
		return this;
	}

	public Position setZ(double z) {
		int prevChunkZ = (int) this.z >> 4;
		this.z = z;
		if (prevChunkZ != (int) this.z >> 4)
			cachedChunk = null;
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
		return Math.sqrt(this.distanceSquared(location));
	}

	public double distance(Position position) {
		return Math.sqrt(this.distanceSquared(position));
	}

	public Position multiply(double m) {
		x *= m;
		y *= m;
		z *= m;
		cachedChunk = null;
		return this;
	}

	public Position zero() {
		x = 0;
		y = 0;
		z = 0;
		cachedChunk = null;
		return this;
	}

	public double length() {
		return Math.sqrt(lengthSquared());
	}

	public double lengthSquared() {
		return square(x) + square(y) + square(z);
	}

	public double distanceSquared(Location location) {
		return square(x - location.getX()) + square(y - location.getY()) + square(z - location.getZ());
	}

	public double distanceSquared(Position position) {
		return square(x - position.x) + square(y - position.y) + square(z - position.z);
	}

	private double square(double d) {
		return d * d;
	}

	public Chunk getChunk() {
		if (Ref.isNewerThan(12))
			return getWorld().getChunkAt(getBlockX() >> 4, getBlockZ() >> 4);
		return BukkitLoader.getNmsProvider().toBukkitChunk(getNMSChunk());
	}

	public Object getNMSChunk() {
		if (cachedChunk == null)
			cachedChunk = BukkitLoader.getNmsProvider().getChunk(getWorld(), getBlockX() >> 4, getBlockZ() >> 4);
		return cachedChunk;
	}

	public Object getBlockPosition() {
		return toBlockPosition();
	}

	public Object toBlockPosition() {
		return BukkitLoader.getNmsProvider().blockPosition(getBlockX(), getBlockY(), getBlockZ());
	}

	public ChunkSnapshot getChunkSnapshot() {
		return getChunk().getChunkSnapshot();
	}

	public Block getBlock() {
		return getWorld().getBlockAt(getBlockX(), getBlockY(), getBlockZ());
	}

	public World getWorld() {
		return Bukkit.getWorld(world);
	}

	public Position add(double x, double y, double z) {
		int prevChunkX = (int) this.x >> 4;
		int prevChunkZ = (int) this.z >> 4;

		this.x += x;
		this.y += y;
		this.z += z;

		if (prevChunkX != (int) this.x >> 4 || prevChunkZ != (int) this.z >> 4)
			cachedChunk = null;
		return this;
	}

	public Position set(double x, double y, double z) {
		int prevChunkX = (int) this.x >> 4;
		int prevChunkZ = (int) this.z >> 4;

		this.x = x;
		this.y = y;
		this.z = z;

		if (prevChunkX != (int) this.x >> 4 || prevChunkZ != (int) this.z >> 4)
			cachedChunk = null;
		return this;
	}

	public Position remove(double x, double y, double z) {
		return add(-x, -y, -z);
	}

	public Position add(Position position) {
		return add(position.getX(), position.getY(), position.getZ());
	}

	public Position add(Location location) {
		return add(location.getX(), location.getY(), location.getZ());
	}

	public Position add(BlockFace face) {
		return add(face.getModX(), face.getModY(), face.getModZ());
	}

	public Position add(Vector vector) {
		return add(vector.getX(), vector.getY(), vector.getZ());
	}

	public Position remove(Location location) {
		return remove(location.getX(), location.getY(), location.getZ());
	}

	public Position remove(BlockFace face) {
		return remove(face.getModX(), face.getModY(), face.getModZ());
	}

	public Position remove(Vector vector) {
		return remove(vector.getX(), vector.getY(), vector.getZ());
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
		return floor == x ? floor : floor - (int) (Double.doubleToRawLongBits(x) >>> 63);
	}

	public int getBlockY() {
		int floor = (int) y;
		return floor == y ? floor : floor - (int) (Double.doubleToRawLongBits(y) >>> 63);
	}

	public int getBlockZ() {
		int floor = (int) z;
		return floor == z ? floor : floor - (int) (Double.doubleToRawLongBits(z) >>> 63);
	}

	public float getYaw() {
		return yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public Location toLocation() {
		return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
	}

	public void setType(Material type) {
		this.setType(new BlockDataStorage(type));
	}

	public void setType(BlockDataStorage type) {
		Position.set(this, type);
	}

	public void setTypeAndUpdate(Material type) {
		this.setTypeAndUpdate(new BlockDataStorage(type), true);
	}

	public void setTypeAndUpdate(Material type, boolean updatePhysics) {
		this.setTypeAndUpdate(new BlockDataStorage(type), updatePhysics);
	}

	public void setTypeAndUpdate(BlockDataStorage type) {
		setTypeAndUpdate(type, true);
	}

	public void setTypeAndUpdate(BlockDataStorage type, boolean updatePhysics) {
		Object prev = updatePhysics ? getIBlockData() : null;
		this.setType(type);
		Position.updateBlockAt(this, type);
		if (type.getNBT() != null && BukkitLoader.getNmsProvider().isTileEntity(getNMSChunk(), getBlockX(), getBlockY(), getBlockZ()))
			BukkitLoader.getNmsProvider().setNBTToTile(getNMSChunk(), getBlockX(), getBlockY(), getBlockZ(), type.getNBT());
		Position.updateLightAt(this);
		if (updatePhysics)
			BukkitLoader.getNmsProvider().updatePhysics(getNMSChunk(), getBlockX(), getBlockY(), getBlockZ(), prev);
	}

	@Override
	public boolean equals(Object a) {
		if (a instanceof Position) {
			Position s = (Position) a;
			return world.equals(s.getWorld().getName()) && s.getX() == x && s.getY() == y && s.getZ() == z && s.getPitch() == pitch && s.getYaw() == yaw;
		}
		if (a instanceof Location) {
			Location s = (Location) a;
			return world.equals(s.getWorld().getName()) && s.getX() == x && s.getY() == y && s.getZ() == z && s.getPitch() == pitch && s.getYaw() == yaw;
		}
		return false;
	}

	public static void updateBlockAt(Position pos) {
		Object packet = BukkitLoader.getNmsProvider().packetBlockChange(pos, pos.getIBlockData(), pos.getData());
		BukkitLoader.getPacketHandler().send(pos.getWorld().getPlayers(), packet);
	}

	public static void updateBlockAt(Position pos, BlockDataStorage blockData) {
		Object packet = BukkitLoader.getNmsProvider().packetBlockChange(pos, blockData.getIBlockData(), blockData.getItemData());
		BukkitLoader.getPacketHandler().send(pos.getWorld().getPlayers(), packet);
	}

	public static void updateLightAt(Position pos) {
		BukkitLoader.getNmsProvider().updateLightAt(pos.getNMSChunk(), pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
	}

	public static void set(Position pos, BlockDataStorage mat) {
		BukkitLoader.getNmsProvider().setBlock(pos.getNMSChunk(), pos.getBlockX(), pos.getBlockY(), pos.getBlockZ(), Ref.isOlderThan(8) ? mat.getBlock() : mat.getIBlockData(), mat.getItemData());
	}

	public long getChunkKey() {
		long k = (getBlockX() >> 4 & 0xFFFF0000L) << 16L | getBlockX() >> 4 & 0xFFFFL;
		k |= (getBlockZ() >> 4 & 0xFFFF0000L) << 32L | (getBlockZ() >> 4 & 0xFFFFL) << 16L;
		return k;
	}

	public void setAir() {
		BukkitLoader.getNmsProvider().setBlock(getNMSChunk(), getBlockX(), getBlockY(), getBlockZ(), null);
	}

	public void setAirAndUpdate() {
		setAirAndUpdate(true);
	}

	public void setAirAndUpdate(boolean updatePhysics) {
		Object prev = updatePhysics ? getIBlockData() : null;
		setAir();
		Object packet = BukkitLoader.getNmsProvider().packetBlockChange(this, null, 0);
		BukkitLoader.getPacketHandler().send(getWorld().getPlayers(), packet);
		Position.updateLightAt(this);
		if (updatePhysics)
			BukkitLoader.getNmsProvider().updatePhysics(getNMSChunk(), getBlockX(), getBlockY(), getBlockZ(), prev);
	}

	public void updatePhysics() {
		BukkitLoader.getNmsProvider().updatePhysics(getNMSChunk(), getBlockX(), getBlockY(), getBlockZ(), getIBlockData());
	}

	public void updatePhysics(Object blockData) {
		BukkitLoader.getNmsProvider().updatePhysics(getNMSChunk(), getBlockX(), getBlockY(), getBlockZ(), blockData);
	}

	@Override
	public Position clone() {
		return new Position(this);
	}

	@Override
	public String toString() {
		Map<String, Object> map = new HashMap<>();
		map.put("world", world);
		map.put("x", x);
		map.put("y", y);
		map.put("z", z);
		map.put("yaw", yaw);
		map.put("pitch", pitch);
		return Json.writer().simpleWrite(map);
	}

	@Override
	public int hashCode() {
		int hashCode = 1;
		hashCode = 31 * hashCode + world.hashCode();
		hashCode = (int) (31 * hashCode + x);
		hashCode = (int) (31 * hashCode + y);
		hashCode = (int) (31 * hashCode + z);
		hashCode = (int) (31 * hashCode + yaw);
		return (int) (31 * hashCode + pitch);
	}
}
