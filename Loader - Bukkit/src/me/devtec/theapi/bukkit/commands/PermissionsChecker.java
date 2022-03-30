package me.devtec.theapi.bukkit.commands;

import org.bukkit.command.CommandSender;

public interface PermissionsChecker {
	public boolean check(CommandSender sender, String perm);
}
