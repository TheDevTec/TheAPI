package me.devtec.theapi.blocksapi.schematic.construct;

import java.io.Serializable;

import me.devtec.theapi.utils.Position;
import me.devtec.theapi.utils.TheMaterial;

public interface SerializedBlock extends Serializable {
	public default SerializedBlock serialize(Position block) {
		return serialize(block.getType());
	}

	public SerializedBlock serialize(TheMaterial material);
	
	public String getAsString();
	
	public SerializedBlock fromString(String string);
}
