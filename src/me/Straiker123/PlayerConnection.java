package me.Straiker123;

import org.bukkit.Location;

public class PlayerConnection {
	private Object a;
	private Class<?> c = Reflections.getNMSClass("PlayerConnection");

	public PlayerConnection(Object object) {
		a = object;
	}

	public void sendPacket(Object packet) {
		Reflections.processMethod(c, "sendPacket", packet);
	}

	public boolean processedDisconnect() {
		return (boolean) Reflections.get(a, "processedDisconnect");
	}

	public boolean isDisconnected() {
		return (boolean) Reflections.get(a, "isDisconnected");
	}

	public Player getPlayer() {
		return new Player(Reflections.get(a, "player"));
	}

	public void teleport(Location destination) {
		Reflections.processMethod(a, "teleport", destination);
	}

	public void disconnect(String reason) {
		Reflections.processMethod(a, "disconnect", reason);
	}

	public void chat(String message, boolean async) {
		Reflections.processMethod(a, "chat", message, async);
	}

	public NetworkManager getNetworkManager() {
		return new NetworkManager(Reflections.get(a, "networkManager"));
	}

}
