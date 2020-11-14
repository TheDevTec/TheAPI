package me.DevTec.TheAPI.BossBar;

import me.DevTec.TheAPI.Utils.Reflections.Ref;

public enum BarStyle {
	NOTCHED_10, NOTCHED_12, NOTCHED_20, NOTCHED_6, PROGRESS;

	public Object toMojang() {
		return Ref.getNulled(Ref.field(Ref.nms("BossBattle$BarStyle"), name()));
	}
}
