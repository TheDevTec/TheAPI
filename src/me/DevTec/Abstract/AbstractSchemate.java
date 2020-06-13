package me.DevTec.Abstract;

import me.DevTec.Other.Position;

public interface AbstractSchemate {
	
	public void paste(Position position);
	
	public boolean canBeCancelled();
	
	public void cancel();
}
