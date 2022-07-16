package me.devtec.shared.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import me.devtec.shared.sorting.SortingAPI;
import me.devtec.shared.sorting.SortingAPI.ComparableObject;

public class EventManager {
	static Map<Integer, List<ListenerHolder>> map = new HashMap<>();

	public static ListenerHolder register(EventListener listener) {
		return EventManager.register(0, listener);
	}

	public static ListenerHolder register(int priority, EventListener listener) {
		ListenerHolder e = new ListenerHolder();
		e.listener = listener;
		List<ListenerHolder> list = EventManager.map.get(priority);
		if (list == null)
			EventManager.map.put(priority, list = new ArrayList<>());
		list.add(e);
		return e;
	}

	public static void unregister(ListenerHolder handler) {
		List<Integer> removeId = new ArrayList<>();
		for (Entry<Integer, List<ListenerHolder>> entry : EventManager.map.entrySet())
			if (entry.getValue().contains(handler)) {
				entry.getValue().remove(handler);
				if (entry.getValue().isEmpty())
					removeId.add(entry.getKey());
			}
		for (Integer i : removeId)
			EventManager.map.remove(i);
	}

	public static void call(Event event) {
		for (ComparableObject<Integer, List<ListenerHolder>> cache : SortingAPI.sortByKeyArray(EventManager.map, false))
			for (ListenerHolder handler : cache.getValue())
				for (Class<? extends Event> clazz : handler.listen)
					if (clazz.isAssignableFrom(event.getClass())) {
						try {
							handler.listener.listen(event);
						} catch (Exception error) {
							error.printStackTrace();
						}
						break;
					}
	}
}
