package me.Straiker123;

import org.bukkit.entity.Player;

public class FakeEconomyAPI {
	public boolean hasAccount(Player player) {
		return hasAccount(player.getName());
	}
	public void depositPlayer(Player player, double money) {
		depositPlayer(player.getName(), money);
	}
	public void withdrawPlayer(Player player, double money) {
		withdrawPlayer(player.getName(), money);
	}
	
	public boolean hasAccount(String player) {
		return LoaderClass.data.getConfig().getString("data."+player+".economy")!=null;
	}
	public void depositPlayer(String player, double money) {
		LoaderClass.data.getConfig().set("data."+player+".economy",getBalance(player)+money);
		LoaderClass.data.save();
	}
	public void withdrawPlayer(String player, double money) {
		LoaderClass.data.getConfig().set("data."+player+".economy",getBalance(player)-money);
		LoaderClass.data.save();
	}
	public double getBalance(String player) {
		return LoaderClass.data.getConfig().getDouble("data."+player+".economy");
	}
	public double getBalance(Player player) {
		return getBalance(player.getName());
	}
	public boolean has(String player, double money) {
		return getBalance(player) >= money;
	}
	public boolean has(Player player, double money) {
		return has(player.getName(),money);
	}
	public boolean createAccount(Player player) {
		return createAccount(player.getName());
	}
	
	public boolean createAccount(String player) {
		if(!hasAccount(player)) {
			LoaderClass.data.getConfig().set("data."+player+".economy",0.0);
			LoaderClass.data.save();
			return true;
		}
		return false;
	}
	
	public void setSymbol(String symbol) {
		LoaderClass.config.getConfig().set("FakeEconomyAPI.Symbol", symbol);
		LoaderClass.config.save();
	}
	
	public String getSymbol() {
		return LoaderClass.config.getConfig().getString("FakeEconomyAPI.Symbol");
	}
	/**
	 * @param s
	 * Available placeholders:
	 * %symbol%, $
	 * %money%, %eco%, %balance%
	 */
	public void setFormat(String s) {
		LoaderClass.config.getConfig().set("FakeEconomyAPI.Format", s);
		LoaderClass.config.save();
		
	}
	
	public String format(double money) {
		return LoaderClass.config.getConfig().getString("FakeEconomyAPI.Format")
				.replace("%money%", String.valueOf(money)).replace("%eco%", String.valueOf(money))
				.replace("%balance%", String.valueOf(money)).replace("%symbol%", getSymbol())
				.replace("$", getSymbol());
	}
}
