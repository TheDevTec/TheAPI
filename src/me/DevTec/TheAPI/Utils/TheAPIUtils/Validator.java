package me.DevTec.TheAPI.Utils.TheAPIUtils;

import org.bukkit.Bukkit;

public class Validator {
	public static boolean hideErrors = LoaderClass.config.getBoolean("Options.HideErrors");

	public static void validate(boolean question, String error) {
		if (question)
			send(error);
	}

	public static void send(String error) {
		if (!hideErrors)
			new Exception(error).printStackTrace();
	}

	public static void send(String error, Throwable err) {
		if (!hideErrors) {
			Bukkit.getLogger().warning("TheAPI Exception: "+error);
			err.printStackTrace();
		}
	}
}
