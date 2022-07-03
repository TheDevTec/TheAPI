package me.straikerinacz.theapi.demo.gui;

import org.bukkit.Material;

import me.devtec.theapi.bukkit.game.ItemMaker;
import me.devtec.theapi.bukkit.gui.EmptyItemGUI;
import me.devtec.theapi.bukkit.gui.GUI;
import me.devtec.theapi.bukkit.gui.ItemGUI;

public class TestGUI {
	public static void init() {// :'D tak piš hustý, použij setItem nic ti to nezabrazuje či?
		GUI gui = new GUI("test" , 54);
		ItemGUI emptyPane = new EmptyItemGUI(ItemMaker.of(Material.BLACK_STAINED_GLASS_PANE).displayName("§7").build());
		for(int i = 0; i < 10; ++i)
			gui.setItem(i, emptyPane);


		/*
		 * 9  xxxxxxxxx
		 * 18 x--------
		 * 27 ---------
		 * 36 ---------
		 * 45 ---------
		 * 54 ---------
		 */
	}
}
