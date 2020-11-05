package me.DevTec.TheAPI.BlocksAPI;

import me.DevTec.TheAPI.BlocksAPI.Schemate.SimpleSave;
import me.DevTec.TheAPI.Utils.Position;
import me.DevTec.TheAPI.Utils.TheMaterial;

public interface SchemBlock {

	public boolean set(Schema schem, Position pos, TheMaterial type, SimpleSave save);
}
