package me.DevTec.NMS.PacketListeners;

import org.bukkit.entity.Player;

public interface PacketHandler<C> {
	public void sendPacket(Player player, Object packet);
	
	public void sendPacket(C channel, Object packet);
	
	public void receivePacket(Player player, Object packet);

	public void receivePacket(C channel, Object packet);

	public void injectPlayer(Player player);

	public C getChannel(Player player);

	public void uninjectPlayer(Player player);

	public void uninjectChannel(C channel);

	public boolean hasInjected(Player player);

	public boolean hasInjected(C channel);

	public void close();
}