package me.Straiker123.Abstract;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;

import me.Straiker123.BanList;
import me.Straiker123.PlayerBanList;

public interface AbstractPunishmentAPI {

	// IP
	public String getIP(String player);

	public List<String> getPlayersOnIP(String ip);

	// Ban
	public void ban(String playerOrIP, String reason);

	public void banIP(String playerOrIP, String reason);

	public void tempban(String playerOrIP, String reason, long time);

	public void tempbanIP(String playerOrIP, String reason, long time);

	// Other
	public void kick(String playerOrIP, String reason);

	public void warn(String playerOrIP, String reason); // Use \n in reason for multiple lines

	// Jail
	public void jail(String playerOrIP, String reason); // Random jail

	public void jail(String playerOrIP, String reason, String jail);

	public void tempjail(String playerOrIP, String reason, long time); // Random jail

	public void tempjail(String playerOrIP, String reason, long time, String jail);

	public void setjail(Location location, String name);

	public void deljail(String name);

	public List<String> getjails();

	// Pardon
	public void unban(String playerOrIP);

	public void unbanIP(String playerOrIP);

	public void unjail(String playerOrIP);

	public void unmute(String playerOrIP);

	// Mute
	public void mute(String playerOrIP, String reason);

	public void tempmute(String playerOrIP, String reason, long time);

	// BanList
	public BanList getBanList(); // everyting

	public PlayerBanList getBanList(String player);

	// Utils
	public static boolean isIP(String text) {
		if(text==null)return false;
		text = text.replaceFirst("/", "");
		Pattern p = Pattern.compile(
				"^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
		Matcher m = p.matcher(text);
		return m.find();
	}
}
