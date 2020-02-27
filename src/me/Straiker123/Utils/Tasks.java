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
			int next = 0;
			public void run() {
				if(Bukkit.getWorlds().size() <= next)next=0;
				if(TheAPI.getMemoryAPI().getFreeMemory(true) <= LoaderClass.config.getConfig().getDouble("Options.LagChecker.ClearMemIfPercentIsFree")) {
					synchronized(this) {
					String sd = TheAPI.getMemoryAPI().clearMemory();
					if(LoaderClass.config.getConfig().getBoolean("Options.Options.LagChecker.Log"))
					TheAPI.getConsole().sendMessage(TheAPI.colorize("&f[&bTheAPI - LagChecker&f] Cleared "+sd+" memory"));
					}
				}
				if(LoaderClass.config.getConfig().getBoolean("Options.LagChecker.ChunkMobLimit.Use")) {
					HashMap<Location,List<Entity>> ent = new HashMap<Location,List<Entity>>();
					for(Chunk d :Bukkit.getWorlds().get(next).getLoadedChunks()) {
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
								TheAPI.getConsole().sendMessage(TheAPI.colorize("&f[&bTheAPI - LagChecker&f] Too many entities ("+ent.size()+") in chunk X:"+loc.getBlockX()+", Z:"+loc.getBlockZ()+" in the world "+loc.getWorld().getName()));
							
							}
						ent.clear();
				}
				++next;
				}
			},20,20*TheAPI.getTimeConventorAPI().getTimeFromString(LoaderClass.config.getConfig().getString("Options.LagChecker.Reflesh"))));
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
							Location a = d.getLocation();
						LivingEntity e = (LivingEntity)d;
						if(LoaderClass.unused.getConfig().getString("entities."+e.getUniqueId())!=null) {
							String[] l = LoaderClass.unused.getConfig().getString("entities."+e.getUniqueId()).split(",");
							if(Bukkit.getWorld(l[0])!=null) {
							Location old = new Location(Bukkit.getWorld(l[0]),TheAPI.getNumbersAPI(l[1].replace("_", ".")).getDouble()
									,TheAPI.getNumbersAPI(l[2].replace("_", ".")).getDouble(),TheAPI.getNumbersAPI(l[3].replace("_", ".")).getDouble()
									,TheAPI.getNumbersAPI(l[4].replace("_", ".")).getLong(),TheAPI.getNumbersAPI(l[5].replace("_", ".")).getLong());
							if(move(e.getUniqueId(),a)) {
								EntityMoveEvent event = new EntityMoveEvent(e,old,a);
								Bukkit.getPluginManager().callEvent(event);
								if(event.isCancelled())
									e.teleport(old);
							}
							}else{
								LoaderClass.unused.getConfig().set("entities."+e,a.getWorld().getName()
										+","+
										String.valueOf(a.getX()).replace(".", "_")
										+","+
										String.valueOf(a.getY()).replace(".", "_")
										+","+
										String.valueOf(a.getZ()).replace(".", "_")
										+","+
										String.valueOf(a.getYaw()).replace(".", "_")
										+","+
										String.valueOf(a.getPitch()).replace(".", "_"));
							}}else 
								LoaderClass.unused.getConfig().set("entities."+e,a.getWorld().getName()
										+","+
										String.valueOf(a.getX()).replace(".", "_")
										+","+
										String.valueOf(a.getY()).replace(".", "_")
										+","+
										String.valueOf(a.getZ()).replace(".", "_")
										+","+
										String.valueOf(a.getYaw()).replace(".", "_")
										+","+
										String.valueOf(a.getPitch()).replace(".", "_"));
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
		String[] l = LoaderClass.unused.getConfig().getString("entities."+e).split(",");
		if(Bukkit.getWorld(l[0])!=null) {
			LoaderClass.unused.getConfig().set("entities."+e,a.getWorld().getName()
					+","+
					String.valueOf(a.getX()).replace(".", "_")
					+","+
					String.valueOf(a.getY()).replace(".", "_")
					+","+
					String.valueOf(a.getZ()).replace(".", "_")
					+","+
					String.valueOf(a.getYaw()).replace(".", "_")
					+","+
					String.valueOf(a.getPitch()).replace(".", "_"));
			return false;
		}
		if(new Location(Bukkit.getWorld(l[0]),TheAPI.getNumbersAPI(l[1].replace("_", ".")).getDouble()
				,TheAPI.getNumbersAPI(l[2].replace("_", ".")).getDouble(),TheAPI.getNumbersAPI(l[3].replace("_", ".")).getDouble()
				,TheAPI.getNumbersAPI(l[4].replace("_", ".")).getLong(),TheAPI.getNumbersAPI(l[5].replace("_", ".")).getLong())==a) {
			return false;
		}
		LoaderClass.unused.getConfig().set("entities."+e,a.getWorld().getName()
				+","+
				String.valueOf(a.getX()).replace(".", "_")
				+","+
				String.valueOf(a.getY()).replace(".", "_")
				+","+
				String.valueOf(a.getZ()).replace(".", "_")
				+","+
				String.valueOf(a.getYaw()).replace(".", "_")
				+","+
				String.valueOf(a.getPitch()).replace(".", "_"));
		return true;
	}
	
	public static void unload() {
		load=false;
		for(int i : s) Bukkit.getScheduler().cancelTask(i);
		s.clear();
	}
}
