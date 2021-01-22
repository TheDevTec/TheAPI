package me.devtec.theapi.scheduler;

import java.util.HashMap;
import java.util.Map.Entry;

import me.devtec.theapi.utils.nms.NMSAPI;

public class Scheduler {
	private static HashMap<Integer, Thread> tasks = new HashMap<>();
	private static int id;

	public static void cancelAll() {
		try {
			for (Entry<Integer, Thread> t : tasks.entrySet())
				t.getValue().interrupt();
		} catch (Exception errr) {
		}
		try {
			tasks.clear();
		} catch (Exception errr) {
			tasks=null;
		}
	}

	public static void cancelTask(int task) {
		if(tasks.containsKey(task))
			tasks.get(task).interrupt();
		tasks.remove(task);
	}

	public static int run(Runnable r) {
		return later(0, r);
	}

	public static int runSync(Runnable r) {
		return laterSync(0, r);
	}

	public static int later(long delay, Runnable r) {
		int id = find();
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					if(tasks==null) {
						Thread.currentThread().interrupt();
						return;
					}
					if (delay > 0)
						Thread.sleep(delay * 50);
					if (!Thread.currentThread().isInterrupted() && tasks.containsKey(id)) {
						r.run();
						Thread.currentThread().interrupt();
						tasks.remove(id);
					}
				} catch (Exception er) {
					Thread.currentThread().interrupt();
					tasks.remove(id);
					if (er instanceof InterruptedException == false)
						er.printStackTrace();
					return;
				}
			}
		}, "TheAPI thread-" + id);
		tasks.put(id, t);
		t.start();
		return id;
	}

	public static int laterSync(long delay, Runnable r) {
		int id = find();
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					if(tasks==null) {
						Thread.currentThread().interrupt();
						return;
					}
					if (delay > 0)
						Thread.sleep(delay * 50);
					if (!Thread.currentThread().isInterrupted() && tasks.containsKey(id)) {
						NMSAPI.postToMainThread(r);
						Thread.currentThread().interrupt();
						tasks.remove(id);
					}
				} catch (Exception er) {
					Thread.currentThread().interrupt();
					tasks.remove(id);
					if (er instanceof InterruptedException == false)
						er.printStackTrace();
					return;
				}
			}
		}, "TheAPI thread-" + id);
		tasks.put(id, t);
		t.start();
		return id;
	}

	public static int repeating(long delay, long period, Runnable r) {
		int id = find();
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					if(tasks==null) {
						Thread.currentThread().interrupt();
						return;
					}
					if (delay > 0)
						Thread.sleep(delay * 50);
					while (true) {
						if (!Thread.currentThread().isInterrupted() && tasks.containsKey(id)) {
							r.run();
							Thread.sleep(period * 50);
						} else {
							Thread.currentThread().interrupt();
							tasks.remove(id);
							break;
						}
					}
				} catch (Exception er) {
					Thread.currentThread().interrupt();
					tasks.remove(id);
					if (er instanceof InterruptedException == false)
						er.printStackTrace();
					return;
				}
			}
		}, "TheAPI thread-" + id);
		tasks.put(id, t);
		t.start();
		return id;
	}

	public static int repeatingSync(long delay, long period, Runnable r) {
		int id = find();
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					if(tasks==null) {
						Thread.currentThread().interrupt();
						return;
					}
					if (delay > 0)
						Thread.sleep(delay * 50);
					while (true) {
						if (!Thread.currentThread().isInterrupted() && tasks.containsKey(id)) {
							NMSAPI.postToMainThread(r);
							Thread.sleep(period * 50);
						} else {
							Thread.currentThread().interrupt();
							tasks.remove(id);
							break;
						}
					}
				} catch (Exception er) {
					Thread.currentThread().interrupt();
					tasks.remove(id);
					if (er instanceof InterruptedException == false)
						er.printStackTrace();
					return;
				}
			}
		}, "TheAPI thread-" + id);
		tasks.put(id, t);
		t.start();
		return id;
	}

	public static int timer(long delay, long period, long times, Runnable r) {
		return repeatingTimes(delay, period, times, r);
	}

	public static int timerSync(long delay, long period, long times, Runnable r) {
		return repeatingTimesSync(delay, period, times, r);
	}

	public static int repeatingTimesSync(long delay, long period, long times, Runnable r) {
		int id = find();
		Thread t = new Thread(new Runnable() {
			long run = 0;

			public void run() {
				try {
					if(tasks==null) {
						Thread.currentThread().interrupt();
						return;
					}
					if (delay > 0)
						Thread.sleep(delay * 50);
					while (true) {
						if (!Thread.currentThread().isInterrupted() && tasks.containsKey(id) && (run++) < times) {
							NMSAPI.postToMainThread(r);
							Thread.sleep(period * 50);
						} else {
							Thread.currentThread().interrupt();
							tasks.remove(id);
							break;
						}
					}
				} catch (Exception er) {
					Thread.currentThread().interrupt();
					tasks.remove(id);
					if (er instanceof InterruptedException == false)
						er.printStackTrace();
					return;
				}
			}
		}, "TheAPI thread-" + id);
		tasks.put(id, t);
		t.start();
		return id;
	}

	public static int repeatingTimes(long delay, long period, long times, Runnable r) {
		int id = find();
		Thread t = new Thread(new Runnable() {
			long run = 0;

			public void run() {
				try {
					if(tasks==null) {
						Thread.currentThread().interrupt();
						return;
					}
					if (delay > 0)
						Thread.sleep(delay * 50);
					while (true) {
						if (!Thread.currentThread().isInterrupted() && tasks.containsKey(id) && (run++) < times) {
							r.run();
							Thread.sleep(period * 50);
						} else {
							Thread.currentThread().interrupt();
							tasks.remove(id);
							break;
						}
					}
				} catch (Exception er) {
					Thread.currentThread().interrupt();
					tasks.remove(id);
					if (er instanceof InterruptedException == false)
						er.printStackTrace();
					return;
				}
			}
		}, "TheAPI thread-" + id);
		tasks.put(id, t);
		t.start();
		return id;
	}

	private static int find() {
		return id++;
	}
}