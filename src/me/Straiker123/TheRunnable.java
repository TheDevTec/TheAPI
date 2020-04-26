package me.Straiker123;

import org.bukkit.Bukkit;

import me.Straiker123.Utils.Error;

public class TheRunnable {
	private int id = -0, id_repeatfor = -0, id_repeatfor_run = -0;
    private long start,start1;
	public static enum RunnableType{
		REPEATING,
		REPEATING_FOR
	}
	
	public int getRunningTime(RunnableType type) {
		return (int)(System.currentTimeMillis()/1000-(type==RunnableType.REPEATING?start==0?System.currentTimeMillis()/1000:start :start1
				==0?System.currentTimeMillis()/1000:start1)); //whoa, psycho
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
		start=System.currentTimeMillis()/1000;
		id= Bukkit.getScheduler().scheduleSyncRepeatingTask(LoaderClass.plugin, r,delay,period);
		return this;
    }

	public TheRunnable runRepeating(Runnable r,long period) {
		runRepeating(r,period,period);
		return this;
    }
	public TheRunnable runRepeatingFor(Runnable r, Runnable onEnd,long period) {
		return runRepeatingFor(r,onEnd,period,1);
    }
	public TheRunnable runRepeatingFor(Runnable r, Runnable onEnd, long period,int times) {
		if(id_repeatfor != -0 || id_repeatfor_run!=-0) {
			Error.err("sending repeating task for "+period, "On this thread is already running task");
			return this;
		}
		if(times <= 0) {
			Error.err("sending repeating task for "+period, "Repeat times must be more than 0");
			return this;
		}
		start1=System.currentTimeMillis()/1000;
		id_repeatfor= Bukkit.getScheduler().scheduleSyncRepeatingTask(LoaderClass.plugin, r,period,period);
		id_repeatfor_run= Bukkit.getScheduler().scheduleSyncRepeatingTask(LoaderClass.plugin, new Runnable() {
			int tr = 0;
			public void run() {
				if(times==tr) {
					Bukkit.getScheduler().cancelTask(id_repeatfor);
					id_repeatfor = -0;
					Bukkit.getScheduler().cancelTask(id_repeatfor_run);
					id_repeatfor_run = -0;
					if(onEnd!=null)
					onEnd.run();
					return;
				}
				++tr;
			}
		},period,period);
		return this;
    }
	
	public void cancel() {
		if(id!=-0)
		Bukkit.getScheduler().cancelTask(id);
		id = -0;
	}
	
	public void cancelRepeatingFor() {
		if(id_repeatfor!=-0)
		Bukkit.getScheduler().cancelTask(id_repeatfor);
		id_repeatfor = -0;
		if(id_repeatfor_run!=-0)
		Bukkit.getScheduler().cancelTask(id_repeatfor_run);
		id_repeatfor_run = -0;
	}
	
}
