package me.DevTec.Abstract;

import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.Location;

import me.DevTec.Bans.BanList;
import me.DevTec.Bans.PlayerBanList;

public interface AbstractPunishmentAPI {

	// IP
	public String getIP(String player);

	public List<String> getPlayersOnIP(String ip);

	// Ban
	public void ban(String player, String reason);

	public void banIP(String playerOrIP, String reason);

	public void tempban(String player, String reason, long time);

	public void tempbanIP(String playerOrIP, String reason, long time);

	// Other
	public void kick(String player, String reason);

	public void kickIP(String playerOrIP, String reason);

	public void warn(String player, String reason);

	public void warnIP(String playerOrIP, String reason);

	// Jail
	public void jail(String player, String reason); // Random jail

	public void jail(String player, String reason, String jail);

	public void tempjail(String player, String reason, long time); // Random jail

	public void tempjail(String player, String reason, long time, String jail);
	
	public void jailIP(String playerOrIP, String reason); // Random jail

	public void jailIP(String playerOrIP, String reason, String jail);

	public void tempjailIP(String playerOrIP, String reason, long time); // Random jail

	public void tempjailIP(String playerOrIP, String reason, long time, String jail);

	public void setjail(Location location, String name);

	public void deljail(String name);

	public List<String> getjails();

	// Pardon
	public void unban(String player);

	public void unbanIP(String playerOrIP);

	public void unjail(String player);

	public void unjailIP(String playerOrIP);

	public void unmute(String player);

	public void unmuteIP(String playerOrIP);

	// Mute
	public void mute(String player, String reason);

	public void tempmute(String player, String reason, long time);

	public void muteIP(String playerOrIP, String reason);

	public void tempmuteIP(String playerOrIP, String reason, long time);

	// BanList
	public BanList getBanList(); // everyting

	public PlayerBanList getBanList(String player);

	// Utils
	public static Pattern ipFinder = Pattern.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
	public static boolean isIP(String text) {
		if(text==null)return false;
		return ipFinder.matcher(text.replaceFirst("/", "")).find();
	}
}
