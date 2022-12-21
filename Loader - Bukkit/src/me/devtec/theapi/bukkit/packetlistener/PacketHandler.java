package me.devtec.theapi.bukkit.packetlistener;

import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.bukkit.entity.Player;

public interface PacketHandler<C> {

	public Future<C> getFuture(Player player);

	public default C get(Player player) {
		try {
			return getFuture(player).get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void add(Player player);

	public void remove(C channel);

	public boolean has(C channel);

	public void close();

	public default void send(Player player, Object packet) {
		this.send(this.get(player), packet);
	}

	public void send(C channel, Object packet);

	public default void send(Collection<? extends Player> onlinePlayers, Object packet) {
		onlinePlayers.forEach(player -> this.send(player, packet));
	}
}