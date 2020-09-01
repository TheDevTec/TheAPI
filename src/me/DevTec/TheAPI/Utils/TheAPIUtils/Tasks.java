package me.DevTec.TheAPI.Utils.TheAPIUtils;

import java.lang.reflect.Array;
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

import me.DevTec.TheAPI.TheAPI;
import me.DevTec.TheAPI.Events.EntityMoveEvent;
import me.DevTec.TheAPI.PlaceholderAPI.PlaceholderAPI;
import me.DevTec.TheAPI.Scheduler.Scheduler;
import me.DevTec.TheAPI.Scheduler.Tasker;
import me.DevTec.TheAPI.Utils.StringUtils;
import me.DevTec.TheAPI.Utils.Reflections.Ref;

public class Tasks {
	private static boolean load;
	private static int task;
	private static Class<?> c=Ref.getClass("com.mojang.authlib.GameProfile")!=null?Ref.getClass("com.mojang.authlib.GameProfile"):Ref.getClass("net.minecraft.util.com.mojang.authlib.GameProfile");
	private static me.DevTec.TheAPI.Utils.PacketListenerAPI.Listener l=null;

	public static void load() {
		FileConfiguration c = LoaderClass.config.getConfig();
		FileConfiguration v = LoaderClass.unused.getConfig();
		if (load)
			return;
		load = true;
		if(l==null)
		l=new me.DevTec.TheAPI.Utils.PacketListenerAPI.Listener() {
			@Override
			public void PacketPlayOut(Player player, Object packet) {
				if(packet.toString().contains("PacketStatusOutServerInfo")) {
					Object w = Ref.invoke(Ref.server(),"getServerPing");
					if(w==null)w=Ref.invoke(Ref.server(), "aG");
					if(w==null)w=Ref.invoke(Ref.invoke(Ref.server(),"getServer"), "getServerPing");
					Object sd = Ref.newInstance(Ref.constructor(Ref.nms("ServerPing$ServerPingPlayerSample"), int.class, int.class), LoaderClass.plugin.max>-1?LoaderClass.plugin.max:Bukkit.getMaxPlayers(),LoaderClass.plugin.fakeOnline>-1?LoaderClass.plugin.fakeOnline:TheAPI.getOnlinePlayers().size());
					if(LoaderClass.plugin.onlineText!=null && !LoaderClass.plugin.onlineText.isEmpty()) {
					Object[] a = (Object[]) Array.newInstance(Tasks.c, LoaderClass.plugin.onlineText.size());
					int i = 0;
					for(String s : LoaderClass.plugin.onlineText) {
						a[i]=Ref.createGameProfile(UUID.randomUUID(), TheAPI.colorize(PlaceholderAPI.setPlaceholders(null, s)));
						++i;
					}
					Ref.set(sd,"c", a);
					}else {
						int online = LoaderClass.plugin.fakeOnline>-1?LoaderClass.plugin.fakeOnline:TheAPI.getOnlinePlayers().size();
						List<Player> seen = Lists.newArrayList();
						for(Player s : TheAPI.getPlayers())
							if(!TheAPI.isVanished(s))
								seen.add(s);
						if(online==-1)online=seen.size();
						sd = Ref.newInstance(Ref.constructor(Ref.nms("ServerPing$ServerPingPlayerSample"), int.class, int.class), LoaderClass.plugin.max>-1?LoaderClass.plugin.max:Bukkit.getMaxPlayers(),online);
						Object[] a = (Object[]) Array.newInstance(Tasks.c, seen.size());
						int i = 0;
						for(Player s : seen) {
							a[i]=Ref.createGameProfile(s.getUniqueId(), s.getName());
							++i;
						}
						Ref.set(sd, "c", a);
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
						try {
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
						}}catch(Exception error) {}
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
