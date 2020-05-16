package me.Straiker123;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

public class Position {
	public Position() {
		this(null,0,0,0,0,0);
	}
	
	public Position(World world, double x, double y, double z) {
		this(world,x,y,z,0,0);
	}
	
	public Position(World world, double x, double y, double z, float yaw, float pitch) {
		w=world.getName();
		this.x=x;
		this.y=y;
		this.z=z;
		this.yaw=yaw;
		this.pitch=pitch;
	}

	public Position(Location location) {
		w=location.getWorld().getName();
		this.x=location.getX();
		this.y=location.getY();
		this.z=location.getZ();
		this.yaw=location.getYaw();
		this.pitch=location.getPitch();
	}

	public Position(Block b) {
		this(b.getLocation());
	}

	private String w;
	private double x,y,z;
	private float yaw,pitch;

	public String toString() {
		return "[Position:"+w+"/"+x+"/"+y+"/"+z+"/"+yaw+"/"+pitch+"]".replace(".", ":");
	}

	public Biome getBiome() {
		return getBlock().getBiome();
	}
	
	public Material getBukkitType() {
		return getBlock().getType();
	}

	@SuppressWarnings("deprecation")
	public TheMaterial getType() {
		return new TheMaterial(getBlock().getType(), getBlock().getData());
	}
	
	public Position subtract(double x, double y, double z) {
		return new Position(Bukkit.getWorld(w),this.x-x, this.y-y, this.z-z,yaw,pitch);
	}
	
	public void setWorld(World world) {
		w=world.getName();
	}
	
	public void setX(double x) {
		this.x=x;
	}
	
	public void setY(double y) {
		this.y=y;
	}
	
	public void setZ(double z) {
		this.z=z;
	}
	
	public void setYaw(float yaw) {
		this.yaw=yaw;
	}
	
	public void setPitch(float pitch) {
		this.pitch=pitch;
	}
	
	public static Position fromString(String stored) {
		if(stored.startsWith("[Position:")) {
			stored=stored.replaceFirst("[Position:", "").substring(0, stored.length()-1);
			String[] part = stored.replace(":", ".").split("/");
			StringUtils s = TheAPI.getStringUtils();
			return new Position(Bukkit.getWorld(part[0]), s.getDouble(part[1]), s.getDouble(part[2]), 
					s.getDouble(part[3]), s.getFloat(part[4]), s.getFloat(part[5]));
		}
		return null;
	}
	
	public double distance(Location location) {
		return location.distance(toLocation());
	}

	public double distanceSquared(Location location) {
		return location.distanceSquared(toLocation());
	}

	public Chunk getChunk() {
		return getWorld().getChunkAt(getBlockX()>>4, getBlockZ()>>4);
	}

	public Block getBlock() {
		return getWorld().getBlockAt(getBlockX(), getBlockY(), getBlockZ());
	}
	
	public World getWorld() {
		return Bukkit.getWorld(w);
	}
	
	public Position add(double x, double y, double z) {
		this.x+=x;
		this.y+=y;
		this.z+=z;
		return this;
	}
	
	public Position add(Location location) {
		add(location.getX(),location.getY(),location.getZ());
		this.yaw+=location.getYaw();
		this.pitch+=location.getPitch();
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
		return (int)x;
	}
	
	public int getBlockY() {
		return (int)y;
	}
	
	public int getBlockZ() {
		return (int)z;
	}
	
	public float getYaw() {
		return yaw;
	}
	
	public float getPitch() {
		return pitch;
	}
	
	public Location toLocation() {
		return new Location(Bukkit.getWorld(w),x,y,z,yaw,pitch);
	}

	public void setType(Material with) {
		setType(with,0);
	}

	public void setType(TheMaterial with) {
		setType(with.getType(),with.getData());
	}

	public void setType(Material with, int data) {
		TheAPI.getNMSAPI().setBlock(getWorld(), getBlockX(), getBlockY(), getBlockZ(), with,data, true);
	}

	public boolean equals(Object a) {
		if(a instanceof Position) {
			Position s = (Position)a;
			return w.equals(s.getWorld().getName()) && s.getX() == x &&  s.getY() == y &&  s.getZ() == z &&  s.getPitch() == pitch &&  s.getYaw() == yaw;
		}
		if(a instanceof Location) {
			Location s = (Location)a;
			return w.equals(s.getWorld().getName()) && s.getX() == x &&  s.getY() == y &&  s.getZ() == z &&  s.getPitch() == pitch &&  s.getYaw() == yaw;
		}
		return false;
	}
}
