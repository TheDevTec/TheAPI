package me.devtec.theapi.bukkit.packetlistener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PacketManager {
    private static final Priority[] PRIORITIES = {Priority.LOWEST, Priority.LOW, Priority.NORMAL, Priority.HIGH, Priority.HIGHEST, Priority.MONITOR};

    private static final Map<Priority, List<PacketListener>> listeners = new HashMap<>();

    static {
        for (Priority priority : PRIORITIES)
            PacketManager.listeners.put(priority, new ArrayList<>());
    }

    public static Object call(String player, final Object packet, final Object channel, PacketType type) {
        if (packet == null || channel == null)
            return packet;

        PacketContainer pContainer = new PacketContainer(packet);
        ChannelContainer cContainer = new ChannelContainer(channel);

        if (type == PacketType.PLAY_OUT)
            for (Priority o : PRIORITIES)
                for (PacketListener w : PacketManager.listeners.get(o))
                    w.playOut(player, pContainer, cContainer);
        else
            for (Priority o : PRIORITIES)
                for (PacketListener w : PacketManager.listeners.get(o))
                    w.playIn(player, pContainer, cContainer);
        return pContainer.isCancelled() ? null : pContainer.getPacket();
    }

    public static void register(PacketListener listener) {
        PacketManager.notify(listener, null, listener.getPriority());
    }

    public static void unregister(PacketListener listener) {
        PacketManager.listeners.get(listener.getPriority()).remove(listener);
    }

    public static void setPriority(PacketListener listener, Priority priority) {
        PacketManager.notify(listener, listener.getPriority(), priority);
        listener.priority = priority;
    }

    public static Priority getPriority(PacketListener listener) {
        return listener.getPriority();
    }

    public static boolean isRegistered(PacketListener listener) {
        for (Priority priority : PRIORITIES)
            if (PacketManager.listeners.get(priority).contains(listener))
                return true;
        return false;
    }

    protected static void notify(PacketListener listener, Priority oldPriority, Priority newPriority) {
        if (listener == null || newPriority == null)
            return;
        if (oldPriority != null)
            PacketManager.listeners.get(oldPriority).remove(listener);
        PacketManager.listeners.get(newPriority).add(listener);
    }

    public static void unregisterAll() {
        for (List<PacketListener> l : PacketManager.listeners.values())
            l.clear();
    }
}
