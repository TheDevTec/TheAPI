package me.Straiker123;

import org.bukkit.Location;
import org.bukkit.World;

import me.Straiker123.Utils.Error;

public class WorldBorderAPI {
	World w;
	public WorldBorderAPI(World world) {
		w=world;
	}
	public void setCenter(double x, double z) {
		w.getWorldBorder().setCenter(x, z);
	}
	public void set(double size, long time) {
		w.getWorldBorder().setSize(size, time);
	}
	public void add(double size, long time) {
		w.getWorldBorder().setSize(w.getWorldBorder().getSize()+size, time);
	}
	
	public void remove(double size, long time) {
		double remove = w.getWorldBorder().getSize()-size;
		if(remove >0)
		w.getWorldBorder().setSize(remove, time);
		else {
			Error.err("set world border size of world "+w.getName()+" to "+remove, "Size to remove can't be more than world border size");
		}
	}
	public static enum DamageType{
		amount,
		buffer
	}
	public void setDamageAmount(double amount) {
		if(amount >0)
		w.getWorldBorder().setDamageAmount(amount);
		else {
			Error.err("set world border damage of world "+w.getName()+" to "+amount, "Damage must be less than 0");
		}
	}
	public void setDamageBuffer(double amount) {
		if(amount >0)
		w.getWorldBorder().setDamageBuffer(amount);
		else {
			Error.err("set world border buffer of world "+w.getName()+" to "+amount, "Buffer must be less than 0");
		}
	}
	
	public void setWarningDistance(int distance) {
		if(distance >0)
			w.getWorldBorder().setWarningDistance(distance);
			else {
				Error.err("set world border warning distance of world "+w.getName()+" to "+distance, "Distance must be less than 0");
			}
	}
	
	public void setWarningTime(int time) {
		if(time >0)
			w.getWorldBorder().setWarningTime(time);
			else {
				Error.err("set world border warning time of world "+w.getName()+" to "+time, "Time must be less than 0");
			}
	}

	public int getWarningTime() {
		return w.getWorldBorder().getWarningTime();
	}
	public double getWarningDistance() {
		return w.getWorldBorder().getWarningDistance();
	}
	public void setWarningMessage(String message) {
		LoaderClass.data.getConfig().set("WorldBorder."+w.getName()+".Message", message);
		LoaderClass.data.save();
	}
	public static enum WarningMessageType {
		ACTIONBAR,
		TITLE,
		SUBTITLE,
		CHAT,
		BOSSBAR,
		NONE
	}
	
	/**
	 * Is location outside world border
	 * @return boolean
	 */
	
    public boolean isOutside(Location loc) {
        double size = getSize()/2;
        double x = loc.getX() - getCenter().getX();
        double z = loc.getZ() - getCenter().getZ();
        return Math.abs(x) > size && Math.abs(z) > size;
    }

    public void cancelMoveOutside(boolean cancel) {
	LoaderClass.data.getConfig().set("WorldBorder."+w.getName()+".CancelMoveOutside", cancel);
	LoaderClass.data.save();
    }
    public boolean isCancellledMoveOutside() {
    	return LoaderClass.data.getConfig().getBoolean("WorldBorder."+w.getName()+".CancelMoveOutside");
    }
	
	/**
	 * 
	 * Is location outside world border
	 * @return boolean
	 */
	public boolean isOutside(double X, double Z) {
	    return isOutside(new Location(w,X,100,Z));
	}
	
	public void setWarningMessageType(WarningMessageType type) {
		LoaderClass.data.getConfig().set("WorldBorder."+w.getName()+".Type", type);
		LoaderClass.data.save();
	}
	public void loadChunksOutside(boolean set) {
		LoaderClass.data.getConfig().set("WorldBorder."+w.getName()+".Outside", set);
		LoaderClass.data.save();
	}
	
	public boolean getLoadChunksOutside() {
		return LoaderClass.data.getConfig().getBoolean("WorldBorder."+w.getName()+".Outside");
	}
	public String getWarningMessage() {
		return LoaderClass.data.getConfig().getString("WorldBorder."+w.getName()+".Message");
	}
	public double getDamageBuffer() {
		return w.getWorldBorder().getDamageBuffer();
	}
	public double getDamageAmount() {
		return w.getWorldBorder().getDamageAmount();
	}
	public Location getCenter() {
		return w.getWorldBorder().getCenter();
	}
	public double getSize() {
		return w.getWorldBorder().getSize();
	}

}
