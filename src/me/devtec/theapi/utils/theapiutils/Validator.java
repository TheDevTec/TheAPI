package me.devtec.theapi.utils.theapiutils;

import org.bukkit.Bukkit;

public class Validator {
	public static final boolean hideErrors = LoaderClass.config.getBoolean("Options.HideErrors");

	public static void validate(boolean question, String error) {
		if (question)
			try {
				throw new Exception(error);
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	public static void send(String error) {
		if (!hideErrors)
			try {
				throw new Exception(error);
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	public static void send(String error, Throwable err) {
		if (!hideErrors) {
			Bukkit.getLogger().severe("TheAPI Exception: " + error);
			err.printStackTrace();
		}
	}
}
