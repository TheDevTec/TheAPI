package me.Straiker123;

import java.util.ArrayList;
import java.util.List;

import me.DevTec.TheVault.Economy;

public class TheEconomyAPI {
	boolean tve = LoaderClass.plugin.tve;
	Economy e = LoaderClass.tveeconomy;
	public double balance(String player) {
		if(tve && e!=null)
		return e.balance(player);
		return 0;
	}

	public double balance(String player, String world) {
		return balance(player);
	}

	public boolean createAccount(String player) {
		if(tve && e!=null)
		return e.createAccount(player);
		return false;
	}

	public boolean createAccount(String player, String world) {
		return createAccount(player);
	}

	public void deposit(String player, double money) {
		if(tve && e!=null)
		e.deposit(player,money);
	}

	public void deposit(String player, double money, String world) {
		if(tve && e!=null)
		e.deposit(player,money,world);
	}

	public String format(double money) {
		if(tve && e!=null)
			return e.format(money);
		return symbol()+money;
	}

	public List<String> getUsers() {
		if(tve && e!=null)
			return e.getUsers();
		return new ArrayList<String>();
	}

	public List<String> getUsers(String world) {
		if(tve && e!=null)
			return e.getUsers(world);
		return new ArrayList<String>();
	}
	
		public boolean has(String player, double money) {
		return false;
	}

	public boolean has(String player, double money, String world) {
		return has(player,money,world);
	}

	public boolean hasAccount(String player) {
		if(tve && e!=null)
			return e.hasAccount(player);
		return false;
	}

	public boolean hasAccount(String player, String world) {
		return hasAccount(player);
	}

	public String name() {
		// TODO Auto-generated method stub
		return null;
	}

	public void set(String player, double money) {
		// TODO Auto-generated method stub
		
	}

	public void set(String player, double money, String world) {
		// TODO Auto-generated method stub
		
	}

	public String symbol() {
		// TODO Auto-generated method stub
		return null;
	}

	public void withdraw(String player, double money) {
		// TODO Auto-generated method stub
		
	}

	public void withdraw(String player, double money, String world) {
		// TODO Auto-generated method stub
		
	}
	
}
