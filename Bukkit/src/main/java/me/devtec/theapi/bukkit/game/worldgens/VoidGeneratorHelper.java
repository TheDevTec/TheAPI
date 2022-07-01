package me.devtec.theapi.bukkit.game.worldgens;

import me.devtec.shared.Ref;
import org.bukkit.generator.ChunkGenerator;

public class VoidGeneratorHelper {
	public static ChunkGenerator get() {
		return Ref.isNewerThan(8) ? new VoidGeneratorModern() : new VoidGeneratorLegacy();
	}
}
