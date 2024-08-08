package me.devtec.theapi.bukkit.packetlistener;

import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public interface PacketHandler<C> {

    Future<C> getFuture(Player player);

    default C get(Player player) {
        try {
            return getFuture(player).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    void add(Player player);

    void remove(C channel);

    boolean has(C channel);

    void close();

    default void send(Player player, Object packet) {
        this.send(this.get(player), packet);
    }

    void send(C channel, Object packet);

    default void send(Collection<? extends Player> onlinePlayers, Object packet) {
        onlinePlayers.forEach(player -> this.send(player, packet));
    }
}