package me.DevTec.Blocks;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Dropper;
import org.bukkit.block.Furnace;
import org.bukkit.block.Hopper;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import me.DevTec.TheAPI;
import me.DevTec.Other.Position;
import me.DevTec.Other.StringUtils;
import me.DevTec.Other.TheMaterial;

public class BlockSave {
	private boolean isSign, isInvBlock, isCmd;
	private String[] lines;
	private ItemStack[] inv;
	private String cmd, cmdname,cname;
	private TheMaterial type;
	private DyeColor color;
	private Biome b;
	private BlockFace f;

	public BlockSave(Position pos) {
		Block b = pos.getBlock();
		f = b.getFace(b);
		if (b.getType().name().contains("CHEST")) {
			isInvBlock = true;
			Chest c = ((Chest) b.getState());
			try {
			cname=c.getCustomName();
			}catch(NoSuchMethodError e) {
				cname=null;
			}
			inv = c.getBlockInventory().getContents();
		}
		if (b.getType().name().contains("SHULKER_BOX")) {
			isInvBlock = true;
			ShulkerBox c = ((ShulkerBox) b.getState());
			try {
			cname=c.getCustomName();
			}catch(NoSuchMethodError e) {
				cname=null;
			}
			inv = c.getInventory().getContents();
		}
		if (b.getType().name().equals("DROPPER")) {
			isInvBlock = true;
			Dropper c = ((Dropper) b.getState());
			try {
			cname=c.getCustomName();
			}catch(NoSuchMethodError e) {
				cname=null;
			}
			inv = c.getInventory().getContents();
		}
		if (b.getType().name().equals("FURNACE")) {
			isInvBlock = true;
			Furnace c = ((Furnace) b.getState());
			try {
			cname=c.getCustomName();
			}catch(NoSuchMethodError e) {
				cname=null;
			}
			inv = c.getInventory().getContents();
		}
		if (b.getType().name().equals("DISPENSER")) {
			isInvBlock = true;
			Dispenser c = ((Dispenser) b.getState());
			cname=c.getCustomName();
			inv = c.getInventory().getContents();
		}
		if (b.getType().name().equals("HOPPER")) {
			isInvBlock = true;
			Hopper c = ((Hopper) b.getState());
			try {
			cname=c.getCustomName();
			}catch(NoSuchMethodError e) {
				cname=null;
			}
			inv = c.getInventory().getContents();
		}
		if (b.getType().name().contains("SIGN")) {
			isSign = true;
			Sign c = (Sign) b.getState();
			lines = c.getLines();
			try {
				color = c.getColor();
			} catch (NoSuchMethodError e) {

			}
		}
		if (b.getType().name().contains("COMMAND")) {
			isCmd = true;
			CommandBlock c = (CommandBlock) b.getState();
			cmd = c.getCommand();
			cmdname = c.getName();
		}
		this.b = pos.getBiome();
		type = pos.getType();
	}

	// sign
	public BlockSave(Biome biome, BlockFace face, TheMaterial material, DyeColor color, String[] lines) {
		this.b = biome;
		f = face;
		type = material;
		this.lines = lines;
		this.color = color;
		isSign = true;
	}

	// other
	public BlockSave(Biome biome, BlockFace face, TheMaterial material, ItemStack[] inv, String customname) {
		this.b = biome;
		f = face;
		type = material;
		this.inv = inv;
		isInvBlock = true;
		cname=customname;
	}

	// cmd
	public BlockSave(Biome biome, BlockFace face, TheMaterial material, String cmd, String cmdname) {
		this.b = biome;
		f = face;
		type = material;
		isCmd = true;
	}

	// normal
	public BlockSave(Biome biome, BlockFace face, TheMaterial material) {
		this.b = biome;
		f = face;
		type = material;
	}

	public String getCustomName() {
		return cname;
	}
	
	public BlockFace getFace() {
		return f;
	}

	public ItemStack[] getBlockInventory() { // shulkerbox, chest..
		return inv;
	}

	public static ItemStack[] getBlockInventoryFromString(int size, String s) {
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(s));
			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
			ItemStack[] items = new ItemStack[size];
			for (int i = 0; i < size; i++) {
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
			for (int i = 0; i < inv.length; i++) {
				dataOutput.writeObject(inv[i]);
			}
			dataOutput.close();
			return inv.length+"/!/"+Base64Coder.encodeLines(outputStream.toByteArray());
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
		return lines!=null ? new StringUtils().join(lines, " ") : null;
	}

	public static String[] getSignLinesFromString(String s) {
		return s.split(" ");
	}

	public Biome getBiome() {
		return b;
	}

	public TheMaterial getMaterial() {
		return type;
	}
	
	public long load(Position pos, boolean createBlock) {
		long k = (pos.getBlockX()>>4 & 0xFFFF0000L) << 16L | (pos.getBlockX()>>4 & 0xFFFFL) << 0L;
	    k |= (pos.getBlockZ()>>4 & 0xFFFF0000L) << 32L | (pos.getBlockZ()>>4 & 0xFFFFL) << 16L;
		if(createBlock)
		pos.setType(type);
		String n = type.getType().name();
		if (n.contains("SIGN")) {
			Sign w = (Sign) pos.getBlock().getState();
			int i = 0;
			if (getSignLines() != null && getSignLines().length > 0)
				for (String line : getSignLines()) {
					w.setLine(i, line);
					++i;
				}
			try {
				if (getColor() != null)
					w.setColor(getColor());
			} catch (NoSuchFieldError er) {
				// old version
			}
			w.update(true, false);
		}
		if (n.contains("CHEST")) {
			Chest w = (Chest) pos.getBlock().getState();
			try {
			if(cname!=null && !cname.equals("null"))
			w.setCustomName(cname);
			}catch(NoSuchMethodError e) {}
			if(inv!=null)
			w.getInventory().setContents(inv);
		}
		if (n.equals("DROPPER")) {
			Dropper w = (Dropper) pos.getBlock().getState();
			try {
			if(cname!=null && !cname.equals("null"))
			w.setCustomName(cname);
			}catch(NoSuchMethodError e) {}
			if (inv != null)
				w.getInventory().setContents(inv);
		}
		if (n.equals("DISPENSER")) {
			Dispenser w = (Dispenser) pos.getBlock().getState();
			try {
			if(cname!=null && !cname.equals("null"))
			w.setCustomName(cname);
			}catch(NoSuchMethodError e) {}
			if (inv != null)
				w.getInventory().setContents(inv);
		}
		if (n.equals("HOPPER")) {
			Hopper w = (Hopper) pos.getBlock().getState();
			try {
			if(cname!=null && !cname.equals("null"))
			w.setCustomName(cname);
			}catch(NoSuchMethodError e) {}
			if (inv != null)
				w.getInventory().setContents(inv);
		}
		if (n.equals("FURNACE")) {
			Furnace w = (Furnace) pos.getBlock().getState();
			if(cname!=null && !cname.equals("null"))
			w.setCustomName(cname);
			if (inv != null)
				w.getInventory().setContents(inv);
		}
		if (n.contains("SHULKER_BOX")) {
			ShulkerBox w = (ShulkerBox) pos.getBlock().getState();
			try {
			if(cname!=null && !cname.equals("null"))
			w.setCustomName(cname);
			}catch(NoSuchMethodError e) {}
			if (inv != null)
				w.getInventory().setContents(inv);
		}
		if (n.contains("COMMAND")) {
			CommandBlock w = (CommandBlock) pos.getBlock().getState();
			if (getCommand() != null)
				w.setCommand(getCommand());
			if (getCommandBlockName() != null)
				w.setName(getCommandBlockName());
			w.update(true, false);
		}
		return k;
	}

	public static BlockSave fromString(String stored) {
		if(stored==null)return null;
		if (stored.startsWith("[BS:")) {
			if (stored.startsWith("[BS:S/!/")) {
				stored = stored.substring(0, stored.length() - 1).replaceFirst("\\[BS:S/!/", "");
				String[] s = stored.split("/!/");
				try {
				return new BlockSave(Biome.values()[TheAPI.getStringUtils().getInt(s[0])], BlockFace.values()[TheAPI.getStringUtils().getInt(s[1])],
						TheMaterial.fromString(s[2]), DyeColor.values()[TheAPI.getStringUtils().getInt(s[3])], getSignLinesFromString(s[4]));
				}catch(Exception ererr) {
					return new BlockSave(Biome.values()[TheAPI.getStringUtils().getInt(s[0])], BlockFace.values()[TheAPI.getStringUtils().getInt(s[1])],
							TheMaterial.fromString(s[2]), null, getSignLinesFromString(s[4]));
				}
			}
			if (stored.startsWith("[BS:IB/!/")) {
				stored = stored.substring(0, stored.length() - 1).replaceFirst("\\[BS:IB/!/", "");
				String[] s = stored.split("/!/");
				return new BlockSave(Biome.values()[TheAPI.getStringUtils().getInt(s[0])], BlockFace.values()[TheAPI.getStringUtils().getInt(s[1])],
						TheMaterial.fromString(s[2]), getBlockInventoryFromString(TheAPI.getStringUtils().getInt(s[3]),s[4]), s[5]);
			}
			if (stored.startsWith("[BS:CB/!/")) {
				stored = stored.substring(0, stored.length() - 1).replaceFirst("\\[BS:CB/!/", "");
				String[] s = stored.split("/!/");
				return new BlockSave(Biome.values()[TheAPI.getStringUtils().getInt(s[0])], BlockFace.values()[TheAPI.getStringUtils().getInt(s[1])],
						TheMaterial.fromString(s[2]), s[3], s[4]);
			}
			if (stored.startsWith("[BS:B/!/")) {
				stored = stored.substring(0, stored.length() - 1).replaceFirst("\\[BS:B/!/", "");
				String[] s = stored.split("/!/");
				return new BlockSave(Biome.values()[TheAPI.getStringUtils().getInt(s[0])], BlockFace.values()[TheAPI.getStringUtils().getInt(s[1])],
						s[2].equals("0")?new TheMaterial("AIR"):TheMaterial.fromString(s[2]));
			}
			return null;
		}
		return null;
	}

	@Override
	public String toString() {
		if (isSign) {
			try {
			return "[BS:S/!/" + b.ordinal() + "/!/" + f.ordinal() + "/!/"
					+ type.toString() + "/!/" + color.ordinal() + "/!/" + getSignLinesAsString() + "]";// Biome/BlockFace/Material/Color/SignLines
			}catch(Exception errer) {
				return "[BS:S/!/" + b.ordinal() + "/!/" + f.ordinal() + "/!/"
						+ type.toString() + "/!/" + 0 + "/!/" + getSignLinesAsString() + "]";// Biome/BlockFace/Material/Color/SignLines
				}
			}
					if (isInvBlock)
			return "[BS:IB/!/" + b.ordinal() + "/!/" + f.ordinal() + "/!/"
					+ type.toString() + "/!/" + getBlockInventoryAsString() + "/!/" + cname + "]"; // Biome/BlockFace/Material/Inventory/CustomName
		if (isCmd)
			return "[BS:CB/!/" + b.ordinal() + "/!/" + f.ordinal() + "/!/"
					+ type.toString() + "/!/" + cmd + "/!/" + cmdname + "]"; // Biome/BlockFace/Material/Command/CmdBlockName
		return "[BS:B/!/" + b.ordinal() + "/!/" + f.ordinal() + "/!/"
				+ (type.getType()==Material.AIR?"0":type.toString()) + "]"; // Biome/BlockFace/Material
	}
}
