package me.DevTec.NMS;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class PacketListener {
	private static final HashMap<ListenerPriorite, List<Listener>> map = Maps.newHashMap();
	
	public static Object call(Player player, Object packet, boolean receice) {
		for(ListenerPriorite a : Arrays.asList(ListenerPriorite.LOWEST,ListenerPriorite.LOW,ListenerPriorite.NORMAL,ListenerPriorite.HIGH,ListenerPriorite.HIGHEST,ListenerPriorite.MONITOR)) {
			if(!map.containsKey(a))continue;
			for(Listener listening : map.get(a)) {
				packet=listening.call(player, packet, receice);
			}
		}
		return packet;
	}
	
	public static boolean register(Listener listener, ListenerPriorite priority) {
		if(listener==null||priority==null||getPriority(listener)!=null)return false;
		List<Listener> list = map.containsKey(priority)?map.get(priority):null;
		if(list==null)list=Lists.newArrayList();
		list.add(listener);
		map.put(priority, list);
		return true;
	}
	
	public static boolean register(Listener listener) {
		return register(listener, ListenerPriorite.NORMAL);
	}

	public static boolean unregister(Listener listener) {
		ListenerPriorite a = getPriority(listener);
		if(listener==null||a==null)return false;
		List<Listener> list = map.get(a);
		list.remove(listener);
		if(list.isEmpty())
			map.remove(a);
		else
		map.put(a, list);
		return true;
	}
	
	public static boolean setPriority(Listener listener, ListenerPriorite priority) {
		ListenerPriorite a = getPriority(listener);
		if(listener==null||a==null)return false;
		List<Listener> list = map.get(a);
		list.remove(listener);
		map.put(a, list);

	    list = map.containsKey(priority)?map.get(priority):null;
		if(list==null)list=Lists.newArrayList();
		list.add(listener);
		map.put(priority, list);
		return true;
	}
	
	public static ListenerPriorite getPriority(Listener listener) {
		ListenerPriorite a = null;
		for(ListenerPriorite key : map.keySet()) {
			if(map.get(key).contains(listener)) {
				a=key;
				break;
			}
		}
		return a;
	}
}
