package me.devtec.theapi.bossbar;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.utils.reflections.Ref;

public enum BarColor {
	GREEN, BLUE, PINK, PURPLE, RED, WHITE, YELLOW;

	public Object toMojang() {
		return Ref.getNulled(Ref.field(Ref.nmsOrOld("world.BossBattle$BarColor","BossBattle$BarColor"), convertNew(name())));
	}

	String convertNew(String name) {
		if(TheAPI.isNewerThan(16)) {
			switch(name) {
			case "GREEN":
				return "d";
			case "BLUE":
				return "b";
			case "PINK":
				return "a";
			case "PURPLE":
				return "f";
			case "RED":
				return "c";
			case "WHITE":
				return "g";
			case "YELLOW":
				return "e";
			}
		}
		return name;
	}
}
