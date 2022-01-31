package me.devtec.theapi.utils.json.instances.custom;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import me.devtec.theapi.utils.Position;
import me.devtec.theapi.utils.reflections.Ref;
import me.devtec.theapi.utils.theapiutils.LoaderClass;

public class Utils {
	private static final Method isEmpty = Ref.method(Ref.nmsOrOld("nbt.NBTTagCompound","NBTTagCompound"), "isEmpty");
	public static Map<String, Object> write(Object w) {
		if (w instanceof Location) {
			Location stack = (Location) w;
			Map<String, Object> done = new HashMap<>();
			Map<String, Object> items = new HashMap<>();
			items.put("world", stack.getWorld().getName());
			items.put("x", stack.getX());
			items.put("y", stack.getY());
			items.put("z", stack.getZ());
			items.put("yaw", stack.getYaw());
			items.put("pitch", stack.getPitch());
			done.put("org.bukkit.Location", items);
			return done;
		}
		if (w instanceof Position) {
			Position stack = (Position) w;
			Map<String, Object> done = new HashMap<>();
			Map<String, Object> items = new HashMap<>();
			items.put("world", stack.getWorldName());
			items.put("x", stack.getX());
			items.put("y", stack.getY());
			items.put("z", stack.getZ());
			items.put("yaw", stack.getYaw());
			items.put("pitch", stack.getPitch());
			done.put("me.devtec.theapi.utils.Position", items);
			return done;
		}
		if (w instanceof ItemStack) {
			ItemStack stack = ((ItemStack) w).clone();
			Map<String, Object> done = new HashMap<>();
			Map<String, Object> items = new HashMap<>();
			items.put("type", stack.getType().name());
			items.put("amount", stack.getAmount());
			items.put("data", stack.getData().getData());
			items.put("durability", stack.getDurability());
			if (stack.hasItemMeta()) {
				ItemMeta meta = stack.getItemMeta();
				if (meta.hasEnchants()) {
					Map<String, Integer> enchs = new HashMap<>();
					for (Entry<Enchantment, Integer> e : stack.getEnchantments().entrySet())
						enchs.put(e.getKey().getName(), e.getValue());
					items.put("meta.enchs", enchs);
				}
				if (meta.hasDisplayName())
					items.put("meta.name", meta.getDisplayName());
				if (meta.hasLore())
					items.put("meta.lore", meta.getLore());
				try {
				if (meta.hasLocalizedName())
					items.put("meta.locName", meta.getLocalizedName());
				meta.setLocalizedName(null);
				}catch(Exception | NoSuchMethodError e) {}
				meta.setDisplayName(null);
				meta.setLore(null);
				stack.setItemMeta(meta);
				for (Enchantment e : stack.getEnchantments().keySet())
					stack.removeEnchantment(e);
			}
			Object tag = LoaderClass.nmsProvider.getNBT(stack);
			if (tag != null && !(boolean) Ref.invoke(tag, isEmpty)) {
				items.put("nbt", tag.toString());
			}
			done.put("org.bukkit.inventory.ItemStack", items);
			return done;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static Object read(String key, Object val) {
		if(!(val instanceof Map))return null;
		Map<String, Object> values = (Map<String, Object>)val;
		switch(key) {
			case "org.bukkit.inventory.ItemStack": {
				ItemStack item = new ItemStack(Material.getMaterial(values.get("type").toString()),
						(int)(double) values.get("amount"),
						(short)(double) values.get("durability"));
				item.setData(new MaterialData(item.getType(), (byte)(double)values.get("data")));
				if (values.containsKey("nbt"))
					item = LoaderClass.nmsProvider.setNBT(item, values.get("nbt").toString());
				ItemMeta meta = item.getItemMeta();
				if (values.containsKey("meta.name"))
					meta.setDisplayName(values.get("meta.name").toString());
				try {
					if (values.containsKey("meta.locName"))
						meta.setLocalizedName(values.get("meta.locName").toString());
				} catch (Exception | NoSuchMethodError e) {
				}
				if (values.containsKey("meta.lore"))
					meta.setLore((List<String>) values.get("meta.lore"));
				if (values.containsKey("meta.enchs")) {
					for (Entry<String, Double> enchs : ((Map<String, Double>) values.get("meta.enchs")).entrySet())
						meta.addEnchant(Enchantment.getByName(enchs.getKey()), enchs.getValue().intValue(), true);
				}
				item.setItemMeta(meta);
				return item;
			}
			case "org.bukkit.Location": {
				return new Location(Bukkit.getWorld(values.get("world").toString()), (double) values.get("x"),
								(double) values.get("y"), (double) values.get("z"),
								(float) (double) values.get("yaw"), (float) (double) values.get("pitch"));
			}
			case "me.devtec.theapi.utils.Position": {
				return new Position(values.get("world").toString(), (double) values.get("x"),
								(double) values.get("y"), (double) values.get("z"),
								(float) (double) values.get("yaw"), (float) (double) values.get("pitch"));
			}
		}
		return null;
	}
}
