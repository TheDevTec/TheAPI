package me.DevTec.TheAPI.Utils.PacketListenerAPI;

import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class PacketManager {
	private static final HashMap<Priority, List<Listener>> listeners = Maps.newHashMap();
	public static Object call(Player player, Object packet, PacketType type) {
		if(type==PacketType.PLAY_IN) {
			if(listeners.containsKey(Priority.LOWEST))
			for(Listener w : listeners.get(Priority.LOWEST))
				w.call(player, packet, false);
			if(listeners.containsKey(Priority.LOW))
			for(Listener w : listeners.get(Priority.LOW))
				w.call(player, packet, false);
			if(listeners.containsKey(Priority.NORMAL))
			for(Listener w : listeners.get(Priority.NORMAL))
				w.call(player, packet, false);
			if(listeners.containsKey(Priority.HIGH))
			for(Listener w : listeners.get(Priority.HIGH))
				w.call(player, packet, false);
			if(listeners.containsKey(Priority.HIGHEST))
			for(Listener w : listeners.get(Priority.HIGHEST))
				w.call(player, packet, false);
			if(listeners.containsKey(Priority.MONITOR))
			for(Listener w : listeners.get(Priority.MONITOR))
				w.call(player, packet, false);
		}else {
			if(listeners.containsKey(Priority.LOWEST))
			for(Listener w : listeners.get(Priority.LOWEST))
				w.call(player, packet, true);
			if(listeners.containsKey(Priority.LOW))
			for(Listener w : listeners.get(Priority.LOW))
				w.call(player, packet, true);
			if(listeners.containsKey(Priority.NORMAL))
			for(Listener w : listeners.get(Priority.NORMAL))
				w.call(player, packet, true);
			if(listeners.containsKey(Priority.HIGH))
			for(Listener w : listeners.get(Priority.HIGH))
				w.call(player, packet, true);
			if(listeners.containsKey(Priority.HIGHEST))
			for(Listener w : listeners.get(Priority.HIGHEST))
				w.call(player, packet, true);
			if(listeners.containsKey(Priority.MONITOR))
			for(Listener w : listeners.get(Priority.MONITOR))
				w.call(player, packet, true);
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
		for(Priority p : listeners.keySet()) {
			if(listeners.get(p).contains(listener)) {
				is=true;
				break;
			}
		}
		return is;
	}
	
	public static void notify(Listener listener, Priority old, Priority neww) {
		if(listener==null)return;
		if(old!=null)
		if(listeners.containsKey(old)&&listeners.get(old).contains(listener)) {
			List<Listener> edit = listeners.get(old);
			edit.remove(listener);
			listeners.put(old, edit);
		}
		List<Listener> edit = listeners.containsKey(neww)?listeners.get(neww):Lists.newArrayList();
		if(neww!=null) {
		if(edit.contains(listener))return;
		edit.add(listener);
		listeners.put(neww, edit);
		}else{
		if(!edit.contains(listener))return;
		edit.remove(listener);
		listeners.put(neww, edit);
		}
	}
}
