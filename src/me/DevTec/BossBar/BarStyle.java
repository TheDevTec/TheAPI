package me.DevTec.BossBar;

import me.DevTec.NMS.Reflections;

public enum BarStyle {
	NOTCHED_10, NOTCHED_12, NOTCHED_20, NOTCHED_6, PROGRESS;

	public Object toMojang() {
		return Reflections.get(Reflections.getField(Reflections.getNMSClass("BossBattle$BarStyle"), toString()), null);
	}
}
