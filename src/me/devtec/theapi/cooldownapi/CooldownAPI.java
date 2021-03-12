package me.devtec.theapi.cooldownapi;

import java.util.UUID;

import org.bukkit.entity.Player;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.utils.datakeeper.User;
import me.devtec.theapi.utils.theapiutils.Validator;

public class CooldownAPI {
	private final User c;

	public CooldownAPI(User player) {
		Validator.validate(player == null, "User is null");
		c = player;
	}

	public CooldownAPI(String player) {
		this(TheAPI.getUser(player));
	}

	public CooldownAPI(UUID player) {
		this(TheAPI.getUser(player));
	}

	public CooldownAPI(Player player) {
		this(TheAPI.getUser(player.getUniqueId()));
	}

	public User getUser() {
		return c;
	}

	public void createCooldown(String cooldown, double length) {
		c.set("cooldown." + cooldown + ".start", System.currentTimeMillis() / 20);
		c.setAndSave("cooldown." + cooldown + ".time", length);
	}

	public void createCooldown(String cooldown, int length) {
		createCooldown(cooldown, (double) length);
	}

	public boolean expired(String cooldown) {
		return getTimeToExpire(cooldown) < 0;
	}

	/**
	 * 
	 * @return long If return is -1, it mean cooldown isn't exist
	 */
	public long getStart(String cooldown) {
		return c.exist("cooldown." + cooldown + ".start") ? c.getLong("cooldown." + cooldown + ".start") : -1;
	}

	/**
	 * 
	 * @return long If return is -1, it mean cooldown isn't exist
	 */
	public long getTimeToExpire(String cooldown) {
		return (long) (getStart(cooldown) != -1
				? (getStart(cooldown) - System.currentTimeMillis() / 20) + getCooldown(cooldown)
				: -1);

	}

	/**
	 * 
	 * @return double If return is -1, it mean cooldown isn't exist
	 */
	public double getCooldown(String cooldown) {
		return c.exist("cooldown." + cooldown + ".time") ? c.getDouble("cooldown." + cooldown + ".time") : -1;
	}

	public void removeCooldown(String cooldown) {
		c.remove("cooldown." + cooldown);
		c.save();
	}
}
