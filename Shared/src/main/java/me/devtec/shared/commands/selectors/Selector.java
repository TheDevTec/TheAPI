package me.devtec.shared.commands.selectors;

public enum Selector {
	// EVERYWHERE
	NUMBER, INTEGER, BOOLEAN,
	// BUKKIT
	WORLD, ENTITY_TYPE, BIOME_TYPE,
	// MC
	PLAYER, ENTITY_SELECTOR, // *, @a, @e, @r, @p, @s or PLAYER
	// PROXY
	SERVER
}
