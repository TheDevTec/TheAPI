package me.devtec.shared.scheduler;

public abstract class Tasker implements Runnable {
	private boolean cancel;
	private int task = -1;

	public final synchronized boolean isCancelled() {
		return this.cancel;
	}

	public final synchronized void cancel() {
		if (this.task == -1)
			return;
		this.cancel = true;
		Scheduler.cancelTask(this.task);
		this.task = -1;
	}

	public final synchronized int getId() {
		return this.task;
	}

	/*
	 * ASYNCHRONOUOS PART
	 */

	public final int runTask() {
		if (this.task == -1)
			return this.task = Scheduler.run(this);
		return this.task;
	}

	public final int runRepeating(long delay, long period) {
		if (this.task == -1)
			return this.task = Scheduler.repeating(delay, period, this);
		return this.task;
	}

	public final int runTimer(long delay, long period, long times) {
		return this.runRepeatingTimes(delay, period, times, null);
	}

	public final int runRepeatingTimes(long delay, long period, long times) {
		return this.runRepeatingTimes(delay, period, times, null);
	}

	public final int runRepeatingTimes(long delay, long period, long times, Runnable onFinish) {
		if (this.task == -1)
			return this.task = Scheduler.repeatingTimes(delay, period, times, this, onFinish);
		return this.task;
	}

	public final int runLater(long delay) {
		if (this.task == -1)
			return this.task = Scheduler.later(delay, this);
		return this.task;
	}
}