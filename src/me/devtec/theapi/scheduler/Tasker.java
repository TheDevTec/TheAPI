package me.devtec.theapi.scheduler;

public abstract class Tasker implements Runnable {
	private boolean cancel;
	private int task;

	public synchronized boolean isCancelled() {
		return cancel;
	}

	public synchronized void cancel() {
		cancel = true;
		Scheduler.cancelTask(task);
	}

	public synchronized int getId() {
		return task;
	}

	public int runTask() {
		return task = Scheduler.run(this);
	}

	public int runRepeating(long delay, long period) {
		if (task == 0)
			return task = Scheduler.repeating(delay, period, this);
		return task;
	}

	public int runTimer(long delay, long period, long times) {
		return runRepeatingTimes(delay, period, times);
	}

	public int runRepeatingTimes(long delay, long period, long times) {
		if (task == 0)
			return task = Scheduler.repeatingTimes(delay, period, times, this);
		return task;
	}

	public int runLater(long delay) {
		if (task == 0)
			return task = Scheduler.later(delay, this);
		return task;
	}

	public int runTaskSync() {
		return task = Scheduler.runSync(this);
	}

	public int runRepeatingSync(long delay, long period) {
		if (task == 0)
			return task = Scheduler.repeatingSync(delay, period, this);
		return task;
	}

	public int runTimerSync(long delay, long period, long times) {
		return runRepeatingTimesSync(delay, period, times);
	}

	public int runRepeatingTimesSync(long delay, long period, long times) {
		if (task == 0)
			return task = Scheduler.repeatingTimesSync(delay, period, times, this);
		return task;
	}

	public int runLaterSync(long delay) {
		if (task == 0)
			return task = Scheduler.laterSync(delay, this);
		return task;
	}
}