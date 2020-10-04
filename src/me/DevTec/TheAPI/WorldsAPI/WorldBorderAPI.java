package me.DevTec.TheAPI.WorldsAPI;

import org.bukkit.Location;
import org.bukkit.World;

import me.DevTec.TheAPI.Utils.TheAPIUtils.LoaderClass;
import me.DevTec.TheAPI.Utils.TheAPIUtils.Validator;

public class WorldBorderAPI {
	private World w;

	public WorldBorderAPI(World world) {
		w = world;
	}

	public World getWorld() {
		return w;
	}

	public void setCenter(double x, double z) {
		try {
			w.getWorldBorder().setCenter(x, z);
		} catch (NoSuchMethodError err) {
			// this is not available
		}
	}

	public void set(double size, long time) {
		try {
			w.getWorldBorder().setSize(size, time);
		} catch (NoSuchMethodError err) {
			// this is not available
		}
	}

	public void add(double size, long time) {
		try {
			w.getWorldBorder().setSize(w.getWorldBorder().getSize() + size, time);
		} catch (NoSuchMethodError err) {
			// this is not available
		}
	}

	public void remove(double size, long time) {
		try {
			double remove = w.getWorldBorder().getSize() - size;
			Validator.validate(remove < 0, "Size to remove can't be more than world border size");
			w.getWorldBorder().setSize(remove, time);
		} catch (NoSuchMethodError err) {
			// this is not available
		}
	}

	public static enum DamageType {
		amount, buffer
	}

	public void setDamageAmount(double amount) {
		try {
			Validator.validate(amount < 0, "Damage must be less than 0");
			w.getWorldBorder().setDamageAmount(amount);
		} catch (NoSuchMethodError err) {
			// this is not available
		}
	}

	public void setDamageBuffer(double amount) {
		try {
			Validator.validate(amount < 0, "Buffer must be less than 0");
			w.getWorldBorder().setDamageBuffer(amount);
		} catch (NoSuchMethodError err) {
			// this is not available
		}
	}

	public void setWarningDistance(int distance) {
		try {
			Validator.validate(distance < 0, "Distance must be less than 0");
			w.getWorldBorder().setWarningDistance(distance);
		} catch (NoSuchMethodError err) {
			// this is not available
		}
	}

	public void setWarningTime(int time) {
		try {
			Validator.validate(time < 0, "Time must be less than 0");
			w.getWorldBorder().setWarningTime(time);
		} catch (NoSuchMethodError err) {
			// this is not available
		}
	}

	public int getWarningTime() {
		try {
			return w.getWorldBorder().getWarningTime();
		} catch (NoSuchMethodError err) {
			return 0;
			// this is not available
		}
	}

	public double getWarningDistance() {
		try {
			return w.getWorldBorder().getWarningDistance();
		} catch (NoSuchMethodError err) {
			return 0.0;
			// this is not available
		}
	}

	public void setWarningMessage(String message) {
		LoaderClass.data.set("WorldBorder." + w.getName() + ".Message", message);
	}

	public static enum WarningMessageType {
		ACTIONBAR, TITLE, SUBTITLE, CHAT, BOSSBAR, NONE
	}

	/**
	 * Is location outside world border
	 * 
	 * @return boolean
	 */

	public boolean isOutside(Location loc) {
		double size = getSize() / 2;
		double x = loc.getX() - getCenter().getX();
		double z = loc.getZ() - getCenter().getZ();
		return Math.abs(x) > size && Math.abs(z) > size;
	}

	public void cancelMoveOutside(boolean cancel) {
		LoaderClass.data.set("WorldBorder." + w.getName() + ".CancelMoveOutside", cancel);
	}

	public boolean isCancellledMoveOutside() {
		return LoaderClass.data.getBoolean("WorldBorder." + w.getName() + ".CancelMoveOutside");
	}

	/**
	 * 
	 * Is location outside world border
	 * 
	 * @return boolean
	 */
	public boolean isOutside(double X, double Z) {
		return isOutside(new Location(w, X, 100, Z));
	}

	public void setWarningMessageType(WarningMessageType type) {
		LoaderClass.data.set("WorldBorder." + w.getName() + ".Type", type);
	}

	public void loadChunksOutside(boolean set) {
		LoaderClass.data.set("WorldBorder." + w.getName() + ".Outside", set);
	}

	public boolean getLoadChunksOutside() {
		return LoaderClass.data.getBoolean("WorldBorder." + w.getName() + ".Outside");
	}

	public String getWarningMessage() {
		return LoaderClass.data.getString("WorldBorder." + w.getName() + ".Message");
	}

	public double getDamageBuffer() {
		try {
			return w.getWorldBorder().getDamageBuffer();
		} catch (NoSuchMethodError err) {
			// this is not available
			return 0.0;
		}
	}

	public double getDamageAmount() {
		try {
			return w.getWorldBorder().getDamageAmount();
		} catch (NoSuchMethodError err) {
			// this is not available
			return 0.0;
		}
	}

	public Location getCenter() {
		try {
			return w.getWorldBorder().getCenter();
		} catch (NoSuchMethodError err) {
			// this is not available
			return null;
		}
	}

	public double getSize() {
		try {
			return w.getWorldBorder().getSize();
		} catch (NoSuchMethodError err) {
			// this is not available
			return 0.0;
		}
	}

}
