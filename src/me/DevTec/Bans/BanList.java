package me.DevTec.Bans;

import java.util.List;

import com.google.common.collect.Lists;

import me.DevTec.Other.LoaderClass;

public class BanList {

	public static List<String> getTempIPBanned() {
		return Lists.newArrayList(LoaderClass.data.getKeys("tempbanip"));
	}

	public static List<String> getTempBanned() {
		return Lists.newArrayList(LoaderClass.data.getKeys("tempban"));
	}

	public static List<String> getIPBanned() {
		return Lists.newArrayList(LoaderClass.data.getKeys("banip"));
	}

	public static List<String> getBanned() {
		return Lists.newArrayList(LoaderClass.data.getKeys("ban"));
	}
	
	public static List<String> getTempIPMuted() {
		return Lists.newArrayList(LoaderClass.data.getKeys("tempmuteip"));
	}
	
	public static List<String> getTempMuted() {
		return Lists.newArrayList(LoaderClass.data.getKeys("tempmute"));
	}
	
	public static List<String> getIPMuted() {
		return Lists.newArrayList(LoaderClass.data.getKeys("muteip"));
	}
	
	public static List<String> getMuted() {
		return Lists.newArrayList(LoaderClass.data.getKeys("mute"));
	}

	public static List<String> getTempIPJailed() {
		return Lists.newArrayList(LoaderClass.data.getKeys("tempjailip"));
	}

	public static List<String> getTempJailed() {
		return Lists.newArrayList(LoaderClass.data.getKeys("tempjail"));
	}

	public static List<String> getIPJailed() {
		return Lists.newArrayList(LoaderClass.data.getKeys("jailip"));
	}

	public static List<String> getJailed() {
		return Lists.newArrayList(LoaderClass.data.getKeys("jail"));
	}

	public static PlayerBanList getBanList(String player) {
		return new PlayerBanList(player);
	}

}
