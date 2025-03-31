package me.devtec.theapi.bukkit.packetlistener;

import lombok.Getter;

@Getter
public class ChannelContainer {
    private final Object channel;

    public ChannelContainer(Object channel) {
        this.channel = channel;
    }

}
