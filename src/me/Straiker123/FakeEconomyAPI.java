package me.Straiker123;

public class FakeEconomyAPI {
	
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
	public boolean has(String player, double money) {
		return getBalance(player) >= money;
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
