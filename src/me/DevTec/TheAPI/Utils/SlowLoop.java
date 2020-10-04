package me.DevTec.TheAPI.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import me.DevTec.TheAPI.Scheduler.Tasker;

public abstract class SlowLoop<T> {
	private List<T> to = new ArrayList<T>();
	private boolean s = false;
	private long old;

	public void addToLoop(List<T> toLoop) {
		for (T t : toLoop)
			to.add(t);
	}

	public void addToLoop(Collection<T> toLoop) {
		for (T t : toLoop)
			to.add(t);
	}

	public void addToLoop(T toLoop) {
		to.add(toLoop);
	}

	public void setInfinityTask(boolean set) {
		s = set;
	}

	public boolean isInfinityTask() {
		return s;
	}

	public long getTimeRunning() {
		return old / 1000 - System.currentTimeMillis() / 1000;
	}

	public void start(long update) {
		old = System.currentTimeMillis();
		new Tasker() {
			public void run() {
				if (!to.isEmpty()) {
					T t = to.get(to.size() - 1);
					toRun(t);
					to.remove(t);
				} else if (!s)
					cancel();

			}
		}.runRepeating(0, update);
	}

	public abstract void toRun(T t);
}
