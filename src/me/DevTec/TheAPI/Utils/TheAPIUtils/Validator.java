package me.DevTec.TheAPI.Utils.TheAPIUtils;

public class Validator {
	public static void validate(boolean question, String error) {
		if(question) {
			send(error);
			return;
		}
	}

	public static void send(String error) {
			if (!LoaderClass.config.getBoolean("Options.HideErrors"))
				try {
					throw new Exception(error);
				} catch (Exception e) {
					e.printStackTrace();
				}
	}

	public static void send(String error, Throwable err) {
			if (!LoaderClass.config.getBoolean("Options.HideErrors"))
				try {
					throw new Exception(error, err);
				} catch (Exception e) {
					e.printStackTrace();
				}
	}
}
