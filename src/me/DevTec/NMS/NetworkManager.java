package me.DevTec.NMS;

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
		return (boolean) Reflections.get(Reflections.getField(Reflections.getNMSClass("NetworkManager"), "preparing"),a);
	}

	public Property[] getProfile() {
		return (Property[]) Reflections.get(Reflections.getField(Reflections.getNMSClass("NetworkManager"), "spoofedProfile"),a);
	}

	public UUID getUUID() {
		return (UUID) Reflections.get(Reflections.getField(Reflections.getNMSClass("NetworkManager"), "spoofedUUID"),a);
	}

	public Channel getChannel() {
		return (Channel) Reflections.get(Reflections.getField(Reflections.getNMSClass("NetworkManager"), "channel"),a);
	}

	// YOUR RISK
	public void setChannel(Channel channel) {
		Reflections.setField(a, Reflections.getField(Reflections.getNMSClass("NetworkManager"), "channel"), channel);
	}

	public SocketAddress getSocketAddress() {
		return (SocketAddress) Reflections.invoke(a,Reflections.getMethod(Reflections.getNMSClass("NetworkManager"), "getSocketAddress"));
	}
}
