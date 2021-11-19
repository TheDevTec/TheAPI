package me.devtec.theapi.blocksapi;

import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.World;

import me.devtec.theapi.utils.BlockMathIterator;
import me.devtec.theapi.utils.Position;

public class BlockIterator implements Iterable<Position> {
	private final BlockMathIterator iterator;

	public BlockIterator(Position a, Position b) {
		copy = new Position(a.getWorldName(), 0, 0 ,0);
		iterator=new BlockMathIterator(a,b);
	}

	public BlockIterator(Location a, Location b) {
		copy = new Position(a.getWorld().getName(), 0, 0 ,0);
		iterator=new BlockMathIterator(a,b);
	}

	public BlockIterator(String world, int posX, int posY, int posZ, int posX2, int posY2, int posZ2) {
		copy = new Position(world, 0, 0 ,0);
		iterator=new BlockMathIterator(posX, posY, posZ, posX2, posY2, posZ2);
	}

	public BlockIterator(World world, int posX, int posY, int posZ, int posX2, int posY2, int posZ2) {
		this(world.getName(), posX, posY, posZ, posX2, posY2, posZ2);
	}

	public void reset() {
		iterator.reset();
	}

	public boolean has() {
		return iterator.has();
	}

	final Position copy;
	
	public Position get() {
		double[] get = iterator.get();
		return copy.setX(get[0]).setY(get[1]).setZ(get[2]);
	}

	@Override
	public Iterator<Position> iterator() {
		return new Iterator<Position>() {
			public boolean hasNext() {
				return has();
			}
			public Position next() {
				return get();
			}
			public void remove() {
				
			}
		};
	}
}
