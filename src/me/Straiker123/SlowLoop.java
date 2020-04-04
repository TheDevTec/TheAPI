package me.Straiker123;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;

public abstract class SlowLoop <T> {
	private List<T> to = new ArrayList<T>();
	private boolean s = false;
	private int task=0;
	private long old;
	public void addToLoop(List<T> toLoop) {
		for(T t: toLoop)to.add(t);
	}
	public void addToLoop(Collection<T> toLoop) {
		for(T t: toLoop)to.add(t);
	}
	public void addToLoop(T toLoop) {
		to.add(toLoop);
	}
	
	public void setInfinityTask(boolean set) {
		s=set;
	}

	public boolean isInfinityTask() {
		return s;
	}

	public long getTimeRunning() {
		return old/1000-System.currentTimeMillis()/1000;
	}
	
	public void start(long update) {
		old=System.currentTimeMillis();
		task=Bukkit.getScheduler().scheduleSyncRepeatingTask(LoaderClass.plugin, new Runnable() {

			@Override
			public void run() {
				if(to.isEmpty()==false) {
					T t = to.get(to.size()-1);
					toRun(t);
					to.remove(t);
				}else if(!s)
					Bukkit.getScheduler().cancelTask(task);
				
			}
			
		}, update, update);
	}

	abstract void toRun(T t);
}
