package me.DevTec.TheAPI.Utils.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import me.DevTec.TheAPI.TheAPI;

public class Reflections {

	public static Object cast(Class<?> object, Object toCast) {
		try {
			return object.cast(toCast);
		} catch (Exception e) {
			return null;
		}
	}

	public static Method getMethod(Class<?> main, String name, Class<?>... bricks) {
		try {
			return main.getMethod(name, bricks);
		} catch (Exception e) {
			try {
				return main.getDeclaredMethod(name, bricks);
			} catch (Exception e1) {
				return null;
			}
		}
	}

	public static Method getMethod(Class<?> main, String name) {
		try {
			return main.getMethod(name);
		} catch (Exception e) {
			try {
				return main.getDeclaredMethod(name);
			} catch (Exception e1) {
				return null;
			}
		}
	}

	public static Object c(Constructor<?> c, Object... items) {
		try {
			return c.newInstance(items);
		} catch (Exception e) {
			return null;
		}
	}

	public static Constructor<?> getConstructor(Class<?> main, Class<?>... bricks) {
		try {
			return main.getConstructor(bricks);
		} catch (Exception e) {
			try {
				return main.getDeclaredConstructor(bricks);
			} catch (Exception e2) {
				return null;
			}
		}
	}

	public static void setField(Object main, Field field, Object value) {
		try {
			field.set(main, value);
		} catch (Exception e) {
		}
	}

	public static void setFieldWithNull(Field field, Object value) {
		try {
			field.set(null, value);
		} catch (Exception e) {
		}
	}

	public static Field getField(Class<?> main, String name) {
		try {
			Field f = main.getField(name);
			f.setAccessible(true);
			return f;
		} catch (Exception e) {
			try {
				Field f = main.getDeclaredField(name);
				f.setAccessible(true);
				return f;
			} catch (Exception e1) {
				return null;
			}
		}
	}

	public static Object get(Field field, Object item) {
		try {
			return field.get(item);
		} catch (Exception e) {
			return null;
		}
	}

	public static Class<?> getNMSClass(String name) {
		return getClass("net.minecraft.server." + TheAPI.getServerVersion() + "." + name);
	}

	public static Class<?> getBukkitClass(String name) {
		return getClass("org.bukkit.craftbukkit." + TheAPI.getServerVersion() + "." + name);
	}

	public static Class<?> getClass(String name) {
		if (existsClass(name))
			try {
				return Class.forName(name);
			} catch (ClassNotFoundException e) {
			}
		return null;
	}

	public static boolean existNMSClass(String name) {
		try {
			Class.forName("net.minecraft.server." + TheAPI.getServerVersion() + "." + name);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	public static boolean existBukkitClass(String name) {
		try {
			Class.forName("org.bukkit.craftbukkit." + TheAPI.getServerVersion() + "." + name);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	public static boolean existsClass(String name) {
		try {
			Class.forName(name);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	public static Object invoke(Object object, Method method, Object... items) {
		try {
			return method.invoke(object, items);
		} catch (Exception error) {
			return null;
		}
	}

	public static Constructor<?>[] getConstructors(Class<?> nmsClass) {
		try {
			return nmsClass.getConstructors();
		} catch (Exception error) {
			return null;
		}
	}

	public static void setField(Object packet, String field, Object value) {
		try {
			getField(packet.getClass(), field).set(packet, value);
		} catch (Exception e) {
		}
	}
}
