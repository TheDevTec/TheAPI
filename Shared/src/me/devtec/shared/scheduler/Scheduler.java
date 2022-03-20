package me.devtec.shared.scheduler;

import me.devtec.shared.API;

public class Scheduler {
	private static final MultiThread thread = new MultiThread();

	public static void cancelAll() {
		thread.destroy();
	}

	public static void cancelTask(int task) {
		if(thread.isAlive(task))
			thread.destroy(task);
	}
	
	public static boolean isCancelled(int task) {
		return !thread.isAlive(task) && API.enabled;
	}

	public static int run(Runnable r) {
		if(r==null||!API.enabled)return -1;
		int id = thread.incrementAndGet();
		return thread.executeWithId(id, () -> {
			try {
				if(!isCancelled(id))
					r.run();
				thread.destroy(id);
			} catch (Exception err) {
				thread.destroy(id);
				if(!(err instanceof InterruptedException))
					err.printStackTrace();
			}
		});
	}

	public static int later(long delay, Runnable r) {
		if(r==null||!API.enabled)return -1;
		int id = thread.incrementAndGet();
		return thread.executeWithId(id, () -> {
			try {
				if (delay > 0)
					Thread.sleep(delay * 50);
				if(!isCancelled(id))
					r.run();
				thread.destroy(id);
			} catch (Exception err) {
				thread.destroy(id);
				if(!(err instanceof InterruptedException))
					err.printStackTrace();
			}
		});
	}

	public static int repeating(long delay, long period, Runnable r) {
		if(r==null||!API.enabled||period<0)return -1;
		int id = thread.incrementAndGet();
		return thread.executeWithId(id, () -> {
			try {
				if (delay > 0)
					Thread.sleep(delay * 50);
				while(!isCancelled(id)) {
					r.run();
					Thread.sleep(period * 50);
				}
				thread.destroy(id);
			} catch (Exception err) {
				thread.destroy(id);
				if(!(err instanceof InterruptedException))
					err.printStackTrace();
			}
		});
	}

	public static int timer(long delay, long period, long times, Runnable r) {
		return repeatingTimes(delay, period, times, r, null);
	}

	public static int repeatingTimes(long delay, long period, long times, Runnable runnable) {
		return repeatingTimes(delay, period, times, runnable, null);
	}

	public static int repeatingTimes(long delay, long period, long times, Runnable runnable, Runnable onFinish) {
		if(runnable==null||!API.enabled||period<0||times<0)return -1;
		int id = thread.incrementAndGet();
		return thread.executeWithId(id, new Runnable() {
			int run = 0;
			public void run() {
				try {
					if (delay > 0)
						Thread.sleep(delay * 50);
					while(!isCancelled(id) && (run++) < times) {
						runnable.run();
						Thread.sleep(period * 50);
					}
					if(onFinish!=null && !isCancelled(id))
						onFinish.run();
					thread.destroy(id);
				} catch (Exception err) {
					thread.destroy(id);
					if(!(err instanceof InterruptedException))
						err.printStackTrace();
				}
			}
		});
	}
}