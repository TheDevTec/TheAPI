package me.Straiker123;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

public class BanList {
	private static final ConfigAPI c = LoaderClass.data;

	public List<String> getIPBanned() {
		ArrayList<String> list = Lists.newArrayList();
		for (String s : c.getKeys("banip"))
			list.add(s);
		return list;
	}

	public List<String> getBanned() {
		ArrayList<String> list = Lists.newArrayList();
		for (String s : c.getKeys("ban"))
			list.add(s);
		return list;
	}

	public List<String> getMuted() {
		ArrayList<String> list = Lists.newArrayList();
		for (String s : c.getKeys("mute"))
			list.add(s);
		return list;
	}

	public List<String> getJailed() {
		ArrayList<String> list = Lists.newArrayList();
		for (String s : c.getKeys("jail"))
			list.add(s);
		return list;
	}

	public PlayerBanList getBanList(String player) {
		return new PlayerBanList(player);
	}

}
