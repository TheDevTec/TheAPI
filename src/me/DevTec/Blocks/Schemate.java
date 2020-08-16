package me.DevTec.Blocks;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.HashMap;

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

import me.DevTec.Config.Config;
import me.DevTec.Other.Compression;
import me.DevTec.Other.Position;
import me.DevTec.Other.StringUtils;
import me.DevTec.Other.TheMaterial;
import me.DevTec.Scheduler.Tasker;

public class Schemate {
	private final String s;
	private Config c;
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
		if(schem==null) {
			schem=new Schema(onFinish,this);
		}
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
	
	private static String split = "/!_!/";
	public void save(Position fromCopy, Position a, Position b, Runnable onFinish) {
		for(String key : c.getKeys())c.set(key, null);
		c.save(); //override old data
		new Tasker() {
			public void run() {
				c.set("info.standing", fromCopy!=null);
				if(fromCopy!=null)
					c.set("info.corners", a.subtract(fromCopy).toString()+"/!/"+b.subtract(fromCopy).toString());
				else
					c.set("info.corners", a.toString()+"/!/"+b.toString());
				c.set("info.blocks", (""+BlocksAPI.count(a, b)).replaceFirst("\\.0", ""));
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
					c.set("c."+key, (Base64Coder.encodeLines(Compression.compress(s.byteStream.toByteArray()))).replace(System.lineSeparator(), ""));
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
		
		public long load(Position pos, TheMaterial type) {
			pos.setType(type);
			String n = type.getType().name();
			if (n.contains("SIGN")) {
				Sign w = (Sign) pos.getBlock().getState();
				int i = 0;
				if (lines != null && lines.length > 0)
					for (String line : lines) {
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
				if (cmd != null && !cmd.equals("null"))
					w.setCommand(cmd);
				if (cmdname != null && !cmdname.equals("null"))
					w.setName(cmdname);
				w.update(true, false);
			}
			return pos.getChunkKey();
		}

		public static SimpleSave fromString(String stored) {
			if(stored==null)return null;
				if (stored.startsWith("S:/")) {
					stored = stored.replaceFirst("S:/", "");
					String[] s = stored.split("/!/");
					return new SimpleSave(BlockFace.values()[StringUtils.getInt(s[0])], s[1].split(" "));
				}
				if (stored.startsWith("I:/")) {
					stored = stored.replaceFirst("I:/", "");
					String[] s = stored.split("/!/");
					try {
						BukkitObjectInputStream dataInput = new BukkitObjectInputStream(new ByteArrayInputStream(Base64Coder.decodeLines(s[2])));
						ItemStack[] items = new ItemStack[StringUtils.getInt(s[1])];
						for (int i = 0; i < StringUtils.getInt(s[1]); i++) {
							items[i] = (ItemStack)dataInput.readObject();
						}
						dataInput.close();
						return new SimpleSave(BlockFace.values()[StringUtils.getInt(s[0])], items, s[3]);
					} catch (Exception e) {
						return new SimpleSave(BlockFace.values()[StringUtils.getInt(s[0])], new ItemStack[1], s[3]);
					}
				}
				if (stored.startsWith("C:/")) {
					stored = stored.replaceFirst("C:/", "");
					String[] s = stored.split("/!/");
					return new SimpleSave(BlockFace.values()[StringUtils.getInt(s[0])], s[1], s[2]);
				}
				return new SimpleSave(BlockFace.values()[StringUtils.getInt(stored)]);
		}

		@Override
		public String toString() {
			if (isSign) {
				return "S:/" + f.ordinal()+"/!/" + lines!=null ? StringUtils.join(lines, " ") : null;
				}
						if (isInvBlock) {
							try {
								ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
								BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
								for (int i = 0; i < inv.length; i++) {
									dataOutput.writeObject(inv[i]);
								}
								dataOutput.close();
								return "I:/" +f.ordinal()+ "/!/" + inv.length+"/!/"+Base64Coder.encodeLines(outputStream.toByteArray()) + "/!/" + cname;
							} catch (Exception err) {
								return "I:/" +f.ordinal()+ "/!/" + inv.length+"/!/"+Base64Coder.encodeLines(new ByteArrayOutputStream().toByteArray()) + "/!/" + cname;
							}
						}
			if (isCmd)
				return "C:/" +f.ordinal()+ "/!/" + cmd + "/!/" + cmdname;
			return f.ordinal()+"";
		}
	}

	public void delete() {
		schem=null;
	}
}
