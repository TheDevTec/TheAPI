package me.Straiker123;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeCordAPI {
	BungeeCord b= BungeeCord.getInstance();
	//With bukkit methods, missing "Map<String, ServerInfo> getServers()"
	public BukkitBungeeAPI getBukkitBungeeAPI() {
		return new BukkitBungeeAPI(this);
	}
	
	public Collection<ProxiedPlayer> getOnlinePlayers(){
		return b.getPlayers();
	}
	public ProxiedPlayer getPlayer(String player){
		return b.getPlayer(player);
	}
	public ProxiedPlayer getPlayer(UUID player){
		return b.getPlayer(player);
	}
	public Map<String, ServerInfo> getServers(){
		return b.getServers();
	}
	public String getGameVersion(){
		return b.getGameVersion();
	}
	public CommandSender getConsole(){
		return b.getConsole();
	}
	public void broadcastMessage(String message){
		 b.broadcast(TheAPI.colorize(message));
	}
	@SuppressWarnings("deprecation")
	public void broadcast(String message, String permission){
		for(ProxiedPlayer p : getOnlinePlayers()) {
			if(p.hasPermission(permission))p.sendMessage(TheAPI.colorize(message));
		}
	}
	public ServerInfo getServerInfo(String server){
		return b.getServerInfo(server);
	}
}
