package me.devtec.theapi.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

public class TheCoder {

	/**
	 * @apiNote Create Location from String
	 * @return Location
	 */
	public static Location locationFromString(String savedLocation) {
		if (savedLocation == null)
			return null;
		try {
			String[] s = savedLocation.replace("_", ".").split(",");
			return new Location(Bukkit.getWorld(s[0].replace(":", "_")), StringUtils.getDouble(s[1]),
					StringUtils.getDouble(s[2]), StringUtils.getDouble(s[3]), StringUtils.getFloat(s[4]),
					StringUtils.getFloat(s[5]));
		} catch (Exception er) {
			return null;
		}

	}

	/**
	 * @apiNote Convert Location to String
	 * @return String
	 */
	public static String locationToString(Location loc) {
		if (loc == null || loc.getWorld() == null)
			return null;
		return (loc.getWorld().getName().replace("_", ":") + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ()
				+ "," + loc.getYaw() + "," + loc.getPitch()).replace(".", "_");
	}

	/**
	 * @apiNote Convert Objects to String
	 * @return String
	 */
	public static String toString(Object... objects) {
		String r = null;
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
			for (Object o : objects)
				dataOutput.writeObject(o);
			dataOutput.close();
			r = Base64.getEncoder().encodeToString(outputStream.toByteArray());
		} catch (Exception e) {
		}
		return r;
	}

	/**
	 * @apiNote Convert Object to String
	 * @return String
	 */
	public static String toString(Object object) {
		String r = null;
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
			dataOutput.writeObject(object);
			dataOutput.close();
			r = Base64.getEncoder().encodeToString(outputStream.toByteArray());
		} catch (Exception e) {
		}
		return r;
	}

	/**
	 * @apiNote Convert Objects from String to List<T>
	 * @return List<T>
	 */
	public static <T> List<T> getObjectsFromString(String savedObjects) {
		return fromStringToList(savedObjects);
	}

	/**
	 * @apiNote Convert Objects from String to List<T>
	 * @return List<T>
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> fromStringToList(String savedObjects) {
		List<T> r = new ArrayList<>();
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(savedObjects));
			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
			while (true) {
				try {
					r.add((T) dataInput.readObject());
				} catch (Exception e) {
					break;
				}
			}
			dataInput.close();
		} catch (Exception e) {
		}
		return r;
	}

	/**
	 * @apiNote Convert Object from String to T
	 * @return T
	 */
	public static <T> T getObjectFromString(String savedObject) {
		return fromString(savedObject);
	}

	/**
	 * @apiNote Convert Object from String to T
	 * @return T
	 */
	@SuppressWarnings("unchecked")
	public static <T> T fromString(String savedObject) {
		T r = null;
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(savedObject));
			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
			r = (T) dataInput.readObject();
			dataInput.close();
		} catch (Exception e) {
		}
		return r;
	}

	public static String toColor(long ints) {
		String s = "" + ints;
		for(int i = ChatColor.values().length-1; i > -1; --i) {
			s=s.replace(i+"", ChatColor.values()[i]+"");
		}
		return s;
	}

	public static String fromColor(String c) {
		for(int i = ChatColor.values().length-1; i > -1; --i) {
			c=c.replace(ChatColor.values()[i]+"", i+"");
		}
		return c;
	}
}
