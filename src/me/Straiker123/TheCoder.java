package me.Straiker123;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import java.util.Base64;

public class TheCoder {

	private StringUtils g = TheAPI.getStringUtils();

	/**
	 * @see see Create Location from String
	 * @return Location
	 */
	public Location locationFromString(String savedLocation) {
		if(savedLocation == null)return null;
		try {
			String[] s = savedLocation.replace("_", ".").split(",");
			return new Location(Bukkit.getWorld(s[0]),g.getDouble(s[1]), g.getDouble(s[2]), g.getDouble(s[3]),g.getFloat(s[4]),g.getFloat(s[5]));
			}catch(Exception er) {
				return null;
		}
	}

	/**
	 * @see see Convert Location to String
	 * @return String
	 */
	public String locationToString(Location loc) {
		if(loc == null)return null;
		return (loc.getWorld().getName()+","+loc.getX()+","+loc.getY()+","+loc.getBlockZ()+","+loc.getYaw()+","+loc.getPitch()).replace(".", "_");
	}

	/**
	 * @see see Convert Objects to String
	 * @return String
	 */
	public String toString(Object... objects) {
		String r = null;
		try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeInt(objects.length);
            for(Object o : objects)
            dataOutput.writeObject(o);
            dataOutput.close();
            r=Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (Exception e) {}
		return r;
	}
	

	/**
	 * @see see Convert Object to String
	 * @return String
	 */
	public String toString(Object object) {
		String r = null;
		try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeObject(object);
            dataOutput.close();
            r=Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (Exception e) {}
		return r;
	}

	/**
	 * @see see Convert Objects from String to List<T>
	 * @return List<T>
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> getObjectsFromString(String savedObjects) {
		List<T> r = new ArrayList<T>();
		try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(savedObjects));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            int a = dataInput.readInt();
            try {
            for(int i = 0; i > a; ++i) {
            	r.add((T)dataInput.readObject());
            }
            } catch (Exception e) {}
            dataInput.close();
        } catch (Exception e) {}
		return r;
	}

	/**
	 * @see see Convert Objects from String to List<T>
	 * @return List<T>
	 */
	@SuppressWarnings("unchecked")
	public <T> T getObjectFromString(String savedObject) {
		T r = null;
		try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(savedObject));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            	r=(T)dataInput.readObject();
            dataInput.close();
        } catch (Exception e) {}
		return r;
	}
	
}
