package me.devtec.shared.utils;

import java.util.Iterator;

public class BlockMathIterator implements Iterable<double[]> {
	private final double sizeZ;
	private final double sizeY;
	private final double sizeX;
	private double baseZ;
	private double baseX;
	private double baseY;
	private double x;
	private double y;
	private double z;
	
	public BlockMathIterator(double posX, double posY, double posZ, double posX2, double posY2, double posZ2) {
		baseX = Math.min(posX, posX2);
		baseY = Math.min(posY, posY2);
		baseZ = Math.min(posZ, posZ2);
		sizeX = Math.abs(Math.max(posX, posX2) - baseX) + 1;
		sizeY = Math.abs(Math.max(posY, posY2) - baseY) + 1;
		sizeZ = Math.abs(Math.max(posZ, posZ2) - baseZ) + 1;
	}

	public void reset() {
		x = 0;
		y = 0;
		z = 0;
	}

	public boolean has() {
		return x < sizeX && y < sizeY && z < sizeZ;
	}

	public double[] get() {
		double[] b = new double[] {baseX + x, baseY + y, baseZ + z};
		if (!has())return b;
		if (++x >= sizeX) {
			x = 0;
			if (++y >= sizeY) {
				y = 0;
				++z;
			}
		}
		return b;
	}

	@Override
	public Iterator<double[]> iterator() {
		return new Iterator<double[]>() {
			public boolean hasNext() {
				return has();
			}
			public double[] next() {
				return get();
			}
		};
	}
}