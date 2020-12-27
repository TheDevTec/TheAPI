package me.devtec.theapi.blocksapi;

import me.devtec.theapi.blocksapi.Schemate.SimpleSave;
import me.devtec.theapi.utils.Position;
import me.devtec.theapi.utils.TheMaterial;

public interface SchemBlock {

	public boolean set(Schema schem, Position pos, TheMaterial type, SimpleSave save);
}
