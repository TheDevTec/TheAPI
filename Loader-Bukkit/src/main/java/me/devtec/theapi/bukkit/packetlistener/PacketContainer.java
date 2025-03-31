package me.devtec.theapi.bukkit.packetlistener;

import lombok.Getter;
import lombok.Setter;

@Getter
public class PacketContainer {
    private Object packet;
    @Setter
    private boolean cancelled;

    public PacketContainer(Object packet) {
        this.packet = packet;
    }

    public void setPacket(Object newPacket) {
        assert packet != null : "Packet cannot be null";
        packet = newPacket;
    }

}
