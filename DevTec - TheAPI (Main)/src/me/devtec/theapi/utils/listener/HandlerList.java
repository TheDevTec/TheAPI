package me.devtec.theapi.utils.listener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import me.devtec.theapi.utils.theapiutils.Validator;

public class HandlerList {
	protected static final Map<String, HandlerList> all = new HashMap<>();
	protected final Map<Integer, List<RegisteredListener>> reg = new HashMap<>();

	protected static HandlerList getOrCreate(String event) {
		if(all.containsKey(event))return all.get(event);
		HandlerList handler = new HandlerList();
		all.put(event, handler);
		return handler;
	}
	
	private HandlerList() {
		for(int i = 0; i < 6; ++i)
			reg.put(i, new ArrayList<>());
	}

	private static Map<Class<? extends Event>, Collection<RegisteredListener>> create(Listener listener) {
		Map<Class<? extends Event>, Collection<RegisteredListener>> ret = new HashMap<>();
		for (Method method : listener.getClass().getDeclaredMethods()) {
			EventHandler eh = method.getAnnotation(EventHandler.class);
			if (eh == null || method.isBridge() || method.isSynthetic())
				continue;
			Class<?> checkClass = method.getParameterTypes()[0];
			if ((method.getParameterTypes()).length != 1 || !Event.class.isAssignableFrom(checkClass))
				continue;
			Class<? extends Event> eventClass = checkClass.asSubclass(Event.class);
			method.setAccessible(true);
			Collection<RegisteredListener> eventSet = ret.get(eventClass);
			if (eventSet == null)
				ret.put(eventClass, eventSet = new ArrayList<>());
			eventSet.add(new RegisteredListener(listener, method, eh.priority(), eh.ignoreCancelled()));
		}
		return ret;
	}

	public static void register(Listener a) {
		Validator.validate(a == null, "Listener cannot be null");
		for (Entry<Class<? extends Event>, Collection<RegisteredListener>> entry : create(a).entrySet())
			getOrCreate(entry.getKey().getCanonicalName()).registerAll(entry.getValue());
	}

	public void register(RegisteredListener a) {
		Validator.validate(a == null, "Listener cannot be null");
		reg.get(a.priority.ordinal()).add(a);
	}

	public void unregister(RegisteredListener a) {
		Validator.validate(a == null, "Listener cannot be null");
		reg.get(a.priority.ordinal()).remove(a);
	}

	public static void unregister(Listener a) {
		Validator.validate(a == null, "Listener cannot be null");
		all.values().forEach(l -> {
			Map<Integer,RegisteredListener> w = new HashMap<>();
			l.reg.forEach((d, s) -> {
				s.forEach(f -> {
				if (f.listener.equals(a))
					w.put(f.priority.ordinal(), f);
				});
			});
			for(Entry<Integer,RegisteredListener> aw : w.entrySet())
				l.reg.get(aw.getKey()).remove(aw.getValue());
		});
	}

	public void registerAll(Collection<RegisteredListener> value) {
		Validator.validate(value == null, "Collection<RegisteredListener> cannot be null");
		for(RegisteredListener r : value)
			reg.get(r.priority.ordinal()).add(r);
	}

	public static void callEvent(Event e) {
		Validator.validate(e == null, "Event cannot be null");
		call(getOrCreate(e.getClass().getCanonicalName()).reg, e);
	}

	private static void call(Map<Integer, List<RegisteredListener>> reg2, Event e) {
		for (List<RegisteredListener> er : reg2.values())
			er.forEach(f -> f.callEvent(e));
		// call event
	}
	
	public static boolean isListening(Event e) {
		Map<Integer, List<RegisteredListener>> reg2 = getOrCreate(e.getClass().getCanonicalName()).reg;
		for (int i = 0; i < 6; ++i) {
			if(!reg2.get(i).isEmpty())return true;
		}
		return false;
	}

	private static class RegisteredListener {
		private final Listener listener;
		private final EventPriority priority;
		private final Method executor;
		private final boolean ignoreCancelled;

		public RegisteredListener(Listener listener, Method executor, EventPriority priority, boolean ignoreCancelled) {
			this.listener = listener;
			this.priority = priority;
			this.executor = executor;
			this.ignoreCancelled = ignoreCancelled;
		}

		public void callEvent(Event event) {
			if (event instanceof Cancellable && ((Cancellable) event).isCancelled() && ignoreCancelled)
				return;
			try {
				executor.invoke(listener, event);
			} catch (Exception ex) {
			}
		}
	}
}