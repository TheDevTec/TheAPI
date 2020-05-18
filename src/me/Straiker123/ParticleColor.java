package me.Straiker123;

import org.bukkit.Color;

public class ParticleColor {
	private int red, green, blue;

	public ParticleColor(int red, int green, int blue) {
		if (red < 0) {
			throw new IllegalArgumentException("The red value is lower than 0");
		}
		if (red > 255) {
			throw new IllegalArgumentException("The red value is higher than 255");
		}
		this.red = red;
		if (green < 0) {
			throw new IllegalArgumentException("The green value is lower than 0");
		}
		if (green > 255) {
			throw new IllegalArgumentException("The green value is higher than 255");
		}
		this.green = green;
		if (blue < 0) {
			throw new IllegalArgumentException("The blue value is lower than 0");
		}
		if (blue > 255) {
			throw new IllegalArgumentException("The blue value is higher than 255");
		}
		this.blue = blue;
	}

	public ParticleColor(Color color) {
		this(color.getRed(), color.getGreen(), color.getBlue());
	}

	public int getRed() {
		return red;
	}

	public int getGreen() {
		return green;
	}

	public int getBlue() {
		return blue;
	}

	public float getValueX() {
		return red / 255F;
	}

	public float getValueY() {
		return green / 255F;
	}

	public float getValueZ() {
		return blue / 255F;
	}
}
