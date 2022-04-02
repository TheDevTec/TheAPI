package me.devtec.theapi.bungee.commands.holder;

import net.md_5.bungee.api.CommandSender;

public interface BungeePermissionsChecker {
	public boolean check(CommandSender sender, String perm);
}
