package me.DevTec.TheAPI.Scheduler;

import me.DevTec.TheAPI.Utils.NMS.NMSAPI;

public class Task implements Runnable {
	private Runnable run;
	private final boolean s, repeat;
	private final int id;
	private int r;
	private boolean c;
	private boolean error;

	public Task(boolean sync, Runnable runnable, int id, boolean multipleTimes) {
		run = runnable;
		s = sync;
		this.id = id;
		repeat = multipleTimes;
	}

	public int getId() {
		return id;
	}

	@Override
	public void run() {
		if(c||error)return;
		++r;
		try {
		if (s)
			NMSAPI.postToMainThread(run);
		else 
			run.run();
		}catch(Exception er) {
			error=true;
			er.printStackTrace();
		}
	}
	
	public boolean hasException() {
		return error;
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
		c = true;
	}

	public int runTimes() {
		return r;
	}
}
