package me.devtec.theapi.bukkit.packetlistener;

public class PacketContainer {
    private Object packet;
    private boolean cancelled;

    public PacketContainer(Object packet) {
        this.packet = packet;
    }

    public Object getPacket() {
        return packet;
    }

    public void setPacket(Object newPacket) {
        assert packet != null : "Packet cannot be null";
        packet = newPacket;
    }

    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    public boolean isCancelled() {
        return cancelled;
    }
}
