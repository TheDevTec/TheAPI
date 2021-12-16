package me.devtec.theapi.blocksapi.schematic.construct;

import me.devtec.theapi.utils.Position;
import me.devtec.theapi.utils.datakeeper.Data;

public interface Schematic {
	public boolean load();
	
	public Data data();
	
	public void paste(Position stand, boolean pasteEntities, boolean replaceAir, SchematicCallable callable);
	
	public void save(Position fromCopy, Position cornerA, Position cornerB, SchematicSaveCallable callable);
}
