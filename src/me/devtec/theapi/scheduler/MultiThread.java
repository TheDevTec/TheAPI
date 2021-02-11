package me.devtec.theapi.scheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
public class MultiThread {
	private LinkedList<Thread> queue = new LinkedList<>();
	private Map<Integer, Thread> threads = new HashMap<>();
	private int i;
	
	public MultiThread() {
		new Thread(new Runnable() {
			public void run() {
				while(queue!=null) {
					if(queue.isEmpty()) {
						try {
							Thread.sleep(20);
						} catch (Exception e) {
						}
						continue;
					}
					queue.poll().start();
					try {
						Thread.sleep(1);
					} catch (Exception e) {
					}
				}
			}
		}, "MultiThread-Processor").start();
	}
	
	public void interrupt() {
		queue=null;
		for(Entry<Integer, Thread> tht : threads.entrySet()) {
			tht.getValue().stop();
			tht.getValue().interrupt();
		}
		threads=null;
	}
	
	public void destroyThreads() {
		queue.clear();
		List<Thread> clone = new ArrayList<>(threads.values());
		threads.clear();
		for(Thread tht : clone) {
			if(tht.isAlive()) {
				tht.stop();
				tht.interrupt();
			}
		}
	}
	
	public synchronized int getNextId() {
		return i++;
	}
	
	public synchronized int execute(int id, Runnable thread) {
		Thread t = new Thread(thread, "MultiThread-Worker-"+id);
		threads.put(id, t);
		queue.add(t);
		return id;
	}
	
	public synchronized int execute(Runnable thread) {
		int id = i++;
		Thread t = new Thread(thread, "MultiThread-Worker-"+id);
		threads.put(id, t);
		queue.add(t);
		return id;
	}
	
	public Set<Integer> getThreads(){
		return threads.keySet();
	}
	
	public void destroyThread(int id) {
		Thread t = threads.remove(id);
		queue.remove(t);
		t.stop();
		t.interrupt();
	}
}