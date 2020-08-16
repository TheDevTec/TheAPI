package me.DevTec.Utils;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;

import me.DevTec.TheAPI;
import me.DevTec.Events.EntityMoveEvent;
import me.DevTec.Other.LoaderClass;
import me.DevTec.Other.Ref;
import me.DevTec.Other.StringUtils;
import me.DevTec.Placeholders.PlaceholderAPI;
import me.DevTec.Scheduler.Scheduler;
import me.DevTec.Scheduler.Tasker;

public class Tasks {
	private static boolean load;
	private static int task;
	private static me.DevTec.NMS.PacketListeners.Listener l=null;

	public static void load() {
		FileConfiguration c = LoaderClass.config.getConfig();
		FileConfiguration v = LoaderClass.unused.getConfig();
		if (load)
			return;
		load = true;
		if(l==null)
		l=new me.DevTec.NMS.PacketListeners.Listener() {
			@Override
			public void PacketPlayOut(Player player, Object packet) {
				if(packet.toString().contains("PacketStatusOutServerInfo")) {
					Object w = Ref.invoke(Ref.server(),"getServerPing");
					if(w==null)w=Ref.invoke(Ref.server(), "aG");
					Object sd = Ref.newInstance(Ref.constructor(Ref.nms("ServerPing$ServerPingPlayerSample"), int.class, int.class), LoaderClass.plugin.max>-1?LoaderClass.plugin.max:Bukkit.getMaxPlayers(),LoaderClass.plugin.fakeOnline>-1?LoaderClass.plugin.fakeOnline:TheAPI.getOnlinePlayers().size());
					if(LoaderClass.plugin.onlineText!=null && !LoaderClass.plugin.onlineText.isEmpty()) {
					GameProfile[] texts = new GameProfile[LoaderClass.plugin.onlineText.size()];
					int i = 0;
					for(String s : LoaderClass.plugin.onlineText) {
						texts[i]=new GameProfile(UUID.randomUUID(), TheAPI.colorize(PlaceholderAPI.setPlaceholders(null, s)));
						++i;
					}
					Ref.set(sd, "c", texts);
					}else {
						int online = LoaderClass.plugin.fakeOnline>-1?LoaderClass.plugin.fakeOnline:TheAPI.getOnlinePlayers().size();
						
						List<Player> seen = Lists.newArrayList();
						for(Player s : TheAPI.getPlayers())
							if(!TheAPI.isVanished(s))
								seen.add(s);
						if(online==-1)online=seen.size();
						sd = Ref.newInstance(Ref.constructor(Ref.nms("ServerPing$ServerPingPlayerSample"), int.class, int.class), LoaderClass.plugin.max>-1?LoaderClass.plugin.max:Bukkit.getMaxPlayers(),online);
						GameProfile[] texts = new GameProfile[seen.size()];
						int i = 0;
						for(Player s : seen) {
							texts[i]=new GameProfile(s.getUniqueId(), s.getName());
							++i;
						}
						Ref.set(sd, "c", texts);
						
					}
					Ref.set(w, "b", sd);
					if(LoaderClass.plugin.motd!=null)
					Ref.set(w, "a", Ref.IChatBaseComponent(TheAPI.colorize(PlaceholderAPI.setPlaceholders(null, LoaderClass.plugin.motd))));
					Ref.set(packet, "b", w);
				}
			}
			
			@Override
			public void PacketPlayIn(Player player, Object packet) {
				
			}
		};
		l.register();
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
										? StringUtils.getLocationFromString(v.getString("entities." + e.getUniqueId()))
										: a);
								if (v.getString("entities." + e.getUniqueId()) != null
										&& StringUtils.getLocationFromString(v.getString("entities." + e.getUniqueId())) != a) {
									EntityMoveEvent event = new EntityMoveEvent(e, old, a);
									Bukkit.getPluginManager().callEvent(event);
									if (event.isCancelled())
										e.teleport(old);
									else
										LoaderClass.unused.getConfig().set("entities." + e.getUniqueId(),
												StringUtils.getLocationAsString(a));
								}
							}
						}
					}
				}
			}.repeating(0, c.getInt("Options.EntityMoveEvent.Reflesh"));
	}

	public static void unload() {
		load = false;
		if(l!=null)
		l.unregister();
		Scheduler.cancelTask(task);
	}
}
