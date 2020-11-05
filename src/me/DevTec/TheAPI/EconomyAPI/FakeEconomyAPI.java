package me.DevTec.TheAPI.EconomyAPI;

import org.bukkit.entity.Player;

import me.DevTec.TheAPI.TheAPI;
import me.DevTec.TheAPI.Utils.TheAPIUtils.LoaderClass;

public class FakeEconomyAPI {
	private String w;

	public FakeEconomyAPI(String economyStore) {
		w = economyStore;
	}

	public String getName() {
		return w;
	}

	public boolean hasAccount(Player player) {
		return hasAccount(player.getName());
	}

	public void add(Player player, double money) {
		add(player.getName(), money);
	}

	public void take(Player player, double money) {
		take(player.getName(), money);
	}

	public boolean hasAccount(String player) {
		return TheAPI.getUser(player).getString("economy." + w) != null;
	}

	public void despositPlayer(String player, double money) {
		add(player, money);
	}

	public void despositPlayer(Player player, double money) {
		add(player.getName(), money);
	}

	public void withdrawPlayer(Player player, double money) {
		take(player.getName(), money);
	}

	public void withdrawPlayer(String player, double money) {
		take(player, money);
	}

	public void add(String player, double money) {
		TheAPI.getUser(player).setAndSave("economy." + w, balance(player) + money);
	}

	public void take(String player, double money) {
		TheAPI.getUser(player).setAndSave("economy." + w, balance(player) - money);
	}

	public void set(Player player, double money) {
		set(player.getName(), money);
	}

	public void set(String player, double money) {
		TheAPI.getUser(player).setAndSave("economy." + w, money);
	}

	public double getBalance(String player) {
		return balance(player);
	}

	public double getBalance(Player player) {
		return balance(player.getName());
	}

	public double balance(String player) {
		return TheAPI.getUser(player).getDouble("economy." + w);
	}

	public double balance(Player player) {
		return balance(player.getName());
	}

	public boolean has(String player, double money) {
		return balance(player) >= money;
	}

	public boolean has(Player player, double money) {
		return has(player.getName(), money);
	}

	public boolean createAccount(Player player) {
		return createAccount(player.getName());
	}

	public boolean createAccount(String player) {
		if (!hasAccount(player)) {
			TheAPI.getUser(player).setAndSave("economy." + w, 0.0);
			return true;
		}
		return false;
	}

	public void setSymbol(String symbol) {
		LoaderClass.config.set("FakeEconomyAPI.Symbol", symbol);
		LoaderClass.config.save();
	}

	public String getSymbol() {
		return LoaderClass.config.getString("FakeEconomyAPI.Symbol");
	}

	/**
	 * @param s Available placeholders: %symbol%, $ %money%, %eco%, %balance%
	 */
	public void setFormat(String s) {
		LoaderClass.config.set("FakeEconomyAPI.Format", s);
		LoaderClass.config.save();
	}

	public String format(double money) {
		return LoaderClass.config.getString("FakeEconomyAPI.Format").replace("%money%", String.valueOf(money))
				.replace("%eco%", String.valueOf(money)).replace("%balance%", String.valueOf(money))
				.replace("%symbol%", getSymbol()).replace("$", getSymbol());
	}
}
