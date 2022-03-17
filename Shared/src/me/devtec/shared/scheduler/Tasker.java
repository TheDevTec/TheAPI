package me.devtec.shared.scheduler;

public abstract class Tasker implements Runnable {
	private boolean cancel;
	private int task = -1;

	public final synchronized boolean isCancelled() {
		return cancel;
	}

	public final synchronized void cancel() {
		if(task==-1)return;
		cancel = true;
		Scheduler.cancelTask(task);
		task=-1;
	}

	public final synchronized int getId() {
		return task;
	}

	/*
	  ASYNCHRONOUOS PART
	 */
	
	public final int runTask() {
		if (task == -1)
			return task = Scheduler.run(this);
		return task;
	}

	public final int runRepeating(long delay, long period) {
		if (task == -1)
			return task = Scheduler.repeating(delay, period, this);
		return task;
	}

	public final int runTimer(long delay, long period, long times) {
		return runRepeatingTimes(delay, period, times, null);
	}

	public final int runRepeatingTimes(long delay, long period, long times) {
		return runRepeatingTimes(delay, period, times, null);
	}

	public final int runRepeatingTimes(long delay, long period, long times, Runnable onFinish) {
		if (task == -1)
			return task = Scheduler.repeatingTimes(delay, period, times, this, onFinish);
		return task;
	}

	public final int runLater(long delay) {
		if (task == -1)
			return task = Scheduler.later(delay, this);
		return task;
	}
}