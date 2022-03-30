package me.devtec.theapi.bungee.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import me.devtec.shared.utility.StringUtils;
import me.devtec.theapi.bungee.commands.selectors.SelectorType;
import me.devtec.theapi.bungee.commands.selectors.Utils;
import me.devtec.theapi.bungee.commands.structures.CommandStructure;
import me.devtec.theapi.bungee.commands.structures.EmptyCommandStructure;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.command.PlayerCommand;

public class CommandHolder {
	static final Map<Integer, String> EMPTY_MAP = Collections.unmodifiableMap(new HashMap<>());
	static final PermissionsChecker DEFAULT_PERMISSIONS_CHECKER = (player, perm) -> {return player.hasPermission(perm);};
	
	private CommandStructure[] args;
	private PermissionsChecker check = DEFAULT_PERMISSIONS_CHECKER;
	
	public CommandHolder(CommandStructure[] structure) {
		for(int i = 0; i < structure.length; ++i) {
			structure[i]=structure[i].first();
		}
		args=structure;
	}

	public static CommandHolder create(CommandStructure... structure) {
		return new CommandHolder(structure);
	}
	
	public boolean process(CommandSender sender, String[] s) {
		if(s.length==0) {
			for(CommandStructure str : args) {
				if(str instanceof EmptyCommandStructure) {
					if(str.getPermission()!=null && !check.check(sender, str.getPermission()))
						return true;
					str.getCommandArguments().get(-1).process(sender, EMPTY_MAP);
					return true;
				}
			}
			return true;
		}
		
		String first = s[0];
		for(CommandStructure str : args) {
			boolean contains = false;
			if(str instanceof EmptyCommandStructure)continue;
			for(String arg : str.getArgs()) {
				if(arg.equalsIgnoreCase(first)) {
					contains = true;
					break;
				}
			}
			if(!contains)continue;
			
			if(str.getPermission()!=null && !check.check(sender, str.getPermission()))
				return true;
			
			Map<Integer, CompletableFuture<Iterable<String>>[]> custom = new HashMap<>(str.getCustomSelectors());
			Map<Integer, SelectorType[]> normal = new HashMap<>(str.getSelectorTypes());
			Map<Integer, CommandTask> arg = new HashMap<>(str.getCommandArguments());
			Map<Integer, CommandTask> argmore = new HashMap<>(str.getCommandArgumentsMoreThan());
			Map<Integer, CommandTask> argless = new HashMap<>(str.getCommandArgumentsLessThan());
			CommandStructure current = str;
			
			//Build
			while(current!=null) {
				if((current.getPermission()==null||check.check(sender, current.getPermission())) && current.getStartArg() <= s.length-1) {
					if(current.getStartArg() > s.length-1)break;
					if(current.getArgs()!=null) {
						contains = false;
						for(String arg2 : current.getArgs()) {
							if(arg2.equalsIgnoreCase(s[current.getStartArg()])) {
								contains = true;
								break;
							}
						}
						if(!contains)break;
					}
					custom.putAll(current.getCustomSelectors());
					normal.putAll(current.getSelectorTypes());
					arg.putAll(current.getCommandArguments());
					argmore.clear();
					argmore.putAll(current.getCommandArgumentsMoreThan());
					argless.clear();
					argless.putAll(current.getCommandArgumentsLessThan());
					current=current.getNext();
				}else break;
			}
			
			//Process
			CommandTask r = find(s.length-1, arg, argmore, argless);
			if(r!=null) {
				Map<Integer, String> buildSelectors = new HashMap<>();
				for(int i = 0; i < s.length-1; ++i) {
					if(normal.containsKey(i+1)) { //todo custom check
						if(Utils.check(normal.get(i+1), s[i+1])) {
							buildSelectors.put(i+1, s[i+1]);
						}else buildSelectors.put(i+1, null);
					}else {
						if(custom.containsKey(i+1)) { //todo custom check
							try {
								boolean foundAny = false;
								for(CompletableFuture<Iterable<String>> customArgument : custom.get(i+1)) {
									if(Utils.check(customArgument.get(), s[i+1])) {
										buildSelectors.put(i+1, s[i+1]);
										foundAny=true;
									}
								}
								if(!foundAny)buildSelectors.put(i+1, null);
							} catch (Exception e) {
							}
						}
					}
				}
				if(r!=null)r.process(sender, buildSelectors);
			}
			return true;
		}
		return false;
	}

	private CommandTask find(int i, Map<Integer, CommandTask> arg, Map<Integer, CommandTask> argmore,
			Map<Integer, CommandTask> argless) {
		CommandTask any;
		if((any=arg.get(i))!=null)return any;
		
		if((any=argmore.get(i))!=null) {
			return any;
		}else {
			CommandTask nearest = null;
			int val = i;
			for(Entry<Integer, CommandTask> s : argmore.entrySet()) {
				if(val >= s.getKey()) {
					val=s.getKey();
					nearest=s.getValue();
				}
			}
			if(nearest!=null)return nearest;
		}
		if((any=argless.get(i))!=null) {
			return any;
		}else {
			CommandTask nearest = null;
			int val = i;
			for(Entry<Integer, CommandTask> s : argless.entrySet()) {
				if(val <= s.getKey()) {
					val=s.getKey();
					nearest=s.getValue();
				}
			}
			if(nearest!=null)return nearest;
		}
		
		return arg.get(-1);
	}
	
	public List<String> tabCompleter(CommandSender sender, String[] s) {
		String first = s[0];
		
		if(s.length==1) {
			List<String> result = new ArrayList<>();
			for(CommandStructure str : args) {
				if(str instanceof EmptyCommandStructure) {
					if(str.getPermission()!=null && !check.check(sender, str.getPermission()))
						return Collections.emptyList();
					continue;
				}
				if(str.getPermission() != null && !check.check(sender, str.getPermission()))continue;
				for(String arg : str.getArgs()) {
					result.add(arg);
				}
			}
			return StringUtils.copyPartialMatches(first, result);
		}
		
		for(CommandStructure str : args) {
			if(str instanceof EmptyCommandStructure)continue;
			if(str.getPermission() != null && !check.check(sender, str.getPermission()))continue;
			boolean contains = false;
			for(String arg : str.getArgs()) {
				if(arg.equalsIgnoreCase(first)) {
					contains = true;
					break;
				}
			}
			if(!contains)continue;
			Map<Integer, CompletableFuture<Iterable<String>>[]> custom = new HashMap<>(str.getCustomSelectors());
			Map<Integer, SelectorType[]> normal = new HashMap<>(str.getSelectorTypes());
			CommandStructure current = str;
			String[] args = null;
			
			int pos = 0;
			//Build
			while(current!=null) {
				if(current.getPermission()==null || check.check(sender, current.getPermission())) {
					if(s.length>current.getStartArg()) {
						custom.putAll(current.getCustomSelectors());
						normal.putAll(current.getSelectorTypes());
						++pos;
						if(current.getArgs()!=null)
							args=current.getArgs();
					}
					current=current.getNext();
				}else break;
			}
			
			if(pos-1<s.length)args=null;
			
			//Process
			if(custom.containsKey(s.length-1)) {
				try {
					List<String> text = new ArrayList<>();
					for(CompletableFuture<Iterable<String>> customArgument : custom.get(s.length-1)) {
						for(String st : customArgument.get()) {
							text.add(st);
						}
					}
					return StringUtils.copyPartialMatches(s[s.length-1], text);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					e1.printStackTrace();
				}
			}
			if(normal.containsKey(s.length-1)) {
				return StringUtils.copyPartialMatches(s[s.length-1], Utils.buildSelectorKeys(normal.get(s.length-1)));
			}
			if(args!=null) //default fallback
				return StringUtils.copyPartialMatches(s[s.length-1], Arrays.asList(args));
		}
		return Collections.emptyList();
	}
	
	public CommandHolder permsChecker(PermissionsChecker object) {
		check=object==null?DEFAULT_PERMISSIONS_CHECKER:object;
		return this;
	}

	public CommandStructure[] getCommandStructure() {
		return args;
	}

	public CommandHolder addCommandStructure(CommandStructure subcmd) {
		args=Arrays.copyOf(args, args.length+1);
		args[args.length-1]=subcmd;
		return this;
	}
	
	public CommandHolder register(Plugin plugin, String name, String... aliases) {
		PlayerCommand command = new PlayerCommand(name, "", aliases) {
			
			@Override
			public void execute(CommandSender s, String[] args) {
				process(s, args);
			}
			
			@Override
			public Iterable<String> onTabComplete(CommandSender s, String[] args) {
				return tabCompleter(s, args);
			}
			
			
		};
		ProxyServer.getInstance().getPluginManager().registerCommand(plugin, command);
		return this;
	}
}
