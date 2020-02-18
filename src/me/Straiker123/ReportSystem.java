package me.Straiker123;

import java.util.ArrayList;
import java.util.List;

public class ReportSystem {

	public void sendReport(String sender, String reported, String reason, String message) {
		int s = getID(sender);
		LoaderClass.data.getConfig().set("report."+sender+"."+s+".player",reported);
		LoaderClass.data.getConfig().set("report."+sender+"."+s+".reason",reason);
		LoaderClass.data.getConfig().set("report."+sender+"."+s+".time",System.currentTimeMillis());
		LoaderClass.data.getConfig().set("report."+sender+"."+s+".message",message);
		LoaderClass.data.save();
	}
	
	public List<Report> getReports(String player){
		List<Report> a = new ArrayList<Report>();
		if(LoaderClass.data.getConfig().getString("report."+player) != null)
		for(String s : LoaderClass.data.getConfig().getConfigurationSection("report."+player).getKeys(false)) {
			//sender, reported, reason, message
			a.add(new Report(
					s
					,player
					,LoaderClass.data.getConfig().getString("report."+player+"."+s+".player")
					,LoaderClass.data.getConfig().getString("report."+player+"."+s+".reason")
					,LoaderClass.data.getConfig().getString("report."+player+"."+s+".message")
					,LoaderClass.data.getConfig().getLong("report."+player+"."+s+".time")));
		}
		return a;
	}

	public List<String> getPlayers(){
		List<String> a = new ArrayList<String>();
		if(LoaderClass.data.getConfig().getString("report") != null)
		for(String s : LoaderClass.data.getConfig().getConfigurationSection("report").getKeys(false))
			if(LoaderClass.data.getConfig().getString("report."+s) != null)a.add(s);
		return a;
	}
	public void removePlayer(String player){
		LoaderClass.data.getConfig().set("report."+player,null);
		LoaderClass.data.save();
	}
	public boolean hasPlayer(String player){
		return LoaderClass.data.getConfig().getString("report."+player) != null;
	}
	
	public Report getReport(String player, int s) {
		if(hasReport(player,s))
		return new Report(s+"", player,LoaderClass.data.getConfig().getString("report."+player+"."+s+".player")
				,LoaderClass.data.getConfig().getString("report."+player+"."+s+".reason")
				,LoaderClass.data.getConfig().getString("report."+player+"."+s+".message")
				,LoaderClass.data.getConfig().getLong("report."+player+"."+s+".time"));
		return null;
	}
	
	public List<String> getReportIDs(String player) {
		List<String> a = new ArrayList<String>();
		if(LoaderClass.data.getConfig().getString("report."+player) != null)
		for(String s : LoaderClass.data.getConfig().getConfigurationSection("report."+player).getKeys(false))a.add(s);
		return a;
	}

	public void removeReport(String player, int s) {
		getReport(player,s).remove();
	}
	public void setSolved(String player, int s, boolean solved) {
		getReport(player,s).setSolved(solved);
	}
	public boolean isSolved(String player, int s) {
		return getReport(player,s).isSolved();
	}
	public boolean hasReport(String player, int s) {
		return LoaderClass.data.getConfig().getString("report."+player+"."+s) != null;
	}
	
	private int getID(String s){
		int d = 1;
		for(int i = 1; i > 0; ++i) {
			if(LoaderClass.data.getConfig().getString("report."+s+"."+i)==null) {
				d=i;
				break;
			}
		}
		return d;
	}
}
