package me.Straiker123.Utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;

import me.Straiker123.LoaderClass;
import me.Straiker123.TheAPI;

public class Error {
	public static void err(String message, String reason) {
		if(!LoaderClass.config.getConfig().getBoolean("Options.HideErrors"))
		TheAPI.getConsole().sendMessage(TheAPI.colorize("&cTheAPI&7: &cA severe error when &4"+message+"&c, reason: &4"+reason));
		else
			sendRequest("&cTheAPI&7: &cA severe error when &4"+message+"&c, reason: &4"+reason);
	}
	static List<String> list = new ArrayList<String>();
	static int r = -0;
	public static void sendRequest(String s) {
	list.add(s);
	if(r==-0)run();
	}
	 
	private static void run() {
		r=Bukkit.getScheduler().scheduleSyncRepeatingTask(LoaderClass.plugin, new Runnable() {

			@Override
			public void run() {
				if(list.isEmpty()==false) {
					TheAPI.getConsole().sendMessage(TheAPI.colorize(list.get(0)));
				}else {
					Bukkit.getScheduler().cancelTask(r);
					r=-0;
					}
			}
					
		}, 200, 200);
	}
}
