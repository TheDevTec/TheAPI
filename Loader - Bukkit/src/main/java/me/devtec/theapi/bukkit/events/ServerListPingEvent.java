package me.devtec.theapi.bukkit.events;

import lombok.Getter;
import lombok.Setter;
import me.devtec.shared.events.Cancellable;
import me.devtec.shared.events.Event;
import me.devtec.shared.events.ListenerHolder;
import me.devtec.theapi.bukkit.nms.GameProfileHandler;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class ServerListPingEvent extends Event implements Cancellable {
    static List<ListenerHolder> handlers = new ArrayList<>();

    private boolean cancel;
    private int online;
    private int max;
    @Setter
    @Getter
    private int protocol;
    @Getter
    private List<GameProfileHandler> slots;
    @Setter
    @Getter
    private String motd;
    @Setter
    @Getter
    private String favicon;
    @Setter
    @Getter
    private String version;
    @Getter
    private final InetAddress address;

    public ServerListPingEvent(int online, int max, List<GameProfileHandler> slots, String motd, String favicon, InetAddress inetAddress, String ver, int protocol) {
        this.online = online;
        this.max = max;
        this.slots = slots;
        this.motd = motd;
        this.favicon = favicon;
        this.protocol = protocol;
        address = inetAddress;
        version = ver;
    }

    public int getOnlinePlayers() {
        return online;
    }

    public int getMaxPlayers() {
        return max;
    }

    public void setOnlinePlayers(int online) {
        this.online = online;
    }

    public void setMaxPlayers(int max) {
        this.max = max;
    }

    public void setPlayersText(List<GameProfileHandler> slots) {
        this.slots = slots;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public List<ListenerHolder> getHandlers() {
        return handlers;
    }

    public static List<ListenerHolder> getHandlerList() {
        return handlers;
    }
}
