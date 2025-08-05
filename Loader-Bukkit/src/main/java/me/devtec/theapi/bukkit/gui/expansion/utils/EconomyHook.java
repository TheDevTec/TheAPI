package me.devtec.theapi.bukkit.gui.expansion.utils;

public interface EconomyHook {
	double getBalance(String name, String world);

	void deposit(String name, String world, double balance);

	void withdraw(String name, String world, double balance);

	default boolean has(String name, String world, double balance) {
		return getBalance(name, world) >= balance;
	}

	String format(Double value);
}
