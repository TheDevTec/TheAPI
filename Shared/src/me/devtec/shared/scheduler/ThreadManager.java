package me.devtec.shared.scheduler;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadManager implements Executor {
	protected final Map<Integer, Thread> threads = new ConcurrentHashMap<>();
	protected final AtomicInteger i = new AtomicInteger();

	public void destroy() {
		Iterator<Thread> it = this.threads.values().iterator();
		while (it.hasNext()) {
			Thread tht = it.next();
			it.remove();
			if (tht != null && tht.isAlive()) {
				tht.interrupt(); // safe destroy of thread
				tht.stop(); // destroy loops and whole running code
			}
		}
	}

	public boolean isAlive(int id) {
		return this.threads.containsKey(id) && this.threads.get(id).isAlive();
	}

	public Map<Integer, Thread> getThreads() {
		return this.threads;
	}

	public int incrementAndGet() {
		return this.i.incrementAndGet();
	}

	public void destroy(int id) {
		Thread t = this.threads.remove(id);
		if (t == null)
			return;
		t.interrupt(); // safe destroy of thread
		t.stop(); // destroy loops and whole running code
	}

	public int executeWithId(int id, Runnable command) {
		Thread t = new Thread(command, "ThreadManager-Worker-" + id);
		this.threads.put(id, t);
		t.start();
		return id;
	}

	public int executeAndGet(Runnable command) {
		int id = this.i.incrementAndGet();
		return this.executeWithId(id, command);
	}

	@Override
	public void execute(Runnable command) {
		int id = this.i.incrementAndGet();
		this.executeWithId(id, command);
	}
}