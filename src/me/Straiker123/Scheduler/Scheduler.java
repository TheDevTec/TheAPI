package me.Straiker123.Scheduler;

import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import com.google.common.collect.Maps;

public class Scheduler {
	private static TreeMap<Integer, Task> list = Maps.newTreeMap();
	
	public static Task getTask(int id) {
		if(list.containsKey(id))return list.get(id);
		return null;
	}
	
	public static void cancelAll() {
		for(Integer r : list.keySet()) {
			list.get(r).cancel();
		}
		list.clear();
	}
	
	public static Task repeatingTimes(Runnable task, long delay, long period, long times) {
		return repeatingTimes(task,delay,period,times,false);
	}

	public static Task repeatingTimesAsync(Runnable task, long delay, long period, long times) {
		return repeatingTimes(task,delay,period,times,true);
	}

	public static Task repeating(Runnable task, long delay, long period) {
		return repeating(task,delay,period,false);
	}

	public static Task repeatingAsync(Runnable task, long delay, long period) {
		return repeating(task,delay,period,true);
	}

	public static Task run(Runnable task) {
		Task t = run(task,false,false);
		t.run();
		return t;
	}

	public static Task runAsync(Runnable task) {
		Task t = run(task,true,false);
		t.run();
		return t;
	}

	public static Task later(Runnable task, long delay) {
		return later(task,delay,false);
	}

	public static Task laterAsync(Runnable task, long delay) {
		return later(task,delay,true);
	}
	
	public static void cancelTask(int id) {
		if(list.containsKey(id)) {
			list.get(id).cancel();
			list.remove(id);
		}
	}
	
	private static Task run(Runnable task, boolean async, boolean multipleTimes) {
		int id = 0;
		for(int i = 1; i > 0; ++i) {
			if(!list.containsKey(i)) {
				id=i;
				break;
			}
		}
		list.put(id, new Task(!async, task, id,multipleTimes));
		return list.get(id);
			
	}

	private static Task later(Runnable task, long delay, boolean async) {
		if(delay<0) {
			new Exception("Error when starting a later task, delay cannot be negative.").printStackTrace();
			return null;
		}
		Task t = run(task,async,false);
		new Timer().schedule(new TimerTask() {
			public void run() {
				if(t.isCancelled()) {
					cancel();
					return;
				}
				t.run();
			}
		}, delay*50);
		return t;
	}
	
	private static Task repeating(Runnable task, long delay, long period, boolean async) {
		if(period<0) {
			new Exception("Error when starting a repeating task, period cannot be negative.").printStackTrace();
			return null;
		}
		if(delay<0) {
			new Exception("Error when starting a repeating task, delay cannot be negative.").printStackTrace();
			return null;
		}
		Task t = run(task,async,true);
		new Timer().scheduleAtFixedRate(new TimerTask() {
			public void run() {
				if(t.isCancelled()) {
					cancel();
					return;
				}
				t.run();
			}
		}, delay*50, period*50);
		return t;
	}
	
	private static Task repeatingTimes(Runnable task, long delay, long period, long times, boolean async) {
		if(times<=0) {
			new Exception("Error when starting a repeatingTimes task, times cannot be zero or negative.").printStackTrace();
			return null;
		}
		if(period<0) {
			new Exception("Error when starting a repeatingTimes task, period cannot be negative.").printStackTrace();
			return null;
		}
		if(delay<0) {
			new Exception("Error when starting a repeatingTimes task, delay cannot be negative.").printStackTrace();
			return null;
		}
		Task t = run(task,async,true);
		new Timer().scheduleAtFixedRate(new TimerTask() {
			long repeat = 0;
			public void run() {
				if(repeat==times||t.isCancelled()) {
					t.cancel();
					cancel();
					return;
				}
				++repeat;
				t.run();
			}
		}, delay*50, period*50);
		return t;
	}
}