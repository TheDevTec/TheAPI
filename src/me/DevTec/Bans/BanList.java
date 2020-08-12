package me.DevTec.Bans;

import java.util.List;

import com.google.common.collect.Lists;

import me.DevTec.Other.LoaderClass;

public class BanList {

	public List<String> getTempIPBanned() {
		return Lists.newArrayList(LoaderClass.data.getKeys("tempbanip"));
	}

	public List<String> getTempBanned() {
		return Lists.newArrayList(LoaderClass.data.getKeys("tempban"));
	}

	public List<String> getIPBanned() {
		return Lists.newArrayList(LoaderClass.data.getKeys("banip"));
	}

	public List<String> getBanned() {
		return Lists.newArrayList(LoaderClass.data.getKeys("ban"));
	}
	
	public List<String> getTempIPMuted() {
		return Lists.newArrayList(LoaderClass.data.getKeys("tempmuteip"));
	}
	
	public List<String> getTempMuted() {
		return Lists.newArrayList(LoaderClass.data.getKeys("tempmute"));
	}
	
	public List<String> getIPMuted() {
		return Lists.newArrayList(LoaderClass.data.getKeys("muteip"));
	}
	
	public List<String> getMuted() {
		return Lists.newArrayList(LoaderClass.data.getKeys("mute"));
	}

	public List<String> getTempIPJailed() {
		return Lists.newArrayList(LoaderClass.data.getKeys("tempjailip"));
	}

	public List<String> getTempJailed() {
		return Lists.newArrayList(LoaderClass.data.getKeys("tempjail"));
	}

	public List<String> getIPJailed() {
		return Lists.newArrayList(LoaderClass.data.getKeys("jailip"));
	}

	public List<String> getJailed() {
		return Lists.newArrayList(LoaderClass.data.getKeys("jail"));
	}

	public PlayerBanList getBanList(String player) {
		return new PlayerBanList(player);
	}

}
