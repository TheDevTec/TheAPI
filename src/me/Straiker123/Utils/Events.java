package me.Straiker123.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
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
		 if(!TheAPI.getServerVersion().contains("v1_8")
				 &&!TheAPI.getServerVersion().contains("v1_9")  
				 &&!TheAPI.getServerVersion().contains("v1_5")
				 &&!TheAPI.getServerVersion().contains("v1_6")
				 &&!TheAPI.getServerVersion().contains("v1_7")
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
		 if(!TheAPI.getServerVersion().contains("v1_8")
				 &&!TheAPI.getServerVersion().contains("v1_9")
				 &&!TheAPI.getServerVersion().contains("v1_5")
				 &&!TheAPI.getServerVersion().contains("v1_6")
				 &&!TheAPI.getServerVersion().contains("v1_7")  
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
		if(TheAPI.getWorldBorder(e.getTo().getWorld()).isOutside(e.getTo())) {
			if(LoaderClass.data.getConfig().getString("WorldBorder."+e.getTo().getWorld().getName()+".CancelMoveOutside")!=null) {
				e.setCancelled(TheAPI.getWorldBorder(e.getTo().getWorld()).isCancellledMoveOutside());
			}
				if(LoaderClass.data.getConfig().getString("WorldBorder."+e.getTo().getWorld().getName()+".Type")!=null) {
			WarningMessageType t = WarningMessageType.valueOf(
					LoaderClass.data.getConfig().getString("WorldBorder."+e.getTo().getWorld().getName()+".Type"));
			String msg = LoaderClass.data.getConfig().getString("WorldBorder."+e.getTo().getWorld().getName()+".Message");
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
	public void onLogin(PlayerLoginEvent e) {
		String s = e.getPlayer().getName();
		LoaderClass.data.getConfig().set("data."+s+".ip", e.getRealAddress().toString().replace(".", "_"));
		LoaderClass.data.save();
		PunishmentAPI a = TheAPI.getPunishmentAPI();
		try {
		if(a.hasBan(s)) {
			e.disallow(Result.KICK_BANNED, TheAPI.colorize(LoaderClass.config.getConfig().getString("Format.Ban")
					.replace("%player%", s)
					.replace("%reason%", a.getBanReason(s))));
			return;
		}
		if(a.hasTempBan(s)) {
				e.disallow(Result.KICK_BANNED, TheAPI.colorize(LoaderClass.config.getConfig().getString("Format.TempBan")
						.replace("%player%", s)
						.replace("%time%", TheAPI.getTimeConventorAPI().setTimeToString(a.getTempBan_ExpireTime(s)))
						.replace("%reason%", a.getTempBanReason(s))));
				return;
		}
		if(a.hasBanIP(s)) {
			e.disallow(Result.KICK_BANNED, TheAPI.colorize(LoaderClass.config.getConfig().getString("Format.BanIP")
					.replace("%player%", s)
					.replace("%reason%",a.getBanReason(s))));
			return;
		}
		if(a.hasTempBanIP(s)) {
			e.disallow(Result.KICK_BANNED, TheAPI.colorize(LoaderClass.config.getConfig().getString("Format.TempBanIP")
					.replace("%player%", s)
					.replace("%time%", TheAPI.getTimeConventorAPI().setTimeToString(a.getTempBanIP_ExpireTime(s)))
					.replace("%reason%", a.getTempBanIPReason(s))));
			return;
		}
		}catch(Exception ad) {
			if(LoaderClass.config.getConfig() == null || LoaderClass.config.getConfig() != null && !LoaderClass.config.getConfig().getBoolean("Options.HideErrors")) {
				TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &cError when processing PunishmentAPI:"));
				ad.printStackTrace();
				TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &cEnd of error."));
				}else
					Error.sendRequest("&bTheAPI&7: &cError when processing PunishmentAPI");
		}
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		String s = e.getPlayer().getName();
		PunishmentAPI a = TheAPI.getPunishmentAPI();
		if(a.hasBan(s)||
				a.hasBanIP(s)||
				a.hasTempBanIP(s)||
				a.hasTempBan(s))
			e.setQuitMessage(null);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onMotd(ServerListPingEvent e) {
		if(LoaderClass.plugin.motd!=null)
		e.setMotd(LoaderClass.plugin.motd);
		if(LoaderClass.plugin.max>0)
		e.setMaxPlayers(LoaderClass.plugin.max);
	}
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		String s = e.getPlayer().getName();
		PunishmentAPI a = TheAPI.getPunishmentAPI();
		try {
		if(a.hasBan(s)) {
			e.setJoinMessage(null);
			Bukkit.getScheduler().runTaskLater(LoaderClass.plugin, new Runnable() { @Override public void run() {
			e.getPlayer().kickPlayer(TheAPI.colorize(LoaderClass.config.getConfig().getString("Format.Ban")
					.replace("%player%", s)
					.replace("%reason%", a.getBanReason(s))));
			return;
		}},25);}
		if(a.hasTempBan(s)) {
			e.setJoinMessage(null);
			Bukkit.getScheduler().runTaskLater(LoaderClass.plugin, new Runnable() { @Override public void run() {
					e.getPlayer().kickPlayer(TheAPI.colorize(LoaderClass.config.getConfig().getString("Format.TempBan")
							.replace("%player%", s)
							.replace("%time%", TheAPI.getTimeConventorAPI().setTimeToString(a.getTempBan_ExpireTime(s)))
							.replace("%reason%", a.getTempBanReason(s))));
				return;
		}},25);}
		if(a.hasBanIP(s)) {
			e.setJoinMessage(null);
			Bukkit.getScheduler().runTaskLater(LoaderClass.plugin, new Runnable() { @Override public void run() {
			e.getPlayer().kickPlayer(TheAPI.colorize(LoaderClass.config.getConfig().getString("Format.BanIP")
					.replace("%player%", s)
					.replace("%reason%",a.getBanReason(s))));
			return;
		}},25);}
		if(a.hasTempBanIP(s)) {
			e.setJoinMessage(null);
			Bukkit.getScheduler().runTaskLater(LoaderClass.plugin, new Runnable() { @Override public void run() {
		
			e.getPlayer().kickPlayer(TheAPI.colorize(LoaderClass.config.getConfig().getString("Format.TempBanIP")
					.replace("%player%", s)
					.replace("%time%", TheAPI.getTimeConventorAPI().setTimeToString(a.getTempBanIP_ExpireTime(s)))));
		}},25);}
		if(TheAPI.getPunishmentAPI().getJailAPI().isJailed(e.getPlayer().getName())) {
			TheAPI.getPlayerAPI(e.getPlayer()).teleport(TheAPI.getPunishmentAPI().getJailAPI().getJailLocation(TheAPI.getPunishmentAPI().getJailAPI().getJailOfPlayer(e.getPlayer().getName())));
		}
		}catch(Exception ad) {
			if(LoaderClass.config.getConfig() == null || LoaderClass.config.getConfig() != null && !LoaderClass.config.getConfig().getBoolean("Options.HideErrors")) {
				TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &cError when processing PunishmentAPI:"));
				ad.printStackTrace();
				TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &cEnd of error."));
				}else
					Error.sendRequest("&bTheAPI&7: &cError when processing PunishmentAPI");
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
		if(TheAPI.getPunishmentAPI().getJailAPI().isJailed(((Player)e.getEntity()).getName())) {
		e.setCancelled(true);
		}
		if(TheAPI.getPlayerAPI((Player)e.getEntity()).allowedGod()) {
			DamageGodPlayerEvent event = new DamageGodPlayerEvent((Player)e.getEntity(),e.getDamage(),e.getCause());
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
			if(TheAPI.getPlayerAPI((Player)e.getEntity()).allowedGod()) {
				DamageGodPlayerByEntityEvent event = new DamageGodPlayerByEntityEvent((Player)e.getEntity(),e.getDamager(),e.getDamage(),e.getCause());
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
		if(LoaderClass.chatformat.get(e.getPlayer()) != null)
		e.setFormat(LoaderClass.chatformat.get(e.getPlayer()).replace("%", "%%")
				.replace("%%player%%", e.getPlayer().getName())
				.replace("%%playername%%", e.getPlayer().getDisplayName())
				.replace("%%playercustom%%", e.getPlayer().getCustomName())
				.replace("%%hp%%", e.getPlayer().getHealthScale()+"")
				.replace("%%food%%", e.getPlayer().getFoodLevel()+"")
				.replace("%%world%%", e.getPlayer().getWorld().getName()+"")
				.replace("%%x%%", e.getPlayer().getLocation().getBlockX()+"")
				.replace("%%y%%", e.getPlayer().getLocation().getBlockY()+"")
				.replace("%%z%%", e.getPlayer().getLocation().getBlockZ()+"").replace("%%message%%", e.getMessage().replace("%", "%%")));
		String s = e.getPlayer().getName();
		if(TheAPI.getPunishmentAPI().hasTempMute(s)) {
			int time = (int)(TheAPI.getPunishmentAPI().getTempMuteStart(s) - System.currentTimeMillis() + TheAPI.getPunishmentAPI().getTempMuteTime(s));
			if(time < 0)
				e.setCancelled(true);
				e.getPlayer().sendMessage(TheAPI.colorize(LoaderClass.config.getConfig().getString("Format.TempMute")
								.replace("%player%", s)
								.replace("%reason%", TheAPI.getPunishmentAPI().getTempMuteReason(s))
						.replace("%time%", TheAPI.getTimeConventorAPI().setTimeToString(time))));
		}
		if(TheAPI.getPunishmentAPI().hasMute(s)) {
			e.setCancelled(true);
			e.getPlayer().sendMessage(TheAPI.colorize(LoaderClass.config.getConfig().getString("Format.Mute")
					.replace("%player%", s)
					.replace("%reason%", TheAPI.getPunishmentAPI().getMuteReason(s))));
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onClose(InventoryCloseEvent e) {
		Player p = (Player)e.getPlayer();
		String playersname = p.getName();
		if(LoaderClass.data.getConfig().getString("guis."+playersname)==null) {
			return;
		}
		String title = e.getView().getTitle();
		
		GUIID as = LoaderClass.gui.get(p);
	
		if(as!=null) {
			String a = as.getID();
			GUICloseEvent event = new GUICloseEvent(p,e.getInventory(),title);
			Bukkit.getPluginManager().callEvent(event);
					if(LoaderClass.data.getConfig().getString("guis."+playersname+"."+a+".SENDMESSAGES_ON_INV_CLOSE")!=null)
				for(String s: LoaderClass.data.getConfig().getStringList("guis."+playersname+"."+a+".SENDMESSAGES_ON_INV_CLOSE"))
					TheAPI.broadcastMessage(s);
			if(LoaderClass.data.getConfig().getString("guis."+playersname+"."+a+".SENDCOMMANDS_ON_INV_CLOSE")!=null)
				for(String s: LoaderClass.data.getConfig().getStringList("guis."+playersname+"."+a+".SENDCOMMANDS_ON_INV_CLOSE"))
					TheAPI.sudoConsole(SudoType.COMMAND, s);
		    	as.runRunnable(GRunnable.RUNNABLE_ON_INV_CLOSE,0);
		    as.clear();
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onClick(InventoryClickEvent e) {
		Player p = (Player)e.getWhoClicked();
		if(LoaderClass.gui.get(p)==null)return;
		String playersname = p.getName();
		int slot = e.getSlot();
		GUIID d = LoaderClass.gui.get(p);
		String a = d.getID();
			ItemStack i = e.getCurrentItem();
			GUIClickEvent event = new GUIClickEvent(p, e.getClickedInventory(), e.getView().getTitle(), slot, i);
			Bukkit.getPluginManager().callEvent(event);
			if(event.isCancelled())
			e.setCancelled(true);
			
		if(i != null) {
			if(e.getClickedInventory().getType()==InventoryType.PLAYER) {
				if(LoaderClass.data.getConfig().getString("guis."+playersname+"."+a+".CANT_PUT_ITEM")!=null)
			e.setCancelled(LoaderClass.data.getConfig().getBoolean("guis."+playersname+"."+a+".CANT_PUT_ITEM"));
			return;
			}
			if(i.getType().name().equals("WRITTEN_BOOK")||i.getType().name().equals("BOOK_AND_QUILL"))i=createWrittenBook(i);

			if(i.getType().name().equals("LEGACY_SKULL_ITEM")||
					i.getType().name().equals("SKULL_ITEM")
					||i.getType().name().equals("PLAYER_HEAD"))
				i=createHead(i);
				e.setCancelled(LoaderClass.data.getConfig().getBoolean("guis."+playersname+"."+a+"."+slot+".CANT_BE_TAKEN"));
				
				if(LoaderClass.data.getConfig().getString("guis."+playersname+"."+a+"."+slot+".SENDMESSAGES")!=null)
					for(String s: LoaderClass.data.getConfig().getStringList("guis."+playersname+"."+a+"."+slot+".SENDMESSAGES"))
						TheAPI.broadcastMessage(s);

				if(LoaderClass.data.getConfig().getString("guis."+playersname+"."+a+"."+slot+".SENDMESSAGES_LEFT_CLICK")!=null)
					if(e.getClick().isLeftClick()&& !e.getClick().isShiftClick())
					for(String s: LoaderClass.data.getConfig().getStringList("guis."+playersname+"."+a+"."+slot+".SENDMESSAGES_LEFT_CLICK"))
						TheAPI.broadcastMessage(s);

					if(LoaderClass.data.getConfig().getString("guis."+playersname+"."+a+"."+slot+".SENDMESSAGES_RIGHT_CLICK")!=null)
					if(e.getClick().isRightClick()&& !e.getClick().isShiftClick())
					for(String s: LoaderClass.data.getConfig().getStringList("guis."+playersname+"."+a+"."+slot+".SENDMESSAGES_RIGHT_CLICK"))
						TheAPI.broadcastMessage(s);

					if(LoaderClass.data.getConfig().getString("guis."+playersname+"."+a+"."+slot+".SENDMESSAGES_MIDDLE_CLICK")!=null)
					if(e.getClick().isCreativeAction())
					for(String s: LoaderClass.data.getConfig().getStringList("guis."+playersname+"."+a+"."+slot+".SENDMESSAGES_MIDDLE_CLICK"))
						TheAPI.broadcastMessage(s);

					if(LoaderClass.data.getConfig().getString("guis."+playersname+"."+a+"."+slot+".SENDMESSAGES_SHIFT_WITH_LEFT_CLICK")!=null)
					if(e.getClick().isLeftClick() && e.getClick().isShiftClick())
					for(String s: LoaderClass.data.getConfig().getStringList("guis."+playersname+"."+a+"."+slot+".SENDMESSAGES_SHIFT_WITH_LEFT_CLICK"))
						TheAPI.broadcastMessage(s);

					if(LoaderClass.data.getConfig().getString("guis."+playersname+"."+a+"."+slot+".SENDMESSAGES_SHIFT_WITH_RIGHT_CLICK")!=null)
					if(e.getClick().isRightClick() && e.getClick().isShiftClick())
					for(String s: LoaderClass.data.getConfig().getStringList("guis."+playersname+"."+a+"."+slot+".SENDMESSAGES_SHIFT_WITH_RIGHT_CLICK"))
						TheAPI.broadcastMessage(s);
				
				if(LoaderClass.data.getConfig().getString("guis."+playersname+"."+a+"."+slot+".SENDCOMMANDS")!=null)
					for(String s: LoaderClass.data.getConfig().getStringList("guis."+playersname+"."+a+"."+slot+".SENDCOMMANDS"))
						TheAPI.sudoConsole(SudoType.COMMAND, s);

				if(LoaderClass.data.getConfig().getString("guis."+playersname+"."+a+"."+slot+".SENDCOMMANDS_LEFT_CLICK")!=null)
					if(e.getClick().isLeftClick()&& !e.getClick().isShiftClick())
					for(String s: LoaderClass.data.getConfig().getStringList("guis."+playersname+"."+a+"."+slot+".SENDCOMMANDS_LEFT_CLICK"))
						TheAPI.sudoConsole(SudoType.COMMAND, s);

				if(LoaderClass.data.getConfig().getString("guis."+playersname+"."+a+"."+slot+".SENDCOMMANDS_RIGHT_CLICK")!=null)
					if(e.getClick().isRightClick()&& !e.getClick().isShiftClick())
					for(String s: LoaderClass.data.getConfig().getStringList("guis."+playersname+"."+a+"."+slot+".SENDCOMMANDS_RIGHT_CLICK"))
						TheAPI.sudoConsole(SudoType.COMMAND, s);

				if(LoaderClass.data.getConfig().getString("guis."+playersname+"."+a+"."+slot+".SENDCOMMANDS_SHIFT_WITH_LEFT_CLICK")!=null)
					if(e.getClick().isLeftClick() && e.getClick().isShiftClick())
					for(String s: LoaderClass.data.getConfig().getStringList("guis."+playersname+"."+a+"."+slot+".SENDCOMMANDS_SHIFT_WITH_LEFT_CLICK"))
						TheAPI.sudoConsole(SudoType.COMMAND, s);

				if(LoaderClass.data.getConfig().getString("guis."+playersname+"."+a+"."+slot+".SENDCOMMANDS_SHIFT_WITH_RIGHT_CLICK")!=null)
					if(e.getClick().isRightClick() && e.getClick().isShiftClick())
					for(String s: LoaderClass.data.getConfig().getStringList("guis."+playersname+"."+a+"."+slot+".SENDCOMMANDS_SHIFT_WITH_RIGHT_CLICK"))
						TheAPI.sudoConsole(SudoType.COMMAND, s);

				if(LoaderClass.data.getConfig().getString("guis."+playersname+"."+a+"."+slot+".SENDCOMMANDS_MIDDLE_CLICK")!=null)
					if(e.getClick().isCreativeAction())
					for(String s: LoaderClass.data.getConfig().getStringList("guis."+playersname+"."+a+"."+slot+".SENDCOMMANDS_MIDDLE_CLICK"))
						TheAPI.sudoConsole(SudoType.COMMAND, s);
				d.runRunnable(GRunnable.RUNNABLE,slot);
				if(e.getClick().isLeftClick()&& !e.getClick().isShiftClick())
					d.runRunnable(GRunnable.RUNNABLE_LEFT_CLICK,slot);
				if(e.getClick().isRightClick()&& !e.getClick().isShiftClick())
					d.runRunnable(GRunnable.RUNNABLE_RIGHT_CLICK,slot);
				if(e.getClick().isCreativeAction())
					d.runRunnable(GRunnable.RUNNABLE_MIDDLE_CLICK,slot);
				if(e.getClick().isLeftClick()&& e.getClick().isShiftClick())
					d.runRunnable(GRunnable.RUNNABLE_SHIFT_WITH_LEFT_CLICK,slot);
				if(e.getClick().isRightClick()&& e.getClick().isShiftClick())
					d.runRunnable(GRunnable.RUNNABLE_SHIFT_WITH_RIGHT_CLICK,slot);
		}}}