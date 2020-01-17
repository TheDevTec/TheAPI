package me.Straiker123.Utils;

import org.bukkit.entity.Player;

import me.Straiker123.TheAPI;

public class Packets {
	public static Object getNMSPlayer(Player p) throws Exception {
	       return p.getClass().getMethod("getHandle", new Class[0]).invoke(p, new Object[0]);
	}

	public static Class<?> getNMSClass(String name) {
	     try {
	         return Class.forName("net.minecraft.server." + TheAPI.getServerVersion() + "." + name);
	     } catch (ClassNotFoundException e) {
				TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &4Error when finding class 'net.minecraft.server."+TheAPI.getServerVersion() + "." + name+"', server version: "+TheAPI.getServerVersion()));
		         return null;
	     }
	}
	public static void sendPacket(Player player, Object packet) {
	     try {
	         Object handle = getNMSPlayer(player);
	         Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
	         playerConnection.getClass().getMethod("sendPacket", new Class[] { getNMSClass("Packet") }).invoke(playerConnection, new Object[] { packet });
	     } catch (Exception e) {
				TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &4Error when sending packets to player "+player.getName()+", server version: "+TheAPI.getServerVersion()));
	     }
	}
}
