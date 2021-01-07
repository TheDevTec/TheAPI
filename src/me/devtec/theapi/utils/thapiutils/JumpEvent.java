package me.devtec.theapi.utils.thapiutils;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.utils.listener.events.PlayerJumpEvent;

public class JumpEvent implements Listener {

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if (e.isCancelled())return;
		double jump = e.getTo().getY() - e.getFrom().getY();
		boolean has = true;
		try {
			has = !e.getPlayer().hasPotionEffect(PotionEffectType.LEVITATION);
		} catch (Exception | NoSuchFieldError | NoSuchMethodError es) {}
		if (jump > 0 && !e.getPlayer().isFlying() && has) {
			PlayerJumpEvent event = new PlayerJumpEvent(e.getPlayer(), e.getFrom(), e.getTo(), jump);
			TheAPI.callEvent(event);
			if (event.isCancelled())
				e.setCancelled(true);
		}
	}
}
