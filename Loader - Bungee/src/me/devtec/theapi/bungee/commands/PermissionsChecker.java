package me.devtec.theapi.bungee.commands;

import net.md_5.bungee.api.CommandSender;

public interface PermissionsChecker {
	public boolean check(CommandSender sender, String perm);
}
