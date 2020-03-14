package me.Straiker123;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

@SuppressWarnings("deprecation")
public class BlockSave {
	BlockData blockdata;
	Biome biom;
	Material mat;
	Location loc;
	MaterialData data;
	String[] lines;
	ItemStack[] inv;
	String cmd,cmdname;
	DyeColor color;
	public BlockSave(Block b) {
		  if(b.getType().name().contains("CHEST")) {
			  Chest c = (Chest) b.getState();
			  inv = c.getBlockInventory().getContents();
		  }
		  try {
		  if(b.getType().name().contains("SHULKER_BOX")) {
			  ShulkerBox c = (ShulkerBox) b.getState();
			  inv = c.getInventory().getContents();
			  try {
				  color=c.getColor();
			  }catch(Exception e) {
				  
			  }
		  }
		  }catch(Exception |NoSuchFieldError e) {
			  
		  }
		  if(b.getType().name().contains("SIGN")) {
			  Sign c = (Sign) b.getState();
			  lines = c.getLines();
			  try {
				  color=c.getColor();
			  }catch(Exception | NoSuchMethodError e) {
				  
			  }
		  }
		  if(b.getType().name().contains("COMMAND")) {
			  CommandBlock c = (CommandBlock)b.getState();
			  cmd =c.getCommand();
			  cmdname=c.getName();
		  }
		data=b.getState().getData();
		try {
		blockdata=b.getBlockData();
	}catch(Exception|NoSuchMethodError|NoSuchFieldError er) {
		//old version
	}
		biom=b.getBiome();
		mat=b.getType();
		loc=b.getLocation();
	}

	public ItemStack[] getBlockInventory() { //shulkerbox & chest
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
		}catch(Exception err) {
			return null;
		}
	}
	
	public String getCommand() {
		return cmd;
	}

	public String getCommandBlockName() {
		return cmdname;
	}

	public DyeColor getColor() { //shulkerbox & sign
		return color;
	}

	public String[] getSignLines() { //sign
		return lines;
	}

	public String getSignLinesAsString() {
		return new StringUtils().join(lines, " ");
	}
	public String[] getSignLinesFromString(String s) {
		return s.split(" ");
	}
	
	public Location getLocation() {
		return loc;
	}
	
	public String getLocationAsString() {
		return TheAPI.getBlocksAPI().getLocationAsString(loc);
	}
	
	public static Location getLocationFromString(String d) {
		return TheAPI.getBlocksAPI().getLocationFromString(d);
	}
	
	public World getWorld() {
		return loc.getWorld();
	}

	public BlockData getBlockData() {
		return blockdata;
	}
	public MaterialData getMaterialData() {
		return data;
	}
	public Biome getBiome() {
		return biom;
	}
	public Material getMaterial() {
		return mat;
	}
	
	public String getAsString() {
		return getLocationAsString()+"#"//0
				+mat.name()+"-"+biom.name()+"-"+blockdata.getAsString()+"-"+getColor()+"#"
				+getBlockInventoryAsString()+"#"+cmd+"#"+cmdname+"#"
				+getSignLinesAsString();
	}
	
	public static BlockSave createBlockSave(String s) {
		BlockSave d = null;
		if(s != null) {
			String[] f = s.split("#");
			String[] a2 = f[1].split("-");
			String inv = f[2];
			if(inv.equals("null"))inv=null;
			String cmd = f[3];
			if(cmd.equals("null"))cmd=null;
			String cmdname = f[4];
			if(cmdname.equals("null"))cmdname=null;
			String[] set = f[5].split(" ");
			if(set[0].equals("null"))set=null;

			String mat = a2[0];
			String bi = a2[1];
			if(bi.equals("null"))bi=null;
			String da = a2[2];
			if(da.equals("null"))da=null;
			Block b = getLocationFromString(f[0]).getBlock();
			if(bi!=null)
			b.setBiome(Biome.valueOf(bi));
			if(mat==null)mat="STONE";	
			b.setType(Material.matchMaterial(mat));
			if(da!=null && Bukkit.createBlockData(da)!=null)
			b.setBlockData(Bukkit.createBlockData(da));
			if(b.getType().name().contains("SIGN")) {
				Sign w = (Sign)b.getState();
				int i = 0;
				if(set != null && set.length > 0)
				for(String line : set) {
				w.setLine(i, line);
				++i;
				}
				try {
					if(a2[3]!=null && DyeColor.valueOf(a2[3])!=null)
					w.setColor(DyeColor.valueOf(a2[3]));
				}catch(Exception er) {
					//old version
				}
				w.update(true, true);
			}
			if(b.getType().name().contains("CHEST")) {
				Chest w = (Chest)b.getState();
				if(inv != null && getBlockInventoryFromString(inv) != null)
				w.getBlockInventory().setContents(getBlockInventoryFromString(inv));
				w.update(true, true);
			}
			if(b.getType().name().contains("SHULKER_BOX")) {
				ShulkerBox w = (ShulkerBox)b.getState();
				if(inv != null && getBlockInventoryFromString(inv) != null)
				w.getInventory().setContents(getBlockInventoryFromString(inv));
				w.update(true, true);
			}
			if(b.getType().name().contains("COMMAND")) {
				CommandBlock w = (CommandBlock)b.getState();
				if(cmd != null)
				w.setCommand(cmd);
				if(cmdname != null)
				w.setName(cmdname);
				w.update(true, true);
			}
			d=new BlockSave(b);
		}
		return d;
	}
}
