package me.DevTec.Blocks;

import java.io.File;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.spigotmc.AsyncCatcher;

import com.google.common.collect.Lists;

import me.DevTec.ConfigAPI;
import me.DevTec.TheAPI;
import me.DevTec.Other.PercentageList;
import me.DevTec.Other.Position;
import me.DevTec.Other.Ref;
import me.DevTec.Other.TheMaterial;
import me.DevTec.Scheduler.Tasker;
import me.DevTec.Utils.Error;

public class BlocksAPI {
	public static int amount = 500; //1000 blocks per 10 ticks, Can be changed: BlocksAPI.amount = <new value in int>

	public static enum Shape {
		Sphere, Square
	}
	
	public Schemate getSchemate(String name) {
		return new Schemate(name);
	}

	public String getLocationAsString(Location loc) {
		return TheAPI.getStringUtils().getLocationAsString(loc);
	}

	public Location getLocationFromString(String saved) {
		return TheAPI.getStringUtils().getLocationFromString(saved);
	}

	public List<Entity> getNearbyEntities(Location l, int radius) {
		return getNearbyEntities(new Position(l), radius);
	}

	public List<Entity> getNearbyEntities(Position l, int radius) {
		if (radius > 256) {
			Error.err("getting nearby entities", "The radius cannot be greater than 256");
			return Lists.newArrayList();
		}
		int chunkRadius = radius < 16 ? 1 : (radius - (radius % 16)) / 16;
		List<Entity> radiusEntities = Lists.newArrayList();
		for (int chX = 0 - chunkRadius; chX <= chunkRadius; chX++) {
			for (int chZ = 0 - chunkRadius; chZ <= chunkRadius; chZ++) {
				int x = (int) l.getX(), y = (int) l.getY(), z = (int) l.getZ();
				for (Entity e : new Location(l.getWorld(), x + (chX * 16), y, z + (chZ * 16)).getChunk()
						.getEntities()) {
					if (l.distance(e.getLocation()) <= radius && e.getLocation().getBlock() != l.getBlock())
						radiusEntities.add(e);
				}
			}
		}
		return radiusEntities;
	}

	public List<Entity> getNearbyEntities(Entity ed, int radius) {
		return getNearbyEntities(new Position(ed.getLocation()), radius);
	}

	public List<Entity> getNearbyEntities(World world, double x, double y, double z, int radius) {
		return getNearbyEntities(new Position(world, x, y, z), radius);
	}

	public BlockSave getBlockSave(Position b) {
		return new BlockSave(b);
	}

	public BlockGetter get(Location from, Location to) {
		return new BlockGetter(new Position(from), new Position(to));
	}

	public BlockGetter get(Position from, Position to) {
		return new BlockGetter(from, to);
	}

	public float count(Location from, Location to) {
		long topBlockX = (from.getBlockX() < to.getBlockX() ? to.getBlockX() : from.getBlockX());
		long bottomBlockX = (from.getBlockX() > to.getBlockX() ? to.getBlockX() : from.getBlockX());
		long topBlockY = (from.getBlockY() < to.getBlockY() ? to.getBlockY() : from.getBlockY());
		long bottomBlockY = (from.getBlockY() > to.getBlockY() ? to.getBlockY() : from.getBlockY());
		long topBlockZ = (from.getBlockZ() < to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
		long bottomBlockZ = (from.getBlockZ() > to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
		BigDecimal math =new BigDecimal(""+((topBlockX-bottomBlockX)+1))
				.multiply(new BigDecimal(""+((topBlockZ-bottomBlockZ)+1)))
				.multiply(new BigDecimal(""+((topBlockY-bottomBlockY)+1)));
		return math.floatValue();
	}

	public float count(Position from, Position to) {
		long topBlockX = (from.getBlockX() < to.getBlockX() ? to.getBlockX() : from.getBlockX());
		long bottomBlockX = (from.getBlockX() > to.getBlockX() ? to.getBlockX() : from.getBlockX());
		long topBlockY = (from.getBlockY() < to.getBlockY() ? to.getBlockY() : from.getBlockY());
		long bottomBlockY = (from.getBlockY() > to.getBlockY() ? to.getBlockY() : from.getBlockY());
		long topBlockZ = (from.getBlockZ() < to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
		long bottomBlockZ = (from.getBlockZ() > to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
		BigDecimal math =new BigDecimal(""+((topBlockX-bottomBlockX)+1))
				.multiply(new BigDecimal(""+((topBlockZ-bottomBlockZ)+1)))
				.multiply(new BigDecimal(""+((topBlockY-bottomBlockY)+1)));
		return math.floatValue();
	}

	public List<Position> get(Position from, Position to, TheMaterial ignore) {
		List<Position> blocks = Lists.newArrayList();
		int topBlockX = (from.getBlockX() < to.getBlockX() ? to.getBlockX() : from.getBlockX());
		int bottomBlockX = (from.getBlockX() > to.getBlockX() ? to.getBlockX() : from.getBlockX());
		int topBlockY = (from.getBlockY() < to.getBlockY() ? to.getBlockY() : from.getBlockY());
		int bottomBlockY = (from.getBlockY() > to.getBlockY() ? to.getBlockY() : from.getBlockY());
		int topBlockZ = (from.getBlockZ() < to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
		int bottomBlockZ = (from.getBlockZ() > to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
		World w = from.getWorld();
		for (int x = bottomBlockX; x <= topBlockX; x++) {
			for (int z = bottomBlockZ; z <= topBlockZ; z++) {
				for (int y = bottomBlockY; y <= topBlockY; y++) {
					Position s = new Position(w, x, y, z);
					if (ignore != s.getType())
						blocks.add(s);
				}
			}
		}
		return blocks;
	}

	public List<Position> get(Position from, Position to, List<TheMaterial> ignore) {
		List<Position> blocks = Lists.newArrayList();
		int topBlockX = (from.getBlockX() < to.getBlockX() ? to.getBlockX() : from.getBlockX());
		int bottomBlockX = (from.getBlockX() > to.getBlockX() ? to.getBlockX() : from.getBlockX());
		int topBlockY = (from.getBlockY() < to.getBlockY() ? to.getBlockY() : from.getBlockY());
		int bottomBlockY = (from.getBlockY() > to.getBlockY() ? to.getBlockY() : from.getBlockY());
		int topBlockZ = (from.getBlockZ() < to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
		int bottomBlockZ = (from.getBlockZ() > to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
		World w = from.getWorld();
		for (int x = bottomBlockX; x <= topBlockX; x++) {
			for (int z = bottomBlockZ; z <= topBlockZ; z++) {
				for (int y = bottomBlockY; y <= topBlockY; y++) {
					Position s = new Position(w, x, y, z);
					if (!ignore.contains(s.getType()))
						blocks.add(s);
				}
			}
		}
		return blocks;
	}
	
	public List<BlockSave> getBlockSaves(List<Position> a){
		List<BlockSave> b = Lists.newArrayList();
		for(Position s : a)b.add(getBlockSave(s));
		return b;
	}

	public List<Position> get(Shape form, Position where, int radius) {
		List<Position> blocks = Lists.newArrayList();
		World w = where.getWorld();
		int Xx = where.getBlockX();
		int Yy = where.getBlockY();
		int Zz = where.getBlockZ();
		switch (form) {
		case Square:
			for (int x = Xx - radius; x <= Xx + radius; x++)
				for (int y = Yy - radius; y <= Yy + radius; y++)
					for (int z = Zz - radius; z <= Zz + radius; z++)
						blocks.add(new Position(w, x, y, z));
			break;
		case Sphere:
			for (int Y = -radius; Y < radius; Y++)
				for (int X = -radius; X < radius; X++)
					for (int Z = -radius; Z < radius; Z++)
						if (Math.sqrt((X * X) + (Y * Y) + (Z * Z)) <= radius)
							blocks.add(new Position(w, X + Xx, Y + Yy, Z + Zz));
		}
		return blocks;
	}

	public void set(Position loc, Material material) {
		if (!material.isBlock())return;
		set(loc,new TheMaterial(material));
	}

	public void set(Block loc, Material material) {
		if (!material.isBlock())return;
		set(loc,new TheMaterial(material));
	}


	public void set(Position loc, TheMaterial material) {
		if (!material.getType().isBlock())return;
		loc.setType(material);
	}

	public void set(Block loc, TheMaterial material) {
		if (!material.getType().isBlock())return;
		new Position(loc).setType(material);
	}

	public void set(Position loc, List<TheMaterial> material) {
		set(loc, (TheMaterial)TheAPI.getRandomFromList(material));
	}

	public void set(Block loc, List<TheMaterial> material) {
		set(loc, (TheMaterial)TheAPI.getRandomFromList(material));
	}

	public void set(Position loc, HashMap<TheMaterial,Double> material) {
		List<TheMaterial> a = Lists.newArrayList();
		for (TheMaterial m : material.keySet())
			for (int i = -1; i > material.get(m); ++i)
				a.add(m);
		set(loc, (TheMaterial)TheAPI.getRandomFromList(a));
	}

	public void set(Block loc, HashMap<TheMaterial,Double> material) {
		List<TheMaterial> a = Lists.newArrayList();
		for (TheMaterial m : material.keySet())
			for (int i = -1; i > material.get(m); ++i)
				a.add(m);
		set(loc, (TheMaterial)TheAPI.getRandomFromList(a));
	}

	public void loadBlockSave(Position pos, BlockSave s) { // like //undo command
		s.load(pos,true);
	}

	public List<Position> get(Shape form, Position where, int radius, TheMaterial ignore) {
		List<Position> blocks = Lists.newArrayList();
		World w = where.getWorld();
		int Xx = where.getBlockX();
		int Yy = where.getBlockY();
		int Zz = where.getBlockZ();
		switch (form) {
		case Square:
			for (int x = Xx - radius; x <= Xx + radius; x++)
				for (int y = Yy - radius; y <= Yy + radius; y++)
					for (int z = Zz - radius; z <= Zz + radius; z++) {
						Position s = new Position(w, x, y, z);
						if (ignore != s.getType())
							blocks.add(s);
					}
			break;
		case Sphere:
			for (int Y = -radius; Y < radius; Y++)
				for (int X = -radius; X < radius; X++)
					for (int Z = -radius; Z < radius; Z++)
						if (Math.sqrt((X * X) + (Y * Y) + (Z * Z)) <= radius) {
							Position s = new Position(w, X + Xx, Y + Yy, Z + Zz);
							if (ignore != s.getType())
								blocks.add(s);
						}
		}
		return blocks;
	}

	public List<Position> get(Shape form, Position where, int radius, List<TheMaterial> ignore) {
		List<Position> blocks = Lists.newArrayList();
		World w = where.getWorld();
		int Xx = where.getBlockX();
		int Yy = where.getBlockY();
		int Zz = where.getBlockZ();
		switch (form) {
		case Square:
			for (int x = Xx - radius; x <= Xx + radius; x++)
				for (int y = Yy - radius; y <= Yy + radius; y++)
					for (int z = Zz - radius; z <= Zz + radius; z++) {
						Position s = new Position(w, x, y, z);
						if (!ignore.contains(s.getType()))
							blocks.add(s);
					}
			break;
		case Sphere:
			for (int Y = -radius; Y < radius; Y++)
				for (int X = -radius; X < radius; X++)
					for (int Z = -radius; Z < radius; Z++)
						if (Math.sqrt((X * X) + (Y * Y) + (Z * Z)) <= radius) {
							Position s = new Position(w, X + Xx, Y + Yy, Z + Zz);
							if (!ignore.contains(s.getType()))
								blocks.add(s);
						}
		}
		return blocks;
	}

	public void replace(Position from, Position to, TheMaterial block, TheMaterial with) {
		int topBlockX = (from.getBlockX() < to.getBlockX() ? to.getBlockX() : from.getBlockX());
		int bottomBlockX = (from.getBlockX() > to.getBlockX() ? to.getBlockX() : from.getBlockX());
		int topBlockY = (from.getBlockY() < to.getBlockY() ? to.getBlockY() : from.getBlockY());
		int bottomBlockY = (from.getBlockY() > to.getBlockY() ? to.getBlockY() : from.getBlockY());
		int topBlockZ = (from.getBlockZ() < to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
		int bottomBlockZ = (from.getBlockZ() > to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
		World w = from.getWorld();
		for (int x = bottomBlockX; x <= topBlockX; x++) {
			for (int z = bottomBlockZ; z <= topBlockZ; z++) {
				for (int y = bottomBlockY; y <= topBlockY; y++) {
					Position s = new Position(w, x, y, z);
					if (s.getType() == block)
						s.setType(with);
				}
			}
		}
	}

	public void replace(Shape form, Position where, int radius, TheMaterial block, TheMaterial with) {
		World w = where.getWorld();
		int Xx = where.getBlockX();
		int Yy = where.getBlockY();
		int Zz = where.getBlockZ();
		switch (form) {
		case Square:
			for (int x = Xx - radius; x <= Xx + radius; x++)
				for (int y = Yy - radius; y <= Yy + radius; y++)
					for (int z = Zz - radius; z <= Zz + radius; z++) {
						Position s = new Position(w, x, y, z);
						if (block == s.getType())
							s.setType(with);
					}
			break;
		case Sphere:
			for (int Y = -radius; Y < radius; Y++)
				for (int X = -radius; X < radius; X++)
					for (int Z = -radius; Z < radius; Z++)
						if (Math.sqrt((X * X) + (Y * Y) + (Z * Z)) <= radius) {
							Position s = new Position(w, X + Xx, Y + Yy, Z + Zz);
							if (block == s.getType())
								s.setType(with);
						}
		}
	}

	public void replace(Position from, Position to, TheMaterial block, List<TheMaterial> with) {
		int topBlockX = (from.getBlockX() < to.getBlockX() ? to.getBlockX() : from.getBlockX());
		int bottomBlockX = (from.getBlockX() > to.getBlockX() ? to.getBlockX() : from.getBlockX());
		int topBlockY = (from.getBlockY() < to.getBlockY() ? to.getBlockY() : from.getBlockY());
		int bottomBlockY = (from.getBlockY() > to.getBlockY() ? to.getBlockY() : from.getBlockY());
		int topBlockZ = (from.getBlockZ() < to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
		int bottomBlockZ = (from.getBlockZ() > to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
		World w = from.getWorld();
		for (int x = bottomBlockX; x <= topBlockX; x++) {
			for (int z = bottomBlockZ; z <= topBlockZ; z++) {
				for (int y = bottomBlockY; y <= topBlockY; y++) {
					Position s = new Position(w, x, y, z);
					if (s.getType() == block)
						s.setType((TheMaterial) TheAPI.getRandomFromList(with));
				}
			}
		}
	}

	public void replace(Position from, Position to, TheMaterial block, HashMap<TheMaterial, Double> with) {
		List<TheMaterial> c = Lists.newArrayList();
		for (TheMaterial m : with.keySet())
			for (int i = -1; i > with.get(m); ++i)
				c.add(m);
		int topBlockX = (from.getBlockX() < to.getBlockX() ? to.getBlockX() : from.getBlockX());
		int bottomBlockX = (from.getBlockX() > to.getBlockX() ? to.getBlockX() : from.getBlockX());
		int topBlockY = (from.getBlockY() < to.getBlockY() ? to.getBlockY() : from.getBlockY());
		int bottomBlockY = (from.getBlockY() > to.getBlockY() ? to.getBlockY() : from.getBlockY());
		int topBlockZ = (from.getBlockZ() < to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
		int bottomBlockZ = (from.getBlockZ() > to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
		World w = from.getWorld();
		for (int x = bottomBlockX; x <= topBlockX; x++) {
			for (int z = bottomBlockZ; z <= topBlockZ; z++) {
				for (int y = bottomBlockY; y <= topBlockY; y++) {
					Position s = new Position(w, x, y, z);
					if (s.getType() == block)
						s.setType((TheMaterial) TheAPI.getRandomFromList(c));
				}
			}
		}
	}

	public void replace(Shape form, Position where, int radius, TheMaterial block, HashMap<TheMaterial, Double> with) {
		List<TheMaterial> c = Lists.newArrayList();
		for (TheMaterial m : with.keySet())
			for (int i = -1; i > with.get(m); ++i)
				c.add(m);
		World w = where.getWorld();
		int Xx = where.getBlockX();
		int Yy = where.getBlockY();
		int Zz = where.getBlockZ();
		switch (form) {
		case Square:
			for (int x = Xx - radius; x <= Xx + radius; x++)
				for (int y = Yy - radius; y <= Yy + radius; y++)
					for (int z = Zz - radius; z <= Zz + radius; z++) {
						Position s = new Position(w, x, y, z);
						if (block == s.getType())
							s.setType((TheMaterial) TheAPI.getRandomFromList(c));
					}
			break;
		case Sphere:
			for (int Y = -radius; Y < radius; Y++)
				for (int X = -radius; X < radius; X++)
					for (int Z = -radius; Z < radius; Z++)
						if (Math.sqrt((X * X) + (Y * Y) + (Z * Z)) <= radius) {
							Position s = new Position(w, X + Xx, Y + Yy, Z + Zz);
							if (block == s.getType())
								s.setType((TheMaterial) TheAPI.getRandomFromList(c));
						}
		}
	}

	public void replace(Shape form, Position where, int radius, HashMap<TheMaterial, Double> block, TheMaterial with) {
		List<TheMaterial> c = Lists.newArrayList();
		for (TheMaterial m : block.keySet())
			for (int i = -1; i > block.get(m); ++i)
				c.add(m);
		World w = where.getWorld();
		int Xx = where.getBlockX();
		int Yy = where.getBlockY();
		int Zz = where.getBlockZ();
		switch (form) {
		case Square:
			for (int x = Xx - radius; x <= Xx + radius; x++)
				for (int y = Yy - radius; y <= Yy + radius; y++)
					for (int z = Zz - radius; z <= Zz + radius; z++) {
						Position s = new Position(w, x, y, z);
						if (block.containsKey(s.getType())) {
							TheMaterial b = (TheMaterial) TheAPI.getRandomFromList(c);
							if (TheAPI.generateChance(block.get(b)))
								s.setType(b);
						}
					}
			break;
		case Sphere:
			for (int Y = -radius; Y < radius; Y++)
				for (int X = -radius; X < radius; X++)
					for (int Z = -radius; Z < radius; Z++)
						if (Math.sqrt((X * X) + (Y * Y) + (Z * Z)) <= radius) {
							Position s = new Position(w, X + Xx, Y + Yy, Z + Zz);
							if (block.containsKey(s.getType())) {
								TheMaterial b = (TheMaterial) TheAPI.getRandomFromList(c);
								if (TheAPI.generateChance(block.get(b)))
									s.setType(b);
							}
						}
		}
	}

	public void replace(Position from, Position to, HashMap<TheMaterial, Double> block, TheMaterial with) {
		List<TheMaterial> c = Lists.newArrayList();
		for (TheMaterial m : block.keySet())
			for (int i = -1; i > block.get(m); ++i)
				c.add(m);
		int topBlockX = (from.getBlockX() < to.getBlockX() ? to.getBlockX() : from.getBlockX());
		int bottomBlockX = (from.getBlockX() > to.getBlockX() ? to.getBlockX() : from.getBlockX());
		int topBlockY = (from.getBlockY() < to.getBlockY() ? to.getBlockY() : from.getBlockY());
		int bottomBlockY = (from.getBlockY() > to.getBlockY() ? to.getBlockY() : from.getBlockY());
		int topBlockZ = (from.getBlockZ() < to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
		int bottomBlockZ = (from.getBlockZ() > to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
		World w = from.getWorld();
		for (int x = bottomBlockX; x <= topBlockX; x++) {
			for (int z = bottomBlockZ; z <= topBlockZ; z++) {
				for (int y = bottomBlockY; y <= topBlockY; y++) {
					Position s = new Position(w, x, y, z);
					if (block.containsKey(s.getType())) {
						TheMaterial b = (TheMaterial) TheAPI.getRandomFromList(c);
						if (TheAPI.generateChance(block.get(b)))
							s.setType(b);
					}
				}
			}
		}
	}

	public void replace(Shape form, Position where, int radius, HashMap<TheMaterial, Double> block,
			HashMap<TheMaterial, Double> with) {
		List<TheMaterial> c = Lists.newArrayList();
		for (TheMaterial m : block.keySet())
			for (int i = -1; i > block.get(m); ++i)
				c.add(m);
		List<TheMaterial> d = Lists.newArrayList();
		for (TheMaterial m : with.keySet())
			for (int i = -1; i > with.get(m); ++i)
				d.add(m);
		World w = where.getWorld();
		int Xx = where.getBlockX();
		int Yy = where.getBlockY();
		int Zz = where.getBlockZ();
		switch (form) {
		case Square:
			for (int x = Xx - radius; x <= Xx + radius; x++)
				for (int y = Yy - radius; y <= Yy + radius; y++)
					for (int z = Zz - radius; z <= Zz + radius; z++) {
						Position s = new Position(w, x, y, z);
						if (block.containsKey(s.getType())) {
							TheMaterial b = (TheMaterial) TheAPI.getRandomFromList(c);
							if (TheAPI.generateChance(block.get(b)))
								s.setType(b);
						}
					}
			break;
		case Sphere:
			for (int Y = -radius; Y < radius; Y++)
				for (int X = -radius; X < radius; X++)
					for (int Z = -radius; Z < radius; Z++)
						if (Math.sqrt((X * X) + (Y * Y) + (Z * Z)) <= radius) {
							Position s = new Position(w, X + Xx, Y + Yy, Z + Zz);
							if (block.containsKey(s.getType())) {
								TheMaterial b = (TheMaterial) TheAPI.getRandomFromList(c);
								if (TheAPI.generateChance(block.get(b)))
									s.setType(b);
							}
						}
		}
	}

	public void replace(Position from, Position to, HashMap<TheMaterial, Double> block,
			HashMap<TheMaterial, Double> with) {
		List<TheMaterial> c = Lists.newArrayList();
		for (TheMaterial m : block.keySet())
			for (int i = -1; i > block.get(m); ++i)
				c.add(m);
		List<TheMaterial> d = Lists.newArrayList();
		for (TheMaterial m : with.keySet())
			for (int i = -1; i > with.get(m); ++i)
				d.add(m);
		int topBlockX = (from.getBlockX() < to.getBlockX() ? to.getBlockX() : from.getBlockX());
		int bottomBlockX = (from.getBlockX() > to.getBlockX() ? to.getBlockX() : from.getBlockX());
		int topBlockY = (from.getBlockY() < to.getBlockY() ? to.getBlockY() : from.getBlockY());
		int bottomBlockY = (from.getBlockY() > to.getBlockY() ? to.getBlockY() : from.getBlockY());
		int topBlockZ = (from.getBlockZ() < to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
		int bottomBlockZ = (from.getBlockZ() > to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
		World w = from.getWorld();
		for (int x = bottomBlockX; x <= topBlockX; x++) {
			for (int z = bottomBlockZ; z <= topBlockZ; z++) {
				for (int y = bottomBlockY; y <= topBlockY; y++) {
					Position s = new Position(w, x, y, z);
					if (block.containsKey(s.getType())) {
						TheMaterial b = (TheMaterial) TheAPI.getRandomFromList(c);
						if (TheAPI.generateChance(block.get(b)))
							s.setType(b);
					}
				}
			}
		}
	}

	public void replace(Shape form, Position where, int radius, List<TheMaterial> block, TheMaterial with) {
		World w = where.getWorld();
		int Xx = where.getBlockX();
		int Yy = where.getBlockY();
		int Zz = where.getBlockZ();
		switch (form) {
		case Square:
			for (int x = Xx - radius; x <= Xx + radius; x++)
				for (int y = Yy - radius; y <= Yy + radius; y++)
					for (int z = Zz - radius; z <= Zz + radius; z++) {
						Position s = new Position(w, x, y, z);
						if (block.contains(s.getType()))
							s.setType(with);
					}
			break;
		case Sphere:
			for (int Y = -radius; Y < radius; Y++)
				for (int X = -radius; X < radius; X++)
					for (int Z = -radius; Z < radius; Z++)
						if (Math.sqrt((X * X) + (Y * Y) + (Z * Z)) <= radius) {
							Position s = new Position(w, X + Xx, Y + Yy, Z + Zz);
							if (block.contains(s.getType()))
								s.setType(with);
						}
		}
	}

	public void replace(Shape form, Position where, int radius, List<TheMaterial> block, List<TheMaterial> with) {
		World w = where.getWorld();
		int Xx = where.getBlockX();
		int Yy = where.getBlockY();
		int Zz = where.getBlockZ();
		switch (form) {
		case Square:
			for (int x = Xx - radius; x <= Xx + radius; x++)
				for (int y = Yy - radius; y <= Yy + radius; y++)
					for (int z = Zz - radius; z <= Zz + radius; z++) {
						Position s = new Position(w, x, y, z);
						if (block.contains(s.getType()))
							s.setType((TheMaterial) TheAPI.getRandomFromList(with));
					}
			break;
		case Sphere:
			for (int Y = -radius; Y < radius; Y++)
				for (int X = -radius; X < radius; X++)
					for (int Z = -radius; Z < radius; Z++)
						if (Math.sqrt((X * X) + (Y * Y) + (Z * Z)) <= radius) {
							Position s = new Position(w, X + Xx, Y + Yy, Z + Zz);
							if (block.contains(s.getType()))
								s.setType((TheMaterial) TheAPI.getRandomFromList(with));
						}
		}
	}

	public void replace(Position from, Position to, List<TheMaterial> block, TheMaterial with) {
		int topBlockX = (from.getBlockX() < to.getBlockX() ? to.getBlockX() : from.getBlockX());
		int bottomBlockX = (from.getBlockX() > to.getBlockX() ? to.getBlockX() : from.getBlockX());
		int topBlockY = (from.getBlockY() < to.getBlockY() ? to.getBlockY() : from.getBlockY());
		int bottomBlockY = (from.getBlockY() > to.getBlockY() ? to.getBlockY() : from.getBlockY());
		int topBlockZ = (from.getBlockZ() < to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
		int bottomBlockZ = (from.getBlockZ() > to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
		World w = from.getWorld();
		for (int x = bottomBlockX; x <= topBlockX; x++) {
			for (int z = bottomBlockZ; z <= topBlockZ; z++) {
				for (int y = bottomBlockY; y <= topBlockY; y++) {
					Position s = new Position(w, x, y, z);
					if (block.contains(s.getType()))
						s.setType(with);
				}
			}
		}
	}

	public void replace(Position from, Position to, List<TheMaterial> block, List<TheMaterial> with) {
		int topBlockX = (from.getBlockX() < to.getBlockX() ? to.getBlockX() : from.getBlockX());
		int bottomBlockX = (from.getBlockX() > to.getBlockX() ? to.getBlockX() : from.getBlockX());
		int topBlockY = (from.getBlockY() < to.getBlockY() ? to.getBlockY() : from.getBlockY());
		int bottomBlockY = (from.getBlockY() > to.getBlockY() ? to.getBlockY() : from.getBlockY());
		int topBlockZ = (from.getBlockZ() < to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
		int bottomBlockZ = (from.getBlockZ() > to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
		World w = from.getWorld();
		for (int x = bottomBlockX; x <= topBlockX; x++) {
			for (int z = bottomBlockZ; z <= topBlockZ; z++) {
				for (int y = bottomBlockY; y <= topBlockY; y++) {
					Position s = new Position(w, x, y, z);
					if (block.contains(s.getType()))
						s.setType((TheMaterial) TheAPI.getRandomFromList(with));
				}
			}
		}
	}

	public void set(Shape form, Position where, int radius, TheMaterial block) {
		World w = where.getWorld();
		int Xx = where.getBlockX();
		int Yy = where.getBlockY();
		int Zz = where.getBlockZ();
		switch (form) {
		case Square:
			for (int x = Xx - radius; x <= Xx + radius; x++)
				for (int y = Yy - radius; y <= Yy + radius; y++)
					for (int z = Zz - radius; z <= Zz + radius; z++) {
						Position s = new Position(w, x, y, z);
						s.setType(block);
					}
			break;
		case Sphere:
			for (int Y = -radius; Y < radius; Y++)
				for (int X = -radius; X < radius; X++)
					for (int Z = -radius; Z < radius; Z++)
						if (Math.sqrt((X * X) + (Y * Y) + (Z * Z)) <= radius) {
							Position s = new Position(w, X + Xx, Y + Yy, Z + Zz);
							s.setType(block);
						}
		}
	}

	public void set(Shape form, Position where, int radius, TheMaterial block, List<TheMaterial> ignore) {
		World w = where.getWorld();
		int Xx = where.getBlockX();
		int Yy = where.getBlockY();
		int Zz = where.getBlockZ();
		switch (form) {
		case Square:
			for (int x = Xx - radius; x <= Xx + radius; x++)
				for (int y = Yy - radius; y <= Yy + radius; y++)
					for (int z = Zz - radius; z <= Zz + radius; z++) {
						Position s = new Position(w, x, y, z);
						if (!ignore.contains(s.getType()))
							s.setType(block);
					}
			break;
		case Sphere:
			for (int Y = -radius; Y < radius; Y++)
				for (int X = -radius; X < radius; X++)
					for (int Z = -radius; Z < radius; Z++)
						if (Math.sqrt((X * X) + (Y * Y) + (Z * Z)) <= radius) {
							Position s = new Position(w, X + Xx, Y + Yy, Z + Zz);
							if (!ignore.contains(s.getType()))
								s.setType(block);
						}
		}
	}

	public void set(Shape form, Position where, int radius, TheMaterial block, TheMaterial ignore) {
		World w = where.getWorld();
		int Xx = where.getBlockX();
		int Yy = where.getBlockY();
		int Zz = where.getBlockZ();
		switch (form) {
		case Square:
			for (int x = Xx - radius; x <= Xx + radius; x++)
				for (int y = Yy - radius; y <= Yy + radius; y++)
					for (int z = Zz - radius; z <= Zz + radius; z++) {
						Position s = new Position(w, x, y, z);
						if (ignore != s.getType())
							s.setType(block);
					}
			break;
		case Sphere:
			for (int Y = -radius; Y < radius; Y++)
				for (int X = -radius; X < radius; X++)
					for (int Z = -radius; Z < radius; Z++)
						if (Math.sqrt((X * X) + (Y * Y) + (Z * Z)) <= radius) {
							Position s = new Position(w, X + Xx, Y + Yy, Z + Zz);
							if (ignore != s.getType())
								s.setType(block);
						}
		}
	}

	public void set(Shape form, Position where, int radius, List<TheMaterial> block) {
		World w = where.getWorld();
		int Xx = where.getBlockX();
		int Yy = where.getBlockY();
		int Zz = where.getBlockZ();
		switch (form) {
		case Square:
			for (int x = Xx - radius; x <= Xx + radius; x++)
				for (int y = Yy - radius; y <= Yy + radius; y++)
					for (int z = Zz - radius; z <= Zz + radius; z++) {
						Position s = new Position(w, x, y, z);
						s.setType((TheMaterial) TheAPI.getRandomFromList(block));
					}
			break;
		case Sphere:
			for (int Y = -radius; Y < radius; Y++)
				for (int X = -radius; X < radius; X++)
					for (int Z = -radius; Z < radius; Z++)
						if (Math.sqrt((X * X) + (Y * Y) + (Z * Z)) <= radius) {
							Position s = new Position(w, X + Xx, Y + Yy, Z + Zz);
							s.setType((TheMaterial) TheAPI.getRandomFromList(block));
						}
		}
	}

	public void set(Shape form, Position where, int radius, List<TheMaterial> block, List<TheMaterial> ignore) {
		World w = where.getWorld();
		int Xx = where.getBlockX();
		int Yy = where.getBlockY();
		int Zz = where.getBlockZ();
		switch (form) {
		case Square:
			for (int x = Xx - radius; x <= Xx + radius; x++)
				for (int y = Yy - radius; y <= Yy + radius; y++)
					for (int z = Zz - radius; z <= Zz + radius; z++) {
						Position s = new Position(w, x, y, z);
						if (!ignore.contains(s.getType()))
							s.setType((TheMaterial) TheAPI.getRandomFromList(block));
					}
			break;
		case Sphere:
			for (int Y = -radius; Y < radius; Y++)
				for (int X = -radius; X < radius; X++)
					for (int Z = -radius; Z < radius; Z++)
						if (Math.sqrt((X * X) + (Y * Y) + (Z * Z)) <= radius) {
							Position s = new Position(w, X + Xx, Y + Yy, Z + Zz);
							if (!ignore.contains(s.getType()))
								s.setType((TheMaterial) TheAPI.getRandomFromList(block));
						}
		}
	}

	public void set(Shape form, Position where, int radius, List<TheMaterial> block, TheMaterial ignore) {
		World w = where.getWorld();
		int Xx = where.getBlockX();
		int Yy = where.getBlockY();
		int Zz = where.getBlockZ();
		switch (form) {
		case Square:
			for (int x = Xx - radius; x <= Xx + radius; x++)
				for (int y = Yy - radius; y <= Yy + radius; y++)
					for (int z = Zz - radius; z <= Zz + radius; z++) {
						Position s = new Position(w, x, y, z);
						if (ignore != s.getType())
							s.setType((TheMaterial) TheAPI.getRandomFromList(block));
					}
			break;
		case Sphere:
			for (int Y = -radius; Y < radius; Y++)
				for (int X = -radius; X < radius; X++)
					for (int Z = -radius; Z < radius; Z++)
						if (Math.sqrt((X * X) + (Y * Y) + (Z * Z)) <= radius) {
							Position s = new Position(w, X + Xx, Y + Yy, Z + Zz);
							if (ignore != s.getType())
								s.setType((TheMaterial) TheAPI.getRandomFromList(block));
						}
		}
	}

	public void set(Shape form, Position where, int radius, HashMap<TheMaterial, Double> block) {
		List<TheMaterial> c = Lists.newArrayList();
		for (TheMaterial m : block.keySet())
			for (int i = -1; i > block.get(m); ++i)
				c.add(m);
		World w = where.getWorld();
		int Xx = where.getBlockX();
		int Yy = where.getBlockY();
		int Zz = where.getBlockZ();
		switch (form) {
		case Square:
			for (int x = Xx - radius; x <= Xx + radius; x++)
				for (int y = Yy - radius; y <= Yy + radius; y++)
					for (int z = Zz - radius; z <= Zz + radius; z++) {
						Position s = new Position(w, x, y, z);
						TheMaterial b = (TheMaterial) TheAPI.getRandomFromList(c);
						if (TheAPI.generateChance(block.get(b)))
							s.setType(b);

					}
			break;
		case Sphere:
			for (int Y = -radius; Y < radius; Y++)
				for (int X = -radius; X < radius; X++)
					for (int Z = -radius; Z < radius; Z++)
						if (Math.sqrt((X * X) + (Y * Y) + (Z * Z)) <= radius) {
							Position s = new Position(w, X + Xx, Y + Yy, Z + Zz);
							TheMaterial b = (TheMaterial) TheAPI.getRandomFromList(c);
							if (TheAPI.generateChance(block.get(b)))
								s.setType(b);

						}
		}
	}

	public void set(Shape form, Position where, int radius, HashMap<TheMaterial, Double> block,
			List<TheMaterial> ignore) {
		List<TheMaterial> c = Lists.newArrayList();
		for (TheMaterial m : block.keySet())
			for (int i = -1; i > block.get(m); ++i)
				c.add(m);
		World w = where.getWorld();
		int Xx = where.getBlockX();
		int Yy = where.getBlockY();
		int Zz = where.getBlockZ();
		switch (form) {
		case Square:
			for (int x = Xx - radius; x <= Xx + radius; x++)
				for (int y = Yy - radius; y <= Yy + radius; y++)
					for (int z = Zz - radius; z <= Zz + radius; z++) {
						Position s = new Position(w, x, y, z);
						if (!ignore.contains(s.getType())) {
							TheMaterial b = (TheMaterial) TheAPI.getRandomFromList(c);
							if (TheAPI.generateChance(block.get(b)))
								s.setType(b);
						}
					}
			break;
		case Sphere:
			for (int Y = -radius; Y < radius; Y++)
				for (int X = -radius; X < radius; X++)
					for (int Z = -radius; Z < radius; Z++)
						if (Math.sqrt((X * X) + (Y * Y) + (Z * Z)) <= radius) {
							Position s = new Position(w, X + Xx, Y + Yy, Z + Zz);
							if (!ignore.contains(s.getType())) {
								TheMaterial b = (TheMaterial) TheAPI.getRandomFromList(c);
								if (TheAPI.generateChance(block.get(b)))
									s.setType(b);
							}
						}
		}
	}

	public void set(Shape form, Position where, int radius, HashMap<TheMaterial, Double> block, TheMaterial ignore) {
		List<TheMaterial> c = Lists.newArrayList();
		for (TheMaterial m : block.keySet())
			for (int i = -1; i > block.get(m); ++i)
				c.add(m);
		World w = where.getWorld();
		int Xx = where.getBlockX();
		int Yy = where.getBlockY();
		int Zz = where.getBlockZ();
		switch (form) {
		case Square:
			for (int x = Xx - radius; x <= Xx + radius; x++)
				for (int y = Yy - radius; y <= Yy + radius; y++)
					for (int z = Zz - radius; z <= Zz + radius; z++) {
						Position s = new Position(w, x, y, z);
						if (ignore != s.getType()) {
							TheMaterial b = (TheMaterial) TheAPI.getRandomFromList(c);
							if (TheAPI.generateChance(block.get(b)))
								s.setType(b);
						}
					}
			break;
		case Sphere:
			for (int Y = -radius; Y < radius; Y++)
				for (int X = -radius; X < radius; X++)
					for (int Z = -radius; Z < radius; Z++)
						if (Math.sqrt((X * X) + (Y * Y) + (Z * Z)) <= radius) {
							Position s = new Position(w, X + Xx, Y + Yy, Z + Zz);
							if (ignore != s.getType()) {
								TheMaterial b = (TheMaterial) TheAPI.getRandomFromList(c);
								if (TheAPI.generateChance(block.get(b)))
									s.setType(b);
							}
						}
		}
	}

	public void set(Position from, Position to, TheMaterial block) {
		int topBlockX = (from.getBlockX() < to.getBlockX() ? to.getBlockX() : from.getBlockX());
		int bottomBlockX = (from.getBlockX() > to.getBlockX() ? to.getBlockX() : from.getBlockX());
		int topBlockY = (from.getBlockY() < to.getBlockY() ? to.getBlockY() : from.getBlockY());
		int bottomBlockY = (from.getBlockY() > to.getBlockY() ? to.getBlockY() : from.getBlockY());
		int topBlockZ = (from.getBlockZ() < to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
		int bottomBlockZ = (from.getBlockZ() > to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
		World w = from.getWorld();
		for (int x = bottomBlockX; x <= topBlockX; x++) {
			for (int z = bottomBlockZ; z <= topBlockZ; z++) {
				for (int y = bottomBlockY; y <= topBlockY; y++) {
					Position s = new Position(w, x, y, z);
					s.setType(block);
				}
			}
		}
	}

	public void set(Position from, Position to, TheMaterial block, List<TheMaterial> ignore) {
		int topBlockX = (from.getBlockX() < to.getBlockX() ? to.getBlockX() : from.getBlockX());
		int bottomBlockX = (from.getBlockX() > to.getBlockX() ? to.getBlockX() : from.getBlockX());
		int topBlockY = (from.getBlockY() < to.getBlockY() ? to.getBlockY() : from.getBlockY());
		int bottomBlockY = (from.getBlockY() > to.getBlockY() ? to.getBlockY() : from.getBlockY());
		int topBlockZ = (from.getBlockZ() < to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
		int bottomBlockZ = (from.getBlockZ() > to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
		World w = from.getWorld();
		for (int x = bottomBlockX; x <= topBlockX; x++) {
			for (int z = bottomBlockZ; z <= topBlockZ; z++) {
				for (int y = bottomBlockY; y <= topBlockY; y++) {
					Position s = new Position(w, x, y, z);
					if (!ignore.contains(s.getType()))
						s.setType(block);
				}
			}
		}
	}

	public void set(Position from, Position to, TheMaterial block, TheMaterial ignore) {
		int topBlockX = (from.getBlockX() < to.getBlockX() ? to.getBlockX() : from.getBlockX());
		int bottomBlockX = (from.getBlockX() > to.getBlockX() ? to.getBlockX() : from.getBlockX());
		int topBlockY = (from.getBlockY() < to.getBlockY() ? to.getBlockY() : from.getBlockY());
		int bottomBlockY = (from.getBlockY() > to.getBlockY() ? to.getBlockY() : from.getBlockY());
		int topBlockZ = (from.getBlockZ() < to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
		int bottomBlockZ = (from.getBlockZ() > to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
		World w = from.getWorld();
		for (int x = bottomBlockX; x <= topBlockX; x++) {
			for (int z = bottomBlockZ; z <= topBlockZ; z++) {
				for (int y = bottomBlockY; y <= topBlockY; y++) {
					Position s = new Position(w, x, y, z);
					if (ignore != s.getType())
						s.setType(block);
				}
			}
		}
	}

	public void set(Position from, Position to, List<TheMaterial> block) {
		int topBlockX = (from.getBlockX() < to.getBlockX() ? to.getBlockX() : from.getBlockX());
		int bottomBlockX = (from.getBlockX() > to.getBlockX() ? to.getBlockX() : from.getBlockX());
		int topBlockY = (from.getBlockY() < to.getBlockY() ? to.getBlockY() : from.getBlockY());
		int bottomBlockY = (from.getBlockY() > to.getBlockY() ? to.getBlockY() : from.getBlockY());
		int topBlockZ = (from.getBlockZ() < to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
		int bottomBlockZ = (from.getBlockZ() > to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
		World w = from.getWorld();
		for (int x = bottomBlockX; x <= topBlockX; x++) {
			for (int z = bottomBlockZ; z <= topBlockZ; z++) {
				for (int y = bottomBlockY; y <= topBlockY; y++) {
					Position s = new Position(w, x, y, z);
					s.setType((TheMaterial) TheAPI.getRandomFromList(block));
				}
			}
		}
	}

	public void set(Position from, Position to, List<TheMaterial> block, List<TheMaterial> ignore) {
		int topBlockX = (from.getBlockX() < to.getBlockX() ? to.getBlockX() : from.getBlockX());
		int bottomBlockX = (from.getBlockX() > to.getBlockX() ? to.getBlockX() : from.getBlockX());
		int topBlockY = (from.getBlockY() < to.getBlockY() ? to.getBlockY() : from.getBlockY());
		int bottomBlockY = (from.getBlockY() > to.getBlockY() ? to.getBlockY() : from.getBlockY());
		int topBlockZ = (from.getBlockZ() < to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
		int bottomBlockZ = (from.getBlockZ() > to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
		World w = from.getWorld();
		for (int x = bottomBlockX; x <= topBlockX; x++) {
			for (int z = bottomBlockZ; z <= topBlockZ; z++) {
				for (int y = bottomBlockY; y <= topBlockY; y++) {
					Position s = new Position(w, x, y, z);
					if (!ignore.contains(s.getType()))
						s.setType((TheMaterial) TheAPI.getRandomFromList(block));
				}
			}
		}
	}

	public void set(Position from, Position to, List<TheMaterial> block, TheMaterial ignore) {
		int topBlockX = (from.getBlockX() < to.getBlockX() ? to.getBlockX() : from.getBlockX());
		int bottomBlockX = (from.getBlockX() > to.getBlockX() ? to.getBlockX() : from.getBlockX());
		int topBlockY = (from.getBlockY() < to.getBlockY() ? to.getBlockY() : from.getBlockY());
		int bottomBlockY = (from.getBlockY() > to.getBlockY() ? to.getBlockY() : from.getBlockY());
		int topBlockZ = (from.getBlockZ() < to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
		int bottomBlockZ = (from.getBlockZ() > to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
		World w = from.getWorld();
		for (int x = bottomBlockX; x <= topBlockX; x++) {
			for (int z = bottomBlockZ; z <= topBlockZ; z++) {
				for (int y = bottomBlockY; y <= topBlockY; y++) {
					Position s = new Position(w, x, y, z);
					if (ignore != s.getType())
						s.setType((TheMaterial) TheAPI.getRandomFromList(block));
				}
			}
		}
	}

	public void set(Position from, Position to, HashMap<TheMaterial, Double> block) {
		List<TheMaterial> c = Lists.newArrayList();
		for (TheMaterial m : block.keySet())
			for (int i = -1; i > block.get(m); ++i)
				c.add(m);
		int topBlockX = (from.getBlockX() < to.getBlockX() ? to.getBlockX() : from.getBlockX());
		int bottomBlockX = (from.getBlockX() > to.getBlockX() ? to.getBlockX() : from.getBlockX());
		int topBlockY = (from.getBlockY() < to.getBlockY() ? to.getBlockY() : from.getBlockY());
		int bottomBlockY = (from.getBlockY() > to.getBlockY() ? to.getBlockY() : from.getBlockY());
		int topBlockZ = (from.getBlockZ() < to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
		int bottomBlockZ = (from.getBlockZ() > to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
		World w = from.getWorld();
		for (int x = bottomBlockX; x <= topBlockX; x++) {
			for (int z = bottomBlockZ; z <= topBlockZ; z++) {
				for (int y = bottomBlockY; y <= topBlockY; y++) {
					Position s = new Position(w, x, y, z);
					TheMaterial b = (TheMaterial) TheAPI.getRandomFromList(c);
					if (TheAPI.generateChance(block.get(b)))
						s.setType(b);
				}
			}
		}
	}

	public void set(Position from, Position to, HashMap<TheMaterial, Double> block, List<TheMaterial> ignore) {
		List<TheMaterial> c = Lists.newArrayList();
		for (TheMaterial m : block.keySet())
			for (int i = -1; i > block.get(m); ++i)
				c.add(m);
		int topBlockX = (from.getBlockX() < to.getBlockX() ? to.getBlockX() : from.getBlockX());
		int bottomBlockX = (from.getBlockX() > to.getBlockX() ? to.getBlockX() : from.getBlockX());
		int topBlockY = (from.getBlockY() < to.getBlockY() ? to.getBlockY() : from.getBlockY());
		int bottomBlockY = (from.getBlockY() > to.getBlockY() ? to.getBlockY() : from.getBlockY());
		int topBlockZ = (from.getBlockZ() < to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
		int bottomBlockZ = (from.getBlockZ() > to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
		World w = from.getWorld();
		for (int x = bottomBlockX; x <= topBlockX; x++) {
			for (int z = bottomBlockZ; z <= topBlockZ; z++) {
				for (int y = bottomBlockY; y <= topBlockY; y++) {
					Position s = new Position(w, x, y, z);
					if (!ignore.contains(s.getType())) {
						TheMaterial b = (TheMaterial) TheAPI.getRandomFromList(c);
						if (TheAPI.generateChance(block.get(b)))
							s.setType(b);
					}
				}
			}
		}
	}

	public void set(Position from, Position to, HashMap<TheMaterial, Double> block, TheMaterial ignore) {
		List<TheMaterial> c = Lists.newArrayList();
		for (TheMaterial m : block.keySet())
			for (int i = -1; i > block.get(m); ++i)
				c.add(m);
		int topBlockX = (from.getBlockX() < to.getBlockX() ? to.getBlockX() : from.getBlockX());
		int bottomBlockX = (from.getBlockX() > to.getBlockX() ? to.getBlockX() : from.getBlockX());
		int topBlockY = (from.getBlockY() < to.getBlockY() ? to.getBlockY() : from.getBlockY());
		int bottomBlockY = (from.getBlockY() > to.getBlockY() ? to.getBlockY() : from.getBlockY());
		int topBlockZ = (from.getBlockZ() < to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
		int bottomBlockZ = (from.getBlockZ() > to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
		World w = from.getWorld();
		for (int x = bottomBlockX; x <= topBlockX; x++) {
			for (int z = bottomBlockZ; z <= topBlockZ; z++) {
				for (int y = bottomBlockY; y <= topBlockY; y++) {
					Position s = new Position(w, x, y, z);
					if (ignore != s.getType()) {
						TheMaterial b = (TheMaterial) TheAPI.getRandomFromList(c);
						if (TheAPI.generateChance(block.get(b)))
							s.setType(b);
					}
				}
			}
		}
	}

	public boolean isInside(Entity entity, Position a, Position b) {
		return isInside(new Position(entity.getLocation()), a, b);
	}

	public boolean isInside(Position loc, Position a, Position b) {
		int xMin = Math.min(a.getBlockX(), b.getBlockX());
		 int yMin = Math.min(a.getBlockY(), b.getBlockY());
		 int zMin = Math.min(a.getBlockZ(), b.getBlockZ());
			int xMax = Math.max(a.getBlockX(), b.getBlockX());
			 int yMax = Math.max(a.getBlockY(), b.getBlockY());
			 int zMax = Math.max(a.getBlockZ(), b.getBlockZ());
			 return loc.getWorld() == a.getWorld() && loc.getBlockX() >= xMin && loc.getBlockX() <= xMax && loc.getBlockY() >= yMin && loc.getBlockY() <= yMax && loc
		                .getBlockZ() >= zMin && loc.getBlockZ() <= zMax;
	}

	public boolean isInside(Entity entity, Location a, Location b) {
		return isInside(entity.getLocation(), a, b);
	}

	public boolean isInside(Location loc, Location a, Location b) {
		int xMin = Math.min(a.getBlockX(), b.getBlockX());
		 int yMin = Math.min(a.getBlockY(), b.getBlockY());
		 int zMin = Math.min(a.getBlockZ(), b.getBlockZ());
			int xMax = Math.max(a.getBlockX(), b.getBlockX());
			 int yMax = Math.max(a.getBlockY(), b.getBlockY());
			 int zMax = Math.max(a.getBlockZ(), b.getBlockZ());
			 return loc.getWorld() == a.getWorld() && loc.getBlockX() >= xMin && loc.getBlockX() <= xMax && loc.getBlockY() >= yMin && loc.getBlockY() <= yMax && loc
		                .getBlockZ() >= zMin && loc.getBlockZ() <= zMax;
	}

	// Synchronized part
	public void synchronizedSet(Position a, Position b, TheMaterial with) {
		synchronizedSet(a, b, new Runnable() {public void run() {}}, with);
	}

	public void synchronizedSet(Position a, Position b, TheMaterial with, TheMaterial ignore) {
		synchronizedSet(a, b, new Runnable() {public void run() {}}, with, ignore);
	}

	public void synchronizedSet(Position a, Position b, TheMaterial with, List<TheMaterial> ignore) {
		synchronizedSet(a, b, new Runnable() {public void run() {}}, with, ignore);
	}

	public void synchronizedSet(Position a, Position b, List<TheMaterial> with, List<TheMaterial> ignore) {
		synchronizedSet(a, b, new Runnable() {public void run() {}}, with, ignore);
	}

	public void synchronizedSet(Position a, Position b, List<TheMaterial> with) {
		synchronizedSet(a, b, new Runnable() {public void run() {}}, with);
	}

	public void synchronizedSet(Position a, Position b, HashMap<TheMaterial, Double> with, List<TheMaterial> ignore) {
		synchronizedSet(a, b, new Runnable() {public void run() {}}, with, ignore);
	}

	public void synchronizedSet(Position a, Position b, HashMap<TheMaterial, Double> with, TheMaterial ignore) {
		synchronizedSet(a, b, new Runnable() {public void run() {}}, with, ignore);
	}

	public void synchronizedReplace(Position a, Position b, List<TheMaterial> block, TheMaterial with) {
		synchronizedReplace(a, b, new Runnable() {public void run() {}}, block, with);
	}

	public void synchronizedReplace(Position a, Position b, TheMaterial block, TheMaterial with) {
		synchronizedReplace(a, b, new Runnable() {public void run() {}}, block, with);
	}

	public void synchronizedReplace(Position a, Position b, TheMaterial block, HashMap<TheMaterial, Double> with) {
		synchronizedReplace(a, b, new Runnable() {public void run() {}}, block, with);
	}

	public void synchronizedReplace(Position a, Position b, TheMaterial block, List<TheMaterial> with) {
		synchronizedReplace(a, b, new Runnable() {public void run() {}}, block, with);
	}

	public void synchronizedReplace(Position a, Position b, List<TheMaterial> block, List<TheMaterial> with) {
		synchronizedReplace(a, b, new Runnable() {public void run() {}}, block, with);
	}

	public void synchronizedReplace(Position a, Position b, HashMap<TheMaterial, Double> block,
			List<TheMaterial> with) {
		synchronizedReplace(a, b, new Runnable() {public void run() {}}, block, with);
	}

	public void synchronizedReplace(Position a, Position b, HashMap<TheMaterial, Double> block,
			HashMap<TheMaterial, Double> with) {
		synchronizedReplace(a, b, new Runnable() {public void run() {}}, block, with);
	}

	public void synchronizedReplace(Position a, Position b, HashMap<TheMaterial, Double> block, TheMaterial with) {
		synchronizedReplace(a, b, new Runnable() {public void run() {}}, block, with);
	}

	private final BlockTask state = new BlockTask() {
		@Override
		public long set(Position block, TheMaterial toSet) {
			return block.setType(toSet);
		}
		
		@Override
		public long replace(Position block, List<TheMaterial> toReplace, TheMaterial toSet) {
			if(toReplace.contains(get(block)))
				return block.setType(toSet);
			return 0;
		}
		
		@Override
		public long replace(Position block, TheMaterial toReplace, TheMaterial toSet) {
			if(toReplace.equals(get(block)))
				return block.setType(toSet);
			return 0;
		}
		
		@Override
		public TheMaterial get(Position block) {
			return block.getType();
		}

		@Override
		public long set(Position block, TheMaterial toSet, TheMaterial ignore) {
			if(!ignore.equals(get(block)))
				return block.setType(toSet);
			return 0;
		}

		@Override
		public long set(Position block, TheMaterial toSet, List<TheMaterial> ignore) {
			if(!ignore.contains(get(block)))
				return block.setType(toSet);
			return 0;
		}
	};
	
	//Synchronized & Runnable on finish part
	public void synchronizedSet(Position a, Position b, Runnable onFinish, TheMaterial with) {
		BlockGetter s = get(a, b);
		ConfigAPI caw = null;
		for(int iaa = 0; iaa > -1; ++iaa) {
			if(!new File("TheAPI/ChunkTask/"+iaa).exists()) {
				caw = new ConfigAPI("TheAPI/ChunkTask", ""+iaa);
				caw.create();
				break;
			}
		}
		ConfigAPI ca = caw;
		new Tasker() {
			@Override
			public void run() {
				for(int i = 0; i < amount; ++i) {
					if (s.has()) {
						Position get = s.get();
						long key = state.set(get, with);
						ca.set(key+"", new int[] {get.getBlockX()>>4,get.getBlockZ()>>4});
					} else
						break;
				}
				if (!s.has()) {
					cancel();
					Object w = Ref.world(a.getWorld());
					for(String o : ca.getKeys(false)) {
						int[] cw = (int[])ca.get(o);
						Object a=Ref.newInstanceNms("PacketPlayOutMapChunk", Ref.invoke(w, "getChunkAt", cw[0], cw[1]), 65535);
						for(Player p : Bukkit.getOnlinePlayers())
							Ref.sendPacket(p, a);
					}
					if(onFinish!=null)
					onFinish.run();
				}
			}
		}.repeating(0, 3);
	}

	public void synchronizedSet(Position a, Position b, Runnable onFinish, TheMaterial with, TheMaterial ignore) {
		BlockGetter s = get(a, b);
		ConfigAPI caw = null;
		for(int iaa = 0; iaa > -1; ++iaa) {
			if(!new File("TheAPI/ChunkTask/"+iaa).exists()) {
				caw = new ConfigAPI("TheAPI/ChunkTask", ""+iaa);
				caw.create();
				break;
			}
		}
		ConfigAPI ca = caw;
		new Tasker() {
			@Override
			public void run() {
				for(int i = 0; i < amount; ++i) {
					if (s.has()) {
						Position get = s.get();
						long key = state.set(get, with, ignore);
						ca.set(key+"", new int[] {get.getBlockX()>>4,get.getBlockZ()>>4});
					} else
						break;
				}
				if (!s.has()) {
					cancel();
					Object w = Ref.world(a.getWorld());
					for(String o : ca.getKeys(false)) {
						int[] cw = (int[])ca.get(o);
						Object a=Ref.newInstanceNms("PacketPlayOutMapChunk", Ref.invoke(w, "getChunkAt", cw[0], cw[1]), 65535);
						for(Player p : Bukkit.getOnlinePlayers())
							Ref.sendPacket(p, a);
					}
					if(onFinish!=null)
					onFinish.run();
				}
			}
		}.repeating(0, 3);
	}

	public void synchronizedSet(Position a, Position b, Runnable onFinish, TheMaterial with, List<TheMaterial> ignore) {
		BlockGetter s = get(a, b);
		ConfigAPI caw = null;
		for(int iaa = 0; iaa > -1; ++iaa) {
			if(!new File("TheAPI/ChunkTask/"+iaa).exists()) {
				caw = new ConfigAPI("TheAPI/ChunkTask", ""+iaa);
				caw.create();
				break;
			}
		}
		ConfigAPI ca = caw;
		new Tasker() {
			@Override
			public void run() {
				for(int i = 0; i < amount; ++i) {
					if (s.has()) {
						Position get = s.get();
						long key = state.set(get, with, ignore);
						ca.set(key+"", new int[] {get.getBlockX()>>4,get.getBlockZ()>>4});
					} else
						break;
				}
				if (!s.has()) {
					cancel();
					Object w = Ref.world(a.getWorld());
					for(String o : ca.getKeys(false)) {
						int[] cw = (int[])ca.get(o);
						Object a=Ref.newInstanceNms("PacketPlayOutMapChunk", Ref.invoke(w, "getChunkAt", cw[0], cw[1]), 65535);
						for(Player p : Bukkit.getOnlinePlayers())
							Ref.sendPacket(p, a);
					}
					if(onFinish!=null)
					onFinish.run();
				}
			}
		}.repeating(0, 3);
	}

	public void synchronizedSet(Position a, Position b, Runnable onFinish, List<TheMaterial> with, List<TheMaterial> ignore) {
		BlockGetter s = get(a, b);
		ConfigAPI caw = null;
		for(int iaa = 0; iaa > -1; ++iaa) {
			if(!new File("TheAPI/ChunkTask/"+iaa).exists()) {
				caw = new ConfigAPI("TheAPI/ChunkTask", ""+iaa);
				caw.create();
				break;
			}
		}
		ConfigAPI ca = caw;
		new Tasker() {
			@Override
			public void run() {
				for(int i = 0; i < amount; ++i) {
					if (s.has()) {
						Position get = s.get();
						long key = state.set(get, (TheMaterial)TheAPI.getRandomFromList(with), ignore);
						ca.set(key+"", new int[] {get.getBlockX()>>4,get.getBlockZ()>>4});
					} else
						break;
				}
				if (!s.has()) {
					cancel();
					Object w = Ref.world(a.getWorld());
					for(String o : ca.getKeys(false)) {
						int[] cw = (int[])ca.get(o);
						Object a=Ref.newInstanceNms("PacketPlayOutMapChunk", Ref.invoke(w, "getChunkAt", cw[0], cw[1]), 65535);
						for(Player p : Bukkit.getOnlinePlayers())
							Ref.sendPacket(p, a);
					}
					if(onFinish!=null)
					onFinish.run();
				}
			}
		}.repeating(0, 3);
	}

	public void synchronizedSet(Position a, Position b, Runnable onFinish, List<TheMaterial> with) {
		BlockGetter s = get(a, b);
		ConfigAPI caw = null;
		for(int iaa = 0; iaa > -1; ++iaa) {
			if(!new File("TheAPI/ChunkTask/"+iaa).exists()) {
				caw = new ConfigAPI("TheAPI/ChunkTask", ""+iaa);
				caw.create();
				break;
			}
		}
		ConfigAPI ca = caw;
		new Tasker() {
			@Override
			public void run() {
				for(int i = 0; i < amount; ++i) {
					if (s.has()) {
						Position get = s.get();
						long key = state.set(get, (TheMaterial)TheAPI.getRandomFromList(with));
						ca.set(key+"", new int[] {get.getBlockX()>>4,get.getBlockZ()>>4});
					} else
						break;
				}
				if (!s.has()) {
					cancel();
					Object w = Ref.world(a.getWorld());
					for(String o : ca.getKeys(false)) {
						int[] cw = (int[])ca.get(o);
						Object a=Ref.newInstanceNms("PacketPlayOutMapChunk", Ref.invoke(w, "getChunkAt", cw[0], cw[1]), 65535);
						for(Player p : Bukkit.getOnlinePlayers())
							Ref.sendPacket(p, a);
					}
					if(onFinish!=null)
					onFinish.run();
				}
			}
		}.repeating(0, 3);
	}

	public void synchronizedSet(Position a, Position b, Runnable onFinish, HashMap<TheMaterial, Double> with, List<TheMaterial> ignore) {
		BlockGetter s = get(a, b);
		PercentageList<TheMaterial> c = new PercentageList<>();
		for (TheMaterial m : with.keySet())
				c.add(m, with.get(m));
		ConfigAPI caw = null;
		for(int iaa = 0; iaa > -1; ++iaa) {
			if(!new File("TheAPI/ChunkTask/"+iaa).exists()) {
				caw = new ConfigAPI("TheAPI/ChunkTask", ""+iaa);
				caw.create();
				break;
			}
		}
		ConfigAPI ca = caw;
		new Tasker() {
			@Override
			public void run() {
				for(int i = 0; i < amount; ++i) {
					if (s.has()) {
						Position get = s.get();
						long key = state.set(get, c.getRandom(), ignore);
						ca.set(key+"", new int[] {get.getBlockX()>>4,get.getBlockZ()>>4});
					} else
						break;
				}
				if (!s.has()) {
					cancel();
					Object w = Ref.world(a.getWorld());
					for(String o : ca.getKeys(false)) {
						int[] cw = (int[])ca.get(o);
						Object a=Ref.newInstanceNms("PacketPlayOutMapChunk", Ref.invoke(w, "getChunkAt", cw[0], cw[1]), 65535);
						for(Player p : Bukkit.getOnlinePlayers())
							Ref.sendPacket(p, a);
					}
					if(onFinish!=null)
					onFinish.run();
				}
			}
		}.repeating(0, 3);
	}

	public void synchronizedSet(Position a, Position b, Runnable onFinish, HashMap<TheMaterial, Double> with, TheMaterial ignore) {
		BlockGetter s = get(a, b);
		PercentageList<TheMaterial> c = new PercentageList<>();
		for (TheMaterial m : with.keySet())
				c.add(m, with.get(m));
		ConfigAPI caw = null;
		for(int iaa = 0; iaa > -1; ++iaa) {
			if(!new File("TheAPI/ChunkTask/"+iaa).exists()) {
				caw = new ConfigAPI("TheAPI/ChunkTask", ""+iaa);
				caw.create();
				break;
			}
		}
		ConfigAPI ca = caw;
		new Tasker() {
			@Override
			public void run() {
				for(int i = 0; i < amount; ++i) {
					if (s.has()) {
						Position get = s.get();
						long key = state.set(get, c.getRandom(), ignore);
						ca.set(key+"", new int[] {get.getBlockX()>>4,get.getBlockZ()>>4});
					} else
						break;
				}
				if (!s.has()) {
					cancel();
					Object w = Ref.world(a.getWorld());
					for(String o : ca.getKeys(false)) {
						int[] cw = (int[])ca.get(o);
						Object a=Ref.newInstanceNms("PacketPlayOutMapChunk", Ref.invoke(w, "getChunkAt", cw[0], cw[1]), 65535);
						for(Player p : Bukkit.getOnlinePlayers())
							Ref.sendPacket(p, a);
					}
					if(onFinish!=null)
					onFinish.run();
				}
			}
		}.repeating(0, 3);
	}

	public void synchronizedReplace(Position a, Position b, Runnable onFinish, List<TheMaterial> block, TheMaterial with) {
		BlockGetter s = get(a, b);
		ConfigAPI caw = null;
		for(int iaa = 0; iaa > -1; ++iaa) {
			if(!new File("TheAPI/ChunkTask/"+iaa).exists()) {
				caw = new ConfigAPI("TheAPI/ChunkTask", ""+iaa);
				caw.create();
				break;
			}
		}
		ConfigAPI ca = caw;
		new Tasker() {
			@Override
			public void run() {
				for(int i = 0; i < amount; ++i) {
					if (s.has()) {
						Position get = s.get();
						long key = state.replace(get, block, with);
						ca.set(key+"", new int[] {get.getBlockX()>>4,get.getBlockZ()>>4});
					} else
						break;
				}
				if (!s.has()) {
					cancel();
					Object w = Ref.world(a.getWorld());
					for(String o : ca.getKeys(false)) {
						int[] cw = (int[])ca.get(o);
						Object a=Ref.newInstanceNms("PacketPlayOutMapChunk", Ref.invoke(w, "getChunkAt", cw[0], cw[1]), 65535);
						for(Player p : Bukkit.getOnlinePlayers())
							Ref.sendPacket(p, a);
					}
					if(onFinish!=null)
					onFinish.run();
				}
			}
		}.repeating(0, 3);
	}

	public void synchronizedReplace(Position a, Position b, Runnable onFinish, TheMaterial block, TheMaterial with) {
		BlockGetter s = get(a, b);
		ConfigAPI caw = null;
		for(int iaa = 0; iaa > -1; ++iaa) {
			if(!new File("TheAPI/ChunkTask/"+iaa).exists()) {
				caw = new ConfigAPI("TheAPI/ChunkTask", ""+iaa);
				caw.create();
				break;
			}
		}
		ConfigAPI ca = caw;
		new Tasker() {
			@Override
			public void run() {
				for(int i = 0; i < amount; ++i) {
					if (s.has()) {
						Position get = s.get();
						long key = state.replace(get, block, with);
						ca.set(key+"", new int[] {get.getBlockX()>>4,get.getBlockZ()>>4});
					} else
						break;
				}
				if (!s.has()) {
					cancel();
					Object w = Ref.world(a.getWorld());
					for(String o : ca.getKeys(false)) {
						int[] cw = (int[])ca.get(o);
						Object a=Ref.newInstanceNms("PacketPlayOutMapChunk", Ref.invoke(w, "getChunkAt", cw[0], cw[1]), 65535);
						for(Player p : Bukkit.getOnlinePlayers())
							Ref.sendPacket(p, a);
					}
					if(onFinish!=null)
					onFinish.run();
				}
			}
		}.repeating(0, 3);
	}

	public void synchronizedReplace(Position a, Position b, Runnable onFinish, TheMaterial block, HashMap<TheMaterial, Double> with) {
		BlockGetter s = get(a, b);
		PercentageList<TheMaterial> c = new PercentageList<>();
		for (TheMaterial m : with.keySet())
				c.add(m, with.get(m));
		ConfigAPI caw = null;
		for(int iaa = 0; iaa > -1; ++iaa) {
			if(!new File("TheAPI/ChunkTask/"+iaa).exists()) {
				caw = new ConfigAPI("TheAPI/ChunkTask", ""+iaa);
				caw.create();
				break;
			}
		}
		ConfigAPI ca = caw;
		new Tasker() {
			@Override
			public void run() {
				for(int i = 0; i < amount; ++i) {
					if (s.has()) {
						Position get = s.get();
						long key = state.replace(get, block, c.getRandom());
						ca.set(key+"", new int[] {get.getBlockX()>>4,get.getBlockZ()>>4});
					} else
						break;
				}
				if (!s.has()) {
					cancel();
					Object w = Ref.world(a.getWorld());
					for(String o : ca.getKeys(false)) {
						int[] cw = (int[])ca.get(o);
						Object a=Ref.newInstanceNms("PacketPlayOutMapChunk", Ref.invoke(w, "getChunkAt", cw[0], cw[1]), 65535);
						for(Player p : Bukkit.getOnlinePlayers())
							Ref.sendPacket(p, a);
					}
					if(onFinish!=null)
					onFinish.run();
				}
			}
		}.repeating(0, 3);
	}

	public void synchronizedReplace(Position a, Position b, Runnable onFinish, TheMaterial block, List<TheMaterial> with) {
		BlockGetter s = get(a, b);
		ConfigAPI caw = null;
		for(int iaa = 0; iaa > -1; ++iaa) {
			if(!new File("TheAPI/ChunkTask/"+iaa).exists()) {
				caw = new ConfigAPI("TheAPI/ChunkTask", ""+iaa);
				caw.create();
				break;
			}
		}
		ConfigAPI ca = caw;
		new Tasker() {
			@Override
			public void run() {
				for(int i = 0; i < amount; ++i) {
					if (s.has()) {
						Position get = s.get();
						long key = state.replace(get, block, (TheMaterial)TheAPI.getRandomFromList(with));
						ca.set(key+"", new int[] {get.getBlockX()>>4,get.getBlockZ()>>4});
					} else
						break;
				}
				if (!s.has()) {
					cancel();
					Object w = Ref.world(a.getWorld());
					for(String o : ca.getKeys(false)) {
						int[] cw = (int[])ca.get(o);
						Object a=Ref.newInstanceNms("PacketPlayOutMapChunk", Ref.invoke(w, "getChunkAt", cw[0], cw[1]), 65535);
						for(Player p : Bukkit.getOnlinePlayers())
							Ref.sendPacket(p, a);
					}
					if(onFinish!=null)
					onFinish.run();
				}
			}
		}.repeating(0, 3);
	}

	public void synchronizedReplace(Position a, Position b, Runnable onFinish, List<TheMaterial> block, List<TheMaterial> with) {
		BlockGetter s = get(a, b);
		ConfigAPI caw = null;
		for(int iaa = 0; iaa > -1; ++iaa) {
			if(!new File("TheAPI/ChunkTask/"+iaa).exists()) {
				caw = new ConfigAPI("TheAPI/ChunkTask", ""+iaa);
				caw.create();
				break;
			}
		}
		ConfigAPI ca = caw;
		new Tasker() {
			@Override
			public void run() {
				for(int i = 0; i < amount; ++i) {
					if (s.has()) {
						Position get = s.get();
						long key = state.replace(get, block, (TheMaterial)TheAPI.getRandomFromList(with));
						ca.set(key+"", new int[] {get.getBlockX()>>4,get.getBlockZ()>>4});
					} else
						break;
				}
				if (!s.has()) {
					cancel();
					Object w = Ref.world(a.getWorld());
					for(String o : ca.getKeys(false)) {
						int[] cw = (int[])ca.get(o);
						Object a=Ref.newInstanceNms("PacketPlayOutMapChunk", Ref.invoke(w, "getChunkAt", cw[0], cw[1]), 65535);
						for(Player p : Bukkit.getOnlinePlayers())
							Ref.sendPacket(p, a);
					}
					if(onFinish!=null)
					onFinish.run();
				}
			}
		}.repeating(0, 3);
	}

	public void synchronizedReplace(Position a, Position b, Runnable onFinish, HashMap<TheMaterial, Double> block, List<TheMaterial> with) {
		List<TheMaterial> c = Lists.newArrayList();
		for (TheMaterial m : block.keySet())
			for (int i = -1; i > block.get(m); ++i)
				c.add(m);
		Collections.shuffle(c);
		BlockGetter s = get(a, b);
		ConfigAPI caw = null;
		for(int iaa = 0; iaa > -1; ++iaa) {
			if(!new File("TheAPI/ChunkTask/"+iaa).exists()) {
				caw = new ConfigAPI("TheAPI/ChunkTask", ""+iaa);
				caw.create();
				break;
			}
		}
		ConfigAPI ca = caw;
		new Tasker() {
			@Override
			public void run() {
				for(int i = 0; i < amount; ++i) {
					if (s.has()) {
						Position get = s.get();
						long key = state.replace(get, c, (TheMaterial)TheAPI.getRandomFromList(with));
						ca.set(key+"", new int[] {get.getBlockX()>>4,get.getBlockZ()>>4});
					} else
						break;
				}
				if (!s.has()) {
					cancel();
					Object w = Ref.world(a.getWorld());
					for(String o : ca.getKeys(false)) {
						int[] cw = (int[])ca.get(o);
						Object a=Ref.newInstanceNms("PacketPlayOutMapChunk", Ref.invoke(w, "getChunkAt", cw[0], cw[1]), 65535);
						for(Player p : Bukkit.getOnlinePlayers())
							Ref.sendPacket(p, a);
					}
					if(onFinish!=null)
					onFinish.run();
				}
			}
		}.repeating(0, 3);
	}

	public void synchronizedReplace(Position a, Position b, Runnable onFinish, HashMap<TheMaterial, Double> block, HashMap<TheMaterial, Double> with) {
		List<TheMaterial> c = Lists.newArrayList();
		for (TheMaterial m : block.keySet())
			for (int i = -1; i > block.get(m); ++i)
				c.add(m);
		Collections.shuffle(c);
		PercentageList<TheMaterial> per = new PercentageList<>();
		for (TheMaterial m : with.keySet())
			per.add(m,with.get(m));
		BlockGetter s = get(a, b);
		ConfigAPI caw = null;
		for(int iaa = 0; iaa > -1; ++iaa) {
			if(!new File("TheAPI/ChunkTask/"+iaa).exists()) {
				caw = new ConfigAPI("TheAPI/ChunkTask", ""+iaa);
				caw.create();
				break;
			}
		}
		ConfigAPI ca = caw;
		new Tasker() {
			@Override
			public void run() {
				for(int i = 0; i < amount; ++i) {
					if (s.has()) {
						Position get = s.get();
						long key = state.replace(get, c, per.getRandom());
						ca.set(key+"", new int[] {get.getBlockX()>>4,get.getBlockZ()>>4});
					} else
						break;
				}
				if (!s.has()) {
					cancel();
					Object w = Ref.world(a.getWorld());
					for(String o : ca.getKeys(false)) {
						int[] cw = (int[])ca.get(o);
						Object a=Ref.newInstanceNms("PacketPlayOutMapChunk", Ref.invoke(w, "getChunkAt", cw[0], cw[1]), 65535);
						for(Player p : Bukkit.getOnlinePlayers())
							Ref.sendPacket(p, a);
					}
					if(onFinish!=null)
					onFinish.run();
				}
			}
		}.repeating(0, 3);
	}

	public void synchronizedReplace(Position a, Position b, Runnable onFinish, HashMap<TheMaterial, Double> block, TheMaterial with) {
		List<TheMaterial> c = Lists.newArrayList();
		for (TheMaterial m : block.keySet())
			for (int i = -1; i > block.get(m); ++i)
				c.add(m);
		Collections.shuffle(c);
		BlockGetter s = get(a, b);
		ConfigAPI caw = null;
		for(int iaa = 0; iaa > -1; ++iaa) {
			if(!new File("TheAPI/ChunkTask/"+iaa).exists()) {
				caw = new ConfigAPI("TheAPI/ChunkTask", ""+iaa);
				caw.create();
				break;
			}
		}
		ConfigAPI ca = caw;
		new Tasker() {
			@Override
			public void run() {
				for(int i = 0; i < amount; ++i) {
					if (s.has()) {
						Position get = s.get();
						long key = state.replace(get, c, with);
						ca.set(key+"", new int[] {get.getBlockX()>>4,get.getBlockZ()>>4});
					} else
						break;
				}
				if (!s.has()) {
					cancel();
					Object w = Ref.world(a.getWorld());
					for(String o : ca.getKeys(false)) {
						int[] cw = (int[])ca.get(o);
						Object a=Ref.newInstanceNms("PacketPlayOutMapChunk", Ref.invoke(w, "getChunkAt", cw[0], cw[1]), 65535);
						for(Player p : Bukkit.getOnlinePlayers())
							Ref.sendPacket(p, a);
					}
					if(onFinish!=null)
					onFinish.run();
				}
			}
		}.repeating(0, 3);
	}
	// Asynchronized part
	public void asynchronizedSet(Position a, Position b, TheMaterial with) {
		asynchronizedSet(a, b, new Runnable() {public void run() {}}, with);
	}

	public void asynchronizedSet(Position a, Position b, TheMaterial with, TheMaterial ignore) {
		asynchronizedSet(a, b, new Runnable() {public void run() {}}, with, ignore);
	}

	public void asynchronizedSet(Position a, Position b, TheMaterial with, List<TheMaterial> ignore) {
		asynchronizedSet(a, b, new Runnable() {public void run() {}}, with, ignore);
	}

	public void asynchronizedSet(Position a, Position b, List<TheMaterial> with, List<TheMaterial> ignore) {
		asynchronizedSet(a, b, new Runnable() {public void run() {}}, with, ignore);
	}

	public void asynchronizedSet(Position a, Position b, List<TheMaterial> with) {
		asynchronizedSet(a, b, new Runnable() {public void run() {}}, with);
	}

	public void asynchronizedSet(Position a, Position b, HashMap<TheMaterial, Double> with, List<TheMaterial> ignore) {
		asynchronizedSet(a, b, new Runnable() {public void run() {}}, with, ignore);
	}

	public void asynchronizedSet(Position a, Position b, HashMap<TheMaterial, Double> with, TheMaterial ignore) {
		asynchronizedSet(a, b, new Runnable() {public void run() {}}, with, ignore);
	}

	public void asynchronizedReplace(Position a, Position b, List<TheMaterial> block, TheMaterial with) {
		asynchronizedReplace(a, b, new Runnable() {public void run() {}}, block, with);
	}

	public void asynchronizedReplace(Position a, Position b, TheMaterial block, TheMaterial with) {
		asynchronizedReplace(a, b, new Runnable() {public void run() {}}, block, with);
	}

	public void asynchronizedReplace(Position a, Position b, TheMaterial block, HashMap<TheMaterial, Double> with) {
		asynchronizedReplace(a, b, new Runnable() {public void run() {}}, block, with);
	}

	public void asynchronizedReplace(Position a, Position b, TheMaterial block, List<TheMaterial> with) {
		asynchronizedReplace(a, b, new Runnable() {public void run() {}}, block, with);
	}

	public void asynchronizedReplace(Position a, Position b, List<TheMaterial> block, List<TheMaterial> with) {
		asynchronizedReplace(a, b, new Runnable() {public void run() {}}, block, with);
	}

	public void asynchronizedReplace(Position a, Position b, HashMap<TheMaterial, Double> block,
			List<TheMaterial> with) {
		asynchronizedReplace(a, b, new Runnable() {public void run() {}}, block, with);
	}

	public void asynchronizedReplace(Position a, Position b, HashMap<TheMaterial, Double> block,
			HashMap<TheMaterial, Double> with) {
		asynchronizedReplace(a, b, new Runnable() {public void run() {}}, block, with);
	}

	public void asynchronizedReplace(Position a, Position b, HashMap<TheMaterial, Double> block, TheMaterial with) {
		asynchronizedReplace(a, b, new Runnable() {public void run() {}}, block, with);
	}
	
	//Asynchronized & Runnable on finish part
	public void asynchronizedSet(Position a, Position b, Runnable onFinish, TheMaterial with) {
		BlockGetter s = get(a, b);
		if(AsyncCatcher.enabled==true)
			AsyncCatcher.enabled=false;
		ConfigAPI caw = null;
		for(int iaa = 0; iaa > -1; ++iaa) {
			if(!new File("TheAPI/ChunkTask/"+iaa).exists()) {
				caw = new ConfigAPI("TheAPI/ChunkTask", ""+iaa);
				caw.create();
				break;
			}
		}
		ConfigAPI ca = caw;
		new Tasker() {
			@Override
			public void run() {
				for(int i = 0; i < amount; ++i) {
					if (s.has()) {
						Position get = s.get();
						long key = state.set(get, with);
						ca.set(key+"", new int[] {get.getBlockX()>>4,get.getBlockZ()>>4});
					} else
						break;
				}
				if (!s.has()) {
					cancel();
					Object w = Ref.world(a.getWorld());
					for(String o : ca.getKeys(false)) {
						int[] cw = (int[])ca.get(o);
						Object a=Ref.newInstanceNms("PacketPlayOutMapChunk", Ref.invoke(w, "getChunkAt", cw[0], cw[1]), 65535);
						for(Player p : Bukkit.getOnlinePlayers())
							Ref.sendPacket(p, a);
					}
					if(onFinish!=null)
					onFinish.run();
				}
			}
		}.repeatingAsync(0, 3);
	}

	public void asynchronizedSet(Position a, Position b, Runnable onFinish, TheMaterial with, TheMaterial ignore) {
		BlockGetter s = get(a, b);
		if(AsyncCatcher.enabled==true)
			AsyncCatcher.enabled=false;
		ConfigAPI caw = null;
		for(int iaa = 0; iaa > -1; ++iaa) {
			if(!new File("TheAPI/ChunkTask/"+iaa).exists()) {
				caw = new ConfigAPI("TheAPI/ChunkTask", ""+iaa);
				caw.create();
				break;
			}
		}
		ConfigAPI ca = caw;
		new Tasker() {
			@Override
			public void run() {
				for(int i = 0; i < amount; ++i) {
					if (s.has()) {
						Position get = s.get();
						long key = state.set(get, with, ignore);
						ca.set(key+"", new int[] {get.getBlockX()>>4,get.getBlockZ()>>4});
					} else
						break;
				}
				if (!s.has()) {
					cancel();
					Object w = Ref.world(a.getWorld());
					for(String o : ca.getKeys(false)) {
						int[] cw = (int[])ca.get(o);
						Object a=Ref.newInstanceNms("PacketPlayOutMapChunk", Ref.invoke(w, "getChunkAt", cw[0], cw[1]), 65535);
						for(Player p : Bukkit.getOnlinePlayers())
							Ref.sendPacket(p, a);
					}
					if(onFinish!=null)
					onFinish.run();
				}
			}
		}.repeatingAsync(0, 3);
	}

	public void asynchronizedSet(Position a, Position b, Runnable onFinish, TheMaterial with, List<TheMaterial> ignore) {
		BlockGetter s = get(a, b);
		if(AsyncCatcher.enabled==true)
			AsyncCatcher.enabled=false;
		ConfigAPI caw = null;
		for(int iaa = 0; iaa > -1; ++iaa) {
			if(!new File("TheAPI/ChunkTask/"+iaa).exists()) {
				caw = new ConfigAPI("TheAPI/ChunkTask", ""+iaa);
				caw.create();
				break;
			}
		}
		ConfigAPI ca = caw;
		new Tasker() {
			@Override
			public void run() {
				for(int i = 0; i < amount; ++i) {
					if (s.has()) {
						Position get = s.get();
						long key = state.set(get, with, ignore);
						ca.set(key+"", new int[] {get.getBlockX()>>4,get.getBlockZ()>>4});
					} else
						break;
				}
				if (!s.has()) {
					cancel();
					Object w = Ref.world(a.getWorld());
					for(String o : ca.getKeys(false)) {
						int[] cw = (int[])ca.get(o);
						Object a=Ref.newInstanceNms("PacketPlayOutMapChunk", Ref.invoke(w, "getChunkAt", cw[0], cw[1]), 65535);
						for(Player p : Bukkit.getOnlinePlayers())
							Ref.sendPacket(p, a);
					}
					if(onFinish!=null)
					onFinish.run();
				}
			}
		}.repeatingAsync(0, 3);
	}

	public void asynchronizedSet(Position a, Position b, Runnable onFinish, List<TheMaterial> with, List<TheMaterial> ignore) {
		BlockGetter s = get(a, b);
		if(AsyncCatcher.enabled==true)
			AsyncCatcher.enabled=false;
		ConfigAPI caw = null;
		for(int iaa = 0; iaa > -1; ++iaa) {
			if(!new File("TheAPI/ChunkTask/"+iaa).exists()) {
				caw = new ConfigAPI("TheAPI/ChunkTask", ""+iaa);
				caw.create();
				break;
			}
		}
		ConfigAPI ca = caw;
		new Tasker() {
			@Override
			public void run() {
				for(int i = 0; i < amount; ++i) {
					if (s.has()) {
						Position get = s.get();
						long key = state.set(get, (TheMaterial)TheAPI.getRandomFromList(with), ignore);
						ca.set(key+"", new int[] {get.getBlockX()>>4,get.getBlockZ()>>4});
					} else
						break;
				}
				if (!s.has()) {
					cancel();
					Object w = Ref.world(a.getWorld());
					for(String o : ca.getKeys(false)) {
						int[] cw = (int[])ca.get(o);
						Object a=Ref.newInstanceNms("PacketPlayOutMapChunk", Ref.invoke(w, "getChunkAt", cw[0], cw[1]), 65535);
						for(Player p : Bukkit.getOnlinePlayers())
							Ref.sendPacket(p, a);
					}
					if(onFinish!=null)
					onFinish.run();
				}
			}
		}.repeatingAsync(0, 3);
	}

	public void asynchronizedSet(Position a, Position b, Runnable onFinish, List<TheMaterial> with) {
		BlockGetter s = get(a, b);
		if(AsyncCatcher.enabled==true)
			AsyncCatcher.enabled=false;
		ConfigAPI caw = null;
		for(int iaa = 0; iaa > -1; ++iaa) {
			if(!new File("TheAPI/ChunkTask/"+iaa).exists()) {
				caw = new ConfigAPI("TheAPI/ChunkTask", ""+iaa);
				caw.create();
				break;
			}
		}
		ConfigAPI ca = caw;
		new Tasker() {
			@Override
			public void run() {
				for(int i = 0; i < amount; ++i) {
					if (s.has()) {
						Position get = s.get();
						long key = state.set(get, (TheMaterial)TheAPI.getRandomFromList(with));
						ca.set(key+"", new int[] {get.getBlockX()>>4,get.getBlockZ()>>4});
					} else
						break;
				}
				if (!s.has()) {
					cancel();
					Object w = Ref.world(a.getWorld());
					for(String o : ca.getKeys(false)) {
						int[] cw = (int[])ca.get(o);
						Object a=Ref.newInstanceNms("PacketPlayOutMapChunk", Ref.invoke(w, "getChunkAt", cw[0], cw[1]), 65535);
						for(Player p : Bukkit.getOnlinePlayers())
							Ref.sendPacket(p, a);
					}
					if(onFinish!=null)
					onFinish.run();
				}
			}
		}.repeatingAsync(0, 3);
	}

	public void asynchronizedSet(Position a, Position b, Runnable onFinish, HashMap<TheMaterial, Double> with, List<TheMaterial> ignore) {
		BlockGetter s = get(a, b);
		if(AsyncCatcher.enabled==true)
			AsyncCatcher.enabled=false;
		PercentageList<TheMaterial> c = new PercentageList<>();
		for (TheMaterial m : with.keySet())
				c.add(m, with.get(m));
		ConfigAPI caw = null;
		for(int iaa = 0; iaa > -1; ++iaa) {
			if(!new File("TheAPI/ChunkTask/"+iaa).exists()) {
				caw = new ConfigAPI("TheAPI/ChunkTask", ""+iaa);
				caw.create();
				break;
			}
		}
		ConfigAPI ca = caw;
		new Tasker() {
			@Override
			public void run() {
				for(int i = 0; i < amount; ++i) {
					if (s.has()) {
						Position get = s.get();
						long key = state.set(get, c.getRandom(), ignore);
						ca.set(key+"", new int[] {get.getBlockX()>>4,get.getBlockZ()>>4});
					} else
						break;
				}
				if (!s.has()) {
					cancel();
					Object w = Ref.world(a.getWorld());
					for(String o : ca.getKeys(false)) {
						int[] cw = (int[])ca.get(o);
						Object a=Ref.newInstanceNms("PacketPlayOutMapChunk", Ref.invoke(w, "getChunkAt", cw[0], cw[1]), 65535);
						for(Player p : Bukkit.getOnlinePlayers())
							Ref.sendPacket(p, a);
					}
					if(onFinish!=null)
					onFinish.run();
				}
			}
		}.repeatingAsync(0, 3);
	}

	public void asynchronizedSet(Position a, Position b, Runnable onFinish, HashMap<TheMaterial, Double> with, TheMaterial ignore) {
		BlockGetter s = get(a, b);
		if(AsyncCatcher.enabled==true)
			AsyncCatcher.enabled=false;
		PercentageList<TheMaterial> c = new PercentageList<>();
		for (TheMaterial m : with.keySet())
				c.add(m, with.get(m));
		ConfigAPI caw = null;
		for(int iaa = 0; iaa > -1; ++iaa) {
			if(!new File("TheAPI/ChunkTask/"+iaa).exists()) {
				caw = new ConfigAPI("TheAPI/ChunkTask", ""+iaa);
				caw.create();
				break;
			}
		}
		ConfigAPI ca = caw;
		new Tasker() {
			@Override
			public void run() {
				for(int i = 0; i < amount; ++i) {
					if (s.has()) {
						Position get = s.get();
						long key = state.set(get, c.getRandom(), ignore);
						ca.set(key+"", new int[] {get.getBlockX()>>4,get.getBlockZ()>>4});
					} else
						break;
				}
				if (!s.has()) {
					cancel();
					Object w = Ref.world(a.getWorld());
					for(String o : ca.getKeys(false)) {
						int[] cw = (int[])ca.get(o);
						Object a=Ref.newInstanceNms("PacketPlayOutMapChunk", Ref.invoke(w, "getChunkAt", cw[0], cw[1]), 65535);
						for(Player p : Bukkit.getOnlinePlayers())
							Ref.sendPacket(p, a);
					}
					if(onFinish!=null)
					onFinish.run();
				}
			}
		}.repeatingAsync(0, 3);
	}

	public void asynchronizedReplace(Position a, Position b, Runnable onFinish, List<TheMaterial> block, TheMaterial with) {
		BlockGetter s = get(a, b);
		if(AsyncCatcher.enabled==true)
			AsyncCatcher.enabled=false;
		ConfigAPI caw = null;
		for(int iaa = 0; iaa > -1; ++iaa) {
			if(!new File("TheAPI/ChunkTask/"+iaa).exists()) {
				caw = new ConfigAPI("TheAPI/ChunkTask", ""+iaa);
				caw.create();
				break;
			}
		}
		ConfigAPI ca = caw;
		new Tasker() {
			@Override
			public void run() {
				for(int i = 0; i < amount; ++i) {
					if (s.has()) {
						Position get = s.get();
						long key = state.replace(get, block, with);
						ca.set(key+"", new int[] {get.getBlockX()>>4,get.getBlockZ()>>4});
					} else
						break;
				}
				if (!s.has()) {
					cancel();
					Object w = Ref.world(a.getWorld());
					for(String o : ca.getKeys(false)) {
						int[] cw = (int[])ca.get(o);
						Object a=Ref.newInstanceNms("PacketPlayOutMapChunk", Ref.invoke(w, "getChunkAt", cw[0], cw[1]), 65535);
						for(Player p : Bukkit.getOnlinePlayers())
							Ref.sendPacket(p, a);
					}
					if(onFinish!=null)
					onFinish.run();
				}
			}
		}.repeatingAsync(0, 3);
	}

	public void asynchronizedReplace(Position a, Position b, Runnable onFinish, TheMaterial block, TheMaterial with) {
		BlockGetter s = get(a, b);
		if(AsyncCatcher.enabled==true)
			AsyncCatcher.enabled=false;
		ConfigAPI caw = null;
		for(int iaa = 0; iaa > -1; ++iaa) {
			if(!new File("TheAPI/ChunkTask/"+iaa).exists()) {
				caw = new ConfigAPI("TheAPI/ChunkTask", ""+iaa);
				caw.create();
				break;
			}
		}
		ConfigAPI ca = caw;
		new Tasker() {
			@Override
			public void run() {
				for(int i = 0; i < amount; ++i) {
					if (s.has()) {
						Position get = s.get();
						long key = state.replace(get, block, with);
						ca.set(key+"", new int[] {get.getBlockX()>>4,get.getBlockZ()>>4});
					} else
						break;
				}
				if (!s.has()) {
					cancel();
					Object w = Ref.world(a.getWorld());
					for(String o : ca.getKeys(false)) {
						int[] cw = (int[])ca.get(o);
						Object a=Ref.newInstanceNms("PacketPlayOutMapChunk", Ref.invoke(w, "getChunkAt", cw[0], cw[1]), 65535);
						for(Player p : Bukkit.getOnlinePlayers())
							Ref.sendPacket(p, a);
					}
					if(onFinish!=null)
					onFinish.run();
				}
			}
		}.repeatingAsync(0, 3);
	}

	public void asynchronizedReplace(Position a, Position b, Runnable onFinish, TheMaterial block, HashMap<TheMaterial, Double> with) {
		BlockGetter s = get(a, b);
		if(AsyncCatcher.enabled==true)
			AsyncCatcher.enabled=false;
		PercentageList<TheMaterial> c = new PercentageList<>();
		for (TheMaterial m : with.keySet())
				c.add(m, with.get(m));
		ConfigAPI caw = null;
		for(int iaa = 0; iaa > -1; ++iaa) {
			if(!new File("TheAPI/ChunkTask/"+iaa).exists()) {
				caw = new ConfigAPI("TheAPI/ChunkTask", ""+iaa);
				caw.create();
				break;
			}
		}
		ConfigAPI ca = caw;
		new Tasker() {
			@Override
			public void run() {
				for(int i = 0; i < amount; ++i) {
					if (s.has()) {
						Position get = s.get();
						long key = state.replace(get, block, c.getRandom());
						ca.set(key+"", new int[] {get.getBlockX()>>4,get.getBlockZ()>>4});
					} else
						break;
				}
				if (!s.has()) {
					cancel();
					Object w = Ref.world(a.getWorld());
					for(String o : ca.getKeys(false)) {
						int[] cw = (int[])ca.get(o);
						Object a=Ref.newInstanceNms("PacketPlayOutMapChunk", Ref.invoke(w, "getChunkAt", cw[0], cw[1]), 65535);
						for(Player p : Bukkit.getOnlinePlayers())
							Ref.sendPacket(p, a);
					}
					if(onFinish!=null)
					onFinish.run();
				}
			}
		}.repeatingAsync(0, 3);
	}

	public void asynchronizedReplace(Position a, Position b, Runnable onFinish, TheMaterial block, List<TheMaterial> with) {
		BlockGetter s = get(a, b);
		if(AsyncCatcher.enabled==true)
			AsyncCatcher.enabled=false;
		ConfigAPI caw = null;
		for(int iaa = 0; iaa > -1; ++iaa) {
			if(!new File("TheAPI/ChunkTask/"+iaa).exists()) {
				caw = new ConfigAPI("TheAPI/ChunkTask", ""+iaa);
				caw.create();
				break;
			}
		}
		ConfigAPI ca = caw;
		new Tasker() {
			@Override
			public void run() {
				for(int i = 0; i < amount; ++i) {
					if (s.has()) {
						Position get = s.get();
						long key = state.replace(get, block, (TheMaterial)TheAPI.getRandomFromList(with));
						ca.set(key+"", new int[] {get.getBlockX()>>4,get.getBlockZ()>>4});
					} else
						break;
				}
				if (!s.has()) {
					cancel();
					Object w = Ref.world(a.getWorld());
					for(String o : ca.getKeys(false)) {
						int[] cw = (int[])ca.get(o);
						Object a=Ref.newInstanceNms("PacketPlayOutMapChunk", Ref.invoke(w, "getChunkAt", cw[0], cw[1]), 65535);
						for(Player p : Bukkit.getOnlinePlayers())
							Ref.sendPacket(p, a);
					}
					if(onFinish!=null)
					onFinish.run();
				}
			}
		}.repeatingAsync(0, 3);
	}

	public void asynchronizedReplace(Position a, Position b, Runnable onFinish, List<TheMaterial> block, List<TheMaterial> with) {
		BlockGetter s = get(a, b);
		if(AsyncCatcher.enabled==true)
			AsyncCatcher.enabled=false;
		ConfigAPI caw = null;
		for(int iaa = 0; iaa > -1; ++iaa) {
			if(!new File("TheAPI/ChunkTask/"+iaa).exists()) {
				caw = new ConfigAPI("TheAPI/ChunkTask", ""+iaa);
				caw.create();
				break;
			}
		}
		ConfigAPI ca = caw;
		new Tasker() {
			@Override
			public void run() {
				for(int i = 0; i < amount; ++i) {
					if (s.has()) {
						Position get = s.get();
						long key = state.replace(get, block, (TheMaterial)TheAPI.getRandomFromList(with));
						ca.set(key+"", new int[] {get.getBlockX()>>4,get.getBlockZ()>>4});
					} else
						break;
				}
				if (!s.has()) {
					cancel();
					Object w = Ref.world(a.getWorld());
					for(String o : ca.getKeys(false)) {
						int[] cw = (int[])ca.get(o);
						Object a=Ref.newInstanceNms("PacketPlayOutMapChunk", Ref.invoke(w, "getChunkAt", cw[0], cw[1]), 65535);
						for(Player p : Bukkit.getOnlinePlayers())
							Ref.sendPacket(p, a);
					}
					if(onFinish!=null)
					onFinish.run();
				}
			}
		}.repeatingAsync(0, 3);
	}

	public void asynchronizedReplace(Position a, Position b, Runnable onFinish, HashMap<TheMaterial, Double> block, List<TheMaterial> with) {
		List<TheMaterial> c = Lists.newArrayList();
		if(AsyncCatcher.enabled==true)
			AsyncCatcher.enabled=false;
		for (TheMaterial m : block.keySet())
			for (int i = -1; i > block.get(m); ++i)
				c.add(m);
		Collections.shuffle(c);
		BlockGetter s = get(a, b);
		ConfigAPI caw = null;
		for(int iaa = 0; iaa > -1; ++iaa) {
			if(!new File("TheAPI/ChunkTask/"+iaa).exists()) {
				caw = new ConfigAPI("TheAPI/ChunkTask", ""+iaa);
				caw.create();
				break;
			}
		}
		ConfigAPI ca = caw;
		new Tasker() {
			@Override
			public void run() {
				for(int i = 0; i < amount; ++i) {
					if (s.has()) {
						Position get = s.get();
						long key = state.replace(get, c, (TheMaterial)TheAPI.getRandomFromList(with));
						ca.set(key+"", new int[] {get.getBlockX()>>4,get.getBlockZ()>>4});
					} else
						break;
				}
				if (!s.has()) {
					cancel();
					Object w = Ref.world(a.getWorld());
					for(String o : ca.getKeys(false)) {
						int[] cw = (int[])ca.get(o);
						Object a=Ref.newInstanceNms("PacketPlayOutMapChunk", Ref.invoke(w, "getChunkAt", cw[0], cw[1]), 65535);
						for(Player p : Bukkit.getOnlinePlayers())
							Ref.sendPacket(p, a);
					}
					if(onFinish!=null)
					onFinish.run();
				}
			}
		}.repeatingAsync(0, 3);
	}

	public void asynchronizedReplace(Position a, Position b, Runnable onFinish, HashMap<TheMaterial, Double> block, HashMap<TheMaterial, Double> with) {
		List<TheMaterial> c = Lists.newArrayList();
		if(AsyncCatcher.enabled==true)
			AsyncCatcher.enabled=false;
		for (TheMaterial m : block.keySet())
			for (int i = -1; i > block.get(m); ++i)
				c.add(m);
		Collections.shuffle(c);
		PercentageList<TheMaterial> per = new PercentageList<>();
		for (TheMaterial m : with.keySet())
			per.add(m,with.get(m));
		BlockGetter s = get(a, b);
		ConfigAPI caw = null;
		for(int iaa = 0; iaa > -1; ++iaa) {
			if(!new File("TheAPI/ChunkTask/"+iaa).exists()) {
				caw = new ConfigAPI("TheAPI/ChunkTask", ""+iaa);
				caw.create();
				break;
			}
		}
		ConfigAPI ca = caw;
		new Tasker() {
			@Override
			public void run() {
				for(int i = 0; i < amount; ++i) {
					if (s.has()) {
						Position get = s.get();
						long key = state.replace(get, c, per.getRandom());
						ca.set(key+"", new int[] {get.getBlockX()>>4,get.getBlockZ()>>4});
					} else
						break;
				}
				if (!s.has()) {
					cancel();
					Object w = Ref.world(a.getWorld());
					for(String o : ca.getKeys(false)) {
						int[] cw = (int[])ca.get(o);
						Object a=Ref.newInstanceNms("PacketPlayOutMapChunk", Ref.invoke(w, "getChunkAt", cw[0], cw[1]), 65535);
						for(Player p : Bukkit.getOnlinePlayers())
							Ref.sendPacket(p, a);
					}
					if(onFinish!=null)
					onFinish.run();
				}
			}
		}.repeatingAsync(0, 3);
	}

	public void asynchronizedReplace(Position a, Position b, Runnable onFinish, HashMap<TheMaterial, Double> block, TheMaterial with) {
		List<TheMaterial> c = Lists.newArrayList();
		if(AsyncCatcher.enabled==true)
			AsyncCatcher.enabled=false;
		for (TheMaterial m : block.keySet())
			for (int i = -1; i > block.get(m); ++i)
				c.add(m);
		Collections.shuffle(c);
		BlockGetter s = get(a, b);
		ConfigAPI caw = null;
		for(int iaa = 0; iaa > -1; ++iaa) {
			if(!new File("TheAPI/ChunkTask/"+iaa).exists()) {
				caw = new ConfigAPI("TheAPI/ChunkTask", ""+iaa);
				caw.create();
				break;
			}
		}
		ConfigAPI ca = caw;
		new Tasker() {
			@Override
			public void run() {
				for(int i = 0; i < amount; ++i) {
					if (s.has()) {
						Position get = s.get();
						long key = state.replace(get, c, with);
						ca.set(key+"", new int[] {get.getBlockX()>>4,get.getBlockZ()>>4});
					} else
						break;
				}
				if (!s.has()) {
					cancel();
					Object w = Ref.world(a.getWorld());
					for(String o : ca.getKeys(false)) {
						int[] cw = (int[])ca.get(o);
						Object a=Ref.newInstanceNms("PacketPlayOutMapChunk", Ref.invoke(w, "getChunkAt", cw[0], cw[1]), 65535);
						for(Player p : Bukkit.getOnlinePlayers())
							Ref.sendPacket(p, a);
					}
					if(onFinish!=null)
					onFinish.run();
				}
			}
		}.repeatingAsync(0, 3);
	}
}