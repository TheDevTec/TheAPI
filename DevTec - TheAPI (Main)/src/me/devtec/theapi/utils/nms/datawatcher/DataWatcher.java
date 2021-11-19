package me.devtec.theapi.utils.nms.datawatcher;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import me.devtec.theapi.utils.reflections.Ref;

public class DataWatcher {
	private static final Constructor<?> c = Ref.constructor(Ref.nmsOrOld("network.syncher.DataWatcher","DataWatcher"), Ref.nmsOrOld("world.entity.Entity","Entity"));
	private static Method get;
    private static Method getC;
    private static final Method set;
    private static Method register;
	static {
		get=Ref.method(Ref.nmsOrOld("network.syncher.DataWatcher","DataWatcher"), "get", Ref.nmsOrOld("network.syncher.DataWatcherObject","DataWatcherObject"));
		if(get==null) {
			get=Ref.method(Ref.nmsOrOld("network.syncher.DataWatcher","DataWatcher"), "j", int.class);
			getC=Ref.method(Ref.nmsOrOld("network.syncher.DataWatcher$WatchableObject","DataWatcher$WatchableObject"), "b");
			set=Ref.method(Ref.nmsOrOld("network.syncher.DataWatcher","DataWatcher"), "a", int.class, Object.class);
		}else {
			set=Ref.method(Ref.nmsOrOld("network.syncher.DataWatcher","DataWatcher"), "a", Ref.nmsOrOld("network.syncher.DataWatcherObject","DataWatcherObject"), Object.class);
			register=Ref.method(Ref.nmsOrOld("network.syncher.DataWatcher","DataWatcher"), "register", Ref.nmsOrOld("network.syncher.DataWatcherObject","DataWatcherObject"), Object.class);
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
