package me.Straiker123.Utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;

import me.Straiker123.TheAPI;

public class Packets {
	
	public static Object getNMSPlayer(Player p) {
		try {
	       return getMethod(getBukkitPlayer(p).getClass(),"getHandle").invoke(p);
		}catch(Exception e) {
			return null;
		}
	}
	
	public static boolean existsClass(String name) {
		try {
			Class.forName(name);
			return true;
		}catch (Exception e) {
			return false;
		}
	}
	public static Constructor<?> getConstructor(Class<?> classz, Class<?> parms) {
		try {
			return classz.getConstructor(parms);
		}catch (Exception e) {
			return null;
		}
	}
	public static Object getObject(Object handle, String name) {
		try {
			return getField(handle.getClass(),"ping").get(handle);
		}catch (Exception e) {
			return null;
		}
	}

	public static int getEntityPlayerInt(Player from, String name) {
		return (int)getObject(getNMSPlayer(from), name);
	}
	public static Object getEntityPlayerBoolean(Player from, String name) {
		return (boolean)getObject(getNMSPlayer(from), name);
	}
	public static String getEntityPlayerString(Player from, String name) {
		return getObject(getNMSPlayer(from), name).toString();
	}
	public static Object getEntityPlayerValue(Player from, String name) {
		return getObject(getNMSPlayer(from), name);
	}
	
	public static Method getMethod(Class<?> classz, String name) {
		try {
			return classz.getMethod(name);
		}catch (Exception e) {
			return null;
		}
	}
	
	public static Method getMethod(Class<?> classz, String name, Class<?>... classes) {
		try {
			return classz.getMethod(name,classes);
		}catch (Exception e) {
			return null;
		}
	}
	
	public static Object getBukkitPlayer(Player p) {
		try {
			return getBukkitClass("entity.CraftPlayer").cast(p);
			}catch(Exception e) {
				return null;
			}
	}
	
	public static Object getValue(Object handle, String fieldName) {
		try {
			return (boolean)handle.getClass().getField(fieldName).get(handle);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static Field getField(Class<?> classz, String name) {
		try {
        Field f = classz.getDeclaredField(name);
        f.setAccessible(true);
        return f;
		}catch(Exception e) {
			try {
		        Field f = classz.getField(name);
		        f.setAccessible(true);
		        return f;
			}catch(Exception er) {
				return null;
			}
		}
	}
	
	public static void setValue(Object handle, String fieldName, Object value) {
		try {
		Field field = handle.getClass().getField(fieldName);
		field.setAccessible(true);
		field.set(handle, value);
		} catch (Exception e) {
		}
	}

	public static Class<?> getBukkitClass(String name) {
	     try {
	         return Class.forName("org.bukkit.craftbukkit." + TheAPI.getServerVersion() + "." + name);
	     } catch (ClassNotFoundException e) {
				TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &4Error when finding class 'org.bukkit.craftbukkit."+TheAPI.getServerVersion() + "." + name+"', server version: "+TheAPI.getServerVersion()));
		         return null;
	     }
	}
	public static Class<?> getNMSClass(String name) {
	     try {
	         return Class.forName("net.minecraft.server." + TheAPI.getServerVersion() + "." + name);
	     } catch (ClassNotFoundException e) {
				TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &4Error when finding class 'net.minecraft.server."+TheAPI.getServerVersion() + "." + name+"', server version: "+TheAPI.getServerVersion()));
		         return null;
	     }
	}
	public static Object getNMSPlayerConnection(Player player) {
	         try {
				return getField(getNMSPlayer(player).getClass(),"playerConnection").get(getNMSPlayer(player));
			} catch (Exception e) {
		         return null;
			}
	}
	public static void sendPacket(Player player, Object packet) {
	     try {
	    	 getMethod(getNMSPlayerConnection(player).getClass(),"sendPacket", new Class[] { getNMSClass("Packet") }).invoke(getNMSPlayerConnection(player), new Object[] { packet });
	     } catch (Exception e) {
				TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &4Error when sending packets to player "+player.getName()+", server version: "+TheAPI.getServerVersion()));
	     }
	}
}
