package me.devtec.theapi.blocksapi.schematic.construct;

import me.devtec.theapi.blocksapi.schematic.storage.SchematicData;
import me.devtec.theapi.utils.Position;

public interface Schematic {
	public boolean load();
	
	public SchematicData data();
	
	public void paste(Position stand, boolean pasteEntities, boolean replaceAir, SchematicCallable callable);
	
	public void save(Position fromCopy, Position cornerA, Position cornerB, SchematicSaveCallable callable);
}
