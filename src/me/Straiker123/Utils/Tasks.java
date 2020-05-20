package me.Straiker123.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import me.Straiker123.LoaderClass;
import me.Straiker123.StringUtils;
import me.Straiker123.TheAPI;
import me.Straiker123.Events.EntityMoveEvent;
import me.Straiker123.Scheduler.Scheduler;
import me.Straiker123.Scheduler.Tasker;

public class Tasks {
	private static StringUtils f;
	private static boolean load;
	private static int task;

	public static void load() {
		f = TheAPI.getStringUtils();
		FileConfiguration c = LoaderClass.config.getConfig();
		FileConfiguration v = LoaderClass.unused.getConfig();
		if (load)
			return;
		load = true;
		if (c.getBoolean("Options.EntityMoveEvent.Enabled"))
			task=new Tasker() {
				public void run() {
					for (World w : Bukkit.getWorlds()) {
						for (Entity d : w.getEntities()) {
							if (d.getType() == EntityType.DROPPED_ITEM)
								continue;
							if (d instanceof LivingEntity) {
								Location a = d.getLocation();
								LivingEntity e = (LivingEntity) d;
								Location old = (v.getString("entities." + e.getUniqueId()) != null
										? f.getLocationFromString(v.getString("entities." + e.getUniqueId()))
										: a);
								if (v.getString("entities." + e.getUniqueId()) != null
										&& f.getLocationFromString(v.getString("entities." + e.getUniqueId())) != a) {
									EntityMoveEvent event = new EntityMoveEvent(e, old, a);
									Bukkit.getPluginManager().callEvent(event);
									if (event.isCancelled())
										e.teleport(old);
									else
										LoaderClass.unused.getConfig().set("entities." + e.getUniqueId(),
												f.getLocationAsString(a));
								}
							}
						}
					}
				}
			}.repeating(20, c.getInt("Options.EntityMoveEvent.Reflesh"));
	}

	public static void unload() {
		Events.f = LoaderClass.config.getConfig();
		Events.d = LoaderClass.data.getConfig();
		load = false;
		Scheduler.cancelTask(task);
	}
}
