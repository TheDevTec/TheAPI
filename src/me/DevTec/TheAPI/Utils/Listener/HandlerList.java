package me.DevTec.TheAPI.Utils.Listener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import me.DevTec.TheAPI.Utils.TheAPIUtils.Validator;

public class HandlerList {
	public static List<HandlerList> all = new ArrayList<>();
	private List<RegisteredListener> l = new ArrayList<>();
	public HandlerList() {
		all.add(this);
	}
	
	private static Map<Class<? extends Event>, Set<RegisteredListener>> create(Listener listener) {
		Map<Class<? extends Event>, Set<RegisteredListener>> ret = new HashMap<>();
		Method[] Methods = listener.getClass().getDeclaredMethods();
		Set<Method> methods = new HashSet<>(Methods.length, 1);
		methods.addAll(Arrays.asList(Methods));
		for (Method method : methods) {
		    EventHandler eh = method.getAnnotation(EventHandler.class);
		    if (eh == null || method.isBridge() || method.isSynthetic())continue;
		    Class<?> checkClass = method.getParameterTypes()[0];
		    if ((method.getParameterTypes()).length != 1 || !Event.class.isAssignableFrom(checkClass))continue;
		    Class<? extends Event> eventClass = checkClass.asSubclass(Event.class);
		    method.setAccessible(true);
		    Set<RegisteredListener> eventSet = ret.get(eventClass);
		    if (eventSet == null) {
		    	eventSet = new HashSet<>();
		    	ret.put(eventClass, eventSet);
		    }
		    EventExecutor executor = new EventExecutor() {
		    	public void execute(Listener listener, Event event) {
		    		try {
		    			if (!eventClass.isAssignableFrom(event.getClass()))return; 
		    			method.invoke(listener, event);
		    		} catch (Exception ex) {
		    }}};
		    eventSet.add(new RegisteredListener(listener, executor, eh.priority(), eh.ignoreCancelled()));
		}
		return ret;
	}

	public static void register(Listener a) {
		Validator.validate(a==null, "Listener can not be null");
		for (Entry<Class<? extends Event>, Set<RegisteredListener>> entry : create(a).entrySet()) {
			if(Event.getHandlerList(entry.getKey())!=null)
				Event.getHandlerList(entry.getKey()).registerAll(entry.getValue()); 
		}}
	
	public void register(RegisteredListener a) {
		Validator.validate(a==null, "Listener can not be null");
		l.add(a);
	}
	
	public void unregister(RegisteredListener a) {
		Validator.validate(a==null, "Listener can not be null");
		l.remove(a);
	}
	
	public static void unregister(Listener a) {
		Validator.validate(a==null, "Listener can not be null");
		all.forEach(l -> 
		l.l.forEach(s -> {
			if(s.listener.equals(a))
				l.l.remove(s);
		}));
	}

	public void registerAll(Collection<RegisteredListener> value) {
		Validator.validate(value==null, "Collection<Listener> can not be null");
		l.addAll(value);
	}

	public static void callEvent(Event e) {
		Validator.validate(e==null, "Event can not be null");
		all.forEach(l -> call(l.l, e));
	}
	
	private static void call(List<RegisteredListener> l, Event e){
		LinkedHashMap<Integer, HashSet<RegisteredListener>> a = new LinkedHashMap<>();
		for(RegisteredListener s : l) {
			if(a.containsKey(s.priority.ordinal())) {
				HashSet<RegisteredListener> set = a.get(s.priority.ordinal());
				set.add(s);
			}else {
				HashSet<RegisteredListener> set = new HashSet<>();
				set.add(s);
				a.put(s.priority.ordinal(), set);
			}
		} //prepare sorted set
		for(Integer ea : a.keySet())
			a.get(ea).forEach(f -> f.callEvent(e));
		//call event
	}


private static class RegisteredListener {
	private final Listener listener;
	private final EventPriority priority;
	private final EventExecutor executor;
	private final boolean ignoreCancelled;
  
	public RegisteredListener(Listener listener, EventExecutor executor, EventPriority priority, boolean ignoreCancelled) {
		this.listener = listener;
		this.priority = priority;
		this.executor = executor;
		this.ignoreCancelled = ignoreCancelled;
	}
  
	public void callEvent(Event event) {
		if (event instanceof Cancellable && ((Cancellable)event).isCancelled() && ignoreCancelled)return; 
		executor.execute(listener, event);
	}
}

private static interface EventExecutor {
    public void execute(Listener listener, Event event);
}}