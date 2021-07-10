package me.devtec.theapi.utils.theapiutils;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.utils.listener.events.ServerListPingEvent;
import me.devtec.theapi.utils.nms.NMSAPI;
import me.devtec.theapi.utils.packetlistenerapi.PacketListener;
import me.devtec.theapi.utils.reflections.Ref;
import me.devtec.theapi.utils.serverlist.PlayerProfile;

public class Tasks {
	private static boolean load;
	private static Class<?> c = Ref.getClass("com.mojang.authlib.GameProfile") != null
			? Ref.getClass("com.mojang.authlib.GameProfile")
			: Ref.getClass("net.minecraft.util.com.mojang.authlib.GameProfile");
	private static Constructor<?> cc = Ref.constructor(Ref.nmsOrOld("network.protocol.status.ServerPing$ServerPingPlayerSample","ServerPing$ServerPingPlayerSample")!=null?Ref.nmsOrOld("network.protocol.status.ServerPing$ServerPingPlayerSample","ServerPing$ServerPingPlayerSample"):Ref.nms("ServerPingPlayerSample"), int.class,
			int.class);
	private static PacketListener l;
	

	public static void load() {
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
									((InetSocketAddress) Ref.invoke(channel, "remoteAddress")).getAddress(), 
									(String)Ref.get(Ref.get(w, TheAPI.isNewerThan(16)?"e":"c"),"a"),
									(int)Ref.get(Ref.get(w, TheAPI.isNewerThan(16)?"e":"c"), "b"));
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
							Ref.set(w, TheAPI.isNewerThan(16)?"d":"b", sd);

							if (event.getMotd() != null)
								Ref.set(w, TheAPI.isNewerThan(16)?"c":"a", NMSAPI.getFixedIChatBaseComponent(event.getMotd()));
							else
								Ref.set(w, TheAPI.isNewerThan(16)?"c":"a", NMSAPI.getIChatBaseComponentText(""));
							if(event.getVersion()!=null)
								Ref.set(Ref.get(w, TheAPI.isNewerThan(16)?"e":"c"), "a",event.getVersion());
							Ref.set(Ref.get(w, TheAPI.isNewerThan(16)?"e":"c"), "b",event.getProtocol());
							if (event.getFalvicon() != null)
								Ref.set(packet, TheAPI.isNewerThan(16)?"f":"d", event.getFalvicon());
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
		}else l =null;
	}

	public static void unload() {
		load = false;
		if (l != null)
			l.unregister();
	}
}
