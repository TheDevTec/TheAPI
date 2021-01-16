package me.devtec.theapi.utils.thapiutils;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.scheduler.Scheduler;
import me.devtec.theapi.scheduler.Tasker;
import me.devtec.theapi.utils.datakeeper.Data;
import me.devtec.theapi.utils.listener.events.EntityMoveEvent;
import me.devtec.theapi.utils.listener.events.ServerListPingEvent;
import me.devtec.theapi.utils.packetlistenerapi.PacketListener;
import me.devtec.theapi.utils.reflections.Ref;
import me.devtec.theapi.utils.serverlist.PlayerProfile;

public class Tasks {
	private static boolean load;
	private static int task;
	private static Class<?> c = Ref.getClass("com.mojang.authlib.GameProfile") != null
			? Ref.getClass("com.mojang.authlib.GameProfile")
			: Ref.getClass("net.minecraft.util.com.mojang.authlib.GameProfile");
	private static Constructor<?> cc = Ref.constructor(Ref.nms("ServerPing$ServerPingPlayerSample"), int.class,
			int.class);
	private static PacketListener l;

	public static void load() {
		Data v = new Data();
		if (load)
			return;
		load = true;
		if (l == null)
			l = new PacketListener() {
				public boolean PacketPlayOut(String player, Object packet, Object channel) {
					if (packet.getClass().getCanonicalName().endsWith("PacketStatusOutServerInfo")) {
						Object w = Ref.invoke(Ref.server(), "getServerPing");
						if (w == null)
							w = Ref.invoke(Ref.server(), "aG");
						if (w == null)
							w = Ref.invoke(Ref.invoke(Ref.server(), "getServer"), "getServerPing");
						List<PlayerProfile> players = new ArrayList<>();
						for (Player p : TheAPI.getOnlinePlayers())
							players.add(new PlayerProfile(p.getName(), p.getUniqueId()));
						ServerListPingEvent event = new ServerListPingEvent(TheAPI.getOnlinePlayers().size(),
								TheAPI.getMaxPlayers(), players, TheAPI.getMotd(), null,
								((InetSocketAddress) Ref.invoke(channel, "remoteAddress")).getAddress(), (String)Ref.get(Ref.get(w, "c"),"a"));
						TheAPI.callEvent(event);
						if (event.isCancelled())
							return true;
						Object sd = Ref.newInstance(cc, event.getMaxPlayers(), event.getOnlinePlayers());
						if (event.getPlayersText() != null) {
							Object[] a = (Object[]) Array.newInstance(c, event.getPlayersText().size());
							int i = -1;
							for (PlayerProfile s : event.getPlayersText())
								a[++i] = Ref.createGameProfile(s.uuid, s.name);
							Ref.set(sd, "c", a);
						} else
							Ref.set(sd, "c", (Object[]) Array.newInstance(c, 0));
						Ref.set(w, "b", sd);

						if (event.getMotd() != null)
							Ref.set(w, "a", Ref.IChatBaseComponent(event.getMotd()));
						else
							Ref.set(w, "a", Ref.IChatBaseComponent(""));
						if(event.getVersion()!=null) {
							Ref.set(Ref.get(w, "c"), "a", event.getVersion());
						}
						Ref.set(packet, "b", w);
						if (event.getFalvicon() != null)
							Ref.set(packet, "d", event.getFalvicon());
						return false;
					}
					return false;
				}

				@Override
				public boolean PacketPlayIn(String player, Object packet, Object channel) {
					return false;
				}
			};
		l.register();
		if (LoaderClass.config.getBoolean("Options.EntityMoveEvent.Enabled"))
			task = new Tasker() {
				public void run() {
					for (World w : Bukkit.getWorlds()) {
						try {
							for (Entity da : w.getEntities()) {
								if (da instanceof LivingEntity) {
									LivingEntity e = (LivingEntity) da;
									Location a = e.getLocation();
									Location old = v.exists(e.getUniqueId().toString())
											? (Location) v.get(e.getUniqueId().toString())
											: a;
									if (v.exists(e.getUniqueId().toString())
											&& v.get(e.getUniqueId().toString()) != a) {
										EntityMoveEvent event = new EntityMoveEvent(e, old, a);
										TheAPI.callEvent(event);
										if (event.isCancelled())
											e.teleport(old);
										else
											v.set(e.getUniqueId().toString(), a);
									}
								}
							}
						} catch (Exception error) {}
					}
				}
			}.runRepeating(0, LoaderClass.config.getInt("Options.EntityMoveEvent.Reflesh"));
	}

	public static void unload() {
		load = false;
		if (l != null)
			l.unregister();
		Scheduler.cancelTask(task);
	}
}
