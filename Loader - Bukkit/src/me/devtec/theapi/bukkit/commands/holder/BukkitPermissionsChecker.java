package me.devtec.theapi.bukkit.commands.holder;

import org.bukkit.command.CommandSender;

public interface BukkitPermissionsChecker {
	public boolean check(CommandSender sender, String perm);
}
