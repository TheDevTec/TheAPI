package me.DevTec.TheAPI.Utils.TheAPIUtils.Command;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import me.DevTec.TheAPI.TheAPI;
import me.DevTec.TheAPI.APIs.ItemCreatorAPI;
import me.DevTec.TheAPI.APIs.PluginManagerAPI;
import me.DevTec.TheAPI.GUIAPI.GUI;
import me.DevTec.TheAPI.GUIAPI.ItemGUI;
import me.DevTec.TheAPI.PlaceholderAPI.ThePlaceholderAPI;
import me.DevTec.TheAPI.SortedMap.RankingAPI;

public class TAC_Test {

	public TAC_Test(CommandSender s, String[] args) {
		if (args.length == 1) {
			TheAPI.msg("&e/TheAPI Test SortedMap", s);
			TheAPI.msg("&e/TheAPI Test GUI", s);
			TheAPI.msg("&e/TheAPI Test Other - DevTec currently testing", s);
			return;
		}
		if (args[1].equalsIgnoreCase("Other")) {
		TheAPI.msg("&eThePlaceholderAPII:", s);
		TheAPI.msg("&6- %player_health% -> "+ThePlaceholderAPI.setPlaceholders((Player)s, "%player_health%"), s);
		TheAPI.msg("&6- %player_statistic_kills% -> "+ThePlaceholderAPI.setPlaceholders((Player)s, "%player_statistic_kills%"), s);
		TheAPI.msg("&6- %server_time% -> "+ThePlaceholderAPI.setPlaceholders((Player)s, "%server_time%"), s);
		TheAPI.msg("&6- %server_online% -> "+ThePlaceholderAPI.setPlaceholders((Player)s, "%server_online%"), s);
		TheAPI.msg(" &eMath:", s);
		TheAPI.msg("&6- %math{5*4+6}% -> "+ThePlaceholderAPI.setPlaceholders((Player)s, "%math{5*4+6}%"), s);
		TheAPI.msg("&6- %math{5.9*4.4+6.448}% -> "+ThePlaceholderAPI.setPlaceholders((Player)s, "%math{5.9*4.4+6.448}%"), s);
		TheAPI.msg("&6- %math{3*%math{2*4}%+1}% -> "+ThePlaceholderAPI.setPlaceholders((Player)s, "%math{3*%math{2*4}%+1}%"), s);
		return;
		}
		if(s instanceof Player) {
		Player p = (Player) s;
		if (args[1].equalsIgnoreCase("GUI")) {
			GUI gui = new GUI("&eTheAPI v" + PluginManagerAPI.getVersion("TheAPI"), 54, p) {
		
			@Override
			public void onClose(Player player) {
				TheAPI.msg("&0[&cTheAPI&0] &eClosed example gui!", player);
			}
			
		};
		Material a = Material.matchMaterial("BLACK_STAINED_GLASS_PANE")!=null?Material.matchMaterial("BLACK_STAINED_GLASS_PANE"):Material.matchMaterial("STAINED_GLASS_PANE");
		ItemGUI item = new ItemGUI(a.name().equals("BLACK_STAINED_GLASS_PANE")?ItemCreatorAPI.create(a, 1, "&7"):ItemCreatorAPI.create(a, 1, "&7", 15)) {
			
			@Override
			public void onClick(Player player, GUI gui, ClickType click) {
				
			}
		};
		for (int i = 0; i < 10; ++i)
			gui.setItem(i, item);
		gui.setItem(17, item);
		gui.setItem(18, item);
		gui.setItem(26, item);
		gui.setItem(27, item);
		gui.setItem(35, item);
		gui.setItem(36, item);
		for (int i = 44; i < 54; ++i)
			gui.setItem(i, item);
		
		gui.setItem(20, new ItemGUI(ItemCreatorAPI.create(Material.DIAMOND, 1, "&eWho created TheAPI?", 
				Arrays.asList("", "  &e» &7Creator of TheAPI is StraikerinaCZ", "  &e» &7Owner of TheAPI is DevTec"))) {
			
			@Override
			public void onClick(Player player, GUI gui, ClickType click) {
				TheAPI.msg("&0[&cTheAPI&0] &eWho created TheAPI?", player);
				TheAPI.msg("  &e» &7Creator of TheAPI is StraikerinaCZ", player);
				TheAPI.msg("  &e» &7Owner of TheAPI is DevTec", player);
			}
		});
		
		gui.setItem(22, new ItemGUI(ItemCreatorAPI.create(Material.EMERALD, 1, "&eWhere report bug?", 
				Arrays.asList("", "  &e» &7On our discord or github", "  &e» &7Discord: https://discord.gg/z4kK66g", "  &e» &7Github: https://github.com/TheDevTec/TheAPI"))) {
			
			@Override
			public void onClick(Player player, GUI gui, ClickType click) {
				TheAPI.msg("&0[&cTheAPI&0] &eWhere report bug?", player);
				TheAPI.msg("  &e» &7On our discord or github", player);
				TheAPI.msg("  &e» &7Discord: https://discord.gg/z4kK66g", player);
				TheAPI.msg("  &e» &7Github: https://github.com/TheDevTec/TheAPI", player);
			}
		});
		
		gui.setItem(24, new ItemGUI(ItemCreatorAPI.create(Material.GOLD_INGOT, 1, "&eAre somewhere examples of GUIs?", 
				Arrays.asList("", "  &e» &7GUI Slots: https://i.imgur.com/f43qxux.png", "  &e» &7GUI #1: https://pastebin.com/PGPwKxRz"))) {
			
			@Override
			public void onClick(Player player, GUI gui, ClickType click) {
				TheAPI.msg("&0[&cTheAPI&0] &eAre somewhere examples of GUIs?", player);
				TheAPI.msg("  &e» &7GUI Slots: https://i.imgur.com/f43qxux.png", player);
				TheAPI.msg("  &e» &7GUI #1: https://pastebin.com/PGPwKxRz", player);
			}
		});
		
		gui.setItem(49, new ItemGUI(ItemCreatorAPI.create(Material.getMaterial("BARRIER")==null?Material.getMaterial("BEDROCK"):Material.getMaterial("BARRIER"), 1, "&cClose")) {
			
			@Override
			public void onClick(Player player, GUI gui, ClickType click) {
				gui.close(player);
			}
		});
			return;
		}}
		if (args[1].equalsIgnoreCase("SortedMap")) {
			HashMap<String, Double> Comparable = new HashMap<>();
			TheAPI.msg("&eInput:", s);
			TheAPI.msg("&6- A, 50.0", s);
			TheAPI.msg("&6- D, 5431.6", s);
			TheAPI.msg("&6- C, 886.5", s);
			TheAPI.msg("&6- G, 53.11", s);
			Comparable.put("A", 50.0);
			Comparable.put("D", 5431.6);
			Comparable.put("C", 886.5);
			Comparable.put("G", 53.11);
			RankingAPI<String, Double> map = new RankingAPI<>(Comparable);
			TheAPI.msg("&eResult:", s);
			for (Entry<String, Double> entry : map.entrySet())
				TheAPI.msg("&6" + map.getPosition(entry.getKey()) + ". " + entry.getKey() + ", "
						+entry.getValue(), s);
			HashMap<String, String> tops = new HashMap<>();
			TheAPI.msg("&eInput:", s);
			TheAPI.msg("&6- A, ABD", s); //1
			TheAPI.msg("&6- B, VGR", s); //4
			TheAPI.msg("&6- C, BTW", s); //2
			TheAPI.msg("&6- D, OAW", s); //3
			tops.put("A", "ABD");
			tops.put("B", "VGR");
			tops.put("C", "BTW");
			tops.put("D", "OAW");
			RankingAPI<String, String> maps = new RankingAPI<>(tops);
			TheAPI.msg("&eResult:", s);
			for (Entry<String, String> entry : maps.entrySet())
				TheAPI.msg("&6" + maps.getPosition(entry.getKey()) + ". " + entry.getKey() + ", "
						+entry.getValue(), s);
			return;
		}
	}

}
