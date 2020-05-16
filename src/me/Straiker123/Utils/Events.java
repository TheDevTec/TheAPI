package me.Straiker123.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Lists;

import me.Straiker123.BlocksAPI.Shape;
import me.Straiker123.ConfigAPI;
import me.Straiker123.ItemCreatorAPI;
import me.Straiker123.LoaderClass;
import me.Straiker123.PlayerBanList;
import me.Straiker123.PlayerBanList.PunishmentType;
import me.Straiker123.Position;
import me.Straiker123.PunishmentAPI;
import me.Straiker123.SignAPI.SignAction;
import me.Straiker123.Storage;
import me.Straiker123.TheAPI;
import me.Straiker123.TheAPI.SudoType;
import me.Straiker123.TheMaterial;
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
	public static FileConfiguration f = LoaderClass.config.getConfig();
	public static FileConfiguration d = LoaderClass.data.getConfig();
	public static ConfigAPI g = LoaderClass.unused;
	public static PunishmentAPI a = TheAPI.getPunishmentAPI();
	@EventHandler(priority = EventPriority.LOWEST)
	private synchronized void onClose(InventoryCloseEvent e) {
		Player p = (Player)e.getPlayer();
		String title = e.getView().getTitle();
		GUIID d = LoaderClass.gui.containsKey(p)?LoaderClass.gui.get(p):null;
		if(d==null)return;
			String a = p.getName()+"."+d.getID();
					GUICloseEvent event = new GUICloseEvent(p,e.getInventory(),title);
					Bukkit.getPluginManager().callEvent(event);
							if(g.getString("guis."+a+".MSGCLOSE")!=null)
						for(String s: g.getStringList("guis."+a+".MSGCLOSE"))
							TheAPI.broadcastMessage(s);
					if(g.getString("guis."+a+".CMDCLOSE")!=null)
						for(String s: g.getStringList("guis."+a+".CMDCLOSE"))
							TheAPI.sudoConsole(SudoType.COMMAND, s);
				    	d.runRunnable(GRunnable.RUNNABLE_ON_INV_CLOSE,0);
				    d.clear();
	}
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
		ItemCreatorAPI s = TheAPI.getItemCreatorAPI(Material.matchMaterial("LEGACY_SKULL_ITEM") != null ? Material.matchMaterial("LEGACY_SKULL_ITEM") : Material.matchMaterial("SKULL_ITEM"));
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
	
	@EventHandler(priority = EventPriority.LOWEST)
	private synchronized void onClick(InventoryClickEvent e) {
		if(e.isCancelled())return;
		Player p = (Player)e.getWhoClicked();
		int slot = e.getSlot();
		GUIID d = LoaderClass.gui.containsKey(p)?LoaderClass.gui.get(p):null;
		if(d==null)return;
			String a = p.getName()+"."+d.getID();
					ItemStack i = e.getCurrentItem();
					if(i==null)return;
			GUIClickEvent event = new GUIClickEvent(p, e.getClickedInventory(), e.getView().getTitle(), slot, i);
			Bukkit.getPluginManager().callEvent(event);
			if(event.isCancelled())
			e.setCancelled(true);
			if(e.getClickedInventory().getType()==InventoryType.PLAYER) {
				if(g.existPath("guis."+a+".PUT"))
			e.setCancelled(g.getBoolean("guis."+a+".PUT"));
			return;
			}
			if(i.getType().name().equals("WRITTEN_BOOK")||i.getType().name().equals("BOOK_AND_QUILL"))i=createWrittenBook(i);
			if(i.getType().name().contains("SKULL_ITEM")
					||i.getType().name().equals("PLAYER_HEAD"))
				i=createHead(i);
			ClickType t = e.getClick();
			if(g.existPath("guis."+a+"."+slot+".TAKE"))
				e.setCancelled(g.getBoolean("guis."+a+"."+slot+".TAKE"));
				new BukkitRunnable() {
					public void run() {
				if(g.getString("guis."+a+"."+slot+".MSG")!=null)
					for(String s: g.getStringList("guis."+a+"."+slot+".MSG"))
						TheAPI.msg(s, p);
				if(g.getString("guis."+a+"."+slot+".CMD")!=null)
					for(String s: g.getStringList("guis."+a+"."+slot+".CMD"))
						TheAPI.sudoConsole(SudoType.COMMAND, s);
				new BukkitRunnable() {
					public void run() {
						d.runRunnable(GRunnable.RUNNABLE,slot);
					}
				}.runTask(LoaderClass.plugin);

				if(t.isLeftClick()&& !t.isShiftClick()) {
				if(g.getString("guis."+a+"."+slot+".MSGLC")!=null)
					for(String s: g.getStringList("guis."+a+"."+slot+".MSGLC"))
						TheAPI.msg(s, p);
				if(g.getString("guis."+a+"."+slot+".CMDLC")!=null)
					for(String s: g.getStringList("guis."+a+"."+slot+".CMDLC"))
						TheAPI.sudoConsole(SudoType.COMMAND, s);
				new BukkitRunnable() {
					public void run() {
						d.runRunnable(GRunnable.RUNNABLE_LEFT_CLICK,slot);
					}
				}.runTask(LoaderClass.plugin);
				}
				if(t.isRightClick()&& !t.isShiftClick()) {
					if(g.getString("guis."+a+"."+slot+".MSGRC")!=null)
					for(String s: g.getStringList("guis."+a+"."+slot+".MSGRC"))
						TheAPI.msg(s, p);
					if(g.getString("guis."+a+"."+slot+".CMDRC")!=null)
						for(String s: g.getStringList("guis."+a+"."+slot+".CMDRC"))
							TheAPI.sudoConsole(SudoType.COMMAND, s);
					new BukkitRunnable() {
						public void run() {
							d.runRunnable(GRunnable.RUNNABLE_RIGHT_CLICK,slot);
						}
					}.runTask(LoaderClass.plugin);
				}
				if(t.isCreativeAction()) {
					if(g.getString("guis."+a+"."+slot+".MSGMC")!=null)
					for(String s: g.getStringList("guis."+a+"."+slot+".MSGMC"))
						TheAPI.msg(s, p);
					if(g.getString("guis."+a+"."+slot+".CMDMC")!=null)
					for(String s: g.getStringList("guis."+a+"."+slot+".CMDMC"))
						TheAPI.sudoConsole(SudoType.COMMAND, s);
					new BukkitRunnable() {
						public void run() {
							d.runRunnable(GRunnable.RUNNABLE_MIDDLE_CLICK,slot);
						}
					}.runTask(LoaderClass.plugin);
				}
				if(t.isLeftClick() && t.isShiftClick()) {
					if(g.getString("guis."+a+"."+slot+".MSGWLC")!=null)
					for(String s: g.getStringList("guis."+a+"."+slot+".MSGWLC"))
						TheAPI.msg(s, p);
					if(g.getString("guis."+a+"."+slot+".CMDWLC")!=null)
						for(String s: g.getStringList("guis."+a+"."+slot+".CMDWLC"))
							TheAPI.sudoConsole(SudoType.COMMAND, s);
					new BukkitRunnable() {
						public void run() {
							d.runRunnable(GRunnable.RUNNABLE_SHIFT_WITH_LEFT_CLICK,slot);
						}
					}.runTask(LoaderClass.plugin);
				}
				if(t.isRightClick()&& t.isShiftClick()) {
					if(g.getString("guis."+a+"."+slot+".MSGWRC")!=null)
					for(String s: g.getStringList("guis."+a+"."+slot+".MSGWRC"))
						TheAPI.msg(s, p);
				if(g.getString("guis."+a+"."+slot+".CMDWLC")!=null)
					for(String s: g.getStringList("guis."+a+"."+slot+".CMDWRC"))
						TheAPI.sudoConsole(SudoType.COMMAND, s);
					new BukkitRunnable() {
						public void run() {
							d.runRunnable(GRunnable.RUNNABLE_SHIFT_WITH_RIGHT_CLICK,slot);
						}
					}.runTask(LoaderClass.plugin);
				}}}.runTaskAsynchronously(LoaderClass.plugin);
		}
	@EventHandler(priority = EventPriority.LOWEST)
	public void onClick(PlayerInteractEvent e) {
		if(e.isCancelled())return;
		if(e.getAction()==Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType().name().contains("SIGN")) {
			if(TheAPI.getSignAPI().getRegistredSigns().contains(e.getClickedBlock().getLocation())) {
				e.setCancelled(true);
				HashMap<SignAction, List<String>> as= TheAPI.getSignAPI().getSignActions((Sign)e.getClickedBlock().getState());
				for(SignAction a : as.keySet()) {
					switch(a) {
					case PLAYER_COMMANDS:
						for(String s:as.get(a)) {
							TheAPI.sudo(e.getPlayer(), SudoType.COMMAND, s.replace("%player%", e.getPlayer().getName()).replace("%playername%", e.getPlayer().getDisplayName()));
						}
						break;
					case CONSOLE_COMMANDS:
						for(String s:as.get(a)) {
							TheAPI.sudoConsole(SudoType.COMMAND, s.replace("%player%", e.getPlayer().getName()).replace("%playername%", e.getPlayer().getDisplayName()));
							}
						break;
					case BROADCAST:
						for(String s:as.get(a)) {
							TheAPI.broadcastMessage(s.replace("%player%", e.getPlayer().getName()).replace("%playername%", e.getPlayer().getDisplayName()));
						}
						break;
					case MESSAGES:
						for(String s:as.get(a)) {
							TheAPI.msg(s.replace("%player%", e.getPlayer().getName()).replace("%playername%", e.getPlayer().getDisplayName()), e.getPlayer());
						}
						break;
					}
				}
			}
		}
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
		if(e.isCancelled())return;
		if(f.getBoolean("Options.LagChecker.Enabled") &&
				f.getBoolean("Options.LagChecker.TNT.Use")) {
			e.setCancelled(true);
			get((e.getEntity().hasMetadata("real") ? new Position(TheAPI.getBlocksAPI().getLocationFromString(e.getEntity().getMetadata("real").get(0).asString())) : new Position(e.getLocation())),new Position(e.getLocation()));
		}}
    public static boolean around(Position position){
    	boolean s = false;
    String f = position.getBukkitType().name();
    	if(f.contains("WATER")||f.contains("LAVA")) {
    		s=true;
    	}
    return s;
    }
   public static void get(Position reals, Position c) {
	   TNTExplosionEvent event = new TNTExplosionEvent(c);
    	Bukkit.getPluginManager().callEvent(event);
    	if(event.isCancelled()) {
    		return;
    	}
			new TNTTask(reals,TheAPI.getBlocksAPI().get(Shape.Sphere, c, event.getPower(), blocks(event.isNuclearBomb() && event.canNuclearDestroyLiquid())),event).start();
	}

	public static List<TheMaterial> blocks(boolean b){
    	ArrayList<TheMaterial> m = Lists.newArrayList();
    	m.add(new TheMaterial("AIR"));
    	try {
    	m.add(new TheMaterial("BARRIER"));
    	 }catch(Exception |NoSuchFieldError e) {
    			
    		}
    	m.add(new TheMaterial("BEDROCK"));
    	m.add(new TheMaterial("ENDER_CHEST"));
    	try {
        	m.add(new TheMaterial("END_PORTAL_FRAME"));
    }catch(Exception |NoSuchFieldError e) {
		
	}
    	try {
        	m.add(new TheMaterial("STRUCTURE_BLOCK"));
        	m.add(new TheMaterial("JIGSAW"));
    	 }catch(Exception |NoSuchFieldError e) {
    			
    		}
    	m.add(new TheMaterial("OBSIDIAN"));
    	try {
        	m.add(new TheMaterial("END_GATEWAY"));
    }catch(Exception |NoSuchFieldError e) {
		
   	}
    	try {
        	m.add(new TheMaterial("END_PORTAL"));
 }catch(Exception |NoSuchFieldError e) {
		
   	}
    	try {
        	m.add(new TheMaterial("COMMAND_BLOCK"));
        	m.add(new TheMaterial("REPEATING_COMMAND_BLOCK"));
        	m.add(new TheMaterial("CHAIN_COMMAND_BLOCK"));
    }catch(Exception |NoSuchFieldError e) {
    	m.add(new TheMaterial("COMMAND"));
   	}
    	if(!b) {
        	m.add(new TheMaterial("LAVA"));
        	m.add(new TheMaterial("STATIONARY_LAVA"));
        	m.add(new TheMaterial("WATER"));
        	m.add(new TheMaterial("STATIONARY_WATER"));
    	}
    	try {
        	m.add(new TheMaterial("ENCHANTING_TABLE"));
    	}catch(Exception |NoSuchFieldError e) {
        	m.add(new TheMaterial("ENCHANTMENT_TABLE"));
       	}
    	m.add(new TheMaterial("ANVIL"));
    	try {
        	m.add(new TheMaterial("CHIPPED_ANVIL"));
        	m.add(new TheMaterial("DAMAGED_ANVIL"));
    	 }catch(Exception |NoSuchFieldError e) {
    			
    		}
    	try {
        	m.add(new TheMaterial("NETHERITE_BLOCK"));
        	m.add(new TheMaterial("CRYING_OBSIDIAN"));
        	m.add(new TheMaterial("ANCIENT_DEBRIS"));
    	}catch(Exception |NoSuchFieldError e) {
    		
    	}
    	return m;
    }
    
	public static Storage add(Position block, Position real, boolean t,Storage st, Collection<ItemStack> collection) {
		if(f.getBoolean("Options.LagChecker.TNT.Drops.Allowed"))
		if(!t) {
		if(f.getBoolean("Options.LagChecker.TNT.Drops.InSingleLocation")){
			for(ItemStack i : collection) {
				if(i!=null&&i.getType()!=Material.AIR)st.add(i);
			}
		}else {
			Storage qd = new Storage();
			for(ItemStack i :collection) {
				if(i!=null&&i.getType()!=Material.AIR)qd.add(i);
			}
			if(qd.isEmpty()==false)
					for(ItemStack i : qd.getItems())
					if(i!=null&&i.getType()!=Material.AIR)block.getWorld().dropItemNaturally(block.toLocation(), i);
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
			for(ItemStack i :collection) {
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
					if(i!=null&&i.getType()!=Material.AIR)real.getWorld().dropItemNaturally(real.toLocation(), i);
		}
		return st;
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
	public void onBreak(BlockBreakEvent e) {
		if(e.isCancelled())return;
		if(TheAPI.getPunishmentAPI().getBanList(e.getPlayer().getName()).isJailed()) {
		e.setCancelled(true);
		}else {
			if(e.getBlock().getType().name().contains("SIGN") && !e.isCancelled()) {
				TheAPI.getSignAPI().removeSign(e.getBlock().getLocation());
			}
		}
	}
	@EventHandler(priority = EventPriority.LOWEST)
	public void onMove(PlayerMoveEvent e) {
		if(e.isCancelled())return;
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
	@EventHandler(priority = EventPriority.LOWEST)
	public void onLogin(AsyncPlayerPreLoginEvent e) {
		if(!AntiBot.hasAccess(e.getUniqueId())) {
			e.disallow(Result.KICK_OTHER, null);
			return;
		}
		String s = TheAPI.getUser(e.getUniqueId()).getName();
		TheAPI.getUser(e.getUniqueId()).setAndSave("ip", (e.getAddress().toString()).replace(".", "_"));
		PlayerBanList a = Events.a.getBanList(s);
		try {
		if(a.isBanned()) {
			e.disallow(Result.KICK_BANNED, TheAPI.colorize(f.getString("Format.Ban").replace("\\n", "\n")
					.replace("%player%", s)
					.replace("%reason%", a.getReason(PunishmentType.BAN))));
			return;
		}
		if(a.isTempBanned()) {
				e.disallow(Result.KICK_BANNED, a.getReason(PunishmentType.TEMPBAN).replace("\\n", "\n")
						.replace("%player%", s)
						.replace("%time%", TheAPI.getStringUtils().setTimeToString(a.getExpire(PunishmentType.TEMPBAN))));
				return;
		}
		if(a.isIPBanned()) {
			e.disallow(Result.KICK_BANNED, TheAPI.colorize(a.getReason(PunishmentType.BANIP).replace("\\n", "\n")
					.replace("%player%", s)));
			return;
		}
		if(a.isTempIPBanned()) {
			e.disallow(Result.KICK_BANNED, TheAPI.colorize(a.getReason(PunishmentType.TEMPBANIP).replace("\\n", "\n")
					.replace("%player%", s)
					.replace("%time%", TheAPI.getStringUtils().setTimeToString(a.getExpire(PunishmentType.TEMPBANIP)))));
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
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		LoaderClass.a.remove(e.getPlayer());
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
		LoaderClass.a.add(e.getPlayer());
		for(Player p : TheAPI.getOnlinePlayers()) {
			if(TheAPI.isVanished(p) && (TheAPI.getUser(p).exist("vanish") ? !e.getPlayer().hasPermission(TheAPI.getUser(p).getString("vanish")) : true)) {
				e.getPlayer().hidePlayer(p);
			}
		}
		if(TheAPI.isVanished(e.getPlayer())) {
			TheAPI.vanish(e.getPlayer(), TheAPI.getUser(e.getPlayer()).getString("vanish"), true);
		}
	}
	
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlace(BlockPlaceEvent e) {
		if(e.isCancelled())return;
		if(TheAPI.getPunishmentAPI().getBanList(e.getPlayer().getName()).isJailed()||TheAPI.getPunishmentAPI().getBanList(e.getPlayer().getName()).isTempJailed()) {
		e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onDamage(EntityDamageEvent e) {
		if(e.isCancelled())return;
		if(e.getEntity()instanceof Player) {
			Player d = (Player)e.getEntity();
		if(TheAPI.getPunishmentAPI().getBanList(d.getName()).isJailed()||TheAPI.getPunishmentAPI().getBanList(d.getName()).isTempJailed()) {
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
		if(e.isCancelled())return;
		if(e.getEntity() instanceof Player)
			if(TheAPI.getPlayerAPI((Player)e.getEntity()).allowedGod()) {
				e.setCancelled(true);
			}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onDamage(EntityDamageByEntityEvent e) {
		if(e.isCancelled())return;
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
		if(e.isCancelled())return;
		Player p = e.getPlayer();
		PlayerBanList b = a.getBanList(p.getName());
		if(b.isTempMuted()) {
				e.setCancelled(true);
				TheAPI.msg(b.getReason(PunishmentType.TEMPMUTE)
						.replace("%time%", TheAPI.getStringUtils().setTimeToString(b.getExpire(PunishmentType.TEMPMUTE))), e.getPlayer());
				return;
		}
		if(b.isMuted()) {
			e.setCancelled(true);
			TheAPI.msg(b.getReason(PunishmentType.MUTE),e.getPlayer());
			return;
		}
		if(LoaderClass.chatformat.containsKey(p))
		e.setFormat(LoaderClass.chatformat.get(p).replace("%", "%%")
				.replace("%%player%%", p.getName())
				.replace("%%playername%%", p.getDisplayName())
				.replace("%%playercustom%%", p.getCustomName()).replace("%%message%%", e.getMessage().replace("%", "%%")));
	}
}