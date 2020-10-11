package me.DevTec.TheAPI.Scheduler;

import java.util.HashMap;

import me.DevTec.TheAPI.Utils.NMS.NMSAPI;

public class Scheduler {
	private final static HashMap<Integer, Thread> tasks = new HashMap<>();
	
	public static void cancelAll() {
		for(Thread t : tasks.values())
			t.interrupt();
		tasks.clear();
	}
	
	public static void cancelTask(int task) {
		if(!tasks.containsKey(task))return;
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
		tasks.put(id, new Thread(new Runnable() {
			public void run() {
				try {
					if(delay>0)
					Thread.sleep(delay*50);
					if(!Thread.currentThread().isInterrupted() && tasks.containsKey(id)) {
						tasks.remove(id);
						Thread.currentThread().interrupt();
						r.run();
					}
				}catch(Exception er) {
					tasks.remove(id);
					Thread.currentThread().interrupt();
					if(er instanceof InterruptedException == false)
					er.printStackTrace();
					return;
				}
			}
		},"TheAPI thread-"+id));
		tasks.get(id).start();
		return id;
	}
	
	public static int laterSync(long delay, Runnable r) {
		int id = find();
		tasks.put(id, new Thread(new Runnable() {
			public void run() {
				try {
					if(delay>0)
					Thread.sleep(delay*50);
					if(!Thread.currentThread().isInterrupted() && tasks.containsKey(id)) {
						tasks.remove(id);
						Thread.currentThread().interrupt();
						NMSAPI.postToMainThread(r);
					}
				}catch(Exception er) {
					tasks.remove(id);
					Thread.currentThread().interrupt();
					if(er instanceof InterruptedException == false)
					er.printStackTrace();
					return;
				}
			}
		},"TheAPI thread-"+id));
		tasks.get(id).start();
		return id;
	}
	
	public static int repeating(long delay, long period, Runnable r) {
		int id = find();
		tasks.put(id, new Thread(new Runnable() {
			public void run() {
				try {
					if(delay>0)
					Thread.sleep(delay*50);
					while(true) {
						if(!Thread.currentThread().isInterrupted() && tasks.containsKey(id)) {
						r.run();
						Thread.sleep(period*50);
						}else {
							tasks.remove(id);
							Thread.currentThread().interrupt();
							break;
						}
					}
				}catch(Exception er) {
					tasks.remove(id);
					Thread.currentThread().interrupt();
					if(er instanceof InterruptedException == false)
					er.printStackTrace();
					return;
				}
			}
		},"TheAPI thread-"+id));
		tasks.get(id).start();
		return id;
	}
	
	public static int repeatingSync(long delay, long period, Runnable r) {
		int id = find();
		tasks.put(id, new Thread(new Runnable() {
			public void run() {
				try {
					if(delay>0)
					Thread.sleep(delay*50);
					while(true) {
						if(!Thread.currentThread().isInterrupted() && tasks.containsKey(id)) {
							NMSAPI.postToMainThread(r);
						Thread.sleep(period*50);
						}else {
							tasks.remove(id);
							Thread.currentThread().interrupt();
							break;
						}
					}
				}catch(Exception er) {
					tasks.remove(id);
					Thread.currentThread().interrupt();
					if(er instanceof InterruptedException == false)
					er.printStackTrace();
					return;
				}
			}
		},"TheAPI thread-"+id));
		tasks.get(id).start();
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
		tasks.put(id, new Thread(new Runnable() {
			long run = 0;
			public void run() {
				try {
					if(delay>0)
					Thread.sleep(delay*50);
					while(true) {
						if(!Thread.currentThread().isInterrupted() && tasks.containsKey(id) && (run++)<times) {
							NMSAPI.postToMainThread(r);
						Thread.sleep(period*50);
						}else {
							tasks.remove(id);
							Thread.currentThread().interrupt();
							break;
						}
					}
				}catch(Exception er) {
					tasks.remove(id);
					Thread.currentThread().interrupt();
					if(er instanceof InterruptedException == false)
					er.printStackTrace();
					return;
				}
			}
		},"TheAPI thread-"+id));
		tasks.get(id).start();
		return id;
	}
	
	public static int repeatingTimes(long delay, long period, long times, Runnable r) {
		int id = find();
		tasks.put(id, new Thread(new Runnable() {
			long run = 0;
			public void run() {
				try {
					if(delay>0)
					Thread.sleep(delay*50);
					while(true) {
						if(!Thread.currentThread().isInterrupted() && tasks.containsKey(id) && (run++)<times) {
						r.run();
						Thread.sleep(period*50);
						}else {
							tasks.remove(id);
							Thread.currentThread().interrupt();
							break;
						}
					}
				}catch(Exception er) {
					tasks.remove(id);
					Thread.currentThread().interrupt();
					if(er instanceof InterruptedException == false)
					er.printStackTrace();
					return;
				}
			}
		},"TheAPI thread-"+id));
		tasks.get(id).start();
		return id;
	}

	private static int find() {
		int f = 0;
		for(int i = 1; i > 0; ++i)
			if(!tasks.containsKey(i)) {
				f=i;
				break;
			}
		return f;
	}
}