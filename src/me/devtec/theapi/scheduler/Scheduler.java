package me.devtec.theapi.scheduler;

import me.devtec.theapi.utils.nms.NMSAPI;

public class Scheduler {
	private static MultiThread thread = new MultiThread();

	public static void cancelAll() {
		thread.destroyThreads();
	}

	public static void cancelTask(int task) {
		if(thread.getThreads().contains(task))
			thread.destroyThread(task);
	}

	public static int run(Runnable r) {
		return later(0, r);
	}

	public static int later(long delay, Runnable r) {
		int id = thread.getNextId();
		return thread.execute(id,new Runnable() {
			public void run() {
				try {
					if (delay > 0)
						Thread.sleep(delay * 50);
					if (!Thread.currentThread().isInterrupted() && Thread.currentThread().isAlive()) {
						r.run();
						cancelTask(id);
					}
				} catch (Exception er) {
					cancelTask(id);
					if (er instanceof InterruptedException == false)
						er.printStackTrace();
					return;
				}
			}
		});
	}

	public static int repeating(long delay, long period, Runnable r) {
		int id = thread.getNextId();
		return thread.execute(id,new Runnable() {
			public void run() {
				try {
					if (delay > 0)
						Thread.sleep(delay * 50);
					while (true) {
						if (!Thread.currentThread().isInterrupted() && Thread.currentThread().isAlive()) {
							r.run();
							Thread.sleep(period * 50);
						} else {
							cancelTask(id);
							break;
						}
					}
				} catch (Exception er) {
					cancelTask(id);
					if (er instanceof InterruptedException == false)
						er.printStackTrace();
					return;
				}
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
		int id = thread.getNextId();
		return thread.execute(id,new Runnable() {
			int run = 0;
			public void run() {
				try {
					if (delay > 0)
						Thread.sleep(delay * 50);
					while (true) {
						if (!Thread.currentThread().isInterrupted() && Thread.currentThread().isAlive() && (run++) < times) {
							runnable.run();
							Thread.sleep(period * 50);
						} else {
							if(run >= times) {
								if(onFinish!=null)
								onFinish.run();
							}
							cancelTask(id);
							break;
						}
					}
				} catch (Exception er) {
					cancelTask(id);
					if (er instanceof InterruptedException == false)
						er.printStackTrace();
					return;
				}
			}
		});
	}

	public static int runSync(Runnable r) {
		return laterSync(0, r);
	}

	public static int laterSync(long delay, Runnable r) {
		int id = thread.getNextId();
		return thread.execute(id,new Runnable() {
			public void run() {
				try {
					if (delay > 0)
						Thread.sleep(delay * 50);
					if (!Thread.currentThread().isInterrupted() && Thread.currentThread().isAlive()) {
						NMSAPI.postToMainThread(r);
						cancelTask(id);
					}
				} catch (Exception er) {
					cancelTask(id);
					if (er instanceof InterruptedException == false)
						er.printStackTrace();
					return;
				}
			}
		});
	}

	public static int repeatingSync(long delay, long period, Runnable r) {
		int id = thread.getNextId();
		return thread.execute(id,new Runnable() {
			public void run() {
				try {
					if (delay > 0)
						Thread.sleep(delay * 50);
					while (true) {
						if (!Thread.currentThread().isInterrupted() && Thread.currentThread().isAlive()) {
							NMSAPI.postToMainThread(r);
							Thread.sleep(period * 50);
						} else {
							cancelTask(id);
							break;
						}
					}
				} catch (Exception er) {
					cancelTask(id);
					if (er instanceof InterruptedException == false)
						er.printStackTrace();
					return;
				}
			}
		});
	}

	public static int timerSyncSync(long delay, long period, long times, Runnable r) {
		return repeatingTimesSync(delay, period, times, r, null);
	}

	public static int repeatingTimesSync(long delay, long period, long times, Runnable runnable) {
		return repeatingTimesSync(delay, period, times, runnable, null);
	}

	public static int repeatingTimesSync(long delay, long period, long times, Runnable runnable, Runnable onFinish) {
		int id = thread.getNextId();
		return thread.execute(id,new Runnable() {
			int run = 0;
			public void run() {
				try {
					if (delay > 0)
						Thread.sleep(delay * 50);
					while (true) {
						if (!Thread.currentThread().isInterrupted() && Thread.currentThread().isAlive() && (run++) < times) {
							NMSAPI.postToMainThread(runnable);
							Thread.sleep(period * 50);
						} else {
							if(!Thread.currentThread().isInterrupted() && Thread.currentThread().isAlive()) {
								if(onFinish!=null)
									NMSAPI.postToMainThread(onFinish);
							}
							cancelTask(id);
							break;
						}
					}
				} catch (Exception er) {
					cancelTask(id);
					if (er instanceof InterruptedException == false)
						er.printStackTrace();
					return;
				}
			}
		});
	}
}