package me.devtec.theapi.velocity.commands;

import java.util.Map;

import com.velocitypowered.api.command.CommandSource;

public interface CommandTask {
	public void process(CommandSource sender, Map<Integer, String> selectors);
}
