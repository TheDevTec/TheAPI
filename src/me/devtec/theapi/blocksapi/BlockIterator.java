package me.devtec.theapi.blocksapi;

import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.World;

import me.devtec.theapi.utils.BlockMathIterator;
import me.devtec.theapi.utils.Position;

public class BlockIterator implements Iterable<Position> {
	private final BlockMathIterator iterator;
	private final String w;

	public BlockIterator(Position a, Position b) {
		w = a.getWorldName();
		iterator=new BlockMathIterator(a,b);
	}

	public BlockIterator(Location a, Location b) {
		w = a.getWorld().getName();
		iterator=new BlockMathIterator(a,b);
	}

	public BlockIterator(String world, int posX, int posY, int posZ, int posX2, int posY2, int posZ2) {
		w = world;
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

	public Position get() {
		int[] get = iterator.get();
		return new Position(w, get[0], get[1], get[2]);
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
		};
	}
}
