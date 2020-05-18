package me.Straiker123;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.bukkit.DyeColor;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Dropper;
import org.bukkit.block.Hopper;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class BlockSave {
	private boolean isSign, isInvBlock, isCmd;
	private Position loc;
	private String[] lines;
	private ItemStack[] inv;
	private String cmd, cmdname;
	private TheMaterial type;
	private DyeColor color;
	private Biome b;
	private BlockFace f;

	public BlockSave(Position pos) {
		Block b = pos.getBlock();
		f = b.getFace(b);
		if (b.getType().name().contains("CHEST")) {
			isInvBlock = true;
			inv = ((Chest) b.getState()).getBlockInventory().getContents();
		}
		if (b.getType().name().contains("SHULKER_BOX")) {
			inv = ((ShulkerBox) b.getState()).getInventory().getContents();
		}
		if (b.getType().name().equals("DROPPER")) {
			isInvBlock = true;
			inv = ((Dropper) b.getState()).getInventory().getContents();
		}
		if (b.getType().name().equals("DISPENSER")) {
			isInvBlock = true;
			inv = ((Dispenser) b.getState()).getInventory().getContents();
		}
		if (b.getType().name().equals("HOPPER")) {
			isInvBlock = true;
			inv = ((Hopper) b.getState()).getInventory().getContents();
		}
		if (b.getType().name().contains("SIGN")) {
			isSign = true;
			Sign c = (Sign) b.getState();
			lines = c.getLines();
			try {
				color = c.getColor();
			} catch (Exception | NoSuchMethodError e) {

			}
		}
		if (b.getType().name().contains("COMMAND")) {
			isCmd = true;
			CommandBlock c = (CommandBlock) b.getState();
			cmd = c.getCommand();
			cmdname = c.getName();
		}
		loc = pos;
		this.b = pos.getBiome();
		type = loc.getType();
	}

	// sign
	public BlockSave(Position pos, Biome biome, BlockFace face, TheMaterial material, DyeColor color, String[] lines) {
		loc = pos;
		this.b = biome;
		f = face;
		type = material;
		this.lines = lines;
		this.color = color;
		isSign = true;
	}

	// other
	public BlockSave(Position pos, Biome biome, BlockFace face, TheMaterial material, ItemStack[] inv) {
		loc = pos;
		this.b = biome;
		f = face;
		type = material;
		this.inv = inv;
		isInvBlock = true;
	}

	// cmd
	public BlockSave(Position pos, Biome biome, BlockFace face, TheMaterial material, String cmd, String cmdname) {
		loc = pos;
		this.b = biome;
		f = face;
		type = material;
		isCmd = true;
	}

	// normal
	public BlockSave(Position pos, Biome biome, BlockFace face, TheMaterial material) {
		loc = pos;
		this.b = biome;
		f = face;
		type = material;
	}

	public BlockFace getFace() {
		return f;
	}

	public ItemStack[] getBlockInventory() { // shulkerbox, chest..
		return inv;
	}

	public static ItemStack[] getBlockInventoryFromString(String s) {
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(s));
			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
			ItemStack[] items = new ItemStack[dataInput.readInt()];
			for (int i = 0; i < items.length; i++) {
				items[i] = (ItemStack) dataInput.readObject();
			}
			dataInput.close();
			return items;
		} catch (Exception e) {
			return null;
		}
	}

	public String getBlockInventoryAsString() {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
			dataOutput.writeInt(inv.length);
			for (int i = 0; i < inv.length; i++) {
				dataOutput.writeObject(inv[i]);
			}
			dataOutput.close();
			return Base64Coder.encodeLines(outputStream.toByteArray());
		} catch (Exception err) {
			return null;
		}
	}

	public String getCommand() {
		return cmd;
	}

	public String getCommandBlockName() {
		return cmdname;
	}

	public DyeColor getColor() { // shulkerbox & sign
		return color;
	}

	public String[] getSignLines() { // sign
		return lines;
	}

	public String getSignLinesAsString() {
		return new StringUtils().join(lines, " ");
	}

	public static String[] getSignLinesFromString(String s) {
		return s.split(" ");
	}

	public Position getLocation() {
		return loc;
	}

	public World getWorld() {
		return loc.getWorld();
	}

	public Biome getBiome() {
		return b;
	}

	public TheMaterial getMaterial() {
		return type;
	}

	public static BlockSave fromString(String stored) {
		if (stored.startsWith("[BlockSave:")) {
			if (stored.startsWith("[BlockSave:Sign/!/")) {
				stored = stored.replaceFirst("[BlockSave:Sign/!/", "").substring(0, stored.length() - 1);
				String[] s = stored.split("/!/");
				return new BlockSave(Position.fromString(s[0]), Biome.valueOf(s[1]), BlockFace.valueOf(s[2]),
						TheMaterial.fromString(s[3]), DyeColor.valueOf(s[4]), getSignLinesFromString(s[5]));
			}
			if (stored.startsWith("[BlockSave:InventoryBlock/!/")) {
				stored = stored.replaceFirst("[BlockSave:InventoryBlock/!/", "").substring(0, stored.length() - 1);
				String[] s = stored.split("/!/");
				return new BlockSave(Position.fromString(s[0]), Biome.valueOf(s[1]), BlockFace.valueOf(s[2]),
						TheMaterial.fromString(s[3]), getBlockInventoryFromString(s[4]));
			}
			if (stored.startsWith("[BlockSave:CommandBlock/!/")) {
				stored = stored.replaceFirst("[BlockSave:CommandBlock/!/", "").substring(0, stored.length() - 1);
				String[] s = stored.split("/!/");
				return new BlockSave(Position.fromString(s[0]), Biome.valueOf(s[1]), BlockFace.valueOf(s[2]),
						TheMaterial.fromString(s[3]), s[4], s[5]);
			}
			if (stored.startsWith("[BlockSave:Block/!/")) {
				stored = stored.replaceFirst("[BlockSave:Block/!/", "").substring(0, stored.length() - 1);
				String[] s = stored.split("/!/");
				return new BlockSave(Position.fromString(s[0]), Biome.valueOf(s[1]), BlockFace.valueOf(s[2]),
						TheMaterial.fromString(s[3]));
			}
			return null;
		}
		return null;
	}

	@Override
	public String toString() {
		if (isSign)
			return "[BlockSave:Sign/!/" + loc.toString() + getBiome().name() + "/!/" + f.name() + "/!/"
					+ type.toString() + "/!/" + color.name() + "/!/" + getSignLinesAsString() + "]";// Position/Biome/BlockFace/Material/Color/SignLines
		if (isInvBlock)
			return "[BlockSave:InventoryBlock/!/" + loc.toString() + getBiome().name() + "/!/" + f.name() + "/!/"
					+ type.toString() + "/!/" + getBlockInventoryAsString() + "]"; // Position/Biome/BlockFace/Material/Inventory
		if (isCmd)
			return "[BlockSave:CommandBlock/!/" + loc.toString() + getBiome().name() + "/!/" + f.name() + "/!/"
					+ type.toString() + "/!/" + cmd + "/!/" + cmdname + "]"; // Position/Biome/BlockFace/Material/Command/CmdBlockName
		return "[BlockSave:Block/!/" + loc.toString() + "/!/" + getBiome().name() + "/!/" + f.name() + "/!/"
				+ type.toString() + "]"; // Position/Biome/BlockFace/Material
	}
}
