package me.devtec.theapi.bossbar;

import me.devtec.theapi.utils.reflections.Ref;

public enum BarStyle {
	NOTCHED_10, NOTCHED_12, NOTCHED_20, NOTCHED_6, PROGRESS;

	public Object toMojang() {
		return Ref.getNulled(Ref.field(Ref.nms("BossBattle$BarStyle"), name()));
	}
}
