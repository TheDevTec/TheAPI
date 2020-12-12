package me.DevTec.TheAPI.Utils.PacketListenerAPI;

import java.util.Arrays;
import java.util.List;

import me.DevTec.TheAPI.Utils.DataKeeper.Collections.UnsortedList;
import me.DevTec.TheAPI.Utils.DataKeeper.Maps.UnsortedMap;

public class PacketManager {
	private static final List<Priority> list = Arrays.asList(Priority.LOWEST, Priority.LOW, Priority.NORMAL,
			Priority.HIGH, Priority.HIGHEST, Priority.MONITOR);
	private static final UnsortedMap<Priority, List<PacketListener>> listeners = new UnsortedMap<>();

	public static Object call(String player, Object packet, Object channel, PacketType type) {
		if (packet == null || channel == null)
			return packet;
		for (Priority o : list) {
			if (listeners.containsKey(o))
				for (PacketListener w : listeners.get(o)) {
					packet = w.call(player, packet, channel, type == PacketType.PLAY_OUT);
				}
		}
		return packet;
	}

	public static void register(PacketListener listener) {
		notify(listener, null, listener.getPriority());
	}

	public static void unregister(PacketListener listener) {
		notify(listener, null, null);
	}

	public static void setPriority(PacketListener listener, Priority priority) {
		listener.setPriority(priority);
	}

	public static Priority getPriority(PacketListener listener) {
		return listener.getPriority();
	}

	public static boolean isRegistered(PacketListener listener) {
		boolean is = false;
		for (Priority p : listeners.keySet()) {
			if (listeners.get(p).contains(listener)) {
				is = true;
				break;
			}
		}
		return is;
	}

	public static void notify(PacketListener listener, Priority old, Priority neww) {
		if (listener == null)
			return;
		if (old != null)
			if (listeners.containsKey(old) && listeners.get(old).contains(listener)) {
				List<PacketListener> edit = listeners.get(old);
				edit.remove(listener);
				listeners.put(old, edit);
			}
		List<PacketListener> edit = listeners.containsKey(neww) ? listeners.get(neww) : new UnsortedList<>();
		if (neww != null) {
			if (edit.contains(listener))
				return;
			edit.add(listener);
			listeners.put(neww, edit);
		} else {
			if (!edit.contains(listener))
				return;
			edit.remove(listener);
			listeners.put(neww, edit);
		}
	}
}
