package me.Straiker123.Scheduler;

import me.Straiker123.TheAPI;

public class Task implements Runnable {
	private Runnable run;
	private final boolean s,repeat;
	private final int id;
	private int r;
	private boolean c;
	
	public Task(boolean sync, Runnable runnable, int id, boolean multipleTimes) {
		run=runnable;
		s=sync;
		this.id=id;
		repeat=multipleTimes;
	}

	public int getId() {
		return id;
	}

	public void run() {
			if(isCancelled())return;
			++r;
			if(s) {
				TheAPI.getNMSAPI().postToMainThread(run);
			}else {
				run.run();
			}
	}

	public boolean isRepeating() {
		return repeat;
	}
	
	public boolean isSync() {
		return s;
	}

	public boolean isCancelled() {
		return c;
	}

	public void cancel() {
		c=true;
	}

	public int runTimes() {
		return r;
	}
}
