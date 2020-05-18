package me.Straiker123;

import org.bukkit.World.Environment;

import java.io.File;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;

import me.Straiker123.Utils.voidGenerator;
import me.Straiker123.Utils.voidGenerator_1_8;

public class WorldsManager {
	/**
	 * @return boolean if world exists
	 **/
	public boolean existsWorld(String world) {
		return Bukkit.getWorld(world) != null;
	}

	/**
	 * @see see This method ReCreate world, working as Import world commmand in
	 *      Multiverse
	 * @param world
	 * @param generator
	 * @param type
	 * @return boolean if world was loaded
	 */
	public boolean load(String world, Environment generator, WorldType type) {
		if (Bukkit.getWorld(world) != null)
			return false;
		return TheAPI.getWorldsManager().create(world, generator, type, true, 0);
	}

	/**
	 * @see see Create new world, return boolean if world was created
	 * @param name      Name of world (Required)
	 * @param generator World generator type (Required)
	 * @param type      set null to create Void world
	 * @return boolean if world was created
	 */
	public boolean create(String name, Environment generator, WorldType type) {
		return create(name, generator, type, true, 0);
	}

	/**
	 * @see see Create new world, return boolean if world was created
	 * @param name               Name of world (Required)
	 * @param generator          World generator type (Required)
	 * @param type               set null to create Void world
	 * @param generateStructures generate in world structures ?
	 * @param seed               set 0 to generate random
	 * @return boolean if world was created
	 */
	public boolean create(String name, Environment generator, WorldType type, boolean generateStructures, long seed) {
		if (name == null || generator == null)
			return false;

		if (Bukkit.getWorld(name) == null) {
			WorldCreator c = new WorldCreator(name);
			c.generateStructures(generateStructures);
			if (generator != null)
				c.environment(generator);
			if (seed != 0)
				c.seed(seed);
			if (type != null)
				c.type(type);
			else {
				// 1.5, 1.6 and 1.7 ?
				if (!TheAPI.getServerVersion().contains("v1_5") && !TheAPI.getServerVersion().contains("v1_6")
						&& !TheAPI.getServerVersion().contains("v1_7") && !TheAPI.getServerVersion().contains("v1_8"))
					c.generator(new voidGenerator());
				else
					c.generator(new voidGenerator_1_8());
			}
			c.createWorld();
			if (type == null) {
				Location loc = new Location(Bukkit.getWorld(name), 0, 60, 0);
				Bukkit.getWorld(name).setSpawnLocation(0, 60, 0);
				loc.getBlock().setType(Material.GLASS);
			}
			return true;
		}
		return false;
	}

	private static boolean deleteDirectory(File path) {
		if (path.exists()) {
			for (File f : path.listFiles()) {
				if (f.isDirectory()) {
					deleteDirectory(f);
				} else {
					f.delete();
				}
			}
		}
		return (path.delete());
	}

	public boolean delete(World name, boolean safeUnloadWorld) {
		return delete(name, safeUnloadWorld, false);
	}

	public boolean delete(World name, boolean safeUnloadWorld, boolean keepFolder) {
		if (name == null)
			return false;
		if (!safeUnloadWorld) {
			List<World> w = Bukkit.getWorlds();
			w.remove(name);
			if (w.isEmpty() == false) {
				Bukkit.unloadWorld(name, false);
				if (!keepFolder) {
					boolean delete = deleteDirectory(name.getWorldFolder());
					return delete;
				} else
					return true;
			}
			return false;
		} else {
			List<World> w = Bukkit.getWorlds();
			w.remove(name);
			if (w.isEmpty() == false) {
				Bukkit.unloadWorld(name, true);
				if (!keepFolder) {
					boolean delete = deleteDirectory(name.getWorldFolder());
					return delete;
				} else
					return true;
			}
			return false;
		}
	}

	public boolean unloadWorld(String name) {
		return unloadWorld(name, true);
	}

	public boolean unloadWorld(String name, boolean saveWorld) {
		if (name == null)
			return false;
		if (Bukkit.getWorld(name) != null) {
			List<World> w = Bukkit.getWorlds();
			w.remove(Bukkit.getWorld(name));
			if (w.isEmpty() == false) {
				for (Player p : TheAPI.getOnlinePlayers())
					if (p.getWorld().getName().equals(name)) {
						TheAPI.getPlayerAPI(p).setGodOnTime(30);
						TheAPI.getPlayerAPI(p).safeTeleport(w.get(0).getSpawnLocation());
					}
				Bukkit.unloadWorld(name, saveWorld);
				return true;
			}
			return false;
		}
		return false;
	}
}
