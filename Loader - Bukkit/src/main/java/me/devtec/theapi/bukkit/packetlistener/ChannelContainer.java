package me.devtec.theapi.bukkit.packetlistener;

public class ChannelContainer {
    private final Object channel;

    public ChannelContainer(Object channel) {
        this.channel = channel;
    }

    public Object getChannel() {
        return channel;
    }
}
