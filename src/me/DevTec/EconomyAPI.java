package me.DevTec;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Player;

import me.DevTec.Other.LoaderClass;
import net.milkbowl.vault.economy.Economy;

public class EconomyAPI {
	private Economy e = LoaderClass.plugin.economy;
	private me.DevTec.TheVault.Economy t = LoaderClass.plugin.tveeconomy;
	private me.DevTec.TheVault.Bank b = LoaderClass.plugin.bank;

	public void setEconomy(Economy a) {
		if (a != null) {
			e = a;
			LoaderClass.plugin.e = true;
		} else {

			e = null;
			LoaderClass.plugin.e = false;
		}
	}

	public void setEconomy(me.DevTec.TheVault.Economy a) {
		if (a != null) {
			t = a;
			LoaderClass.plugin.tve = true;
		} else {

			t = null;
			LoaderClass.plugin.tve = false;
		}
	}

	@Nullable
	public Economy getEconomy() {
		return e;
	}

	@Nullable
	public me.DevTec.TheVault.Economy getTheEconomy() {
		return t;
	}

	@Nullable
	public me.DevTec.TheVault.Bank getTheBank() {
		return b;
	}

	/**
	 * @see see Warning, this FakeEconomyAPI return Economy stored in config. Isn't
	 *      connect to the Vault plugin. Working if is EconomyAPI "disabled" too.
	 * @return FakeEconomyAPI
	 */
	public FakeEconomyAPI getFakeEconomyAPI(String economyName) {
		return new FakeEconomyAPI(economyName);
	}

	/**
	 * @see see Warning, this FakeEconomyAPI return Economy stored in config. Isn't
	 *      connect to the Vault plugin. Working if is EconomyAPI "disabled" too.
	 * @return FakeEconomyAPI
	 */
	@Deprecated
	public FakeEconomyAPI getFakeEconomyAPI() {
		return new FakeEconomyAPI("Default");
	}

	public boolean hasBankSupport() {
		if (e != null && LoaderClass.plugin.e)
			return e.hasBankSupport();
		if (b != null && LoaderClass.plugin.tbank)
			return true;
		return false;
	}

	@SuppressWarnings("deprecation")
	public boolean hasAccount(String player) {
		if (e != null && LoaderClass.plugin.e)
			return e.hasAccount(player);
		if (t != null && LoaderClass.plugin.tve)
			return t.hasAccount(player);
		return false;
	}

	@SuppressWarnings("deprecation")
	public boolean hasAccount(String player, String world) {
		if (e != null && LoaderClass.plugin.e)
			return e.hasAccount(player, world);
		if (t != null && LoaderClass.plugin.tve)
			return t.hasAccount(player, world);
		return false;
	}

	public List<String> getBanks() {
		if (e != null && LoaderClass.plugin.e && hasBankSupport())
			return e.getBanks();
		if (b != null && LoaderClass.plugin.tbank && hasBankSupport())
			return b.getBanks();
		return new ArrayList<String>();
	}

	public String getName() {
		if (e != null && LoaderClass.plugin.e)
			return e.getName();
		if (t != null && LoaderClass.plugin.tve)
			return t.name();
		if (b != null && LoaderClass.plugin.tbank && hasBankSupport())
			return b.name();
		return null;
	}

	public void bankDeposit(String bank, double money) {
		if (e != null && LoaderClass.plugin.e && hasBankSupport())
			e.bankDeposit(bank, money);
		if (b != null && LoaderClass.plugin.tbank && hasBankSupport())
			b.deposit(bank, money);
	}

	public void bankWithdraw(String bank, double money) {
		if (e != null && LoaderClass.plugin.e && hasBankSupport())
			e.bankWithdraw(bank, money);
		if (b != null && LoaderClass.plugin.tbank && hasBankSupport())
			b.withdraw(bank, money);
	}

	public boolean bankHas(String bank, double money) {
		if (e != null && LoaderClass.plugin.e && hasBankSupport())
			return bankBalance(bank) >= money;
		if (b != null && LoaderClass.plugin.tbank && hasBankSupport())
			return b.has(bank, money);
		return false;
	}

	@SuppressWarnings("deprecation")
	public void createBank(String bank, String owner) {
		if (e != null && LoaderClass.plugin.e && hasBankSupport())
			e.createBank(bank, owner);
		if (b != null && LoaderClass.plugin.tbank && hasBankSupport())
			b.create(bank, owner);
	}

	public void deleteBank(String bank) {
		if (e != null && LoaderClass.plugin.e && hasBankSupport())
			e.deleteBank(bank);
		if (b != null && LoaderClass.plugin.tbank && hasBankSupport())
			b.delete(bank);
	}

	@SuppressWarnings("deprecation")
	public boolean isBankMember(String bank, String player) {
		if (e != null && LoaderClass.plugin.e && hasBankSupport())
			return e.isBankMember(bank, player).transactionSuccess();
		if (b != null && LoaderClass.plugin.tbank && hasBankSupport())
			b.isMember(bank, player);
		return false;
	}

	@SuppressWarnings("deprecation")
	public boolean isBankOwner(String bank, String player) {
		if (e != null && LoaderClass.plugin.e && hasBankSupport())
			return e.isBankOwner(bank, player).transactionSuccess();
		if (b != null && LoaderClass.plugin.tbank && hasBankSupport())
			b.getOwner(bank).equals(player);
		return false;
	}

	public double bankBalance(String bank) {
		if (e != null && LoaderClass.plugin.e && hasBankSupport())
			return e.bankBalance(bank).balance;
		if (b != null && LoaderClass.plugin.tbank && hasBankSupport())
			b.balance(bank);
		return 0.0;
	}

	@SuppressWarnings("deprecation")
	public void depositPlayer(String player, double money) {
		if (e != null && LoaderClass.plugin.e)
			e.depositPlayer(player, money);
		if (t != null && LoaderClass.plugin.tve)
			t.deposit(player, money);
	}

	@SuppressWarnings("deprecation")
	public void depositPlayer(String player, String world, double money) {
		if (e != null && LoaderClass.plugin.e)
			e.depositPlayer(player, world, money);
		if (t != null && LoaderClass.plugin.tve)
			t.deposit(player, money, world);
	}

	@SuppressWarnings("deprecation")
	public void withdrawPlayer(String player, double money) {
		if (e != null && LoaderClass.plugin.e)
			e.withdrawPlayer(player, money);
		if (t != null && LoaderClass.plugin.tve)
			t.withdraw(player, money);
	}

	@SuppressWarnings("deprecation")
	public void withdrawPlayer(String player, String world, double money) {
		if (e != null && LoaderClass.plugin.e)
			e.withdrawPlayer(player, world, money);
		if (t != null && LoaderClass.plugin.tve)
			t.withdraw(player, money, world);
	}

	@SuppressWarnings("deprecation")
	public double getBalance(String player) {
		if (e != null && LoaderClass.plugin.e)
			return e.getBalance(player);
		if (t != null && LoaderClass.plugin.tve)
			return t.balance(player);
		return 0.0;
	}

	@SuppressWarnings("deprecation")
	public double getBalance(String player, String world) {
		if (e != null && LoaderClass.plugin.e)
			return e.getBalance(player, world);
		if (t != null && LoaderClass.plugin.tve)
			return t.balance(player, world);
		return 0.0;
	}

	@SuppressWarnings("deprecation")
	public boolean has(String player, double money) {
		if (e != null && LoaderClass.plugin.e)
			return e.has(player, money);
		if (t != null && LoaderClass.plugin.tve)
			return t.has(player, money);
		return false;
	}

	@SuppressWarnings("deprecation")
	public boolean has(String player, String world, double money) {
		if (e != null && LoaderClass.plugin.e)
			return e.has(player, world, money);
		if (t != null && LoaderClass.plugin.tve)
			return t.has(player, money, world);
		return false;
	}

	@SuppressWarnings("deprecation")
	public boolean createAccount(String player) {
		if (e != null && LoaderClass.plugin.e)
			return e.createPlayerAccount(player);
		if (t != null && LoaderClass.plugin.tve)
			return t.createAccount(player);
		return false;
	}

	public String format(double money) {
		if (e != null && LoaderClass.plugin.e)
			return e.format(money);
		if (t != null && LoaderClass.plugin.tve)
			return t.format(money);
		return "" + money;
	}

	public boolean hasAccount(Player player) {
		return hasAccount(player.getName());
	}

	public boolean hasAccount(Player player, String world) {
		return hasAccount(player.getName(), world);
	}

	public void depositPlayer(Player player, double money) {
		depositPlayer(player.getName(), money);
	}

	public void depositPlayer(Player player, String world, double money) {
		depositPlayer(player.getName(), world, money);
	}

	public void withdrawPlayer(Player player, double money) {
		withdrawPlayer(player.getName(), money);
	}

	public void withdrawPlayer(Player player, String world, double money) {
		withdrawPlayer(player.getName(), world, money);
	}

	public double getBalance(Player player) {
		return getBalance(player.getName());
	}

	public double getBalance(Player player, String world) {
		return getBalance(player.getName(), world);
	}

	public boolean has(Player player, double money) {
		return has(player.getName(), money);
	}

	public boolean has(Player player, String world, double money) {
		return has(player.getName(), world, money);
	}

	public boolean createAccount(Player player) {
		return createAccount(player.getName());
	}

}
