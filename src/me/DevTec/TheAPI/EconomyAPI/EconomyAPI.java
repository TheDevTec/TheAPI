package me.DevTec.TheAPI.EconomyAPI;

import java.util.List;

import org.bukkit.entity.Player;

import me.DevTec.TheAPI.Utils.DataKeeper.Collections.UnsortedList;
import me.DevTec.TheAPI.Utils.TheAPIUtils.LoaderClass;
import net.milkbowl.vault.economy.Economy;

public class EconomyAPI {
	public static void setEconomy(Economy a) {
		if (a != null) {
			LoaderClass.plugin.economy = a;
			LoaderClass.plugin.e = true;
		} else {
			LoaderClass.plugin.economy = null;
			LoaderClass.plugin.e = false;
		}
	}

	public static Economy getEconomy() {
		return LoaderClass.plugin.economy;
	}

	/**
	 * @segetEconomy()segetEconomy()Warning, this FakeEconomyAPI return Economy
	 *                                       stored in config. Isn't connect to
	 *                                       thgetEconomy()Vault plugin. Working if
	 *                                       is EconomyAPI "disabled" too.
	 * @return FakeEconomyAPI
	 */
	public static FakeEconomyAPI getFakeEconomyAPI(String economyName) {
		return new FakeEconomyAPI(economyName);
	}

	/**
	 * @segetEconomy()segetEconomy()Warning, this FakeEconomyAPI return Economy
	 *                                       stored in config. Isn't connect to
	 *                                       thgetEconomy()Vault plugin. Working if
	 *                                       is EconomyAPI "disabled" too.
	 * @return FakeEconomyAPI
	 */
	@Deprecated
	public static FakeEconomyAPI getFakeEconomyAPI() {
		return new FakeEconomyAPI("Default");
	}

	public static boolean hasBankSupport() {
		if (getEconomy() != null && LoaderClass.plugin.e)
			return getEconomy().hasBankSupport();
		return false;
	}

	public static boolean hasAccount(String player) {
		if (getEconomy() != null && LoaderClass.plugin.e)
			return getEconomy().hasAccount(player);
		return false;
	}

	public static boolean hasAccount(String player, String world) {
		if (getEconomy() != null && LoaderClass.plugin.e)
			return getEconomy().hasAccount(player, world);
		return false;
	}

	public static List<String> getBanks() {
		if (getEconomy() != null && LoaderClass.plugin.e && hasBankSupport())
			return getEconomy().getBanks();
		return new UnsortedList<String>();
	}

	public static String getName() {
		if (getEconomy() != null && LoaderClass.plugin.e)
			return getEconomy().getName();
		return null;
	}

	public static void bankDeposit(String bank, double money) {
		if (getEconomy() != null && LoaderClass.plugin.e && hasBankSupport())
			getEconomy().bankDeposit(bank, money);
	}

	public static void bankWithdraw(String bank, double money) {
		if (getEconomy() != null && LoaderClass.plugin.e && hasBankSupport())
			getEconomy().bankWithdraw(bank, money);
	}

	public static boolean bankHas(String bank, double money) {
		if (getEconomy() != null && LoaderClass.plugin.e && hasBankSupport())
			return bankBalance(bank) >= money;
		return false;
	}

	public static void createBank(String bank, String owner) {
		if (getEconomy() != null && LoaderClass.plugin.e && hasBankSupport())
			getEconomy().createBank(bank, owner);
	}

	public static void deleteBank(String bank) {
		if (getEconomy() != null && LoaderClass.plugin.e && hasBankSupport())
			getEconomy().deleteBank(bank);
	}

	public static boolean isBankMember(String bank, String player) {
		if (getEconomy() != null && LoaderClass.plugin.e && hasBankSupport())
			return getEconomy().isBankMember(bank, player).transactionSuccess();
		return false;
	}

	public static boolean isBankOwner(String bank, String player) {
		if (getEconomy() != null && LoaderClass.plugin.e && hasBankSupport())
			return getEconomy().isBankOwner(bank, player).transactionSuccess();
		return false;
	}

	public static double bankBalance(String bank) {
		if (getEconomy() != null && LoaderClass.plugin.e && hasBankSupport())
			return getEconomy().bankBalance(bank).balance;
		return 0.0;
	}

	public static void depositPlayer(String player, double money) {
		if (getEconomy() != null && LoaderClass.plugin.e)
			getEconomy().depositPlayer(player, money);
	}

	public static void depositPlayer(String player, String world, double money) {
		if (getEconomy() != null && LoaderClass.plugin.e)
			getEconomy().depositPlayer(player, world, money);
	}

	public static void withdrawPlayer(String player, double money) {
		if (getEconomy() != null && LoaderClass.plugin.e)
			getEconomy().withdrawPlayer(player, money);
	}

	public static void withdrawPlayer(String player, String world, double money) {
		if (getEconomy() != null && LoaderClass.plugin.e)
			getEconomy().withdrawPlayer(player, world, money);
	}

	public static double getBalance(String player) {
		if (getEconomy() != null && LoaderClass.plugin.e)
			return getEconomy().getBalance(player);
		return 0.0;
	}

	public static double getBalance(String player, String world) {
		if (getEconomy() != null && LoaderClass.plugin.e)
			return getEconomy().getBalance(player, world);
		return 0.0;
	}

	public static boolean has(String player, double money) {
		if (getEconomy() != null && LoaderClass.plugin.e)
			return getEconomy().has(player, money);
		return false;
	}

	public static boolean has(String player, String world, double money) {
		if (getEconomy() != null && LoaderClass.plugin.e)
			return getEconomy().has(player, world, money);
		return false;
	}

	public static boolean createAccount(String player) {
		if (getEconomy() != null && LoaderClass.plugin.e)
			return getEconomy().createPlayerAccount(player);
		return false;
	}

	public static String format(double money) {
		if (getEconomy() != null && LoaderClass.plugin.e)
			return getEconomy().format(money);
		return "" + money;
	}

	public static boolean hasAccount(Player player) {
		return hasAccount(player.getName());
	}

	public static boolean hasAccount(Player player, String world) {
		return hasAccount(player.getName(), world);
	}

	public static void depositPlayer(Player player, double money) {
		depositPlayer(player.getName(), money);
	}

	public static void depositPlayer(Player player, String world, double money) {
		depositPlayer(player.getName(), world, money);
	}

	public static void withdrawPlayer(Player player, double money) {
		withdrawPlayer(player.getName(), money);
	}

	public static void withdrawPlayer(Player player, String world, double money) {
		withdrawPlayer(player.getName(), world, money);
	}

	public static double getBalance(Player player) {
		return getBalance(player.getName());
	}

	public static double getBalance(Player player, String world) {
		return getBalance(player.getName(), world);
	}

	public static boolean has(Player player, double money) {
		return has(player.getName(), money);
	}

	public static boolean has(Player player, String world, double money) {
		return has(player.getName(), world, money);
	}

	public static boolean createAccount(Player player) {
		return createAccount(player.getName());
	}

}
