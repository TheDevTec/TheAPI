package me.Straiker123.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.StringUtil;

import me.Straiker123.BlockSave;
import me.Straiker123.BlocksAPI.Shape;
import me.Straiker123.GUICreatorAPI;
import me.Straiker123.GUICreatorAPI.Options;
import me.Straiker123.ItemCreatorAPI;
import me.Straiker123.LoaderClass;
import me.Straiker123.MultiMap;
import me.Straiker123.Position;
import me.Straiker123.RankingAPI;
import me.Straiker123.ScoreboardAPI;
import me.Straiker123.TheAPI;
import me.Straiker123.TheMaterial;
import me.Straiker123.Scheduler.Tasker;

public class TheAPICommand implements CommandExecutor, TabCompleter {

	private String getPlugin(Plugin a) {
		if (a.isEnabled())
			return "&a" + a.getName();
		return "&c" + a.getName() + " &7(Disabled)";
	}

	String[] args;

	private boolean eq(int i, String s) {
		return args[i].equalsIgnoreCase(s);
	}

	boolean r;
	CommandSender s;

	private boolean perm(String p) {
		if (s.hasPermission("TheAPI.Command." + p))
			return true;
		TheAPI.msg("&cYou do not have permission '&4TheAPI.Command." + p + "&c' to do that!", s);
		return false;
	}

	private void regWorld(String w, int type) {
		LoaderClass.config.set("WorldsSetting." + w + ".Generator", type);
		LoaderClass.config.set("WorldsSetting." + w + ".GenerateStructures", true);
		LoaderClass.config.save();
	}

	private void unregWorld(String w) {
		LoaderClass.config.set("WorldsSetting." + w, null);
		LoaderClass.config.save();
	}

	@Override
	public boolean onCommand(CommandSender s, Command arg1, String arg2, String[] args) {
		this.args = args;
		this.s = s;
		if (args.length == 0) {
			TheAPI.msg("&7-----------------", s);
			if (s.hasPermission("TheAPI.Command.Info"))
				TheAPI.msg("&6/TheAPI Info", s);
			if (s.hasPermission("TheAPI.Command.Reload"))
				TheAPI.msg("&6/TheAPI Reload", s);
			if (s.hasPermission("TheAPI.Command.WorldsManager"))
				TheAPI.msg("&6/TheAPI WorldsManager", s);
			if (s.hasPermission("TheAPI.Command.ClearCache"))
				TheAPI.msg("&6/TheAPI ClearCache", s);
			if (s.isOp())
				TheAPI.msg("&6/TheAPI Test", s);
			TheAPI.msg("&6Credits: TheAPI created by DevTec", s);
			TheAPI.msg("&7-----------------", s);
			return true;
		}
		if (eq(0, "test")) {
			if (!s.isOp() || !(s instanceof Player))
				return true; // sender must be player & has op
			Player p = (Player) s;
			if (args.length == 1) {
				TheAPI.msg("&7-----------------", s);
				TheAPI.msg("&6/TheAPI Test ActionBar", s);
				TheAPI.msg("&6/TheAPI Test BlocksAPI", s);
				TheAPI.msg("&6/TheAPI Test BossBar", s);
				TheAPI.msg("&6/TheAPI Test MultiMap", s);
				TheAPI.msg("&6/TheAPI Test PlayerName", s);
				TheAPI.msg("&6/TheAPI Test RankingAPI", s);
				TheAPI.msg("&6/TheAPI Test Scoreboard", s);
				TheAPI.msg("&6/TheAPI Test TabList", s);
				TheAPI.msg("&6/TheAPI Test Title", s);
				TheAPI.msg("&6/TheAPI Test GUICreatorAPI", s);
				TheAPI.msg("&6/TheAPI Test hideShowEntity", s);
				TheAPI.msg("&6/TheAPI Test Other - DevTec currently testing", s);
				TheAPI.msg("&7-----------------", s);
				return true;
			}
			if (eq(1, "Other")) {
				double[] tps = TheAPI.getNMSAPI().getServerTPS();
				s.sendMessage("TPS: " + tps[0] + " 1min, " + tps[1] + " 5min, " + tps[2] + " 15min");
				return true;
			}
			if (eq(1, "hideShowEntity")) {
				Pig pig = (Pig) p.getWorld().spawnEntity(p.getLocation(), EntityType.PIG);
				pig.setCollidable(false);
				pig.setBaby();
				try {
					pig.setAI(false);
				} catch (Exception | NoSuchMethodError e) {
				}
				pig.setSilent(true);
				new Tasker() {
					@Override
					public void run() {
						pig.setCustomName(TheAPI.colorize("&4Become invisible in " + (5 - runTimes())));
						pig.setCustomNameVisible(true);
						if (runTimes() == 5) {
							for (Player all : TheAPI.getOnlinePlayers())
								TheAPI.hideEntity(all, pig);
							pig.setCustomName(TheAPI.colorize("&4Repeat visible!"));
							new Tasker() {
								@Override
								public void run() {
									for (Player all : TheAPI.getOnlinePlayers())
										TheAPI.showEntity(all, pig);
									new Tasker() {
										@Override
										public void run() {
											pig.remove();
										}
									}.later(100);
								}
							}.later(100);
						}
					}
				}.repeatingTimes(0, 20, 5);
				return true;
			}
			if (eq(1, "GUICreatorAPI")) {
				TheAPI.msg("&eThis maybe help you with creating gui: https://i.imgur.com/f43qxux.png", p);
				GUICreatorAPI a = TheAPI.getGUICreatorAPI(p);
				// REQUIRED
				a.setSize(54);
				a.setTitle("&eTheAPI v" + TheAPI.getPluginsManagerAPI().getVersion("TheAPI"));

				// Frame
				ItemCreatorAPI iCreator = TheAPI.getItemCreatorAPI(Material.BLACK_STAINED_GLASS_PANE);
				iCreator.setDisplayName(" ");
				ItemStack item = iCreator.create();
				HashMap<Options, Object> setting = new HashMap<Options, Object>();
				setting.put(Options.CANT_BE_TAKEN, true);
				setting.put(Options.CANT_PUT_ITEM, true);
				for (int i = 0; i < 10; ++i)
					a.setItem(i, item, setting);
				a.setItem(17, item, setting);
				a.setItem(18, item, setting);
				a.setItem(26, item, setting);
				a.setItem(27, item, setting);
				a.setItem(35, item, setting);
				a.setItem(36, item, setting);
				for (int i = 44; i < 54; ++i)
					a.setItem(i, item, setting);

				// Items
				iCreator = TheAPI.getItemCreatorAPI(Material.DIAMOND);
				iCreator.setDisplayName("&eCreator of plugin");
				iCreator.addLore("");
				iCreator.addLore("&cPlugin is created by Straiker123");
				item = iCreator.create();
				a.setItem(20, item, setting);

				iCreator = TheAPI.getItemCreatorAPI(Material.EMERALD);
				iCreator.setDisplayName("&eVersion of plugin");
				iCreator.addLore("");
				iCreator.addLore("&cTheAPI v" + TheAPI.getPluginsManagerAPI().getVersion("TheAPI"));
				item = iCreator.create();
				a.setItem(22, item, setting);

				iCreator = TheAPI.getItemCreatorAPI(Material.GOLD_INGOT);
				iCreator.setDisplayName("&ePlugins using TheAPI");
				iCreator.addLore("");
				iCreator.addLore("&7---------");
				for (Plugin d : LoaderClass.plugin.getTheAPIsPlugins()) // add plugins to lore
					iCreator.addLore("&c" + d.getName() + " v" + TheAPI.getPluginsManagerAPI().getVersion(d.getName())); // get
																															// version
																															// of
																															// plugin
				iCreator.addLore("&7---------");
				item = iCreator.create();
				a.setItem(24, item, setting);

				iCreator = TheAPI.getItemCreatorAPI(Material.BARRIER);
				iCreator.setDisplayName("&cClose");
				item = iCreator.create();
				setting.put(Options.RUNNABLE, new Runnable() { // Apply this runnable on item
					@Override
					public void run() {
						a.close();
					}
				});
				a.setItem(49, item, setting);

				// REQUIRED
				a.open();
				return true;
			}
			if (eq(1, "RankingAPI")) {
				HashMap<String, Double> tops = new HashMap<String, Double>();
				TheAPI.msg("&eInput:", s);
				TheAPI.msg("&6- Straiker123, 50.0", s);
				TheAPI.msg("&6- TheAPI, 5431.6", s);
				TheAPI.msg("&6- SCR, 886.5", s);
				TheAPI.msg("&6- Houska02, 53.11", s);
				tops.put("Straiker123", 50.0);
				tops.put("TheAPI", 5431.6);
				tops.put("SCR", 886.5);
				tops.put("Houska02", 53.11);
				RankingAPI map = TheAPI.getRankingAPI(tops);
				TheAPI.msg("&eResult:", s);
				for (int i = 1; i < map.size(); ++i) { // 1 2 3 4
					TheAPI.msg("&6" + map.getPosition(map.getObject(i)) + ". " + map.getObject(i) + " with "
							+ map.getValue(map.getObject(i)) + " points", s);
				}
				return true;
			}
			if (eq(1, "multimap")) {
				MultiMap<String> map = TheAPI.getMultiMap();
				// Key, Values
				TheAPI.msg("&eInput: &6TheAPI, DevTec, Top, 1", s);
				map.put("Straiker123", "TheAPI", "DevTec", "Top", 1);
				for (Object o : map.getValues("Straiker123")) {
					TheAPI.msg("&eResult: &6" + o, s);
				}
				return true;
			}

			if (eq(1, "bossbar")) {
				TheAPI.sendBossBar(p, "&eTheAPI v" + TheAPI.getPluginsManagerAPI().getVersion("TheAPI"), 0.5, 40);
				return true;
			}
			if (eq(1, "PlayerName")) {
				String old = p.getName();
				TheAPI.msg("&eYour nickname changed to &nTheAPI", s);
				TheAPI.getNameTagAPI(p, "", "").setPlayerName("TheAPI");
				new Tasker() {
					@Override
					public void run() {
						TheAPI.getNameTagAPI(p, "", "").setPlayerName(old);
					}
				}.later(40);
				return true;
			}
			if (eq(1, "ActionBar")) {
				TheAPI.sendActionBar(p, "&eTheAPI v" + TheAPI.getPluginsManagerAPI().getVersion("TheAPI"));
				return true;
			}
			if (eq(1, "Title")) {
				TheAPI.sendTitle(p, "&eTheAPI v" + TheAPI.getPluginsManagerAPI().getVersion("TheAPI"), "");
				return true;
			}
			if (eq(1, "TabList")) {
				TheAPI.getTabListAPI().setHeaderFooter(p,
						"&eTheAPI v" + TheAPI.getPluginsManagerAPI().getVersion("TheAPI"),
						"&eTheAPI v" + TheAPI.getPluginsManagerAPI().getVersion("TheAPI"));
				return true;
			}
			if (eq(1, "Scoreboard")) {
				ScoreboardAPI a = TheAPI.getScoreboardAPI(p);
				a.setDisplayName("&eTheAPI v" + TheAPI.getPluginsManagerAPI().getVersion("TheAPI"));
				a.setLine(1, "&7Random: &c"+TheAPI.generateRandomInt(10));
				a.setLine(0, "&aBy DevTec");
				new Tasker() {
					@Override
					public void run() {
						a.destroy();
					}
				}.later(40);
				return true;
			}
			if (eq(1, "BlocksAPI")) {
				if (!r) {
					r = true;
					List<BlockSave> save = TheAPI.getBlocksAPI().getBlockSaves(TheAPI.getBlocksAPI().get(Shape.Sphere, new Position(p.getLocation()), 5,new TheMaterial("AIR")));
					TheAPI.getBlocksAPI().set(Shape.Sphere, new Position(p.getLocation()), 5, new TheMaterial("DIAMOND_BLOCK"),new TheMaterial("AIR"));
					new Tasker() {
						@Override
						public void run() {
							TheAPI.getBlocksAPI().loadBlockSaves(save);
							save.clear();
							r = false;
						}
					}.later(40);
					return true;
				}
				return true;
			}
		}
		if (eq(0, "cc") || eq(0, "clear") || eq(0, "clearcache")) {
			if (perm("ClearCache")) {
				TheAPI.msg("&7-----------------", s);
				TheAPI.msg("&6Clearing cache..", s);
				for (Player id : LoaderClass.plugin.gui.keySet())
					LoaderClass.plugin.gui.get(id).close();
				LoaderClass.plugin.gui.clear();
				TheAPI.msg("&6Cache cleared.", s);
				TheAPI.msg("&7-----------------", s);
				return true;
			}
			return true;
		}
		if (eq(0, "reload") || eq(0, "rl")) {
			if (perm("Reload")) {
				TheAPI.msg("&7-----------------", s);
				TheAPI.msg("&6Reloading configs..", s);
				for (Player p : TheAPI.getOnlinePlayers())
					TheAPI.getUser(p).config().reload();
				LoaderClass.data.reload();
				LoaderClass.config.reload();
				LoaderClass.gameapi.reload();
				LoaderClass.unused.reload();
				Tasks.unload();
				Tasks.load();
				TheAPI.msg("&6Configs reloaded.", s);
				TheAPI.msg("&7-----------------", s);

				return true;
			}
			return true;
		}
		if (eq(0, "inf") || eq(0, "info")) {
			if (perm("Info")) {
				TheAPI.msg("&7-----------------", s);
				TheAPI.msg("&6Version: &cv" + LoaderClass.plugin.getDescription().getVersion(), s);
				if (LoaderClass.plugin.getTheAPIsPlugins().size() != 0) {
					TheAPI.msg("&6Plugins using TheAPI:", s);
					for (Plugin a : LoaderClass.plugin.getTheAPIsPlugins())
						TheAPI.msg("&7 - " + getPlugin(a), s);
				}
				TheAPI.msg("&7-----------------", s);
				return true;
			}
			return true;
		}
		if (eq(0, "worldsmanager") || eq(0, "world") || eq(0, "worlds") || eq(0, "wm") || eq(0, "mw") /** "multiworld" **/ || eq(0, "worldmanager")) {
			if (perm("WorldsManager")) {
				if (args.length == 1) {
					TheAPI.msg("&7-----------------", s);
					TheAPI.msg("&6/TheAPI WorldsManager Create <world> <generator>", s);
					TheAPI.msg("&6/TheAPI WorldsManager Delete <world>", s);
					TheAPI.msg("&6/TheAPI WorldsManager Unload <world>", s);
					TheAPI.msg("&6/TheAPI WorldsManager Load <world> <generator>", s);
					TheAPI.msg("&6/TheAPI WorldsManager Teleport <world> [player]", s);
					TheAPI.msg("&6/TheAPI WorldsManager Save <world>", s);
					TheAPI.msg("&6/TheAPI WorldsManager SaveAll", s);
					TheAPI.msg("&6Worlds:", s);
					for (World w : Bukkit.getWorlds())
						TheAPI.msg("&7 - &a" + w.getName(), s);
					TheAPI.msg("&7-----------------", s);
					return true;
				}
				if (eq(1, "Teleport") || eq(1, "tp")) {
					if (args.length == 2) {
						if (s instanceof Player)
							TheAPI.msg("&6/TheAPI WorldsManager Teleport <world> [player]", s);
						else
							TheAPI.msg("&6/TheAPI WorldsManager Teleport <world> <player>", s);
						TheAPI.msg("&6Worlds:", s);
						for (World w : Bukkit.getWorlds())
							TheAPI.msg("&7 - &a" + w.getName(), s);
						return true;
					}
					if (Bukkit.getWorld(args[2]) == null) {
						TheAPI.msg("&7-----------------", s);
						TheAPI.msg("&6World with name '" + args[2] + "' doesn't exists.", s);
						TheAPI.msg("&7-----------------", s);
						return true;
					}
					if (args.length == 3) {
						if (s instanceof Player) {
							try {
								((Player) s).teleport(Bukkit.getWorld(args[2]).getSpawnLocation());
							} catch (Exception e) {
								((Player) s).teleport(new Location(Bukkit.getWorld(args[2]), 60, 60, 60));
							}
							TheAPI.msg("&6Teleporting to the world " + args[2] + "..", s);
							return true;
						} else
							TheAPI.msg("&6/TheAPI WorldsManager Teleport <world> <player>", s);
						return true;
					}
					if (args.length == 4) {
						Player p = Bukkit.getPlayer(args[3]);
						if (p == null) {
							TheAPI.msg("&6Player " + args[3] + " isn't online", p);
							return true;
						}
						TheAPI.msg("&6Teleporting to the world " + args[2] + "..", p);
						TheAPI.msg("&6Teleporting player " + p.getName() + " to the world " + args[2] + "..", s);
						return true;
					}
				}
				if (eq(1, "saveall")) {
						TheAPI.msg("&7-----------------", s);
						TheAPI.msg("&6TheAPI WorldsManager saving " + (Bukkit.getWorlds().size()) + " world(s)..", s);
						for (World w : Bukkit.getWorlds())
							w.save();
						TheAPI.msg("&6Worlds saved..", s);
						TheAPI.msg("&7-----------------", s);
					return true;
				}
				if (eq(1, "save")) {
					if (args.length == 2) {
						TheAPI.msg("&7-----------------", s);
						TheAPI.msg("&6/TheAPI WorldsManager Save <world>", s);
						TheAPI.msg("&6Worlds:", s);
						for (World w : Bukkit.getWorlds())
							TheAPI.msg("&7 - &a" + w.getName(), s);
						TheAPI.msg("&7-----------------", s);
						return true;
					}

					if (Bukkit.getWorld(args[2]) == null) {
						TheAPI.msg("&7-----------------", s);
						TheAPI.msg("&6World with name '" + args[2] + "' doesn't exists.", s);
						TheAPI.msg("&7-----------------", s);
						return true;
					}

					TheAPI.msg("&7-----------------", s);
					TheAPI.msg("&6TheAPI WorldsManager saving world with name '" + args[2] + "'..", s);
					Bukkit.getWorld(args[2]).save();
					TheAPI.msg("&6World with name '" + args[2] + "' saved.", s);
					TheAPI.msg("&7-----------------", s);
					return true;
				}
				if (eq(1, "unload")) {
					if (args.length == 2) {
						TheAPI.msg("&7-----------------", s);
						TheAPI.msg("&6/TheAPI WorldsManager Unload <world>", s);
						TheAPI.msg("&6Worlds:", s);
						for (World w : Bukkit.getWorlds())
							TheAPI.msg("&7 - &a" + w.getName(), s);
						TheAPI.msg("&7-----------------", s);
						return true;
					}
					if (Bukkit.getWorld(args[2]) == null) {
						TheAPI.msg("&7-----------------", s);
						TheAPI.msg("&6World with name '" + args[2] + "' doesn't exists.", s);
						TheAPI.msg("&7-----------------", s);
						return true;
					}
					TheAPI.msg("&7-----------------", s);
					TheAPI.msg("&6TheAPI WorldsManager unloading world with name '" + args[2] + "'..", s);
					TheAPI.getWorldsManager().unloadWorld(args[2], true);

					List<String> a = LoaderClass.config.getConfig().getStringList("Worlds");
					a.remove(args[2]);
					LoaderClass.config.getConfig().set("Worlds", a);
					TheAPI.msg("&6World with name '" + args[2] + "' unloaded.", s);
					TheAPI.msg("&7-----------------", s);
					return true;
				}
				if (eq(1, "load")) {
					if (args.length == 2) {
						TheAPI.msg("&7-----------------", s);
						TheAPI.msg("&6/TheAPI WorldsManager Load <world> <generator>", s);
						TheAPI.msg("&6Generators:", s);
						for (String w : Arrays.asList("Default", "Nether", "The_End", "The_Void"))
							TheAPI.msg("&7 - &a" + w, s);
						TheAPI.msg("&7-----------------", s);
						return true;
					}
					if (Bukkit.getWorld(args[2]) != null) {
						TheAPI.msg("&7-----------------", s);
						TheAPI.msg("&6World with name '" + args[2] + "' already exists.", s);
						TheAPI.msg("&7-----------------", s);
						return true;
					}
					if (args.length == 3) {
						TheAPI.msg("&7-----------------", s);
						TheAPI.msg("&6/TheAPI WorldsManager Load <world> <generator>", s);
						TheAPI.msg("&6Generators:", s);
						for (String w : Arrays.asList("Default", "Nether", "The_End", "The_Void"))
							TheAPI.msg("&7 - &a" + w, s);
						TheAPI.msg("&7-----------------", s);
						return true;
					}
					if (new File(Bukkit.getWorldContainer().getPath() + "/" + args[2] + "/session.lock").exists()) {
						TheAPI.msg("&7-----------------", s);
						TheAPI.msg("&6TheAPI WorldsManager loading world with name '" + args[2] + "'..", s);
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
						TheAPI.msg("&7-----------------", s);
						TheAPI.msg("&6TheAPI WorldsManager creating new world with name '" + args[2]
								+ "' using generator '" + args[3] + "'..", s);
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
						TheAPI.getWorldsManager().load(args[2], env, wt);
						List<String> a = LoaderClass.config.getStringList("Worlds");
						a.add(args[2]);
						LoaderClass.config.set("Worlds", a);
						LoaderClass.config.save();
						regWorld(args[2], generator);
						TheAPI.msg("&6World with name '" + args[2] + "' loaded.", s);
						TheAPI.msg("&7-----------------", s);
						return true;
					}
					TheAPI.msg("&7-----------------", s);
					TheAPI.msg("&6World with name '" + args[2] + "' doesn't exists.", s);
					TheAPI.msg("&7-----------------", s);
					return true;
				}
				if (eq(1, "delete")) {
					if (args.length == 2) {
						TheAPI.msg("&7-----------------", s);
						TheAPI.msg("&6/TheAPI WorldsManager Delete <world>", s);
						TheAPI.msg("&6Worlds:", s);
						for (World w : Bukkit.getWorlds())
							TheAPI.msg("&7 - &a" + w.getName(), s);
						TheAPI.msg("&7-----------------", s);
						return true;
					}
					if (Bukkit.getWorld(args[2]) == null) {
						TheAPI.msg("&7-----------------", s);
						TheAPI.msg("&6World with name '" + args[2] + "' doesn't exists.", s);
						TheAPI.msg("&7-----------------", s);
						return true;
					}
					TheAPI.msg("&7-----------------", s);
					TheAPI.msg("&6TheAPI WorldsManager deleting world with name '" + args[2] + "'..", s);
					TheAPI.getWorldsManager().delete(Bukkit.getWorld(args[2]), true);
					List<String> a = LoaderClass.config.getConfig().getStringList("Worlds");
					if(a.contains(args[2])) {
					a.remove(args[2]);
					LoaderClass.config.set("Worlds", a);
					LoaderClass.config.save();
					}
					unregWorld(args[2]);
					TheAPI.msg("&6World with name '" + args[2] + "' deleted.", s);
					TheAPI.msg("&7-----------------", s);
					return true;
				}
				if (eq(1, "create")) {
					if (args.length == 2) {
						TheAPI.msg("&7-----------------", s);
						TheAPI.msg("&6/TheAPI WorldsManager Create <world> <generator>", s);
						TheAPI.msg("&6Generators:", s);
						for (String w : Arrays.asList("Default", "Nether", "The_End", "The_Void"))
							TheAPI.msg("&7 - &a" + w, s);
						TheAPI.msg("&7-----------------", s);
						return true;
					}
					if (Bukkit.getWorld(args[2]) != null) {
						TheAPI.msg("&7-----------------", s);
						TheAPI.msg("&6World with name '" + args[2] + "' already exists.", s);
						TheAPI.msg("&7-----------------", s);
						return true;
					}

					if (args.length == 3) {
						TheAPI.msg("&7-----------------", s);
						TheAPI.msg("&6/TheAPI WorldsManager Create " + args[2] + " <generator>", s);
						TheAPI.msg("&6Generators:", s);
						for (String w : Arrays.asList("Default", "Nether", "The_End", "The_Void", "Flat"))
							TheAPI.msg("&7 - &a" + w, s);
						TheAPI.msg("&7-----------------", s);
						return true;
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
					TheAPI.msg("&7-----------------", s);
					TheAPI.msg("&6TheAPI WorldsManager creating new world with name '" + args[2] + "' using generator '"
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
					LoaderClass.config.save();
				    }
					regWorld(args[2], generator);
					TheAPI.getWorldsManager().create(args[2], env, wt, true, 0);
					TheAPI.msg("&6World with name '" + args[2] + "' created.", s);
					TheAPI.msg("&7-----------------", s);
					return true;
				}
				TheAPI.msg("&7-----------------", s);
				TheAPI.msg("&6/TheAPI WorldsManager Create <world> <generator>", s);
				TheAPI.msg("&6/TheAPI WorldsManager Delete <world>", s);
				TheAPI.msg("&6/TheAPI WorldsManager Unload <world>", s);
				TheAPI.msg("&6/TheAPI WorldsManager Load <world> <generator>", s);
				TheAPI.msg("&6/TheAPI WorldsManager Teleport <world> [player]", s);
				TheAPI.msg("&6/TheAPI WorldsManager Save <world>", s);
				TheAPI.msg("&6/TheAPI WorldsManager SaveAll", s);
				TheAPI.msg("&6Worlds:", s);
				for (World w : Bukkit.getWorlds())
					TheAPI.msg("&7 - &a" + w.getName(), s);
				TheAPI.msg("&7-----------------", s);
				return true;

			}
			return true;
		}
		return false;
	}

	List<String> getWorlds() {
		List<String> list = new ArrayList<String>();
		for (World w : Bukkit.getWorlds())
			list.add(w.getName());
		return list;
	}

	@Override
	public List<String> onTabComplete(CommandSender s, Command arg1, String arg2, String[] args) {
		List<String> c = new ArrayList<>();
		if (args.length == 1) {
			if (s.hasPermission("TheAPI.Command.Info"))
				c.addAll(StringUtil.copyPartialMatches(args[0], Arrays.asList("Info"), new ArrayList<>()));
			if (s.hasPermission("TheAPI.Command.Reload"))
				c.addAll(StringUtil.copyPartialMatches(args[0], Arrays.asList("Reload"), new ArrayList<>()));
			if (s.hasPermission("TheAPI.Command.ClearCache"))
				c.addAll(StringUtil.copyPartialMatches(args[0], Arrays.asList("ClearCache"), new ArrayList<>()));
			if (s.hasPermission("TheAPI.Command.WorldsManager"))
				c.addAll(StringUtil.copyPartialMatches(args[0], Arrays.asList("WorldsManager"), new ArrayList<>()));
			if (s.isOp())
				c.addAll(StringUtil.copyPartialMatches(args[0], Arrays.asList("Test"), new ArrayList<>()));
		}
		if (args[0].equalsIgnoreCase("Test") && s.isOp()) {
			if (args.length == 2) {
				c.addAll(StringUtil.copyPartialMatches(args[1],
						Arrays.asList("ActionBar", "hideShowEntity", "BlocksAPI", "BossBar", "MultiMap", "PlayerName",
								"RankingAPI", "Scoreboard", "ScoreboardV2", "TabList", "Title", "GUICreatorAPI"),
						new ArrayList<>()));
			}
		}
		if (args[0].equalsIgnoreCase("WorldsManager") && s.hasPermission("TheAPI.Command.WorldsManager")) {
			if (args.length == 2) {
				c.addAll(StringUtil.copyPartialMatches(args[1],
						Arrays.asList("Create", "Delete", "Load", "Teleport", "Unload", "Save", "SaveAll"),
						new ArrayList<>()));
			}
			if (args.length >= 3) {
				if (args[1].equalsIgnoreCase("Create") || args[1].equalsIgnoreCase("Load")) {
					if (args.length == 3)
						return Arrays.asList("?");
					if (args.length == 4)
						c.addAll(StringUtil.copyPartialMatches(args[3],
								Arrays.asList("Default", "Nether", "The_End", "The_Void", "Flat"), new ArrayList<>()));
				}
				if (args[1].equalsIgnoreCase("Teleport")) {
					if (args.length == 3)
						c.addAll(StringUtil.copyPartialMatches(args[1], getWorlds(), new ArrayList<>()));
					if (args.length == 4)
						return null;
				}
				if (args[1].equalsIgnoreCase("Unload") || args[1].equalsIgnoreCase("Delete")
						|| args[1].equalsIgnoreCase("Save")) {
					if (args.length == 3)
						c.addAll(StringUtil.copyPartialMatches(args[1], getWorlds(), new ArrayList<>()));
				}
			}
		}
		return c;
	}

}
