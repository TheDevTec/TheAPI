package me.Straiker123.Abstract;

import me.Straiker123.Position;

public interface AbstractSchemate {
	
	public void paste(Position position);
	
	public boolean canBeCancelled();
	
	public void cancel();
}
