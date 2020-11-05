package me.DevTec.TheAPI.Utils.NMS;

import java.lang.reflect.Method;

import org.bukkit.Location;

import me.DevTec.TheAPI.Utils.Reflections.Reflections;

public class PlayerConnection {
	private Object a;
	private static Method m = Reflections.getMethod(Reflections.getNMSClass("PlayerConnection"), "sendPacket",
			Reflections.getNMSClass("Packet"));

	public PlayerConnection(Object object) {
		a = object;
	}

	public void sendPacket(Object packet) {
		Reflections.invoke(a, m, packet);
	}

	public boolean processedDisconnect() {
		return (boolean) Reflections
				.get(Reflections.getField(Reflections.getNMSClass("PlayerConnection"), "processedDisconnect"), a);
	}

	public boolean isDisconnected() {
		return (boolean) Reflections
				.get(Reflections.getField(Reflections.getNMSClass("PlayerConnection"), "isDisconnected"), a);
	}

	public NMSPlayer getPlayer() {
		return new NMSPlayer(
				Reflections.get(Reflections.getField(Reflections.getNMSClass("PlayerConnection"), "player"), a));
	}

	public void teleport(Location destination) {
		Reflections.invoke(a,
				Reflections.getMethod(Reflections.getNMSClass("PlayerConnection"), "teleport", Location.class),
				destination);
	}

	public void disconnect(String reason) {
		Reflections.invoke(a,
				Reflections.getMethod(Reflections.getNMSClass("PlayerConnection"), "disconnect", String.class), reason);
	}

	public void chat(String message, boolean async) {
		Reflections.invoke(a,
				Reflections.getMethod(Reflections.getNMSClass("PlayerConnection"), "chat", String.class, boolean.class),
				message, async);
	}

	public NetworkManager getNetworkManager() {
		return new NetworkManager(Reflections
				.get(Reflections.getField(Reflections.getNMSClass("PlayerConnection"), "networkManager"), a));
	}

}
