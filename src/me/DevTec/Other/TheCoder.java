package me.DevTec.Other;

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
	 * @see see Create Location from String
	 * @return Location
	 */
	public static Location locationFromString(String savedLocation) {
		if (savedLocation == null)
			return null;
		try {
			String[] s = savedLocation.replace("_", ".").split(",");
			return new Location(Bukkit.getWorld(s[0].replace(":", "_")), StringUtils.getDouble(s[1]), StringUtils.getDouble(s[2]),
					StringUtils.getDouble(s[3]), StringUtils.getFloat(s[4]), StringUtils.getFloat(s[5]));
		} catch (Exception er) {
			return null;
		}
	}

	/**
	 * @see see Convert Location to String
	 * @return String
	 */
	public static String locationToString(Location loc) {
		if (loc == null)
			return null;
		return (loc.getWorld().getName().replace("_", ":") + "," + loc.getX() + "," + loc.getY() + "," + loc.getBlockZ()
				+ "," + loc.getYaw() + "," + loc.getPitch()).replace(".", "_");
	}

	/**
	 * @see see Convert Objects to String
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
	 * @see see Convert Object to String
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
	 * @see see Convert Objects from String to List<T>
	 * @return List<T>
	 */
	public static <T> List<T> getObjectsFromString(String savedObjects) {
		return fromStringToList(savedObjects);
	}

	/**
	 * @see see Convert Objects from String to List<T>
	 * @return List<T>
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> fromStringToList(String savedObjects) {
		List<T> r = new ArrayList<T>();
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
	 * @see see Convert Object from String to T
	 * @return T
	 */
	public static <T> T getObjectFromString(String savedObject) {
		return fromString(savedObject);
	}

	/**
	 * @see see Convert Object from String to T
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
	
	public static String toColor(int ints) {
		String s = ""+ints;
		return s.replace("0", ChatColor.AQUA+"")
				.replace("1", ChatColor.BLACK+"")
				.replace("2", ChatColor.BLUE+"")
				.replace("3", ChatColor.GOLD+"")
				.replace("4", ChatColor.GRAY+"")
				.replace("5", ChatColor.GREEN+"")
				.replace("6", ChatColor.RED+"")
				.replace("7", ChatColor.YELLOW+"")
				.replace("8", ChatColor.WHITE+"")
				.replace("9", ChatColor.DARK_RED+"");
	}
	
	public static String fromColor(String c) {
		return c.replace(ChatColor.AQUA+"","0")
				.replace(ChatColor.BLACK+"","1")
				.replace(ChatColor.BLUE+"","2")
				.replace(ChatColor.GOLD+"","3")
				.replace(ChatColor.GRAY+"","4")
				.replace(ChatColor.GREEN+"","5")
				.replace(ChatColor.RED+"","6")
				.replace(ChatColor.YELLOW+"","7")
				.replace(ChatColor.WHITE+"","8")
				.replace(ChatColor.DARK_RED+"","9");
	}

}
