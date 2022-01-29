package me.devtec.theapi.scheduler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
public class MultiThread implements Executor {
	protected final Map<Integer, Thread> threads = new HashMap<>();
	protected final AtomicInteger i = new AtomicInteger();
	
	public void destroy() {
		for(Thread tht : threads.values()) {
			if(tht!=null)
			if(tht.isAlive()) {
				//tht.stop();
				tht.interrupt();
			}
		}
		threads.clear();
	}
	
	public boolean isAlive(int id) {
		return threads.containsKey(id) && threads.get(id).isAlive();
	}
	
	public Map<Integer, Thread> getThreads(){
		return threads;
	}
	
	public int  incrementAndGet(){
		return i.incrementAndGet();
	}
	
	public void destroy(int id) {
		Thread t = threads.remove(id);
		if(t==null)return;
		//t.stop(); //destroy loops and whole running code
		t.interrupt(); //safe destroy of thread
	}
	
	public int executeWithId(int id, Runnable command) {
		Thread t = new Thread(command, "MultiThread-Worker-"+id);
		threads.put(id, t);
		t.start();
		return id;
	}
	
	public int executeAndGet(Runnable command) {
		int id = i.incrementAndGet();
		return executeWithId(id, command);
	}

	@Override
	public void execute(Runnable command) {
		int id = i.incrementAndGet();
		executeWithId(id, command);
	}
}