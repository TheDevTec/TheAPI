package me.devtec.theapi.utils.theapiutils;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.scheduler.Scheduler;
import me.devtec.theapi.scheduler.Tasker;
import me.devtec.theapi.utils.listener.events.EntityMoveEvent;
import me.devtec.theapi.utils.listener.events.ServerListPingEvent;
import me.devtec.theapi.utils.nms.NMSAPI;
import me.devtec.theapi.utils.packetlistenerapi.PacketListener;
import me.devtec.theapi.utils.reflections.Ref;
import me.devtec.theapi.utils.serverlist.PlayerProfile;

public class Tasks {
	private static boolean load;
	private static int task;
	private static Class<?> c = Ref.getClass("com.mojang.authlib.GameProfile") != null
			? Ref.getClass("com.mojang.authlib.GameProfile")
			: Ref.getClass("net.minecraft.util.com.mojang.authlib.GameProfile");
	private static Constructor<?> cc = Ref.constructor(Ref.nmsOrOld("network.protocol.status.ServerPing$ServerPingPlayerSample","ServerPing$ServerPingPlayerSample")!=null?Ref.nmsOrOld("network.protocol.status.ServerPing$ServerPingPlayerSample","ServerPing$ServerPingPlayerSample"):Ref.nms("ServerPingPlayerSample"), int.class,
			int.class);
	private static PacketListener l;
	

	public static void load() {
		Map<UUID, Location> v = new HashMap<>();
		if (load)
			return;
		load = true;
		if(LoaderClass.config.getBoolean("Options.ServerListPingEvent")) {
			if (l == null)
				l = new PacketListener() {
				
					Class<?> info = Ref.nmsOrOld("network.protocol.status.PacketStatusOutServerInfo","PacketStatusOutServerInfo");
					public boolean PacketPlayOut(String player, Object packet, Object channel) {
						if (packet.getClass()==info) {
							Object w = Ref.get(packet, "b");
							List<PlayerProfile> players = new ArrayList<>();
							for (Player p : TheAPI.getOnlinePlayers())
								players.add(new PlayerProfile(p.getName(), p.getUniqueId()));
							ServerListPingEvent event = new ServerListPingEvent(TheAPI.getOnlinePlayers().size(),
									TheAPI.getMaxPlayers(), players, TheAPI.getMotd(), null,
									((InetSocketAddress) Ref.invoke(channel, "remoteAddress")).getAddress(), (String)Ref.get(Ref.get(w, TheAPI.isNewerThan(16)?"e":"c"),"a"));
							TheAPI.callEvent(event);
							if (event.isCancelled())
								return true;
							Object sd = Ref.newInstance(cc, event.getMaxPlayers(), event.getOnlinePlayers());
							if (event.getPlayersText() != null) {
								Object[] a = (Object[]) Array.newInstance(c, event.getPlayersText().size());
								int i = -1;
								for (PlayerProfile s : event.getPlayersText())
									a[++i] = Ref.createGameProfile(s.getUUID(), s.getName());
								Ref.set(sd, "c", a);
							} else
								Ref.set(sd, "c", (Object[]) Array.newInstance(c, 0));
							Ref.set(w, "b", sd);
	
							if (event.getMotd() != null)
								Ref.set(w, "a", NMSAPI.getFixedIChatBaseComponent(event.getMotd()));
							else
								Ref.set(w, "a", NMSAPI.getIChatBaseComponentText(""));
							if(event.getVersion()!=null)
								Ref.set(Ref.get(w, TheAPI.isNewerThan(16)?"e":"c"), "a", event.getVersion());
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
		}
		if (LoaderClass.config.getBoolean("Options.EntityMoveEvent.Enabled"))
			if(TheAPI.isNewerThan(16)) {
				task = new Tasker() {
				EntityMoveEvent event = new EntityMoveEvent(null, null, null);
				Field from = Ref.field(EntityMoveEvent.class, "from");
				Field to = Ref.field(EntityMoveEvent.class, "to");
				Field entity = Ref.field(EntityMoveEvent.class, "entity");
					public void run() {
						for (World w : Bukkit.getWorlds()) {
							try {
								List<Entity> ent = w.getEntities();
								new Tasker() {
									public void run() {
										for (Entity da : ent) {
											if (da instanceof LivingEntity) {
												LivingEntity e = (LivingEntity) da;
												Location a = e.getLocation();
												Location old = v.getOrDefault(e.getUniqueId(),a);
												if (!v.get(e.getUniqueId()).equals(a)) {
													Ref.set(event, from, old);
													Ref.set(event, to, a);
													Ref.set(event, entity, e);
													event.setCancelled(false);
													TheAPI.callEvent(event);
													if (event.isCancelled())
														e.teleport(old);
													else
														v.put(e.getUniqueId(), a);
												}
											}
										}
									}
								}.runTask();
							} catch (Exception error) {}
						}
					}
				}.runRepeatingSync(3, LoaderClass.config.getInt("Options.EntityMoveEvent.Reflesh"));
			}else {
				task = new Tasker() {
					EntityMoveEvent event = new EntityMoveEvent(null, null, null);
					Field from = Ref.field(EntityMoveEvent.class, "from");
					Field to = Ref.field(EntityMoveEvent.class, "to");
					Field entity = Ref.field(EntityMoveEvent.class, "entity");
						public void run() {
							for (World w : Bukkit.getWorlds()) {
								try {
									for (Entity da : w.getEntities()) {
										if (da instanceof LivingEntity) {
											LivingEntity e = (LivingEntity) da;
											Location a = e.getLocation();
											Location old = v.getOrDefault(e.getUniqueId(),a);
											if (!v.get(e.getUniqueId()).equals(a)) {
												Ref.set(event, from, old);
												Ref.set(event, to, a);
												Ref.set(event, entity, e);
												event.setCancelled(false);
												TheAPI.callEvent(event);
												if (event.isCancelled())
													e.teleport(old);
												else
													v.put(e.getUniqueId(), a);
											}
										}
									}
								} catch (Exception error) {}
							}
						}
					}.runRepeating(3, LoaderClass.config.getInt("Options.EntityMoveEvent.Reflesh"));
			}
	}

	public static void unload() {
		load = false;
		if (l != null)
			l.unregister();
		Scheduler.cancelTask(task);
	}
}
