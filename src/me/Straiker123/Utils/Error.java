package me.Straiker123.Utils;
import java.util.ArrayList;
import java.util.List;

import me.Straiker123.LoaderClass;
import me.Straiker123.TheAPI;
import me.Straiker123.TheRunnable;

public class Error {
	public static void err(String message, String reason) {
		if(!LoaderClass.config.getBoolean("Options.HideErrors"))
		TheAPI.msg("&cTheAPI&7: &cA severe error when &4"+message+"&c, reason: &4"+reason, TheAPI.getConsole());
		else
			sendRequest("&cTheAPI&7: &cA severe error when &4"+message+"&c, reason: &4"+reason);
	}
	static List<String> list = new ArrayList<String>();
	public static void sendRequest(String s) {
	list.add(s);
	if(!run)run();
	}
	static boolean run;
	private static void run() {
		run=true;
		TheRunnable r = TheAPI.getTheRunnable();
		r.runRepeating( new Runnable() {
			@Override
			public void run() {
				if(!list.isEmpty()) {
					TheAPI.msg(list.get(0),TheAPI.getConsole());
				}else {
					r.cancel();
					}
			}
		}, 200);
	}
}
