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
		this.w = world.getName();
	}

	public Position(String world) {
		this.w = world;
	}

	public Position(World world, double x, double y, double z) {
		this(world, x, y, z, 0, 0);
	}

	public Position(World world, double x, double y, double z, float yaw, float pitch) {
		this.w = world.getName();
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
		this.w = world;
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
		this.w = location.getWorld().getName();
		this.x = location.getX();
		this.y = location.getY();
		this.z = location.getZ();
		this.yaw = location.getYaw();
		this.pitch = location.getPitch();
	}

	public Position(Block b) {
		this(b.getLocation());
	}

	public Position(Entity b) {
		this(b.getLocation());
	}

	public static Position fromString(String stored) {
		try {
			stored = stored.substring(10, stored.length() - 1);
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
		return this.getBlock().getBiome();
	}

	public int getData() {
		return Ref.isOlderThan(8)
				? (byte) BukkitLoader.getNmsProvider().getData(this.getNMSChunk(), this.getBlockX(), this.getBlockY(),
						this.getBlockZ())
				: this.getType().getData();
	}

	public Material getBukkitType() {
		return this.getType().getType();
	}

	public Object getIBlockData() {
		return BukkitLoader.getNmsProvider().getBlock(this.getNMSChunk(), this.getBlockX(), this.getBlockY(),
				this.getBlockZ());
	}

	public TheMaterial getType() {
		Object chunk = this.getNMSChunk();
		if (Ref.isOlderThan(8)) // 1.7.10
			return TheMaterial.fromData(
					BukkitLoader.getNmsProvider().getBlock(chunk, this.getBlockX(), this.getBlockY(), this.getBlockZ()),
					(byte) BukkitLoader.getNmsProvider().getData(chunk, this.getBlockX(), this.getBlockY(),
							this.getBlockZ()));
		return TheMaterial.fromData(
				BukkitLoader.getNmsProvider().getBlock(chunk, this.getBlockX(), this.getBlockY(), this.getBlockZ()));
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
		return this.w;
	}

	public Position setWorld(World world) {
		this.w = world.getName();
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
		this.x *= m;
		this.y *= m;
		this.z *= m;
		return this;
	}

	public Position zero() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
		return this;
	}

	public double length() {
		return Math.sqrt(this.lengthSquared());
	}

	public double lengthSquared() {
		return this.square(this.x) + this.square(this.y) + this.square(this.z);
	}

	public double distanceSquared(Location location) {
		return this.square(this.x - location.getX()) + this.square(this.y - location.getY())
				+ this.square(this.z - location.getZ());
	}

	public double distanceSquared(Position position) {
		return this.square(this.x - position.x) + this.square(this.y - position.y) + this.square(this.z - position.z);
	}

	private double square(double d) {
		return d * d;
	}

	public Chunk getChunk() {
		if (Ref.isNewerThan(12))
			return this.getWorld().getChunkAt(this.getBlockX() >> 4, this.getBlockZ() >> 4);
		return BukkitLoader.getNmsProvider().toBukkitChunk(this.getNMSChunk());
	}

	public Object getNMSChunk() {
		try {
			return BukkitLoader.getNmsProvider().getChunk(this.getWorld(), this.getBlockX() >> 4,
					this.getBlockZ() >> 4);
		} catch (Exception er) {
		}
		return null;
	}

	public Object getBlockPosition() {
		return BukkitLoader.getNmsProvider().blockPosition(this.getBlockX(), this.getBlockY(), this.getBlockZ());
	}

	public ChunkSnapshot getChunkSnapshot() {
		return this.getChunk().getChunkSnapshot();
	}

	public Block getBlock() {
		return this.getWorld().getBlockAt(this.getBlockX(), this.getBlockY(), this.getBlockZ());
	}

	public World getWorld() {
		return Bukkit.getWorld(this.w);
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
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public double getZ() {
		return this.z;
	}

	public int getBlockX() {
		int floor = (int) this.x;
		return floor == this.x ? floor : floor - (int) (Double.doubleToRawLongBits(this.x) >>> 63);
	}

	public int getBlockY() {
		int floor = (int) this.y;
		return floor == this.y ? floor : floor - (int) (Double.doubleToRawLongBits(this.y) >>> 63);
	}

	public int getBlockZ() {
		int floor = (int) this.z;
		return floor == this.z ? floor : floor - (int) (Double.doubleToRawLongBits(this.z) >>> 63);
	}

	public float getYaw() {
		return this.yaw;
	}

	public float getPitch() {
		return this.pitch;
	}

	public Location toLocation() {
		return new Location(Bukkit.getWorld(this.w), this.x, this.y, this.z, this.yaw, this.pitch);
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
			return this.w.equals(s.getWorld().getName()) && s.getX() == this.x && s.getY() == this.y
					&& s.getZ() == this.z && s.getPitch() == this.pitch && s.getYaw() == this.yaw;
		}
		if (a instanceof Location) {
			Location s = (Location) a;
			return this.w.equals(s.getWorld().getName()) && s.getX() == this.x && s.getY() == this.y
					&& s.getZ() == this.z && s.getPitch() == this.pitch && s.getYaw() == this.yaw;
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
		long k = (this.getBlockX() >> 4 & 0xFFFF0000L) << 16L | this.getBlockX() >> 4 & 0xFFFFL;
		k |= (this.getBlockZ() >> 4 & 0xFFFF0000L) << 32L | (this.getBlockZ() >> 4 & 0xFFFFL) << 16L;
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
		BukkitLoader.getNmsProvider().setBlock(this.getNMSChunk(), this.getBlockX(), this.getBlockY(), this.getBlockZ(),
				BukkitLoader.airBlock);
		return this.getChunkKey();
	}

	public void setAirAndUpdate() {
		this.setAir();
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
		return new Position(this.w, this.x, this.y, this.z, this.yaw, this.pitch);
	}

	@Override
	public String toString() {
		return ("[Position:" + this.w + "/" + this.x + "/" + this.y + "/" + this.z + "/" + this.yaw + "/" + this.pitch
				+ ']').replace(".", ":");
	}

	@Override
	public int hashCode() {
		int hashCode = 1;
		hashCode = 31 * hashCode + this.w.hashCode();
		hashCode = (int) (31 * hashCode + this.x);
		hashCode = (int) (31 * hashCode + this.y);
		hashCode = (int) (31 * hashCode + this.z);
		hashCode = (int) (31 * hashCode + this.yaw);
		return (int) (31 * hashCode + this.pitch);
	}
}
