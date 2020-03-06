package me.Straiker123;

import org.bukkit.Bukkit;

import me.Straiker123.Utils.Error;

public class TheRunnable {
	private int id = -0;
    
	public int getID() {
		return id;
	}
	
	public void resetID() {
		id=-0;
	}
	
	public TheRunnable runLater(Runnable r,long delay) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(LoaderClass.plugin, r, delay);
		return this;
    }
    
	public TheRunnable runRepeating(Runnable r,long delay,long period) {
		if(id != -0) {
			Error.err("sending repeating task", "On this thread is already running task");
			return this;
		}
		id= Bukkit.getScheduler().scheduleSyncRepeatingTask(LoaderClass.plugin, r,delay,period);
		return this;
    }
    
	public TheRunnable runRepeating(Runnable r,long period) {
		runRepeating(r,0,period);
		return this;
    }
	
	public void cancel() {
		Bukkit.getScheduler().cancelTask(id);
		id = -0;
	}
	
}
