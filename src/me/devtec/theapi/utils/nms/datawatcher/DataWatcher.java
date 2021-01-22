package me.devtec.theapi.utils.nms.datawatcher;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import me.devtec.theapi.utils.reflections.Ref;

public class DataWatcher {
	private static final Constructor<?> c = Ref.constructor(Ref.nms("DataWatcher"), Ref.nms("Entity"));
	private static Method get, getC, set, register;
	static {
		get=Ref.method(Ref.nms("DataWatcher"), "get", Ref.nms("DataWatcherObject"));
		if(get==null) {
			get=Ref.method(Ref.nms("DataWatcher"), "j", int.class);
			getC=Ref.method(Ref.nms("DataWatcher$WatchableObject"), "b");
			set=Ref.method(Ref.nms("DataWatcher"), "a", int.class, Object.class);
		}else {
			set=Ref.method(Ref.nms("DataWatcher"), "a", Ref.nms("DataWatcherObject"), Object.class);
			register=Ref.method(Ref.nms("DataWatcher"), "register", Ref.nms("DataWatcherObject"), Object.class);
		}
	}
	
	private Object w;

	public DataWatcher() {
		this(null);
	}

	public DataWatcher(Object entity) {
		w = Ref.newInstance(c, entity);
	}
	
	public DataWatcher makeNew() {
		return makeNew(null);
	}
	
	public DataWatcher makeNew(Object entity) {
		w = Ref.newInstance(c, entity);
		return this;
	}

	public Object getDataWatcher() {
		return w;
	}

	public Object get(DataWatcherObject dataWatcherObject) {
		return Ref.invoke(w, get, dataWatcherObject.get());
	}

	public Object get(int id) {
		return Ref.invoke(Ref.invoke(w, get, id), getC);
	}

	public DataWatcher set(int id, Object data) {
		Ref.invoke(w, set, id, data);
		return this;
	}

	public DataWatcher set(DataWatcherObject dataWatcherObject, Object data) {
		Ref.invoke(w, set, dataWatcherObject.get(), data);
		return this;
	}

	public DataWatcher register(DataWatcherObject dataWatcherObject, Object data) {
		Ref.invoke(w, register, dataWatcherObject.get(), data);
		return this;
	}
}
