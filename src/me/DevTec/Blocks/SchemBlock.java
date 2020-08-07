package me.DevTec.Blocks;

import me.DevTec.Blocks.Schemate.SimpleSave;
import me.DevTec.Other.Position;
import me.DevTec.Other.TheMaterial;

public interface SchemBlock {
	
	public boolean set(Schema schem, Position pos, TheMaterial type, SimpleSave save);
}
