package me.DevTec.Other;

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

	@SuppressWarnings("deprecation")
	public byte getData() {
		return getBlock().getData();
	}

	public Material getBukkitType() {
		return getBlock().getType();
	}

	public TheMaterial getType() {
		return new TheMaterial(getBukkitType(),getData(),1);
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
			StringUtils s = TheAPI.getStringUtils();
			return new Position(Bukkit.getWorld(part[0]), s.getDouble(part[1]), s.getDouble(part[2]),
					s.getDouble(part[3]), s.getFloat(part[4]), s.getFloat(part[5]));
		}
		Location loc = TheAPI.getStringUtils().getLocationFromString(stored);
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

	public Chunk getChunk() {
		return getWorld().getChunkAt(getBlockX() >> 4, getBlockZ() >> 4);
	}

	public ChunkSnapshot getChunkSnapshot() {
		return getChunk().getChunkSnapshot();
	}

	public Block getBlock() {
		return getChunk().getBlock(getBlockX(), getBlockY(), getBlockZ());
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

	public void setType(Material with) {
		setType(with, 0, true);
	}

	public void setType(TheMaterial with) {
		setType(with.getType(), with.getData(), true);
	}

	public void setType(Material with, int data) {
		setType(with,data,true);
	}


	public void setType(Material with, boolean psyhics) {
		setType(with, 0, psyhics);
	}

	public void setType(TheMaterial with, boolean psyhics) {
		setType(with.getType(), with.getData(), psyhics);
	}

	public void setType(Material with, int data, boolean psyhics) {
		TheAPI.getNMSAPI().setBlock(getWorld(), getBlockX(), getBlockY(), getBlockZ(), with, data, psyhics);
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
	
	public Position clone() {
		try {
			return (Position)super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
}
