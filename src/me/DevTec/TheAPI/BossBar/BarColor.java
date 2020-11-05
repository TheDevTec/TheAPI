package me.DevTec.TheAPI.BossBar;

import me.DevTec.TheAPI.Utils.Reflections.Reflections;

public enum BarColor {
	GREEN, BLUE, PINK, PURPLE, RED, WHITE, YELLOW;

	public Object toMojang() {
		return Reflections.get(Reflections.getField(Reflections.getNMSClass("BossBattle$BarColor"), toString()), null);
	}
}
