package me.DevTec.Other;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import io.netty.channel.Channel;

public class Ref {
	public static void set(Object main, Field field, Object o){
		try {
			field.setAccessible(true);
			field.set(main, o);
		}catch(Exception e) {
		}
	}

	public static void set(Object main, String field, Object o){
		try {
			Field f = field(main.getClass(), field);
			f.setAccessible(true);
			f.set(main, o);
		}catch(Exception e) {
		}
	}
	
	public static Class<?> getClass(String name){
		try {
			return Class.forName(name);
		}catch(Exception e) {
			return null;
		}
	}
	
	public static boolean existsMethod(Class<?> c, String name){
		boolean a = false;
		for(Method d : getMethods(c))
			if(d.getName().equals(name)) {
				a=true;
				break;
			}
		return a;
	}
	
	public static Object blockPos(double x, double y, double z) {
		return newInstance(constructor(nms("BlockPosition"), double.class, double.class, double.class), x,y,z);
	}
	
	private static Method ichatcon;
	static {
		try {
		if(nms("IChatBaseComponent")!=null)
		ichatcon = method(getDeclaredClasses(nms("IChatBaseComponent"))[0],"a", String.class);
		if(ichatcon==null)
			ichatcon = method(nms("ChatSerializer"), "a", String.class);
		}catch(Exception err) {
			ichatcon = method(nms("ChatSerializer"), "a", String.class);
		}
	}
	
	public static Object IChatBaseComponent(String text) {
		return invoke(null, ichatcon, "{\"text\":\"" + text + "\"}");
	}
	
	public static void sendPacket(Player to, Object packet) {
		Object p = Ref.player(to);
		Object con = Ref.get(p, Ref.field(p.getClass(), "playerConnection"));
		Ref.invoke(con, Ref.method(con.getClass(), "sendPacket", Ref.nms("Packet")), packet);
		
	}
	
	public static Object server() {
		return invoke(handle(cast(craft("CraftServer"),Bukkit.getServer())),"getServer");
	}
	
	public static Object player(Player a) {
		return handle(cast(craft("entity.CraftPlayer"), a));
	}
	
	public static Object playerCon(Player a) {
		return get(player(a), "playerConnection");
	}
	
	public static Object network(Object playercon) {
		return get(playercon, "networkManager");
	}
	
	public static Channel channel(Object network) {
		return (Channel)get(network, "channel");
	}
	
	public static Object world(World a) {
		return handle(cast(craft("CraftWorld"), a));
	}

	public static Object cast(Class<?> c, Object item){
		try {
			return c.cast(item);
		}catch(Exception e) {
			return null;
		}
	}
	
	public static Class<?> nms(String name){
		try {
			return Class.forName("net.minecraft.server."+version()+"."+name);
		}catch(Exception e) {
			return null;
		}
	}
	
	public static Class<?> craft(String name){
		try {
			return Class.forName("org.bukkit.craftbukkit."+version()+"."+name);
		}catch(Exception e) {
			return null;
		}
	}
	
	public static Constructor<?> constructor(Class<?> main, Class<?>... bricks){
		try {
			return main.getConstructor(bricks);
		}catch(Exception es) {
			try {
				return main.getDeclaredConstructor(bricks);
			}catch(Exception e) {
				return null;
			}
		}
	}
	
	public static Class<?>[] getClasses(Class<?> main){
		try {
			return main.getClasses();
		}catch(Exception es) {
			return new Class<?>[0];
		}
	}
	
	public static Class<?>[] getDeclaredClasses(Class<?> main){
		try {
			return main.getDeclaredClasses();
		}catch(Exception es) {
			return new Class<?>[0];
		}
	}
	
	public static Field[] getFields(Class<?> main){
		try {
			return main.getFields();
		}catch(Exception es) {
			return null;
		}
	}
	
	public static Field[] getDeclaredFields(Class<?> main){
		try {
			return main.getDeclaredFields();
		}catch(Exception es) {
			return new Field[0];
		}
	}
	
	public static Method[] getMethods(Class<?> main){
		try {
			return main.getMethods();
		}catch(Exception es) {
			return new Method[0];
		}
	}
	
	public static Method[] getDeclaredMethods(Class<?> main){
		try {
			return main.getDeclaredMethods();
		}catch(Exception es) {
			return null;
		}
	}
	
	public static Constructor<?>[] getConstructors(Class<?> main){
		try {
			return main.getConstructors();
		}catch(Exception es) {
			return null;
		}
	}
	
	public static Constructor<?>[] getDeclaredConstructors(Class<?> main){
		try {
			return main.getDeclaredConstructors();
		}catch(Exception es) {
			return null;
		}
	}
	
	public static Method method(Class<?> main, String name, Class<?>... bricks){
		try {
			Method f=bricks.length==1?main.getMethod(name):main.getMethod(name, bricks);
			f.setAccessible(true);
			return f;
		}catch(Exception es) {
			try {
				Method f=bricks.length==1?main.getDeclaredMethod(name):main.getDeclaredMethod(name, bricks);
				f.setAccessible(true);
				return f;
			}catch(Exception e) {
				if (main.getSuperclass() != null)
					return method(main.getSuperclass(), name, bricks);
				return null;
			}
		}
	}
	
	public static Field field(Class<?> main, String name){
		try {
			Field f=main.getField(name);
			if(f!=null)
			f.setAccessible(true);
			return f;
		}catch(Exception es) {
			try {
				Field f=main.getDeclaredField(name);
				f.setAccessible(true);
				return f;
			}catch(Exception e) {
				if (main.getSuperclass() != null)
					return field(main.getSuperclass(), name);
				return null;
			}
		}
	}
	
	public static Object get(Object main, Field field){
		try {
			if(!field.isAccessible())
			field.setAccessible(true);
			return field.get(main);
		}catch(Exception es) {
			return null;
		}
	}
	
	public static Object get(Object main, String field){
		try {
			return field(main.getClass(),field).get(main);
		}catch(Exception es) {
			return null;
		}
	}
	
	public static Object handle(Object item){
		try {
			return invoke(item,method(item.getClass(),"getHandle"));
		}catch(Exception e) {
			return null;
		}
	}
	
	public static Object invoke(Object main, Method method, Object... bricks){
		try {
			if(!method.isAccessible())
			method.setAccessible(true);
			return method.invoke(main, bricks);
		}catch(Exception es) {
			return null;
		}
	}

	public static Object invoke(Object main, String method, Object... bricks){
		try {
			return findMethod(main.getClass(), method, bricks).invoke(main, bricks);
		}catch(Exception es) {
			return null;
		}
	}
	public static Object invokeNulled(Class<?> classInMethod, String method, Object... bricks){
		try {
			return findMethod(classInMethod, method, bricks).invoke(null, bricks);
		}catch(Exception es) {
			return null;
		}
	}

	public static Method findMethod(Object c, String name, Object... bricks) {
		return findMethod(c.getClass(), name, bricks);
	}

	public static Method findMethodByName(Class<?> c, String name) {
		Method a = null;
		for(Method m : getMethods(c)) {
			if(m.getName().equals(name)) {
				a=m;
				break;
			}
		}
		if(a==null)
		for(Method m : getDeclaredMethods(c)) {
			if(m.getName().equals(name)) {
				a=m;
				break;
			}
		}
		if(a!=null)
		a.setAccessible(true);
		return a;
	}
	
	public static Method findMethod(Class<?> c, String name, Object... bricks) {
		Method a = null;
		Class<?>[] param = new Class<?>[bricks.length];
		int i = 0;
		for(Object o : bricks) {
			if(o!=null)
			param[i]=o instanceof Class ? (Class<?>)o : o.getClass();
			++i;
		}
		if(bricks.length==0) {
			for(Method m : getMethods(c)) {
				if(m.getParameterCount()==0 && m.getName().equals(name)) {
					a=m;
					break;
				}
			}
			if(a==null)
			for(Method m : getDeclaredMethods(c)) {
				if(m.getParameterCount()==0 && m.getName().equals(name)) {
					a=m;
					break;
				}
			}
		}else {
		for(Method m : getMethods(c)) {
			if(m.getParameterTypes().equals(param) && m.getName().equals(name)) {
				a=m;
				break;
			}
		}
		if(a==null)
		for(Method m : getDeclaredMethods(c)) {
			if(m.getParameterTypes().equals(param) && m.getName().equals(name)) {
				a=m;
				break;
			}
		}
		}
		if(a!=null)
		a.setAccessible(true);
		return a;
	}

	public static Constructor<?> findConstructor(Class<?> c, Object... bricks) {
		Constructor<?> a = null;
		Class<?>[] param = new Class<?>[bricks.length];
		int i = 0;
		for(Object o : bricks) {
			if(o!=null)
			param[i]=o instanceof Class ? (Class<?>)o : o.getClass();
			++i;
		}
		if(bricks.length==0) {
			for(Constructor<?> m : getConstructors(c)) {
				if(m.getParameterCount()==0) {
					a=m;
					break;
				}
			}
			if(a==null)
			for(Constructor<?> m : getDeclaredConstructors(c)) {
				if(m.getParameterCount()==0) {
					a=m;
					break;
				}
			}
		}else {
			for(Constructor<?> m : getConstructors(c)) {
				if(m.getParameterTypes().equals(param)) {
					a=m;
					break;
				}
			}
			if(a==null)
			for(Constructor<?> m : getDeclaredConstructors(c)) {
				if(m.getParameterTypes().equals(param)) {
					a=m;
					break;
				}
			}}
		if(a!=null)
		a.setAccessible(true);
		return a;
	}
	
	public static Object create(Constructor<?> constructor, Object... bricks){
		return newInstance(constructor, bricks);
	}
	
	public static Object newInstance(Constructor<?> constructor, Object... bricks){
		try {
			constructor.setAccessible(true);
			return constructor.newInstance(bricks);
		}catch(Exception es) {
			return null;
		}
	}
	
	public static Object createNms(String className, Object... bricks){
		return newInstanceNms(className, bricks);
	}
	
	public static Object newInstanceNms(String className, Object... bricks){
		return newInstance(findConstructor(nms(className), bricks), bricks);
	}

	public static Object createCraft(String className, Object... bricks){
		return newInstanceCraft(className, bricks);
	}
	
	public static Object newInstanceCraft(String className, Object... bricks){
		return newInstance(findConstructor(craft(className), bricks), bricks);
	}
	public static Object createByClass(String className, Object... bricks){
		return newInstanceByClass(className, bricks);
	}
	
	public static Object newInstanceByClass(String className, Object... bricks){
		return newInstance(findConstructor(getClass(className), bricks), bricks);
	}
	
	public static String version() {
		return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
	}
}