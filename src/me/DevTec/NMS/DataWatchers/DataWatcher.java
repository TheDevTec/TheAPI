package me.DevTec.NMS.DataWatchers;

import me.DevTec.NMS.Reflections;

/**
 * @apiNote This utility is only for 1.9+
 */
public class DataWatcher {
	private Object w;
	public DataWatcher(Object entity) {
		w=Reflections.c(Reflections.getConstructor(Reflections.getNMSClass("DataWatcher"), Reflections.getNMSClass("Entity")),entity);
	}
	
	public Object getDataWatcher() {
		return w;
	}
	
	public Object get(DataWatcherObject dataWatcherObject) {
		return Reflections.invoke(w, Reflections.getMethod(w.getClass(), "get", Reflections.getNMSClass("DataWatcherObject")), dataWatcherObject.get());
	}
	
	public Object get(int id) {
		Object c = Reflections.invoke(w, Reflections.getMethod(w.getClass(), "j", int.class), id);
		return Reflections.invoke(c, Reflections.getMethod(c.getClass(), "b"));
	}
	
	public void set(int id, Object data) {
		Reflections.invoke(w, Reflections.getMethod(w.getClass(), "a", int.class, Object.class), id, data);
	}
	
	public void set(DataWatcherObject dataWatcherObject, Object data) {
		Reflections.invoke(w, Reflections.getMethod(w.getClass(), "a", Reflections.getNMSClass("DataWatcherObject"), Object.class), dataWatcherObject.get(), data);
	}
	
	public void register(DataWatcherObject dataWatcherObject, Object data) {
		Reflections.invoke(w, Reflections.getMethod(w.getClass(), "register", Reflections.getNMSClass("DataWatcherObject"), Object.class), dataWatcherObject.get(), data);
	}
}
