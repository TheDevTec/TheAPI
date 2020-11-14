package me.DevTec.TheAPI.BossBar;

import me.DevTec.TheAPI.Utils.Reflections.Ref;

public enum BarColor {
	GREEN, BLUE, PINK, PURPLE, RED, WHITE, YELLOW;

	public Object toMojang() {
		return Ref.getNulled(Ref.field(Ref.nms("BossBattle$BarColor"), name()));
	}
}
