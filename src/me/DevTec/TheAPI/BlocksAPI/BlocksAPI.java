package me.DevTec.TheAPI.BlocksAPI;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.spigotmc.AsyncCatcher;

import me.DevTec.TheAPI.TheAPI;
import me.DevTec.TheAPI.Scheduler.Tasker;
import me.DevTec.TheAPI.Utils.PercentageList;
import me.DevTec.TheAPI.Utils.Position;
import me.DevTec.TheAPI.Utils.StringUtils;
import me.DevTec.TheAPI.Utils.TheMaterial;
import me.DevTec.TheAPI.Utils.Reflections.Ref;
import me.DevTec.TheAPI.Utils.TheAPIUtils.Validator;

public class BlocksAPI {
	private static interface Blocking {
		public void set(Position pos);
	}

	public static int amount = 500; // 1000 blocks per 10 ticks, Can be changed: BlocksAPI.amount = <new value in int>

	private static void set(Shape form, Position where, int radius, Blocking task) {
		World w = where.getWorld();
		int Xx = where.getBlockX();
		int Yy = where.getBlockY();
		int Zz = where.getBlockZ();
		switch (form) {
		case Square:
			for (int x = Xx - radius; x <= Xx + radius; x++)
				for (int y = Yy - radius; y <= Yy + radius; y++)
					for (int z = Zz - radius; z <= Zz + radius; z++)
						task.set(new Position(w, x, y, z));
			break;
		case Sphere:
			for (int Y = -radius; Y < radius; Y++)
				for (int X = -radius; X < radius; X++)
					for (int Z = -radius; Z < radius; Z++)
						if (Math.sqrt((X * X) + (Y * Y) + (Z * Z)) <= radius)
							task.set(new Position(w, X + Xx, Y + Yy, Z + Zz));
		}
	}

	public static Block getLookingBlock(Player player, int range) {
		BlockIterator iter = new BlockIterator(player, range);
		Block lastBlock = iter.next();
		while (iter.hasNext()) {
			lastBlock = iter.next();
			if (lastBlock.getType() == Material.AIR)
				continue;
			break;
		}
		return lastBlock;
	}

	private static void set(Position from, Position to, Blocking task) {
		BlockGetter g = new BlockGetter(from, to);
		while (g.has())
			task.set(g.get());
	}

	public static enum Shape {
		Sphere, Square
	}

	public static Schemate getSchemate(String name) {
		return new Schemate(name);
	}

	public static String getLocationAsString(Location loc) {
		return StringUtils.getLocationAsString(loc);
	}

	public static Location getLocationFromString(String saved) {
		return StringUtils.getLocationFromString(saved);
	}

	public static List<Entity> getNearbyEntities(Location l, int radius) {
		return getNearbyEntities(new Position(l), radius);
	}

	public static List<Entity> getNearbyEntities(Position l, int radius) {
		if (radius > 256) {
			Validator.send("The radius cannot be greater than 256");
			return new ArrayList<>();
		}
		int chunkRadius = radius < 16 ? 1 : (radius - (radius % 16)) / 16;
		List<Entity> radiusEntities = new ArrayList<>();
		for (int chX = 0 - chunkRadius; chX <= chunkRadius; chX++)
			for (int chZ = 0 - chunkRadius; chZ <= chunkRadius; chZ++)
				if(new Location(l.getWorld(), l.getX() + (chX * 16), l.getY(), l.getZ() + (chZ * 16))
						.getChunk()!=null)
				for (Entity e : new Location(l.getWorld(), l.getX() + (chX * 16), l.getY(), l.getZ() + (chZ * 16))
						.getChunk().getEntities())
					if (l.distance(e.getLocation()) <= radius)
						radiusEntities.add(e);
		return radiusEntities;
	}

	public static List<Entity> getNearbyEntities(Entity ed, int radius) {
		return getNearbyEntities(new Position(ed.getLocation()), radius);
	}

	public static List<Entity> getNearbyEntities(World world, double x, double y, double z, int radius) {
		return getNearbyEntities(new Position(world, x, y, z), radius);
	}

	public static BlockSave getBlockSave(Position b) {
		return new BlockSave(b);
	}

	public static BlockGetter get(Location from, Location to) {
		return new BlockGetter(new Position(from), new Position(to));
	}

	public static BlockGetter get(Position from, Position to) {
		return new BlockGetter(from, to);
	}

	public static float count(Location from, Location to) {
		return count(new Position(from), new Position(to));
	}

	public static float count(Position from, Position to) {
		return new BigDecimal("" + (((from.getBlockX() < to.getBlockX() ? to.getBlockX() : from.getBlockX())
				- (from.getBlockX() > to.getBlockX() ? to.getBlockX() : from.getBlockX())) + 1)).multiply(
						new BigDecimal("" + (((from.getBlockZ() < to.getBlockZ() ? to.getBlockZ() : from.getBlockZ())
								- (from.getBlockZ() > to.getBlockZ() ? to.getBlockZ() : from.getBlockZ())) + 1)))
						.multiply(new BigDecimal(
								"" + (((from.getBlockY() < to.getBlockY() ? to.getBlockY() : from.getBlockY())
										- (from.getBlockY() > to.getBlockY() ? to.getBlockY() : from.getBlockY()))
										+ 1)))
						.floatValue();
	}

	public static List<Position> get(Position from, Position to, TheMaterial ignore) {
		return gt(from, to, Arrays.asList(ignore));
	}

	public static List<Position> get(Position from, Position to, List<TheMaterial> ignore) {
		return gt(from, to, ignore);
	}

	private static List<Position> gt(Position from, Position to, List<TheMaterial> ignore) {
		List<Position> blocks = new ArrayList<>();
		BlockGetter getter = get(from, to);
		while (getter.has()) {
			Position s = getter.get();
			if (ignore == null || !ignore.contains(s.getType()))
				blocks.add(s);
		}
		return blocks;
	}

	public static List<BlockSave> getBlockSaves(List<Position> a) {
		List<BlockSave> b = new ArrayList<>();
		for (Position s : a)
			b.add(getBlockSave(s));
		return b;
	}

	public static void set(Position loc, Material material) {
		set(loc, new TheMaterial(material));
	}

	public static void set(Block loc, Material material) {
		set(new Position(loc), new TheMaterial(material));
	}

	public static void set(Position loc, TheMaterial material) {
		if (!material.getType().isBlock())
			return;
		loc.setType(material);
	}

	public static void set(Block loc, TheMaterial material) {
		set(new Position(loc), material);
	}

	public static void set(Position loc, List<TheMaterial> material) {
		set(loc, TheAPI.getRandomFromList(material));
	}

	public static void set(Block loc, List<TheMaterial> material) {
		set(loc, TheAPI.getRandomFromList(material));
	}

	public static void set(Position loc, PercentageList<TheMaterial> material) {
		set(loc, material.getRandom());
	}

	public static void set(Block loc, PercentageList<TheMaterial> material) {
		set(new Position(loc), material);
	}

	public static void loadBlockSave(Position pos, BlockSave s) {
		s.load(pos, true);
	}

	public static void pasteBlockSave(Position pos, BlockSave s) {
		s.load(pos, true);
	}

	public static List<Position> get(Shape form, Position where, int radius) {
		return g(form, where, radius, null);
	}

	public static List<Position> get(Shape form, Position where, int radius, TheMaterial ignore) {
		return g(form, where, radius, Arrays.asList(ignore));
	}

	private static List<Position> g(Shape form, Position where, int radius, List<TheMaterial> ignore) {
		List<Position> blocks = new ArrayList<>();
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
						if (ignore == null || !ignore.contains(s.getType()))
							blocks.add(s);
					}
			break;
		case Sphere:
			for (int Y = -radius; Y < radius; Y++)
				for (int X = -radius; X < radius; X++)
					for (int Z = -radius; Z < radius; Z++)
						if (Math.sqrt((X * X) + (Y * Y) + (Z * Z)) <= radius) {
							Position s = new Position(w, X + Xx, Y + Yy, Z + Zz);
							if (ignore == null || !ignore.contains(s.getType()))
								blocks.add(s);
						}
			break;
		}
		return blocks;
	}

	public static List<Position> get(Shape form, Position where, int radius, List<TheMaterial> ignore) {
		return g(form, where, radius, ignore);
	}

	public static void replace(Position from, Position to, TheMaterial block, TheMaterial with) {
		set(from, to, new Blocking() {
			@Override
			public void set(Position pos) {
				if (pos.getType() == block)
					pos.setType(with);
			}
		});
	}

	public static void replace(Shape form, Position where, int radius, TheMaterial block, TheMaterial with) {
		set(form, where, radius, new Blocking() {
			@Override
			public void set(Position pos) {
				if (block == pos.getType())
					pos.setType(with);
			}
		});
	}

	public static void replace(Position from, Position to, TheMaterial block, List<TheMaterial> with) {
		set(from, to, new Blocking() {
			@Override
			public void set(Position pos) {
				if (block == pos.getType())
					pos.setType(TheAPI.getRandomFromList(with));
			}
		});
	}

	public static void replace(Position from, Position to, TheMaterial block, PercentageList<TheMaterial> with) {
		set(from, to, new Blocking() {
			@Override
			public void set(Position pos) {
				if (block == pos.getType())
					pos.setType(with.getRandom());
			}
		});
	}

	public static void replace(Shape form, Position where, int radius, TheMaterial block,
			PercentageList<TheMaterial> with) {
		set(form, where, radius, new Blocking() {
			@Override
			public void set(Position pos) {
				if (block == pos.getType())
					pos.setType(with.getRandom());
			}
		});
	}

	public static void replace(Shape form, Position where, int radius, PercentageList<TheMaterial> block,
			TheMaterial with) {
		set(form, where, radius, new Blocking() {
			@Override
			public void set(Position pos) {
				if (block.contains(pos.getType()))
					if (TheAPI.generateChance(block.getChance(block.getRandom())))
						pos.setType(with);
			}
		});
	}

	public static void replace(Position from, Position to, PercentageList<TheMaterial> block, TheMaterial with) {
		set(from, to, new Blocking() {
			@Override
			public void set(Position pos) {
				if (block.contains(pos.getType()))
					if (TheAPI.generateChance(block.getChance(block.getRandom())))
						pos.setType(with);
			}
		});
	}

	public static void replace(Shape form, Position where, int radius, PercentageList<TheMaterial> block,
			PercentageList<TheMaterial> with) {
		set(form, where, radius, new Blocking() {
			@Override
			public void set(Position pos) {
				if (block.contains(pos.getType())) {
					TheMaterial random = block.getRandom();
					if (TheAPI.generateChance(block.getChance(random)))
						pos.setType(with.getRandom());
				}
			}
		});
	}

	public static void replace(Position from, Position to, PercentageList<TheMaterial> block,
			PercentageList<TheMaterial> with) {
		set(from, to, new Blocking() {
			@Override
			public void set(Position pos) {
				if (block.contains(pos.getType())) {
					TheMaterial random = block.getRandom();
					if (TheAPI.generateChance(block.getChance(random)))
						pos.setType(with.getRandom());
				}
			}
		});
	}

	public static void replace(Shape form, Position where, int radius, List<TheMaterial> block, TheMaterial with) {
		set(form, where, radius, new Blocking() {
			@Override
			public void set(Position pos) {
				if (block.contains(pos.getType()))
					pos.setType(with);
			}
		});
	}

	public static void replace(Shape form, Position where, int radius, List<TheMaterial> block,
			List<TheMaterial> with) {
		set(form, where, radius, new Blocking() {
			@Override
			public void set(Position pos) {
				if (block.contains(pos.getType()))
					pos.setType(TheAPI.getRandomFromList(with));
			}
		});
	}

	public static void replace(Position from, Position to, List<TheMaterial> block, TheMaterial with) {
		set(from, to, new Blocking() {
			@Override
			public void set(Position pos) {
				if (block.contains(pos.getType()))
					pos.setType(with);
			}
		});
	}

	public static void replace(Position from, Position to, List<TheMaterial> block, List<TheMaterial> with) {
		set(from, to, new Blocking() {
			@Override
			public void set(Position pos) {
				if (block.contains(pos.getType()))
					pos.setType(TheAPI.getRandomFromList(with));
			}
		});
	}

	public static void set(Shape form, Position where, int radius, TheMaterial block) {
		set(form, where, radius, new Blocking() {
			@Override
			public void set(Position pos) {
				pos.setType(block);
			}
		});
	}

	public static void set(Shape form, Position where, int radius, TheMaterial block, List<TheMaterial> ignore) {
		set(form, where, radius, new Blocking() {
			@Override
			public void set(Position pos) {
				if (!ignore.contains(pos.getType()))
					pos.setType(block);
			}
		});
	}

	public static void set(Shape form, Position where, int radius, TheMaterial block, TheMaterial ignore) {
		set(form, where, radius, new Blocking() {
			@Override
			public void set(Position pos) {
				if (ignore != pos.getType())
					pos.setType(block);
			}
		});
	}

	public static void set(Shape form, Position where, int radius, List<TheMaterial> block) {
		set(form, where, radius, new Blocking() {
			@Override
			public void set(Position pos) {
				pos.setType(TheAPI.getRandomFromList(block));
			}
		});
	}

	public static void set(Shape form, Position where, int radius, List<TheMaterial> block, List<TheMaterial> ignore) {
		set(form, where, radius, new Blocking() {
			@Override
			public void set(Position pos) {
				if (!ignore.contains(pos.getType()))
					pos.setType(TheAPI.getRandomFromList(block));
			}
		});
	}

	public static void set(Shape form, Position where, int radius, List<TheMaterial> block, TheMaterial ignore) {
		set(form, where, radius, new Blocking() {
			@Override
			public void set(Position pos) {
				if (ignore != pos.getType())
					pos.setType(TheAPI.getRandomFromList(block));
			}
		});
	}

	public static void set(Shape form, Position where, int radius, PercentageList<TheMaterial> block) {
		set(form, where, radius, new Blocking() {
			@Override
			public void set(Position pos) {
				pos.setType(block.getRandom());
			}
		});
	}

	public static void set(Shape form, Position where, int radius, PercentageList<TheMaterial> block,
			List<TheMaterial> ignore) {
		set(form, where, radius, new Blocking() {
			@Override
			public void set(Position pos) {
				if (!ignore.contains(pos.getType()))
					pos.setType(block.getRandom());
			}
		});
	}

	public static void set(Shape form, Position where, int radius, PercentageList<TheMaterial> block,
			TheMaterial ignore) {
		set(form, where, radius, new Blocking() {
			@Override
			public void set(Position pos) {
				if (ignore != pos.getType())
					pos.setType(block.getRandom());
			}
		});
	}

	public static void set(Position from, Position to, TheMaterial block) {
		set(from, to, new Blocking() {
			@Override
			public void set(Position pos) {
				pos.setType(block);
			}
		});
	}

	public static void set(Position from, Position to, TheMaterial block, List<TheMaterial> ignore) {
		set(from, to, new Blocking() {
			@Override
			public void set(Position pos) {
				if (!ignore.contains(pos.getType()))
					pos.setType(block);
			}
		});
	}

	public static void set(Position from, Position to, TheMaterial block, TheMaterial ignore) {
		set(from, to, new Blocking() {
			@Override
			public void set(Position pos) {
				if (ignore != pos.getType())
					pos.setType(block);
			}
		});
	}

	public static void set(Position from, Position to, List<TheMaterial> block) {
		set(from, to, new Blocking() {
			@Override
			public void set(Position pos) {
				pos.setType(TheAPI.getRandomFromList(block));
			}
		});
	}

	public static void set(Position from, Position to, List<TheMaterial> block, List<TheMaterial> ignore) {
		set(from, to, new Blocking() {
			@Override
			public void set(Position pos) {
				if (!ignore.contains(pos.getType()))
					pos.setType(TheAPI.getRandomFromList(block));
			}
		});
	}

	public static void set(Position from, Position to, List<TheMaterial> block, TheMaterial ignore) {
		set(from, to, new Blocking() {
			@Override
			public void set(Position pos) {
				if (ignore != pos.getType())
					pos.setType(TheAPI.getRandomFromList(block));
			}
		});
	}

	public static void set(Position from, Position to, PercentageList<TheMaterial> block) {
		set(from, to, new Blocking() {
			@Override
			public void set(Position pos) {
				pos.setType(block.getRandom());
			}
		});
	}

	public static void set(Position from, Position to, PercentageList<TheMaterial> block, List<TheMaterial> ignore) {
		set(from, to, new Blocking() {
			@Override
			public void set(Position pos) {
				if (!ignore.contains(pos.getType()))
					pos.setType(block.getRandom());
			}
		});
	}

	public static void set(Position from, Position to, PercentageList<TheMaterial> block, TheMaterial ignore) {
		set(from, to, new Blocking() {
			@Override
			public void set(Position pos) {
				if (ignore != pos.getType())
					pos.setType(block.getRandom());
			}
		});
	}

	public static boolean isInside(Entity entity, Position a, Position b) {
		return isInside(new Position(entity.getLocation()), a, b);
	}

	public static boolean isInside(Position loc, Position a, Position b) {
		int xMin = Math.min(a.getBlockX(), b.getBlockX());
		int yMin = Math.min(a.getBlockY(), b.getBlockY());
		int zMin = Math.min(a.getBlockZ(), b.getBlockZ());
		int xMax = Math.max(a.getBlockX(), b.getBlockX());
		int yMax = Math.max(a.getBlockY(), b.getBlockY());
		int zMax = Math.max(a.getBlockZ(), b.getBlockZ());
		return loc.getWorld() == a.getWorld() && loc.getBlockX() >= xMin && loc.getBlockX() <= xMax
				&& loc.getBlockY() >= yMin && loc.getBlockY() <= yMax && loc.getBlockZ() >= zMin
				&& loc.getBlockZ() <= zMax;
	}

	public static boolean isInside(Entity entity, Location a, Location b) {
		return isInside(entity.getLocation(), a, b);
	}

	public static boolean isInside(Location loc, Location a, Location b) {
		return isInside(new Position(loc), new Position(a), new Position(b));
	}

	// Asynchronized part
	public static void asynchronizedSet(Position a, Position b, TheMaterial with) {
		asynchronizedSet(a, b, new Runnable() {
			public void run() {
			}
		}, with);
	}

	public static void asynchronizedSet(Position a, Position b, TheMaterial with, TheMaterial ignore) {
		asynchronizedSet(a, b, new Runnable() {
			public void run() {
			}
		}, with, ignore);
	}

	public static void asynchronizedSet(Position a, Position b, TheMaterial with, List<TheMaterial> ignore) {
		asynchronizedSet(a, b, new Runnable() {
			public void run() {
			}
		}, with, ignore);
	}

	public static void asynchronizedSet(Position a, Position b, List<TheMaterial> with, List<TheMaterial> ignore) {
		asynchronizedSet(a, b, new Runnable() {
			public void run() {
			}
		}, with, ignore);
	}

	public static void asynchronizedSet(Position a, Position b, List<TheMaterial> with) {
		asynchronizedSet(a, b, new Runnable() {
			public void run() {
			}
		}, with);
	}

	public static void asynchronizedSet(Position a, Position b, PercentageList<TheMaterial> with) {
		asynchronizedSet(a, b, new Runnable() {
			public void run() {
			}
		}, with, Arrays.asList());
	}

	public static void asynchronizedSet(Position a, Position b, PercentageList<TheMaterial> with,
			List<TheMaterial> ignore) {
		asynchronizedSet(a, b, new Runnable() {
			public void run() {
			}
		}, with, ignore);
	}

	public static void asynchronizedSet(Position a, Position b, PercentageList<TheMaterial> with, TheMaterial ignore) {
		asynchronizedSet(a, b, new Runnable() {
			public void run() {
			}
		}, with, ignore);
	}

	public static void asynchronizedReplace(Position a, Position b, List<TheMaterial> block, TheMaterial with) {
		asynchronizedReplace(a, b, new Runnable() {
			public void run() {
			}
		}, block, with);
	}

	public static void asynchronizedReplace(Position a, Position b, TheMaterial block, TheMaterial with) {
		asynchronizedReplace(a, b, new Runnable() {
			public void run() {
			}
		}, block, with);
	}

	public static void asynchronizedReplace(Position a, Position b, TheMaterial block,
			PercentageList<TheMaterial> with) {
		asynchronizedReplace(a, b, new Runnable() {
			public void run() {
			}
		}, block, with);
	}

	public static void asynchronizedReplace(Position a, Position b, TheMaterial block, List<TheMaterial> with) {
		asynchronizedReplace(a, b, new Runnable() {
			public void run() {
			}
		}, block, with);
	}

	public static void asynchronizedReplace(Position a, Position b, List<TheMaterial> block, List<TheMaterial> with) {
		asynchronizedReplace(a, b, new Runnable() {
			public void run() {
			}
		}, block, with);
	}

	public static void asynchronizedReplace(Position a, Position b, PercentageList<TheMaterial> block,
			List<TheMaterial> with) {
		asynchronizedReplace(a, b, new Runnable() {
			public void run() {
			}
		}, block, with);
	}

	public static void asynchronizedReplace(Position a, Position b, PercentageList<TheMaterial> block,
			PercentageList<TheMaterial> with) {
		asynchronizedReplace(a, b, new Runnable() {
			public void run() {
			}
		}, block, with);
	}

	public static void asynchronizedReplace(Position a, Position b, PercentageList<TheMaterial> block,
			TheMaterial with) {
		asynchronizedReplace(a, b, new Runnable() {
			public void run() {
			}
		}, block, with);
	}

	// Asynchronized & Runnable on finish part
	public static void asynchronizedSet(Position a, Position b, Runnable onFinish, TheMaterial with) {
		asynchronizedSet(a, b, onFinish, Arrays.asList(with), Arrays.asList());
	}

	public static void asynchronizedSet(Position a, Position b, Runnable onFinish, TheMaterial with,
			TheMaterial ignore) {
		asynchronizedSet(a, b, onFinish, Arrays.asList(with), Arrays.asList(ignore));
	}

	public static void asynchronizedSet(Position a, Position b, Runnable onFinish, TheMaterial with,
			List<TheMaterial> ignore) {
		asynchronizedSet(a, b, onFinish, Arrays.asList(with), ignore);
	}

	private static boolean ww = StringUtils.getInt(TheAPI.getServerVersion().split("_")[1]) >= 14,
			palet = StringUtils.getInt(TheAPI.getServerVersion().split("_")[1]) >= 9;

	public static void asynchronizedSet(Position a, Position b, Runnable onFinish, List<TheMaterial> with,
			List<TheMaterial> ignore) {
		try {
			if (AsyncCatcher.enabled)
				AsyncCatcher.enabled = false;
		} catch (Exception | NoSuchFieldError | NoSuchMethodError notEx) {
		}
		new Tasker() {
			public void run() {
				BlockGetter s = get(a, b);
				HashMap<Long, Object> chunks = new HashMap<>();
				while (s.has()) {
					Position pos = s.get();
					if (!ignore.contains(pos.getType())) {
						Object c = pos.getNMSChunk();
						if (!chunks.containsKey(pos.getChunkKey()))
							chunks.put(pos.getChunkKey(), c);
						Object sc = ((Object[]) Ref.invoke(c, get))[pos.getBlockY() >> 4];
						if (sc == null) {
							if (ww)
								sc = Ref.newInstance(aw, pos.getBlockY() >> 4 << 4);
							else
								sc = Ref.newInstance(aw, pos.getBlockY() >> 4 << 4, true);
							((Object[]) Ref.invoke(c, get))[pos.getBlockY() >> 4] = sc;
						}
						Object cr = TheAPI.getRandomFromList(with).getIBlockData();
						if (palet)
							Ref.invoke(Ref.invoke(sc, blocks), BlocksAPI.a, pos.getBlockX() & 0xF,
									pos.getBlockY() & 0xF, pos.getBlockZ() & 0xF, cr);
						else
							Ref.invoke(sc, type, pos.getBlockX() & 0xF, pos.getBlockY() & 0xF, pos.getBlockZ() & 0xF,
									cr);
						Object packet = Ref.newInstance(Ref.constructor(Ref.nms("PacketPlayOutBlockChange"),
								Ref.nms("BlockPosition"), Ref.nms("IBlockData")), pos.getBlockPosition(), cr);
						if (packet == null)
							packet = Ref.newInstance(
									Ref.constructor(Ref.nms("PacketPlayOutBlockChange"), Ref.nms("World"),
											Ref.nms("BlockPosition")),
									Ref.world(pos.getWorld()), pos.getBlockPosition());
						if (packet == null)
							packet = Ref.newInstance(
									Ref.constructor(Ref.nms("PacketPlayOutBlockChange"), int.class, int.class,
											int.class, Ref.nms("World")),
									pos.getBlockX(), pos.getBlockY(), pos.getBlockZ(), Ref.world(pos.getWorld()));
						for (Player p : TheAPI.getOnlinePlayers())
							Ref.sendPacket(p, packet);
					}
				}
				if (onFinish != null)
					onFinish.run();
			}
		}.runTask();
	}

	public static void asynchronizedSet(Position a, Position b, Runnable onFinish, List<TheMaterial> with) {
		asynchronizedSet(a, b, onFinish, with, Arrays.asList());
	}

	public static void asynchronizedSet(Position a, Position b, Runnable onFinish, PercentageList<TheMaterial> with) {
		asynchronizedSet(a, b, onFinish, with, Arrays.asList());
	}

	public static void asynchronizedSet(Position a, Position b, Runnable onFinish, PercentageList<TheMaterial> with,
			List<TheMaterial> ignore) {
		try {
			if (AsyncCatcher.enabled)
				AsyncCatcher.enabled = false;
		} catch (Exception | NoSuchFieldError | NoSuchMethodError notEx) {
		}
		new Tasker() {
			public void run() {
				BlockGetter s = get(a, b);
				HashMap<Long, Object> chunks = new HashMap<>();
				while (s.has()) {
					Position pos = s.get();
					if (!ignore.contains(pos.getType())) {
						Object c = pos.getNMSChunk();
						if (!chunks.containsKey(pos.getChunkKey()))
							chunks.put(pos.getChunkKey(), c);
						Object sc = ((Object[]) Ref.invoke(c, get))[pos.getBlockY() >> 4];
						if (sc == null) {
							if (ww)
								sc = Ref.newInstance(aw, pos.getBlockY() >> 4 << 4);
							else
								sc = Ref.newInstance(aw, pos.getBlockY() >> 4 << 4, true);
							((Object[]) Ref.invoke(c, get))[pos.getBlockY() >> 4] = sc;
						}
						Object cr = with.getRandom().getIBlockData();
						if (palet)
							Ref.invoke(Ref.invoke(sc, blocks), BlocksAPI.a, pos.getBlockX() & 0xF,
									pos.getBlockY() & 0xF, pos.getBlockZ() & 0xF, cr);
						else
							Ref.invoke(sc, type, pos.getBlockX() & 0xF, pos.getBlockY() & 0xF, pos.getBlockZ() & 0xF,
									cr);
						Object packet = Ref.newInstance(Ref.constructor(Ref.nms("PacketPlayOutBlockChange"),
								Ref.nms("BlockPosition"), Ref.nms("IBlockData")), pos.getBlockPosition(), cr);
						if (packet == null)
							packet = Ref.newInstance(
									Ref.constructor(Ref.nms("PacketPlayOutBlockChange"), Ref.nms("World"),
											Ref.nms("BlockPosition")),
									Ref.world(pos.getWorld()), pos.getBlockPosition());
						if (packet == null)
							packet = Ref.newInstance(
									Ref.constructor(Ref.nms("PacketPlayOutBlockChange"), int.class, int.class,
											int.class, Ref.nms("World")),
									pos.getBlockX(), pos.getBlockY(), pos.getBlockZ(), Ref.world(pos.getWorld()));
						for (Player p : TheAPI.getOnlinePlayers())
							Ref.sendPacket(p, packet);
					}
				}
				if (onFinish != null)
					onFinish.run();
			}
		}.runTask();
	}

	public static void asynchronizedSet(Position a, Position b, Runnable onFinish, PercentageList<TheMaterial> with,
			TheMaterial ignore) {
		asynchronizedSet(a, b, onFinish, with, Arrays.asList(ignore));
	}

	public static void asynchronizedReplace(Position a, Position b, Runnable onFinish, List<TheMaterial> block,
			TheMaterial with) {
		asynchronizedReplace(a, b, onFinish, block, Arrays.asList(with));
	}

	public static void asynchronizedReplace(Position a, Position b, Runnable onFinish, TheMaterial block,
			TheMaterial with) {
		asynchronizedReplace(a, b, onFinish, Arrays.asList(block), Arrays.asList(with));
	}

	public static void asynchronizedReplace(Position a, Position b, Runnable onFinish, TheMaterial block,
			PercentageList<TheMaterial> with) {
		asynchronizedReplace(a, b, onFinish, Arrays.asList(block), with);
	}

	public static void asynchronizedReplace(Position a, Position b, Runnable onFinish, List<TheMaterial> block,
			PercentageList<TheMaterial> with) {
		try {
			if (AsyncCatcher.enabled)
				AsyncCatcher.enabled = false;
		} catch (Exception | NoSuchFieldError | NoSuchMethodError notEx) {
		}
		new Tasker() {
			public void run() {
				BlockGetter s = get(a, b);
				HashMap<Long, Object> chunks = new HashMap<>();
				while (s.has()) {
					Position pos = s.get();
					if (block.contains(pos.getType())) {
						Object c = pos.getNMSChunk();
						if (!chunks.containsKey(pos.getChunkKey()))
							chunks.put(pos.getChunkKey(), c);
						Object sc = ((Object[]) Ref.invoke(c, get))[pos.getBlockY() >> 4];
						if (sc == null) {
							if (ww)
								sc = Ref.newInstance(aw, pos.getBlockY() >> 4 << 4);
							else
								sc = Ref.newInstance(aw, pos.getBlockY() >> 4 << 4, true);
							((Object[]) Ref.invoke(c, get))[pos.getBlockY() >> 4] = sc;
						}
						Object cr = with.getRandom().getIBlockData();
						if (palet)
							Ref.invoke(Ref.invoke(sc, blocks), BlocksAPI.a, pos.getBlockX() & 0xF,
									pos.getBlockY() & 0xF, pos.getBlockZ() & 0xF, cr);
						else
							Ref.invoke(sc, type, pos.getBlockX() & 0xF, pos.getBlockY() & 0xF, pos.getBlockZ() & 0xF,
									cr);
						Object packet = Ref.newInstance(Ref.constructor(Ref.nms("PacketPlayOutBlockChange"),
								Ref.nms("BlockPosition"), Ref.nms("IBlockData")), pos.getBlockPosition(), cr);
						if (packet == null)
							packet = Ref.newInstance(
									Ref.constructor(Ref.nms("PacketPlayOutBlockChange"), Ref.nms("World"),
											Ref.nms("BlockPosition")),
									Ref.world(pos.getWorld()), pos.getBlockPosition());
						if (packet == null)
							packet = Ref.newInstance(
									Ref.constructor(Ref.nms("PacketPlayOutBlockChange"), int.class, int.class,
											int.class, Ref.nms("World")),
									pos.getBlockX(), pos.getBlockY(), pos.getBlockZ(), Ref.world(pos.getWorld()));
						for (Player p : TheAPI.getOnlinePlayers())
							Ref.sendPacket(p, packet);
					}
				}
				if (onFinish != null)
					onFinish.run();
			}
		}.runTask();
	}

	public static void asynchronizedReplace(Position a, Position b, Runnable onFinish, TheMaterial block,
			List<TheMaterial> with) {
		asynchronizedReplace(a, b, onFinish, Arrays.asList(block), with);
	}

	public static void asynchronizedReplace(Position a, Position b, Runnable onFinish, List<TheMaterial> block,
			List<TheMaterial> with) {
		try {
			if (AsyncCatcher.enabled)
				AsyncCatcher.enabled = false;
		} catch (Exception | NoSuchFieldError | NoSuchMethodError notEx) {
		}
		new Tasker() {
			public void run() {
				BlockGetter s = get(a, b);
				HashMap<Long, Object> chunks = new HashMap<>();
				while (s.has()) {
					Position pos = s.get();
					if (block.contains(pos.getType())) {
						Object c = pos.getNMSChunk();
						if (!chunks.containsKey(pos.getChunkKey()))
							chunks.put(pos.getChunkKey(), c);
						Object sc = ((Object[]) Ref.invoke(c, get))[pos.getBlockY() >> 4];
						if (sc == null) {
							if (ww)
								sc = Ref.newInstance(aw, pos.getBlockY() >> 4 << 4);
							else
								sc = Ref.newInstance(aw, pos.getBlockY() >> 4 << 4, true);
							((Object[]) Ref.invoke(c, get))[pos.getBlockY() >> 4] = sc;
						}
						Object cr = TheAPI.getRandomFromList(with).getIBlockData();
						if (palet)
							Ref.invoke(Ref.invoke(sc, blocks), BlocksAPI.a, pos.getBlockX() & 0xF,
									pos.getBlockY() & 0xF, pos.getBlockZ() & 0xF, cr);
						else
							Ref.invoke(sc, type, pos.getBlockX() & 0xF, pos.getBlockY() & 0xF, pos.getBlockZ() & 0xF,
									cr);
						Object packet = Ref.newInstance(Ref.constructor(Ref.nms("PacketPlayOutBlockChange"),
								Ref.nms("BlockPosition"), Ref.nms("IBlockData")), pos.getBlockPosition(), cr);
						if (packet == null)
							packet = Ref.newInstance(
									Ref.constructor(Ref.nms("PacketPlayOutBlockChange"), Ref.nms("World"),
											Ref.nms("BlockPosition")),
									Ref.world(pos.getWorld()), pos.getBlockPosition());
						if (packet == null)
							packet = Ref.newInstance(
									Ref.constructor(Ref.nms("PacketPlayOutBlockChange"), int.class, int.class,
											int.class, Ref.nms("World")),
									pos.getBlockX(), pos.getBlockY(), pos.getBlockZ(), Ref.world(pos.getWorld()));
						for (Player p : TheAPI.getOnlinePlayers())
							Ref.sendPacket(p, packet);
					}
				}
				if (onFinish != null)
					onFinish.run();
			}
		}.runTask();
	}

	private static Constructor<?> aw = Ref.constructor(Ref.nms("ChunkSection"), int.class);
	private static Method a, get = Ref.method(Ref.nms("Chunk"), "getSections"),
			blocks = Ref.method(Ref.nms("ChunkSection"), "getBlocks"), type = Ref.method(Ref.nms("ChunkSection"),
					"setType", int.class, int.class, int.class, Ref.nms("IBlockData"));
	static {
		a = Ref.method(Ref.nms("DataPaletteBlock"), "b", int.class, int.class, int.class, Object.class);
		if (a == null)
			a = Ref.method(Ref.nms("DataPaletteBlock"), "setBlock", int.class, int.class, int.class,
					Ref.nms("IBlockData"));
		if (a == null)
			a = Ref.method(Ref.nms("DataPaletteBlock"), "setBlock", int.class, int.class, int.class, Object.class);
		if (aw == null)
			aw = Ref.constructor(Ref.nms("ChunkSection"), int.class, boolean.class);
	}

	public static void asynchronizedReplace(Position a, Position b, Runnable onFinish,
			PercentageList<TheMaterial> block, List<TheMaterial> with) {
		try {
			if (AsyncCatcher.enabled)
				AsyncCatcher.enabled = false;
		} catch (Exception | NoSuchFieldError | NoSuchMethodError notEx) {
		}
		new Tasker() {
			public void run() {
				BlockGetter s = get(a, b);
				HashMap<Long, Object> chunks = new HashMap<>();
				while (s.has()) {
					Position pos = s.get();
					if (block.contains(pos.getType())) {
						Object c = pos.getNMSChunk();
						if (!chunks.containsKey(pos.getChunkKey()))
							chunks.put(pos.getChunkKey(), c);
						Object sc = ((Object[]) Ref.invoke(c, get))[pos.getBlockY() >> 4];
						if (sc == null) {
							if (ww)
								sc = Ref.newInstance(aw, pos.getBlockY() >> 4 << 4);
							else
								sc = Ref.newInstance(aw, pos.getBlockY() >> 4 << 4, true);
							((Object[]) Ref.invoke(c, get))[pos.getBlockY() >> 4] = sc;
						}
						Object cr = TheAPI.getRandomFromList(with).getIBlockData();
						if (palet)
							Ref.invoke(Ref.invoke(sc, blocks), BlocksAPI.a, pos.getBlockX() & 0xF,
									pos.getBlockY() & 0xF, pos.getBlockZ() & 0xF, cr);
						else
							Ref.invoke(sc, type, pos.getBlockX() & 0xF, pos.getBlockY() & 0xF, pos.getBlockZ() & 0xF,
									cr);
						Object packet = Ref.newInstance(Ref.constructor(Ref.nms("PacketPlayOutBlockChange"),
								Ref.nms("BlockPosition"), Ref.nms("IBlockData")), pos.getBlockPosition(), cr);
						if (packet == null)
							packet = Ref.newInstance(
									Ref.constructor(Ref.nms("PacketPlayOutBlockChange"), Ref.nms("World"),
											Ref.nms("BlockPosition")),
									Ref.world(pos.getWorld()), pos.getBlockPosition());
						if (packet == null)
							packet = Ref.newInstance(
									Ref.constructor(Ref.nms("PacketPlayOutBlockChange"), int.class, int.class,
											int.class, Ref.nms("World")),
									pos.getBlockX(), pos.getBlockY(), pos.getBlockZ(), Ref.world(pos.getWorld()));
						for (Player p : TheAPI.getOnlinePlayers())
							Ref.sendPacket(p, packet);
					}
				}
				if (onFinish != null)
					onFinish.run();
			}
		}.runTask();
	}

	public static void asynchronizedReplace(Position a, Position b, Runnable onFinish,
			PercentageList<TheMaterial> block, PercentageList<TheMaterial> with) {
		try {
			if (AsyncCatcher.enabled)
				AsyncCatcher.enabled = false;
		} catch (Exception | NoSuchFieldError | NoSuchMethodError notEx) {
		}
		new Tasker() {
			public void run() {
				BlockGetter s = get(a, b);
				HashMap<Long, Object> chunks = new HashMap<>();
				while (s.has()) {
					Position pos = s.get();
					if (block.contains(pos.getType())) {
						Object c = pos.getNMSChunk();
						if (!chunks.containsKey(pos.getChunkKey()))
							chunks.put(pos.getChunkKey(), c);
						Object sc = ((Object[]) Ref.invoke(c, get))[pos.getBlockY() >> 4];
						if (sc == null) {
							if (ww)
								sc = Ref.newInstance(aw, pos.getBlockY() >> 4 << 4);
							else
								sc = Ref.newInstance(aw, pos.getBlockY() >> 4 << 4, true);
							((Object[]) Ref.invoke(c, get))[pos.getBlockY() >> 4] = sc;
						}
						Object cr = block.getRandom().getIBlockData();
						if (palet)
							Ref.invoke(Ref.invoke(sc, blocks), BlocksAPI.a, pos.getBlockX() & 0xF,
									pos.getBlockY() & 0xF, pos.getBlockZ() & 0xF, cr);
						else
							Ref.invoke(sc, type, pos.getBlockX() & 0xF, pos.getBlockY() & 0xF, pos.getBlockZ() & 0xF,
									cr);
						Object packet = Ref.newInstance(Ref.constructor(Ref.nms("PacketPlayOutBlockChange"),
								Ref.nms("BlockPosition"), Ref.nms("IBlockData")), pos.getBlockPosition(), cr);
						if (packet == null)
							packet = Ref.newInstance(
									Ref.constructor(Ref.nms("PacketPlayOutBlockChange"), Ref.nms("World"),
											Ref.nms("BlockPosition")),
									Ref.world(pos.getWorld()), pos.getBlockPosition());
						if (packet == null)
							packet = Ref.newInstance(
									Ref.constructor(Ref.nms("PacketPlayOutBlockChange"), int.class, int.class,
											int.class, Ref.nms("World")),
									pos.getBlockX(), pos.getBlockY(), pos.getBlockZ(), Ref.world(pos.getWorld()));
						for (Player p : TheAPI.getOnlinePlayers())
							Ref.sendPacket(p, packet);
					}
				}
				if (onFinish != null)
					onFinish.run();
			}
		}.runTask();
	}

	public static void asynchronizedReplace(Position a, Position b, Runnable onFinish,
			PercentageList<TheMaterial> block, TheMaterial with) {
		asynchronizedReplace(a, b, onFinish, block, Arrays.asList(with));
	}

}