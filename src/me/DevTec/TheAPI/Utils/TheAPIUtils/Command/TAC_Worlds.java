package me.DevTec.TheAPI.Utils.TheAPIUtils.Command;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.DevTec.TheAPI.TheAPI;
import me.DevTec.TheAPI.Utils.TheAPIUtils.LoaderClass;
import me.DevTec.TheAPI.WorldsAPI.WorldsAPI;

public class TAC_Worlds {

	public TAC_Worlds(CommandSender s, String[] args) {
		if (args.length == 1) {
			TheAPI.msg("&e/TheAPI WorldsManager Create <world> <generator>", s);
			TheAPI.msg("&e/TheAPI WorldsManager Delete <world>", s);
			TheAPI.msg("&e/TheAPI WorldsManager Unload <world>", s);
			TheAPI.msg("&e/TheAPI WorldsManager Load <world> <generator>", s);
			TheAPI.msg("&e/TheAPI WorldsManager Teleport <world> [player]", s);
			TheAPI.msg("&e/TheAPI WorldsManager Save <world>", s);
			TheAPI.msg("&e/TheAPI WorldsManager SaveAll", s);
			TheAPI.msg("&7Worlds:", s);
			for (World w : Bukkit.getWorlds())
				TheAPI.msg("&7 - &e" + w.getName(), s);
			return;
		}
		if (args[1].equalsIgnoreCase("Teleport") || args[1].equalsIgnoreCase("tp")) {
			if (args.length == 2) {
				if (s instanceof Player)
					TheAPI.msg("&e/TheAPI WorldsManager Teleport <world> [player]", s);
				else
					TheAPI.msg("&e/TheAPI WorldsManager Teleport <world> <player>", s);
				TheAPI.msg("&7Worlds:", s);
				for (World w : Bukkit.getWorlds())
					TheAPI.msg("&7 - &e" + w.getName(), s);
				return;
			}
			if (Bukkit.getWorld(args[2]) == null) {
				TheAPI.msg("&7World with name '" + args[2] + "' doesn't exists.", s);
				return;
			}
			if (args.length == 3) {
				if (s instanceof Player) {
					try {
						((Player) s).teleport(Bukkit.getWorld(args[2]).getSpawnLocation());
					} catch (Exception e) {
						((Player) s).teleport(new Location(Bukkit.getWorld(args[2]), 60, 60, 60));
					}
					TheAPI.msg("&eTeleporting to the world " + args[2] + "..", s);
					return;
				} else
					TheAPI.msg("&e/TheAPI WorldsManager Teleport <world> <player>", s);
				return;
			}
			if (args.length == 4) {
				Player p = Bukkit.getPlayer(args[3]);
				if (p == null) {
					TheAPI.msg("&ePlayer " + args[3] + " isn't online", p);
					return;
				}
				TheAPI.msg("&eTeleporting to the world " + args[2] + "..", p);
				TheAPI.msg("&eTeleporting player " + p.getName() + " to the world " + args[2] + "..", s);
				return;
			}
		}
		if (args[1].equalsIgnoreCase("saveall")) {
				TheAPI.msg("&eTheAPI WorldsManager saving " + (Bukkit.getWorlds().size()) + " world(s)..", s);
				for (World w : Bukkit.getWorlds())
					w.save();
				TheAPI.msg("&eWorlds saved..", s);
			return;
		}
		if (args[1].equalsIgnoreCase("save")) {
			if (args.length == 2) {
				TheAPI.msg("&e/TheAPI WorldsManager Save <world>", s);
				TheAPI.msg("&7Worlds:", s);
				for (World w : Bukkit.getWorlds())
					TheAPI.msg("&7 - &e" + w.getName(), s);
				return;
			}

			if (Bukkit.getWorld(args[2]) == null) {
				TheAPI.msg("&eWorld with name '" + args[2] + "' doesn't exists.", s);
				return;
			}
			
			TheAPI.msg("&eTheAPI WorldsManager saving world with name '" + args[2] + "'..", s);
			Bukkit.getWorld(args[2]).save();
			TheAPI.msg("&eWorld with name '" + args[2] + "' saved.", s);
			return;
		}
		if (args[1].equalsIgnoreCase("unload")) {
			if (args.length == 2) {
				TheAPI.msg("&e/TheAPI WorldsManager Unload <world>", s);
				TheAPI.msg("&7Worlds:", s);
				for (World w : Bukkit.getWorlds())
					TheAPI.msg("&7 - &e" + w.getName(), s);
				return;
			}
			if (Bukkit.getWorld(args[2]) == null) {
				TheAPI.msg("&eWorld with name '" + args[2] + "' doesn't exists.", s);
				return;
			}
			TheAPI.msg("&eTheAPI WorldsManager unloading world with name '" + args[2] + "'..", s);
			WorldsAPI.unloadWorld(args[2], true);

			List<String> a = LoaderClass.config.getStringList("Worlds");
			a.remove(args[2]);
			LoaderClass.config.set("Worlds", a);
			TheAPI.msg("&eWorld with name '" + args[2] + "' unloaded.", s);
			return;
		}
		if (args[1].equalsIgnoreCase("load")) {
			if (args.length == 2) {
				TheAPI.msg("&e/TheAPI WorldsManager Load <world> <generator>", s);
				TheAPI.msg("&7Generators:", s);
				for (String w : Arrays.asList("Default", "Nether", "The_End", "The_Void", "Flat"))
					TheAPI.msg("&7 - &e" + w, s);
				return;
			}
			if (Bukkit.getWorld(args[2]) != null) {
				TheAPI.msg("&eWorld with name '" + args[2] + "' already exists.", s);
				return;
			}
			if (args.length == 3) {
				TheAPI.msg("&e/TheAPI WorldsManager Load <world> <generator>", s);
				TheAPI.msg("&7Generators:", s);
				for (String w : Arrays.asList("Default", "Nether", "The_End", "The_Void", "Flat"))
					TheAPI.msg("&7 - &e" + w, s);
				return;
			}
			if (new File(Bukkit.getWorldContainer().getPath() + "/" + args[2] + "/session.lock").exists()) {
				TheAPI.msg("&eTheAPI WorldsManager loading world with name '" + args[2] + "'..", s);
				int generator = 0;
				if (args[3].equalsIgnoreCase("Flat"))
					generator = 1;
				if (args[3].equalsIgnoreCase("Nether"))
					generator = 2;
				if (args[3].equalsIgnoreCase("The_End") || args[3].equalsIgnoreCase("End"))
					generator = 3;
				if (args[3].equalsIgnoreCase("The_Void") || args[3].equalsIgnoreCase("Void")
						|| args[3].equalsIgnoreCase("Empty"))
					generator = 4;
				Environment env = Environment.NORMAL;
				WorldType wt = WorldType.NORMAL;
				switch (generator) {
				case 1:
					wt = WorldType.FLAT;
					break;
				case 2:
					env = Environment.NETHER;
					break;
				case 3:
					try {
						env = Environment.valueOf("THE_END");
					} catch (Exception e) {
						env = Environment.valueOf("END");
					}
					break;
				case 4:
					wt = null;
					break;
				}
				WorldsAPI.load(args[2], env, wt);
				List<String> a = LoaderClass.config.getStringList("Worlds");
				a.add(args[2]);
				LoaderClass.config.set("Worlds", a);
				LoaderClass.config.set("WorldsSetting." + args[2] + ".Generator", generator);
				LoaderClass.config.set("WorldsSetting." + args[2] + ".GenerateStructures", true);
				LoaderClass.config.save();
				TheAPI.msg("&eWorld with name '" + args[2] + "' loaded.", s);
				return;
			}
			TheAPI.msg("&eWorld with name '" + args[2] + "' doesn't exists.", s);
			return;
		}
		if (args[1].equalsIgnoreCase("delete")) {
			if (args.length == 2) {
				TheAPI.msg("&e/TheAPI WorldsManager Delete <world>", s);
				TheAPI.msg("&7Worlds:", s);
				for (World w : Bukkit.getWorlds())
					TheAPI.msg("&7 - &e" + w.getName(), s);
				return;
			}
			if (Bukkit.getWorld(args[2]) == null) {
				TheAPI.msg("&eWorld with name '" + args[2] + "' doesn't exists.", s);
				return;
			}
			TheAPI.msg("&eTheAPI WorldsManager deleting world with name '" + args[2] + "'..", s);
			WorldsAPI.delete(Bukkit.getWorld(args[2]), true);
			List<String> a = LoaderClass.config.getStringList("Worlds");
			if(a.contains(args[2])) {
			a.remove(args[2]);
			LoaderClass.config.set("Worlds", a);
			}
			LoaderClass.config.set("WorldsSetting." + args[2], null);
			LoaderClass.config.save();
			TheAPI.msg("&eWorld with name '" + args[2] + "' deleted.", s);
			return;
		}
		if (args[1].equalsIgnoreCase("create")) {
			if (args.length == 2) {
				TheAPI.msg("&e/TheAPI WorldsManager Create <world> <generator>", s);
				TheAPI.msg("&7Generators:", s);
				for (String w : Arrays.asList("Default", "Nether", "The_End", "The_Void"))
					TheAPI.msg("&7 - &e" + w, s);
				return;
			}
			if (Bukkit.getWorld(args[2]) != null) {
				TheAPI.msg("&eWorld with name '" + args[2] + "' already exists.", s);
				return;
			}

			if (args.length == 3) {
				TheAPI.msg("&e/TheAPI WorldsManager Create " + args[2] + " <generator>", s);
				TheAPI.msg("&7Generators:", s);
				for (String w : Arrays.asList("Default", "Nether", "The_End", "The_Void", "Flat"))
					TheAPI.msg("&7 - &e" + w, s);
				return;
			}
			int generator = 0;
			if (args[3].equalsIgnoreCase("Flat"))
				generator = 1;
			if (args[3].equalsIgnoreCase("Nether"))
				generator = 2;
			if (args[3].equalsIgnoreCase("The_End") || args[3].equalsIgnoreCase("End"))
				generator = 3;
			if (args[3].equalsIgnoreCase("The_Void") || args[3].equalsIgnoreCase("Void")
					|| args[3].equalsIgnoreCase("Empty"))
				generator = 4;
			TheAPI.msg("&eTheAPI WorldsManager creating new world with name '" + args[2] + "' using generator '"
					+ args[3] + "'..", s);
			Environment env = Environment.NORMAL;
			WorldType wt = WorldType.NORMAL;
			switch (generator) {
			case 1:
				wt = WorldType.FLAT;
				break;
			case 2:
				env = Environment.NETHER;
				break;
			case 3:
				try {
					env = Environment.valueOf("THE_END");
				} catch (Exception e) {
					env = Environment.valueOf("END");
				}
				break;
			case 4:
				wt = null;
				break;
			}
			List<String> a = LoaderClass.config.getStringList("Worlds");
			if(!a.contains(args[2])) {
			a.add(args[2]);
			LoaderClass.config.set("Worlds", a);
		    }
			LoaderClass.config.set("WorldsSetting." + args[2] + ".Generator", generator);
			LoaderClass.config.set("WorldsSetting." + args[2] + ".GenerateStructures", true);
			LoaderClass.config.save();
			WorldsAPI.create(args[2], env, wt, true, 0);
			TheAPI.msg("&eWorld with name '" + args[2] + "' created.", s);
			return;
		}
		TheAPI.msg("&e/TheAPI WorldsManager Create <world> <generator>", s);
		TheAPI.msg("&e/TheAPI WorldsManager Delete <world>", s);
		TheAPI.msg("&e/TheAPI WorldsManager Unload <world>", s);
		TheAPI.msg("&e/TheAPI WorldsManager Load <world> <generator>", s);
		TheAPI.msg("&e/TheAPI WorldsManager Teleport <world> [player]", s);
		TheAPI.msg("&e/TheAPI WorldsManager Save <world>", s);
		TheAPI.msg("&e/TheAPI WorldsManager SaveAll", s);
		TheAPI.msg("&7Worlds:", s);
		for (World w : Bukkit.getWorlds())
			TheAPI.msg("&7 - &e" + w.getName(), s);
		return;
	}

}
