package me.DevTec.TheAPI.ParticlesAPI;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.DevTec.TheAPI.Utils.TheMaterial;
import me.DevTec.TheAPI.Utils.Reflections.Ref;

public class ParticleData {
	public static class NoteOptions extends ParticleData {
		private int note;

		public NoteOptions(int note) {
			if (note < 0) {
				throw new IllegalArgumentException("The note value is lower than 0");
			}
			if (note > 24) {
				throw new IllegalArgumentException("The note value is higher than 24");
			}
			this.note = note;
		}

		public int getNote() {
			return note;
		}

		public float getValueX() {
			return note / 24F;
		}

		public float getValueY() {
			return 0;
		}

		public float getValueZ() {
			return 0;
		}
	}

	public static class RedstoneOptions extends ParticleData {
		private float red, green, blue, size;

		public RedstoneOptions(float size, float red, float green, float blue) {
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
			this.size = size;
		}

		public RedstoneOptions(Color color) {
			this(1, color.getRed(), color.getGreen(), color.getBlue());
		}

		public RedstoneOptions(float red, float green, float blue) {
			this(1, red, green, blue);
		}

		public float getRed() {
			return red;
		}

		public float getGreen() {
			return green;
		}

		public float getBlue() {
			return blue;
		}

		public float getValueX() {
			return red / 255.0F;
		}

		public float getValueY() {
			return green / 255.0F;
		}

		public float getValueZ() {
			return blue / 255.0F;
		}

		public float getSize() {
			return size;
		}
	}

	public static class ItemOptions extends ParticleData {
		private ItemStack item;
		private int[] packetData;

		public ItemOptions(ItemStack stack) {
			this(new TheMaterial(stack));
		}

		public ItemOptions(Material material, byte data) {
			this(new TheMaterial(material, data));
		}

		public ItemOptions(TheMaterial material) {
			this.item = material.toItemStack();
			packetData = new int[] {
					(int) Ref.invokeNulled(Ref.nms("Block"), "getCombinedId", material.getIBlockData()) };
		}

		public ItemStack getItem() {
			return item;
		}

		public int[] getPacketData() {
			return packetData;
		}
	}

	public static class BlockOptions extends ParticleData {
		private TheMaterial material;
		private int[] packetData;

		public BlockOptions(TheMaterial material) {
			this.material = material;
			try {
				this.packetData = new int[] { material.getType().getId(), material.getData() };
			} catch (Exception err) {
				// not supported for 1.13+
			}
		}

		public BlockOptions(Material material, byte data) {
			this(new TheMaterial(material, data));
		}

		public BlockOptions(ItemStack stack) {
			this(new TheMaterial(stack));
		}

		public TheMaterial getType() {
			return material;
		}

		public int[] getPacketData() {
			return packetData;
		}

		public String getPacketDataString() {
			return "_" + packetData[0] + "_" + packetData[1];
		}
	}
}
