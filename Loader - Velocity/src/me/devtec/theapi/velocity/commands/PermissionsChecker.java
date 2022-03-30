package me.devtec.theapi.velocity.commands;

import com.velocitypowered.api.command.CommandSource;

public interface PermissionsChecker {
	public boolean check(CommandSource sender, String perm);
}
