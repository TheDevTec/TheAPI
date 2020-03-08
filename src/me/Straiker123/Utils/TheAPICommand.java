package me.Straiker123.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.StringUtil;

import me.Straiker123.BlockSave;
import me.Straiker123.BlocksAPI;
import me.Straiker123.BlocksAPI.Shape;
import me.Straiker123.LoaderClass;
import me.Straiker123.MultiMap;
import me.Straiker123.RankingAPI;
import me.Straiker123.ScoreboardAPI;
import me.Straiker123.ScoreboardAPIV2;
import me.Straiker123.TheAPI;
import me.Straiker123.TheRunnable;

public class TheAPICommand implements CommandExecutor, TabCompleter {
	
	private String getPlugin(Plugin a) {
		if(a.isEnabled())return "&a"+a.getName();
		return "&c"+a.getName()+" &7(Disabled)";
	}
	String[] args;
	private boolean eq(int i, String s) {
		return args[i].equalsIgnoreCase(s);
	}
	boolean r;
	CommandSender s;
	private boolean perm(String p) {
		if(s.hasPermission("TheAPI.Command."+p))return true;
		TheAPI.msg("&cYou do not have permission '&4TheAPI.Command."+p+"&c' to do that!",s);
		return false;
	}

	private void regWorld(String w, String gen) {
		LoaderClass.config.getConfig().set("WorldsSetting."+w+".Generator", gen);
		LoaderClass.config.getConfig().set("WorldsSetting."+w+".GenerateStructures", true);
	}
	private void unregWorld(String w) {
		LoaderClass.config.getConfig().set("WorldsSetting."+w, null);
	}
	
	//Info, Reload, ClearCache, WorldsManager
	@Override
	public boolean onCommand(CommandSender s, Command arg1, String arg2, String[] args) {
		this.args=args;
		this.s=s;
		if(args.length==0) {
			TheAPI.msg("&7-----------------",s);
			if(s.hasPermission("TheAPI.Command.Info"))
				TheAPI.msg("&6/TheAPI Info",s);
			if(s.hasPermission("TheAPI.Command.Reload"))
				TheAPI.msg("&6/TheAPI Reload",s);
			if(s.hasPermission("TheAPI.Command.WorldsManager"))
				TheAPI.msg("&6/TheAPI WorldsManager",s);
			if(s.hasPermission("TheAPI.Command.ClearCache"))
				TheAPI.msg("&6/TheAPI ClearCache",s);
			if(s.isOp())
				TheAPI.msg("&6/TheAPI Test",s);
						TheAPI.msg("&6Credits: TheAPI created by DevTec",s);
			TheAPI.msg("&7-----------------",s);
			return true;
		}
		if(eq(0,"test")) {
			if(!s.isOp() || !(s instanceof Player))return true; //sender must be player & has op
			Player p = (Player)s;
			if(args.length==1) {
				TheAPI.msg("&7-----------------",s);
				TheAPI.msg("&6/TheAPI Test ActionBar",s);
			TheAPI.msg("&6/TheAPI Test BlocksAPI",s);
			TheAPI.msg("&6/TheAPI Test BossBar",s);
			TheAPI.msg("&6/TheAPI Test MultiMap",s);
			TheAPI.msg("&6/TheAPI Test PlayerName",s);
			TheAPI.msg("&6/TheAPI Test RankingAPI",s);//new
			TheAPI.msg("&6/TheAPI Test Scoreboard",s);
			TheAPI.msg("&6/TheAPI Test ScoreboardV2",s); //new
			TheAPI.msg("&6/TheAPI Test TabList",s);
			TheAPI.msg("&6/TheAPI Test Title",s);
			TheAPI.msg("&7-----------------",s);
			return true;
			}
			if(eq(1,"RankingAPI")) {
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
				for(int i = 1; i < map.size(); ++i) { //1 2 3 4
					TheAPI.msg("&6"+map.getPosition(map.getObject(i))+". "+map.getObject(i)+" with "+map.getValue(map.getObject(i))+" points", s);
				}
				return true;
			}
			if(eq(1,"ScoreboardV2")) {
				ScoreboardAPIV2 a = TheAPI.getScoreboardAPIV2(p);
				a.setTitle("&eTheAPI v"+TheAPI.getPluginsManagerAPI().getVersion("TheAPI"));
				TheRunnable r = TheAPI.getRunnable();
				r.runRepeating(new Runnable() {
					int times=0;
					public void run() {
						if(times == 10) {
							r.cancel();
						p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
							return;
						}
						a.addLine("&aBy DevTec");
						a.addLine("&eRandom numbers:");
						a.addLine("&6"+TheAPI.generateRandomDouble(200));
						a.create();
						++times;
					}
				}, 10); 
				return true;
			}
			if(eq(1,"multimap")) {
				MultiMap map = TheAPI.getMultiMap();
				//Key, Values
				TheAPI.msg("&eInput: &6TheAPI, DevTec, Top, 1", s);
				map.put("Straiker123", "TheAPI", "DevTec","Top",1);
				for(Object o : map.getValues("Straiker123")) {
					TheAPI.msg("&eResult: &6"+o, s);
				}
				return true;
			}
			
			if(eq(1,"bossbar")) {
				TheAPI.sendBossBar(p, "&eTheAPI v"+TheAPI.getPluginsManagerAPI().getVersion("TheAPI"), 0.5, 40);
				return true;
			}
			if(eq(1,"PlayerName")) {
				String old = p.getName();
				TheAPI.msg("&eYour nickname changed to &nTheAPI",s);
				TheAPI.getNameTagAPI(p, "", "").setPlayerName("TheAPI");
				Bukkit.getScheduler().runTaskLater(LoaderClass.plugin, new Runnable() {
					@Override
					public void run() {
						TheAPI.getNameTagAPI(p, "", "").setPlayerName(old);
					}
				}, 40);
				return true;
			}
			if(eq(1,"ActionBar")) {
				TheAPI.sendActionBar(p, "&eTheAPI v"+TheAPI.getPluginsManagerAPI().getVersion("TheAPI"));
				return true;
			}
			if(eq(1,"Title")) {
				TheAPI.sendTitle(p, "&eTheAPI v"+TheAPI.getPluginsManagerAPI().getVersion("TheAPI"),"");
				return true;
			}
			if(eq(1,"TabList")) {
				TheAPI.getTabListAPI().setHeaderFooter(p, "&eTheAPI v"+TheAPI.getPluginsManagerAPI().getVersion("TheAPI"), "&eTheAPI v"+TheAPI.getPluginsManagerAPI().getVersion("TheAPI"));
				return true;
			}
			if(eq(1,"Scoreboard")) {
				ScoreboardAPI a = TheAPI.getScoreboardAPI(p);
				a.setTitle("&eTheAPI v"+TheAPI.getPluginsManagerAPI().getVersion("TheAPI"));
				a.addLine("&aBy DevTec", 0);
				a.create();
				Bukkit.getScheduler().runTaskLater(LoaderClass.plugin, new Runnable() {
					@Override
					public void run() {
						p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
					}
				}, 40);
				return true;
			}
			if(eq(1,"BlocksAPI")) {
				if(!r) {
					r=true;
				BlocksAPI a = TheAPI.getBlocksAPI();
				List<Block> d =a.getBlocks(Shape.Sphere, p.getLocation(), 5,Material.AIR);
				List<BlockSave> save = new ArrayList<BlockSave>();
				for(Block b : d) {
					save.add(a.getBlockSave(b));
					TheAPI.getBlocksAPI().setBlock(b.getLocation(), Material.DIAMOND_BLOCK);
				}
				d.clear();
				Bukkit.getScheduler().runTaskLater(LoaderClass.plugin, new Runnable() { // undo command ?
					@Override
					public void run() {
						if(save.isEmpty()==false)
						a.setBlockSaves(save);
						r=false;
					}
				}, 40);
				return true;
				}
				return true;
			}
		}
		if(eq(0,"cc")||eq(0,"clear")||eq(0,"clearcache")) {
			if(perm("ClearCache")) {
				TheAPI.msg("&7-----------------",s);
				TheAPI.msg("&6Clearing cache..",s);
				for(Player id : LoaderClass.gui.keySet()) {
					LoaderClass.gui.get(id).close();
				}
				LoaderClass.gui.clear();
				TheAPI.msg("&6Cache cleared.",s);
				TheAPI.msg("&7-----------------",s);
				return true;
			}
			return true;
		}
		if(eq(0,"reload")||eq(0,"rl")) {
		if(perm("Reload")) {
			TheAPI.msg("&7-----------------",s);
			TheAPI.msg("&6Reloading configs..",s);
			LoaderClass.data.reload();
			LoaderClass.config.reload();
			LoaderClass.gameapi.reload();
			LoaderClass.unused.reload();
			Tasks.unload();
			Tasks.load();
			TheAPI.msg("&6Configs reloaded.",s);
			TheAPI.msg("&7-----------------",s);
			
			return true;
			}
		return true;
		}
		if(eq(0,"inf")||eq(0,"info")) {
			if(perm("Info")) {
				TheAPI.msg("&7-----------------",s);
				TheAPI.msg("&6Version: &cv"+LoaderClass.plugin.getDescription().getVersion(),s);
				if(LoaderClass.plugin.getTheAPIsPlugins().size()!=0) {
				TheAPI.msg("&6Plugins using TheAPI:",s);
				for(Plugin a: LoaderClass.plugin.getTheAPIsPlugins())
				TheAPI.msg("&7 - "+getPlugin(a),s);
				}
				TheAPI.msg("&7-----------------",s);
				return true;
				}return true;
		}
		if(eq(0,"worldsmanager")||eq(0,"world")||eq(0,"worlds")||eq(0,"wm")||eq(0,"worldmanager")) {
			if(perm("WorldsManager")) {
				if(args.length==1) {
				TheAPI.msg("&7-----------------",s);
				TheAPI.msg("&6/TheAPI WorldsManager Create <world> <generator>",s);
				TheAPI.msg("&6/TheAPI WorldsManager Delete <world>",s);
				TheAPI.msg("&6/TheAPI WorldsManager Unload <world>",s);
				TheAPI.msg("&6/TheAPI WorldsManager Load <world> <generator>",s);
				TheAPI.msg("&6/TheAPI WorldsManager Save <world>",s);
				TheAPI.msg("&6/TheAPI WorldsManager SaveAll",s);
				TheAPI.msg("&6Worlds:",s);
				for(World w : Bukkit.getWorlds())
				TheAPI.msg("&7 - &a"+w.getName(),s);
				TheAPI.msg("&7-----------------",s);
				return true;
				}
				if(eq(1,"saveall")) {
					TheAPI.msg("&7-----------------",s);
					TheAPI.msg("&6TheAPI WorldsManager saving all worlds..",s);
					for(World w:Bukkit.getWorlds())w.save();
					TheAPI.msg("&6All worlds saved.",s);
					TheAPI.msg("&7-----------------",s);
					return true;
				}
				if(eq(1,"save")) {
					if(args.length==2) {
						TheAPI.msg("&7-----------------",s);
						TheAPI.msg("&6/TheAPI WorldsManager Save <world>",s);
						TheAPI.msg("&6Worlds:",s);
						for(World w : Bukkit.getWorlds())
						TheAPI.msg("&7 - &a"+w.getName(),s);
						TheAPI.msg("&7-----------------",s);
						return true;
					}
					
					if(Bukkit.getWorld(args[2])==null) {
						TheAPI.msg("&7-----------------",s);
						TheAPI.msg("&6World with name '"+args[2]+"' doesn't exists.",s);
						TheAPI.msg("&7-----------------",s);
						return true;
					}
					
					TheAPI.msg("&7-----------------",s);
					TheAPI.msg("&6TheAPI WorldsManager saving world with name '"+args[2]+"'..",s);
					Bukkit.getWorld(args[2]).save();
					TheAPI.msg("&6World with name '"+args[2]+"' saved.",s);
					TheAPI.msg("&7-----------------",s);
					return true;
				}
				if(eq(1,"unload")) {
					if(args.length==2) {
						TheAPI.msg("&7-----------------",s);
						TheAPI.msg("&6/TheAPI WorldsManager Unload <world>",s);
						TheAPI.msg("&6Worlds:",s);
						for(World w : Bukkit.getWorlds())
						TheAPI.msg("&7 - &a"+w.getName(),s);
						TheAPI.msg("&7-----------------",s);
						return true;
					}
					if(Bukkit.getWorld(args[2])==null) {
						TheAPI.msg("&7-----------------",s);
						TheAPI.msg("&6World with name '"+args[2]+"' doesn't exists.",s);
						TheAPI.msg("&7-----------------",s);
						return true;
					}
					TheAPI.msg("&7-----------------",s);
					TheAPI.msg("&6TheAPI WorldsManager unloading world with name '"+args[2]+"'..",s);
					TheAPI.getWorldsManager().unloadWorld(args[2], true);

					List<String> a = LoaderClass.config.getConfig().getStringList("Worlds");
					a.remove(args[2]);
					LoaderClass.config.getConfig().set("Worlds", a);
					TheAPI.msg("&6World with name '"+args[2]+"' unloaded.",s);
					TheAPI.msg("&7-----------------",s);
					return true;
				}
				if(eq(1,"load")) {
					if(args.length==2) {
						TheAPI.msg("&7-----------------",s);
						TheAPI.msg("&6/TheAPI WorldsManager Load <world> <generator>",s);
						TheAPI.msg("&6Generators:",s);
						for(String w:Arrays.asList("Default","Nether","The_End","The_Void"))
						TheAPI.msg("&7 - &a"+w,s);
						TheAPI.msg("&7-----------------",s);
						return true;
					}
					if(Bukkit.getWorld(args[2])!=null) {
						TheAPI.msg("&7-----------------",s);
						TheAPI.msg("&6World with name '"+args[2]+"' already exists.",s);
						TheAPI.msg("&7-----------------",s);
						return true;
					}
					if(args.length==3) {
						TheAPI.msg("&7-----------------",s);
						TheAPI.msg("&6/TheAPI WorldsManager Load <world> <generator>",s);
						TheAPI.msg("&6Generators:",s);
						for(String w:Arrays.asList("Default","Nether","The_End","The_Void"))
						TheAPI.msg("&7 - &a"+w,s);
						TheAPI.msg("&7-----------------",s);
						return true;
					}
					String generator = args[3];
					String type="Default";
					boolean i=false;
					for(String w:Arrays.asList("Default","Normal","Nether","The_End"
							,"End","The_Void","Void","Empty","Flat")) {
					if(generator.equalsIgnoreCase(w)) {
						i=true;
						if(w.equals("Flat"))type="Flat";
						if(w.equals("Nether"))type="Nether";
						if(w.equals("The_End")||w.equals("End"))type="The_End";
						if(w.equals("The_Void")||w.equals("Void")||w.equals("Empty"))type="The_Void";
						break;
					}
					}
					if(i) {
						if(new File(Bukkit.getWorldContainer().getPath()+"/"+args[2]+"/session.lock").exists()) {
					TheAPI.msg("&7-----------------",s);
					TheAPI.msg("&6TheAPI WorldsManager loading world with name '"+args[2]+"'..",s);
					Environment env = Environment.NORMAL;
					WorldType wt= WorldType.NORMAL;

					if(type.equals("Flat"))wt=WorldType.FLAT;
					if(type.equals("The_Void"))env=null;
					if(type.equals("The_End")) {
						try {
						env=Environment.valueOf("THE_END");
						}catch(Exception e) {
							env=Environment.valueOf("END");
						}
					}
					if(type.equals("Nether"))env=Environment.NETHER;
					TheAPI.getWorldsManager().load(args[2], env, wt);
					List<String> a = LoaderClass.config.getConfig().getStringList("Worlds");
					a.add(args[2]);
					LoaderClass.config.getConfig().set("Worlds", a);
					regWorld(args[2],type);
					TheAPI.msg("&6World with name '"+args[2]+"' loaded.",s);
					TheAPI.msg("&7-----------------",s);
					return true;
						}
							TheAPI.msg("&7-----------------",s);
							TheAPI.msg("&6World with name '"+args[2]+"' doesn't exists.",s);
							TheAPI.msg("&7-----------------",s);
							return true;
					}
					TheAPI.msg("&7-----------------",s);
					TheAPI.msg("&6/TheAPI WorldsManager Load "+args[2]+" <generator>",s);
					TheAPI.msg("&6Generators:",s);
					for(String w:Arrays.asList("Default","Nether","The_End","The_Void"))
					TheAPI.msg("&7 - &a"+w,s);
					TheAPI.msg("&7-----------------",s);
					return true;
				}
				if(eq(1,"delete")) {
					if(args.length==2) {
						TheAPI.msg("&7-----------------",s);
						TheAPI.msg("&6/TheAPI WorldsManager Delete <world>",s);
						TheAPI.msg("&6Worlds:",s);
						for(World w : Bukkit.getWorlds())
						TheAPI.msg("&7 - &a"+w.getName(),s);
						TheAPI.msg("&7-----------------",s);
						return true;
					}
					if(Bukkit.getWorld(args[2])==null) {
						TheAPI.msg("&7-----------------",s);
						TheAPI.msg("&6World with name '"+args[2]+"' doesn't exists.",s);
						TheAPI.msg("&7-----------------",s);
						return true;
					}
					TheAPI.msg("&7-----------------",s);
					TheAPI.msg("&6TheAPI WorldsManager deleting world with name '"+args[2]+"'..",s);
					TheAPI.getWorldsManager().delete(Bukkit.getWorld(args[2]), true);
					List<String> a = LoaderClass.config.getConfig().getStringList("Worlds");
					a.remove(args[2]);
					LoaderClass.config.getConfig().set("Worlds", a);
					unregWorld(args[2]);
					TheAPI.msg("&6World with name '"+args[2]+"' deleted.",s);
					TheAPI.msg("&7-----------------",s);
					return true;
				}
				if(eq(1,"create")) {
					if(args.length==2) {
						TheAPI.msg("&7-----------------",s);
						TheAPI.msg("&6/TheAPI WorldsManager Create <world> <generator>",s);
						TheAPI.msg("&6Generators:",s);
						for(String w:Arrays.asList("Default","Nether","The_End","The_Void"))
						TheAPI.msg("&7 - &a"+w,s);
						TheAPI.msg("&7-----------------",s);
						return true;
					}
					if(Bukkit.getWorld(args[2])!=null) {
						TheAPI.msg("&7-----------------",s);
						TheAPI.msg("&6World with name '"+args[2]+"' already exists.",s);
						TheAPI.msg("&7-----------------",s);
						return true;
					}
					
					if(args.length==3) {
						TheAPI.msg("&7-----------------",s);
						TheAPI.msg("&6/TheAPI WorldsManager Create "+args[2]+" <generator>",s);
						TheAPI.msg("&6Generators:",s);
						for(String w:Arrays.asList("Default","Nether","The_End","The_Void","Flat"))
						TheAPI.msg("&7 - &a"+w,s);
						TheAPI.msg("&7-----------------",s);
						return true;
					}
					String generator = args[3];
					String type="Default";
					boolean i=false;
					for(String w:Arrays.asList("Default","Normal","Nether","The_End"
							,"End","The_Void","Void","Empty","Flat")) {
					if(generator.equalsIgnoreCase(w)) {
						i=true;
						if(w.equals("Flat"))type="Flat";
						if(w.equals("Nether"))type="Nether";
						if(w.equals("The_End")||w.equals("End"))type="The_End";
						if(w.equals("The_Void")||w.equals("Void")||w.equals("Empty"))type="The_Void";
						break;
					}
					}
					if(i) {
						TheAPI.msg("&7-----------------",s);
						TheAPI.msg("&6TheAPI WorldsManager creating new world with name '"+args[2]+"' using generator '"+type+"'..",s);
						Environment env = Environment.NORMAL;
						WorldType wt= WorldType.NORMAL;

						if(type.equals("Flat"))wt=WorldType.FLAT;
						if(type.equals("The_Void"))env=null;
						if(type.equals("The_End")) {
							try {
							env=Environment.valueOf("THE_END");
							}catch(Exception e) {
								env=Environment.valueOf("END");
							}
						}
						if(type.equals("Nether"))env=Environment.NETHER;
						
						TheAPI.getWorldsManager().create(args[2], env, wt, true, 0);
						List<String> a = LoaderClass.config.getConfig().getStringList("Worlds");
						a.add(args[2]);
						LoaderClass.config.getConfig().set("Worlds", a);
						regWorld(args[2],type);
						TheAPI.msg("&6World with name '"+args[2]+"' created.",s);
						TheAPI.msg("&7-----------------",s);
						return true;
					}
					TheAPI.msg("&7-----------------",s);
					TheAPI.msg("&6/TheAPI WorldsManager Create "+args[2]+" <generator>",s);
					TheAPI.msg("&6Generators:",s);
					for(String w:Arrays.asList("Default","Nether","The_End","The_Void","Flat"))
					TheAPI.msg("&7 - &a"+w,s);
					TheAPI.msg("&7-----------------",s);
					return true;
				}
				TheAPI.msg("&7-----------------",s);
				TheAPI.msg("&6/TheAPI WorldsManager Create <world> <generator>",s);
				TheAPI.msg("&6/TheAPI WorldsManager Delete <world>",s);
				TheAPI.msg("&6/TheAPI WorldsManager Unload <world>",s);
				TheAPI.msg("&6/TheAPI WorldsManager Load <world> <generator>",s);
				TheAPI.msg("&6/TheAPI WorldsManager Save <world>",s);
				TheAPI.msg("&6/TheAPI WorldsManager SaveAll",s);
				TheAPI.msg("&6Worlds:",s);
				for(World w : Bukkit.getWorlds())
				TheAPI.msg("&7 - &a"+w.getName(),s);
				TheAPI.msg("&7-----------------",s);
				return true;
				
				}return true;
		}
		return false;
	}
	List<String> getWorlds(){
		List<String> list = new ArrayList<String>();
		for(World w :Bukkit.getWorlds())list.add(w.getName());
		return list;
	}
	@Override
	public List<String> onTabComplete(CommandSender s, Command arg1, String arg2, String[] args) {
		List<String> c = new ArrayList<>();
		if(args.length==1) {
			if(s.hasPermission("TheAPI.Command.Info"))
		    	c.addAll(StringUtil.copyPartialMatches(args[0], Arrays.asList("Info"), new ArrayList<>()));
			if(s.hasPermission("TheAPI.Command.Reload"))
		    	c.addAll(StringUtil.copyPartialMatches(args[0], Arrays.asList("Reload"), new ArrayList<>()));
			if(s.hasPermission("TheAPI.Command.ClearCache"))
		    	c.addAll(StringUtil.copyPartialMatches(args[0], Arrays.asList("ClearCache"), new ArrayList<>()));
			if(s.hasPermission("TheAPI.Command.WorldsManager"))
		    	c.addAll(StringUtil.copyPartialMatches(args[0], Arrays.asList("WorldsManager"), new ArrayList<>()));
			if(s.isOp())
		    	c.addAll(StringUtil.copyPartialMatches(args[0], Arrays.asList("Test"), new ArrayList<>()));
		}
		if(args[0].equalsIgnoreCase("Test") && s.isOp()) {
			if(args.length==2) {
				c.addAll(StringUtil.copyPartialMatches(args[1], Arrays.asList("ActionBar","BlocksAPI","BossBar","MultiMap","PlayerName","RankingAPI","Scoreboard","ScoreboardV2","TabList","Title"), new ArrayList<>()));
				}
		}
		if(args[0].equalsIgnoreCase("WorldsManager") && s.hasPermission("TheAPI.Command.WorldsManager")) {
			if(args.length==2) {
		    	c.addAll(StringUtil.copyPartialMatches(args[1], Arrays.asList("Create","Delete","Load","Unload","Save","SaveAll"), new ArrayList<>()));
			}
			if(args.length>=3) {
			if(args[1].equalsIgnoreCase("Create")||args[1].equalsIgnoreCase("Load")) {
				if(args.length==3)
				return Arrays.asList("?");
				if(args.length==4)
				 	c.addAll(StringUtil.copyPartialMatches(args[3], Arrays.asList("Default","Nether","The_End","The_Void","Flat"), new ArrayList<>()));
			}
			if(args[1].equalsIgnoreCase("Unload")||args[1].equalsIgnoreCase("Delete")||args[1].equalsIgnoreCase("Save")) {
				if(args.length==3)
					c.addAll(StringUtil.copyPartialMatches(args[1], getWorlds(), new ArrayList<>()));
				}}
		}
		return c;
	}

}
