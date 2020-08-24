package me.DevTec.TheAPI.WorldsAPI;

import org.bukkit.Location;
import org.bukkit.World;

import me.DevTec.TheAPI.Utils.TheAPIUtils.LoaderClass;

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
		} catch (Exception | NoSuchMethodError err) {
			// this is not available
		}
	}

	public void set(double size, long time) {
		try {
			w.getWorldBorder().setSize(size, time);
		} catch (Exception | NoSuchMethodError err) {
			// this is not available
		}
	}

	public void add(double size, long time) {
		try {
			w.getWorldBorder().setSize(w.getWorldBorder().getSize() + size, time);
		} catch (Exception | NoSuchMethodError err) {
			// this is not available
		}
	}

	public void remove(double size, long time) {
		try {
			double remove = w.getWorldBorder().getSize() - size;
			if (remove > 0)
				w.getWorldBorder().setSize(remove, time);
			else {
				me.DevTec.TheAPI.Utils.TheAPIUtils.Error.err("set world border size of world " + w.getName() + " to " + remove,
						"Size to remove can't be more than world border size");
			}
		} catch (Exception | NoSuchMethodError err) {
			// this is not available
		}
	}

	public static enum DamageType {
		amount, buffer
	}

	public void setDamageAmount(double amount) {
		try {
			if (amount > 0)
				w.getWorldBorder().setDamageAmount(amount);
			else {
				me.DevTec.TheAPI.Utils.TheAPIUtils.Error.err("set world border damage of world " + w.getName() + " to " + amount,
						"Damage must be less than 0");
			}
		} catch (Exception | NoSuchMethodError err) {
			// this is not available
		}
	}

	public void setDamageBuffer(double amount) {
		try {
			if (amount > 0)
				w.getWorldBorder().setDamageBuffer(amount);
			else {
				me.DevTec.TheAPI.Utils.TheAPIUtils.Error.err("set world border buffer of world " + w.getName() + " to " + amount,
						"Buffer must be less than 0");
			}
		} catch (Exception | NoSuchMethodError err) {
			// this is not available
		}
	}

	public void setWarningDistance(int distance) {
		try {
			if (distance > 0)
				w.getWorldBorder().setWarningDistance(distance);
			else {
				me.DevTec.TheAPI.Utils.TheAPIUtils.Error.err("set world border warning distance of world " + w.getName() + " to " + distance,
						"Distance must be less than 0");
			}
		} catch (Exception | NoSuchMethodError err) {
			// this is not available
		}
	}

	public void setWarningTime(int time) {
		try {
			if (time > 0)
				w.getWorldBorder().setWarningTime(time);
			else {
				me.DevTec.TheAPI.Utils.TheAPIUtils.Error.err("set world border warning time of world " + w.getName() + " to " + time,
						"Time must be less than 0");
			}
		} catch (Exception | NoSuchMethodError err) {
			// this is not available
		}
	}

	public int getWarningTime() {
		try {
			return w.getWorldBorder().getWarningTime();
		} catch (Exception | NoSuchMethodError err) {
			return 0;
			// this is not available
		}
	}

	public double getWarningDistance() {
		try {
			return w.getWorldBorder().getWarningDistance();
		} catch (Exception | NoSuchMethodError err) {
			return 0.0;
			// this is not available
		}
	}

	public void setWarningMessage(String message) {
		LoaderClass.data.getConfig().set("WorldBorder." + w.getName() + ".Message", message);
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
		LoaderClass.data.getConfig().set("WorldBorder." + w.getName() + ".CancelMoveOutside", cancel);
	}

	public boolean isCancellledMoveOutside() {
		return LoaderClass.data.getConfig().getBoolean("WorldBorder." + w.getName() + ".CancelMoveOutside");
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
		LoaderClass.data.getConfig().set("WorldBorder." + w.getName() + ".Type", type);
	}

	public void loadChunksOutside(boolean set) {
		LoaderClass.data.getConfig().set("WorldBorder." + w.getName() + ".Outside", set);
	}

	public boolean getLoadChunksOutside() {
		return LoaderClass.data.getConfig().getBoolean("WorldBorder." + w.getName() + ".Outside");
	}

	public String getWarningMessage() {
		return LoaderClass.data.getConfig().getString("WorldBorder." + w.getName() + ".Message");
	}

	public double getDamageBuffer() {
		try {
			return w.getWorldBorder().getDamageBuffer();
		} catch (Exception | NoSuchMethodError err) {
			// this is not available
			return 0.0;
		}
	}

	public double getDamageAmount() {
		try {
			return w.getWorldBorder().getDamageAmount();
		} catch (Exception | NoSuchMethodError err) {
			// this is not available
			return 0.0;
		}
	}

	public Location getCenter() {
		try {
			return w.getWorldBorder().getCenter();
		} catch (Exception | NoSuchMethodError err) {
			// this is not available
			return null;
		}
	}

	public double getSize() {
		try {
			return w.getWorldBorder().getSize();
		} catch (Exception | NoSuchMethodError err) {
			// this is not available
			return 0.0;
		}
	}

}
