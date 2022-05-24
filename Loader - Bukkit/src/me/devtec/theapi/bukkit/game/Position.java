package me.devtec.theapi.bukkit.game;

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
import org.bukkit.entity.Entity;

import me.devtec.shared.Ref;
import me.devtec.shared.utility.StringUtils;
import me.devtec.theapi.bukkit.BukkitLoader;

public class Position implements Cloneable {
	private String w;
	private double x;
	private double y;
	private double z;
	private float yaw;
	private float pitch;

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

	public static Position fromString(String text) {
		try {
			String stored = text.substring(10, text.length() - 1);
			String[] part = stored.replace(":", ".").split("/");
			return new Position(part[0], StringUtils.getDouble(part[1]), StringUtils.getDouble(part[2]),
					StringUtils.getDouble(part[3]), StringUtils.getFloat(part[4]), StringUtils.getFloat(part[5]));
		} catch (Exception notMat) {
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

	public Biome getBiome() {
		return getBlock().getBiome();
	}

	public int getData() {
		return Ref.isOlderThan(8)
				? (byte) BukkitLoader.getNmsProvider().getData(getNMSChunk(), getBlockX(), getBlockY(),
						getBlockZ())
						: getType().getData();
	}

	public Material getBukkitType() {
		return getType().getType();
	}

	public Object getIBlockData() {
		return BukkitLoader.getNmsProvider().getBlock(getNMSChunk(), getBlockX(), getBlockY(),
				getBlockZ());
	}

	public TheMaterial getType() {
		Object chunk = getNMSChunk();
		if (Ref.isOlderThan(8)) // 1.7.10
			return TheMaterial.fromData(
					BukkitLoader.getNmsProvider().getBlock(chunk, getBlockX(), getBlockY(), getBlockZ()),
					(byte) BukkitLoader.getNmsProvider().getData(chunk, getBlockX(), getBlockY(),
							getBlockZ()));
		return TheMaterial.fromData(
				BukkitLoader.getNmsProvider().getBlock(chunk, getBlockX(), getBlockY(), getBlockZ()));
	}

	public Position subtract(double x, double y, double z) {
		this.x -= x;
		this.y -= y;
		this.z -= z;
		return this;
	}

	public Position subtract(Position position) {
		x -= position.getX();
		y -= position.getY();
		z -= position.getZ();
		return this;
	}

	public Position subtract(Location location) {
		x -= location.getX();
		y -= location.getY();
		z -= location.getZ();
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
		return Math.sqrt(this.distanceSquared(location));
	}

	public double distance(Position position) {
		return Math.sqrt(this.distanceSquared(position));
	}

	public Position multiply(double m) {
		x *= m;
		y *= m;
		z *= m;
		return this;
	}

	public Position zero() {
		x = 0;
		y = 0;
		z = 0;
		return this;
	}

	public double length() {
		return Math.sqrt(lengthSquared());
	}

	public double lengthSquared() {
		return square(x) + square(y) + square(z);
	}

	public double distanceSquared(Location location) {
		return square(x - location.getX()) + square(y - location.getY())
		+ square(z - location.getZ());
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
		try {
			return BukkitLoader.getNmsProvider().getChunk(getWorld(), getBlockX() >> 4,
					getBlockZ() >> 4);
		} catch (Exception er) {
		}
		return null;
	}

	public Object getBlockPosition() {
		return BukkitLoader.getNmsProvider().blockPosition(getBlockX(), getBlockY(), getBlockZ());
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
		x += position.getX();
		y += position.getY();
		z += position.getZ();
		return this;
	}

	public Position add(Location location) {
		x += location.getX();
		y += location.getY();
		z += location.getZ();
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
		return new Location(Bukkit.getWorld(w), x, y, z, yaw, pitch);
	}

	public long setType(Material with) {
		return this.setType(new TheMaterial(with));
	}

	public long setType(Material with, int data) {
		return this.setType(new TheMaterial(with, data));
	}

	public long setType(TheMaterial with) {
		return Position.set(this, with);
	}

	public void setTypeAndUpdate(Material with) {
		this.setTypeAndUpdate(new TheMaterial(with));
	}

	public void setTypeAndUpdate(Material with, int data) {
		this.setTypeAndUpdate(new TheMaterial(with, data));
	}

	public void setTypeAndUpdate(TheMaterial with) {
		this.setType(with);
		Position.updateBlockAt(this);
		Position.updateLightAt(this);
	}

	@Override
	public boolean equals(Object a) {
		if (a instanceof Position) {
			Position s = (Position) a;
			return w.equals(s.getWorld().getName()) && s.getX() == x && s.getY() == y
					&& s.getZ() == z && s.getPitch() == pitch && s.getYaw() == yaw;
		}
		if (a instanceof Location) {
			Location s = (Location) a;
			return w.equals(s.getWorld().getName()) && s.getX() == x && s.getY() == y
					&& s.getZ() == z && s.getPitch() == pitch && s.getYaw() == yaw;
		}
		return false;
	}

	public static void updateBlockAt(Position pos) {
		Object packet = BukkitLoader.getNmsProvider().packetBlockChange(pos.getWorld(), pos);
		pos.getWorld().getPlayers().forEach(player -> BukkitLoader.getPacketHandler().send(player, packet));
	}

	public static void updateLightAt(Position pos) {
		BukkitLoader.getNmsProvider().updateLightAt(pos.getNMSChunk(), pos.getBlockX(), pos.getBlockY(),
				pos.getBlockZ());
	}

	public static long set(Position pos, TheMaterial mat) {
		BukkitLoader.getNmsProvider().setBlock(pos.getNMSChunk(), pos.getBlockX(), pos.getBlockY(), pos.getBlockZ(),
				Ref.isOlderThan(8) ? mat.getBlock() : mat.getIBlockData(), mat.getData());
		return pos.getChunkKey();
	}

	public long getChunkKey() {
		long k = (getBlockX() >> 4 & 0xFFFF0000L) << 16L | getBlockX() >> 4 & 0xFFFFL;
		k |= (getBlockZ() >> 4 & 0xFFFF0000L) << 32L | (getBlockZ() >> 4 & 0xFFFFL) << 16L;
		return k;
	}

	public void setState(BlockState state) {
		Position.setState(this, state);
	}

	public void setBlockData(BlockData state) {
		Position.setBlockData(this, state);
	}

	public void setStateAndUpdate(BlockState state) {
		Position.setState(this, state);
		Position.updateBlockAt(this);
		Position.updateLightAt(this);
	}

	public void setBlockDataAndUpdate(BlockData state) {
		Position.setBlockData(this, state);
		Position.updateBlockAt(this);
		Position.updateLightAt(this);
	}

	public long setAir() {
		BukkitLoader.getNmsProvider().setBlock(getNMSChunk(), getBlockX(), getBlockY(), getBlockZ(),
				BukkitLoader.airBlock);
		return getChunkKey();
	}

	public void setAirAndUpdate() {
		setAir();
		Position.updateBlockAt(this);
		Position.updateLightAt(this);
	}

	public static long set(Location pos, int id, int data) {
		return Position.set(new Position(pos), new TheMaterial(id, data));
	}

	public static void setBlockData(Position pos, BlockData data) {
		if (data == null || Ref.isOlderThan(13) || pos == null)
			return;
		BukkitLoader.getNmsProvider().setBlock(pos.getNMSChunk(), pos.getBlockX(), pos.getBlockY(), pos.getBlockZ(),
				BukkitLoader.getNmsProvider().toIBlockData(data));
	}

	public static void setState(Position pos, BlockState state) {
		if (state == null || pos == null)
			return;
		if (Ref.isNewerThan(7))
			BukkitLoader.getNmsProvider().setBlock(pos.getNMSChunk(), pos.getBlockX(), pos.getBlockY(), pos.getBlockZ(),
					BukkitLoader.getNmsProvider().toIBlockData(state));
		else
			BukkitLoader.getNmsProvider().setBlock(pos.getNMSChunk(), pos.getBlockX(), pos.getBlockY(), pos.getBlockZ(),
					BukkitLoader.getNmsProvider().toBlock(state.getType()), state.getRawData());
	}

	@Override
	public Position clone() {
		return new Position(w, x, y, z, yaw, pitch);
	}

	@Override
	public String toString() {
		return ("[Position:" + w + "/" + x + "/" + y + "/" + z + "/" + yaw + "/" + pitch
				+ ']').replace(".", ":");
	}

	@Override
	public int hashCode() {
		int hashCode = 1;
		hashCode = 31 * hashCode + w.hashCode();
		hashCode = (int) (31 * hashCode + x);
		hashCode = (int) (31 * hashCode + y);
		hashCode = (int) (31 * hashCode + z);
		hashCode = (int) (31 * hashCode + yaw);
		return (int) (31 * hashCode + pitch);
	}
}
