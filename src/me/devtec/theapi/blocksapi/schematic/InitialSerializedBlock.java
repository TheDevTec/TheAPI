package me.devtec.theapi.blocksapi.schematic;

import me.devtec.theapi.blocksapi.schematic.construct.SerializedBlock;
import me.devtec.theapi.utils.TheMaterial;

public class InitialSerializedBlock implements SerializedBlock {
	private static final long serialVersionUID = 98117975197494498L;
	
	protected TheMaterial material;
	
	@Override
	public SerializedBlock serialize(TheMaterial material) {
		this.material=material;
		return this;
	}

	@Override
	public String getAsString() {
		return material.toString();
	}
	
	public TheMaterial getType() {
		return material;
	}

	@Override
	public SerializedBlock fromString(String string) {
		material=TheMaterial.fromString(string);
		return this;
	}
	
}
