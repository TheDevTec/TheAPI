package me.Straiker123;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BukkitBungeeAPI {
	private BungeeCordAPI b;
	public BukkitBungeeAPI(BungeeCordAPI bungeeCordAPI) {
		b=bungeeCordAPI;
	}
	public Collection<Player> getOnlinePlayers(){
		Collection<Player> c = new ArrayList<Player>();
		for(ProxiedPlayer p : b.getOnlinePlayers()) {
			c.add((Player)p);
		}
		return c;
	}
	public Player getPlayer(String player){
		return (Player)b.getPlayer(player);
	}
	public Player getPlayer(UUID player){
		return (Player)b.getPlayer(player);
	}
	public List<String> getServers(){
		List<String> sr = new ArrayList<String>();
		for(String s :b.getServers().keySet()) {
			sr.add(s);
		}
		return sr;
	}
	public String getGameVersion(){
		return b.getGameVersion();
	}
	public CommandSender getConsole(){
		return (CommandSender)b.getConsole();
	}
	public void broadcastMessage(String message){
		 b.broadcastMessage(message);
	}
	public void broadcast(String message, String permission){
		b.broadcast(message, permission);
	}
	public String getServerMOTD(String server){
		return b.getServerInfo(server).getMotd();
	}
	public String getServerName(String server){
		return b.getServerInfo(server).getName();
	}
	public Collection<Player> getOnlinePlayers(String server){
		Collection<Player> c = new ArrayList<Player>();
		for(ProxiedPlayer p : b.getServerInfo(server).getPlayers()) {
			c.add((Player)p);
		}
		return c;
	}
}
