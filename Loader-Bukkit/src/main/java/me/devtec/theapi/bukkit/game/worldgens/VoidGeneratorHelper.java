package me.devtec.theapi.bukkit.game.worldgens;

import org.bukkit.generator.ChunkGenerator;

import me.devtec.shared.Ref;

public class VoidGeneratorHelper {
    public static ChunkGenerator get() {
        return Ref.isAtLeast(9, 0) ? new VoidGeneratorModern() : new VoidGeneratorLegacy();
    }
}
