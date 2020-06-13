package me.DevTec.Bans;

import java.util.ArrayList;
import java.util.List;

import me.DevTec.Other.LoaderClass;

public class ReportSystem {

	public void sendReport(String sender, String reported, String reason, String message) {
		int s = getID(sender);
		LoaderClass.data.set("report." + sender + "." + s + ".player", reported);
		LoaderClass.data.set("report." + sender + "." + s + ".reason", reason);
		LoaderClass.data.set("report." + sender + "." + s + ".time", System.currentTimeMillis());
		LoaderClass.data.set("report." + sender + "." + s + ".message", message);
		LoaderClass.data.save();
	}

	public List<Report> getReports(String player) {
		List<Report> a = new ArrayList<Report>();
		if (LoaderClass.data.exist("report." + player))
			for (String s : LoaderClass.data.getKeys("report." + player)) {
				// sender, reported, reason, message
				a.add(new Report(s, player, LoaderClass.data.getString("report." + player + "." + s + ".player"),
						LoaderClass.data.getString("report." + player + "." + s + ".reason"),
						LoaderClass.data.getString("report." + player + "." + s + ".message"),
						LoaderClass.data.getLong("report." + player + "." + s + ".time")));
			}
		return a;
	}

	public List<String> getPlayers() {
		List<String> a = new ArrayList<String>();
		if (LoaderClass.data.exist("report"))
			for (String s : LoaderClass.data.getKeys("report"))
				a.add(s);
		return a;
	}

	public void removePlayer(String player) {
		LoaderClass.data.set("report." + player, null);
	}

	public boolean hasPlayer(String player) {
		return LoaderClass.data.getString("report." + player) != null;
	}

	public Report getReport(String player, int s) {
		if (hasReport(player, s))
			return new Report(s + "", player, LoaderClass.data.getString("report." + player + "." + s + ".player"),
					LoaderClass.data.getString("report." + player + "." + s + ".reason"),
					LoaderClass.data.getString("report." + player + "." + s + ".message"),
					LoaderClass.data.getLong("report." + player + "." + s + ".time"));
		return null;
	}

	public List<String> getReportIDs(String player) {
		List<String> a = new ArrayList<String>();
		if (LoaderClass.data.exist("report." + player))
			for (String s : LoaderClass.data.getKeys("report." + player))
				a.add(s);
		return a;
	}

	public void removeReport(String player, int s) {
		getReport(player, s).remove();
	}

	public void setSolved(String player, int s, boolean solved) {
		getReport(player, s).setSolved(solved);
	}

	public boolean isSolved(String player, int s) {
		return getReport(player, s).isSolved();
	}

	public boolean hasReport(String player, int s) {
		return LoaderClass.data.getString("report." + player + "." + s) != null;
	}

	private int getID(String s) {
		int d = 1;
		for (int i = 1; i > 0; ++i) {
			if (LoaderClass.data.getString("report." + s + "." + i) == null) {
				d = i;
				break;
			}
		}
		return d;
	}
}
