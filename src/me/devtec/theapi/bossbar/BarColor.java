package me.devtec.theapi.bossbar;

import me.devtec.theapi.utils.reflections.Ref;

public enum BarColor {
	GREEN, BLUE, PINK, PURPLE, RED, WHITE, YELLOW;

	public Object toMojang() {
		return Ref.getNulled(Ref.field(Ref.nms("BossBattle$BarColor"), name()));
	}
}
