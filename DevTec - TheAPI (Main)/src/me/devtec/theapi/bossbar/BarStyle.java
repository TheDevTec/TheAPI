package me.devtec.theapi.bossbar;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.utils.reflections.Ref;

public enum BarStyle {
	NOTCHED_10, NOTCHED_12, NOTCHED_20, NOTCHED_6, PROGRESS;

	public Object toMojang() {
		return Ref.getNulled(Ref.field(Ref.nmsOrOld("world.BossBattle$BarStyle","BossBattle$BarStyle"), convertNew(name())));
	}

	String convertNew(String name) {
		if(TheAPI.isNewerThan(16)) {
			switch(name) {
			case "NOTCHED_6":
				return "b";
			case "NOTCHED_10":
				return "c";
			case "NOTCHED_12":
				return "d";
			case "NOTCHED_20":
				return "e";
			case "PROGRESS":
				return "a";
			}
		}
		return name;
	}
}
