package me.DevTec.TheAPI.Utils.PacketListenerAPI;

import org.bukkit.entity.Player;

public interface PacketHandler<C> {

	public C get(Player player);

	public void add(Player player);

	public void remove(C channel);

	public boolean has(C channel);

	public void close();
	
	public default void send(Player player, Object packet) {
		send(get(player), packet);
	}
	
	public void send(C channel, Object packet);
}