package me.devtec.theapi.bukkit.packetlistener;

public abstract class PacketListener {
	protected Priority priority;
	
	public PacketListener() {
		this(Priority.NORMAL);
	}

	public PacketListener(Priority priority) {
		this.priority = priority;
	}

	public final PacketListener setPriority(Priority priority) {
		if (priority == null)
			return this;
		PacketManager.notify(this, priority, priority);
		this.priority = priority;
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
