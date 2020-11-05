package me.DevTec.TheAPI.Utils.NMS;

import java.net.SocketAddress;
import java.util.UUID;

import me.DevTec.TheAPI.TheAPI;
import me.DevTec.TheAPI.Utils.Reflections.Ref;

public class NetworkManager {
	private Object a;

	public NetworkManager(Object object) {
		a = object;
	}

	public boolean isPreparing() {
		return (boolean) Ref.get(a, Ref.field(Ref.nms("NetworkManager"), "preparing"));
	}

	public Object getProfile() {
		return Ref.get(a, Ref.field(Ref.nms("NetworkManager"), "spoofedProfile"));
	}

	public UUID getUUID() {
		return (UUID) Ref.get(a, Ref.field(Ref.nms("NetworkManager"), "spoofedUUID"));
	}

	public Object getChannel() {
		return Ref.get(a, Ref.field(Ref.nms("NetworkManager"), TheAPI.isOlderThan(8) ? "m" : "channel"));
	}

	// YOUR RISK
	public void setChannel(Object channel) {
		Ref.set(a, Ref.field(Ref.nms("NetworkManager"), TheAPI.isOlderThan(8) ? "m" : "channel"), channel);
	}

	public SocketAddress getSocketAddress() {
		return (SocketAddress) Ref.invoke(a, Ref.method(Ref.nms("NetworkManager"), "getSocketAddress"));
	}
}
