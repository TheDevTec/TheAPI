package me.devtec.theapi.bungee.commands.structures;

import java.util.concurrent.CompletableFuture;

import me.devtec.theapi.bungee.commands.CommandTask;
import me.devtec.theapi.bungee.commands.selectors.SelectorType;

public class EmptyCommandStructure extends CommandStructure {
	EmptyCommandStructure(String[] values) {
		super(values);
	}
	
	@Override
	public CommandStructure onArgs(int pos, CommandTask runnable) {
		return onAnyArg(runnable);
	}
	
	@Override
	public CommandStructure onAnyArg(CommandTask object) {
		return super.onAnyArg(object);
	}
	
	@Override
	public CommandStructure onArgsOrLess(int i, CommandTask runnable) {
		return onAnyArg(runnable);
	}
	
	@Override
	public CommandStructure onArgsOrMore(int i, CommandTask runnable) {
		return onAnyArg(runnable);
	}
	
	public CommandStructure multiSelectors(int argumentPosition, SelectorType[] selectorTypes, CompletableFuture<Iterable<String>>[] customSelectors) {
		return this;
	}
	
	public CommandStructure selector(int argumentPosition, SelectorType selector) {
		return this;
	}
	
	public CommandStructure customSelector(int argumentPosition, CompletableFuture<Iterable<String>> selector) {
		return this;
	}
	
	public CommandStructure customSelector(int argumentPosition, Iterable<String> values) {
		return this;
	}
	
	public CommandStructure customSelector(int pos, String... values) {
		return this;
	}
	
	@Override
	public CommandStructure requirement(int startArg, String... values) {
		return this;
	}
	
	@Override
	public CommandStructure permission(String perm) {
		super.perm=perm;
		return this;
	}
}
