package me.Straiker123.Scheduler;

public class SchedulerController {
	public static void prepare(Task t) {
		if (check(t))
			t.run();
	}

	public static boolean check(Task t) {
		if (t == null)
			return false;
		if (t.isCancelled())
			return false;
		return true;
	}

	public static boolean check(Runnable t) {
		if (t == null)
			return false;
		return true;
	}

}
