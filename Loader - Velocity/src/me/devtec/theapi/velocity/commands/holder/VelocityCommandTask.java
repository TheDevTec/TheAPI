package me.devtec.theapi.velocity.commands.holder;

import java.util.Map;

import com.velocitypowered.api.command.CommandSource;

import me.devtec.shared.commands.holder.CommandTask;

public interface VelocityCommandTask extends CommandTask<CommandSource> {
	public void process(CommandSource sender, Map<Integer, String> selectors);
}
