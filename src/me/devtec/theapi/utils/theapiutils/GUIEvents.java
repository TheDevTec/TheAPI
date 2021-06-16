package me.devtec.theapi.utils.theapiutils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.devtec.theapi.guiapi.GUI.ClickType;
import me.devtec.theapi.guiapi.HolderGUI;
import me.devtec.theapi.guiapi.ItemGUI;
import me.devtec.theapi.utils.theapiutils.LoaderClass.InventoryClickType;

public class GUIEvents {
	public static boolean useItem(Player player, ItemStack stack, HolderGUI g, int slot, ClickType mouse) {
		ItemGUI d = g.getItemGUI(slot);
		boolean stolen = d==null||!d.isUnstealable();
		if(d!=null) {
			d.onClick(player, g, mouse);
		}
		return !stolen;
	}

	public static ClickType buildClick(ItemStack stack, InventoryClickType type, int button, int mouse) {
		String action = stack.getType()==Material.AIR && (type==InventoryClickType.PICKUP||type==InventoryClickType.QUICK_CRAFT)?"DROP":"PICKUP";
		action=(type==InventoryClickType.CLONE?"MIDDLE_":(mouse==0?"LEFT_":mouse==1?"RIGHT_":"MIDDLE_"))+action;
		if(type==InventoryClickType.QUICK_MOVE)
		action="SHIFT_"+action;
		return ClickType.valueOf(action);
	}
}
