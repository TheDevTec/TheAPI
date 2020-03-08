package me.Straiker123.Utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import me.Straiker123.BlocksAPI.Shape;
import me.Straiker123.ItemCreatorAPI;
import me.Straiker123.LoaderClass;
import me.Straiker123.PunishmentAPI;
import me.Straiker123.TheAPI;
import me.Straiker123.TheAPI.SudoType;
import me.Straiker123.WorldBorderAPI.WarningMessageType;
import me.Straiker123.Events.DamageGodPlayerByEntityEvent;
import me.Straiker123.Events.DamageGodPlayerEvent;
import me.Straiker123.Events.GUIClickEvent;
import me.Straiker123.Events.GUICloseEvent;
import me.Straiker123.Events.PlayerJumpEvent;
import me.Straiker123.Events.TNTExplosionEvent;
import me.Straiker123.Utils.GUIID.GRunnable;

@SuppressWarnings("deprecation")
public class Events implements Listener {

	private ItemStack createWrittenBook(ItemStack a) {
		Material ms = Material.matchMaterial("WRITABLE_BOOK");
		if(ms==null)ms=Material.matchMaterial("BOOK_AND_QUILL");
		ItemCreatorAPI s = TheAPI.getItemCreatorAPI(ms);
		 if(a.getItemMeta().hasDisplayName())
		 s.setDisplayName(a.getItemMeta().getDisplayName());
		 if(a.getItemMeta().hasLore())s.setLore(a.getItemMeta().getLore());
		 if(TheAPI.isNewVersion()
				 &&!TheAPI.getServerVersion().contains("v1_13"))
		if(a.getItemMeta().hasCustomModelData()) s.setCustomModelData(a.getItemMeta().getCustomModelData());
		 if(!TheAPI.isOlder1_9()
				 &&!TheAPI.getServerVersion().contains("v1_9")
				 &&!TheAPI.getServerVersion().contains("v1_10"))
		 s.setUnbreakable(a.getItemMeta().isUnbreakable());
		 return s.create();
	}
	
	private ItemStack createHead(ItemStack a) {
		ItemCreatorAPI s = TheAPI.getItemCreatorAPI(Material.matchMaterial("PLAYER_HEAD"));
		 if(a.getItemMeta().hasDisplayName())
		 s.setDisplayName(a.getItemMeta().getDisplayName());
		 if(a.getItemMeta().hasLore())s.setLore(a.getItemMeta().getLore());
		 if(TheAPI.isNewVersion()
				 &&!TheAPI.getServerVersion().contains("v1_13"))
		if(a.getItemMeta().hasCustomModelData()) s.setCustomModelData(a.getItemMeta().getCustomModelData());
		 if(!TheAPI.isOlder1_9()
				 &&!TheAPI.getServerVersion().contains("v1_9")
				 &&!TheAPI.getServerVersion().contains("v1_10"))
		 s.setUnbreakable(a.getItemMeta().isUnbreakable());
		 return s.create();
	}
	
	private boolean isUnbreakable(ItemStack i) {
		boolean is = false;
		if(i.getItemMeta().hasLore()) {
			if(i.getItemMeta().getLore().isEmpty()==false) {
				for(String s :i.getItemMeta().getLore()) {
					if(s.equals(TheAPI.colorize("&9UNBREAKABLE")))is= true;
				}
			}
		}
		return is;
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onExplode(EntityExplodeEvent e) {
		if(f.getBoolean("Options.LagChecker.Enabled") &&
				f.getBoolean("Options.LagChecker.TNT.Use")) {
			e.setCancelled(true);
			get((e.getEntity().hasMetadata("real") ? TheAPI.getBlocksAPI().getLocationFromString(e.getEntity().getMetadata("real").get(0).asString()) : e.getLocation()),e.getLocation());
		}}
    public static boolean around(Location b){
    	boolean s = false;
    for(Block f : TheAPI.getBlocksAPI().getBlocks(Shape.Sphere, b, 1)) {
    	if(f.getType().name().contains("WATER")||f.getType().name().contains("LAVA")) {
    		s=true;
    		break;
    	}
    }
    return s;
    }
   public static void get(Location reals, Location c) {
	   TNTExplosionEvent event = new TNTExplosionEvent(c);
    	Bukkit.getPluginManager().callEvent(event);
    	if(event.isCancelled()) {
    		return;
    	}
			new Task(reals,TheAPI.getBlocksAPI().getBlocks(Shape.Sphere, c, event.getPower(), blocks(event.isNuclearBomb() && event.canNuclearDestroyLiquid())),event).start();
	}

	public static List<Material> blocks(boolean b){
    	List<Material> m = new ArrayList<Material>();
    	m.add(Material.AIR);
    	try {
    	m.add(Material.BARRIER);
    	 }catch(Exception |NoSuchFieldError e) {
    			
    		}
    	m.add(Material.BEDROCK);
    	m.add(Material.ENDER_CHEST);
    	try {
    	m.add(Material.END_PORTAL_FRAME);
    }catch(Exception |NoSuchFieldError e) {
		
	}
    	try {
    	m.add(Material.STRUCTURE_BLOCK);
    	m.add(Material.JIGSAW);
    	 }catch(Exception |NoSuchFieldError e) {
    			
    		}
    	m.add(Material.OBSIDIAN);
    	try {
    	m.add(Material.END_GATEWAY);
    }catch(Exception |NoSuchFieldError e) {
		
   	}
    	try {
    	m.add(Material.END_PORTAL);
 }catch(Exception |NoSuchFieldError e) {
		
   	}
    	try {
    	m.add(Material.COMMAND_BLOCK);
    	m.add(Material.matchMaterial("REPEATING_COMMAND_BLOCK"));
    	m.add(Material.matchMaterial("CHAIN_COMMAND_BLOCK"));
    }catch(Exception |NoSuchFieldError e) {
    	m.add(Material.matchMaterial("COMMAND"));
   	}
    	if(!b) {
    	m.add(Material.LAVA);
    	m.add(Material.matchMaterial("STATIONARY_LAVA"));
    	m.add(Material.WATER);
    	m.add(Material.matchMaterial("STATIONARY_WATER"));
    	}
    	try {
    	m.add(Material.matchMaterial("ENCHANTING_TABLE"));
    	}catch(Exception |NoSuchFieldError e) {
        	m.add(Material.matchMaterial("ENCHANTMENT_TABLE"));
       	}
    	m.add(Material.ANVIL);
    	try {
    	m.add(Material.CHIPPED_ANVIL);
    	m.add(Material.DAMAGED_ANVIL);
    	 }catch(Exception |NoSuchFieldError e) {
    			
    		}
    	try {
    	m.add(Material.matchMaterial("netherite_block"));
    	m.add(Material.matchMaterial("crying_obsidian"));
    	m.add(Material.matchMaterial("ancient_debris"));
    	}catch(Exception |NoSuchFieldError e) {
    		
    	}
    	return m;
    }
    
	public static List<Inventory> add(Location block, Location real, boolean t,List<Inventory> r, List<ItemStack> drops) {
		List<Inventory> q = r;
		if(f.getBoolean("Options.LagChecker.TNT.Drops.Allowed"))
		if(!t) {
		if(f.getBoolean("Options.LagChecker.TNT.Drops.InSingleLocation")){
			Inventory a = Bukkit.createInventory(null, 54);
			if(q.isEmpty()==false) {
				for(Inventory i : q) {
					if(i.firstEmpty()!=-1) {
						a=i;
						break;
					}
				}
			}
			if(q.contains(a))
			q.remove(a);
			for(ItemStack i : drops) {
				if(a.firstEmpty()!=-1)
				if(i!=null&&i.getType()!=Material.AIR)a.addItem(i);
				else {
					q.add(a);
					a = Bukkit.createInventory(null, 54);
					a.addItem(i);
				}
			}
			q.add(a);
		}else {
			List<Inventory> qd = new ArrayList<Inventory>();
			Inventory a = Bukkit.createInventory(null, 54);
			if(qd.isEmpty()==false) {
				for(Inventory i : qd) {
					if(i.firstEmpty()!=-1) {
						a=i;
						break;
					}
				}
			}
			if(qd.contains(a))
			qd.remove(a);
			for(ItemStack i :drops) {
				if(a.firstEmpty()!=-1)
				if(i!=null&&i.getType()!=Material.AIR)a.addItem(i);
				else {
					qd.add(a);
					a = Bukkit.createInventory(null, 54);
					a.addItem(i);
				}
			}
			qd.add(a);
			if(qd.isEmpty()==false)
				for(Inventory f : qd)
					for(ItemStack i : f.getContents())
					if(i!=null&&i.getType()!=Material.AIR)block.getWorld().dropItemNaturally(block, i);
		}
		}else {
			List<Inventory> qd = new ArrayList<Inventory>();
			Inventory a = Bukkit.createInventory(null, 54);
			if(qd.isEmpty()==false) {
				for(Inventory i : qd) {
					if(i.firstEmpty()!=-1) {
						a=i;
						break;
					}
				}
			}
			if(qd.contains(a))
			qd.remove(a);
			for(ItemStack i :drops) {
				if(a.firstEmpty()!=-1)
				if(i!=null&&i.getType()!=Material.AIR)a.addItem(i);
				else {
					qd.add(a);
					a = Bukkit.createInventory(null, 54);
					a.addItem(i);
				}
			}
			qd.add(a);
			if(qd.isEmpty()==false)
				for(Inventory f : qd)
					for(ItemStack i : f.getContents())
					if(i!=null&&i.getType()!=Material.AIR)real.getWorld().dropItemNaturally(real, i);
		}
		return r;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onItemDestroy(PlayerItemBreakEvent e) {
		me.Straiker123.Events.PlayerItemBreakEvent event = new me.Straiker123.Events.PlayerItemBreakEvent(e.getPlayer(),e.getBrokenItem());
		Bukkit.getPluginManager().callEvent(event);
		if(event.isCancelled()||isUnbreakable(event.getItem())) {
		ItemStack a = e.getBrokenItem();
		a.setDurability((short) 0);
		TheAPI.giveItem(e.getPlayer(), a);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onMove(PlayerMoveEvent e) {
		if(TheAPI.getPlayerAPI(e.getPlayer()).isFreezen()) {
			e.setCancelled(true);
			return;
		}
		int jump = (int) (e.getFrom().getY()-e.getTo().getY());
		if(jump>0) {
			PlayerJumpEvent event = new PlayerJumpEvent(e.getPlayer(),e.getFrom(),e.getTo(),jump);
			Bukkit.getPluginManager().callEvent(event);
			if(event.isCancelled())
				e.setCancelled(true);
		}
		try {
			World w = e.getTo().getWorld();
		if(TheAPI.getWorldBorder(w).isOutside(e.getTo())) {
			if(d.getString("WorldBorder."+w.getName()+".CancelMoveOutside")!=null) {
				e.setCancelled(TheAPI.getWorldBorder(w).isCancellledMoveOutside());
			}
				if(d.getString("WorldBorder."+w.getName()+".Type")!=null) {
			WarningMessageType t = WarningMessageType.valueOf(
					d.getString("WorldBorder."+w.getName()+".Type"));
			String msg = d.getString("WorldBorder."+w.getName()+".Message");
			if(msg==null)return;
			switch(t) {
			case ACTIONBAR:
				TheAPI.sendActionBar(e.getPlayer(), msg);
				break;
			case BOSSBAR:
				TheAPI.sendBossBar(e.getPlayer(), msg, 1, 5);
				break;
			case CHAT:
				TheAPI.getPlayerAPI(e.getPlayer()).msg(msg);
				break;
			case NONE:
				break;
			case SUBTITLE:
				TheAPI.getPlayerAPI(e.getPlayer()).sendTitle("", msg);
				break;
			case TITLE:
				TheAPI.getPlayerAPI(e.getPlayer()).sendTitle(msg, "");
				break;
			}
			}
		}
		}catch(Exception er) {
			
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onChunkLoad(ChunkLoadEvent e) {
		try {
			if (TheAPI.getWorldBorder(e.getWorld()).isOutside(e.getChunk().getBlock(15, 0, 15).getLocation()) || 
					TheAPI.getWorldBorder(e.getWorld()).isOutside(e.getChunk().getBlock(0, 0, 0).getLocation()))
			if(!TheAPI.getWorldBorder(e.getWorld()).getLoadChunksOutside())
			e.getChunk().unload(true);
	}catch(Exception er) {
		
	}
		}
	public static FileConfiguration f = LoaderClass.config.getConfig();
	public static FileConfiguration d = LoaderClass.data.getConfig();
	public static PunishmentAPI a = TheAPI.getPunishmentAPI();
	@EventHandler(priority = EventPriority.LOWEST)
	public void onLogin(AsyncPlayerPreLoginEvent e) {
		String s = e.getName();
		LoaderClass.data.getConfig().set("data."+s+".ip", e.getAddress().toString().replace(".", "_"));
		try {
		if(a.hasBan(s)) {
			e.disallow(Result.KICK_BANNED, TheAPI.colorize(f.getString("Format.Ban")
					.replace("%player%", s)
					.replace("%reason%", a.getBanReason(s))));
			return;
		}
		if(a.hasTempBan(s)) {
				e.disallow(Result.KICK_BANNED, TheAPI.colorize(f.getString("Format.TempBan")
						.replace("%player%", s)
						.replace("%time%", TheAPI.getStringUtils().setTimeToString(a.getTempBanExpireTime(s)))
						.replace("%reason%", a.getTempBanReason(s))));
				return;
		}
		if(a.hasBanIP(s)) {
			e.disallow(Result.KICK_BANNED, TheAPI.colorize(f.getString("Format.BanIP")
					.replace("%player%", s)
					.replace("%reason%",a.getBanIPReason(s))));
			return;
		}
		if(a.hasTempBanIP(s)) {
			e.disallow(Result.KICK_BANNED, TheAPI.colorize(f.getString("Format.TempBanIP")
					.replace("%player%", s)
					.replace("%time%", TheAPI.getStringUtils().setTimeToString(a.getTempBanIPExpireTime(s)))
					.replace("%reason%", a.getTempBanIPReason(s))));
			return;
		}
		}catch(Exception ad) {
			if(!f.getBoolean("Options.HideErrors")) {
				TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &cError when processing PunishmentAPI:"));
				ad.printStackTrace();
				TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &cEnd of error."));
				}else
					Error.sendRequest("&bTheAPI&7: &cError when processing PunishmentAPI");
		}
		LoaderClass.data.save();
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		String s = e.getPlayer().getName();
		if(a.hasBan(s)||
				a.hasBanIP(s)||
				a.hasTempBanIP(s)||
				a.hasTempBan(s))
			e.setQuitMessage(null);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onMotd(ServerListPingEvent e) {
		if(LoaderClass.plugin.motd!=null)
		e.setMotd(TheAPI.getPlaceholderAPI().setPlaceholders(null, LoaderClass.plugin.motd));
		if(LoaderClass.plugin.max>0)
		e.setMaxPlayers(LoaderClass.plugin.max);
	}
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		String s = e.getPlayer().getName();
		for(Player p : TheAPI.getOnlinePlayers()) {
			if(TheAPI.isVanished(p) && (d.getString("data."+p.getName()+".vanish") != null ? !e.getPlayer().hasPermission(d.getString("data."+p.getName()+".vanish")) : true)) {
				e.getPlayer().hidePlayer(p);
			}
		}
		if(TheAPI.isVanished(e.getPlayer())) {
			TheAPI.vanish(e.getPlayer(), d.getString("data."+s+".vanish"), true);
		}
	}
	
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onBreak(BlockBreakEvent e) {
		if(TheAPI.getPunishmentAPI().getJailAPI().isJailed(e.getPlayer().getName())) {
		e.setCancelled(true);
		}
	}
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlace(BlockPlaceEvent e) {
		if(TheAPI.getPunishmentAPI().getJailAPI().isJailed(e.getPlayer().getName())) {
		e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onDamage(EntityDamageEvent e) {
		if(e.getEntity()instanceof Player) {
			Player d = (Player)e.getEntity();
		if(TheAPI.getPunishmentAPI().getJailAPI().isJailed(d.getName())) {
		e.setCancelled(true);
		}
		if(TheAPI.getPlayerAPI(d).allowedGod()) {
			DamageGodPlayerEvent event = new DamageGodPlayerEvent(d,e.getDamage(),e.getCause());
			Bukkit.getPluginManager().callEvent(event);
			if(event.isCancelled())
				e.setCancelled(true);
			else
				e.setDamage(event.getDamage());
			return;
		}}
	}
	@EventHandler(priority = EventPriority.LOWEST)
	public void onFood(FoodLevelChangeEvent e) {
		if(e.getEntity() instanceof Player)
			if(TheAPI.getPlayerAPI((Player)e.getEntity()).allowedGod()) {
				e.setCancelled(true);
			}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onDamage(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof Player) {
			Player d = (Player)e.getEntity();
			if(TheAPI.getPlayerAPI(d).allowedGod()) {
				DamageGodPlayerByEntityEvent event = new DamageGodPlayerByEntityEvent(d,e.getDamager(),e.getDamage(),e.getCause());
				Bukkit.getPluginManager().callEvent(event);
				if(event.isCancelled())
					e.setCancelled(true);
				else
					e.setDamage(event.getDamage());
				return;
			}
		}
		try {
			double set = 0;
			double min = 0;
			double max = 0;
			if(e.getDamager().hasMetadata("damage:min")) {
				min=e.getDamager().getMetadata("damage:min").get(0).asDouble();	
				}
			if(e.getDamager().hasMetadata("damage:max")) {
				max=e.getDamager().getMetadata("damage:max").get(0).asDouble();	
				}
			if(e.getDamager().hasMetadata("damage:set")) {
				set=e.getDamager().getMetadata("damage:set").get(0).asDouble();	
				}
			if(set==0) {
				if(max!=0 && max>min) {
			double damage = TheAPI.generateRandomDouble(max);
			if(damage<min)damage=min;
			if(max>damage)damage=0;
			e.setDamage(e.getDamage()+damage);
				}}else {
				e.setDamage(set);
				}
		
		}catch(Exception err) {
			
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onChat(PlayerChatEvent e) {
		Player p = e.getPlayer();
		String s = p.getName();
		if(LoaderClass.chatformat.containsKey(p))
		e.setFormat(LoaderClass.chatformat.get(p).replace("%", "%%")
				.replace("%%player%%", s)
				.replace("%%playername%%", p.getDisplayName())
				.replace("%%playercustom%%", p.getCustomName()).replace("%%message%%", e.getMessage().replace("%", "%%")));
		if(a.hasTempMute(s)) {
				e.setCancelled(true);
				e.getPlayer().sendMessage(TheAPI.colorize(f.getString("Format.TempMute")
								.replace("%player%", s)
								.replace("%reason%", a.getTempMuteReason(s))
						.replace("%time%", TheAPI.getStringUtils().setTimeToString(a.getTempMuteExpireTime(s)))));
		}
		if(a.hasMute(s)) {
			e.setCancelled(true);
			e.getPlayer().sendMessage(TheAPI.colorize(f.getString("Format.Mute")
					.replace("%player%", s)
					.replace("%reason%", a.getMuteReason(s))));
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onClose(InventoryCloseEvent e) {
		Player p = (Player)e.getPlayer();
		String playersname = p.getName();
		if(g.getString("guis."+playersname)==null)return;
		String title = e.getView().getTitle();
		
		GUIID as = LoaderClass.gui.get(p);
	
		if(as!=null) {
			String a = as.getID();
			GUICloseEvent event = new GUICloseEvent(p,e.getInventory(),title);
			Bukkit.getPluginManager().callEvent(event);
					if(g.getString("guis."+playersname+"."+a+".MSGCLOSE")!=null)
				for(String s: g.getStringList("guis."+playersname+"."+a+".MSGCLOSE"))
					TheAPI.broadcastMessage(s);
			if(g.getString("guis."+playersname+"."+a+".CMDCLOSE")!=null)
				for(String s: g.getStringList("guis."+playersname+"."+a+".CMDCLOSE"))
					TheAPI.sudoConsole(SudoType.COMMAND, s);
		    	as.runRunnable(GRunnable.RUNNABLE_ON_INV_CLOSE,0);
		    as.clear();
		}
	}

	FileConfiguration g =LoaderClass.unused.getConfig();
	@EventHandler(priority = EventPriority.LOWEST)
	public void onClick(InventoryClickEvent e) {
		Player p = (Player)e.getWhoClicked();
		String playersname = p.getName();
		if(g.getString("guis."+playersname)==null)return;
		int slot = e.getSlot();
		GUIID d = LoaderClass.gui.get(p);
		if(d==null)return;
		String a = d.getID();
			ItemStack i = e.getCurrentItem();
			GUIClickEvent event = new GUIClickEvent(p, e.getClickedInventory(), e.getView().getTitle(), slot, i);
			Bukkit.getPluginManager().callEvent(event);
			if(event.isCancelled())
			e.setCancelled(true);
			
		if(i != null) {
			if(e.getClickedInventory().getType()==InventoryType.PLAYER) {
				if(g.getString("guis."+playersname+"."+a+".PUT")!=null)
			e.setCancelled(g.getBoolean("guis."+playersname+"."+a+".PUT"));
			return;
			}
			if(i.getType().name().equals("WRITTEN_BOOK")||i.getType().name().equals("BOOK_AND_QUILL"))i=createWrittenBook(i);

			if(i.getType().name().equals("LEGACY_SKULL_ITEM")||
					i.getType().name().equals("SKULL_ITEM")
					||i.getType().name().equals("PLAYER_HEAD"))
				i=createHead(i);
			if(g.getBoolean("guis."+playersname+"."+a+"."+slot+".TAKE"))
				e.setCancelled(g.getBoolean("guis."+playersname+"."+a+"."+slot+".TAKE"));
				
				if(g.getString("guis."+playersname+"."+a+"."+slot+".MSG")!=null)
					for(String s: g.getStringList("guis."+playersname+"."+a+"."+slot+".MSG"))
						TheAPI.broadcastMessage(s);
				if(g.getString("guis."+playersname+"."+a+"."+slot+".CMD")!=null)
					for(String s: g.getStringList("guis."+playersname+"."+a+"."+slot+".CMD"))
						TheAPI.sudoConsole(SudoType.COMMAND, s);
				d.runRunnable(GRunnable.RUNNABLE,slot);

				if(e.getClick().isLeftClick()&& !e.getClick().isShiftClick()) {
				if(g.getString("guis."+playersname+"."+a+"."+slot+".MSGLC")!=null)
					for(String s: g.getStringList("guis."+playersname+"."+a+"."+slot+".MSGLC"))
						TheAPI.broadcastMessage(s);
				if(g.getString("guis."+playersname+"."+a+"."+slot+".CMDLC")!=null)
					for(String s: g.getStringList("guis."+playersname+"."+a+"."+slot+".CMDLC"))
						TheAPI.sudoConsole(SudoType.COMMAND, s);
				d.runRunnable(GRunnable.RUNNABLE_LEFT_CLICK,slot);
				}
				if(e.getClick().isRightClick()&& !e.getClick().isShiftClick()) {
					if(g.getString("guis."+playersname+"."+a+"."+slot+".MSGRC")!=null)
					for(String s: g.getStringList("guis."+playersname+"."+a+"."+slot+".MSGRC"))
						TheAPI.broadcastMessage(s);
					if(g.getString("guis."+playersname+"."+a+"."+slot+".CMDRC")!=null)
						for(String s: g.getStringList("guis."+playersname+"."+a+"."+slot+".CMDRC"))
							TheAPI.sudoConsole(SudoType.COMMAND, s);
					d.runRunnable(GRunnable.RUNNABLE_RIGHT_CLICK,slot);
				}
				if(e.getClick().isCreativeAction()) {
					if(g.getString("guis."+playersname+"."+a+"."+slot+".MSGMC")!=null)
					for(String s: g.getStringList("guis."+playersname+"."+a+"."+slot+".MSGMC"))
						TheAPI.broadcastMessage(s);
					if(g.getString("guis."+playersname+"."+a+"."+slot+".CMDMC")!=null)
					for(String s: g.getStringList("guis."+playersname+"."+a+"."+slot+".CMDMC"))
						TheAPI.sudoConsole(SudoType.COMMAND, s);
					d.runRunnable(GRunnable.RUNNABLE_MIDDLE_CLICK,slot);
				}
				if(e.getClick().isLeftClick() && e.getClick().isShiftClick()) {
					if(g.getString("guis."+playersname+"."+a+"."+slot+".MSGWLC")!=null)
					for(String s: g.getStringList("guis."+playersname+"."+a+"."+slot+".MSGWLC"))
						TheAPI.broadcastMessage(s);
					if(g.getString("guis."+playersname+"."+a+"."+slot+".CMDWLC")!=null)
						for(String s: g.getStringList("guis."+playersname+"."+a+"."+slot+".CMDWLC"))
							TheAPI.sudoConsole(SudoType.COMMAND, s);
					d.runRunnable(GRunnable.RUNNABLE_SHIFT_WITH_LEFT_CLICK,slot);
				}
				if(e.getClick().isRightClick()&& e.getClick().isShiftClick()) {
					if(g.getString("guis."+playersname+"."+a+"."+slot+".MSGWRC")!=null)
					for(String s: g.getStringList("guis."+playersname+"."+a+"."+slot+".MSGWRC"))
						TheAPI.broadcastMessage(s);
				if(g.getString("guis."+playersname+"."+a+"."+slot+".CMDWLC")!=null)
					for(String s: g.getStringList("guis."+playersname+"."+a+"."+slot+".CMDWRC"))
						TheAPI.sudoConsole(SudoType.COMMAND, s);
					d.runRunnable(GRunnable.RUNNABLE_SHIFT_WITH_RIGHT_CLICK,slot);
		}}}}