package me.devtec.shared.utility;

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
		this.baseX = Math.min(posX, posX2);
		this.baseY = Math.min(posY, posY2);
		this.baseZ = Math.min(posZ, posZ2);
		this.sizeX = Math.abs(Math.max(posX, posX2) - this.baseX) + 1;
		this.sizeY = Math.abs(Math.max(posY, posY2) - this.baseY) + 1;
		this.sizeZ = Math.abs(Math.max(posZ, posZ2) - this.baseZ) + 1;
	}

	public void reset() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}

	public boolean has() {
		return this.x < this.sizeX && this.y < this.sizeY && this.z < this.sizeZ;
	}

	public double[] get() {
		double[] b = { this.baseX + this.x, this.baseY + this.y, this.baseZ + this.z };
		if (!this.has())
			return b;
		if (++this.x >= this.sizeX) {
			this.x = 0;
			if (++this.y >= this.sizeY) {
				this.y = 0;
				++this.z;
			}
		}
		return b;
	}

	@Override
	public Iterator<double[]> iterator() {
		return new Iterator<double[]>() {
			@Override
			public boolean hasNext() {
				return BlockMathIterator.this.has();
			}

			@Override
			public double[] next() {
				return BlockMathIterator.this.get();
			}
		};
	}
}