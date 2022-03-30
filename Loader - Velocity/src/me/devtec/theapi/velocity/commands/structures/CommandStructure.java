package me.devtec.theapi.velocity.commands.structures;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import me.devtec.theapi.velocity.commands.CommandTask;
import me.devtec.theapi.velocity.commands.selectors.SelectorType;

public class CommandStructure {

	public static EmptyCommandStructure empty() {
		return new EmptyCommandStructure(null);
	}
	
	private String[] args;
	private int startArg;
	protected String perm;
	private Map<Integer, SelectorType[]> selector = new HashMap<>();
	private Map<Integer, CompletableFuture<Iterable<String>>[]> customSelectors = new HashMap<>();
	private Map<Integer, CommandTask> arg = new HashMap<>();
	private Map<Integer, CommandTask> argmore = new HashMap<>();
	private Map<Integer, CommandTask> argless = new HashMap<>();
	
	private CommandStructure next, prev;
	
	CommandStructure(String[] values) {
		args=values;
	}

	public static CommandStructure create(String... args) {
		return new CommandStructure(args);
	}
	
	public String getPermission() {
		return perm;
	}
	
	public String[] getArgs() {
		return args;
	}
	
	public int getStartArg() {
		return startArg;
	}
	
	public Map<Integer, CompletableFuture<Iterable<String>>[]> getCustomSelectors(){
		return customSelectors;
	}
	
	public Map<Integer, SelectorType[]> getSelectorTypes(){
		return selector;
	}
	
	public Map<Integer, CommandTask> getCommandArguments(){
		return arg;
	}
	
	public Map<Integer, CommandTask> getCommandArgumentsMoreThan(){
		return argmore;
	}
	
	public Map<Integer, CommandTask> getCommandArgumentsLessThan(){
		return argless;
	}

	public CommandStructure requirement(int startArg, String... values) {
		CommandStructure str = new CommandStructure(values);
		str.prev=this;
		next=str;
		str.perm=perm;
		str.startArg=startArg;
		return str;
	}
	
	public CommandStructure permission(String perm) {
		CommandStructure str = new CommandStructure(null);
		str.prev=this;
		next=str;
		str.perm=perm;
		str.startArg=startArg;
		return str;
	}
	
	public CommandStructure multiSelectors(int argumentPosition, SelectorType[] selectorTypes, CompletableFuture<Iterable<String>>[] customSelectors) {
		this.selector.put(argumentPosition, selectorTypes);
		this.customSelectors.put(argumentPosition, customSelectors);
		return this;
	}
	
	public CommandStructure selector(int argumentPosition, SelectorType selector) {
		this.selector.put(argumentPosition, new SelectorType[] {selector});
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public CommandStructure customSelector(int argumentPosition, CompletableFuture<Iterable<String>> selector) {
		this.customSelectors.put(argumentPosition, new CompletableFuture[] {selector});
		return this;
	}
	
	public CommandStructure customSelector(int argumentPosition, Iterable<String> values) {
		return customSelector(argumentPosition, new CompletableFuture<Iterable<String>>() {
			@Override
			public Iterable<String> get() {
				return values;
			}
		});
	}
	
	public CommandStructure customSelector(int pos, String... values) {
		return customSelector(pos, Arrays.asList(values));
	}

	public CommandStructure onArgs(int i, CommandTask object) {
		arg.put(startArg+i-(startArg!=0?1:0), object);
		return this;
	}

	public CommandStructure onArgsOrMore(int i, CommandTask object) {
		argmore.put(startArg+i-(startArg!=0?1:0), object);
		return this;
	}

	public CommandStructure onArgsOrLess(int i, CommandTask object) {
		argless.put(startArg+i-(startArg!=0?1:0), object);
		return this;
	}

	public CommandStructure onAnyArg(CommandTask object) {
		arg.put(-1, object);
		return this;
	}
	
	public CommandStructure first() {
		return prev==null ? this : prev.first();
	}
	
	public CommandStructure last() {
		return next==null ? this : next.last();
	}
	
	public CommandStructure getNext() {
		return next==null ? this : next;
	}
	
	public CommandStructure getPrevious() {
		return prev==null ? this : prev;
	}
}
