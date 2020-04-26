package me.Straiker123;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
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
import org.bukkit.material.MaterialData;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

@SuppressWarnings("deprecation")
public class BlockSave {
	private Biome biom;
	private Material mat;
	private Location loc;
	private MaterialData data;
	private String[] lines;
	private ItemStack[] inv;
	private String cmd,cmdname;
	private DyeColor color;
	private BlockFace f;
	public BlockSave(Block b) {
			f=b.getFace(b);
		  if(b.getType().name().contains("CHEST")) {
			  inv = ((Chest) b.getState()).getBlockInventory().getContents();
		  }
		  if(b.getType().name().contains("SHULKER_BOX")) {
			  inv = ((ShulkerBox) b.getState()).getInventory().getContents();
		  }
		  if(b.getType().name().equals("DROPPER")) {
			  inv = ((Dropper) b.getState()).getInventory().getContents();
		  }
		  if(b.getType().name().equals("DISPENSER")) {
			  inv = ((Dispenser) b.getState()).getInventory().getContents();
		  }
		  if(b.getType().name().equals("HOPPER")) {
			  inv = ((Hopper) b.getState()).getInventory().getContents();
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
		biom=b.getBiome();
		mat=b.getType();
		loc=b.getLocation();
	}

	public BlockFace getFace() {
		return f;
	}
	public ItemStack[] getBlockInventory() { //shulkerbox, chest..
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
	public MaterialData getMaterialData() {
		return data;
	}
	public Biome getBiome() {
		return biom;
	}
	public Material getMaterial() {
		return mat;
	}
	
	public static BlockSave getBlockSaveFromString(String s) {
		try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(s));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            BlockSave items =(BlockSave) dataInput.readObject();
            dataInput.close();
            return items;
        } catch (Exception e) {
        	return null;
        }
	}
	
	public String getBlockSaveAsString() {
		try {
		 ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
         dataOutput.writeObject(this);
         dataOutput.close();
         return Base64Coder.encodeLines(outputStream.toByteArray());
		}catch(Exception err) {
			return null;
		}
	}
}
