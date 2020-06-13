package me.DevTec.NMS;

import org.bukkit.Material;

public class BlockData extends ParticleData {
	public BlockData(Material material, byte data) throws IllegalArgumentException {
		super(material, data);
		if (!material.isBlock()) {
			throw new IllegalArgumentException("The material is not a block");
		}
	}
}
