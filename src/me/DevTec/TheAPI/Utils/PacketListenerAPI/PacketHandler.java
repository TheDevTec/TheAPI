package me.DevTec.TheAPI.Utils.PacketListenerAPI;

import org.bukkit.entity.Player;

public interface PacketHandler<C> {

	public void injectPlayer(Player player);

	public C getChannel(Player player);

	public void uninjectPlayer(Player player);

	public void uninjectChannel(C channel);

	public boolean hasInjected(Player player);

	public boolean hasInjected(C channel);

	public void close();
}