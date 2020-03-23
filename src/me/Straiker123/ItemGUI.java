package me.Straiker123;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import me.Straiker123.GUICreatorAPI.Options;

public class ItemGUI {
	private ItemStack i=new ItemStack(Material.STONE);
	private HashMap<Options, Object> ops = new HashMap<Options, Object>();
	
	public ItemGUI(ItemStack item) {
		i=item;
	}
	
	public ItemStack getItem() {
		return i;
	}
	
	public HashMap<Options, Object> getOptions() {
		return ops;
	}
	
	public boolean hasOption(Options o) {
		return ops.containsKey(o);
	}
	
	public void removeOption(Options o) {
		ops.remove(o);
	}
	
	public void addOption(Options o, Object a) {
		ops.put(o,a);
	}
	/**
	 * @see see Apply this item with options on preparing GUI
	 */
	public void apply(GUICreatorAPI a, int position) {
		a.setItem(position, i, ops);
	}
	
	public String getAsString() {
		try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeObject(this);
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
        	return null;
        }
	}
	
	public static ItemGUI fromString(String s) {
		try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(s));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemGUI items = (ItemGUI) dataInput.readObject();
            dataInput.close();
            return items;
        } catch (Exception e) {
        	return null;
        }
	}
}
