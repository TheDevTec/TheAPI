package me.DevTec.TheAPI.Scheduler;

import java.util.Timer;
import java.util.TimerTask;

import me.DevTec.TheAPI.Utils.NMS.NMSAPI;
import me.DevTec.TheAPI.Utils.TheAPIUtils.LoaderClass;

public class Scheduler {
	public static Task getTask(int id) {
		if (LoaderClass.plugin.scheduler.containsKey(id))
			return LoaderClass.plugin.scheduler.get(id);
		return null;
	}

	public static void cancelTask(int id) {
		if (LoaderClass.plugin.scheduler.containsKey(id)) {
			LoaderClass.plugin.scheduler.get(id).cancel();
			LoaderClass.plugin.scheduler.remove(id);
		}
	}

	public static void cancelTask(Task id) {
		id.cancel();
		if (LoaderClass.plugin.scheduler.containsKey(id.getId()))
			LoaderClass.plugin.scheduler.remove(id.getId());
	}

	public static void cancelAll() {
		for (Integer r : LoaderClass.plugin.scheduler.keySet()) {
			LoaderClass.plugin.scheduler.get(r).cancel();
		}
		LoaderClass.plugin.scheduler.clear();
	}

	public static Task repeatingTimes(Runnable task, long delay, long period, long times) {
		return repeatingTimes(task, delay, period, times, false);
	}

	public static Task repeatingTimesAsync(Runnable task, long delay, long period, long times) {
		return repeatingTimes(task, delay, period, times, true);
	}

	public static Task repeating(Runnable task, long delay, long period) {
		return repeating(task, delay, period, false);
	}

	public static Task repeatingAsync(Runnable task, long delay, long period) {
		return repeating(task, delay, period, true);
	}

	public static Task run(Runnable task) {
		Task t = run(task, false, false);
		new Thread(new Runnable() {
			public void run() {
				NMSAPI.postToMainThread(task);
				cancelTask(t);
				Thread.interrupted();
			}
		}).start();
		return t;
	}

	public static Task runAsync(Runnable task) {
		Task t = run(task, true, false);
		new Thread(new Runnable() {
			public void run() {
				t.run();
				cancelTask(t);
				Thread.interrupted();
			}
		}).start();
		return t;
	}

	public static Task later(Runnable task, long delay) {
		return later(task, delay, false);
	}

	public static Task laterAsync(Runnable task, long delay) {
		return later(task, delay, true);
	}

	private static Task run(Runnable task, boolean async, boolean multipleTimes) {
		if (task == null) {
			new Exception("Error when starting task, runnable cannot be null.").printStackTrace();
			return null;
		}
		int id = 0;
		for (int i = 1; i > 0; ++i) {
			if (!LoaderClass.plugin.scheduler.containsKey(i)) {
				id = i;
				break;
			}
		}
		LoaderClass.plugin.scheduler.put(id, new Task(!async, task, id, multipleTimes));
		return LoaderClass.plugin.scheduler.get(id);
	}

	private static Task later(Runnable task, long delay, boolean async) {
		if (delay < 0) {
			new Exception("Error when starting a later task, delay cannot be negative.").printStackTrace();
			return null;
		}
		Task t = run(task, async, false);
		new Timer().scheduleAtFixedRate(new TimerTask() {
			public void run() {
					t.run();
					cancelTask(t);
					cancel();
					Thread.interrupted();
			}
		}, delay*50,1);
		return t;
	}

	private static Task repeating(Runnable task, long delay, long period, boolean async) {
		if (period < 0) {
			new Exception("Error when starting a repeating task, period cannot be negative.").printStackTrace();
			return null;
		}
		if (delay < 0) {
			new Exception("Error when starting a repeating task, delay cannot be negative.").printStackTrace();
			return null;
		}
		Task t = run(task, async, true);
		new Timer().scheduleAtFixedRate(new TimerTask() {
			public void run() {
					t.run();
					if(t.isCancelled()) {
					cancelTask(t);
					cancel();
					Thread.interrupted();
					}
			}
		}, delay*50, period*50);
		return t;
	}

	private static Task repeatingTimes(Runnable task, long delay, long period, long times, boolean async) {
		if (times <= 0) {
			new Exception("Error when starting a repeatingTimes task, times cannot be zero or negative.")
					.printStackTrace();
			return null;
		}
		if (period < 0) {
			new Exception("Error when starting a repeatingTimes task, period cannot be negative.").printStackTrace();
			return null;
		}
		if (delay < 0) {
			new Exception("Error when starting a repeatingTimes task, delay cannot be negative.").printStackTrace();
			return null;
		}
		Task t = run(task, async, true);
		new Timer().scheduleAtFixedRate(new TimerTask() {
			public void run() {
					t.run();
					if(t.isCancelled()||t.runTimes()>=times) {
					cancelTask(t);
					cancel();
					Thread.interrupted();
					}
			}
		}, delay*50, period*50);
		return t;
	}
}