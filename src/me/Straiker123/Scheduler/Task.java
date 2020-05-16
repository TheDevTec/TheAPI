package me.Straiker123.Scheduler;

import me.Straiker123.TheAPI;

public class Task implements Runnable {
	private Runnable run;
	private final boolean s,removeOnRun;
	private final int id;
	private final TheThread t;
	private int r;
	private boolean c;
	
	public Task(boolean sync, Runnable runnable, int id, boolean multipleTimes) {
		run=runnable;
		s=sync;
		this.id=id;
		removeOnRun=!multipleTimes;
		if(!sync) {
			t = new TheThread("Task-"+id);
		}else
			t=null;
	}

	public int getId() {
		return id;
	}

	public void run() {
			if(isCancelled())return;
			++r;
			if(s)
				TheAPI.getNMSAPI().postToMainThread(run);
			else
			t.postRunnable(run);
			if(removeOnRun) {
				cancel();
				if(t!=null)
				t.interrupt();
				run=null;
				r=0;
			}
	}

	public boolean isRepeating() {
		return !removeOnRun;
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
