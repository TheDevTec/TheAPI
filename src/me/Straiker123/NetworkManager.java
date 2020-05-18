package me.Straiker123;

import java.net.SocketAddress;
import java.util.UUID;

import com.mojang.authlib.properties.Property;

import io.netty.channel.Channel;

public class NetworkManager {
	private Object a;

	public NetworkManager(Object object) {
		a = object;
	}

	public boolean isPreparing() {
		return (boolean) Reflections.get(a, "preparing");
	}

	public Property[] getProfile() {
		return (Property[]) Reflections.get(a, "spoofedProfile");
	}

	public UUID getUUID() {
		return (UUID) Reflections.get(a, "spoofedUUID");
	}

	public Channel getChannel() {
		return (Channel) Reflections.get(a, "channel");
	}

	// YOUR RISK
	public void setChannel(Channel channel) {
		Reflections.setField(a, "channel", channel);
	}

	public SocketAddress getSocketAddress() {
		return (SocketAddress) Reflections.get(a, "socketAddress");
	}

	// YOUR RISK
	public void setSocketAddress(SocketAddress channel) {
		Reflections.setField(a, "socketAddress", channel);
	}

}
