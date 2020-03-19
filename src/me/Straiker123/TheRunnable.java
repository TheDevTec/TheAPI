package me.Straiker123;

import org.bukkit.Bukkit;

import me.Straiker123.Utils.Error;

public class TheRunnable {
	private int id = -0, id_repeatfor = -0, id_repeatfor_run = -0;
    
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
		runRepeating(r,period,period);
		return this;
    }
	public TheRunnable runRepeatingFor(Runnable r, Runnable onEnd,long periodAndRepeatTime) {
		return runRepeatingFor(r,onEnd,periodAndRepeatTime,periodAndRepeatTime);
    }
	public TheRunnable runRepeatingFor(Runnable r, Runnable onEnd, long delay,long periodAndRepeatTime) {
		if(id_repeatfor != -0 || id_repeatfor_run!=-0) {
			Error.err("sending repeating task for "+periodAndRepeatTime, "On this thread is already running task");
			return this;
		}
		id_repeatfor= Bukkit.getScheduler().scheduleSyncRepeatingTask(LoaderClass.plugin, r,delay,periodAndRepeatTime);
		id_repeatfor_run= Bukkit.getScheduler().scheduleSyncRepeatingTask(LoaderClass.plugin, new Runnable() {
			int times =0;
			public void run() {
				if(times==periodAndRepeatTime) {
					Bukkit.getScheduler().cancelTask(id_repeatfor);
					id_repeatfor = -0;
					Bukkit.getScheduler().cancelTask(id_repeatfor_run);
					id_repeatfor_run = -0;
					onEnd.run();
				}
				++times;
			}
		},delay,periodAndRepeatTime);
		return this;
    }
	
	public void cancel() {
		Bukkit.getScheduler().cancelTask(id);
		id = -0;
	}
	
}
