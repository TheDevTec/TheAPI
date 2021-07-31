package me.devtec.theapi.utils.packetlistenerapi;

public abstract class PacketListener {
	public PacketListener() {
		this(Priority.NORMAL);
	}

	public PacketListener(Priority prio) {
		priority = prio;
	}

	protected Priority priority;

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

	public abstract boolean PacketPlayOut(String player, Object packet, Object channel);

	public abstract boolean PacketPlayIn(String player, Object packet, Object channel);
}
