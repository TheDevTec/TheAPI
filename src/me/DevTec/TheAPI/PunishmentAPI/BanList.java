package me.DevTec.TheAPI.PunishmentAPI;

import java.util.ArrayList;
import java.util.List;

import me.DevTec.TheAPI.Utils.TheAPIUtils.LoaderClass;

public class BanList {

	public static List<String> getTempIPBanned() {
		return new ArrayList<>(LoaderClass.data.getKeys("tempbanip"));
	}

	public static List<String> getTempBanned() {
		return new ArrayList<>(LoaderClass.data.getKeys("tempban"));
	}

	public static List<String> getIPBanned() {
		return new ArrayList<>(LoaderClass.data.getKeys("banip"));
	}

	public static List<String> getBanned() {
		return new ArrayList<>(LoaderClass.data.getKeys("ban"));
	}

	public static List<String> getTempIPMuted() {
		return new ArrayList<>(LoaderClass.data.getKeys("tempmuteip"));
	}

	public static List<String> getTempMuted() {
		return new ArrayList<>(LoaderClass.data.getKeys("tempmute"));
	}

	public static List<String> getIPMuted() {
		return new ArrayList<>(LoaderClass.data.getKeys("muteip"));
	}

	public static List<String> getMuted() {
		return new ArrayList<>(LoaderClass.data.getKeys("mute"));
	}

	public static List<String> getTempIPJailed() {
		return new ArrayList<>(LoaderClass.data.getKeys("tempjailip"));
	}

	public static List<String> getTempJailed() {
		return new ArrayList<>(LoaderClass.data.getKeys("tempjail"));
	}

	public static List<String> getIPJailed() {
		return new ArrayList<>(LoaderClass.data.getKeys("jailip"));
	}

	public static List<String> getJailed() {
		return new ArrayList<>(LoaderClass.data.getKeys("jail"));
	}

	public static PlayerBanList getBanList(String player) {
		return new PlayerBanList(player);
	}

}
