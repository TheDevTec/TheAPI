package me.devtec.theapi.bukkit.packetlistener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.Map;

public class PacketManager {
	private static final Priority[] list = Arrays.asList(Priority.LOWEST, Priority.LOW, Priority.NORMAL,
			Priority.HIGH, Priority.HIGHEST, Priority.MONITOR).toArray(new Priority[5]);
	
	private static final Map<Priority, List<PacketListener>> listeners = new ConcurrentHashMap<>();
	static {
		for(Priority l : list)listeners.put(l, new ArrayList<>());
	}
	public static Object call(String player, Object packet, Object channel, PacketType type) {
		if (packet == null || channel == null)
			return packet;
		for (Priority o : list)
			for (PacketListener w : listeners.get(o)) {
				if(packet==null)return null;
				if (type == PacketType.PLAY_OUT ? w.PacketPlayOut(player, packet, channel) : w.PacketPlayIn(player, packet, channel))
					return null;
			}
		return packet;
	}

	public static void register(PacketListener listener) {
		notify(listener, null, listener.getPriority());
	}

	public static void unregister(PacketListener listener) {
		listeners.get(listener.getPriority()).remove(listener);
	}

	public static void setPriority(PacketListener listener, Priority priority) {
		PacketManager.notify(listener, listener.getPriority(), priority);
		listener.priority=priority;
	}

	public static Priority getPriority(PacketListener listener) {
		return listener.getPriority();
	}

	public static boolean isRegistered(PacketListener listener) {
		for (Priority p : list)
			if (listeners.get(p).contains(listener))return true;
		return false;
	}

	protected static void notify(PacketListener listener, Priority old, Priority neww) {
		if (listener == null || neww == null)
			return;
		if (old != null)listeners.get(old).remove(listener);
		listeners.get(neww).add(listener);
	}

	public static void unregisterAll() {
		for(List<PacketListener> l : listeners.values())
			l.clear();
	}
}
