package me.devtec.shared.commands.holder;

import me.devtec.shared.commands.structures.CommandStructure;

public interface CommandExecutor<S> {
	public void execute(S sender, CommandStructure<S> structure, String[] args);
}
