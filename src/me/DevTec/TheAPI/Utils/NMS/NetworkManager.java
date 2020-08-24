package me.DevTec.TheAPI.Utils.NMS;

import java.net.SocketAddress;
import java.util.UUID;

import me.DevTec.TheAPI.TheAPI;
import me.DevTec.TheAPI.Utils.Reflections.Reflections;

public class NetworkManager {
	private Object a;

	public NetworkManager(Object object) {
		a = object;
	}

	public boolean isPreparing() {
		return (boolean) Reflections.get(Reflections.getField(Reflections.getNMSClass("NetworkManager"), "preparing"),a);
	}

	public Object getProfile() {
		return Reflections.get(Reflections.getField(Reflections.getNMSClass("NetworkManager"), "spoofedProfile"),a);
	}

	public UUID getUUID() {
		return (UUID) Reflections.get(Reflections.getField(Reflections.getNMSClass("NetworkManager"), "spoofedUUID"),a);
	}

	public Object getChannel() {
		return Reflections.get(Reflections.getField(Reflections.getNMSClass("NetworkManager"), TheAPI.isOlderThan(8)?"m":"channel"),a);
	}

	// YOUR RISK
	public void setChannel(Object channel) {
		Reflections.setField(a, Reflections.getField(Reflections.getNMSClass("NetworkManager"), TheAPI.isOlderThan(8)?"m":"channel"), channel);
	}

	public SocketAddress getSocketAddress() {
		return (SocketAddress) Reflections.invoke(a,Reflections.getMethod(Reflections.getNMSClass("NetworkManager"), "getSocketAddress"));
	}
}
