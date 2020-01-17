package me.Straiker123;

import java.util.ArrayList;
import java.util.List;

public class ReportSystem {
	public void sendReport(String sender, String reported, String message) {
		List<String> list = new ArrayList<String>();
		if(LoaderClass.data.getConfig().getString("data."+sender+".reports."+reported)!=null)
			list=LoaderClass.data.getConfig().getStringList("data."+sender+".reports."+reported);
		LoaderClass.data.getConfig().set("data."+sender+".reports."+reported, list.add(message));
		LoaderClass.data.save();
		TheAPI.broadcast(LoaderClass.config.getConfig().getString("Format.Report").replace("%sender%", sender).replace("%target%", reported)
				.replace("%target%", reported).replace("%message%", message), LoaderClass.config.getConfig().getString("Format.Report-Permission"));
	}
	
	public boolean isReported(String player) {
	boolean report=false;
		if(LoaderClass.data.getConfig().getString("data")!=null)
		for(String s:LoaderClass.data.getConfig().getConfigurationSection("data").getKeys(false)) {
			if(LoaderClass.data.getConfig().getString("data."+s+".reports")!=null)
			for(String d:LoaderClass.data.getConfig().getConfigurationSection("data."+s+".reports").getKeys(false)) {
				if(d.equals(player))report=true;
			}
		}
		return report;
	}

	/**
	 * List of messages of 'player' that reported 'target'
	 * @return List<String>
	 */
	public List<String> getReportsMessages(String player, String target) {
		List<String> a = new ArrayList<String>();
		if(LoaderClass.data.getConfig().getString("data."+player+".reports."+target)!=null)
			a=LoaderClass.data.getConfig().getStringList("data."+player+".reports."+target);
			return a;
	}
	/**
	 * List of players that 'player' reported
	 * @return List<String>
	 */
	public List<String> getReported(String player) {
		List<String> a = new ArrayList<String>();
		if(LoaderClass.data.getConfig().getString("data."+player+".reports")!=null)
			for(String s:LoaderClass.data.getConfig().getConfigurationSection("data."+player+".reports").getKeys(false))
			a.add(s);
			return a;
	}
	
}
