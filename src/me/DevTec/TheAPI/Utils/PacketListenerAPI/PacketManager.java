package me.DevTec.TheAPI.Utils.PacketListenerAPI;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;

import me.DevTec.TheAPI.Utils.DataKeeper.Collections.UnsortedList;
import me.DevTec.TheAPI.Utils.DataKeeper.Maps.UnsortedMap;

public class PacketManager {
	private static final List<Priority> list = Arrays.asList(Priority.LOWEST, Priority.LOW, Priority.NORMAL,
			Priority.HIGH, Priority.HIGHEST, Priority.MONITOR);
	private static final UnsortedMap<Priority, List<Listener>> listeners = new UnsortedMap<>();

	public static Object call(Player player, Object packet, Object channel, PacketType type) {
		if (packet == null || channel == null)
			return packet;
		for (Priority o : list) {
			if (listeners.containsKey(o))
				for (Listener w : listeners.get(o)) {
					packet = w.call(player, packet, channel, type == PacketType.PLAY_OUT);
				}
		}
		return packet;
	}

	public static void register(Listener listener) {
		notify(listener, null, listener.getPriority());
	}

	public static void unregister(Listener listener) {
		notify(listener, null, null);
	}

	public static void setPriority(Listener listener, Priority priority) {
		listener.setPriority(priority);
	}

	public static Priority getPriority(Listener listener) {
		return listener.getPriority();
	}

	public static boolean isRegistered(Listener listener) {
		boolean is = false;
		for (Priority p : listeners.keySet()) {
			if (listeners.get(p).contains(listener)) {
				is = true;
				break;
			}
		}
		return is;
	}

	public static void notify(Listener listener, Priority old, Priority neww) {
		if (listener == null)
			return;
		if (old != null)
			if (listeners.containsKey(old) && listeners.get(old).contains(listener)) {
				List<Listener> edit = listeners.get(old);
				edit.remove(listener);
				listeners.put(old, edit);
			}
		List<Listener> edit = listeners.containsKey(neww) ? listeners.get(neww) : new UnsortedList<>();
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
