package me.Straiker123.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Tameable;

import me.Straiker123.LoaderClass;
import me.Straiker123.TheAPI;
import me.Straiker123.Events.EntityMoveEvent;

public class Tasks {
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
		if(load)return;
		load=true;
		if(LoaderClass.config.getConfig().getBoolean("Options.LagChecker.Enabled"))
		s.add(Bukkit.getScheduler().scheduleSyncRepeatingTask(LoaderClass.plugin, new Runnable() {
			int w=0;
			public void run() {
				if(Bukkit.getWorlds().size() == w)w=0;
				if(TheAPI.getMemoryAPI().getFreeMemory(true) >= LoaderClass.config.getConfig().getInt("Options.LagChecker.ClearMemIfPercentIsFree")) {
					String sd = TheAPI.getMemoryAPI().clearMemory();
					if(LoaderClass.config.getConfig().getBoolean("Options.Options.LagChecker.Log"))
					TheAPI.getConsole().sendMessage(TheAPI.colorize("&f[&bTheAPI - LagChecker&f] Cleared "+sd+" memory"));
				}
				if(LoaderClass.config.getConfig().getBoolean("Options.LagChecker.ChunkMobLimit.Use")) {
					HashMap<Location,List<Entity>> ent = new HashMap<Location,List<Entity>>();
					synchronized(this) {
					for(Chunk d : Bukkit.getWorlds().get(w).getLoadedChunks()) {
						if(d.getEntities().length < LoaderClass.config.getConfig().getInt("Options.LagChecker.ChunkMobLimit.Limit"))continue;
						List<Entity> es = new ArrayList<Entity>();
						for(Entity awd : d.getEntities()) {
							if(!con(awd))
								es.add(awd);
						}
						if(es.isEmpty()==false)ent.put(d.getBlock(0, 0, 0).getLocation(), es);
					}
					for(Location loc : ent.keySet()) {
						if(ent.get(loc).size() < LoaderClass.config.getConfig().getInt("Options.LagChecker.ChunkMobLimit.Limit"))continue;
							if(LoaderClass.config.getConfig().getString("Options.LagChecker.ChunkMobLimit.OnLimitExceeded").equalsIgnoreCase("kill")
									||LoaderClass.config.getConfig().getString("Options.LagChecker.ChunkMobLimit.OnLimitExceeded").equalsIgnoreCase("remove")) {
								if(LoaderClass.config.getConfig().getBoolean("Options.LagChecker.Log"))
								TheAPI.getConsole().sendMessage(TheAPI.colorize("&f[&bTheAPI - LagChecker&f] Killed ("+ent.size()+") entities"));
								for(Entity e : ent.get(loc)) {
									e.remove();
								}
							}else
								TheAPI.getConsole().sendMessage(TheAPI.colorize("&f[&bTheAPI - LagChecker&f] Too many entities ("+ent.size()+") in chunk X:"+loc.getBlockX()+", Z:"+loc.getBlockZ()+" in the world "+Bukkit.getWorlds().get(w).getName()));
							
							}
						ent.clear();
				}}
				++w;
				}
			},20,20*LoaderClass.config.getConfig().getInt("Options.LagChecker.Reflesh")));
		if(!TheAPI.isNewVersion() && !TheAPI.getServerVersion().startsWith("v1_12")
				&& !TheAPI.getServerVersion().startsWith("v1_11")
				&& !TheAPI.getServerVersion().startsWith("v1_10")
				&& !TheAPI.getServerVersion().startsWith("v1_9")
				&& !TheAPI.getServerVersion().startsWith("v1_8")
				&& !TheAPI.getServerVersion().equals("v1_7_R4")) 
			s.add(Bukkit.getScheduler().scheduleSyncRepeatingTask(LoaderClass.plugin, new Runnable() {
				public void run() {
		TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &8********************"));
		TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &6Info: &cYour server version isn't supported!"));
		TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &8********************"));
	}}, 20, 20*60*3600));
		if(LoaderClass.config.getConfig().getBoolean("Options.EntityMoveEvent.Enabled"))
			s.add(Bukkit.getScheduler().scheduleSyncRepeatingTask(LoaderClass.plugin, new Runnable() {
			@Override
			public void run() {
				for(World w: Bukkit.getWorlds()) {
					for(Entity d :w.getEntities()) {
						if(d.getType()==EntityType.DROPPED_ITEM)continue;
						if(d instanceof LivingEntity) {
						LivingEntity e = (LivingEntity)d;
						if(LoaderClass.data.getConfig().getString("entities."+e.getUniqueId())!=null) {
							Location old = (Location)LoaderClass.data.getConfig().get("entities."+e.getUniqueId());
							if(move(e.getUniqueId(),e.getLocation())) {
								EntityMoveEvent event = new EntityMoveEvent(e,old,e.getLocation());
								Bukkit.getPluginManager().callEvent(event);
								if(event.isCancelled())
									e.teleport(old);
							}
					}else
						LoaderClass.data.getConfig().set("entities."+e.getUniqueId(),e.getLocation());
					}}
				}
			}
		}, 20, LoaderClass.config.getConfig().getInt("Options.EntityMoveEvent.Reflesh")));
		else {
			TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &8********************"));
			TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &6EntityMoveEvent is disabled."));
			TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &6You can enable EntityMoveEvent in Config.yml"));
			TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &6 *TheAPI will still normally work without problems*"));
			TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &8********************"));

		}
	}

	private static boolean move(UUID e,Location a) {
		if((Location)LoaderClass.data.getConfig().get("entities."+e)==a) {
			return false;
		}
		LoaderClass.data.getConfig().set("entities."+e,a);
		return true;
	}
	
	public static void unload() {
		load=false;
		for(int i : s) Bukkit.getScheduler().cancelTask(i);
		s.clear();
	}
}
