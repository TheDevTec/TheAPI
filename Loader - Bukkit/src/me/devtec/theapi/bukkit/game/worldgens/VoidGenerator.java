package me.devtec.theapi.bukkit.game.worldgens;

import org.bukkit.generator.ChunkGenerator;

import me.devtec.shared.Ref;

public class VoidGenerator {
	public static ChunkGenerator get() {
		return Ref.isNewerThan(8) ? new VoidGeneratorModern() : new voidGeneratorLegacy();
	}
}
