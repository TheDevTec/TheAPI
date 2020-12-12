package me.DevTec.TheAPI.Utils.PacketListenerAPI;

public abstract class PacketListener {
	public PacketListener() {
		this(Priority.NORMAL);
	}

	public PacketListener(Priority prio) {
		priority = prio;
	}

	private Priority priority;

	public final PacketListener setPriority(Priority prio) {
		if (prio == null)
			return this;
		PacketManager.notify(this, priority, prio);
		priority = prio;
		return this;
	}

	public final void register() {
		PacketManager.register(this);
	}

	public final void unregister() {
		PacketManager.unregister(this);
	}

	public final Priority getPriority() {
		return priority;
	}

	protected final Object call(String player, Object packet, Object channel, boolean isPacketOut) {
		if (packet == null || channel == null
				|| (isPacketOut ? PacketPlayOut(player, packet, channel) : PacketPlayIn(player, packet, channel)))
			return null;
		return packet;
	}

	public abstract boolean PacketPlayOut(String player, Object packet, Object channel);

	public abstract boolean PacketPlayIn(String player, Object packet, Object channel);
}
