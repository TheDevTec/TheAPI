package me.devtec.theapi.punishmentapi;

import java.util.List;

import me.devtec.theapi.utils.datakeeper.collections.UnsortedList;
import me.devtec.theapi.utils.thapiutils.LoaderClass;

public class BanList {

	public static List<String> getTempIPBanned() {
		return new UnsortedList<>(LoaderClass.data.getKeys("tempbanip"));
	}

	public static List<String> getTempBanned() {
		return new UnsortedList<>(LoaderClass.data.getKeys("tempban"));
	}

	public static List<String> getIPBanned() {
		return new UnsortedList<>(LoaderClass.data.getKeys("banip"));
	}

	public static List<String> getBanned() {
		return new UnsortedList<>(LoaderClass.data.getKeys("ban"));
	}

	public static List<String> getTempIPMuted() {
		return new UnsortedList<>(LoaderClass.data.getKeys("tempmuteip"));
	}

	public static List<String> getTempMuted() {
		return new UnsortedList<>(LoaderClass.data.getKeys("tempmute"));
	}

	public static List<String> getIPMuted() {
		return new UnsortedList<>(LoaderClass.data.getKeys("muteip"));
	}

	public static List<String> getMuted() {
		return new UnsortedList<>(LoaderClass.data.getKeys("mute"));
	}

	public static List<String> getTempIPJailed() {
		return new UnsortedList<>(LoaderClass.data.getKeys("tempjailip"));
	}

	public static List<String> getTempJailed() {
		return new UnsortedList<>(LoaderClass.data.getKeys("tempjail"));
	}

	public static List<String> getIPJailed() {
		return new UnsortedList<>(LoaderClass.data.getKeys("jailip"));
	}

	public static List<String> getJailed() {
		return new UnsortedList<>(LoaderClass.data.getKeys("jail"));
	}

	public static PlayerBanList getBanList(String player) {
		return new PlayerBanList(player);
	}

}
