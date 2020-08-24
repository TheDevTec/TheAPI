package me.DevTec.TheAPI.Scheduler;

public abstract class Tasker implements Runnable {
	private Task task;

	public synchronized void cancel() {
		if (task != null)
			Scheduler.cancelTask(task);
	}

	public synchronized int runTimes() {
		if (task != null)
			return task.runTimes();
		return 0;
	}

	public synchronized int runTask() {
		return set(Scheduler.run(this));
	}

	public synchronized int runAsync() {
		return set(Scheduler.runAsync(this));
	}

	public synchronized int later(long delay) {
		return set(Scheduler.later(this, delay));
	}

	public synchronized int laterAsync(long delay) {
		return set(Scheduler.laterAsync(this, delay));
	}

	public synchronized int repeating(long delay, long period) {
		return set(Scheduler.repeating(this, delay, period));
	}

	public synchronized int repeatingAsync(long delay, long period) {
		return set(Scheduler.repeatingAsync(this, delay, period));
	}

	public synchronized int repeatingTimes(long delay, long period, long times) {
		return set(Scheduler.repeatingTimes(this, delay, period, times));
	}

	public synchronized int repeatingTimesAsync(long delay, long period, long times) {
		return set(Scheduler.repeatingTimesAsync(this, delay, period, times));
	}

	public synchronized Task getTask() {
		return task;
	}

	private int set(Task t) {
		if (t != null) {
			task = t;
			return t.getId();
		}
		return -1;
	}

	public static void cancelTask(int id) {
		if (id > 0)
			Scheduler.cancelTask(id);
	}
}
