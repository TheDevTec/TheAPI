package me.Straiker123.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Tameable;

import me.Straiker123.LoaderClass;
import me.Straiker123.StringUtils;
import me.Straiker123.TheAPI;
import me.Straiker123.Events.EntityMoveEvent;

public class Tasks {
	private static StringUtils f;
	private static boolean load;
	private static List<Integer> s = new ArrayList<Integer>();
	private static boolean con(Entity s) {
		boolean c = false;
		if(s.getType()!=EntityType.PLAYER) {
		for(String d: LoaderClass.config.getConfig().getStringList("Options.LagChecker.ChunkMobLimit.Bypass"))
			if(d.toLowerCase().startsWith("tamed_")) {
					if(s instanceof LivingEntity && s instanceof Tameable) {
						d=d.substring(6,d.length());
						if(((Tameable)s).isTamed() && d.equalsIgnoreCase(s.getType().name())) {
							c=true;
							break;
						}
					}
			}else
			if(d.equalsIgnoreCase(s.getType().name())) {
				c=true;
				break;
			}
		}else c=true;
		return c;
	}
	public static void load() {
		f = TheAPI.getStringUtils();
		FileConfiguration c= LoaderClass.config.getConfig();
		FileConfiguration v= LoaderClass.unused.getConfig();
		if(load)return;
		load=true;
		if(LoaderClass.config.getConfig().getBoolean("Options.LagChecker.Enabled"))
		s.add(Bukkit.getScheduler().scheduleSyncRepeatingTask(LoaderClass.plugin, new Runnable() {
			int next = 0;
			public void run() {
				if(Bukkit.getWorlds().size() <= next)next=0;
				if(TheAPI.getMemoryAPI().getFreeMemory(true) <= c.getDouble("Options.LagChecker.ClearMemIfPercentIsFree")) {
					synchronized(this) {
					String sd = TheAPI.getMemoryAPI().clearMemory();
					if(c.getBoolean("Options.Options.LagChecker.Log"))
					TheAPI.msg("&f[&bTheAPI - LagChecker&f] Cleared "+sd+" memory",TheAPI.getConsole());
					}
				}
				if(c.getBoolean("Options.LagChecker.ChunkMobLimit.Use")) {
					HashMap<Location,List<Entity>> ent = new HashMap<Location,List<Entity>>();
					for(Chunk d :Bukkit.getWorlds().get(next).getLoadedChunks()) {
						if(d.getEntities().length < c.getInt("Options.LagChecker.ChunkMobLimit.Limit"))continue;
						List<Entity> es = new ArrayList<Entity>();
						for(Entity awd : d.getEntities()) {
							if(!con(awd))
								es.add(awd);
						}
						if(es.isEmpty()==false)ent.put(d.getBlock(0, 0, 0).getLocation(), es);
					}
					for(Location loc : ent.keySet()) {
						if(ent.get(loc).size() < c.getInt("Options.LagChecker.ChunkMobLimit.Limit"))continue;
							if(c.getString("Options.LagChecker.ChunkMobLimit.OnLimitExceeded").equalsIgnoreCase("kill")
									||c.getString("Options.LagChecker.ChunkMobLimit.OnLimitExceeded").equalsIgnoreCase("remove")) {
								if(c.getBoolean("Options.LagChecker.Log"))
								TheAPI.msg("&f[&bTheAPI - LagChecker&f] Killed ("+ent.size()+") entities",TheAPI.getConsole());
								for(Entity e : ent.get(loc)) {
									e.remove();
								}
							}else
								TheAPI.msg("&f[&bTheAPI - LagChecker&f] Too many entities ("+ent.size()+") in chunk X:"+loc.getBlockX()+", Z:"+loc.getBlockZ()+" in the world "+loc.getWorld().getName(),TheAPI.getConsole());
							
							}
						ent.clear();
				}
				++next;
				}
			},20,20*f.getTimeFromString(c.getString("Options.LagChecker.Reflesh"))));
		if(TheAPI.isOlder1_9() && !TheAPI.getServerVersion().equals("v1_7_R4")&& !TheAPI.getServerVersion().startsWith("v1_8")) 
			s.add(Bukkit.getScheduler().scheduleSyncRepeatingTask(LoaderClass.plugin, new Runnable() {
				public void run() {
		TheAPI.msg("&bTheAPI&7: &8********************",TheAPI.getConsole());
		TheAPI.msg("&bTheAPI&7: &6Info: &cYour server version isn't supported! ("+TheAPI.getServerVersion()+")",TheAPI.getConsole());
		TheAPI.msg("&bTheAPI&7: &8********************",TheAPI.getConsole());
	}}, 20, 20*60*3600));
		if(c.getBoolean("Options.EntityMoveEvent.Enabled"))
			s.add(Bukkit.getScheduler().scheduleSyncRepeatingTask(LoaderClass.plugin, new Runnable() {
			@Override
			public void run() {
				for(World w: Bukkit.getWorlds()) {
					for(Entity d :w.getEntities()) {
						if(d.getType()==EntityType.DROPPED_ITEM)continue;
						if(d instanceof LivingEntity) {
							Location a = d.getLocation();
						LivingEntity e = (LivingEntity)d;
							Location old = (v.getString("entities."+e.getUniqueId()) != null ? 
									f.getLocationFromString(v.getString("entities."+e.getUniqueId())):a);
							if(v.getString("entities."+e.getUniqueId()) != null &&f.getLocationFromString(v.getString("entities."+e.getUniqueId()))!=a) {
							EntityMoveEvent event = new EntityMoveEvent(e,old,a);
							Bukkit.getPluginManager().callEvent(event);
							if(event.isCancelled())
								e.teleport(old);
							else
							LoaderClass.unused.getConfig().set("entities."+e.getUniqueId(),f.getLocationAsString(a));
							}
					}}
				}
			}
		}, 20, c.getInt("Options.EntityMoveEvent.Reflesh")));
		else {
			TheAPI.msg("&bTheAPI&7: &8********************",TheAPI.getConsole());
			TheAPI.msg("&bTheAPI&7: &6EntityMoveEvent is disabled.",TheAPI.getConsole());
			TheAPI.msg("&bTheAPI&7: &6You can enable EntityMoveEvent in Config.yml",TheAPI.getConsole());
			TheAPI.msg("&bTheAPI&7: &6 *TheAPI will still normally work without problems*",TheAPI.getConsole());
			TheAPI.msg("&bTheAPI&7: &8********************",TheAPI.getConsole());

		}
	}
	
	public static void unload() {
		 Events.f = LoaderClass.config.getConfig();
		 Events.d = LoaderClass.data.getConfig();
		load=false;
		for(int i : s) Bukkit.getScheduler().cancelTask(i);
		s.clear();
	}
}
