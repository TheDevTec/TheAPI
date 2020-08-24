package me.DevTec.TheAPI.Utils.TheAPIUtils;

import java.util.ArrayList;
import java.util.List;

import me.DevTec.TheAPI.TheAPI;
import me.DevTec.TheAPI.Scheduler.Tasker;

public class Error {
	public static void err(String message, String reason) {
		if (!LoaderClass.config.getBoolean("Options.HideErrors"))
			TheAPI.msg("&cTheAPI&7: &cA severe error when &4" + message + "&c, reason: &4" + reason,
					TheAPI.getConsole());
		else
			sendRequest("&cTheAPI&7: &cA severe error when &4" + message + "&c, reason: &4" + reason);
	}

	static List<String> list = new ArrayList<String>();

	public static void sendRequest(String s) {
		list.add(s);
		if (!run)
			run();
	}

	static boolean run;

	private static void run() {
		run = true;
		new Tasker() {
			@Override
			public void run() {
				if (!list.isEmpty()) {
					TheAPI.msg(list.get(0), TheAPI.getConsole());
				} else {
					cancel();
				}
			}
		}.repeating(0, 200);
	}
}
