package me.devtec.theapi.bukkit.packetlistener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PacketManager {
	private static final Priority[] list = Arrays
			.asList(Priority.LOWEST, Priority.LOW, Priority.NORMAL, Priority.HIGH, Priority.HIGHEST, Priority.MONITOR)
			.toArray(new Priority[5]);

	private static final Map<Priority, List<PacketListener>> listeners = new ConcurrentHashMap<>();
	static {
		for (Priority l : PacketManager.list)
			PacketManager.listeners.put(l, new ArrayList<>());
	}

	public static Object call(String player, Object packet, Object channel, PacketType type) {
		if (packet == null || channel == null)
			return packet;
		for (Priority o : PacketManager.list)
			for (PacketListener w : PacketManager.listeners.get(o)) {
				if ((packet == null) || (type == PacketType.PLAY_OUT ? w.playOut(player, packet, channel)
						: w.playIn(player, packet, channel)))
					return null;
			}
		return packet;
	}

	public static void register(PacketListener listener) {
		PacketManager.notify(listener, null, listener.getPriority());
	}

	public static void unregister(PacketListener listener) {
		PacketManager.listeners.get(listener.getPriority()).remove(listener);
	}

	public static void setPriority(PacketListener listener, Priority priority) {
		PacketManager.notify(listener, listener.getPriority(), priority);
		listener.priority = priority;
	}

	public static Priority getPriority(PacketListener listener) {
		return listener.getPriority();
	}

	public static boolean isRegistered(PacketListener listener) {
		for (Priority p : PacketManager.list)
			if (PacketManager.listeners.get(p).contains(listener))
				return true;
		return false;
	}

	protected static void notify(PacketListener listener, Priority old, Priority neww) {
		if (listener == null || neww == null)
			return;
		if (old != null)
			PacketManager.listeners.get(old).remove(listener);
		PacketManager.listeners.get(neww).add(listener);
	}

	public static void unregisterAll() {
		for (List<PacketListener> l : PacketManager.listeners.values())
			l.clear();
	}
}
