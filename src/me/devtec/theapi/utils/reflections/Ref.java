package me.devtec.theapi.utils.reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.utils.ChatMessage;
import me.devtec.theapi.utils.nms.NMSAPI;
import me.devtec.theapi.utils.theapiutils.LoaderClass;

public class Ref {
	private static Constructor<?> blockpos = constructor(nmsOrOld("core.BlockPosition", "BlockPosition"), double.class, double.class,
			double.class),
			c = Ref.constructor(
					Ref.getClass("com.mojang.authlib.GameProfile") != null
							? Ref.getClass("com.mojang.authlib.GameProfile")
							: Ref.getClass("net.minecraft.util.com.mojang.authlib.GameProfile"),
					UUID.class, String.class),
			d = Ref.constructor(
					Ref.getClass("com.mojang.authlib.properties.Property") != null
							? Ref.getClass("com.mojang.authlib.properties.Property")
							: Ref.getClass("net.minecraft.util.com.mojang.authlib.properties.Property"),
					String.class, String.class, String.class);
	private static Object server = NMSAPI.getServer();
	private static Class<?> craft = craft("entity.CraftPlayer"), world = craft("CraftWorld"), playerInfoData = Ref.nmsOrOld("network.protocol.game.PacketPlayOutPlayerInfo$PlayerInfoData","PacketPlayOutPlayerInfo$PlayerInfoData");
	private static Method ichatcon;
	private static Constructor<?> playerInfo;
	static {
		try {
		playerInfo = Ref.getConstructors(playerInfoData)[0].getParameterTypes()[0].getName()
				.contains("Packet")?Ref.getConstructors(playerInfoData)[0]:null;
		}catch(Exception err) {}
		ichatcon = method(nmsOrOld("network.chat.IChatBaseComponent", "IChatBaseComponent$ChatSerializer"), "a", String.class);
		if (ichatcon == null)
			ichatcon = method(nmsOrOld(null, "ChatSerializer"), "a", String.class);
	}

	public static Object createGameProfile(UUID id, String name) {
		if (id == null)
			id = UUID.randomUUID();
		return Ref.newInstance(c, id, name);
	}

	public static Object createPlayerInfoData(Object packet, Object profile, int ping, String gamemode, String playerName) {
		if (playerInfo!=null)
			return Ref.newInstance(playerInfo, packet,
					profile, ping, Ref.get(null, Ref.field(Ref.nmsOrOld("world.level.EnumGamemode","EnumGamemode"), gamemode.toUpperCase())),
					((Object[]) Ref.invokeNulled(
							Ref.method(Ref.craft("util.CraftChatMessage"), "fromString", String.class),
							playerName))[0]);
		return Ref.newInstance(playerInfo, profile, ping,
				Ref.get(null, Ref.field(Ref.nmsOrOld("world.level.EnumGamemode","EnumGamemode"), gamemode.toUpperCase())),
				((Object[]) Ref.invokeNulled(Ref.method(Ref.craft("util.CraftChatMessage"), "fromString", String.class),
						playerName))[0]);
	}

	public static Object createProperty(String key, String texture, String signature) {
		if (key == null || texture == null)
			return null;
		return Ref.newInstance(d, key, texture, signature);
	}

	public static Object createProperty(String key, String texture) {
		if (key == null || texture == null)
			return null;
		return Ref.newInstance(d, key, texture, null);
	}

	public static void set(Object main, Field f, Object o) {
		try {
			f.setAccessible(true);
			f.set(main, o);
		} catch (Exception e) {
		}
	}

	public static void set(Object main, String field, Object o) {
		try {
			Field f = field(main.getClass(), field);
			f.setAccessible(true);
			f.set(main, o);
		} catch (Exception e) {
		}
	}

	public static Class<?> getClass(String name) {
		try {
			return Class.forName(name);
		} catch (Exception e) {
			return null;
		}
	}

	public static boolean existsMethod(Class<?> c, String name) {
		boolean a = false;
		for (Method d : getDeclaredMethods(c))
			if (d.getName().equals(name)) {
				a = true;
				break;
			}
		return a;
	}

	public static Object blockPos(double x, double y, double z) {
		return newInstance(blockpos, x, y, z);
	}

	public static Object IChatBaseComponent(String text) {
		return new ChatMessage(text).toNMS();
	}

	public static Object IChatBaseComponentJson(String text) {
		return invokeNulled(ichatcon, text);
	}

	public static void sendPacket(Player to, Object packet) {
		if(packet==null)Bukkit.getLogger().severe("Tryting to send to player null packet!");
		if(packet!=null && to!=null)
			LoaderClass.plugin.handler.send(to, packet);
	}

	public static void sendPacket(Collection<? extends Player> toPlayers, Object packet) {
		if(packet!=null)
		for(Player to : toPlayers) {
			if(to!=null)
				LoaderClass.plugin.handler.send(to, packet);
		}
	}

	public static void sendPacket(Player[] toPlayers, Object packet) {
		if(packet!=null)
		for(Player to : toPlayers) {
			if(to!=null)
				LoaderClass.plugin.handler.send(to, packet);
		}
	}

	public static Object server() {
		return server;
	}

	public static Object player(Player a) {
		return handle(cast(craft, a));
	}

	public static Object playerCon(Player a) {
		return get(player(a), TheAPI.isNewerThan(16)?"b":"playerConnection");
	}

	public static Object network(Object playercon) {
		return get(playercon, TheAPI.isNewerThan(16)?"a":"networkManager");
	}

	public static Object channel(Object network) {
		return get(network, TheAPI.isNewerThan(16)?"k":"channel");
	}

	public static Object world(World a) {
		return handle(cast(world, a));
	}

	public static Object cast(Class<?> c, Object item) {
		try {
			return c.cast(item);
		} catch (Exception e) {
			return null;
		}
	}

	public static Class<?> nms(String name) {
		try {
			return Class.forName("net.minecraft.server." + version() + "." + name);
		} catch (Exception e) {
			return null;
		}
	}

	public static Class<?> nmsOrOld(String name, String old) {
		try {
			if(name!=null)
				return Class.forName("net.minecraft."+name);
			return Class.forName("net.minecraft.server." + version() + "." + old);
		} catch (Exception e) {
			try {
				return Class.forName("net.minecraft.server." + version() + "." + old);
			} catch (Exception e1) {
				return null;
			}
		}
	}

	public static Class<?> craft(String name) {
		try {
			return Class.forName("org.bukkit.craftbukkit." + version() + "." + name);
		} catch (Exception e) {
			return null;
		}
	}

	public static Constructor<?> constructor(Class<?> main, Class<?>... bricks) {
		try {
			return main.getDeclaredConstructor(bricks);
		} catch (Exception es) {
			return null;
		}
	}

	public static Class<?>[] getClasses(Class<?> main) {
		try {
			return main.getClasses();
		} catch (Exception es) {
			return new Class<?>[0];
		}
	}

	public static Class<?>[] getDeclaredClasses(Class<?> main) {
		try {
			return main.getDeclaredClasses();
		} catch (Exception es) {
			return new Class<?>[0];
		}
	}

	public static Field[] getFields(Class<?> main) {
		try {
			return main.getFields();
		} catch (Exception es) {
			return new Field[0];
		}
	}

	public static List<Field> getAllFields(Class<?> main) {
		List<Field> f = new ArrayList<>();
		Class<?> superclass = main;
		while (superclass != null) {
			f.addAll(Arrays.asList(getDeclaredFields(superclass)));
			superclass = superclass.getSuperclass();
		}
		return f;
	}

	public static Field[] getDeclaredFields(Class<?> main) {
		try {
			return main.getDeclaredFields();
		} catch (Exception es) {
			return new Field[0];
		}
	}

	public static Method[] getMethods(Class<?> main) {
		try {
			return main.getMethods();
		} catch (Exception es) {
			return new Method[0];
		}
	}

	public static Method[] getDeclaredMethods(Class<?> main) {
		try {
			return main.getDeclaredMethods();
		} catch (Exception es) {
			return null;
		}
	}

	public static Constructor<?>[] getConstructors(Class<?> main) {
		try {
			return main.getConstructors();
		} catch (Exception es) {
			return null;
		}
	}

	public static Constructor<?>[] getDeclaredConstructors(Class<?> main) {
		try {
			return main.getDeclaredConstructors();
		} catch (Exception es) {
			return null;
		}
	}

	public static Method method(Class<?> main, String name, Class<?>... bricks) {
		try {
			Method a = main.getDeclaredMethod(name, bricks);
			Class<?> d = main;
			while (d != null && a == null) {
				for (Method m : getDeclaredMethods(d)) {
					if (m.getName().equals(name) && areSame(m.getParameterTypes(), bricks)) {
						a = m;
						break;
					}
				}
				d = d.getSuperclass();
			}
			if (a != null)
				a.setAccessible(true);
			return a;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static Field field(Class<?> main, String name) {
		try {
			Field f = main.getDeclaredField(name);
			f.setAccessible(true);
			return f;
		} catch (Exception e) {
			try {
				Field f = null;
				Class<?> c = main.getSuperclass();
				while (c != null) {
					try {
						f = c.getDeclaredField(name);
					} catch (Exception err) {
					}
					if (f != null)
						break;
					try {
						c = c.getSuperclass();
					} catch (Exception err) {
						break;
					}
				}
				if (f != null) {
					f.setAccessible(true);
				}
				return f;
			} catch (Exception er) {
			}
			return null;
		}
	}

	public static Object get(Object main, Field field) {
		try {
			field.setAccessible(true);
			return field.get(main);
		} catch (Exception es) {
			return null;
		}
	}

	public static Object getNulled(Field field) {
		try {
			field.setAccessible(true);
			return field.get(null);
		} catch (Exception es) {
			return null;
		}
	}

	public static Object getNulled(Class<?> clas, String field) {
		try {
			return field(clas, field).get(null);
		} catch (Exception es) {
			return null;
		}
	}

	public static Object getStatic(Field field) {
		return getNulled(field);
	}

	public static Object getStatic(Class<?> clas, String field) {
		return getNulled(clas,field);
	}

	public static Object get(Object main, String field) {
		return get(main, field(main.getClass(), field));
	}

	public static Object handle(Object item) {
		try {
			return invoke(item, method(item.getClass(), "getHandle"));
		} catch (Exception e) {
			return null;
		}
	}

	public static Object invoke(Object main, Method method, Object... bricks) {
		try {
			method.setAccessible(true);
			return method.invoke(main, bricks);
		} catch (Exception | NoSuchMethodError es) {
			return null;
		}
	}

	public static Object invoke(Object main, String method, Object... bricks) {
		try {
			return findMethod(main.getClass(), method, bricks).invoke(main, bricks);
		} catch (Exception es) {
			return null;
		}
	}

	public static Object invokeNulled(Class<?> classInMethod, String method, Object... bricks) {
		try {
			return findMethod(classInMethod, method, bricks).invoke(null, bricks);
		} catch (Exception es) {
			return null;
		}
	}

	public static Object invokeNulled(Method method, Object... bricks) {
		try {
			return method.invoke(null, bricks);
		} catch (Exception es) {
			return null;
		}
	}

	public static Object invokeStatic(Class<?> classInMethod, String method, Object... bricks) {
		return invokeNulled(classInMethod, method, bricks);
	}

	public static Object invokeStatic(Method method, Object... bricks) {
		return invokeNulled(method, bricks);
	}

	public static Method findMethod(Object c, String name, Object... bricks) {
		return findMethod(c.getClass(), name, bricks);
	}

	public static Method findMethodByName(Class<?> c, String name) {
		Method a = null;
		Class<?> d = c;
		while (d != null) {
			for (Method m : getDeclaredMethods(d)) {
				if (m.getName().equals(name)) {
					a = m;
					break;
				}
			}
			if (a != null)
				break;
			try {
				d = d.getSuperclass();
			} catch (Exception err) {
				break;
			}
		}
		if (a != null)
			a.setAccessible(true);
		return a;
	}

	public static Method findMethod(Class<?> c, String name, Object... bricks) {
		Method a = null;
		Class<?> d = c;
		Class<?>[] param = new Class<?>[bricks.length];
		int i = 0;
		for (Object o : bricks) {
			if (o != null)
				param[i++] = o instanceof Class ? (Class<?>) o : o.getClass();
		}
		while (d != null) {
			for (Method m : getDeclaredMethods(d)) {
				if (m.getName().equals(name) && areSame(m.getParameterTypes(), param)) {
					a = m;
					break;
				}
			}
			if (a != null)
				break;
			try {
				d = d.getSuperclass();
			} catch (Exception err) {
				break;
			}
		}
		if (a != null)
			a.setAccessible(true);
		return a;
	}
	
	public static Constructor<?> findConstructor(Class<?> c, Object... bricks) {
		Constructor<?> a = null;
		Class<?>[] param = new Class<?>[bricks.length];
		int i = 0;
		for (Object o : bricks) {
			if (o != null)
				param[i++] = o instanceof Class ? (Class<?>) o : o.getClass();
		}
		for (Constructor<?> m : getDeclaredConstructors(c)) {
			if (areSame(m.getParameterTypes(), param)) {
				a = m;
				break;
			}
		}
		if (a != null)
			a.setAccessible(true);
		return a;
	}

	private static boolean areSame(Class<?>[] a, Class<?>[] b) {
		return Arrays.asList(a).containsAll(Arrays.asList(b));
	}

	public static Object newInstance(Constructor<?> constructor, Object... bricks) {
		try {
			constructor.setAccessible(true);
			return constructor.newInstance(bricks);
		} catch (Exception es) {
			return null;
		}
	}
	
	public static Object newInstanceNms(String className, Object... bricks) {
		return newInstance(findConstructor(nms(className), bricks), bricks);
	}

	public static Object newInstanceCraft(String className, Object... bricks) {
		return newInstance(findConstructor(craft(className), bricks), bricks);
	}

	public static Object newInstanceByClass(String className, Object... bricks) {
		return newInstance(findConstructor(getClass(className), bricks), bricks);
	}

	public static Object newInstanceByClass(Class<?> clazz, Object... bricks) {
		return newInstance(findConstructor(clazz, bricks), bricks);
	}

	public static String version() {
		return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
	}
}