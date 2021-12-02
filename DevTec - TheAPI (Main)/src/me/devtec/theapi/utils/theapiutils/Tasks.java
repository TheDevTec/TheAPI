package me.devtec.theapi.utils.theapiutils;

import me.devtec.theapi.utils.packetlistenerapi.PacketListener;
import me.devtec.theapi.utils.reflections.Ref;

public class Tasks {
	private static boolean load;
	private static PacketListener l;
	

	public static void load() {
		if (load)
			return;
		load = true;
		if(LoaderClass.config.getBoolean("Options.ServerListPingEvent")) {
			if (l == null)
				l = new PacketListener() {
					final Class<?> info = Ref.nmsOrOld("network.protocol.status.PacketStatusOutServerInfo","PacketStatusOutServerInfo");
					public boolean PacketPlayOut(String player, Object packet, Object channel) {
						if (packet.getClass()==info) {
							LoaderClass.nmsProvider.processServerListPing(player, channel, packet);
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
