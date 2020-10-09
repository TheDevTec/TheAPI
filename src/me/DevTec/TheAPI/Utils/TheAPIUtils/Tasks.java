package me.DevTec.TheAPI.Utils.TheAPIUtils;

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

import me.DevTec.TheAPI.TheAPI;
import me.DevTec.TheAPI.Events.EntityMoveEvent;
import me.DevTec.TheAPI.Scheduler.Scheduler;
import me.DevTec.TheAPI.Scheduler.Tasker;
import me.DevTec.TheAPI.Utils.DataKeeper.Data;
import me.DevTec.TheAPI.Utils.Listener.Events.PlayerReceiveMessageEvent;
import me.DevTec.TheAPI.Utils.Listener.Events.ServerListPingEvent;
import me.DevTec.TheAPI.Utils.Reflections.Ref;
import me.DevTec.TheAPI.Utils.ServerList.PlayerProfile;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class Tasks {
	private static boolean load;
	private static int task;
	private static Class<?> c=Ref.getClass("com.mojang.authlib.GameProfile")!=null?Ref.getClass("com.mojang.authlib.GameProfile"):Ref.getClass("net.minecraft.util.com.mojang.authlib.GameProfile");
	private static Constructor<?> cc = Ref.constructor(Ref.nms("ServerPing$ServerPingPlayerSample"), int.class, int.class);
	private static me.DevTec.TheAPI.Utils.PacketListenerAPI.Listener l;
	
	private static String toString(Object component) {
		if(component==null)return "";
		StringBuilder out = new StringBuilder();
		Object o = Ref.cast(Ref.nms("ChatComponentText"), component);
	    for(Object a : (Iterable<?>)o) {
			out.append(color(a)+Ref.invoke(a, "getText"));
	    }
	    if(out.toString().isEmpty())
		out.append(color(o)+Ref.invoke(o, "getText"));
	    return out.toString();
	}
	
	private static String color(Object o) {
		Object color = Ref.get(Ref.invoke(Ref.invoke(o, "getChatModifier"),"getColor"),"format");
		return color==null?"":color.toString();
	}
	
	public static void load() {
		Data v = new Data();
		if (load)return;
		load = true;
		if(l==null)
		l=new me.DevTec.TheAPI.Utils.PacketListenerAPI.Listener() {
			public boolean PacketPlayOut(Player player, Object packet, Object channel) {
				if(packet.toString().contains("PacketPlayOutChat")) {
					Object request = Ref.get(packet, "a");
					if(request==null) {
						try {
							request=TextComponent.toLegacyText((BaseComponent[])Ref.get(packet, "components"));
						}catch(Exception err) {
							request=null;
						}
					}
					PlayerReceiveMessageEvent message = new PlayerReceiveMessageEvent(player, (request instanceof String ? (String)request : Tasks.toString(request)));
					TheAPI.callEvent(message);
					Ref.set(packet, "a", Ref.invokeNulled(Ref.method(Ref.craft("util.CraftChatMessage"), "fixComponent", Ref.nms("IChatBaseComponent")),Ref.IChatBaseComponent(message.getMessage())));
					return false;
				}
				if(packet.toString().contains("PacketStatusOutServerInfo")) {
					Object w = Ref.invoke(Ref.server(),"getServerPing");
					if(w==null)w=Ref.invoke(Ref.server(), "aG");
					if(w==null)w=Ref.invoke(Ref.invoke(Ref.server(),"getServer"), "getServerPing");
					List<PlayerProfile> players = new ArrayList<>();
					for(Player p : TheAPI.getOnlinePlayers())
						players.add(new PlayerProfile(p.getName(),p.getUniqueId()));
					ServerListPingEvent event = new ServerListPingEvent(TheAPI.getOnlinePlayers().size(), TheAPI.getMaxPlayers(), players, TheAPI.getMotd(), null, ((InetSocketAddress)Ref.invoke(channel, "localAddress")).getAddress());
					TheAPI.callEvent(event);
					if(event.isCancelled())
						return true;
					Object sd = Ref.newInstance(cc, event.getMaxPlayers(),event.getOnlinePlayers());
					if(event.getPlayersText()!=null) {
					Object[] a = (Object[]) Array.newInstance(c, event.getPlayersText().size());
					int i = -1;
					for(PlayerProfile s : event.getPlayersText())
						a[++i]=Ref.createGameProfile(s.uuid, s.name);
					Ref.set(sd,"c", a);
					}else
						Ref.set(sd,"c", (Object[]) Array.newInstance(c, 0));
					Ref.set(w, "b", sd);
					
					if(event.getMotd()!=null)
						Ref.set(w, "a", Ref.IChatBaseComponent(event.getMotd()));
					else
						Ref.set(w, "a", Ref.IChatBaseComponent(""));
					Ref.set(packet, "b", w);
					if(event.getFalvicon()!=null)
					Ref.set(packet, "d", event.getFalvicon());
					return false;
				}
				return false;
			}

			@Override
			public boolean PacketPlayIn(Player player, Object packet, Object channel) {
				return false;
			}
		};
		l.register();
		if (LoaderClass.config.getBoolean("Options.EntityMoveEvent.Enabled"))
			task=new Tasker() {
				public void run() {
					for (World w : Bukkit.getWorlds()) {
						try {
						for (Entity da : w.getEntities()) {
							if (da instanceof LivingEntity) {
								LivingEntity e = (LivingEntity) da;
								Location a = e.getLocation();
								Location old = v.exists(e.getUniqueId().toString()) ? (Location)v.get(e.getUniqueId().toString()) : a;
								if (v.exists(e.getUniqueId().toString())
										&& v.get(e.getUniqueId().toString()) != a) {
									EntityMoveEvent event = new EntityMoveEvent(e, old, a);
									Bukkit.getPluginManager().callEvent(event);
									if (event.isCancelled())
										e.teleport(old);
									else
										v.set(e.getUniqueId().toString(),a);
								}
							}
						}}catch(Exception error) {}
					}
				}
			}.runRepeating(0, LoaderClass.config.getInt("Options.EntityMoveEvent.Reflesh"));
	}

	public static void unload() {
		load = false;
		if(l!=null)
		l.unregister();
		Scheduler.cancelTask(task);
	}
}
