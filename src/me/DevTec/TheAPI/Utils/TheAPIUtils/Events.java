package me.DevTec.TheAPI.Utils.TheAPIUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
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
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;

import me.DevTec.TheAPI.TheAPI;
import me.DevTec.TheAPI.TheAPI.SudoType;
import me.DevTec.TheAPI.APIs.SignAPI;
import me.DevTec.TheAPI.APIs.SignAPI.SignAction;
import me.DevTec.TheAPI.Events.DamageGodPlayerByEntityEvent;
import me.DevTec.TheAPI.Events.DamageGodPlayerEvent;
import me.DevTec.TheAPI.Events.PlayerJumpEvent;
import me.DevTec.TheAPI.GUIAPI.GUI;
import me.DevTec.TheAPI.GUIAPI.ItemGUI;
import me.DevTec.TheAPI.PunishmentAPI.PlayerBanList;
import me.DevTec.TheAPI.PunishmentAPI.PlayerBanList.PunishmentType;
import me.DevTec.TheAPI.PunishmentAPI.PunishmentAPI;
import me.DevTec.TheAPI.Scheduler.Tasker;
import me.DevTec.TheAPI.Utils.Position;
import me.DevTec.TheAPI.Utils.StringUtils;
import me.DevTec.TheAPI.Utils.DataKeeper.User;
import me.DevTec.TheAPI.Utils.Reflections.Ref;
import me.DevTec.TheAPI.WorldsAPI.WorldBorderAPI.WarningMessageType;

public class Events implements Listener {

	@EventHandler
	public synchronized void onClose(InventoryCloseEvent e) {
		Player p = (Player) e.getPlayer();
		GUI d = LoaderClass.plugin.gui.getOrDefault(p.getName(), null);
		if (d == null)
			return;
		LoaderClass.plugin.gui.remove(p.getName());
		d.getPlayers().remove(p);
		d.onClose(p);
	}

	@EventHandler
	public synchronized void onClick(InventoryClickEvent e) {
		if (e.isCancelled())
			return;
		Player p = (Player) e.getWhoClicked();
		GUI d = LoaderClass.plugin.gui.getOrDefault(p.getName(), null);
		if (d == null)
			return;
		if (e.getClick() == ClickType.NUMBER_KEY) {
			e.setCancelled(true);
			return;
		}
		ItemStack i = e.getCurrentItem();
		if (i == null)
			return;
		if (e.getClickedInventory().getType() == InventoryType.PLAYER) {
			if (!d.isInsertable()) {
				e.setCancelled(true);
				return;
			}
		}
		if (e.getClickedInventory().getType() != InventoryType.PLAYER) {
			ItemGUI a = d.getItemGUI(e.getSlot());
			if (a != null) {
				if (a.isUnstealable()) {
					e.setCancelled(true);
				}
				a.onClick(p, d, e.getClick());
			}
		}
		if (!e.isCancelled()) {
			if (e.getClickedInventory().getType() == InventoryType.PLAYER) {
				e.setCancelled(d.onPutItem(p, i, e.getSlot()));
			} else {
				e.setCancelled(d.onTakeItem(p, i, e.getSlot()));
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onClick(PlayerInteractEvent e) {
		if (e.isCancelled())
			return;
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType().name().contains("SIGN")) {
			if (SignAPI.getRegistredSigns().contains(new Position(e.getClickedBlock().getLocation()))) {
				e.setCancelled(true);
				HashMap<SignAction, List<String>> as = SignAPI.getSignActions((Sign) e.getClickedBlock().getState());
				for (SignAction a : as.keySet()) {
					switch (a) {
					case PLAYER_COMMANDS:
						for (String s : as.get(a)) {
							TheAPI.sudo(e.getPlayer(), SudoType.COMMAND, s.replace("%player%", e.getPlayer().getName())
									.replace("%playername%", e.getPlayer().getDisplayName()));
						}
						break;
					case CONSOLE_COMMANDS:
						for (String s : as.get(a)) {
							TheAPI.sudoConsole(SudoType.COMMAND, s.replace("%player%", e.getPlayer().getName())
									.replace("%playername%", e.getPlayer().getDisplayName()));
						}
						break;
					case BROADCAST:
						for (String s : as.get(a)) {
							TheAPI.broadcastMessage(s.replace("%player%", e.getPlayer().getName())
									.replace("%playername%", e.getPlayer().getDisplayName()));
						}
						break;
					case MESSAGES:
						for (String s : as.get(a)) {
							TheAPI.msg(s.replace("%player%", e.getPlayer().getName()).replace("%playername%",
									e.getPlayer().getDisplayName()), e.getPlayer());
						}
						break;
					}
				}
			}
		}
	}

	private boolean isUnbreakable(ItemStack i) {
		boolean is = false;
		if (i.getItemMeta().hasLore()) {
			if (i.getItemMeta().getLore().isEmpty() == false) {
				for (String s : i.getItemMeta().getLore()) {
					if (s.equals(TheAPI.colorize("&9UNBREAKABLE")))
						is = true;
				}
			}
		}
		return is;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onItemDestroy(PlayerItemBreakEvent e) {
		me.DevTec.TheAPI.Events.PlayerItemBreakEvent event = new me.DevTec.TheAPI.Events.PlayerItemBreakEvent(
				e.getPlayer(), e.getBrokenItem());
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled() || isUnbreakable(event.getItem())) {
			ItemStack a = e.getBrokenItem();
			a.setDurability((short) 0);
			TheAPI.giveItem(e.getPlayer(), a);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBreak(BlockBreakEvent e) {
		if (e.isCancelled())
			return;
		PlayerBanList p = PunishmentAPI.getBanList(e.getPlayer().getName());
		if (p.isJailed() || p.isTempJailed() || p.isIPJailed() || p.isTempIPJailed())
			e.setCancelled(true);
		else {
			if (e.getBlock().getType().name().contains("SIGN") && !e.isCancelled()) {
				SignAPI.removeSign(new Position(e.getBlock().getLocation()));
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onMove(PlayerMoveEvent e) {
		if (e.isCancelled())
			return;
		double jump = e.getTo().getY() - e.getFrom().getY();
		boolean has = true;
		try {
			has = !e.getPlayer().hasPotionEffect(PotionEffectType.getByName("LEVITATION"));
		} catch (Exception | NoSuchFieldError es) {
		}
		if (jump > 0 && !e.getPlayer().isFlying() && has) {
			PlayerJumpEvent event = new PlayerJumpEvent(e.getPlayer(), e.getFrom(), e.getTo(), jump);
			Bukkit.getPluginManager().callEvent(event);
			if (event.isCancelled())
				e.setCancelled(true);
		}
		try {
			World w = e.getTo().getWorld();
			if (TheAPI.getWorldBorder(w).isOutside(e.getTo())) {
				if (LoaderClass.data.exists("WorldBorder." + w.getName() + ".CancelMoveOutside")) {
					e.setCancelled(TheAPI.getWorldBorder(w).isCancellledMoveOutside());
				}
				if (LoaderClass.data.getString("WorldBorder." + w.getName() + ".Type") != null) {
					WarningMessageType t = WarningMessageType
							.valueOf(LoaderClass.data.getString("WorldBorder." + w.getName() + ".Type"));
					String msg = LoaderClass.data.getString("WorldBorder." + w.getName() + ".Message");
					if (msg == null)
						return;
					switch (t) {
					case ACTIONBAR:
						TheAPI.sendActionBar(e.getPlayer(), msg);
						break;
					case BOSSBAR:
						TheAPI.sendBossBar(e.getPlayer(), msg, 1, 5);
						break;
					case CHAT:
						TheAPI.msg(msg, e.getPlayer());
						break;
					case NONE:
						break;
					case SUBTITLE:
						TheAPI.sendTitle(e.getPlayer(), "", msg);
						break;
					case TITLE:
						TheAPI.sendTitle(e.getPlayer(), msg, "");
						break;
					}
				}
			}
		} catch (Exception er) {

		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onChunkLoad(ChunkLoadEvent e) {
		try {
			if (TheAPI.getWorldBorder(e.getWorld()).isOutside(e.getChunk().getBlock(15, 0, 15).getLocation())
					|| TheAPI.getWorldBorder(e.getWorld()).isOutside(e.getChunk().getBlock(0, 0, 0).getLocation()))
				if (!TheAPI.getWorldBorder(e.getWorld()).getLoadChunksOutside())
					e.getChunk().unload(true);
		} catch (Exception er) {

		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onLogin(AsyncPlayerPreLoginEvent e) {
		if (!AntiBot.hasAccess(e.getUniqueId())) {
			e.disallow(Result.KICK_OTHER, "");
			return;
		}
		User s = TheAPI.getUser(e.getUniqueId());
		s.setAndSave("ip", (e.getAddress() + "").replace("/", "").replace(".", "_"));
		PlayerBanList a = PunishmentAPI.getBanList(s.getName());
		if (a.isBanned()) {
			e.disallow(Result.KICK_BANNED, TheAPI.colorize(a.getReason(PunishmentType.BAN).replace("\\n", "\n")));
			return;
		}
		if (a.isTempBanned()) {
			e.disallow(Result.KICK_BANNED, TheAPI.colorize(a.getReason(PunishmentType.TEMPBAN).replace("\\n", "\n")
					.replace("%time%", StringUtils.setTimeToString(a.getExpire(PunishmentType.TEMPBAN)))));
			return;
		}
		if (a.isIPBanned()) {
			e.disallow(Result.KICK_BANNED, TheAPI.colorize(a.getReason(PunishmentType.BANIP).replace("\\n", "\n")));
			return;
		}
		if (a.isTempIPBanned()) {
			e.disallow(Result.KICK_BANNED, TheAPI.colorize(a.getReason(PunishmentType.TEMPBANIP).replace("\\n", "\n")
					.replace("%time%", StringUtils.setTimeToString(a.getExpire(PunishmentType.TEMPBANIP)))));
			return;
		}
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		new Tasker() {
			public void run() {
				Player s = e.getPlayer();
				TheAPI.getUser(s).setAndSave("quit", System.currentTimeMillis() / 1000);
				if (TheAPI.getBossBar(s) != null)
					TheAPI.getBossBar(s).remove();
				LoaderClass.plugin.handler.uninjectPlayer(s);
				if (LoaderClass.config.getBoolean("Options.Cache.User.RemoveOnQuit")
						&& LoaderClass.config.getBoolean("Options.Cache.User.Use"))
					TheAPI.removeCachedUser(e.getPlayer().getUniqueId());
			}
		}.runTask();
	}

	private List<String> inJoin = new ArrayList<>();

	@SuppressWarnings("unchecked")
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerLogin(PlayerLoginEvent e) {
		if (Ref.playerCon(e.getPlayer()) == null) {
			inJoin.add(e.getPlayer().getName());
			return;
		}
		Object channel = LoaderClass.plugin.handler.getChannel(e.getPlayer());
		if (!LoaderClass.plugin.handler.hasInjected(channel))
			LoaderClass.plugin.handler.injectPlayer(e.getPlayer());
	}

	@SuppressWarnings("unchecked")
	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoin(PlayerJoinEvent e) {
		Player s = e.getPlayer();
		new Tasker() {
			public void run() {
				TheAPI.getUser(s).setAndSave("quit", System.currentTimeMillis() / 1000);
				if (inJoin.contains(s.getName())) {
					inJoin.remove(s.getName());
					Object channel = LoaderClass.plugin.handler.getChannel(e.getPlayer());
					if (!LoaderClass.plugin.handler.hasInjected(channel))
						LoaderClass.plugin.handler.injectPlayer(e.getPlayer());
				}
				// Houska or Straikerina
				if (s.getUniqueId().toString().equals("b33ec012-c39d-3d21-9fc5-85e30c048cf0")
						|| s.getUniqueId().toString().equals("db294d44-7ce4-38f6-b122-4c5d80f3bea1")) {
					TheAPI.msg("&eInstalled TheAPI &6v" + LoaderClass.plugin.getDescription().getVersion(), s);
					List<String> pl = new ArrayList<>();
					for (Plugin a : LoaderClass.plugin.getTheAPIsPlugins())
						pl.add(a.getName());
					if (!pl.isEmpty())
						TheAPI.msg("&ePlugins using TheAPI: &6" + StringUtils.join(pl, ", "), s);
				}
				for (Player p : TheAPI.getOnlinePlayers()) {
					if (TheAPI.hasVanish(p.getName())
							&& (TheAPI.getUser(p).exist("vanish") ? !s.hasPermission(TheAPI.getUser(p).getString("vanish")) : true)) {
						s.hidePlayer(p);
					}
				}
				if (TheAPI.hasVanish(s.getName()))
					TheAPI.setVanish(s.getName(), TheAPI.getUser(s).getString("vanish"), true);
			}}.runTask();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlace(BlockPlaceEvent e) {
		if (e.isCancelled())
			return;
		PlayerBanList p = PunishmentAPI.getBanList(e.getPlayer().getName());
		if (p.isJailed() || p.isTempJailed() || p.isIPJailed() || p.isTempIPJailed())
			e.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onDamage(EntityDamageEvent e) {
		if (e.isCancelled())
			return;
		if (e.getEntity() instanceof Player) {
			Player d = (Player) e.getEntity();
			PlayerBanList p = PunishmentAPI.getBanList(d.getName());
			if (p.isJailed() || p.isTempJailed() || p.isIPJailed() || p.isTempIPJailed()) {
				e.setCancelled(true);
				return;
			}
			if (TheAPI.getUser(d).getBoolean("God")) {
				DamageGodPlayerEvent event = new DamageGodPlayerEvent(d, e.getDamage(), e.getCause());
				Bukkit.getPluginManager().callEvent(event);
				if (event.isCancelled())
					e.setCancelled(true);
				else
					e.setDamage(event.getDamage());
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onFood(FoodLevelChangeEvent e) {
		if (e.isCancelled())
			return;
		if (e.getEntity() instanceof Player)
			if (TheAPI.getUser((Player) e.getEntity()).getBoolean("God"))
				e.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onDamage(EntityDamageByEntityEvent e) {
		if (e.isCancelled())
			return;
		if (e.getEntity() instanceof Player) {
			Player d = (Player) e.getEntity();
			if (TheAPI.getUser(d).getBoolean("God")) {
				DamageGodPlayerByEntityEvent event = new DamageGodPlayerByEntityEvent(d, e.getDamager(), e.getDamage(),
						e.getCause());
				Bukkit.getPluginManager().callEvent(event);
				if (event.isCancelled())
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
			if (e.getDamager().hasMetadata("damage:min")) {
				min = e.getDamager().getMetadata("damage:min").get(0).asDouble();
			}
			if (e.getDamager().hasMetadata("damage:max")) {
				max = e.getDamager().getMetadata("damage:max").get(0).asDouble();
			}
			if (e.getDamager().hasMetadata("damage:set")) {
				set = e.getDamager().getMetadata("damage:set").get(0).asDouble();
			}
			if (set == 0) {
				if (max != 0 && max > min) {
					double damage = TheAPI.generateRandomDouble(max);
					if (damage < min)
						damage = min;
					if (max > damage)
						damage = 0;
					e.setDamage(e.getDamage() + damage);
				}
			} else {
				e.setDamage(set);
			}

		} catch (Exception err) {

		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onChat(PlayerChatEvent e) {
		if (e.isCancelled())
			return;
		PlayerBanList b = PunishmentAPI.getBanList(e.getPlayer().getName());
		if (b.isTempMuted()) {
			e.setCancelled(true);
			TheAPI.msg(b.getReason(PunishmentType.TEMPMUTE).replace("%time%",
					StringUtils.setTimeToString(b.getExpire(PunishmentType.TEMPMUTE))), e.getPlayer());
			return;
		}
		if (b.isMuted()) {
			e.setCancelled(true);
			TheAPI.msg(b.getReason(PunishmentType.MUTE), e.getPlayer());
			return;
		}
		if (b.isTempIPMuted()) {
			e.setCancelled(true);
			TheAPI.msg(b.getReason(PunishmentType.TEMPMUTEIP).replace("%time%",
					StringUtils.setTimeToString(b.getExpire(PunishmentType.TEMPMUTEIP))), e.getPlayer());
			return;
		}
		if (b.isIPMuted()) {
			e.setCancelled(true);
			TheAPI.msg(b.getReason(PunishmentType.MUTEIP), e.getPlayer());
			return;
		}
	}
}