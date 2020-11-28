package me.DevTec.TheAPI.Utils.NMS.DataWatcher;

import me.DevTec.TheAPI.Utils.Reflections.Ref;

/**
 * @apiNote This utility is only for 1.9+
 */
public class DataWatcher {
	private Object w;

	public DataWatcher(Object entity) {
		w = Ref.newInstance(Ref.constructor(Ref.nms("DataWatcher"), Ref.nms("Entity")), entity);
	}

	public Object getDataWatcher() {
		return w;
	}

	public Object get(DataWatcherObject dataWatcherObject) {
		return Ref.invoke(w, Ref.method(w.getClass(), "get", Ref.nms("DataWatcherObject")), dataWatcherObject.get());
	}

	public Object get(int id) {
		Object c = Ref.invoke(w, Ref.method(w.getClass(), "j", int.class), id);
		return Ref.invoke(c, Ref.method(c.getClass(), "b"));
	}

	public void set(int id, Object data) {
		Ref.invoke(w, Ref.method(w.getClass(), "a", int.class, Object.class), id, data);
	}

	public void set(DataWatcherObject dataWatcherObject, Object data) {
		Ref.invoke(w, Ref.method(w.getClass(), "a", Ref.nms("DataWatcherObject"), Object.class),
				dataWatcherObject.get(), data);
	}

	public void register(DataWatcherObject dataWatcherObject, Object data) {
		Ref.invoke(w, Ref.method(w.getClass(), "register", Ref.nms("DataWatcherObject"), Object.class),
				dataWatcherObject.get(), data);
	}
}
