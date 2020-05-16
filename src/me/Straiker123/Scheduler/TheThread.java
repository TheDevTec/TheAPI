package me.Straiker123.Scheduler;

import java.util.concurrent.Executors;

import com.google.common.util.concurrent.ListenableFutureTask;

public class TheThread extends Thread {
	public TheThread(String name) {
	this.setName(name);	
	}
	public void postRunnable(Runnable runnable) {
		ListenableFutureTask.create(Executors.callable(runnable)).run();
	}
}
