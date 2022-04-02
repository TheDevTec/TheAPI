package me.devtec.theapi.velocity.commands.holder;

import com.velocitypowered.api.command.CommandSource;

public interface VelocityPermissionsChecker {
	public boolean check(CommandSource sender, String perm);
}
