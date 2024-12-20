package me.devtec.theapi.bukkit.game.particles;

import java.util.Arrays;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import me.devtec.shared.Ref;
import me.devtec.theapi.bukkit.game.BlockDataStorage;
import me.devtec.theapi.bukkit.xseries.XMaterial;

public class ParticleData {

    public float getValueX() {
        return 0;
    }

    public float getValueY() {
        return 0;
    }

    public float getValueZ() {
        return 0;
    }

    @Getter
    public static class NoteOptions extends ParticleData {
        private final int note;

        public NoteOptions(int note) {
            if (note < 0) {
				throw new IllegalArgumentException("The note value is lower than 0");
			}
            if (note > 24) {
				throw new IllegalArgumentException("The note value is higher than 24");
			}
            this.note = note;
        }

        @Override
        public float getValueX() {
            return note / 24F;
        }

        @Override
        public int hashCode() {
            int hash = 1;
            return hash * 11 + note;
        }
    }

    @Getter
    public static class RedstoneOptions extends ParticleData {
        private final float red;
        private final float green;
        private final float blue;
        private final float size;

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

        @Override
        public float getValueX() {
            return red / 255.0F;
        }

        @Override
        public float getValueY() {
            return green / 255.0F;
        }

        @Override
        public float getValueZ() {
            return blue / 255.0F;
        }

        @Override
        public int hashCode() {
            int hash = 1;
            hash = hash * 11 + Float.floatToIntBits(red);
            hash = hash * 11 + Float.floatToIntBits(green);
            hash = hash * 11 + Float.floatToIntBits(blue);
            return hash * 11 + Float.floatToIntBits(size);
        }
    }

    public static class ItemOptions extends ParticleData {
        @Getter
        private final ItemStack item;
        private int[] packetData;

        public ItemOptions(ItemStack stack) {
            item = stack;
            if (Ref.isOlderThan(13)) {
				try {
                    packetData = new int[]{BlockDataStorage.fromItemStack(stack).getCombinedId()};
                } catch (Exception err) {
                    packetData = new int[]{0};
                }
			}
        }

        public ItemOptions(Material material) {
            this(new BlockDataStorage(material));
        }

        public ItemOptions(Material material, byte data) {
            this(new BlockDataStorage(material, data));
        }

        public ItemOptions(BlockDataStorage material) {
            item = material.toItemStack();
            if (Ref.isOlderThan(13)) {
				try {
                    packetData = new int[]{material.getCombinedId()};
                } catch (Exception err) {
                    packetData = new int[]{0};
                }
			}
        }

        @Deprecated
        /*
         * @apiNote 1.12.2 and older only.
         * @return packedData of item
         **/
        public int[] getPacketData() {
            return packetData;
        }

        @Override
        public int hashCode() {
            int hash = 1;
            hash = hash * 11 + item.hashCode();
            return hash * 11 + Arrays.hashCode(packetData);
        }
    }

    public static class BlockOptions extends ParticleData {
        private final BlockDataStorage material;
        private int[] packetData;

        public BlockOptions(BlockDataStorage material) {
            this.material = material;
            if (Ref.isOlderThan(13)) {
				try {
                    packetData = new int[]{XMaterial.matchXMaterial(material.getType()).getId(), material.getItemData()};
                } catch (Exception err) {
                    packetData = new int[]{0, 0};
                }
			}
        }

        public BlockOptions(Material material) {
            this(new BlockDataStorage(material));
        }

        public BlockOptions(Material material, byte data) {
            this(new BlockDataStorage(material, data));
        }

        public BlockOptions(ItemStack stack) {
            this(BlockDataStorage.fromItemStack(stack));
        }

        public BlockDataStorage getType() {
            return material;
        }

        @Deprecated
        /*
          @apiNote 1.12.2 and older only.
         * @return packedData of item
         */
        public int[] getPacketData() {
            return packetData;
        }

        public String getPacketDataString() {
            return "_" + packetData[0] + "_" + packetData[1];
        }

        @Override
        public int hashCode() {
            int hash = 1;
            hash = hash * 11 + material.hashCode();
            return hash * 11 + Arrays.hashCode(packetData);
        }
    }
}
