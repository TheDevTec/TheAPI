package me.devtec.theapi.scheduler;

import me.devtec.theapi.utils.nms.NMSAPI;

public class Scheduler {
	private static MultiThread thread = new MultiThread();

	public static void cancelAll() {
		thread.destroy();
	}

	public static void cancelTask(int task) {
		if(!isCancelled(task))
			thread.destroy(task);
	}
	
	public static boolean isCancelled(int task) {
		return !thread.isAlive(task);
	}
	
	//ASYNCHRONOUOS

	public static int run(Runnable r) {
		if(r==null)return -1;
		int id = thread.incrementAndGet();
		return thread.executeWithId(id, new Runnable() {
			public void run() {
				try {
					if(!isCancelled(id))
						r.run();
					thread.destroy(id);
				} catch (Exception er) {
					thread.destroy(id);
					if (er instanceof InterruptedException == false)
						er.printStackTrace();
					return;
				}
			}
		});
	}

	public static int later(long delay, Runnable r) {
		if(r==null)return -1;
		int id = thread.incrementAndGet();
		return thread.executeWithId(id, new Runnable() {
			public void run() {
				try {
					if (delay > 0)
						Thread.sleep(delay * 50);
					if(!isCancelled(id))
						r.run();
					thread.destroy(id);
				} catch (Exception er) {
					thread.destroy(id);
					if (er instanceof InterruptedException == false)
						er.printStackTrace();
					return;
				}
			}
		});
	}

	public static int repeating(long delay, long period, Runnable r) {
		if(r==null||period<0)return -1;
		int id = thread.incrementAndGet();
		return thread.executeWithId(id, new Runnable() {
			public void run() {
				try {
					if (delay > 0)
						Thread.sleep(delay * 50);
					while(!isCancelled(id)) {
						r.run();
						Thread.sleep(period * 50);
					}
					thread.destroy(id);
				} catch (Exception er) {
					thread.destroy(id);
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
		if(runnable==null||period<0||times<0)return -1;
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
					if(!isCancelled(id)) {
						if(onFinish!=null)
							onFinish.run();
					}
					thread.destroy(id);
				} catch (Exception er) {
					thread.destroy(id);
					if (er instanceof InterruptedException == false)
						er.printStackTrace();
					return;
				}
			}
		});
	}

	//SYNCHRONOUS WITH SERVER
	
	public static int runSync(Runnable r) {
		if(r==null)return -1;
		int id = thread.incrementAndGet();
		return thread.executeWithId(id, new Runnable() {
			public void run() {
				try {
					if(!isCancelled(id))
						NMSAPI.postToMainThread(r);
					thread.destroy(id);
				} catch (Exception er) {
					thread.destroy(id);
					if (er instanceof InterruptedException == false)
						er.printStackTrace();
					return;
				}
			}
		});
	}

	public static int laterSync(long delay, Runnable r) {
		if(r==null)return -1;
		int id = thread.incrementAndGet();
		return thread.executeWithId(id, new Runnable() {
			public void run() {
				try {
					if (delay > 0)
						Thread.sleep(delay * 50);
					if(!isCancelled(id))
						NMSAPI.postToMainThread(r);
					thread.destroy(id);
				} catch (Exception er) {
					thread.destroy(id);
					if (er instanceof InterruptedException == false)
						er.printStackTrace();
					return;
				}
			}
		});
	}

	public static int repeatingSync(long delay, long period, Runnable r) {
		if(r==null||period<0)return -1;
		int id = thread.incrementAndGet();
		return thread.executeWithId(id, new Runnable() {
			public void run() {
				try {
					if (delay > 0)
						Thread.sleep(delay * 50);
					while(!isCancelled(id)) {
						NMSAPI.postToMainThread(r);
						Thread.sleep(period * 50);
					}
					thread.destroy(id);
				} catch (Exception er) {
					thread.destroy(id);
					if (er instanceof InterruptedException == false)
						er.printStackTrace();
					return;
				}
			}
		});
	}

	public static int timerSync(long delay, long period, long times, Runnable r) {
		return repeatingTimesSync(delay, period, times, r, null);
	}

	public static int repeatingTimesSync(long delay, long period, long times, Runnable runnable) {
		return repeatingTimesSync(delay, period, times, runnable, null);
	}

	public static int repeatingTimesSync(long delay, long period, long times, Runnable runnable, Runnable onFinish) {
		if(runnable==null||period<0||times<0)return -1;
		int id = thread.incrementAndGet();
		return thread.executeWithId(id, new Runnable() {
			int run = 0;
			public void run() {
				try {
					if (delay > 0)
						Thread.sleep(delay * 50);
					while(!isCancelled(id) && (run++) < times) {
						NMSAPI.postToMainThread(runnable);
						Thread.sleep(period * 50);
					}
					if(!isCancelled(id)) {
						if(onFinish!=null)
							NMSAPI.postToMainThread(onFinish);
					}
					thread.destroy(id);
				} catch (Exception er) {
					thread.destroy(id);
					if (er instanceof InterruptedException == false)
						er.printStackTrace();
					return;
				}
			}
		});
	}
}