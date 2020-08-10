package me.DevTec.Blocks;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.HashMap;
import java.util.zip.Deflater;

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

import com.google.common.collect.Maps;

import me.DevTec.TheAPI;
import me.DevTec.Config.Config;
import me.DevTec.Other.Position;
import me.DevTec.Other.StringUtils;
import me.DevTec.Other.TheMaterial;
import me.DevTec.Scheduler.Tasker;

public class Schemate {
	private final String s;
	private final Config c;
	private Schema schem;
	public Schemate(String name) {
		s=name;
		c =new Config("TheAPI/Schematic/"+s+".schem");
	}
	
	public String getName() {
		return s;
	}

	public Schema load() {
		return load(null);
	}
	
	public Schema load(Runnable onFinish) {
		if(schem==null)schem=new Schema(onFinish,this);
		return schem;
	}
	
	public void save(Position fromCopy, Position a, Position b) {
		save(fromCopy, a, b,null);
	}
	
	public Config getFile() {
		return c;
	}
	
	public boolean isSetStandingPosition() {
		return c.getBoolean("info.standing");
	}
	
	public float getBlocks() {
		return (float)c.getLong("info.blocks");
	}
	
	public int getCompression() {
		return c.getInt("info.compression");
	}
	
	public Position[] getCorners() {
		String[] s= c.getString("info.corners").split("/!/");
		return new Position[] {Position.fromString(s[0]),Position.fromString(s[1])};
	}

	public static int compression = 2;
	
	private static String split = "/!_!/";
	public void save(Position fromCopy, Position a, Position b, Runnable onFinish) {
		for(String key : c.getKeys())c.set(key, null);
		c.save(); //override old data
		new Tasker() {
			public void run() {
				c.set("info.standing", fromCopy!=null);
				c.set("info.compression", compression);
				if(fromCopy!=null)
					c.set("info.corners", a.subtract(fromCopy).toString()+"/!/"+b.subtract(fromCopy).toString());
				else
					c.set("info.corners", a.toString()+"/!/"+b.toString());
				c.set("info.blocks", (""+TheAPI.getBlocksAPI().count(a, b)).replaceFirst("\\.0", ""));
				HashMap<String, SchemSaving> perChunk = Maps.newHashMap();
				BlockGetter getter = new BlockGetter(a, b);
				while(getter.has()) {
					Position pos = getter.get();
					if(fromCopy!=null)
					pos.add(-fromCopy.getBlockX(),-fromCopy.getBlockY(),-fromCopy.getBlockZ());
					try {
						SchemSaving s = perChunk.getOrDefault(pos.getType().getType().name()+pos.getType().getData(), null);
						if(s==null)s=new SchemSaving();
						s.dataStream.writeUTF(pos.getBlockX()+"/"+pos.getBlockY()+"/"+pos.getBlockZ()+split+new SimpleSave(pos).toString());
						perChunk.put(pos.getType().getType().name()+pos.getType().getData(), s);
					} catch (Exception e) {
					}
				}
				for(String key : perChunk.keySet()) {
					SchemSaving s = perChunk.get(key);
					c.set("c."+key, (Base64Coder.encodeLines(compress(s.byteStream.toByteArray(),compression))).replace(System.lineSeparator(), ""));
					try {
						s.dataStream.close();
						s.byteStream.close();
					}catch(Exception e) {}
				}
				c.getFile().save();
				perChunk.clear();
				if(onFinish!=null)
					onFinish.run();
			}
		}.runAsync();
	}
	
	private class SchemSaving {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream dataStream;
		public SchemSaving() {
			dataStream = new DataOutputStream(byteStream);
		}
	}
	
	public static class SimpleSave {
		private boolean isSign, isInvBlock, isCmd;
		private String[] lines;
		private ItemStack[] inv;
		private String cmd, cmdname,cname;
		private BlockFace f;
		public SimpleSave(Position pos) {
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
			}
			if (b.getType().name().contains("COMMAND")) {
				isCmd = true;
				CommandBlock c = (CommandBlock) b.getState();
				cmd = c.getCommand();
				cmdname = c.getName();
			}
		}
		
		// sign
		public SimpleSave(BlockFace face, String[] lines) {
			f = face;
			this.lines = lines;
			isSign = true;
		}

		// other
		public SimpleSave(BlockFace face, ItemStack[] inv, String customname) {
			f = face;
			this.inv = inv;
			isInvBlock = true;
			cname=customname;
		}

		// cmd
		public SimpleSave(BlockFace face, String cmd, String cmdname) {
			f = face;
			isCmd = true;
		}

		// normal
		public SimpleSave(BlockFace face) {
			f = face;
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
				BukkitObjectInputStream dataInput = new BukkitObjectInputStream(new ByteArrayInputStream(Base64Coder.decodeLines(s)));
				ItemStack[] items = new ItemStack[size];
				for (int i = 0; i < size; i++) {
					items[i] = (ItemStack)dataInput.readObject();
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

		public String[] getSignLines() { // sign
			return lines;
		}

		public String getSignLinesAsString() {
			return lines!=null ? new StringUtils().join(lines, " ") : null;
		}

		public static String[] getSignLinesFromString(String s) {
			return s.split(" ");
		}
		
		public long load(Position pos, TheMaterial type) {
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
			return pos.getChunkKey();
		}

		public static SimpleSave fromString(String stored) {
			if(stored==null)return null;
				if (stored.startsWith("S:/")) {
					stored = stored.replaceFirst("S:/", "");
					String[] s = stored.split("/!/");
					return new SimpleSave(BlockFace.values()[TheAPI.getStringUtils().getInt(s[0])], getSignLinesFromString(s[1]));
				}
				if (stored.startsWith("I:/")) {
					stored = stored.replaceFirst("I:/", "");
					String[] s = stored.split("/!/");
					return new SimpleSave(BlockFace.values()[TheAPI.getStringUtils().getInt(s[0])], getBlockInventoryFromString(TheAPI.getStringUtils().getInt(s[1]),s[2]), s[3]);
				}
				if (stored.startsWith("C:/")) {
					stored = stored.replaceFirst("C:/", "");
					String[] s = stored.split("/!/");
					return new SimpleSave(BlockFace.values()[TheAPI.getStringUtils().getInt(s[0])], s[1], s[2]);
				}
				return new SimpleSave(BlockFace.values()[TheAPI.getStringUtils().getInt(stored)]);
		}

		@Override
		public String toString() {
			if (isSign) {
				return "S:/" + f.ordinal()+"/!/" + getSignLinesAsString();
				}
						if (isInvBlock)
				return "I:/" +f.ordinal()+ "/!/" + getBlockInventoryAsString() + "/!/" + cname;
			if (isCmd)
				return "C:/" +f.ordinal()+ "/!/" + cmd + "/!/" + cmdname;
			return f.ordinal()+"";
		}
	}

	private static byte[] buf = new byte[1024];
	public static byte[] compress(byte[] in, long times) {
		Deflater compressor = new Deflater(Deflater.BEST_COMPRESSION, true);
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
	    for(int i = 0; i < times; ++i) {
		  compressor.setInput(in);
		  compressor.finish();
		  while (!compressor.finished())
			  byteStream.write(buf, 0, compressor.deflate(buf));
		  in=byteStream.toByteArray();
		  compressor.reset();
		  byteStream.reset();
		}
	    return in;
	}

	public void delete() {
		schem=null;
	}
}
